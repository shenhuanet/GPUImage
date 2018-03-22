package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * exposure: The adjusted exposure (-10.0 - 10.0, with 0.0 as the default)
 */
class GPUImageExposureFilter @JvmOverloads constructor(private var mExposure: Float = 1.0f) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, EXPOSURE_FRAGMENT_SHADER) {

    private var mExposureLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mExposureLocation = GLES20.glGetUniformLocation(program, "exposure")
    }

    override fun onInitialized() {
        super.onInitialized()
        setExposure(mExposure)
    }

    fun setExposure(exposure: Float) {
        mExposure = exposure
        setFloat(mExposureLocation, mExposure)
    }

    companion object {
        val EXPOSURE_FRAGMENT_SHADER = "" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform highp float exposure;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     gl_FragColor = vec4(textureColor.rgb * pow(2.0, exposure), textureColor.w);\n" +
                " } "
    }
}
