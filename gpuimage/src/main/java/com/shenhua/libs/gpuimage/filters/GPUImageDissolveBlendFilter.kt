package com.shenhua.libs.gpuimage.filters

/**
 * Mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
 */
class GPUImageDissolveBlendFilter : GPUImageMixBlendFilter {

    constructor() : super(DISSOLVE_BLEND_FRAGMENT_SHADER) {}

    constructor(mix: Float) : super(DISSOLVE_BLEND_FRAGMENT_SHADER, mix) {}

    companion object {
        val DISSOLVE_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                "\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " uniform lowp float mixturePercent;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                "    \n" +
                "    gl_FragColor = mix(textureColor, textureColor2, mixturePercent);\n" +
                " }"
    }
}
