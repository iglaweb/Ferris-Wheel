package ru.github.igla.carousel

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class WheelViewConfig(
        var cabinsNumber: Int = CABINS_NUMBER,
        var rotateSpeed: Int = ROTATE_DEGREE_SPEED_IN_SEC,
        var isClockwise: Boolean = true,
        var isRotating: Boolean = true,
        var cabinSize: Int = 0,
        var startAngle: Float = 0f,
        var centerListener: FerrisWheelView.OnClickCenterListener? = null,
        var scaleUpConfig: ScaleUpConfig = ScaleUpConfig(),
        var rimColor: Int,
        var wheelColor: Int
) {
    val getAngleFrom: Float get() = if (isClockwise) 0f else 360f
    val getAngleTo: Float = if (isClockwise) 360f else 0f

    val getDurationOffset: Long = if (startAngle == 0f) 0L else ((startAngle / 360f) * getDurationFromSpeed(rotateSpeed)).toLong()
}

fun getDurationFromSpeed(rotateSpeed: Int): Long = (360 / rotateSpeed) * 1000L