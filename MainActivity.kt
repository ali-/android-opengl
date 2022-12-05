package af.ali.test.planetx


import android.graphics.Color
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider


class MainActivity: AppCompatActivity() {

	private lateinit var gameView: GLSurfaceView
	private lateinit var textView: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		gameView = GameView(applicationContext, this, this)
		setContentView(gameView)
		textView = TextView(this)
		textView.setTextColor(Color.WHITE)
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f);
		//textView.setBackgroundColor(Color.BLACK)
		if (!Global.debug) { textView.gravity = Gravity.CENTER_HORIZONTAL }
		val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
		layoutParams.setMargins(20, 20, 20, 20)
		addContentView(textView, layoutParams)
	}

	fun updateText(text: String) {
		textView.text = text
	}

	override fun onResume() {
		super.onResume()
		if (gameView != null) { gameView.onResume() }
	}

	override fun onPause() {
		super.onPause()
		if (gameView != null) { gameView.onPause() }
	}

}