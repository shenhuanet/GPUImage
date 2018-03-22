package com.shenhua.libs.gpuimage.filters

import android.opengl.GLES20
import android.opengl.Matrix

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by shenhua on 2018-03-16-0016.
 *
 * @author shenhua
 * Email shenhuanet@126.com
 */
class GPUImageTransformFilter : GPUImageFilter(TRANSFORM_VERTEX_SHADER, GPUImageFilter.Companion.NO_FILTER_FRAGMENT_SHADER) {

    private var transformMatrixUniform: Int = 0
    private var orthographicMatrixUniform: Int = 0
    private val orthographicMatrix: FloatArray

    @JvmField
    var transform3D: FloatArray? = null

    // This applies the transform to the raw frame data if set to YES, the default of NO takes the aspect ratio of the image input into account when rotating
    private var ignoreAspectRatio: Boolean = false

    // sets the anchor point to top left corner
    private var anchorTopLeft: Boolean = false

    init {

        orthographicMatrix = FloatArray(16)
        Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f)

        transform3D = FloatArray(16)
        Matrix.setIdentityM(transform3D, 0)
    }

    override fun onInit() {
        super.onInit()
        transformMatrixUniform = GLES20.glGetUniformLocation(program, "transformMatrix")
        orthographicMatrixUniform = GLES20.glGetUniformLocation(program, "orthographicMatrix")

        setUniformMatrix4f(transformMatrixUniform, transform3D!!)
        setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix)
    }

    override fun onInitialized() {
        super.onInitialized()
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)

        if (!ignoreAspectRatio) {
            Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f * height.toFloat() / width.toFloat(), 1.0f * height.toFloat() / width.toFloat(), -1.0f, 1.0f)
            setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix)
        }
    }

    override fun onDraw(textureId: Int, cubeBuffer: FloatBuffer,
                        textureBuffer: FloatBuffer) {

        var vertBuffer = cubeBuffer

        if (!ignoreAspectRatio) {

            val adjustedVertices = FloatArray(8)

            cubeBuffer.position(0)
            cubeBuffer.get(adjustedVertices)

            val normalizedHeight = outputHeight.toFloat() / outputWidth.toFloat()
            adjustedVertices[1] *= normalizedHeight
            adjustedVertices[3] *= normalizedHeight
            adjustedVertices[5] *= normalizedHeight
            adjustedVertices[7] *= normalizedHeight

            vertBuffer = ByteBuffer.allocateDirect(adjustedVertices.size * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()

            vertBuffer.put(adjustedVertices).position(0)
        }

        super.onDraw(textureId, vertBuffer, textureBuffer)
    }

    fun setTransform3D(transform3D: FloatArray) {
        this.transform3D = transform3D
        setUniformMatrix4f(transformMatrixUniform, transform3D)
    }

    fun getTransform3D(): FloatArray? {
        return transform3D
    }

    fun setIgnoreAspectRatio(ignoreAspectRatio: Boolean) {
        this.ignoreAspectRatio = ignoreAspectRatio

        if (ignoreAspectRatio) {
            Matrix.orthoM(orthographicMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f)
            setUniformMatrix4f(orthographicMatrixUniform, orthographicMatrix)
        } else {
            onOutputSizeChanged(outputWidth, outputHeight)
        }
    }

    fun ignoreAspectRatio(): Boolean {
        return ignoreAspectRatio
    }

    fun setAnchorTopLeft(anchorTopLeft: Boolean) {
        this.anchorTopLeft = anchorTopLeft
        setIgnoreAspectRatio(ignoreAspectRatio)
    }

    fun anchorTopLeft(): Boolean {
        return anchorTopLeft
    }

    companion object {
        val TRANSFORM_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                " attribute vec4 inputTextureCoordinate;\n" +
                " \n" +
                " uniform mat4 transformMatrix;\n" +
                " uniform mat4 orthographicMatrix;\n" +
                " \n" +
                " varying vec2 textureCoordinate;\n" +
                " \n" +
                " void main()\n" +
                " {\n" +
                "     gl_Position = transformMatrix * vec4(position.xyz, 1.0) * orthographicMatrix;\n" +
                "     textureCoordinate = inputTextureCoordinate.xy;\n" +
                " }"
    }
}
