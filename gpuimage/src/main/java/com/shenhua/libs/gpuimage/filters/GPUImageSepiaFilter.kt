package com.shenhua.libs.gpuimage.filters

import com.shenhua.libs.gpuimage.filters.GPUImageColorMatrixFilter

/**
 * Applies a simple sepia effect.
 *
 * @author shenhua
 */
class GPUImageSepiaFilter @JvmOverloads constructor(intensity: Float = 1.0f) : GPUImageColorMatrixFilter(intensity, floatArrayOf(0.3588f, 0.7044f, 0.1368f, 0.0f, 0.2990f, 0.5870f, 0.1140f, 0.0f, 0.2392f, 0.4696f, 0.0912f, 0.0f, 0f, 0f, 0f, 1.0f))
