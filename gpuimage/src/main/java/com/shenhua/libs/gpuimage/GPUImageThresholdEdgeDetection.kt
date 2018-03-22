package com.shenhua.libs.gpuimage

import com.shenhua.libs.gpuimage.filters.GPUImage3x3TextureSamplingFilter
import com.shenhua.libs.gpuimage.filters.GPUImageGrayscaleFilter
import com.shenhua.libs.gpuimage.filters.GPUImageSobelThresholdFilter

/**
 * Applies sobel edge detection on the image.
 */
class GPUImageThresholdEdgeDetection : GPUImageFilterGroup() {
    init {
        addFilter(GPUImageGrayscaleFilter())
        addFilter(GPUImageSobelThresholdFilter())
    }

    fun setLineSize(size: Float) {
        (filters!![1] as GPUImage3x3TextureSamplingFilter).setLineSize(size)
    }

    fun setThreshold(threshold: Float) {
        (filters!![1] as GPUImageSobelThresholdFilter).setThreshold(threshold)
    }
}
