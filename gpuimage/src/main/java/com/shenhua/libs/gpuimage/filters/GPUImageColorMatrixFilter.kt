package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Applies a ColorMatrix to the image.
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
open class GPUImageColorMatrixFilter @JvmOverloads constructor(private var mIntensity: Float = 1.0f, private var mColorMatrix: FloatArray? = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f)) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, COLOR_MATRIX_FRAGMENT_SHADER) {
    private var mColorMatrixLocation: Int = 0
    private var mIntensityLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mColorMatrixLocation = GLES20.glGetUniformLocation(program, "colorMatrix")
        mIntensityLocation = GLES20.glGetUniformLocation(program, "intensity")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(mIntensity)
        setColorMatrix(mColorMatrix)
    }

    fun setIntensity(intensity: Float) {
        mIntensity = intensity
        setFloat(mIntensityLocation, intensity)
    }

    fun setColorMatrix(colorMatrix: FloatArray?) {
        mColorMatrix = colorMatrix
        setUniformMatrix4f(mColorMatrixLocation, colorMatrix!!)
    }

    companion object {
        val COLOR_MATRIX_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform lowp mat4 colorMatrix;\n" +
                "uniform lowp float intensity;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp vec4 outputColor = textureColor * colorMatrix;\n" +
                "    \n" +
                "    gl_FragColor = (intensity * outputColor) + ((1.0 - intensity) * textureColor);\n" +
                "}"
    }
}
