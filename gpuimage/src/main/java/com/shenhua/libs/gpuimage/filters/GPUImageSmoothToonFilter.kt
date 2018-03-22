package com.shenhua.libs.gpuimage.filters

import com.shenhua.libs.gpuimage.GPUImageFilterGroup

/**
 * This uses a similar process as the GPUImageToonFilter, only it precedes the toon effect
 * with a Gaussian blur to smooth out noise.
 *
 * @author shenhua
 */
class GPUImageSmoothToonFilter : GPUImageFilterGroup() {
    internal var blurFilter: GPUImageGaussianBlurFilter
    internal var toonFilter: GPUImageToonFilter

    /**
     * Setup and Tear down
     */
    init {
        // First pass: apply a variable Gaussian blur
        blurFilter = GPUImageGaussianBlurFilter()
        addFilter(blurFilter)

        // Second pass: run the Sobel edge detection on this blurred image, along with a posterization effect
        toonFilter = GPUImageToonFilter()
        addFilter(toonFilter)

        filters!!.add(blurFilter)

        setBlurSize(0.5f)
        setThreshold(0.2f)
        setQuantizationLevels(10.0f)
    }

    /**
     * Accessors
     */
    fun setTexelWidth(value: Float) {
        toonFilter.setTexelWidth(value)
    }

    fun setTexelHeight(value: Float) {
        toonFilter.setTexelHeight(value)
    }

    fun setBlurSize(value: Float) {
        blurFilter.setBlurSize(value)
    }

    fun setThreshold(value: Float) {
        toonFilter.setThreshold(value)
    }

    fun setQuantizationLevels(value: Float) {
        toonFilter.setQuantizationLevels(value)
    }

}
