package com.shenhua.libs.gpuimage.filters

import android.graphics.PointF
import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageSphereRefractionFilter @JvmOverloads constructor(private var mCenter: PointF? = PointF(0.5f, 0.5f), private var mRadius: Float = 0.25f, private var mRefractiveIndex: Float = 0.71f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, SPHERE_FRAGMENT_SHADER) {
    private var mCenterLocation: Int = 0
    private var mRadiusLocation: Int = 0
    private var mAspectRatio: Float = 0.toFloat()
    private var mAspectRatioLocation: Int = 0
    private var mRefractiveIndexLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mCenterLocation = GLES20.glGetUniformLocation(program, "center")
        mRadiusLocation = GLES20.glGetUniformLocation(program, "radius")
        mAspectRatioLocation = GLES20.glGetUniformLocation(program, "aspectRatio")
        mRefractiveIndexLocation = GLES20.glGetUniformLocation(program, "refractiveIndex")
    }

    override fun onInitialized() {
        super.onInitialized()
        setRadius(mRadius)
        setCenter(mCenter)
        setRefractiveIndex(mRefractiveIndex)
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
     * The index of refraction for the sphere, with a default of 0.71
     *
     * @param refractiveIndex default 0.71
     */
    fun setRefractiveIndex(refractiveIndex: Float) {
        mRefractiveIndex = refractiveIndex
        setFloat(mRefractiveIndexLocation, refractiveIndex)
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

    /**
     * The radius of the distortion, ranging from 0.0 to 1.0, with a default of 0.25
     *
     * @param radius from 0.0 to 1.0, default 0.25
     */
    fun setRadius(radius: Float) {
        mRadius = radius
        setFloat(mRadiusLocation, radius)
    }

    companion object {
        val SPHERE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform highp vec2 center;\n" +
                "uniform highp float radius;\n" +
                "uniform highp float aspectRatio;\n" +
                "uniform highp float refractiveIndex;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
                "highp float distanceFromCenter = distance(center, textureCoordinateToUse);\n" +
                "lowp float checkForPresenceWithinSphere = step(distanceFromCenter, radius);\n" +
                "\n" +
                "distanceFromCenter = distanceFromCenter / radius;\n" +
                "\n" +
                "highp float normalizedDepth = radius * sqrt(1.0 - distanceFromCenter * distanceFromCenter);\n" +
                "highp vec3 sphereNormal = normalize(vec3(textureCoordinateToUse - center, normalizedDepth));\n" +
                "\n" +
                "highp vec3 refractedVector = refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);\n" +
                "\n" +
                "gl_FragColor = texture2D(inputImageTexture, (refractedVector.xy + 1.0) * 0.5) * checkForPresenceWithinSphere;     \n" +
                "}\n"
    }
}
