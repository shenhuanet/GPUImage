package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
open class GPUImageMixBlendFilter @JvmOverloads constructor(fragmentShader: String, private var mMix: Float = 0.5f) : GPUImageTwoInputFilter(fragmentShader) {

    private var mMixLocation: Int = 0

    override fun onInit() {
        super.onInit()
        mMixLocation = GLES20.glGetUniformLocation(program, "mixturePercent")
    }

    override fun onInitialized() {
        super.onInitialized()
        setMix(mMix)
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    fun setMix(mix: Float) {
        mMix = mix
        setFloat(mMixLocation, mMix)
    }
}
