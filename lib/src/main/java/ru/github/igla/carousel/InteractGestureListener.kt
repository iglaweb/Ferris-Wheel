package ru.github.igla.carousel

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Created by igor-lashkov on 11/01/2018.
 */
class InteractGestureListener(
        private val circleListener: CircleDrawableListener,
        private val clickImageListener: CircleRotateView.OnClickImageListener? = null,
        private val centerClickCenterListener: CircleRotateView.OnClickCenterListener? = null)
    : GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapUp(e: MotionEvent): Boolean = true

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (clickImageListener != null) {
            circleListener.findImageByCoordinates(e.x, e.y)?.let {
                clickImageListener.onClickImage(e, it)
                return true
            }
        }
        centerClickCenterListener?.let {
            if (circleListener.isCenterCoordinate(e.x, e.y)) {
                it.onClickCenter(e)
                return true
            }
        }
        return super.onSingleTapConfirmed(e)
    }
}