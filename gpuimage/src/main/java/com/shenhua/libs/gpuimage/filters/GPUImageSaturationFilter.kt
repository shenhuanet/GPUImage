package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * saturation: The degree of saturation or desaturation to apply to the image (0.0 - 2.0, with 1.0 as the default)
 *
 * @author shenhua
 */
class GPUImageSaturationFilter @JvmOverloads constructor(private var mSaturation: Float = 1.0f) : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, SATURATION_FRAGMENT_SHADER) {

    private var mSaturationLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mSaturationLocation = GLES20.glGetUniformLocation(program, "saturation")
    }

    override fun onInitialized() {
        super.onInitialized()
        setSaturation(mSaturation)
    }

    fun setSaturation(saturation: Float) {
        mSaturation = saturation
        setFloat(mSaturationLocation, mSaturation)
    }

    companion object {
        val SATURATION_FRAGMENT_SHADER = "" +
                " varying highp vec2 textureCoordinate;\n" +
                " \n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform lowp float saturation;\n" +
                " \n" +
                " // Values from \"Graphics Shaders: Theory and Practice\" by Bailey and Cunningham\n" +
                " const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
                "    lowp vec3 greyScaleColor = vec3(luminance);\n" +
                "    \n" +
                "    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);\n" +
                "     \n" +
                " }"
    }
}
