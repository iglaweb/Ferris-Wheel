package ru.github.igla.carousel

import android.animation.TimeInterpolator
import android.view.animation.LinearInterpolator

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class CircleViewConfig(
        val images: List<CircleImage> = emptyList(),
        var interpolator: TimeInterpolator = LinearInterpolator(),
        var rotateDuration: Long = 60_000,
        var isClockwise: Boolean = true,
        var isRotating: Boolean = true,
        var imagesSize: Int = 0,
        var startAngle: Float = 0f,
        var clickImageListener: CircleRotateView.OnClickImageListener? = null,
        var centerListener: CircleRotateView.OnClickCenterListener? = null,
        var scaleUpConfig: ScaleUpConfig = ScaleUpConfig()
) {
    val getAngleFrom: Float get() = if (isClockwise) 0f else 360f
    val getAngleTo: Float = if (isClockwise) 360f else 0f
    val getDurationOffset: Long = if (startAngle == 0f) 0L else ((startAngle / 360f) * rotateDuration).toLong()
}