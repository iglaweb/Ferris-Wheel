package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by igor-lashkov on 11/01/2018.
 */

private const val PILL_ANGLE_FROM = 110.0
private const val PILL_ANGLE_TO = 70.0

internal class WheelBaseDrawer(private val context: Context, private val config: WheelViewConfig) : IWheelDrawer {

    private val dp6 = context.dpF(6f)
    private val dp10 = context.dp(10f)
    private val dp14 = context.dpF(14f)
    private val dp16 = context.dpF(16f)
    private val dp56 = context.dpF(56f)
    private val dp28 = context.dpF(28f)
    private val dp32 = context.dpF(32f)
    private val dp34 = context.dpF(34f)

    private val minRadius: Double = context.dp(100f)
    private val radiusWheelForCabin: Double = context.dp(150f) //for 42dp

    var radius = 0.0
        set(value) {
            field = if (value < minRadius) minRadius else value
        }

    private val useCabinAutoSize = config.cabinSize == -1
    private val defaultCabinSize: Int = context.resources.getDimensionPixelSize(R.dimen.fwv_cabin_size)
    var cabinSize = defaultCabinSize
    var ratioCabinSize: Double = 1.0

    val centerPoint = PointF()
    private var dirtyDraw = true

    var rotateAngle = 0f

    private val baseGroundPaint = smoothPaint(config.baseColor).apply {
        style = Paint.Style.FILL
    }

    private val patternPaint = smoothPaint(config.wheelColor).apply {
        strokeWidth = context.dpF(2f)
        style = Paint.Style.STROKE
    }

    private val innerCirclePaint = smoothPaint(config.wheelColor).apply {
        strokeWidth = dp6
        style = Paint.Style.STROKE
    }

    private val pillLinePaint = smoothPaint(config.baseColor).apply {
        strokeWidth = context.dpF(8f)
        style = Paint.Style.STROKE
    }

    private val circleOuterPaint = smoothPaint(config.wheelColor).apply {
        strokeWidth = dp6
        style = Paint.Style.STROKE
    }

    private val circleInnerPaintStroke = smoothPaint(config.coreStyle.colorCircleStroke).apply {
        strokeWidth = context.dpF(4f)
        style = Paint.Style.STROKE
    }
    private val circleInnerPaintFill = smoothPaint(config.coreStyle.colorCircleFill).apply {
        style = Paint.Style.FILL
    }

    private val pillLeftStart1 = PointF()
    private val pillRightStart2 = PointF()

    private val pillLeftEnd1 = PointF()
    private val pillRightEnd2 = PointF()

    private val pillGroundBlock1 = PointF()
    private val pillGroundBlock2 = PointF()

    private val roundRect = RectF()


    private val patternStep = 5
    private val patternPoints = 360 / (patternStep * 2)

    private val patternPointsOut = Array(patternPoints + 1) { PointF() }
    private val patternPointsIn = Array(patternPoints + 1) { PointF() }

    private val linePoints = FloatArray((patternPoints + 1) * 4 * 2 + patternPoints * 4)

    private val paintStar by lazyNonSafe {
        val color = config.coreStyle.starIcon?.colorFill
                ?: context.getColorRes(R.color.fwv_star_fill_color)
        smoothPaint(color).apply {
            style = Paint.Style.FILL
        }
    }
    private val pathStar by lazyNonSafe { Path() }

    private fun getGroundPadding(): Double = defaultCabinSize + CABIN_TILT_MAX + dp10 + dp16

    private fun getPaddingOutside(): Double = dp6.toDouble()

    override fun configure(rect: Rect) {
        val minDiameter = minRadius * 2.0
        val minAvailableWidth = minDiameter + defaultCabinSize
        val minAvailableHeight = minDiameter + getGroundPadding()
        val parentWidth = rect.width()
        val parentHeight = rect.height()
        if (parentWidth < minAvailableWidth || parentHeight < minAvailableHeight) {
            //no space
            return
        }
        val circleLength = minDiameter * PI
        if (defaultCabinSize * config.cabinsNumber > circleLength) {
            //no space
            return
        }

        val centerX = parentWidth / 2.0f
        val centerY = (parentHeight - getGroundPadding().toFloat()) / 2.0f
        this.centerPoint.set(centerX, centerY)
        val minSize = minOf(centerX - defaultCabinSize / 2f, centerY)
        this.radius = minSize - getPaddingOutside()

        if (useCabinAutoSize) {
            this.ratioCabinSize = this.radius / radiusWheelForCabin
            this.cabinSize = (defaultCabinSize * ratioCabinSize).toInt()
        }

        dirtyDraw = true
    }

    fun setPointPosAsWheel(outPoint: PointF, angle: Double) {
        setPointPos(outPoint, this.centerPoint, angle, this.radius)
    }

    override fun onPostDraw(canvas: Canvas) {
        canvas.apply {
            drawBase(this)
            drawCircle(centerPoint, dp16, circleInnerPaintStroke)
            drawCircle(centerPoint, dp14, circleInnerPaintFill)
            if (config.coreStyle.starIcon != null) {
                drawPath(pathStar, paintStar)
            }
        }
    }

