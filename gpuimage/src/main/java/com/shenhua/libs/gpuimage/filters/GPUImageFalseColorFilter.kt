package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageFalseColorFilter(private var mFirstColor: FloatArray?, private var mSecondColor: FloatArray?) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, FALSECOLOR_FRAGMENT_SHADER) {
    private var mFirstColorLocation: Int = 0
    private var mSecondColorLocation: Int = 0

    @JvmOverloads constructor(firstRed: Float = 0f, firstGreen: Float = 0f, firstBlue: Float = 0.5f, secondRed: Float = 1f, secondGreen: Float = 0f, secondBlue: Float = 0f) : this(floatArrayOf(firstRed, firstGreen, firstBlue), floatArrayOf(secondRed, secondGreen, secondBlue)) {}

    override fun onInit() {
        super.onInit()
        mFirstColorLocation = GLES20.glGetUniformLocation(program, "firstColor")
        mSecondColorLocation = GLES20.glGetUniformLocation(program, "secondColor")
    }

    override fun onInitialized() {
        super.onInitialized()
        setFirstColor(mFirstColor)
        setSecondColor(mSecondColor)
    }

    fun setFirstColor(firstColor: FloatArray?) {
        mFirstColor = firstColor
        setFloatVec3(mFirstColorLocation, firstColor!!)
    }

    fun setSecondColor(secondColor: FloatArray?) {
        mSecondColor = secondColor
        setFloatVec3(mSecondColorLocation, secondColor!!)
    }

    companion object {
        val FALSECOLOR_FRAGMENT_SHADER = "" +
                "precision lowp float;\n" +
                "\n" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform float intensity;\n" +
                "uniform vec3 firstColor;\n" +
                "uniform vec3 secondColor;\n" +
                "\n" +
                "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                "\n" +
                "gl_FragColor = vec4( mix(firstColor.rgb, secondColor.rgb, luminance), textureColor.a);\n" +
                "}\n"
    }
}
