package com.shenhua.libs.gpuimage.filters

import android.graphics.PointF
import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageGlassSphereFilter @JvmOverloads constructor(private var mCenter: PointF? = PointF(0.5f, 0.5f), private var mRadius: Float = 0.25f, private var mRefractiveIndex: Float = 0.71f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, SPHERE_FRAGMENT_SHADER) {
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

    fun setRefractiveIndex(refractiveIndex: Float) {
        mRefractiveIndex = refractiveIndex
        setFloat(mRefractiveIndexLocation, refractiveIndex)
    }

    fun setCenter(center: PointF?) {
        mCenter = center
        setPoint(mCenterLocation, center!!)
    }

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
                "// uniform vec3 lightPosition;\n" +
                "const highp vec3 lightPosition = vec3(-0.5, 0.5, 1.0);\n" +
                "const highp vec3 ambientLightPosition = vec3(0.0, 0.0, 1.0);\n" +
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
                "highp vec3 refractedVector = 2.0 * refract(vec3(0.0, 0.0, -1.0), sphereNormal, refractiveIndex);\n" +
                "refractedVector.xy = -refractedVector.xy;\n" +
                "\n" +
                "highp vec3 finalSphereColor = texture2D(inputImageTexture, (refractedVector.xy + 1.0) * 0.5).rgb;\n" +
                "\n" +
                "// Grazing angle lighting\n" +
                "highp float lightingIntensity = 2.5 * (1.0 - pow(clamp(dot(ambientLightPosition, sphereNormal), 0.0, 1.0), 0.25));\n" +
                "finalSphereColor += lightingIntensity;\n" +
                "\n" +
                "// Specular lighting\n" +
                "lightingIntensity  = clamp(dot(normalize(lightPosition), sphereNormal), 0.0, 1.0);\n" +
                "lightingIntensity  = pow(lightingIntensity, 15.0);\n" +
                "finalSphereColor += vec3(0.8, 0.8, 0.8) * lightingIntensity;\n" +
                "\n" +
                "gl_FragColor = vec4(finalSphereColor, 1.0) * checkForPresenceWithinSphere;\n" +
                "}\n"
    }
}
