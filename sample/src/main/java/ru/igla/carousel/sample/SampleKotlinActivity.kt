package ru.igla.carousel.sample

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import ru.github.igla.carousel.CircleRotateView
import ru.github.igla.carousel.CircleImage
import ru.github.igla.carousel.ScaleUpConfig

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class SampleKotlinActivity : AppCompatActivity() {

    private lateinit var circleRotateView: CircleRotateView

    private val gradient1 by lazy(LazyThreadSafetyMode.NONE) { createShader("#f54462", "#ffbbad") }
    private val gradient2 by lazy(LazyThreadSafetyMode.NONE) { createShader("#48c6ef", "#6f86d6") }
    private val gradient3 by lazy(LazyThreadSafetyMode.NONE) { createShader("#ff758c", "#ff7eb3") }
    private val gradient4 by lazy(LazyThreadSafetyMode.NONE) { createShader("#56ab2f", "#a8e063") }
    private val gradient5 by lazy(LazyThreadSafetyMode.NONE) { createShader("#33ccff", "#ff99cc") }

    private val clickListener = object : CircleRotateView.OnClickImageListener {
        override fun onClickImage(e: MotionEvent, image: CircleImage) {
            this@SampleKotlinActivity.toast("Click $image $e")
        }
    }

    private val clickCenterListener = object : CircleRotateView.OnClickCenterListener {
        override fun onClickCenter(e: MotionEvent) {
            this@SampleKotlinActivity.toast("Click CENTER $e")
        }
    }

    private fun createShader(colorFrom: String, colorTo: String): ShapeDrawable.ShaderFactory {
        val from = Color.parseColor(colorFrom)
        val to = Color.parseColor(colorTo)
        return object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader = LinearGradient(0f, 0f,
                    width.toFloat(), height.toFloat(),
                    from, to,
                    Shader.TileMode.CLAMP
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        circleRotateView = findViewById(R.id.carouselView)
        findViewById<TextView>(R.id.tvAction).setOnClickListener {
            startActivity(Intent(this@SampleKotlinActivity, SampleJavaActivity::class.java))
        }
        findViewById<TextView>(R.id.startCircle).setOnClickListener {
            circleRotateView.startAnimation()
        }
        findViewById<TextView>(R.id.stopCircle).setOnClickListener {
            circleRotateView.stopAnimation()
        }
        findViewById<TextView>(R.id.pauseCircle).setOnClickListener {
            circleRotateView.pauseAnimation()
        }
        findViewById<TextView>(R.id.resumeCircle).setOnClickListener {
            circleRotateView.resumeAnimation()
        }
        findViewById<SeekBar>(R.id.rotateSpeedSeekBar).apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    circleRotateView.apply {
                        rotateDuration = progress.toLong()
                        stopAnimation()
                        startAnimation()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }

        circleRotateView.apply {
            clickImageListener = this@SampleKotlinActivity.clickListener
            centerListener = this@SampleKotlinActivity.clickCenterListener
            scaleUpConfig = ScaleUpConfig(true, 800)
            images = listOf(
                    CircleImage(
                            R.drawable.ic_voice,
                            gradient = gradient1),
                    CircleImage(
                            R.drawable.ic_calendar,
                            gradient = gradient2),
                    CircleImage(
                            R.drawable.ic_key,
                            gradient = gradient3),
                    CircleImage(
                            R.drawable.ic_business_card,
                            gradient = gradient4),
                    CircleImage(
                            R.drawable.ic_cloud,
                            gradient = gradient5)
            )
            build()
            startAnimation()
        }
    }

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


