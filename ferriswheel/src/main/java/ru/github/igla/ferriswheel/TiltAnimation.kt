package ru.github.igla.ferriswheel

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

private const val DURATION_TILT_MIN = 400L
private const val DURATION_TILT_MAX = 800L

internal class TiltAnimation {

    interface TiltValueChangeListener {
        fun onTiltChange(angle: Float)
    }

    private var animator: ValueAnimator? = null

    private val animInterpolator = LinearInterpolator()
    private val durationInterpolator = DecelerateInterpolator()

    fun startAnimation(rotateSpeed: Int, listener: TiltValueChangeListener) {
        cancelAnimation()
        createAnimator(listener, rotateSpeed.toFloat()).apply {
            start()
        }
    }

    private fun createAnimator(listener: TiltValueChangeListener, rotateSpeed: Float): ObjectAnimator {
        val property = object : FloatProperty<TiltValueChangeListener>("angle") {
            override fun setValue(obj: TiltValueChangeListener, value: Float) {
                listener.onTiltChange(value)
            }

            override fun get(obj: TiltValueChangeListener) = 0f
        }

        val ratioSpeed = rotateSpeed / MAX_ROTATE_SPEED
        val calcAngle = CABIN_TILT_MIN + ratioSpeed * (CABIN_TILT_MAX - CABIN_TILT_MIN)

        val interpolatedRatioSpeed = durationInterpolator.getInterpolation(ratioSpeed)
        val ratioDuration = maxOf((1f - interpolatedRatioSpeed), 0f)
        val calcDuration = DURATION_TILT_MIN + (ratioDuration * (DURATION_TILT_MAX - DURATION_TILT_MIN)).toLong()

        return ObjectAnimator.ofFloat(listener, property, -calcAngle, calcAngle).apply {
            interpolator = animInterpolator
            duration = calcDuration
            currentPlayTime = calcDuration / 2
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }
    }

    fun cancelAnimation() {
        animator?.let {
            it.removeAllListeners()
            it.cancel()
        }
    }
}