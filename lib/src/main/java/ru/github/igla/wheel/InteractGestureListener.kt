package ru.github.igla.wheel

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class InteractGestureListener(
        private val clickCenter: (MotionEvent) -> Boolean
) : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent): Boolean = true

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (clickCenter(e)) {
            return true
        }
        return super.onSingleTapConfirmed(e)
    }
}