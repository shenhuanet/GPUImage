package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageLevelsFilter private constructor(private val mMin: FloatArray, private val mMid: FloatArray, private val mMax: FloatArray, private val mMinOutput: FloatArray, private val mMaxOutput: FloatArray) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, LEVELS_FRAGMET_SHADER) {

    private var mMinLocation: Int = 0
    private var mMidLocation: Int = 0
    private var mMaxLocation: Int = 0
    private var mMinOutputLocation: Int = 0
    private var mMaxOutputLocation: Int = 0

    constructor() : this(floatArrayOf(0.0f, 0.0f, 0.0f), floatArrayOf(1.0f, 1.0f, 1.0f), floatArrayOf(1.0f, 1.0f, 1.0f), floatArrayOf(0.0f, 0.0f, 0.0f), floatArrayOf(1.0f, 1.0f, 1.0f)) {}

    init {
        setMin(0.0f, 1.0f, 1.0f, 0.0f, 1.0f)
    }

    override fun onInit() {
        super.onInit()
        mMinLocation = GLES20.glGetUniformLocation(program, "levelMinimum")
        mMidLocation = GLES20.glGetUniformLocation(program, "levelMiddle")
        mMaxLocation = GLES20.glGetUniformLocation(program, "levelMaximum")
        mMinOutputLocation = GLES20.glGetUniformLocation(program, "minOutput")
        mMaxOutputLocation = GLES20.glGetUniformLocation(program, "maxOutput")
    }

    override fun onInitialized() {
        super.onInitialized()
        updateUniforms()
    }


    fun updateUniforms() {
        setFloatVec3(mMinLocation, mMin)
        setFloatVec3(mMidLocation, mMid)
        setFloatVec3(mMaxLocation, mMax)
        setFloatVec3(mMinOutputLocation, mMinOutput)
        setFloatVec3(mMaxOutputLocation, mMaxOutput)
    }

    @JvmOverloads
    fun setMin(min: Float, mid: Float, max: Float, minOut: Float = 0.0f, maxOut: Float = 1.0f) {
        setRedMin(min, mid, max, minOut, maxOut)
        setGreenMin(min, mid, max, minOut, maxOut)
        setBlueMin(min, mid, max, minOut, maxOut)
    }

    @JvmOverloads
    fun setRedMin(min: Float, mid: Float, max: Float, minOut: Float = 0f, maxOut: Float = 1f) {
        mMin[0] = min
        mMid[0] = mid
        mMax[0] = max
        mMinOutput[0] = minOut
        mMaxOutput[0] = maxOut
        updateUniforms()
    }

    @JvmOverloads
    fun setGreenMin(min: Float, mid: Float, max: Float, minOut: Float = 0f, maxOut: Float = 1f) {
        mMin[1] = min
        mMid[1] = mid
        mMax[1] = max
        mMinOutput[1] = minOut
        mMaxOutput[1] = maxOut
        updateUniforms()
    }

    @JvmOverloads
    fun setBlueMin(min: Float, mid: Float, max: Float, minOut: Float = 0f, maxOut: Float = 1f) {
        mMin[2] = min
        mMid[2] = mid
        mMax[2] = max
        mMinOutput[2] = minOut
        mMaxOutput[2] = maxOut
        updateUniforms()
    }

    companion object {

        private val LOGTAG = GPUImageLevelsFilter::class.java.simpleName

        val LEVELS_FRAGMET_SHADER = " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform mediump vec3 levelMinimum;\n" +
                " uniform mediump vec3 levelMiddle;\n" +
                " uniform mediump vec3 levelMaximum;\n" +
                " uniform mediump vec3 minOutput;\n" +
                " uniform mediump vec3 maxOutput;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     mediump vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     \n" +
                "     gl_FragColor = vec4( mix(minOutput, maxOutput, pow(min(max(textureColor.rgb -levelMinimum, vec3(0.0)) / (levelMaximum - levelMinimum  ), vec3(1.0)), 1.0 /levelMiddle)) , textureColor.a);\n" +
                " }\n"
    }
}
