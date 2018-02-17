package ru.github.igla.carousel

/**
 * Created by igor-lashkov on 10/01/2018.
 */
interface WheelDrawableListener {
    fun isCenterCoordinate(x: Float, y: Float): Boolean
}