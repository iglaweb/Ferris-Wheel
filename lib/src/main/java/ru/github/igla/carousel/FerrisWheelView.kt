package ru.github.igla.carousel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


/**
 * Created by igor-lashkov on 11/01/2018.
 */

internal const val DEFAULT_CABINS_NUMBER = 8
internal const val DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC = 6

class FerrisWheelView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnClickCenterListener {
        fun onClickCenter(e: MotionEvent)
    }

    private var cabinColorsDefault = arrayOf("#6eabdf", "#ffb140", "#ce4d5b", "#96bd58")
    private val cabinSizeDefault: Int by lazyNonSafe { resources.getDimensionPixelSize(R.dimen.fwv_cabin_size) }
    private val baseColorDefault: Int by lazyNonSafe { getColorRes(context, R.color.fwv_rim_color) }
    private val wheelColorDefault: Int by lazyNonSafe { getColorRes(context, R.color.fwv_wheel_color) }

    private var config = WheelViewContext(baseColor = baseColorDefault, wheelColor = wheelColorDefault, cabinColors = cabinColorsDefault)

    var cabinColors: Array<String> = cabinColorsDefault
    var baseColor: Int = 0
        set(value) {
            field = value
            config.baseColor = value
        }
    var wheelColor: Int = 0
        set(value) {
            field = value
            config.wheelColor = value
        }
    var cabinSize: Int = cabinSizeDefault
        set(value) {
            field = value
            config.cabinSize = value
        }
    var centerListener: FerrisWheelView.OnClickCenterListener? = null
        set(value) {
            field = value
            config.centerListener = value
        }
    var numberOfCabins: Int = DEFAULT_CABINS_NUMBER
        set(value) {
            if (value < 0) {
                throw ExceptionInInitializerError("Number of cabins should be not negative")
            }
            field = value
            config.cabinsNumber = value
        }
    var isClockwise: Boolean = true
        set(value) {
            field = value
            config.isClockwise = value
        }
    var startAngle: Float = 0f
        set(value) {
            if (value < 0f || value > 360f) {
                throw ExceptionInInitializerError("Start angle must be between 0 and 360")
            }
            field = value % 360f
            config.startAngle = field
        }
    var rotateDegreeSpeedInSec: Int = DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC
        set(value) {
            if (value < 0) {
                throw ExceptionInInitializerError("Rotate duration must be a non-negative integer number")
            }
            field = value
            config.rotateSpeed = value
        }


    private lateinit var wheelDrawable: WheelDrawable

    private var gestureDetector: GestureDetector? = null

    init {
        if (!isInEditMode) {
            if (attrs != null) {
                context.obtainStyledAttributes(attrs, R.styleable.FerrisWheelView)?.apply {
                    isClockwise = getBoolean(R.styleable.FerrisWheelView_fwv_isClockwise, true)
                    rotateDegreeSpeedInSec = getInt(R.styleable.FerrisWheelView_fwv_rotateSpeed, DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC)
                    startAngle = getFloat(R.styleable.FerrisWheelView_fwv_startAngle, 0f)
                    cabinSize = getDimensionPixelSize(R.styleable.FerrisWheelView_fwv_cabinSize, cabinSizeDefault)
                    numberOfCabins = getInt(R.styleable.FerrisWheelView_fwv_cabinsNumber, DEFAULT_CABINS_NUMBER)
                    baseColor = getColor(R.styleable.FerrisWheelView_fwv_baseStrokeColor, baseColorDefault)
                    wheelColor = getColor(R.styleable.FerrisWheelView_fwv_wheelStrokeColor, wheelColorDefault)
                    recycle()
                }
            } else {
                baseColor = baseColorDefault
                wheelColor = wheelColorDefault
            }
            wheelDrawable = WheelDrawable(context).apply { callback = this@FerrisWheelView }
            this.setDrawable(wheelDrawable)
        }
    }

    @Suppress("DEPRECATION")
    private fun setDrawable(drawable: Drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable)
        } else {
            background = drawable
        }
    }

    fun build() {
        config = WheelViewContext(
                cabinsNumber = this.numberOfCabins,
                rotateSpeed = this.rotateDegreeSpeedInSec,
                isClockwise = this.isClockwise,
                cabinSize = this.cabinSize,
                startAngle = this.startAngle,
                centerListener = this.centerListener,
                baseColor = this.baseColor,
                wheelColor = this.wheelColor,
                cabinColors = this.cabinColors
        )
        this.gestureDetector = GestureDetector(context, InteractGestureListener
        { e ->
            config.centerListener?.let { performClickWheel(it, e) } ?: false
        })
        this.wheelDrawable.build(config)
    }

    private fun performClickWheel(listener: OnClickCenterListener, e: MotionEvent): Boolean {
        if (wheelDrawable.isCenterCoordinate(e.x, e.y)) {
            listener.onClickCenter(e)
            return true
        }
        return false
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        super.onDetachedFromWindow()
    }

    fun getLocationCenter(point: PointF) {
        wheelDrawable.getLocationCenter(point)
    }

    fun startAnimation() = wheelDrawable.startAnimation()

    fun stopAnimation() = wheelDrawable.stopAnimation()

    fun resumeAnimation() = wheelDrawable.resumeAnimation()

    fun pauseAnimation() = wheelDrawable.pauseAnimation()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            gestureDetector?.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    performClick()
                    return true
                }
            }
            return true
        }
        return false
    }
}

@Suppress("DEPRECATION")
fun getColorRes(context: Context, id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getColor(id)
    } else {
        context.resources.getColor(id)
    }
}