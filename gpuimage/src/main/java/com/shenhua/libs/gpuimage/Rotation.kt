package com.shenhua.libs.gpuimage

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
enum class Rotation {
    NORMAL, ROTATION_90, ROTATION_180, ROTATION_270;

    /**
     * Retrieves the int representation of the Rotation.
     *
     * @return 0, 90, 180 or 270
     */
    fun asInt(): Int {
        when (this) {
            NORMAL -> return 0
            ROTATION_90 -> return 90
            ROTATION_180 -> return 180
            ROTATION_270 -> return 270
            else -> throw IllegalStateException("Unknown Rotation!")
        }
    }

    companion object {

        /**
         * Create a Rotation from an integer. Needs to be either 0, 90, 180 or 270.
         *
         * @param rotation 0, 90, 180 or 270
         * @return Rotation object
         */
        fun fromInt(rotation: Int): Rotation {
            when (rotation) {
                0 -> return NORMAL
                90 -> return ROTATION_90
                180 -> return ROTATION_180
                270 -> return ROTATION_270
                360 -> return NORMAL
                else -> throw IllegalStateException(
                        rotation.toString() + " is an unknown rotation. Needs to be either 0, 90, 180 or 270!")
            }
        }
    }
}
