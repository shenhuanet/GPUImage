package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * This uses Sobel edge detection to place a black border around objects,
 * and then it quantizes the colors present in the image to give a cartoon-like quality to the image.
 */
class GPUImageToonFilter @JvmOverloads constructor(internal var mThreshold: Float = 0.2f, internal var mQuantizationLevels: Float = 10.0f) : GPUImage3x3TextureSamplingFilter(TOON_FRAGMENT_SHADER) {
    internal var mThresholdLocation: Int = 0
    internal var mQuantizationLevelsLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mThresholdLocation = GLES20.glGetUniformLocation(program, "threshold")
        mQuantizationLevelsLocation = GLES20.glGetUniformLocation(program, "quantizationLevels")
    }

    override fun onInitialized() {
        super.onInitialized()
        setThreshold(mThreshold)
        setQuantizationLevels(mQuantizationLevels)
    }

    /**
     * The threshold at which to apply the edges, default of 0.2.
     *
     * @param threshold default 0.2
     */
    fun setThreshold(threshold: Float) {
        mThreshold = threshold
        setFloat(mThresholdLocation, threshold)
    }

    /**
     * The levels of quantization for the posterization of colors within the scene, with a default of 10.0.
     *
     * @param quantizationLevels default 10.0
     */
    fun setQuantizationLevels(quantizationLevels: Float) {
        mQuantizationLevels = quantizationLevels
        setFloat(mQuantizationLevelsLocation, quantizationLevels)
    }

    companion object {
        val TOON_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "varying vec2 leftTextureCoordinate;\n" +
                "varying vec2 rightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 topTextureCoordinate;\n" +
                "varying vec2 topLeftTextureCoordinate;\n" +
                "varying vec2 topRightTextureCoordinate;\n" +
                "\n" +
                "varying vec2 bottomTextureCoordinate;\n" +
                "varying vec2 bottomLeftTextureCoordinate;\n" +
                "varying vec2 bottomRightTextureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp float intensity;\n" +
                "uniform highp float threshold;\n" +
                "uniform highp float quantizationLevels;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "\n" +
                "float bottomLeftIntensity = texture2D(inputImageTexture, bottomLeftTextureCoordinate).r;\n" +
                "float topRightIntensity = texture2D(inputImageTexture, topRightTextureCoordinate).r;\n" +
                "float topLeftIntensity = texture2D(inputImageTexture, topLeftTextureCoordinate).r;\n" +
                "float bottomRightIntensity = texture2D(inputImageTexture, bottomRightTextureCoordinate).r;\n" +
                "float leftIntensity = texture2D(inputImageTexture, leftTextureCoordinate).r;\n" +
                "float rightIntensity = texture2D(inputImageTexture, rightTextureCoordinate).r;\n" +
                "float bottomIntensity = texture2D(inputImageTexture, bottomTextureCoordinate).r;\n" +
                "float topIntensity = texture2D(inputImageTexture, topTextureCoordinate).r;\n" +
                "float h = -topLeftIntensity - 2.0 * topIntensity - topRightIntensity + bottomLeftIntensity + 2.0 * bottomIntensity + bottomRightIntensity;\n" +
                "float v = -bottomLeftIntensity - 2.0 * leftIntensity - topLeftIntensity + bottomRightIntensity + 2.0 * rightIntensity + topRightIntensity;\n" +
                "\n" +
                "float mag = length(vec2(h, v));\n" +
                "\n" +
                "vec3 posterizedImageColor = floor((textureColor.rgb * quantizationLevels) + 0.5) / quantizationLevels;\n" +
                "\n" +
                "float thresholdTest = 1.0 - step(threshold, mag);\n" +
                "\n" +
                "gl_FragColor = vec4(posterizedImageColor * thresholdTest, textureColor.a);\n" +
                "}\n"
    }
}
