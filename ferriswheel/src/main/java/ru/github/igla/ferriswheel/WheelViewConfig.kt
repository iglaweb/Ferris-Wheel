package ru.github.igla.ferriswheel


/**
 * Created by igor-lashkov on 11/01/2018.
 */

internal const val MAX_ROTATE_SPEED = 100f

internal class WheelViewConfig(
        var cabinsNumber: Int = DEFAULT_CABINS_NUMBER,
        var rotateSpeed: Int = DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC,
        var isAutoRotate: Boolean = true,
        var isClockwise: Boolean = true,
        var cabinSize: Int = -1,
        var startAngle: Float = 0f,
        var centerListener: FerrisWheelView.OnClickCenterListener? = null,
        var cabinListener: FerrisWheelView.OnClickCabinListener? = null,
        var baseColor: Int,
        var wheelColor: Int,
        var cabinColors: List<CabinStyle>,
        var coreStyle: CoreStyle
) {
    val getAngleFrom: Float get() = if (isClockwise) 0f else 360f
    val getAngleTo: Float = if (isClockwise) 360f else 0f

    fun getDurationOffset(angle: Float): Long = if (angle == 0f) 0L else ((angle / 360f) * getDurationFromSpeed(rotateSpeed)).toLong()
}

internal fun getDurationFromSpeed(rotateSpeed: Int): Long = if (rotateSpeed == 0) 0L else (360 / rotateSpeed) * 1000L