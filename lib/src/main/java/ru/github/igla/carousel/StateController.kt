package ru.github.igla.carousel

import android.animation.Animator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.pow


/**
 * Created by igor-lashkov on 11/01/2018.
 */

private const val STATE_SCALE_UP = 0
private const val STATE_ROTATING = 1

internal class StateController(
        val context: Context,
        private var viewConfig: WheelViewConfig,
        cabinImages: List<CabinDrawable>,
        bounds: Rect) {

    private val wheelBaseDrawer by lazyNonSafe { WheelBaseDrawer(context, viewConfig) }
    private val tiltAnimation by lazyNonSafe { TiltAnimation() }
    private val scaleUpAnimation by lazyNonSafe { ScaleUpAnimation(viewConfig.scaleUpConfig) }
    private val rotateAnimation by lazyNonSafe { RotateAnimation(viewConfig) }

    private var orientation = Configuration.ORIENTATION_PORTRAIT

    private var currentState = STATE_ROTATING

    private val cabinImages: List<InnerImage>

    init {
        this.orientation = context.resources.configuration.orientation
        this.cabinImages = getList(cabinImages)
        configure(bounds, orientation)
    }

    private fun getList(images: List<CabinDrawable>): List<InnerImage> {
        if (images.isEmpty()) {
            return emptyList()
        }
        val imgCount = images.size
        val rad = 360.0 / imgCount
        var offsetAngle = 0.0
        return List(imgCount) { index ->
            val carouselPos = PointF()
            offsetAngle += rad
            InnerImage(
                    images[index],
                    offsetAngle,
                    carouselPos,
                    viewConfig.cabinSize)
        }
    }

    fun configure(bounds: Rect, orientation: Int) {
        pauseAnimation()
        configureSizes(bounds)
        if (orientation != this.orientation) {
            this.currentState = STATE_ROTATING
            resetImagesState()
        }
        this.orientation = orientation
        resumeAnimation()
    }

    private fun configureSizes(bounds: Rect) {
        if (bounds.isEmpty) {
            return
        }
        configureLayoutPoints(bounds)
        for (item in cabinImages) {
            val offsetAngle = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.carouselPos, offsetAngle)
        }
    }

    private fun InnerImage.getAngleOffset(): Double = getAngleOffset(wheelBaseDrawer.rotateAngle)

    private fun configureLayoutPoints(rect: Rect) {
        wheelBaseDrawer.configure(rect)
    }

    private fun resetImagesState() {
        cabinImages.forEachNoIterator { item ->
            item.lastSize = viewConfig.cabinSize
            val offset = item.getAngleOffset()
            wheelBaseDrawer.setPointPosAsWheel(item.carouselPos, offset)
        }
    }

    fun getLocationCenter(point: PointF) {
        point.set(wheelBaseDrawer.centerPoint)
    }

    private fun isPointInsideRadius(x: Float, y: Float, centerPoint: PointF, radius: Float): Boolean =
            (x - centerPoint.x).pow(2f) +
                    (y - centerPoint.y).pow(2f) <= radius * radius

    fun isCenterCoordinate(x: Float, y: Float): Boolean = isPointInsideRadius(x, y, wheelBaseDrawer.centerPoint, (wheelBaseDrawer.radius / 2.0).toFloat())

    fun drawWheel(canvas: Canvas) {
        if (currentState != STATE_ROTATING) {
            return
        }
        wheelBaseDrawer.onPreDraw(canvas)
        cabinImages.forEachNoIterator { item ->
            item.drawable.drawLineFromCenterTo(canvas, wheelBaseDrawer.centerPoint, item.carouselPos)
            val offsetAngle = item.getAngleOffset()
            when (currentState) {
                STATE_ROTATING -> {
                    wheelBaseDrawer.setPointPosAsWheel(item.carouselPos, offsetAngle)
                    item.drawable.drawCabin(canvas, item.carouselPos, item.lastSize)
                }
            }
        }
        wheelBaseDrawer.onPostDraw(canvas)
    }

    fun startAnimation(callback: Drawable.Callback) {
        if (rotateAnimation.isRunning
                || scaleUpAnimation.isRunning
                || cabinImages.isEmpty()) {
            return
        }
        if (!rotateAnimation.wasStarted && scaleUpAnimation.shouldStartAnim()) {
            this.currentState = STATE_SCALE_UP
            scaleUpAnimation.startAnimation(callback, object : ScaleUpAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(animation: Animator) {
                    startCarouselAnimation(callback)
                }
            })
        } else {
            startCarouselAnimation(callback)
        }
    }

    fun startCarouselAnimation(callback: Drawable.Callback) {
        this.currentState = STATE_ROTATING
        rotateAnimation.startCarouselAnimation(object : RotateAnimation.OnRotateAngleValueChangeListener {
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
        rotateAnimation.pauseCarouselAnimation()
    }

    fun resumeAnimation() {
        rotateAnimation.resumeCarouselAnimation()
    }

    fun stopAnimation() {
        scaleUpAnimation.stopAnimation()
        tiltAnimation.cancelAnimation()
        rotateAnimation.stopAnimation()
    }
}