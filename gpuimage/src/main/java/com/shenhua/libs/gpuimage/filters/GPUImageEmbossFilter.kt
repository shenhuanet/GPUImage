package com.shenhua.libs.gpuimage.filters

/**
 * Applies an emboss effect to the image.<br></br>
 * <br></br>
 * Intensity ranges from 0.0 to 4.0, with 1.0 as the normal level
 */
class GPUImageEmbossFilter @JvmOverloads constructor(private var mIntensity: Float = 1.0f) : GPUImage3x3ConvolutionFilter() {

    var intensity: Float
        get() = mIntensity
        set(intensity) {
            mIntensity = intensity
            setConvolutionKernel(floatArrayOf(intensity * -2.0f, -intensity, 0.0f, -intensity, 1.0f, intensity, 0.0f, intensity, intensity * 2.0f))
        }

    override fun onInit() {
        super.onInit()
        intensity = mIntensity
    }
}
