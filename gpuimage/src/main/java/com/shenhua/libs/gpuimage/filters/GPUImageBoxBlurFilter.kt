package com.shenhua.libs.gpuimage.filters


/**
 * A hardware-accelerated 9-hit box blur of an image
 *
 *
 * scaling: for the size of the applied blur, default of 1.0
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageBoxBlurFilter @JvmOverloads constructor(blurSize: Float = 1f) : GPUImageTwoPassTextureSamplingFilter(VERTEX_SHADER, FRAGMENT_SHADER, VERTEX_SHADER, FRAGMENT_SHADER) {

    override var verticalTexelOffsetRatio = 1f
        set(value: Float) {
            super.verticalTexelOffsetRatio = value
        }


    init {
        this.verticalTexelOffsetRatio = blurSize
    }

    /**
     * A scaling for the size of the applied blur, default of 1.0
     *
     * @param blurSize
     */
    fun setBlurSize(blurSize: Float) {
        this.verticalTexelOffsetRatio = blurSize
    }

    companion object {
        val VERTEX_SHADER = "attribute vec4 position;\n" +
                "attribute vec2 inputTextureCoordinate;\n" +
                "\n" +
                "uniform float texelWidthOffset; \n" +
                "uniform float texelHeightOffset; \n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepLeftTextureCoordinate;\n" +
                "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                "varying vec2 oneStepRightTextureCoordinate;\n" +
                "varying vec2 twoStepsRightTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "gl_Position = position;\n" +
                "\n" +
                "vec2 firstOffset = vec2(1.5 * texelWidthOffset, 1.5 * texelHeightOffset);\n" +
                "vec2 secondOffset = vec2(3.5 * texelWidthOffset, 3.5 * texelHeightOffset);\n" +
                "\n" +
                "centerTextureCoordinate = inputTextureCoordinate;\n" +
                "oneStepLeftTextureCoordinate = inputTextureCoordinate - firstOffset;\n" +
                "twoStepsLeftTextureCoordinate = inputTextureCoordinate - secondOffset;\n" +
                "oneStepRightTextureCoordinate = inputTextureCoordinate + firstOffset;\n" +
                "twoStepsRightTextureCoordinate = inputTextureCoordinate + secondOffset;\n" +
                "}\n"

        val FRAGMENT_SHADER = "precision highp float;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "varying vec2 centerTextureCoordinate;\n" +
                "varying vec2 oneStepLeftTextureCoordinate;\n" +
                "varying vec2 twoStepsLeftTextureCoordinate;\n" +
                "varying vec2 oneStepRightTextureCoordinate;\n" +
                "varying vec2 twoStepsRightTextureCoordinate;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 fragmentColor = texture2D(inputImageTexture, centerTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, oneStepLeftTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, oneStepRightTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, twoStepsLeftTextureCoordinate) * 0.2;\n" +
                "fragmentColor += texture2D(inputImageTexture, twoStepsRightTextureCoordinate) * 0.2;\n" +
                "\n" +
                "gl_FragColor = fragmentColor;\n" +
                "}\n"
    }
}
/**
 * Construct new BoxBlurFilter with default blur size of 1.0.
 */
