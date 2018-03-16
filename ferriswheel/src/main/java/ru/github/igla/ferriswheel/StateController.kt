package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.pow


/**
 * Created by igor-lashkov on 11/01/2018.
 */

internal class StateController(
        private val callback: Drawable.Callback,
        private val context: Context,
        private var viewConfig: WheelViewConfig,
        bounds: Rect) {

    private var lastBounds: Rect = bounds
    private var cabinImages: List<CabinImage>
    private val wheelBaseDrawer by lazyNonSafe { WheelBaseDrawer(context, viewConfig) }
    private val tiltAnimation by lazyNonSafe { TiltAnimation() }
    private val rotateAnimation by lazyNonSafe { RotateAnimation(viewConfig) }

    private var orientation = context.resources.configuration.orientation

    init {
        cabinImages = createListOfCabins(viewConfig)
        configure(bounds, orientation)
    }

    fun setData(viewConfig: WheelViewConfig) {
        cabinImages = createListOfCabins(viewConfig)
        configure(lastBounds, orientation)
    }

    private fun createListOfCabins(viewConfig: WheelViewConfig): List<CabinImage> {
        if (viewConfig.cabinsNumber == 0) {
            return emptyList()
        }
        val imgCount = viewConfig.cabinsNumber
        val rad = 360.0 / imgCount
        var offsetAngle = 0.0
        return List(imgCount) { number ->
            val cabinColor = viewConfig.cabinColors[number % viewConfig.cabinColors.size]
            offsetAngle += rad
            CabinImage(context, number, offsetAngle, cabinColor)
        }
    }

    fun configure(bounds: Rect, orientation: Int) {
        pauseAnimation()
        configureSizes(bounds)
        if (orientation != this.orientation) {
            resetImagesState()
        }
        this.orientation = orientation
        this.lastBounds = bounds
        resumeAnimation()
    }

    private fun configureSizes(bounds: Rect) {
        if (bounds.isEmpty) {
            return
        }
        wheelBaseDrawer.configure(bounds)
        resetImagesState()
    }

    private fun CabinImage.getAngleOffset(): Double = getAngleOffset(wheelBaseDrawer.rotateAngle)

    private fun resetImagesState() {
        cabinImages.forEachNoIterator { item ->
            val offsetAngle = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.wheelPos, offsetAngle)
        }
    }

    fun getLocationCenter(point: PointF) {
        point.set(wheelBaseDrawer.centerPoint)
    }

    private fun isPointInsideRadius(x: Float, y: Float, centerPoint: PointF, radius: Float): Boolean =
            (x - centerPoint.x).pow(2f) +
                    (y - centerPoint.y).pow(2f) <= radius * radius

    fun isCenterCoordinate(x: Float, y: Float): Boolean = isPointInsideRadius(
            x, y, wheelBaseDrawer.centerPoint, (wheelBaseDrawer.radius / 2.0).toFloat())

    fun drawWheel(canvas: Canvas) {
        wheelBaseDrawer.onPreDraw(canvas)
        cabinImages.forEachNoIterator { item ->
            val offsetAngle = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.wheelPos, offsetAngle)
            item.drawCabin(
                    canvas,
                    item.wheelPos,
                    wheelBaseDrawer.cabinSize,
                    wheelBaseDrawer.ratioCabinSize
            )
        }
        wheelBaseDrawer.onPostDraw(canvas)
    }


    private val rotateListener = object : RotateAnimation.OnRotateAngleValueChangeListener {
        override fun onChangeRotateAngle(angle: Float) {
            wheelBaseDrawer.rotateAngle = angle
            callback.invalidateDrawable(null)
        }
    }

    private val tiltListener = object : TiltAnimation.TiltValueChangeListener {
        override fun onTiltChange(angle: Float) {
            cabinImages.forEachNoIterator { item ->
                item.tiltAngle = angle
            }
            callback.invalidateDrawable(null)
        }
    }

    fun startAnimation() {
        if (rotateAnimation.isRunning
                || cabinImages.isEmpty()) {
            return
        }
        rotateAnimation.startAnimation(rotateListener)
        tiltAnimation.startAnimation(viewConfig.rotateSpeed, tiltListener)
    }

    fun pauseAnimation() {
        rotateAnimation.pauseAnimation()
    }

    fun resumeAnimation() {
        rotateAnimation.resumeAnimation()
    }

    fun stopAnimation() {
        tiltAnimation.cancelAnimation()
        rotateAnimation.stopAnimation()
    }
}