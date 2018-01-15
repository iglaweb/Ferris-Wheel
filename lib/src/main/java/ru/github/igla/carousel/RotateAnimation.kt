package ru.github.igla.carousel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.util.FloatProperty

/**
 * Created by igor-lashkov on 13/01/2018.
 */
class RotateAnimation(val viewConfig: CircleViewConfig) {

    private var currentPlayTime = 0L

    var carouselAngle = 0f
        private set

    var isRunning = false
        private set

    private var animator: ValueAnimator? = null

    var wasStarted = false
        private set

    fun startCarouselAnimation(callback: Drawable.Callback) {
        stopAnimation()
        if (viewConfig.rotateDuration > 0) {
            animator = createCircleAnimator(
                    callback,
                    viewConfig.rotateDuration,
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
            it.cancel()
            this.isRunning = false
        }
    }

    private fun createCircleAnimator(callback: Drawable.Callback, rotateDuration: Long, from: Float, to: Float): ObjectAnimator {
        val floatProperty = object : FloatProperty<RotateAnimation>("angle") {
            override fun setValue(obj: RotateAnimation, carouselAngle: Float) {
                if (viewConfig.isRotating) {
                    obj.carouselAngle = carouselAngle
                }
                callback.invalidateDrawable(null)
            }

            override operator fun get(obj: RotateAnimation) = obj.carouselAngle
        }
        return ObjectAnimator.ofFloat(this, floatProperty, from, to).apply {
            currentPlayTime = viewConfig.getDurationOffset
            duration = rotateDuration
            interpolator = viewConfig.interpolator
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

    fun pauseCarouselAnimation() {
        animator?.let {
            if (it.isRunning) {
                currentPlayTime = it.currentPlayTime
                it.cancel()
                this.isRunning = false
            }
        }
    }

    fun resumeCarouselAnimation() {
        animator?.let {
            if (!it.isRunning) {
                it.start()
                it.currentPlayTime = currentPlayTime
                this.isRunning = true
            }
        }
    }
}