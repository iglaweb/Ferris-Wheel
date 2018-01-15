package ru.github.igla.carousel

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.ColorFilter


/**
 * Created by igor-lashkov on 11/01/2018.
 */

const val IMAGE_STATE_NOT_ACTIVE = 0
const val IMAGE_STATE_CAROUSEL = 1

abstract class BaseImageDrawable(context: Context, val circleImage: CircleImage) : Drawable() {

    private val gradientDrawable: Drawable? = createGradientDrawable()

    var state = IMAGE_STATE_NOT_ACTIVE

    private val dstScaleRect = RectF()

    private val image = circleImage.bitmap ?: BitmapFactory.decodeResource(context.resources, circleImage.imageResId)

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    private val shadowPaint: Paint by lazy(LazyThreadSafetyMode.NONE) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.WHITE
            setShadowLayer(1f, 0f, 2f, Color.parseColor("#28000000"))
        }
    }

    private val alphaPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun resetAlpha() {
        alpha = circleImage.alpha
    }

    fun setCurrentState(state: Int) {
        this.state = state
    }

    fun isViewVisible() = state != IMAGE_STATE_NOT_ACTIVE

    fun drawImage(canvas: Canvas, drawable: Drawable) {
        drawable.draw(canvas)
    }

    fun drawBitmap(canvas: Canvas, image: Bitmap) {
        val w = bounds.width()
        val h = bounds.height()
        val size = minOf(w, h)
        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()
        drawGradientCircle(canvas, size.toFloat(), cx, cy)

        val imageWidthHalf = image.width / 2f
        val imageHeightHalf = image.height / 2f
        canvas.drawBitmap(image, cx - imageWidthHalf, cy - imageHeightHalf, alphaPaint)
    }

    private fun drawGradientCircle(canvas: Canvas, size: Float, cx: Float, cy: Float) {
        gradientDrawable?.let {
            val halfSize = size / 2f
            it.setBounds(
                    (cx - halfSize).toInt(),
                    (cy - halfSize).toInt(),
                    (cx + halfSize).toInt(),
                    (cy + halfSize).toInt()
            )
            it.draw(canvas)
        }
    }

    fun drawBitmap(canvas: Canvas, lastPosition: PointF, sizeF: Int, fraction: Float = 1f, isScalingUp: Boolean = false) {
        val size = sizeF.toFloat() * fraction

        val halfSize = size / 2.0f
        val x = lastPosition.x - halfSize
        val y = lastPosition.y - halfSize
        val right = lastPosition.x + halfSize
        val bottom = lastPosition.y + halfSize
        dstScaleRect.set(
                x,
                y,
                right,
                bottom)

        gradientDrawable?.let {
            if (!isScalingUp) {
                canvas.drawCircle(
                        lastPosition.x,
                        lastPosition.y,
                        halfSize,
                        shadowPaint)
            }

            val oldBounds = it.bounds
            if (oldBounds.isEmpty || isScalingUp) {
                it.setBounds(0, 0,
                        dstScaleRect.width().toInt(), dstScaleRect.height().toInt()
                )
            }
            canvas.save()
            canvas.translate(x, y)
            it.draw(canvas)
            canvas.translate(-x, -y)
            canvas.restore()
        }
        canvas.drawBitmap(image, null, dstScaleRect, null)
    }

    private fun createGradientDrawable(): Drawable? =
            if (circleImage.gradient == null) {
                null
            } else {
                PaintDrawable().apply {
                    shape = OvalShape()
                    shaderFactory = circleImage.gradient
                }
            }


    override fun setAlpha(alpha: Int) {
        val alphaValue = paint.alpha
        if (alpha == alphaValue) {
            return
        }
        paint.alpha = alpha
        this.alphaPaint.alpha = alpha
        this.gradientDrawable?.alpha = alpha
        invalidateSelf()
    }

    override fun getOpacity() = if (paint.alpha < 255) PixelFormat.TRANSLUCENT else PixelFormat.OPAQUE

    override fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getColorFilter(): ColorFilter? = paint.colorFilter

    override fun setFilterBitmap(filter: Boolean) {
        paint.isFilterBitmap = filter
        invalidateSelf()
    }
}