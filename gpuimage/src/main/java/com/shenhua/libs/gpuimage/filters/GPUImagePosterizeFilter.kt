package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Reduces the color range of the image. <br></br>
 * <br></br>
 * colorLevels: ranges from 1 to 256, with a default of 10
 *
 * @author shenhua
 */
class GPUImagePosterizeFilter @JvmOverloads constructor(private var mColorLevels: Int = 10) : GPUImageFilter(GPUImageFilter.NO_FILTER_VERTEX_SHADER, POSTERIZE_FRAGMENT_SHADER) {

    private var mGLUniformColorLevels: Int = 0

    override fun onInit() {
        super.onInit()
        mGLUniformColorLevels = GLES20.glGetUniformLocation(program, "colorLevels")
        setColorLevels(mColorLevels)
    }

    fun setColorLevels(colorLevels: Int) {
        mColorLevels = colorLevels
        setFloat(mGLUniformColorLevels, colorLevels.toFloat())
    }

    companion object {
        val POSTERIZE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform highp float colorLevels;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "   highp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   \n" +
                "   gl_FragColor = floor((textureColor * colorLevels) + vec4(0.5)) / colorLevels;\n" +
                "}"
    }
}
