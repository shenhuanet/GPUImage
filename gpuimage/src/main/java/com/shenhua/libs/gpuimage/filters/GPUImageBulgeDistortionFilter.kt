package com.shenhua.libs.gpuimage.filters

import android.graphics.PointF
import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageBulgeDistortionFilter @JvmOverloads constructor(private var mRadius: Float = 0.25f, private var mScale: Float = 0.5f, private var mCenter: PointF? = PointF(0.5f, 0.5f)) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, BULGE_FRAGMENT_SHADER) {
    private var mScaleLocation: Int = 0
    private var mRadiusLocation: Int = 0
    private var mCenterLocation: Int = 0
    private var mAspectRatio: Float = 0.toFloat()
    private var mAspectRatioLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mScaleLocation = GLES20.glGetUniformLocation(program, "scale")
        mRadiusLocation = GLES20.glGetUniformLocation(program, "radius")
        mCenterLocation = GLES20.glGetUniformLocation(program, "center")
        mAspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
    }

    override fun onInitialized() {
        super.onInitialized()
        setRadius(mRadius)
        setScale(mScale)
        setCenter(mCenter)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        mAspectRatio = height.toFloat() / width
        setAspectRatio(mAspectRatio)
        super.onOutputSizeChanged(width, height)
    }

    private fun setAspectRatio(aspectRatio: Float) {
        mAspectRatio = aspectRatio
        setFloat(mAspectRatioLocation, aspectRatio)
    }

    /**
     * The radius of the distortion, ranging from 0.0 to 1.0, with a default of 0.25
     *
     * @param radius from 0.0 to 1.0, default 0.25
     */
    fun setRadius(radius: Float) {
        mRadius = radius
        setFloat(mRadiusLocation, radius)
    }

    /**
     * The amount of distortion to apply, from -1.0 to 1.0, with a default of 0.5
     *
     * @param scale from -1.0 to 1.0, default 0.5
     */
    fun setScale(scale: Float) {
        mScale = scale
        setFloat(mScaleLocation, scale)
    }

    /**
     * The center about which to apply the distortion, with a default of (0.5, 0.5)
     *
     * @param center default (0.5, 0.5)
     */
    fun setCenter(center: PointF?) {
        mCenter = center
        setPoint(mCenterLocation, center!!)
    }

    companion object {
        val BULGE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp float aspectRatio;\n" +
                "uniform highp vec2 center;\n" +
                "uniform highp float radius;\n" +
                "uniform highp float scale;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "highp float dist = distance(center, textureCoordinateToUse);\n" +
                "textureCoordinateToUse = textureCoordinate;\n" +
                "\n" +
                "if (dist < radius)\n" +
                "{\n" +
                "textureCoordinateToUse -= center;\n" +
                "highp float percent = 1.0 - ((radius - dist) / radius) * scale;\n" +
                "percent = percent * percent;\n" +
                "\n" +
                "textureCoordinateToUse = textureCoordinateToUse * percent;\n" +
                "textureCoordinateToUse += center;\n" +
                "}\n" +
                "\n" +
                "gl_FragColor = texture2D(inputImageTexture, textureCoordinateToUse );    \n" +
                "}\n"
    }
}
