package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * Created by igor-lashkov on 11/01/2018.
 */
internal class WheelDrawable(private val context: Context) :
        Drawable() {

    private var stateController: StateController? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val drawableCallback = object : Callback {
        override fun invalidateDrawable(who: Drawable?) = invalidateSelf()

        override fun unscheduleDrawable(who: Drawable?, what: Runnable) {
        }

        override fun scheduleDrawable(who: Drawable?, what: Runnable, time: Long) {
        }
    }

    fun isCenterCoordinate(x: Float, y: Float): Boolean = stateController?.isCenterCoordinate(x, y)
            ?: false

    fun findCabinByPoint(x: Float, y: Float): CabinImage? = stateController?.getCabinByPoint(x, y)

    fun build(viewConfig: WheelViewConfig) {
        if (stateController == null) {
            stateController = StateController(drawableCallback, context, viewConfig, bounds)
        } else {
            stateController?.setData(viewConfig)
        }
    }

    fun getLocationCenter(point: PointF) {
        stateController?.getLocationCenter(point)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        stateController?.apply {
            val orientation = context.resources.configuration.orientation
            configure(bounds, orientation)
            invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        stateController?.drawWheel(canvas)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = if (paint.alpha < 255) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter = colorFilter
    }

    private fun throwExceptionIfNotBuild() {
        if (stateController == null) {
            throw IllegalStateException("View is not build up. Call method build()")
        }
    }

    fun startAnimation() {
        throwExceptionIfNotBuild()
        stateController?.startAnimation()
    }

    fun stopAnimation() {
        throwExceptionIfNotBuild()
        stateController?.stopAnimation()
    }

    fun resumeAnimation() {
        throwExceptionIfNotBuild()
        stateController?.resumeAnimation()
    }

    fun pauseAnimation() {
        throwExceptionIfNotBuild()
        stateController?.pauseAnimation()
    }
}


