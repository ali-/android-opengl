package af.ali.test.planetx

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Gravity
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GLRenderer : GLSurfaceView.Renderer {

	private val vPMatrix = FloatArray(16)
	private val projectionMatrix = FloatArray(16)
	private val viewMatrix = FloatArray(16)
	private val enemyRotateMatrix = FloatArray(16)
	private var enemyAngle = 0f
	private var starField = ArrayList<Star>()
	private lateinit var activity: MainActivity

	override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
		if (Global.player.geometry.size == 0) {
			Global.player.loadGeometry()
		}
		Global.loadEnemies()
		Global.enemies.forEach { enemy ->
			enemy.loadGeometry()
		}
		starField = Star.field()
		Global.loadProjectiles()
		Global.projectiles.forEach { projectile ->
			projectile.loadGeometry()
		}
	}

	override fun onDrawFrame(unused: GL10) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
		Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
		Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

		starField.forEach { star ->
			star.update()
			Matrix.multiplyMM(star.matrix, 0, projectionMatrix, 0, viewMatrix, 0)
			Matrix.translateM(star.matrix, 0, star.position.x, star.position.y, 0f)
			star.geometry.forEach { triangle ->
				triangle.color = star.color
				triangle.draw(star.matrix)
			}
		}
		Global.triangles = Star.fieldSize
		Global.enemies.forEach { enemy ->
			enemy.update()
			Matrix.multiplyMM(enemy.matrix, 0, projectionMatrix, 0, viewMatrix, 0)
			Matrix.translateM(enemy.matrix, 0, enemy.position.x, enemy.position.y, 0f)
			// Rotate enemy
			enemyAngle += 0.25f
			Matrix.setRotateM(enemyRotateMatrix, 0, enemyAngle, 0f, 0f, -1.0f)
			Matrix.multiplyMM(enemy.matrix, 0, enemy.matrix, 0, enemyRotateMatrix, 0)
			// -----
			enemy.geometry.forEach { triangle ->
				Global.triangles++
				triangle.draw(enemy.matrix)
			}
			if (Global.player.checkCollision(enemy) && !Global.paused) {
				enemy.destroy()
				Global.paused = true
			}
		}

		if (!Global.paused) {
			Global.projectiles.forEach { projectile ->
				if (!projectile.fired) {
					// TODO: Projectile should not rotate, instead calculate force based on angle of player
					projectile.loadGeometry()
					projectile.position = Vector2D(Global.player.position.x, Global.player.position.y + 0.001f)
					projectile.force = Vector2D(0f, 0.05f)
					projectile.fired = true
				}
				if (!projectile.destroyed) {
					Matrix.multiplyMM(projectile.matrix, 0, projectionMatrix, 0, viewMatrix, 0)
					Matrix.translateM(projectile.matrix, 0, projectile.position.x, projectile.position.y, 0f)
					projectile.geometry.forEach { triangle ->
						Global.triangles++
						triangle.draw(projectile.matrix)
					}
					projectile.update()
					// TODO: Check collision of projectiles with enemies
					Global.enemies.forEach { enemy ->
						if (projectile.checkCollision(enemy)) {
							Global.points++
							enemy.destroy()
							projectile.destroyed = true
						}
					}
				}
			}
			Global.player.geometry.forEach { t ->
				Global.triangles++
				Matrix.multiplyMM(Global.player.matrix, 0, projectionMatrix, 0, viewMatrix, 0)
				Matrix.translateM(Global.player.matrix, 0, Global.player.position.x, Global.player.position.y, 0f)
				t.draw(Global.player.matrix)
			}
		}

		if (Global.paused) { activity.updateText("YOU LOSE!") }
		else { activity.updateText("POINTS: ${Global.points}") }
		Global.ticks++
		logFPS()
	}

	override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
		GLES20.glViewport(0, 0, width, height)
		val ratio: Float = width.toFloat() / height.toFloat()
		Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
	}

	fun setActivity(a: MainActivity) {
		activity = a
	}

	var startTime = System.nanoTime()
	var frames = 0
	private fun logFPS() {
		frames++
		if (System.nanoTime() - startTime >= 1000000000) {
			Global.framerate = frames
			frames = 0
			startTime = System.nanoTime()
		}
	}

}