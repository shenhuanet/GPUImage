package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Changes the contrast of the image.<br></br>
 * <br></br>
 * contrast value ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
class GPUImageContrastFilter @JvmOverloads constructor(private var mContrast: Float = 1.2f) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, CONTRAST_FRAGMENT_SHADER) {

    private var mContrastLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mContrastLocation = GLES20.glGetUniformLocation(program, "contrast")
    }

    override fun onInitialized() {
        super.onInitialized()
        setContrast(mContrast)
    }

    fun setContrast(contrast: Float) {
        mContrast = contrast
        setFloat(mContrastLocation, mContrast)
    }

    companion object {
        val CONTRAST_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform lowp float contrast;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), textureColor.w);\n" +
                " }"
    }
}
