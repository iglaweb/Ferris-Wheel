package ru.github.igla.carousel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class CircleImage private constructor(val bitmap: Bitmap? = null,
                                      val imageResId: Int = -1,
                                      val drawable: Drawable? = null,
                                      val size: Int = -1,
                                      val alpha: Int = 255,
                                      val gradient: ShapeDrawable.ShaderFactory? = null) {
    @JvmOverloads
    constructor(bmp: Bitmap,
                size: Int = -1,
                gradient: ShapeDrawable.ShaderFactory? = null) : this(bitmap = bmp, size = size, gradient = gradient)

    @JvmOverloads
    constructor(resId: Int,
                size: Int = -1,
                gradient: ShapeDrawable.ShaderFactory? = null) : this(imageResId = resId, size = size, gradient = gradient)
}