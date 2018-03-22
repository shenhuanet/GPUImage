package com.shenhua.libs.gpuimage.filters

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageGrayscaleFilter : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, GRAYSCALE_FRAGMENT_SHADER) {
    companion object {
        val GRAYSCALE_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +
                "\n" +
                "varying vec2 textureCoordinate;\n" +
                "\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "  lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "  float luminance = dot(textureColor.rgb, W);\n" +
                "\n" +
                "  gl_FragColor = vec4(vec3(luminance), textureColor.a);\n" +
                "}"
    }
}
