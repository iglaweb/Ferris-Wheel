package ru.github.igla.wheel

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.FloatProperty
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator


/**
 * Created by igor-lashkov on 12/01/2018.
 */


internal const val CABIN_TILT_MIN = 1.0f
internal const val CABIN_TILT_MAX = 3.0f

internal class TiltAnimation(private val config: WheelViewConfig) {

    interface TiltValueChangeListener {
        fun onTiltChange(angle: Float)
    }

    private val DURATION_TILT_MIN = 400L
    private val DURATION_TILT_MAX = 700L

    private var animator: ValueAnimator? = null

    private val animInterpolator = LinearInterpolator()
    private val durationInterpolator = DecelerateInterpolator()

    fun startAnimation(listener: TiltValueChangeListener) {
        cancelAnimation()
        val property = object : FloatProperty<TiltValueChangeListener>("scaleUp") {
            override fun setValue(obj: TiltValueChangeListener, value: Float) {
                listener.onTiltChange(value)
            }

            override fun get(obj: TiltValueChangeListener) = 0f
        }

        val ratioSpeed = config.rotateSpeed.toFloat() / MAX_ROTATE_SPEED
        val calcAngle = CABIN_TILT_MIN + ratioSpeed * (CABIN_TILT_MAX - CABIN_TILT_MIN)


        val interpolatedRatioSpeed = durationInterpolator.getInterpolation(ratioSpeed)
        val ratioDuration = maxOf((1f - interpolatedRatioSpeed), 0f)
        val calcDuration = DURATION_TILT_MIN + (ratioDuration * (DURATION_TILT_MAX - DURATION_TILT_MIN)).toLong()

        animator = ObjectAnimator.ofFloat(listener, property, -calcAngle, calcAngle).apply {
            interpolator = animInterpolator
            duration = calcDuration
            currentPlayTime = calcDuration / 2
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    fun cancelAnimation() {
        animator?.let {
            it.removeAllListeners()
            it.cancel()
        }
    }
}