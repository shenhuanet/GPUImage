package com.shenhua.libs.gpuimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.shenhua.libs.gpuimage.filters.GPUImageFilter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.IntBuffer
import java.util.concurrent.Semaphore

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageView : FrameLayout {

    private var mGLSurfaceView: GLSurfaceView? = null
    /**
     * Retrieve the GPUImage instance used by this view.
     *
     * @return used GPUImage instance
     */
    var gpuImage: GPUImage? = null
        private set
    /**
     * Get the current applied filter.
     *
     * @return the current filter
     */
    /**
     * Set the filter to be applied on the image.
     *
     * @param filter Filter that should be applied on the image.
     */
    var filter: GPUImageFilter? = null
        set(filter) {
            field = filter
            gpuImage!!.setFilter(filter)
            requestRender()
        }
    var mForceSize: Size? = null
    private var mRatio = 0.0f

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mGLSurfaceView = GPUImageGLSurfaceView(context, attrs!!)
        addView(mGLSurfaceView)
        gpuImage = GPUImage(getContext())
        gpuImage!!.setGLSurfaceView(mGLSurfaceView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mRatio != 0.0f) {
            val width = View.MeasureSpec.getSize(widthMeasureSpec)
            val height = View.MeasureSpec.getSize(heightMeasureSpec)

            val newHeight: Int
            val newWidth: Int
            if (width / mRatio < height) {
                newWidth = width
                newHeight = Math.round(width / mRatio)
            } else {
                newHeight = height
                newWidth = Math.round(height * mRatio)
            }

            val newWidthSpec = View.MeasureSpec.makeMeasureSpec(newWidth, View.MeasureSpec.EXACTLY)
            val newHeightSpec = View.MeasureSpec.makeMeasureSpec(newHeight, View.MeasureSpec.EXACTLY)
            super.onMeasure(newWidthSpec, newHeightSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
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
        gpuImage!!.setBackgroundColor(red, green, blue)
    }

    // TODO Should be an xml attribute. But then GPUImage can not be distributed as .jar anymore.
    fun setRatio(ratio: Float) {
        mRatio = ratio
        mGLSurfaceView!!.requestLayout()
        gpuImage!!.deleteImage()
    }

    /**
     * Set the scale type of GPUImage.
     *
     * @param scaleType the new ScaleType
     */
    fun setScaleType(scaleType: GPUImage.ScaleType) {
        gpuImage!!.setScaleType(scaleType)
    }

    /**
     * Sets the rotation of the displayed image.
     *
     * @param rotation new rotation
     */
    fun setRotation(rotation: Rotation) {
        gpuImage!!.setRotation(rotation)
        requestRender()
    }

    /**
     * Sets the image on which the filter should be applied.
     *
     * @param bitmap the new image
     */
    fun setImage(bitmap: Bitmap) {
        gpuImage!!.setImage(bitmap)
    }

    /**
     * Sets the image on which the filter should be applied from a Uri.
     *
     * @param uri the uri of the new image
     */
    fun setImage(uri: Uri) {
        gpuImage!!.setImage(uri)
    }

    /**
     * Sets the image on which the filter should be applied from a File.
     *
     * @param file the file of the new image
     */
    fun setImage(file: File) {
        gpuImage!!.setImage(file)
    }

    fun requestRender() {
        mGLSurfaceView!!.requestRender()
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br></br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName   the file name
     * @param listener   the listener
     */
    fun saveToPictures(folderName: String, fileName: String,
                       listener: OnPictureSavedListener) {
        SaveTask(folderName, fileName, listener).execute()
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br></br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName   the file name
     * @param width      requested output width
     * @param height     requested output height
     * @param listener   the listener
     */
    fun saveToPictures(folderName: String, fileName: String,
                       width: Int, height: Int,
                       listener: OnPictureSavedListener) {
        SaveTask(folderName, fileName, width, height, listener).execute()
    }

    /**
     * Retrieve current image with filter applied and given size as Bitmap.
     *
     * @param width  requested Bitmap width
     * @param height requested Bitmap height
     * @return Bitmap of picture with given size
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    fun capture(width: Int, height: Int): Bitmap {
        // This method needs to run on a background thread because it will take a longer time
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("Do not call this method from the UI thread!")
        }

        mForceSize = Size(width, height)

        val waiter = Semaphore(0)

        // Layout with new size
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                waiter.release()
            }
        })
        post {
            // Show loading
            addView(LoadingView(context))

            mGLSurfaceView!!.requestLayout()
        }
        waiter.acquire()

        // Run one render pass
        gpuImage!!.runOnGLThread { waiter.release() }
        requestRender()
        waiter.acquire()
        val bitmap = capture()


        mForceSize = null
        post { mGLSurfaceView!!.requestLayout() }
        requestRender()

        postDelayed({
            // Remove loading view
            removeViewAt(1)
        }, 300)

        return bitmap
    }

    /**
     * Capture the current image with the size as it is displayed and retrieve it as Bitmap.
     *
     * @return current output as Bitmap
     * @throws InterruptedException
     */
    @Throws(InterruptedException::class)
    fun capture(): Bitmap {
        val waiter = Semaphore(0)

        val width = mGLSurfaceView!!.measuredWidth
        val height = mGLSurfaceView!!.measuredHeight

        // Take picture on OpenGL thread
        val pixelMirroredArray = IntArray(width * height)
        gpuImage!!.runOnGLThread {
            val pixelBuffer = IntBuffer.allocate(width * height)
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer)
            val pixelArray = pixelBuffer.array()

            // Convert upside down mirror-reversed image to right-side up normal image.
            for (i in 0 until height) {
                for (j in 0 until width) {
                    pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j]
                }
            }
            waiter.release()
        }
        requestRender()
        waiter.acquire()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray))
        return bitmap
    }

    /**
     * Pauses the GLSurfaceView.
     */
    fun onPause() {
        mGLSurfaceView!!.onPause()
    }

    /**
     * Resumes the GLSurfaceView.
     */
    fun onResume() {
        mGLSurfaceView!!.onResume()
    }

    class Size(internal var width: Int, internal var height: Int)

    private inner class GPUImageGLSurfaceView : GLSurfaceView {
        constructor(context: Context) : super(context) {}

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            if (mForceSize != null) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(mForceSize!!.width, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(mForceSize!!.height, View.MeasureSpec.EXACTLY))
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    private inner class LoadingView : FrameLayout {
        constructor(context: Context) : super(context) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            init()
        }

        constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
            init()
        }

        private fun init() {
            val view = ProgressBar(context)
            view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
            addView(view)
            setBackgroundColor(Color.BLACK)
        }
    }

    private inner class SaveTask(private val mFolderName: String, private val mFileName: String, private val mWidth: Int, private val mHeight: Int,
                                 private val mListener: OnPictureSavedListener?) : AsyncTask<Void, Void, Void>() {
        private val mHandler: Handler

        constructor(folderName: String, fileName: String,
                    listener: OnPictureSavedListener) : this(folderName, fileName, 0, 0, listener) {
        }

        init {
            mHandler = Handler()
        }

        override fun doInBackground(vararg params: Void): Void? {
            try {
                val result = if (mWidth != 0) capture(mWidth, mHeight) else capture()
                saveImage(mFolderName, mFileName, result)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return null
        }

        private fun saveImage(folderName: String, fileName: String, image: Bitmap) {
            val path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val file = File(path, folderName + "/" + fileName)
            try {
                file.parentFile.mkdirs()
                image.compress(Bitmap.CompressFormat.JPEG, 80, FileOutputStream(file))
                MediaScannerConnection.scanFile(context,
                        arrayOf(file.toString()), null
                ) { path, uri ->
                    if (mListener != null) {
                        mHandler.post { mListener.onPictureSaved(uri) }
                    }
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    interface OnPictureSavedListener {
        fun onPictureSaved(uri: Uri)
    }
}
