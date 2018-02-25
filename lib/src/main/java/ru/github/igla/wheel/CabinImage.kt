package ru.github.igla.wheel

import android.graphics.PointF

/**
 * Created by igor-lashkov on 11/01/2018.
 */
internal class CabinImage constructor(
        val drawable: CabinDrawable,
        private val angle: Double = 0.0,
        var wheelPos: PointF = PointF()
) {
    fun getAngleOffset(rotateAngle: Float): Double = (angle + rotateAngle) % 360.0
}