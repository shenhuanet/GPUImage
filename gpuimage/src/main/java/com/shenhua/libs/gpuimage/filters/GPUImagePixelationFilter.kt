package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImagePixelationFilter : GPUImageFilter(GPUImageFilter.Companion.NO_FILTER_VERTEX_SHADER, PIXELATION_FRAGMENT_SHADER) {

    private var mImageWidthFactorLocation: Int = 0
    private var mImageHeightFactorLocation: Int = 0
    private var mPixel: Float = 0.toFloat()
    private var mPixelLocation: Int = 0

    init {
        mPixel = 1.0f
    }

    override fun onInit() {
        super.onInit()
        mImageWidthFactorLocation = GLES20.glGetUniformLocation(program, "imageWidthFactor")
        mImageHeightFactorLocation = GLES20.glGetUniformLocation(program, "imageHeightFactor")
        mPixelLocation = GLES20.glGetUniformLocation(program, "pixel")
        setPixel(mPixel)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setFloat(mImageWidthFactorLocation, 1.0f / width)
        setFloat(mImageHeightFactorLocation, 1.0f / height)
    }

    fun setPixel(pixel: Float) {
        mPixel = pixel
        setFloat(mPixelLocation, mPixel)
    }

    companion object {
        val PIXELATION_FRAGMENT_SHADER = "" +
                "precision highp float;\n" +

                "varying vec2 textureCoordinate;\n" +

                "uniform float imageWidthFactor;\n" +
                "uniform float imageHeightFactor;\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform float pixel;\n" +

                "void main()\n" +
                "{\n" +
                "  vec2 uv  = textureCoordinate.xy;\n" +
                "  float dx = pixel * imageWidthFactor;\n" +
                "  float dy = pixel * imageHeightFactor;\n" +
                "  vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));\n" +
                "  vec3 tc = texture2D(inputImageTexture, coord).xyz;\n" +
                "  gl_FragColor = vec4(tc, 1.0);\n" +
                "}"
    }
}
