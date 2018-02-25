package ru.github.igla.wheel

import android.content.Context
import android.graphics.*

/**
 * Created by igor-lashkov on 17/01/2018.
 */

internal class CabinDrawable(context: Context, private val imageNumber: Int = 0, cabinColors: Array<String>) {

    var tiltAngle = 0f

    private val dstRect = RectF()

    private val bottomLineOffset by lazyNonSafe { context.dpF(14f) }
    private val cabinLineHeight by lazyNonSafe { context.dpF(8f) }
    private val cabinWindowHeight by lazyNonSafe { context.dpF(24f) }
    private val cabinStrokeWidth by lazyNonSafe { context.dpF(6f) }
    private val cabinCornerRadius by lazyNonSafe { context.dpF(8f) }
    private val arcRadius by lazyNonSafe { context.dpF(8f) }


    private val cabinLinePaint by lazyNonSafe {
        smoothPaint(Color.BLACK).apply {
            style = Paint.Style.FILL
        }
    }

    private lateinit var cabinPaint: Paint
    private lateinit var cabinPaintFill: Paint

    init {
        val cabinColor = cabinColors[imageNumber % cabinColors.size]
        Color.parseColor(cabinColor).apply {
            cabinPaint = smoothPaint(this).apply {
                strokeWidth = cabinStrokeWidth
                style = Paint.Style.STROKE
            }
            cabinPaintFill = smoothPaint(this).apply {
                style = Paint.Style.FILL
            }
        }
    }

    fun drawCabin(canvas: Canvas, position: PointF, size: Float) {
        canvas.apply {
            save()

            val angle = if (imageNumber % 2 == 0) tiltAngle else -tiltAngle
            rotate(angle, position.x, position.y)

            val halfSize = size / 2.0f
            val leftBorder = position.x - halfSize
            val topBorder = position.y
            val rightBorder = position.x + halfSize
            val bottomBorder = position.y + size


            val cabinStrokeHalf = cabinStrokeWidth / 2f
            val cabinTop = topBorder + cabinStrokeHalf
            val cabinBottom = bottomBorder - cabinStrokeHalf
            dstRect.set(
                    leftBorder + cabinStrokeHalf,
                    cabinTop,
                    rightBorder - cabinStrokeHalf,
                    cabinBottom)
            drawRoundRect(dstRect, cabinCornerRadius, cabinCornerRadius, cabinPaint) //cabin


            val centerArc = leftBorder + halfSize
            dstRect.set(centerArc - arcRadius * 0.8f,
                    cabinTop - arcRadius,
                    centerArc + arcRadius * 0.8f,
                    topBorder + arcRadius)
            drawArc(dstRect, 180f, 180f, true, cabinPaintFill) //top arc

            dstRect.set(
                    leftBorder,
                    cabinTop + cabinWindowHeight,
                    rightBorder,
                    cabinBottom)
            drawBottomRoundRect(canvas, dstRect, cabinPaintFill, cabinCornerRadius) //cabin fill


            val blackLineTop = bottomBorder - bottomLineOffset - cabinLineHeight
            val blackLineBottom = bottomBorder - bottomLineOffset
            drawRect(
                    leftBorder,
                    blackLineTop,
                    rightBorder,
                    blackLineBottom,
                    cabinLinePaint
            )
            restore()
        }
    }

    private fun drawBottomRoundRect(canvas: Canvas, rect: RectF, paint: Paint, radius: Float) {
        canvas.drawRoundRect(rect, radius, radius, paint)
        canvas.drawRect(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom - radius,
                paint
        )
    }
}