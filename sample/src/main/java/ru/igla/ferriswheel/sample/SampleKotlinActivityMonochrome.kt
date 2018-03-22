package ru.igla.ferriswheel.sample

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_monochrome.*
import ru.github.igla.ferriswheel.CoreStyle

/**
 * Created by igor-lashkov on 11/01/2018.
 */

class SampleKotlinActivityMonochrome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_monochrome)
        ferrisWheelView.apply {
            coreStyle = CoreStyle(Color.WHITE, Color.WHITE)
            startAnimation()
        }
    }
}