package com.shenhua.libs.gpuimage.filters

import com.shenhua.libs.gpuimage.GPUImageFilterGroup

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
open class GPUImageTwoPassFilter(firstVertexShader: String, firstFragmentShader: String,
                                 secondVertexShader: String, secondFragmentShader: String) : GPUImageFilterGroup(null) {
    init {
        addFilter(GPUImageFilter(firstVertexShader, firstFragmentShader))
        addFilter(GPUImageFilter(secondVertexShader, secondFragmentShader))
    }
}
