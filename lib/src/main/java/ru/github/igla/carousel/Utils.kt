package ru.github.igla.carousel

import android.content.Context
import android.util.Log


private const val TAG = "CircleView"

internal fun Context.dp(dp: Float): Int = Math.round(dp * resources.displayMetrics.density)

internal inline fun log(lambda: () -> String) {
    if (BuildConfig.DEBUG) {
        Log.d(TAG, lambda())
    }
}
