package ru.github.igla.ferriswheel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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

class FerrisWheelView : View {

    interface OnClickCenterListener {
        fun onClickCenter(e: MotionEvent)
    }

    private val cabinColorsDefault: List<CabinStyle> = resources.getStringArray(R.array.cabin_colors_array).map { color ->
        CabinStyle(Color.parseColor(color), cabinLineColorDefault)
    }
    private val cabinLineColorDefault: Int = context.getColorRes(R.color.fwv_cabin_line_color)
    private val baseColorDefault: Int = context.getColorRes(R.color.fwv_base_color)
    private val wheelColorDefault: Int = context.getColorRes(R.color.fwv_wheel_color)
    private val coreStyleDefault: CoreStyle = CoreStyle(
            context.getColorRes(R.color.fwv_star_bg_color),
            baseColorDefault,
            StarIcon(context.getColorRes(R.color.fwv_star_fill_color))
    )

    private var config: WheelViewConfig = WheelViewConfig(
            baseColor = baseColorDefault,
            wheelColor = wheelColorDefault,
            cabinColors = cabinColorsDefault,
            coreStyle = coreStyleDefault)

    var coreStyle: CoreStyle = coreStyleDefault
        set(value) {
            field = value
            config.coreStyle = value
        }
    var cabinColors: List<CabinStyle> = cabinColorsDefault
        set(value) {
            field = if (value.isEmpty()) cabinColorsDefault else value
            config.cabinColors = field
        }
    var baseColor: Int = 0
        set(value) {
            field = value
            config.baseColor = value
            coreStyle = CoreStyle(
                    coreStyle.colorCircleFill,
                    value,
                    coreStyle.starIcon
            )
        }
    var wheelColor: Int = 0
        set(value) {
            field = value
            config.wheelColor = value
        }
    var cabinSize: Int = -1
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
            if (value < 0f) {
                throw ExceptionInInitializerError("Start angle must not be negative")
            }
            field = value % 360f
            config.startAngle = field
        }
    var rotateDegreeSpeedInSec: Int = DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC
        set(value) {
            if (value < 0 || value > MAX_ROTATE_SPEED) {
                throw ExceptionInInitializerError("Rotate speed must be between 0 and 100")
            }
            field = value
            config.rotateSpeed = value
        }

    private lateinit var wheelDrawable: WheelDrawable

    private var gestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, attributeSetId: Int) : super(context, attrs, attributeSetId) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet? = null) {
        if (!isInEditMode) {
            if (attrs != null) {
                context.obtainStyledAttributes(attrs, R.styleable.FerrisWheelView)?.apply {
                    isClockwise = getBoolean(R.styleable.FerrisWheelView_fwv_isClockwise, true)
                    rotateDegreeSpeedInSec = getInt(R.styleable.FerrisWheelView_fwv_rotateSpeed, DEFAULT_ROTATES_SPEED_DEGREE_IN_SEC)
                    startAngle = getFloat(R.styleable.FerrisWheelView_fwv_startAngle, 0f)
                    cabinSize = getDimensionPixelSize(R.styleable.FerrisWheelView_fwv_cabinSize, -1)

                    if (hasValue(R.styleable.FerrisWheelView_fwv_cabinFillColor)) {
                        val cabinColorFill = getColor(R.styleable.FerrisWheelView_fwv_cabinFillColor, 0)
                        val cabinColorLineStroke = getColor(R.styleable.FerrisWheelView_fwv_cabinLineStrokeColor, cabinLineColorDefault)
                        cabinColors = listOf(CabinStyle(cabinColorFill, cabinColorLineStroke))
                    }

                    numberOfCabins = getInt(R.styleable.FerrisWheelView_fwv_cabinsNumber, DEFAULT_CABINS_NUMBER)
                    baseColor = getColor(R.styleable.FerrisWheelView_fwv_baseStrokeColor, baseColorDefault)
                    wheelColor = getColor(R.styleable.FerrisWheelView_fwv_wheelStrokeColor, wheelColorDefault)
                    recycle()
                }
            } else {
                baseColor = baseColorDefault
                wheelColor = wheelColorDefault
                cabinColors = cabinColorsDefault
            }
            this.gestureDetector = GestureDetector(context, InteractGestureListener
            { e ->
                config.centerListener?.let { performClickWheel(it, e) } ?: false
            })
            wheelDrawable = WheelDrawable(context).apply {
                callback = this@FerrisWheelView
            }
            this@FerrisWheelView.setDrawable(wheelDrawable)
            wheelDrawable.build(config)
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

    fun startAnimation() {
        wheelDrawable.apply {
            build(config)
            startAnimation()
        }
    }

    fun stopAnimation() = wheelDrawable.stopAnimation()

    fun resumeAnimation() {
        wheelDrawable.apply {
            build(config)
            resumeAnimation()
        }
    }

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
internal fun Context.getColorRes(id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(id)
    } else {
        resources.getColor(id)
    }
}