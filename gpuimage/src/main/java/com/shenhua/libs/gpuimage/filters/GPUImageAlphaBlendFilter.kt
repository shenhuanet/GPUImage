package com.shenhua.libs.gpuimage.filters

/**
 * Mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageAlphaBlendFilter : GPUImageMixBlendFilter {

    constructor() : super(ALPHA_BLEND_FRAGMENT_SHADER) {}

    constructor(mix: Float) : super(ALPHA_BLEND_FRAGMENT_SHADER, mix) {}

    companion object {
        val ALPHA_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                "\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " \n" +
                " uniform lowp float mixturePercent;\n" +
                "\n" +
                " void main()\n" +
                " {\n" +
                "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                "\n" +
                "   gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, textureColor2.a * mixturePercent), textureColor.a);\n" +
                " }"
    }
}
