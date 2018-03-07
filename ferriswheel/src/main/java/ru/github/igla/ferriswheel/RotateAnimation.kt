package ru.github.igla.ferriswheel

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.FloatProperty
import android.view.animation.LinearInterpolator

/**
 * Created by igor-lashkov on 13/01/2018.
 */
internal class RotateAnimation(private val viewConfig: WheelViewConfig) {

    interface OnRotateAngleValueChangeListener {
        fun onChangeRotateAngle(angle: Float)
    }

    private var lastChangeAngle: Float = viewConfig.startAngle

    private var animator: ValueAnimator? = null

    private val animInterpolator = LinearInterpolator()

    var isRunning = animator?.isRunning ?: false
        private set

    fun startAnimation(listener: OnRotateAngleValueChangeListener) {
        stopAnimation()
        val rotateSpeed = viewConfig.rotateSpeed
        if (rotateSpeed > 0) {
            animator = createAnimator(
                    listener,
                    rotateSpeed,
                    viewConfig.getAngleFrom,
                    viewConfig.getAngleTo).apply {
                start()
            }
        }
    }

    fun stopAnimation() {
        animator?.let {
            it.removeAllListeners()
            it.cancel()
            this.lastChangeAngle = 0f
        }
    }

    private fun createAnimator(listener: OnRotateAngleValueChangeListener, rotateSpeed: Int, from: Float, to: Float): ObjectAnimator {
        val floatProperty = object : FloatProperty<RotateAnimation>("angle") {
            override fun setValue(obj: RotateAnimation, angle: Float) {
                lastChangeAngle = angle
                listener.onChangeRotateAngle(angle)
            }

            override operator fun get(obj: RotateAnimation): Float = 0f
        }
        return ObjectAnimator.ofFloat(this, floatProperty, from, to).apply {
            currentPlayTime = viewConfig.getDurationOffset(lastChangeAngle)
            duration = getDurationFromSpeed(rotateSpeed)
            interpolator = animInterpolator
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
    }

    fun pauseAnimation() {
        animator?.let {
            if (it.isRunning) {
                it.cancel()
            }
        }
    }

    fun resumeAnimation() {
        animator?.apply {
            if (!isRunning) {
                duration = getDurationFromSpeed(viewConfig.rotateSpeed)
                currentPlayTime = viewConfig.getDurationOffset(lastChangeAngle)
                start()
            }
        }
    }
}