package ru.github.igla.carousel

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.FloatProperty
import android.view.animation.LinearInterpolator


/**
 * Created by igor-lashkov on 12/01/2018.
 */

internal const val CABIN_TILT = 3.0f

internal class TiltAnimation {

    interface TiltValueChangeListener {
        fun onTiltChange(angle: Float)
    }

    private val DURATION_TILT = 600L

    private var animator: ValueAnimator? = null

    private val animInterpolator = LinearInterpolator()

    fun startAnimation(listener: TiltValueChangeListener) {
        cancelAnimation()
        val property = object : FloatProperty<TiltValueChangeListener>("scaleUp") {
            override fun setValue(obj: TiltValueChangeListener, value: Float) {
                listener.onTiltChange(value)
            }

            override fun get(obj: TiltValueChangeListener) = 0f
        }
        animator = ObjectAnimator.ofFloat(listener, property, -CABIN_TILT, CABIN_TILT).apply {
            interpolator = animInterpolator
            duration = DURATION_TILT
            currentPlayTime = DURATION_TILT / 2
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