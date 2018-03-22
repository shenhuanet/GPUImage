package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
open class GPUImageTwoPassTextureSamplingFilter(firstVertexShader: String, firstFragmentShader: String,
                                                secondVertexShader: String, secondFragmentShader: String) : GPUImageTwoPassFilter(firstVertexShader, firstFragmentShader, secondVertexShader, secondFragmentShader) {

    open var verticalTexelOffsetRatio: Float = 1f
        get() = 1f

    open val horizontalTexelOffsetRatio: Float
        get() = 1f

    override fun onInit() {
        super.onInit()
        initTexelOffsets()
    }

    protected fun initTexelOffsets() {
        var ratio = horizontalTexelOffsetRatio
        var filter = mFilters!![0]
        var texelWidthOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelWidthOffset")
        var texelHeightOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelHeightOffset")
        filter.setFloat(texelWidthOffsetLocation, ratio / outputWidth)
        filter.setFloat(texelHeightOffsetLocation, 0f)

        ratio = verticalTexelOffsetRatio
        filter = mFilters!![1]
        texelWidthOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelWidthOffset")
        texelHeightOffsetLocation = GLES20.glGetUniformLocation(filter.program, "texelHeightOffset")
        filter.setFloat(texelWidthOffsetLocation, 0f)
        filter.setFloat(texelHeightOffsetLocation, ratio / outputHeight)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        initTexelOffsets()
    }
}
