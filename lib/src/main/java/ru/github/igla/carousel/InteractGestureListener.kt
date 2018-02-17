package ru.github.igla.carousel

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class InteractGestureListener(
        private val wheelListener: WheelDrawableListener,
        private val centerClickCenterListener: FerrisWheelView.OnClickCenterListener? = null)
    : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent): Boolean = true

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        centerClickCenterListener?.let {
            if (wheelListener.isCenterCoordinate(e.x, e.y)) {
                it.onClickCenter(e)
                return true
            }
        }
        return super.onSingleTapConfirmed(e)
    }
}