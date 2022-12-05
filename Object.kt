package af.ali.test.planetx


import android.opengl.Matrix
import java.lang.Math.cos
import java.lang.Math.sin
import kotlin.collections.ArrayList
import kotlin.random.Random


open class Object(var id: String, var position: Vector2D) {

	var geometry = ArrayList<Triangle>()
	var matrix = FloatArray(16)

	fun checkCollision(other: Object): Boolean {
		val a = Box(position)
		val b = Box(other.position)
		if (a.x < b.x + b.s && a.x + a.s > b.x && a.y < b.y + b.s && a.s + a.y > b.y) { return true }
		return false
	}

	open fun update() {}

	open fun loadGeometry() {
		Matrix.translateM(matrix, 0, position.x, position.y, 0f)
		if (Global.debug) {
			val size = 0.03f
			val triangleLeft: Triangle = Triangle(floatArrayOf(
				-size, size, 0f,
				-size, -size, 0f,
				size, size, 0f
			))
			val triangleRight: Triangle = Triangle(floatArrayOf(
				size, size, 0f,
				-size, -size, 0f,
				size, -size, 0f
			))
			triangleLeft.color = floatArrayOf(0f, 0.7f, 0f, 0.5f)
			triangleRight.color = triangleLeft.color
			geometry.add(triangleLeft)
			geometry.add(triangleRight)
		}
	}

}

class Player(position: Vector2D): Object("player", position) {

	var cooldown: Int = 0
	var vulnerable: Boolean = true
	var weapon: Int = 0

	companion object {
		val gunPosition = 0.02f
		val gunWidth = 0.01f
	}

	override fun loadGeometry() {
		super.loadGeometry()
		val ship: Triangle = Triangle(floatArrayOf(
			(0.0f), (0.0625f), 0.0f,
			(-0.055f), (-0.025f), 0.0f,
			(0.055f), (-0.025f), 0.0f
		))
		val mask: Triangle = Triangle(floatArrayOf(
			(0.0f), (0.015f), 0.0f,
			(-0.03f), (-0.035f), 0.0f,
			(0.03f), (-0.035f), 0.0f
		))
		val gunLeftA: Triangle = Triangle(floatArrayOf(
			(-gunPosition), (0.045f), 0.0f,
			(-gunPosition), (-0.015f), 0.0f,
			(-gunPosition-gunWidth), (0.045f), 0.0f
		))
		val gunLeftB: Triangle = Triangle(floatArrayOf(
			(-gunPosition-gunWidth), (0.045f), 0.0f,
			(-gunPosition), (-0.015f), 0.0f,
			(-gunPosition-gunWidth), (-0.015f), 0.0f
		))
		val gunRightA: Triangle = Triangle(floatArrayOf(
			(gunPosition), (0.045f), 0.0f,
			(gunPosition), (-0.015f), 0.0f,
			(gunPosition+gunWidth), (0.045f), 0.0f
		))
		val gunRightB: Triangle = Triangle(floatArrayOf(
			(gunPosition+gunWidth), (0.045f), 0.0f,
			(gunPosition), (-0.015f), 0.0f,
			(gunPosition+gunWidth), (-0.015f), 0.0f
		))
		ship.color = floatArrayOf(0.3f, 0.6f, 0.9f, 1.0f)
		mask.color = floatArrayOf(0.8f, 0.9f, 1.0f, 1.0f)
		gunLeftA.color = floatArrayOf(0.3f, 0.4f, 0.5f, 1.0f)
		gunLeftB.color = gunLeftA.color
		gunRightA.color = gunLeftA.color
		gunRightB.color = gunLeftA.color
		geometry.add(gunLeftA)
		geometry.add(gunLeftB)
		geometry.add(gunRightA)
		geometry.add(gunRightB)
		geometry.add(ship)
		geometry.add(mask)
	}

}

class Enemy(id: String, position: Vector2D, var force: Vector2D): Object(id, position) {

	constructor(id: String) : this(id, position = Vector2D(0f, 0f), force = Vector2D(0f, 0f))

	fun destroy() {
		// Randomize enemy location
		// Increase speed with points
		when (id) {
			"diamond" -> {
				val sign = arrayOf(-1f, 1f)
				val randomInt = (0..sign.size-1).random()
				position = Vector2D(sign[randomInt] * (Random.nextFloat()/2f), 0.5f+Random.nextFloat())
			}
			"hexagon" -> {
				val sign = arrayOf(-1f, 1f)
				val randomInt = (0..sign.size-1).random()
				force = Vector2D(-force.x, force.y)
				position = Vector2D(sign[randomInt] * Random.nextFloat(), 1.5f)
			}
			"triangle" -> {
				val positions = arrayOf(-0.75f, 0.75f)
				val randomInt = (0..positions.size-1).random()
				force = Vector2D(-force.x, 0f)
				position = Vector2D(positions[randomInt], -0.35f+Random.nextFloat())
			}
		}
		update()
	}

