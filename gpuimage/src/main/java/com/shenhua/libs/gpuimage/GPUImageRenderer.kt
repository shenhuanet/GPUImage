package com.shenhua.libs.gpuimage

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import com.shenhua.libs.gpuimage.filters.GPUImageFilter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@TargetApi(11)
class GPUImageRenderer(private var mFilter: GPUImageFilter?) : Renderer, PreviewCallback {

    val mSurfaceChangedWaiter = Any()

    private var mGLTextureId = NO_IMAGE
    private var mSurfaceTexture: SurfaceTexture? = null
    private val mGLCubeBuffer: FloatBuffer
    private val mGLTextureBuffer: FloatBuffer
    private var mGLRgbBuffer: IntBuffer? = null

    var frameWidth: Int = 0
        private set
    var frameHeight: Int = 0
        private set
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mAddedPadding: Int = 0

    private val mRunOnDraw: Queue<Runnable>
    private val mRunOnDrawEnd: Queue<Runnable>
    var rotation: Rotation? = null
        set(rotation) {
            field = rotation
            adjustImageScaling()
        }
    var isFlippedHorizontally: Boolean = false
        private set
    var isFlippedVertically: Boolean = false
        private set
    private var mScaleType: GPUImage.ScaleType = GPUImage.ScaleType.CENTER_CROP

    private var mBackgroundRed = 0f
    private var mBackgroundGreen = 0f
    private var mBackgroundBlue = 0f

