package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageHalftoneFilter @JvmOverloads constructor(private var mFractionalWidthOfAPixel: Float = 0.01f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, HALFTONE_FRAGMENT_SHADER) {

    private var mFractionalWidthOfPixelLocation: Int = 0
    private var mAspectRatioLocation: Int = 0
    private var mAspectRatio: Float = 0.toFloat()

    override fun onInit() {
        super.onInit()
        mFractionalWidthOfPixelLocation = GLES20.glGetUniformLocation(program, "fractionalWidthOfPixel")
        mAspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
        setFractionalWidthOfAPixel(mFractionalWidthOfAPixel)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setAspectRatio(height.toFloat() / width.toFloat())
    }

    fun setFractionalWidthOfAPixel(fractionalWidthOfAPixel: Float) {
        mFractionalWidthOfAPixel = fractionalWidthOfAPixel
        setFloat(mFractionalWidthOfPixelLocation, mFractionalWidthOfAPixel)
    }

    fun setAspectRatio(aspectRatio: Float) {
        mAspectRatio = aspectRatio
        setFloat(mAspectRatioLocation, mAspectRatio)
    }

    companion object {
        val HALFTONE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +

                "uniform sampler2D inputImageTexture;\n" +

                "uniform highp float fractionalWidthOfPixel;\n" +
                "uniform highp float aspectRatio;\n" +

                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +

                "void main()\n" +
                "{\n" +
                "  highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
                "  highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
                "  highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "  highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "  highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
                "  lowp vec3 sampledColor = texture2D(inputImageTexture, samplePos).rgb;\n" +
                "  highp float dotScaling = 1.0 - dot(sampledColor, W);\n" +
                "  lowp float checkForPresenceWithinDot = 1.0 - step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
                "  gl_FragColor = vec4(vec3(checkForPresenceWithinDot), 1.0);\n" +
                "}"
    }
}
