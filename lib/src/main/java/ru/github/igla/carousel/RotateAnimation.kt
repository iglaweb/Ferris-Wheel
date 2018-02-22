package ru.github.igla.carousel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.FloatProperty
import android.view.animation.LinearInterpolator

/**
 * Created by igor-lashkov on 13/01/2018.
 */
internal class RotateAnimation(private val viewConfig: WheelViewContext) {

    interface OnRotateAngleValueChangeListener {
        fun onChangeRotateAngle(angle: Float)
    }

    private var currentPlayTime = 0L

    var isRunning = false
        private set

    private var animator: ValueAnimator? = null

    var wasStarted = false
        private set

    private val animInterpolator = LinearInterpolator()

    fun startAnimation(listener: OnRotateAngleValueChangeListener) {
        stopAnimation()
        val rotateSpeed = viewConfig.rotateSpeed
        if (rotateSpeed > 0) {
            animator = createCircleAnimator(
                    listener,
                    rotateSpeed,
                    viewConfig.getAngleFrom,
                    viewConfig.getAngleTo).apply {
                start()
                wasStarted = true
                this@RotateAnimation.isRunning = true
            }
        }
    }

    fun stopAnimation() {
        animator?.let {
            it.removeAllListeners()
            it.cancel()
            this.isRunning = false
        }
    }

    private fun createCircleAnimator(listener: OnRotateAngleValueChangeListener, rotateSpeed: Int, from: Float, to: Float): ObjectAnimator {
        val floatProperty = object : FloatProperty<RotateAnimation>("angle") {
            override fun setValue(obj: RotateAnimation, carouselAngle: Float) {
                listener.onChangeRotateAngle(carouselAngle)
            }

            override operator fun get(obj: RotateAnimation): Float = 0f
        }
        return ObjectAnimator.ofFloat(this, floatProperty, from, to).apply {
            currentPlayTime = viewConfig.getDurationOffset
            duration = getDurationFromSpeed(rotateSpeed)
            interpolator = animInterpolator
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationCancel(animation: Animator) = onAnimationEnd(animation)
                        override fun onAnimationEnd(animation: Animator) {
                            this@RotateAnimation.isRunning = false
                        }
                    })
        }
    }

    fun pauseAnimation() {
        animator?.let {
            if (it.isRunning) {
                currentPlayTime = it.currentPlayTime
                it.cancel()
                this.isRunning = false
            }
        }
    }

    fun resumeAnimation() {
        animator?.let {
            if (!it.isRunning) {
                it.start()
                it.currentPlayTime = currentPlayTime
                this.isRunning = true
            }
        }
    }
}