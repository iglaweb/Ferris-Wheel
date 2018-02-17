package ru.igla.carousel.sample

import android.content.Context
import android.content.Intent
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
import ru.github.igla.carousel.FerrisWheelView
import ru.github.igla.carousel.ScaleUpConfig

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class SampleKotlinActivity : AppCompatActivity() {

    private lateinit var behavior: BottomSheetBehavior<View>

    private val clickCenterListener = object : FerrisWheelView.OnClickCenterListener {
        override fun onClickCenter(e: MotionEvent) {
            toast("Click CENTER $e")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSettingsBottomSheet()
        tvAction.setOnClickListener {
            startActivity(Intent(this@SampleKotlinActivity, SampleJavaActivity::class.java))
        }
        startCircle.setOnClickListener {
            carouselView.startAnimation()
        }
        stopCircle.setOnClickListener {
            carouselView.stopAnimation()
        }
        pauseCircle.setOnClickListener {
            carouselView.pauseAnimation()
        }
        resumeCircle.setOnClickListener {
            carouselView.resumeAnimation()
        }
        rotateSpeedSeekBar.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    carouselView.apply {
                        stopAnimation()
                        rotateDegreeSpeedInSec = progress
                        startAnimation()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
        }
        seekbarNumberOfCabins.apply {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    carouselView.apply {
                        stopAnimation()
                        numberOfCabins = progress
                        build()
                        startAnimation()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
        }
        startCarousel()
    }

    private fun startCarousel() {
        carouselView.apply {
            centerListener = this@SampleKotlinActivity.clickCenterListener
            scaleUpConfig = ScaleUpConfig(true, 800)
            build()
            startAnimation()
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
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            image_toggle.setImageResource(R.drawable.ic_expand_less_black_24dp)
                        }
                        BottomSheetBehavior.STATE_EXPANDED ->
                            image_toggle.setImageResource(R.drawable.ic_expand_more_black_24dp)
                        else -> image_toggle.setImageResource(R.drawable.ic_expand_less_black_24dp)
                    }
                }

                override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                }
            })
        }
        view_options_header.setOnClickListener({
            behavior.state = if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
        })
    }

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


