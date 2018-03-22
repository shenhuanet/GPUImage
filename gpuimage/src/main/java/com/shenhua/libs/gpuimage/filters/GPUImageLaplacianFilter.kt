package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageLaplacianFilter private constructor(private var mConvolutionKernel: FloatArray?) : GPUImage3x3TextureSamplingFilter(LAPLACIAN_FRAGMENT_SHADER) {
    private var mUniformConvolutionMatrix: Int = 0

    constructor() : this(floatArrayOf(0.5f, 1.0f, 0.5f, 1.0f, -6.0f, 1.0f, 0.5f, 1.0f, 0.5f)) {}

    override fun onInit() {
        super.onInit()
        mUniformConvolutionMatrix = GLES20.glGetUniformLocation(program, "convolutionMatrix")
        setConvolutionKernel(mConvolutionKernel)
    }

    private fun setConvolutionKernel(convolutionKernel: FloatArray?) {
        mConvolutionKernel = convolutionKernel
        setUniformMatrix3f(mUniformConvolutionMatrix, mConvolutionKernel!!)
    }

    companion object {
        val LAPLACIAN_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform mediump mat3 convolutionMatrix;\n" +
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
                "void main()\n" +
                "{\n" +
                "mediump vec3 bottomColor = texture2D(inputImageTexture, bottomTextureCoordinate).rgb;\n" +
                "mediump vec3 bottomLeftColor = texture2D(inputImageTexture, bottomLeftTextureCoordinate).rgb;\n" +
                "mediump vec3 bottomRightColor = texture2D(inputImageTexture, bottomRightTextureCoordinate).rgb;\n" +
                "mediump vec4 centerColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "mediump vec3 leftColor = texture2D(inputImageTexture, leftTextureCoordinate).rgb;\n" +
                "mediump vec3 rightColor = texture2D(inputImageTexture, rightTextureCoordinate).rgb;\n" +
                "mediump vec3 topColor = texture2D(inputImageTexture, topTextureCoordinate).rgb;\n" +
                "mediump vec3 topRightColor = texture2D(inputImageTexture, topRightTextureCoordinate).rgb;\n" +
                "mediump vec3 topLeftColor = texture2D(inputImageTexture, topLeftTextureCoordinate).rgb;\n" +
                "\n" +
                "mediump vec3 resultColor = topLeftColor * convolutionMatrix[0][0] + topColor * convolutionMatrix[0][1] + topRightColor * convolutionMatrix[0][2];\n" +
                "resultColor += leftColor * convolutionMatrix[1][0] + centerColor.rgb * convolutionMatrix[1][1] + rightColor * convolutionMatrix[1][2];\n" +
                "resultColor += bottomLeftColor * convolutionMatrix[2][0] + bottomColor * convolutionMatrix[2][1] + bottomRightColor * convolutionMatrix[2][2];\n" +
                "\n" +
                "// Normalize the results to allow for negative gradients in the 0.0-1.0 colorspace\n" +
                "resultColor = resultColor + 0.5;\n" +
                "\n" +
                "gl_FragColor = vec4(resultColor, centerColor.a);\n" +
                "}\n"
    }
}
