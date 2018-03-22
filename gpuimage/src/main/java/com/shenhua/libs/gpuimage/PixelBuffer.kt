package com.shenhua.libs.gpuimage

import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import android.util.Log
import java.nio.IntBuffer
import javax.microedition.khronos.egl.*
import javax.microedition.khronos.egl.EGL10.*
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL10.GL_RGBA
import javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class PixelBuffer(internal var mWidth: Int, internal var mHeight: Int) {

    internal var mRenderer: GLSurfaceView.Renderer? = null // borrow this interface
    private lateinit var mBitmap: Bitmap

    internal var mEGL: EGL10
    internal var mEGLDisplay: EGLDisplay
    private lateinit var mEGLConfigs: Array<EGLConfig?>
    private var mEGLConfig: EGLConfig
    internal var mEGLContext: EGLContext
    internal var mEGLSurface: EGLSurface
    internal var mGL: GL10

    internal var mThreadOwner: String

    // Do we have a renderer?
    // Does this thread own the OpenGL context?
    // Call the renderer draw routine (it seems that some filters do not
    // work if this is only called once)
    val bitmap: Bitmap?
        get() {
            if (mRenderer == null) {
                Log.e(TAG, "getBitmap: Renderer was not set.")
                return null
            }
            if (Thread.currentThread().name != mThreadOwner) {
                Log.e(TAG, "getBitmap: This thread does not own the OpenGL context.")
                return null
            }
            mRenderer!!.onDrawFrame(mGL)
            mRenderer!!.onDrawFrame(mGL)
            convertToBitmap()
            return mBitmap
        }

    init {

        val version = IntArray(2)
        val attribList = intArrayOf(EGL_WIDTH, mWidth, EGL_HEIGHT, mHeight, EGL_NONE)

        // No error checking performed, minimum required code to elucidate logic
        mEGL = EGLContext.getEGL() as EGL10
        mEGLDisplay = mEGL.eglGetDisplay(EGL_DEFAULT_DISPLAY)
        mEGL.eglInitialize(mEGLDisplay, version)
        mEGLConfig = chooseConfig() // Choosing a config is a little more
        // complicated

        // mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig,
        // EGL_NO_CONTEXT, null);
        val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        val attrib_list = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        mEGLContext = mEGL.eglCreateContext(mEGLDisplay, mEGLConfig, EGL_NO_CONTEXT, attrib_list)

        mEGLSurface = mEGL.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, attribList)
        mEGL.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)

        mGL = mEGLContext.gl as GL10

        // Record thread owner of OpenGL context
        mThreadOwner = Thread.currentThread().name
    }

    fun setRenderer(renderer: GLSurfaceView.Renderer) {
        mRenderer = renderer

        // Does this thread own the OpenGL context?
        if (Thread.currentThread().name != mThreadOwner) {
            Log.e(TAG, "setRenderer: This thread does not own the OpenGL context.")
            return
        }

        // Call the renderer initialization routines
        mRenderer!!.onSurfaceCreated(mGL, mEGLConfig)
        mRenderer!!.onSurfaceChanged(mGL, mWidth, mHeight)
    }

    fun destroy() {
        mRenderer!!.onDrawFrame(mGL)
        mRenderer!!.onDrawFrame(mGL)
        mEGL.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)

        mEGL.eglDestroySurface(mEGLDisplay, mEGLSurface)
        mEGL.eglDestroyContext(mEGLDisplay, mEGLContext)
        mEGL.eglTerminate(mEGLDisplay)
    }

    private fun chooseConfig(): EGLConfig {
        val attribList = intArrayOf(EGL_DEPTH_SIZE, 0, EGL_STENCIL_SIZE, 0, EGL_RED_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_BLUE_SIZE, 8, EGL_ALPHA_SIZE, 8, EGL10.EGL_RENDERABLE_TYPE, 4, EGL_NONE)

        // No error checking performed, minimum required code to elucidate logic
        // Expand on this logic to be more selective in choosing a configuration
        val numConfig = IntArray(1)
        mEGL.eglChooseConfig(mEGLDisplay, attribList, null, 0, numConfig)
        val configSize = numConfig[0]
        mEGLConfigs = arrayOfNulls(configSize)
        mEGL.eglChooseConfig(mEGLDisplay, attribList, mEGLConfigs, configSize, numConfig)

        if (LIST_CONFIGS) {
            listConfig()
        }

        return mEGLConfigs[0]!! // Best match is probably the first configuration
    }

    private fun listConfig() {
        Log.i(TAG, "Config List {")

        for (config in mEGLConfigs) {
            val d: Int
            val s: Int
            val r: Int
            val g: Int
            val b: Int
            val a: Int

            // Expand on this logic to dump other attributes
            d = getConfigAttrib(config!!, EGL_DEPTH_SIZE)
            s = getConfigAttrib(config, EGL_STENCIL_SIZE)
            r = getConfigAttrib(config, EGL_RED_SIZE)
            g = getConfigAttrib(config, EGL_GREEN_SIZE)
            b = getConfigAttrib(config, EGL_BLUE_SIZE)
            a = getConfigAttrib(config, EGL_ALPHA_SIZE)
            Log.i(TAG, "    <d,s,r,g,b,a> = <" + d + "," + s + "," +
                    r + "," + g + "," + b + "," + a + ">")
        }

        Log.i(TAG, "}")
    }

    private fun getConfigAttrib(config: EGLConfig, attribute: Int): Int {
        val value = IntArray(1)
        return if (mEGL.eglGetConfigAttrib(mEGLDisplay, config,
                attribute, value))
            value[0]
        else
            0
    }

    private fun convertToBitmap() {
        val iat = IntArray(mWidth * mHeight)
        val ib = IntBuffer.allocate(mWidth * mHeight)
        mGL.glReadPixels(0, 0, mWidth, mHeight, GL_RGBA, GL_UNSIGNED_BYTE, ib)
        val ia = ib.array()

        //Stupid !
        // Convert upside down mirror-reversed image to right-side up normal
        // image.
        for (i in 0 until mHeight) {
            for (j in 0 until mWidth) {
                iat[(mHeight - i - 1) * mWidth + j] = ia[i * mWidth + j]
            }
        }


        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        mBitmap.copyPixelsFromBuffer(IntBuffer.wrap(iat))
    }

    companion object {
        internal val TAG = "PixelBuffer"
        internal val LIST_CONFIGS = false
    }
}
