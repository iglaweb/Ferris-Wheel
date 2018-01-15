package ru.github.igla.carousel

import android.animation.Animator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


/**
 * Created by igor-lashkov on 11/01/2018.
 */

private const val STATE_SCALE_UP = 0
private const val STATE_ROTATING = 1

class StateController(
        context: Context,
        private var viewConfig: CircleViewConfig,
        images: List<CustomDrawable>,
        bounds: Rect) {

    private val scaleUpAnimation by lazy(LazyThreadSafetyMode.NONE) { ScaleUpAnimation(viewConfig.scaleUpConfig) }

    private val rotateAnimation by lazy(LazyThreadSafetyMode.NONE) { RotateAnimation(viewConfig) }

    private var orientation = Configuration.ORIENTATION_PORTRAIT

    private var currentState = STATE_ROTATING

    private var R = 0.0

    private val circleImages: MutableList<InnerImage> = mutableListOf()

    private val centerPoint by lazy(LazyThreadSafetyMode.NONE) { PointF() }

    init {
        this.orientation = context.resources.configuration.orientation
        configure(bounds, orientation)

        if (!images.isEmpty()) {
            val imgCount = images.size
            val rad = 360.0 / imgCount
            var offsetAngle = 0.0
            for (i in 0 until imgCount) {
                val carouselPos = PointF()
                setPointPos(carouselPos, centerPoint, offsetAngle)
                circleImages.add(InnerImage(
                        images[i],
                        offsetAngle,
                        carouselPos,
                        PointF(),
                        viewConfig.imagesSize)
                )
                offsetAngle += rad
            }
        }
    }

    fun configure(rect: Rect, orientation: Int) {
        pauseAnimation()
        configureSizes(rect)
        if (orientation != this.orientation) {
            this.currentState = STATE_ROTATING
            setImagesState(IMAGE_STATE_CAROUSEL)
            resetImagesState()
        }
        this.orientation = orientation
        resumeAnimation()
    }

    private fun configureSizes(rect: Rect) {
        configurePortrait(rect)
        for (item in circleImages) {
            val offset = getAngleOffset(item)
            setPointPos(item.carouselPos, centerPoint, offset)
        }
    }

    private fun configurePortrait(rect: Rect) {
        val parentWidth = rect.width()
        val parentHeight = rect.height()
        val centerX = parentWidth / 2.0f
        val centerY = parentHeight / 2.0f
        val minSize = minOf(centerX, centerY)
        this.R = minSize - viewConfig.imagesSize / 2.0 - viewConfig.imagesSize / 4.0 //for shadow
        this.centerPoint.set(centerX, centerY)
    }

    private fun setImagesState(state: Int) {
        circleImages.forEach { it.drawable.setCurrentState(state) }
    }

    private fun getAngleOffset(image: InnerImage): Double = image.getAngleOffset(rotateAnimation.carouselAngle)

    private fun resetImagesState() {
        for (element in circleImages) {
            element.drawable.resetAlpha()
            element.drawable.state = IMAGE_STATE_CAROUSEL
            element.lastSize = viewConfig.imagesSize
            val offset = getAngleOffset(element)
            setPointPos(element.carouselPos, centerPoint, offset)
        }
    }

    fun getLocationCenter(point: PointF) {
        point.set(centerPoint)
    }

    private fun getItemLocation(image: InnerImage): PointF {
        val point = PointF()
        val offset = getAngleOffset(image)
        setPointPos(point, centerPoint, offset)
        return point
    }

    fun getImage(x: Float, y: Float): CircleImage? {
        circleImages
                .asSequence()
                .filter {
                    val radius = it.lastSize / 2f
                    isPointInsideRadius(x, y, it.carouselPos, radius)
                }
                .forEach { return it.drawable.circleImage }
        return null
    }

    private fun isPointInsideRadius(x: Float, y: Float, centerPoint: PointF, radius: Float): Boolean =
            (x - centerPoint.x).pow(2f) +
                    (y - centerPoint.y).pow(2f) <= radius * radius

    fun isCenterCoordinate(x: Float, y: Float): Boolean = isPointInsideRadius(x, y, centerPoint, (R / 2).toFloat())

    fun drawImages(canvas: Canvas) {
        val fraction = scaleUpAnimation.fraction
        for (i in circleImages.size - 1 downTo 0) { //draw first image on top
            val item = circleImages[i]
            val offsetAngle = getAngleOffset(item)
            when (currentState) {
                STATE_ROTATING -> {
                    setPointPos(item.carouselPos, centerPoint, offsetAngle)
                    item.drawable.drawBitmap(canvas, item.carouselPos, item.lastSize)
                }
                STATE_SCALE_UP -> {
                    setPointPos(item.tempPos, centerPoint, offsetAngle, R)
                    item.drawable.drawBitmap(canvas, item.tempPos, item.lastSize, fraction, true)
                }
            }
        }
    }

    fun startAnimation(callback: Drawable.Callback) {
        if (rotateAnimation.isRunning
                || scaleUpAnimation.isRunning
                || circleImages.isEmpty()) {
            return
        }
        if (!rotateAnimation.wasStarted && scaleUpAnimation.shouldStartAnim()) {
            this.currentState = STATE_SCALE_UP
            scaleUpAnimation.startAnimation(callback, object : ScaleUpAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(animation: Animator) {
                    this@StateController.currentState = STATE_ROTATING
                    setImagesState(IMAGE_STATE_CAROUSEL)
                    rotateAnimation.startCarouselAnimation(callback)
                }
            })
        } else {
            this.currentState = STATE_ROTATING
            setImagesState(IMAGE_STATE_CAROUSEL)
            rotateAnimation.startCarouselAnimation(callback)
        }
    }

    fun pauseAnimation() {
        rotateAnimation.pauseCarouselAnimation()
    }

    fun resumeAnimation() {
        rotateAnimation.resumeCarouselAnimation()
    }

    fun stopAnimation() {
        scaleUpAnimation.stopAnimation()
        rotateAnimation.stopAnimation()
    }

    private fun setPointPos(outPoint: PointF, centerPoint: PointF, angle: Double, radius: Double = R) {
        outPoint.x = getXPos(centerPoint.x, radius, angle).toFloat()
        outPoint.y = getYPos(centerPoint.y, radius, angle).toFloat()
    }

    /***
     * https://en.wikipedia.org/wiki/Sine#Relation_to_the_unit_circle
     */
    private fun getXPos(centerX: Float, R: Double, angle: Double): Double = centerX + R * cos(getRadians(angle))

    private fun getYPos(centerY: Float, R: Double, angle: Double): Double = centerY + R * sin(getRadians(angle))

    private fun getRadians(angle: Double): Double = Math.toRadians(angle)
}