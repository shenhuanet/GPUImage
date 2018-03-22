package com.shenhua.libs.gpuimage.filters

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageSoftLightBlendFilter : GPUImageTwoInputFilter(SOFT_LIGHT_BLEND_FRAGMENT_SHADER) {
    companion object {
        val SOFT_LIGHT_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                "\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
                "     mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                "     \n" +
                "     gl_FragColor = base * (overlay.a * (base / base.a) + (2.0 * overlay * (1.0 - (base / base.a)))) + overlay * (1.0 - base.a) + base * (1.0 - overlay.a);\n" +
                " }"
    }
}
