package ru.github.igla.carousel

import android.annotation.SuppressLint
import android.annotation.TargetApi
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

internal const val CABINS_NUMBER = 8
internal const val ROTATE_DEGREE_SPEED_IN_SEC = 6

class FerrisWheelView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnClickCenterListener {
        fun onClickCenter(e: MotionEvent)
    }

    private val CABIN_SIZE by lazyNonSafe { resources.getDimensionPixelSize(R.dimen.fwv_cabin_size) }

    private val rimColorDefault: Int by lazyNonSafe { getColorRes(context, R.color.fwv_rim_color) }
    private val wheelColorDefault: Int by lazyNonSafe { getColorRes(context, R.color.fwv_wheel_color) }

    private var config: WheelViewConfig = WheelViewConfig(rimColor = rimColorDefault, wheelColor = wheelColorDefault)

    var rimColor: Int = 0
        set(value) {
            field = value
            config.rimColor = value
        }
    var wheelColor: Int = 0
        set(value) {
            field = value
            config.wheelColor = value
        }
    var scaleUpConfig: ScaleUpConfig = ScaleUpConfig()
    var cabinSize: Int = CABIN_SIZE
        set(value) {
            field = value
            config.cabinSize = value
        }
    var centerListener: FerrisWheelView.OnClickCenterListener? = null
        set(value) {
            field = value
            config.centerListener = value
        }
    var numberOfCabins: Int = CABINS_NUMBER
        set(value) {
            if (value < 0) {
                throw ExceptionInInitializerError("Number of cabins should be not negative")
            }
            field = value
            config.cabinsNumber = value
        }

    var isRotating: Boolean = true
        set(value) {
            field = value
            config.isRotating = value
        }
    var isClockwise: Boolean = true
        set(value) {
            field = value
            config.isClockwise = value
        }
    var startAngle: Float = 0f
        set(value) {
            if (value < 0 || value >= 360) {
                throw ExceptionInInitializerError("Start angle must be between 0 and 359")
            }
            field = value
            config.startAngle = value
        }
    var rotateDegreeSpeedInSec: Int = ROTATE_DEGREE_SPEED_IN_SEC
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
                    isRotating = getBoolean(R.styleable.FerrisWheelView_fwv_isRotating, true)
                    rotateDegreeSpeedInSec = getInt(R.styleable.FerrisWheelView_fwv_rotateSpeed, ROTATE_DEGREE_SPEED_IN_SEC)
                    startAngle = getFloat(R.styleable.FerrisWheelView_fwv_startAngle, 0f)
                    cabinSize = getDimensionPixelSize(R.styleable.FerrisWheelView_fwv_cabinSize, CABIN_SIZE)
                    numberOfCabins = getInt(R.styleable.FerrisWheelView_fwv_cabinsNumber, CABINS_NUMBER)
                    rimColor = getColor(R.styleable.FerrisWheelView_fwv_rimStrokeColor, rimColorDefault)
                    wheelColor = getColor(R.styleable.FerrisWheelView_fwv_wheelStrokeColor, wheelColorDefault)
                    recycle()
                }
            } else {
                rimColor = rimColorDefault
                wheelColor = wheelColorDefault
            }
            wheelDrawable = WheelDrawable(context).apply { callback = this@FerrisWheelView }
            this.setDrawable(wheelDrawable)
        }
    }

    @Suppress("DEPRECATION")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setDrawable(drawable: Drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(drawable)
        } else {
            background = drawable
        }
    }

    fun build() {
        config = WheelViewConfig(
                cabinsNumber = this.numberOfCabins,
                scaleUpConfig = this.scaleUpConfig,
                rotateSpeed = this.rotateDegreeSpeedInSec,
                isClockwise = this.isClockwise,
                isRotating = this.isRotating,
                cabinSize = this.cabinSize,
                startAngle = this.startAngle,
                centerListener = this.centerListener,
                rimColor = this.rimColor,
                wheelColor = this.wheelColor
        )
        this.gestureDetector = GestureDetector(context, InteractGestureListener(
                wheelDrawable,
                config.centerListener)
        )
        this.wheelDrawable.build(config)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
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
    return if (Build.VERSION.SDK_INT >= 23) {
        context.getColor(id)
    } else {
        context.resources.getColor(id)
    }
}