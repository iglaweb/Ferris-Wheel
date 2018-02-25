package ru.igla.carousel.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.support.v7.widget.AppCompatSeekBar
import android.text.TextPaint
import android.util.AttributeSet
import ru.github.igla.wheel.dp

class TextThumbSeekBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.seekBarStyle) :
        AppCompatSeekBar(context, attrs, defStyleAttr) {

    private val thumbSize = context.dp(16f)
    private val textPaint: TextPaint = TextPaint().apply {
        color = Color.WHITE
        textSize = context.sp(14f)
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
    }
    private var bounds = Rect()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progressText = progress.toString()
        textPaint.getTextBounds(progressText, 0, progressText.length, bounds)

        val leftPadding = paddingLeft - thumbOffset
        val rightPadding = paddingRight - thumbOffset
        val width = width - leftPadding - rightPadding
        val progressRatio = progress.toFloat() / max
        val thumbOffset = thumbSize * (.5f - progressRatio)
        val thumbX = progressRatio * width + leftPadding.toFloat() + thumbOffset
        val thumbY = height / 2f + bounds.height() / 2f
        canvas.drawText(progressText, thumbX, thumbY, textPaint)
    }

    private fun Context.sp(sp: Float): Float = sp * resources.displayMetrics.scaledDensity
}