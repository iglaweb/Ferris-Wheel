package ru.github.igla.ferriswheel

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by igor-lashkov on 22/02/2018.
 */
interface IWheelDrawer {
    fun onPreDraw(canvas: Canvas)
    fun onPostDraw(canvas: Canvas)
    fun configure(rect: Rect)
}

fun Canvas.drawLine(p1: PointF, p2: PointF, paint: Paint) {
    drawLine(p1.x, p1.y, p2.x, p2.y, paint)
}

fun Canvas.drawCircle(point: PointF, radius: Float, paint: Paint) {
    drawCircle(point.x, point.y, radius, paint)
}

fun setPointPos(outPoint: PointF, centerPoint: PointF, angle: Double, radius: Double) {
    outPoint.x = getXPos(centerPoint.x, radius, angle).toFloat()
    outPoint.y = getYPos(centerPoint.y, radius, angle).toFloat()
}

/***
 * https://en.wikipedia.org/wiki/Sine#Relation_to_the_unit_circle
 */
private fun getXPos(centerX: Float, R: Double, angle: Double): Double = centerX + R * cos(getRadians(angle))

private fun getYPos(centerY: Float, R: Double, angle: Double): Double = centerY + R * sin(getRadians(angle))

private fun getRadians(angle: Double): Double = Math.toRadians(angle)