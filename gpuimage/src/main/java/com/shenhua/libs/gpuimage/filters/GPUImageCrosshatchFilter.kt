package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * crossHatchSpacing: The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
 * lineWidth: A relative width for the crosshatch lines. The default is 0.003.
 */
class GPUImageCrosshatchFilter @JvmOverloads constructor(private var mCrossHatchSpacing: Float = 0.03f, private var mLineWidth: Float = 0.003f) : GPUImageFilter(NO_FILTER_VERTEX_SHADER, CROSSHATCH_FRAGMENT_SHADER) {
    private var mCrossHatchSpacingLocation: Int = 0
    private var mLineWidthLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mCrossHatchSpacingLocation = GLES20.glGetUniformLocation(program, "crossHatchSpacing")
        mLineWidthLocation = GLES20.glGetUniformLocation(program, "lineWidth")
    }

    override fun onInitialized() {
        super.onInitialized()
        setCrossHatchSpacing(mCrossHatchSpacing)
        setLineWidth(mLineWidth)
    }

    /**
     * The fractional width of the image to use as the spacing for the crosshatch. The default is 0.03.
     *
     * @param crossHatchSpacing default 0.03
     */
    fun setCrossHatchSpacing(crossHatchSpacing: Float) {
        val singlePixelSpacing: Float
        if (outputWidth != 0) {
            singlePixelSpacing = 1.0f / outputWidth.toFloat()
        } else {
            singlePixelSpacing = 1.0f / 2048.0f
        }

        if (crossHatchSpacing < singlePixelSpacing) {
            mCrossHatchSpacing = singlePixelSpacing
        } else {
            mCrossHatchSpacing = crossHatchSpacing
        }

        setFloat(mCrossHatchSpacingLocation, mCrossHatchSpacing)
    }

    /**
     * A relative width for the crosshatch lines. The default is 0.003.
     *
     * @param lineWidth default 0.003
     */
    fun setLineWidth(lineWidth: Float) {
        mLineWidth = lineWidth
        setFloat(mLineWidthLocation, mLineWidth)
    }

    companion object {
        val CROSSHATCH_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                "uniform sampler2D inputImageTexture;\n" +
                "uniform highp float crossHatchSpacing;\n" +
                "uniform highp float lineWidth;\n" +
                "const highp vec3 W = vec3(0.2125, 0.7154, 0.0721);\n" +
                "void main()\n" +
                "{\n" +
                "highp float luminance = dot(texture2D(inputImageTexture, textureCoordinate).rgb, W);\n" +
                "lowp vec4 colorToDisplay = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                "if (luminance < 1.00)\n" +
                "{\n" +
                "if (mod(textureCoordinate.x + textureCoordinate.y, crossHatchSpacing) <= lineWidth)\n" +
                "{\n" +
                "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                "}\n" +
                "}\n" +
                "if (luminance < 0.75)\n" +
                "{\n" +
                "if (mod(textureCoordinate.x - textureCoordinate.y, crossHatchSpacing) <= lineWidth)\n" +
                "{\n" +
                "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                "}\n" +
                "}\n" +
                "if (luminance < 0.50)\n" +
                "{\n" +
                "if (mod(textureCoordinate.x + textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
                "{\n" +
                "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                "}\n" +
                "}\n" +
                "if (luminance < 0.3)\n" +
                "{\n" +
                "if (mod(textureCoordinate.x - textureCoordinate.y - (crossHatchSpacing / 2.0), crossHatchSpacing) <= lineWidth)\n" +
                "{\n" +
                "colorToDisplay = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                "}\n" +
                "}\n" +
                "gl_FragColor = colorToDisplay;\n" +
                "}\n"
    }
}
/**
 * Using default values of crossHatchSpacing: 0.03f and lineWidth: 0.003f.
 */
