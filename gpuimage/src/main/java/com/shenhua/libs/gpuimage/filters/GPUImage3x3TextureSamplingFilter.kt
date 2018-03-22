package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
open class GPUImage3x3TextureSamplingFilter @JvmOverloads constructor(fragmentShader: String = NO_FILTER_VERTEX_SHADER) : GPUImageFilter(THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER, fragmentShader) {

    private var mUniformTexelWidthLocation: Int = 0
    private var mUniformTexelHeightLocation: Int = 0

    private var mHasOverriddenImageSizeFactor = false
    private var mTexelWidth: Float = 0.toFloat()
    private var mTexelHeight: Float = 0.toFloat()
    private var mLineSize = 1.0f

    override fun onInit() {
        super.onInit()
        mUniformTexelWidthLocation = GLES20.glGetUniformLocation(program, "texelWidth")
        mUniformTexelHeightLocation = GLES20.glGetUniformLocation(program, "texelHeight")
        if (mTexelWidth != 0f) {
            updateTexelValues()
        }
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        if (!mHasOverriddenImageSizeFactor) {
            setLineSize(mLineSize)
        }
    }

    fun setTexelWidth(texelWidth: Float) {
        mHasOverriddenImageSizeFactor = true
        mTexelWidth = texelWidth
        setFloat(mUniformTexelWidthLocation, texelWidth)
    }

    fun setTexelHeight(texelHeight: Float) {
        mHasOverriddenImageSizeFactor = true
        mTexelHeight = texelHeight
        setFloat(mUniformTexelHeightLocation, texelHeight)
    }

    fun setLineSize(size: Float) {
        mLineSize = size
        mTexelWidth = size / outputWidth
        mTexelHeight = size / outputHeight
        updateTexelValues()
    }

    private fun updateTexelValues() {
        setFloat(mUniformTexelWidthLocation, mTexelWidth)
        setFloat(mUniformTexelHeightLocation, mTexelHeight)
    }

    companion object {
        val THREE_X_THREE_TEXTURE_SAMPLING_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                "\n" +
                "uniform highp float texelWidth; \n" +
                "uniform highp float texelHeight; \n" +
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
                "    gl_Position = position;\n" +
                "\n" +
                "    vec2 widthStep = vec2(texelWidth, 0.0);\n" +
                "    vec2 heightStep = vec2(0.0, texelHeight);\n" +
                "    vec2 widthHeightStep = vec2(texelWidth, texelHeight);\n" +
                "    vec2 widthNegativeHeightStep = vec2(texelWidth, -texelHeight);\n" +
                "\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "    leftTextureCoordinate = inputTextureCoordinate.xy - widthStep;\n" +
                "    rightTextureCoordinate = inputTextureCoordinate.xy + widthStep;\n" +
                "\n" +
                "    topTextureCoordinate = inputTextureCoordinate.xy - heightStep;\n" +
                "    topLeftTextureCoordinate = inputTextureCoordinate.xy - widthHeightStep;\n" +
                "    topRightTextureCoordinate = inputTextureCoordinate.xy + widthNegativeHeightStep;\n" +
                "\n" +
                "    bottomTextureCoordinate = inputTextureCoordinate.xy + heightStep;\n" +
                "    bottomLeftTextureCoordinate = inputTextureCoordinate.xy - widthNegativeHeightStep;\n" +
                "    bottomRightTextureCoordinate = inputTextureCoordinate.xy + widthHeightStep;\n" +
                "}"
    }
}
