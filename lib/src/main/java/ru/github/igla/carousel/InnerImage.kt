package ru.github.igla.carousel

import android.graphics.PointF

/**
 * Created by igor-lashkov on 11/01/2018.
 */
internal class InnerImage constructor(
        val drawable: BaseImageDrawable,
        private val angle: Double = 0.0,
        var carouselPos: PointF = PointF(),
        var tempPos: PointF = PointF(),
        var lastSize: Int
) {
    fun getAngleOffset(carouselAngle: Float): Double = (angle + carouselAngle) % 360.0
}