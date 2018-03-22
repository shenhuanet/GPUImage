package com.shenhua.libs.gpuimage.filters

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageColorInvertFilter : GPUImageFilter(NO_FILTER_VERTEX_SHADER, COLOR_INVERT_FRAGMENT_SHADER) {
    companion object {
        val COLOR_INVERT_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "    \n" +
                "    gl_FragColor = vec4((1.0 - textureColor.rgb), textureColor.w);\n" +
                "}"
    }
}
