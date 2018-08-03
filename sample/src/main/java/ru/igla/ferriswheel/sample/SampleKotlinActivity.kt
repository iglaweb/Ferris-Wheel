package ru.igla.ferriswheel.sample

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_settings_attributes.*
import ru.github.igla.ferriswheel.CabinStyle
import ru.github.igla.ferriswheel.FerrisWheelView

/**
 * Created by igor-lashkov on 11/01/2018.
 */

class SampleKotlinActivity : AppCompatActivity() {

    private lateinit var behavior: BottomSheetBehavior<View>

    private val clickCenterListener = object : FerrisWheelView.OnClickCenterListener {
        override fun onClickCenter(e: MotionEvent) {
            Toast.makeText(this@SampleKotlinActivity, "Click center $e", Toast.LENGTH_LONG).show()
        }
    }

    private val clickCabinListener = object : FerrisWheelView.OnClickCabinListener {
        override fun onClickCabin(cabinNumber: Int, e: MotionEvent) {
            Toast.makeText(this@SampleKotlinActivity, "Click cabin $cabinNumber", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSettingsBottomSheet()
        ferrisWheelView.apply {
            centerListener = this@SampleKotlinActivity.clickCenterListener
            cabinListener = this@SampleKotlinActivity.clickCabinListener
            cabinColors = arrayOf("#6eabdf", "#ffb140", "#ce4d5b", "#96bd58", "#ed7a50").map { color ->
                CabinStyle(Color.parseColor(color), Color.BLACK)
            }
        }
        tvAction.setOnClickListener {
            startActivity(Intent(this, SampleJavaActivity::class.java))
        }
        startCircle.setOnClickListener {
            ferrisWheelView.startAnimation()
        }
        stopCircle.setOnClickListener {
            ferrisWheelView.stopAnimation()
        }
        pauseCircle.setOnClickListener {
            ferrisWheelView.pauseAnimation()
        }
        resumeCircle.setOnClickListener {
            ferrisWheelView.resumeAnimation()
        }
        rotateSpeedSeekBar.apply {
            setOnSeekBarChangeListener(object : SeekbarProgressListener() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    ferrisWheelView.apply {
                        pauseAnimation()
                        rotateDegreeSpeedInSec = progress
                        resumeAnimation()
                    }
                }
            })
        }
        seekbarNumberOfCabins.apply {
            setOnSeekBarChangeListener(object : SeekbarProgressListener() {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    ferrisWheelView.apply {
                        pauseAnimation()
                        numberOfCabins = progress
                        resumeAnimation()
                    }
                }
            })
        }
        ferrisWheelView.startAnimation()
    }

    open class SeekbarProgressListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onBackPressed() {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    private fun initSettingsBottomSheet() {
        behavior = BottomSheetBehavior.from<View>(bottomSheet).apply {
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                    val res = when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> R.drawable.ic_expand_more_black_24dp
                        else -> R.drawable.ic_expand_less_black_24dp
                    }
                    image_toggle.setImageResource(res)
                }

                override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                }
            })
        }
        view_options_header.setOnClickListener {
            behavior.state = if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }
}