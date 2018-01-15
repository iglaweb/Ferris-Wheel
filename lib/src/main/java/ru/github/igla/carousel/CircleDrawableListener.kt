package ru.github.igla.carousel

/**
 * Created by igor-lashkov on 10/01/2018.
 */
interface CircleDrawableListener {
    fun findImageByCoordinates(x: Float, y: Float): CircleImage?
    fun isCenterCoordinate(x: Float, y: Float): Boolean
}