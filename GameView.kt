package af.ali.test.planetx

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.*
import java.util.*
import kotlin.collections.ArrayList


class Global: ViewModel() {

	companion object {
		var debug = false
		var enemies = ArrayList<Enemy>()
		var framerate = 0
		var fired = 0
		var lastFire = 0
		var paused = false
		var points = 0
		var projectiles = ArrayList<Projectile>()
		var ticks = 0
		var touching = false
		var triangles = 0
		var player = Player(Vector2D(0f, -0.5f))
		fun loadEnemies() {
			if (enemies.isEmpty()) {
				enemies.add(Enemy("hexagon", Vector2D(0.2f, 1f), Vector2D(0.005f, -0.005f)))
				enemies.add(Enemy("hexagon", Vector2D(-0.2f, 1f), Vector2D(-0.005f, -0.005f)))
				enemies.add(Enemy("hexagon", Vector2D(0.4f, 1f), Vector2D(0.005f, -0.005f)))
				enemies.add(Enemy("hexagon", Vector2D(-0.4f, 1f), Vector2D(-0.005f, -0.005f)))
				enemies.add(Enemy("diamond", Vector2D(0.2f, 1f), Vector2D(0f, -0.005f)))
				enemies.add(Enemy("diamond", Vector2D(-0.2f, 1f), Vector2D(0f, -0.005f)))
				enemies.add(Enemy("diamond", Vector2D(0.4f, 1f), Vector2D(0f, -0.005f)))
				enemies.add(Enemy("diamond", Vector2D(-0.4f, 1f), Vector2D(0f, -0.005f)))
				enemies.add(Enemy("triangle", Vector2D(0.2f, 0.25f), Vector2D(0.005f, 0f)))
				enemies.add(Enemy("triangle", Vector2D(-0.2f, -0.25f), Vector2D(-0.005f, 0f)))
				enemies.add(Enemy("triangle", Vector2D(0.4f, 0.25f), Vector2D(0.005f, 0f)))
				enemies.add(Enemy("triangle", Vector2D(-0.4f, -0.25f), Vector2D(-0.005f, 0f)))
			}
		}
		fun loadProjectiles() {
			if (projectiles.isEmpty()) {
				for (i in 1..10) { projectiles.add(Projectile()) }
			}
		}
	}

}


class GameView(context: Context, owner: ViewModelStoreOwner, var activityMain: MainActivity): GLSurfaceView(context) {

	private val renderer: GLRenderer
	private val TOUCH_SCALE_FACTOR: Float = (180.0f / 320f) * 0.75f
	private var previousX: Float = 0f
	private var previousY: Float = 0f
	private var viewModel: Global
	private var lastTouch: Int = 0

	init {
		setEGLContextClientVersion(2)
		renderer = GLRenderer()
		renderer.setActivity(activityMain)
		setRenderer(renderer)
		viewModel = ViewModelProvider(owner).get(Global::class.java)
	}

	override fun onTouchEvent(e: MotionEvent): Boolean {
		val x: Float = e.x
		val y: Float = e.y
		lastTouch = Global.ticks
		when (e.action) {
			MotionEvent.ACTION_MOVE -> {
				var dx: Float = x - previousX
				Global.player.position.x += dx * TOUCH_SCALE_FACTOR * 0.0025f
				requestRender()
				if (Global.player.position.x < -0.4f) { Global.player.position.x = -0.4f }
				else if (Global.player.position.x > 0.4f) { Global.player.position.x = 0.4f }
				if (Global.player.position.y < -0.75f) { Global.player.position.y = -0.75f }
				else if (Global.player.position.y > 0.75f) { Global.player.position.y = 0.75f }
			}
			MotionEvent.ACTION_DOWN -> {
				Global.touching = true
				// TODO: Double tap to fire
				if (Global.paused) {
					// TODO: Reset everything
				}
				else {
					if ((Global.ticks - Global.lastFire) > 5) {
						Global.lastFire = Global.ticks
						Global.projectiles[Global.fired].fired = false
						Global.projectiles[Global.fired].destroyed = false
						Global.projectiles[Global.fired].startTime = Global.ticks
						if (Global.fired < Global.projectiles.size-1) { Global.fired++ }
						else { Global.fired = 0 }
					}
				}
			}
			MotionEvent.ACTION_UP -> {
				Global.touching = false
			}
		}
		previousX = x
		previousY = y
		return true
	}

	fun setActivity(a: MainActivity) {
		activityMain = a
	}

}