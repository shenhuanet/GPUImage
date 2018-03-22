package com.shenhua.libs.gpuimage.filters

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageSubtractBlendFilter : GPUImageTwoInputFilter(SUBTRACT_BLEND_FRAGMENT_SHADER) {
    companion object {
        val SUBTRACT_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
                " varying highp vec2 textureCoordinate2;\n" +
                "\n" +
                " uniform sampler2D inputImageTexture;\n" +
                " uniform sampler2D inputImageTexture2;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "   lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "   lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                "\n" +
                "   gl_FragColor = vec4(textureColor.rgb - textureColor2.rgb, textureColor.a);\n" +
                " }"
    }
}
