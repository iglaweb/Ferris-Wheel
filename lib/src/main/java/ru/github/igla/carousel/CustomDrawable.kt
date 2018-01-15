package ru.github.igla.carousel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable


/**
 * Created by igor-lashkov on 11/01/2018.
 */
class CustomDrawable(context: Context, circleImage: CircleImage) : BaseImageDrawable(context, circleImage) {

    private var outputBmp: Bitmap? = null
    private var outputDrawable: Drawable? = null

    init {
        if (circleImage.drawable != null) {
            outputDrawable = circleImage.drawable
        } else {
            outputBmp = circleImage.bitmap ?: BitmapFactory.decodeResource(context.resources, circleImage.imageResId)
        }
    }

    override fun draw(canvas: Canvas) {
        if (!isViewVisible()) {
            return
        }

        if (state == IMAGE_STATE_CAROUSEL) {
            outputBmp?.let {
                drawBitmap(canvas, it)
            }
            outputDrawable?.let {
                drawImage(canvas, it)
            }
        }
    }
}