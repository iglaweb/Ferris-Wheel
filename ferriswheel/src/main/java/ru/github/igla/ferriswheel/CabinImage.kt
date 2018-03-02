package ru.github.igla.ferriswheel

import android.graphics.PointF

/**
 * Created by igor-lashkov on 11/01/2018.
 */
internal class CabinImage constructor(
        val drawable: CabinDrawable,
        private val angle: Double
) {
    val wheelPos: PointF = PointF()

    fun getAngleOffset(rotateAngle: Float): Double = (angle + rotateAngle) % 360.0
}