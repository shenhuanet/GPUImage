package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Adjusts the shadows and highlights of an image
 * shadows: Increase to lighten shadows, from 0.0 to 1.0, with 0.0 as the default.
 * highlights: Decrease to darken highlights, from 0.0 to 1.0, with 1.0 as the default.
 */
class GPUImageHighlightShadowFilter @JvmOverloads constructor(private var mShadows: Float = 0.0f, private var mHighlights: Float = 1.0f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, HIGHLIGHT_SHADOW_FRAGMENT_SHADER) {

    private var mShadowsLocation: Int = 0
    private var mHighlightsLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mHighlightsLocation = GLES20.glGetUniformLocation(program, "highlights")
        mShadowsLocation = GLES20.glGetUniformLocation(program, "shadows")
    }

    override fun onInitialized() {
        super.onInitialized()
        setHighlights(mHighlights)
        setShadows(mShadows)
    }

    fun setHighlights(highlights: Float) {
        mHighlights = highlights
        setFloat(mHighlightsLocation, mHighlights)
    }

    fun setShadows(shadows: Float) {
        mShadows = shadows
        setFloat(mShadowsLocation, mShadows)
    }

    companion object {
        val HIGHLIGHT_SHADOW_FRAGMENT_SHADER = "" +
                " uniform sampler2D inputImageTexture;\n" +
                " varying highp vec2 textureCoordinate;\n" +
                "  \n" +
                " uniform lowp float shadows;\n" +
                " uniform lowp float highlights;\n" +
                " \n" +
                " const mediump vec3 luminanceWeighting = vec3(0.3, 0.3, 0.3);\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                " 	lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);\n" +
                " 	mediump float luminance = dot(source.rgb, luminanceWeighting);\n" +
                " \n" +
                " 	mediump float shadow = clamp((pow(luminance, 1.0/(shadows+1.0)) + (-0.76)*pow(luminance, 2.0/(shadows+1.0))) - luminance, 0.0, 1.0);\n" +
                " 	mediump float highlight = clamp((1.0 - (pow(1.0-luminance, 1.0/(2.0-highlights)) + (-0.8)*pow(1.0-luminance, 2.0/(2.0-highlights)))) - luminance, -1.0, 0.0);\n" +
                " 	lowp vec3 result = vec3(0.0, 0.0, 0.0) + ((luminance + shadow + highlight) - 0.0) * ((source.rgb - vec3(0.0, 0.0, 0.0))/(luminance - 0.0));\n" +
                " \n" +
                " 	gl_FragColor = vec4(result.rgb, source.a);\n" +
                " }"
    }
}
