package ru.github.igla.ferriswheel

import android.graphics.Canvas
import android.graphics.Rect

/**
 * Created by igor-lashkov on 22/02/2018.
 */
interface IWheelDrawer {
    fun onPreDraw(canvas: Canvas)
    fun onPostDraw(canvas: Canvas)
    fun configure(rect: Rect)
}