	override fun loadGeometry() {
		super.loadGeometry()
		when (id) {
			"diamond" -> {
				val size = 0.04f
				val diamondBottom: Triangle = Triangle(floatArrayOf(
					0f, -size, 0f,
					-size, 0f, 0f,
					size, 0f, 0f
				))
				val diamondTop: Triangle = Triangle(floatArrayOf(
					0f, size, 0f,
					-size, 0f, 0f,
					size, 0f, 0f
				))
				diamondBottom.color = floatArrayOf(0.8f, 0.25f, 0.2f, 1.0f)
				diamondTop.color = diamondBottom.color
				geometry.add(diamondBottom)
				geometry.add(diamondTop)
			}
			"hexagon" -> {
				val size = 0.04f
				val points = arrayOf<Vector2D>( Vector2D(-size/2f, size*0.9f),
												Vector2D(size/2f, size*0.9f),
												Vector2D(size, 0f),
												Vector2D(size/2f, -size*0.9f),
												Vector2D(-size/2f, -size*0.9f),
												Vector2D(-size, 0f),
												Vector2D(-size/2f, size*0.9f))
				for (i in 0..(points.size-2)) {
					val triangle: Triangle = Triangle(floatArrayOf(
						0f, 0f, 0f,
						points[i].x, points[i].y, 0f,
						points[i+1].x, points[i+1].y, 0f
					))
					triangle.color = floatArrayOf(0.5f, 0.3f, 0.65f, 1.0f)
					geometry.add(triangle)
				}
			}
			"triangle" -> {
				val size = 0.04f
				val triangle: Triangle = Triangle(floatArrayOf(
					0f, size*0.8f, 0f,
					-size, -size, 0f,
					size, -size, 0f
				))
				triangle.color = floatArrayOf(0.9f, 0.8f, 0.3f, 1.0f)
				geometry.add(triangle)
			}
		}
	}

	override fun update() {
		if (position.x > 0.55f) { position.x = -0.55f }
		else if (position.x < -0.55f) { position.x = 0.55f }
		if (position.y > 1.05f) { position.y = -1.05f }
		else if (position.y < -1.05f) { position.y = 1.05f }
		position.x += force.x
		position.y += force.y
		super.update()
	}

}

class Projectile(position: Vector2D): Object("projectile", position) {

	constructor() : this(position = Vector2D(10f, 10f))

	var destroyed = false
	var fired = false
	var force = Vector2D(0f, 0.0075f)
	var startTime = 0

	override fun loadGeometry() {
		super.loadGeometry()
		val triangleLeft: Triangle = Triangle(floatArrayOf(
			-Player.gunPosition-(Player.gunWidth/2), 0.065f, 0f,
			-Player.gunPosition, 0.045f, 0f,
			-Player.gunPosition-Player.gunWidth, 0.045f, 0f
		))
		val triangleRight: Triangle = Triangle(floatArrayOf(
			Player.gunPosition+(Player.gunWidth/2), 0.065f, 0f,
			Player.gunPosition, 0.045f, 0f,
			Player.gunPosition+Player.gunWidth, 0.045f, 0f
		))
		triangleLeft.color = floatArrayOf(0.4f, 0.7f, 1.0f, 1.0f)
		triangleRight.color = triangleLeft.color
		geometry.add(triangleLeft)
		geometry.add(triangleRight)
	}

	override fun update() {
		val delta = Global.ticks - startTime
		if (delta >= 30) { destroyed = true }
		if (position.x > 0.55f || position.x < -0.55f) { destroyed = true }
		else if (position.y > 1.05f || position.y < -1.05f) { destroyed = true }
		position.y += force.y
		super.update()
	}

}

class Star(position: Vector2D, var force: Vector2D): Object(id = "StarField", position) {

	var offset = Random.nextDouble(0.9).toFloat()
	var color = floatArrayOf(1.0f-offset, 1.0f-offset, 1.0f-offset, 1.0f)
	var flickerUp = false

	companion object {
		val fieldSize = 200
		fun field(): ArrayList<Star> {
			val starField = ArrayList<Star>()
			for (i in 0..fieldSize) {
				val randomX = Random.nextDouble(-0.0005, 0.0005).toFloat()
				val randomY = Random.nextDouble(-0.0005, 0.0005).toFloat()
				val star = Star(
					Vector2D(
						Random.nextDouble(-0.5, 0.5).toFloat(),
						Random.nextDouble(-1.0, 1.0).toFloat()
					), Vector2D(randomX, randomY)
				)
				star.loadGeometry()
				starField.add(star)
			}
			return starField
		}
	}

	override fun loadGeometry() {
		super.loadGeometry()
		val size = 0.004f
		val triangle: Triangle = Triangle(floatArrayOf(
			0f, 0f, 0f,
			-size, -size, 0f,
			size, -size, 0f
		))
		triangle.color = color
		geometry.add(triangle)
		offset = 0.0075f
	}

	override fun update() {
		// Loop at boundaries
		if (position.x > 0.55f) { position.x = -0.55f }
		else if (position.x < -0.55f) { position.x = 0.55f }
		if (position.y > 1.05f) { position.y = -1.05f }
		else if (position.y < -1.05f) { position.y = 1.05f }
		position.x += force.x
		position.y += force.y
		// Flicker color
		if (color[0] < 1f && flickerUp) { color = floatArrayOf(color[0]+offset, color[1]+offset, color[2]+offset, 1.0f) }
		else if (color[0] > 1f && flickerUp) { flickerUp = false }
		else if (color[0] < 0.2f && !flickerUp) { flickerUp = true }
		else { color = floatArrayOf(color[0]-offset, color[1]-offset, color[2]-offset, 1.0f) }
		super.update()
	}

}

class Box(position: Vector2D) {
	var s: Float = 0.1f
	var x: Float = position.x - (s/2)
	var y: Float = position.y - (s/2)
}

class Vector2D(x: Float, y: Float) {
	var x: Float = x
	var y: Float = y
	companion object { fun zero(): Vector2D = Vector2D(0f, 0f) }
}