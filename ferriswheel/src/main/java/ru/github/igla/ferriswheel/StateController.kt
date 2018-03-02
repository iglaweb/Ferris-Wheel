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
        val context: Context,
        private var viewConfig: WheelViewConfig,
        cabinImages: List<CabinDrawable>,
        bounds: Rect) {

    private val wheelBaseDrawer by lazyNonSafe { WheelBaseDrawer(context, viewConfig) }
    private val tiltAnimation by lazyNonSafe { TiltAnimation(viewConfig) }
    private val rotateAnimation by lazyNonSafe { RotateAnimation(viewConfig) }

    private var orientation = context.resources.configuration.orientation

    private val cabinImages: List<CabinImage>

    init {
        this.cabinImages = getList(cabinImages)
        configure(bounds, orientation)
    }

    private fun getList(images: List<CabinDrawable>): List<CabinImage> {
        if (images.isEmpty()) {
            return emptyList()
        }
        val imgCount = images.size
        val rad = 360.0 / imgCount
        var offsetAngle = 0.0
        return List(imgCount) { index ->
            offsetAngle += rad
            CabinImage(images[index], offsetAngle)
        }
    }

    fun configure(bounds: Rect, orientation: Int) {
        pauseAnimation()
        configureSizes(bounds)
        if (orientation != this.orientation) {
            resetImagesState()
        }
        this.orientation = orientation
        resumeAnimation()
    }

    private fun configureSizes(bounds: Rect) {
        if (bounds.isEmpty) {
            return
        }
        wheelBaseDrawer.configure(bounds)
        cabinImages.forEachNoIterator { item ->
            val offsetAngle = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.wheelPos, offsetAngle)
        }
    }

    private fun CabinImage.getAngleOffset(): Double = getAngleOffset(wheelBaseDrawer.rotateAngle)

    private fun resetImagesState() {
        cabinImages.forEachNoIterator { item ->
            val offset = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.wheelPos, offset)
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
            item.drawable.drawCabin(
                    canvas,
                    item.wheelPos,
                    wheelBaseDrawer.cabinSize,
                    wheelBaseDrawer.ratioCabinSize
            )
        }
        wheelBaseDrawer.onPostDraw(canvas)
    }

    fun startAnimation(callback: Drawable.Callback) {
        if (rotateAnimation.isRunning
                || cabinImages.isEmpty()) {
            return
        }
        startWheelAnimation(callback)
    }

    private fun startWheelAnimation(callback: Drawable.Callback) {
        rotateAnimation.startAnimation(object : RotateAnimation.OnRotateAngleValueChangeListener {
            override fun onChangeRotateAngle(angle: Float) {
                wheelBaseDrawer.rotateAngle = angle
                callback.invalidateDrawable(null)
            }
        })
        tiltAnimation.startAnimation(object : TiltAnimation.TiltValueChangeListener {
            override fun onTiltChange(angle: Float) {
                cabinImages.forEachNoIterator { item ->
                    item.drawable.tiltAngle = angle
                }
                callback.invalidateDrawable(null)
            }
        })
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