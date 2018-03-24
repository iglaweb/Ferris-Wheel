package ru.github.igla.ferriswheel

import android.content.Context
import android.graphics.Paint


internal fun Context.dp(dp: Float): Double = dpF(dp).toDouble()
internal fun Context.dpF(dp: Float): Float = dp * resources.displayMetrics.density

internal fun <T> lazyNonSafe(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

internal fun smoothPaint(color: Int): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
        }

/***
 * https://discuss.kotlinlang.org/t/performant-and-elegant-iterator-over-custom-collection/2962/6
 */
internal inline fun <E> List<E>.forEachNoIterator(block: (E) -> Unit) {
    var index = 0
    val size = size
    while (index < size) {
        block(get(index))
        index++
    }
}