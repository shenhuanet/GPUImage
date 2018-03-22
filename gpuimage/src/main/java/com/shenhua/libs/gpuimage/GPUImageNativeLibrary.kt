package com.shenhua.libs.gpuimage

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
object GPUImageNativeLibrary {

    init {
        System.loadLibrary("yuv-decoder")
    }

    external fun YUVtoRBGA(yuv: ByteArray, width: Int, height: Int, out: IntArray)

    external fun YUVtoARBG(yuv: ByteArray, width: Int, height: Int, out: IntArray)
}
