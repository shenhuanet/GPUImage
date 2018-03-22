package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * The haze filter can be used to add or remove haze.
 *
 * This is similar to a UV filter.
 */
class GPUImageHazeFilter @JvmOverloads constructor(private var mDistance: Float = 0.2f, private var mSlope: Float = 0.0f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, HAZE_FRAGMENT_SHADER) {
    private var mDistanceLocation: Int = 0
    private var mSlopeLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mDistanceLocation = GLES20.glGetUniformLocation(program, "distance")
        mSlopeLocation = GLES20.glGetUniformLocation(program, "slope")
    }

    override fun onInitialized() {
        super.onInitialized()
        setDistance(mDistance)
        setSlope(mSlope)
    }

    /**
     * Strength of the color applied. Default 0. Values between -.3 and .3 are best.
     *
     * @param distance -0.3 to 0.3 are best, default 0
     */
    fun setDistance(distance: Float) {
        mDistance = distance
        setFloat(mDistanceLocation, distance)
    }

    /**
     * Amount of color change. Default 0. Values between -.3 and .3 are best.
     *
     * @param slope -0.3 to 0.3 are best, default 0
     */
    fun setSlope(slope: Float) {
        mSlope = slope
        setFloat(mSlopeLocation, slope)
    }

    companion object {
        val HAZE_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "uniform lowp float distance;\n" +
                "uniform highp float slope;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "	//todo reconsider precision modifiers	 \n" +
                "	 highp vec4 color = vec4(1.0);//todo reimplement as a parameter\n" +
                "\n" +
                "	 highp float  d = textureCoordinate.y * slope  +  distance; \n" +
                "\n" +
                "	 highp vec4 c = texture2D(inputImageTexture, textureCoordinate) ; // consider using unpremultiply\n" +
                "\n" +
                "	 c = (c - d * color) / (1.0 -d);\n" +
                "\n" +
                "	 gl_FragColor = c; //consider using premultiply(c);\n" +
                "}\n"
    }
}
