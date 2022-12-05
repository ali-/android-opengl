package af.ali.test.planetx

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

const val COORDS_PER_VERTEX = 3

class Triangle(var coordinates: FloatArray) {

	var color = floatArrayOf(0.3f, 0.6f, 0.9f, 1.0f)
	private var positionHandle: Int = 0
	private var mColorHandle: Int = 0
	private val vertexCount: Int = coordinates.size / COORDS_PER_VERTEX
	private val vertexStride: Int = COORDS_PER_VERTEX * 4
	private var vPMatrixHandle: Int = 0
	private val vertexShaderCode =
		"uniform mat4 uMVPMatrix;" +
		"attribute vec4 vPosition;" +
		"void main() {" +
		"  gl_Position = uMVPMatrix * vPosition;" +
		"}"
	private val fragmentShaderCode =
		"precision mediump float;" +
		"uniform vec4 vColor;" +
		"void main() {" +
		"  gl_FragColor = vColor;" +
		"}"
	private var vertexBuffer: FloatBuffer =
		ByteBuffer.allocateDirect(coordinates.size * 4).run {
			order(ByteOrder.nativeOrder())
			asFloatBuffer().apply {
				put(coordinates)
				position(0)
			}
		}

	private var mProgram: Int

	init {
		val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
		val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
		mProgram = GLES20.glCreateProgram().also {
			GLES20.glAttachShader(it, vertexShader)
			GLES20.glAttachShader(it, fragmentShader)
			GLES20.glLinkProgram(it)
		}
	}

	fun draw(mvpMatrix: FloatArray) {
		GLES20.glUseProgram(mProgram)
		vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
		GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0)
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
		GLES20.glDisableVertexAttribArray(positionHandle)
		positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
			GLES20.glEnableVertexAttribArray(it)
			GLES20.glVertexAttribPointer(
				it,
				COORDS_PER_VERTEX,
				GLES20.GL_FLOAT,
				false,
				vertexStride,
				vertexBuffer
			)
			mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->
				GLES20.glUniform4fv(colorHandle, 1, color, 0)
			}
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
			GLES20.glDisableVertexAttribArray(it)
		}
	}

	fun loadShader(type: Int, shaderCode: String): Int {
		return GLES20.glCreateShader(type).also { shader ->
			GLES20.glShaderSource(shader, shaderCode)
			GLES20.glCompileShader(shader)
		}
	}

}