    init {
        mRunOnDraw = LinkedList()
        mRunOnDrawEnd = LinkedList()

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        mGLCubeBuffer.put(CUBE).position(0)

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        setRotation(Rotation.NORMAL, false, false)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        GLES20.glClearColor(mBackgroundRed, mBackgroundGreen, mBackgroundBlue, 1f)
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        mFilter!!.init()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        frameWidth = width
        frameHeight = height
        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(mFilter!!.program)
        mFilter!!.onOutputSizeChanged(width, height)
        adjustImageScaling()
//        synchronized(mSurfaceChangedWaiter) {
//            mSurfaceChangedWaiter.notifyAll()
//        }
    }

    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        runAll(mRunOnDraw)
        mFilter!!.onDraw(mGLTextureId, mGLCubeBuffer, mGLTextureBuffer)
        runAll(mRunOnDrawEnd)
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.updateTexImage()
        }
    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    fun setBackgroundColor(red: Float, green: Float, blue: Float) {
        mBackgroundRed = red
        mBackgroundGreen = green
        mBackgroundBlue = blue
    }

    private fun runAll(queue: Queue<Runnable>) {
        synchronized(queue) {
            while (!queue.isEmpty()) {
                queue.poll().run()
            }
        }
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val previewSize = camera.parameters.previewSize
        if (mGLRgbBuffer == null) {
            mGLRgbBuffer = IntBuffer.allocate(previewSize.width * previewSize.height)
        }
        if (mRunOnDraw.isEmpty()) {
            runOnDraw(Runnable {
                GPUImageNativeLibrary.YUVtoRBGA(data, previewSize.width, previewSize.height,
                        mGLRgbBuffer!!.array())
                mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer!!, previewSize, mGLTextureId)
                camera.addCallbackBuffer(data)

                if (mImageWidth != previewSize.width) {
                    mImageWidth = previewSize.width
                    mImageHeight = previewSize.height
                    adjustImageScaling()
                }
            })
        }
    }

    fun setUpSurfaceTexture(camera: Camera) {
        runOnDraw(Runnable {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            mSurfaceTexture = SurfaceTexture(textures[0])
            try {
                camera.setPreviewTexture(mSurfaceTexture)
                camera.setPreviewCallback(this@GPUImageRenderer)
                camera.startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        })
    }

    fun setFilter(filter: GPUImageFilter) {
        runOnDraw(Runnable {
            val oldFilter = mFilter
            mFilter = filter
            oldFilter?.destroy()
            mFilter!!.init()
            GLES20.glUseProgram(mFilter!!.program)
            mFilter!!.onOutputSizeChanged(frameWidth, frameHeight)
        })
    }

    fun deleteImage() {
        runOnDraw(Runnable {
            GLES20.glDeleteTextures(1, intArrayOf(mGLTextureId), 0)
            mGLTextureId = NO_IMAGE
        })
    }

    @JvmOverloads
    fun setImageBitmap(bitmap: Bitmap?, recycle: Boolean = true) {
        if (bitmap == null) {
            return
        }

        runOnDraw(Runnable {
            var resizedBitmap: Bitmap? = null
            if (bitmap.width % 2 == 1) {
                resizedBitmap = Bitmap.createBitmap(bitmap.width + 1, bitmap.height,
                        Bitmap.Config.ARGB_8888)
                val can = Canvas(resizedBitmap!!)
                can.drawARGB(0x00, 0x00, 0x00, 0x00)
                can.drawBitmap(bitmap, 0f, 0f, null)
                mAddedPadding = 1
            } else {
                mAddedPadding = 0
            }

            mGLTextureId = OpenGlUtils.loadTexture(
                    if (resizedBitmap != null) resizedBitmap else bitmap, mGLTextureId, recycle)
            if (resizedBitmap != null) {
                resizedBitmap.recycle()
            }
            mImageWidth = bitmap.width
            mImageHeight = bitmap.height
            adjustImageScaling()
        })
    }

    fun setScaleType(scaleType: GPUImage.ScaleType) {
        mScaleType = scaleType
    }

    private fun adjustImageScaling() {
        var outputWidth = frameWidth.toFloat()
        var outputHeight = frameHeight.toFloat()
        if (rotation === Rotation.ROTATION_270 || rotation === Rotation.ROTATION_90) {
            outputWidth = frameHeight.toFloat()
            outputHeight = frameWidth.toFloat()
        }

        val ratio1 = outputWidth / mImageWidth
        val ratio2 = outputHeight / mImageHeight
        val ratioMax = Math.max(ratio1, ratio2)
        val imageWidthNew = Math.round(mImageWidth * ratioMax)
        val imageHeightNew = Math.round(mImageHeight * ratioMax)

        val ratioWidth = imageWidthNew / outputWidth
        val ratioHeight = imageHeightNew / outputHeight

        var cube = CUBE
        var textureCords = TextureRotationUtil.getRotation(rotation!!, isFlippedHorizontally, isFlippedVertically)
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            val distHorizontal = (1 - 1 / ratioWidth) / 2
            val distVertical = (1 - 1 / ratioHeight) / 2
            textureCords = floatArrayOf(addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical), addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical), addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical), addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical))
        } else {
            cube = floatArrayOf(CUBE[0] / ratioHeight, CUBE[1] / ratioWidth, CUBE[2] / ratioHeight, CUBE[3] / ratioWidth, CUBE[4] / ratioHeight, CUBE[5] / ratioWidth, CUBE[6] / ratioHeight, CUBE[7] / ratioWidth)
        }

        mGLCubeBuffer.clear()
        mGLCubeBuffer.put(cube).position(0)
        mGLTextureBuffer.clear()
        mGLTextureBuffer.put(textureCords).position(0)
    }

    private fun addDistance(coordinate: Float, distance: Float): Float {
        return if (coordinate == 0.0f) distance else 1 - distance
    }

    fun setRotationCamera(rotation: Rotation, flipHorizontal: Boolean,
                          flipVertical: Boolean) {
        setRotation(rotation, flipVertical, flipHorizontal)
    }

    fun setRotation(rotation: Rotation,
                    flipHorizontal: Boolean, flipVertical: Boolean) {
        isFlippedHorizontally = flipHorizontal
        isFlippedVertically = flipVertical
        this.rotation = rotation
    }

    fun runOnDraw(runnable: Runnable) {
        synchronized(mRunOnDraw) {
            mRunOnDraw.add(runnable)
        }
    }

    fun runOnDrawEnd(runnable: Runnable) {
        synchronized(mRunOnDrawEnd) {
            mRunOnDrawEnd.add(runnable)
        }
    }

    companion object {
        val NO_IMAGE = -1
        internal val CUBE = floatArrayOf(-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f)
    }
}
