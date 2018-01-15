package ru.github.igla.carousel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.util.FloatProperty
import android.view.animation.OvershootInterpolator

/**
 * Created by igor-lashkov on 12/01/2018.
 */
class ScaleUpAnimation(private val scaleUpConfig: ScaleUpConfig) {

    interface OnAnimationEndListener {
        fun onAnimationEnd(animation: Animator)
    }

    var fraction = 0.0f
        private set

    var isRunning = false
        private set

    private var isCanceled = false

    private var appearAnimator: ValueAnimator? = null

    private fun createAppearAnimator(callback: Drawable.Callback, listener: OnAnimationEndListener, appearDuration: Long, from: Float, to: Float): ObjectAnimator {
        val floatProperty = object : FloatProperty<ScaleUpAnimation>("scaleUp") {
            override fun setValue(obj: ScaleUpAnimation, fraction: Float) {
                obj.fraction = fraction
                callback.invalidateDrawable(null)
            }

            override operator fun get(obj: ScaleUpAnimation) = obj.fraction
        }
        return ObjectAnimator.ofFloat(this, floatProperty, from, to).apply {
            duration = appearDuration
            interpolator = OvershootInterpolator()
            addListener(
                    object : AnimatorListenerAdapter() {
                        override fun onAnimationCancel(animation: Animator) {
                            this@ScaleUpAnimation.isCanceled = true
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            this@ScaleUpAnimation.isRunning = false
                            if (!isCanceled) {
                                listener.onAnimationEnd(animation)
                            }
                        }
                    })
        }
    }

    fun startAnimation(callback: Drawable.Callback, listener: OnAnimationEndListener) {
        if (isRunning) {
            return
        }
        stopAnimation()
        this.isCanceled = false
        if (scaleUpConfig.scaleUpDuration > 0) {
            appearAnimator = createAppearAnimator(
                    callback,
                    listener,
                    scaleUpConfig.scaleUpDuration.toLong(),
                    0f,
                    1f).apply {
                start()
                this@ScaleUpAnimation.isRunning = true
            }
        }
    }

    fun stopAnimation() {
        appearAnimator?.let {
            it.cancel()
            this.isCanceled = true
            this.isRunning = false
        }
    }

    fun shouldStartAnim(): Boolean = scaleUpConfig.showScaleUpAnimation
}