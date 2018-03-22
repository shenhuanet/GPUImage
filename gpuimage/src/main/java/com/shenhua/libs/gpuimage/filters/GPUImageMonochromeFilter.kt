package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Converts the image to a single-color version, based on the luminance of each pixel
 * intensity: The degree to which the specific color replaces the normal image color (0.0 - 1.0, with 1.0 as the default)
 * color: The color to use as the basis for the effect, with (0.6, 0.45, 0.3, 1.0) as the default.
 */
class GPUImageMonochromeFilter @JvmOverloads constructor(private var mIntensity: Float = 1.0f, private var mColor: FloatArray? = floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, MONOCHROME_FRAGMENT_SHADER) {

    private var mIntensityLocation: Int = 0
    private var mFilterColorLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mIntensityLocation = GLES20.glGetUniformLocation(program, "intensity")
        mFilterColorLocation = GLES20.glGetUniformLocation(program, "filterColor")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(1.0f)
        setColor(floatArrayOf(0.6f, 0.45f, 0.3f, 1f))
    }

    fun setIntensity(intensity: Float) {
        mIntensity = intensity
        setFloat(mIntensityLocation, mIntensity)
    }

    fun setColor(color: FloatArray) {
        mColor = color
        setColorRed(mColor!![0], mColor!![1], mColor!![2])

    }

    fun setColorRed(red: Float, green: Float, blue: Float) {
        setFloatVec3(mFilterColorLocation, floatArrayOf(red, green, blue))
    }

    companion object {
        val MONOCHROME_FRAGMENT_SHADER = "" +
                " precision lowp float;\n" +
                "  \n" +
                "  varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                "  uniform sampler2D inputImageTexture;\n" +
                "  uniform float intensity;\n" +
                "  uniform vec3 filterColor;\n" +
                "  \n" +
                "  const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                "  \n" +
                "  void main()\n" +
                "  {\n" +
                " 	//desat, then apply overlay blend\n" +
                " 	lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                " 	float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                " 	\n" +
                " 	lowp vec4 desat = vec4(vec3(luminance), 1.0);\n" +
                " 	\n" +
                " 	//overlay\n" +
                " 	lowp vec4 outputColor = vec4(\n" +
                "                                  (desat.r < 0.5 ? (2.0 * desat.r * filterColor.r) : (1.0 - 2.0 * (1.0 - desat.r) * (1.0 - filterColor.r))),\n" +
                "                                  (desat.g < 0.5 ? (2.0 * desat.g * filterColor.g) : (1.0 - 2.0 * (1.0 - desat.g) * (1.0 - filterColor.g))),\n" +
                "                                  (desat.b < 0.5 ? (2.0 * desat.b * filterColor.b) : (1.0 - 2.0 * (1.0 - desat.b) * (1.0 - filterColor.b))),\n" +
                "                                  1.0\n" +
                "                                  );\n" +
                " 	\n" +
                " 	//which is better, or are they equal?\n" +
                " 	gl_FragColor = vec4( mix(textureColor.rgb, outputColor.rgb, intensity), textureColor.a);\n" +
                "  }"
    }
}