    override fun onPreDraw(canvas: Canvas) {

        val radiusF = radius.toFloat()

        if (dirtyDraw) {
            calcNewPosition(centerPoint, radiusF)
            dirtyDraw = false
        }

        canvas.apply {
            save()
            rotate(rotateAngle, centerPoint.x, centerPoint.y)

            drawCircle(centerPoint, dp56, innerCirclePaint)

            drawCircle(centerPoint, dp28, patternPaint)
            drawCircle(centerPoint, dp34, patternPaint)

            drawCircle(centerPoint, radiusF, circleOuterPaint)

            drawLines(linePoints, patternPaint)

            drawCircle(centerPoint, getPatternRadiusInner(radiusF).toFloat(), patternPaint)
            restore()
        }
    }

    private fun setLineAtIndex(arr: FloatArray, index: Int, line1: PointF, line2: PointF) {
        arr[index] = line1.x
        arr[index + 1] = line1.y
        arr[index + 2] = line2.x
        arr[index + 3] = line2.y
    }

    private fun fillArrayWithData() {
        var n = 0
        var i = 0
        while (i <= patternPoints) {
            setLineAtIndex(linePoints, n, patternPointsOut[i], patternPointsIn[i])
            n += 4
            if (i > 0) {
                setLineAtIndex(linePoints, n, patternPointsIn[i], patternPointsOut[i - 1])
                n += 4
            }
            setLineAtIndex(linePoints, n, centerPoint, patternPointsIn[i])
            n += 4
            i++
        }
    }

    private fun drawBase(canvas: Canvas) {
        canvas.apply {
            drawLine(pillLeftStart1, pillLeftEnd1, pillLinePaint)
            drawLine(pillRightStart2, pillRightEnd2, pillLinePaint)
            drawRoundRect(
                    roundRect,
                    dp6,
                    dp6,
                    baseGroundPaint)
        }
    }

    private fun calcNewPosition(centerPoint: PointF, radius: Float) {

        setPointPos(pillLeftStart1, centerPoint, PILL_ANGLE_TO, dp16.toDouble())
        setPointPos(pillRightStart2, centerPoint, PILL_ANGLE_FROM, dp16.toDouble())

        val groundPoint = radius + getGroundPadding()
        setPointPos(pillLeftEnd1, centerPoint, PILL_ANGLE_TO, groundPoint)
        setPointPos(pillRightEnd2, centerPoint, PILL_ANGLE_FROM, groundPoint)

        setPointPos(pillGroundBlock1, centerPoint, PILL_ANGLE_TO, groundPoint - dp16)
        setPointPos(pillGroundBlock2, centerPoint, PILL_ANGLE_FROM, groundPoint - dp16)

        roundRect.set(
                pillRightEnd2.x - dp28,
                pillRightEnd2.y - dp6,
                pillLeftEnd1.x + dp28,
                pillLeftEnd1.y - dp6 + dp16
        )

        var angle1 = patternStep.toDouble()
        var angle2 = 0.0
        val outPatternRadius = getPatternRadiusOuter(radius)
        val innerPatternRadius = getPatternRadiusInner(radius)
        val stepBy = patternStep * 2.0
        for (i in 0..patternPoints) {
            setPointPos(patternPointsOut[i], centerPoint, angle1, outPatternRadius)
            setPointPos(patternPointsIn[i], centerPoint, angle2, innerPatternRadius)

            angle1 += stepBy
            angle2 += stepBy
        }
        measureStarPath(centerPoint, dp32)

        fillArrayWithData()
    }

    private fun measureStarPath(centerPoint: PointF, size: Float) {
        pathStar.apply {
            val half = size / 2f
            val fromX = centerPoint.x - half
            val fromY = centerPoint.y - half
            rewind()
            // top left
            moveTo(fromX + half * 0.5f, fromY + half * 0.84f)
            // top right
            lineTo(fromX + half * 1.5f, fromY + half * 0.84f)
            // bottom left
            lineTo(fromX + half * 0.68f, fromY + half * 1.45f)
            // top tip
            lineTo(fromX + half * 1.0f, fromY + half * 0.5f)
            // bottom right
            lineTo(fromX + half * 1.32f, fromY + half * 1.45f)
            // top left
            lineTo(fromX + half * 0.5f, fromY + half * 0.84f)
            close()
        }
    }

    private fun getPatternRadiusOuter(radius: Float): Double = radius - circleOuterPaint.strokeWidth / 2.0
    private fun getPatternRadiusInner(radius: Float): Double = getPatternRadiusOuter(radius) - dp16


    private fun Canvas.drawLine(p1: PointF, p2: PointF, paint: Paint) {
        drawLine(p1.x, p1.y, p2.x, p2.y, paint)
    }

    private fun Canvas.drawCircle(point: PointF, radius: Float, paint: Paint) {
        drawCircle(point.x, point.y, radius, paint)
    }

    private fun setPointPos(outPoint: PointF, centerPoint: PointF, angle: Double, radius: Double) {
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