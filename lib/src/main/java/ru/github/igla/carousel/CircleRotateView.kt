package ru.github.igla.carousel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by igor-lashkov on 11/01/2018.
 */


class CircleRotateView : View {

    interface OnClickImageListener {
        fun onClickImage(e: MotionEvent, image: CircleImage)
    }

    interface OnClickCenterListener {
        fun onClickCenter(e: MotionEvent)
    }

    private val ROTATE_DURATION = 60_0000L
    private val CIRCLE_IMAGE_SIZE by lazy(LazyThreadSafetyMode.NONE) { context.dp(72f) }

    private var viewConfig: CircleViewConfig = CircleViewConfig()
    var scaleUpConfig: ScaleUpConfig = ScaleUpConfig()
    var imagesSize: Int = CIRCLE_IMAGE_SIZE
        set(value) {
            field = value
            viewConfig.imagesSize = value
        }
    var clickImageListener: CircleRotateView.OnClickImageListener? = null
        set(value) {
            field = value
            viewConfig.clickImageListener = value
        }
    var centerListener: CircleRotateView.OnClickCenterListener? = null
        set(value) {
            field = value
            viewConfig.centerListener = value
        }
    var images: List<CircleImage> = emptyList()
    var isRotating: Boolean = true
        set(value) {
            field = value
            viewConfig.isRotating = value
        }
    var isClockwise: Boolean = true
        set(value) {
            field = value
            viewConfig.isClockwise = value
        }
    var startAngle: Float = 0f
        set(value) {
            if (value < 0 || value >= 360) {
                throw ExceptionInInitializerError("Start angle must be between 0 and 359")
            }
            field = value
            viewConfig.startAngle = value
        }
    var rotateDuration: Long = ROTATE_DURATION
        set(value) {
            if (value < 0) {
                throw ExceptionInInitializerError("Rotate duration must be a non-negative integer number")
            }
            field = value
            viewConfig.rotateDuration = value
        }


    private lateinit var circleDrawable: CircleDrawable

    private var gestureDetector: GestureDetector? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (isInEditMode) return
        circleDrawable = CircleDrawable(context).apply { callback = this@CircleRotateView }
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.CircleRotateView)
            array?.apply {
                this@CircleRotateView.isClockwise = getBoolean(R.styleable.CircleRotateView_isClockwise, true)
                this@CircleRotateView.isRotating = getBoolean(R.styleable.CircleRotateView_isRotating, true)
                this@CircleRotateView.rotateDuration = getInt(R.styleable.CircleRotateView_rotateDuration, ROTATE_DURATION.toInt()).toLong()
                this@CircleRotateView.startAngle = getFloat(R.styleable.CircleRotateView_startAngle, 0f)
                this@CircleRotateView.imagesSize = getDimension(R.styleable.CircleRotateView_imageSize, CIRCLE_IMAGE_SIZE.toFloat()).toInt()
                recycle()
            }
        }
        background = circleDrawable
    }

    fun build() {
        viewConfig = CircleViewConfig(
                scaleUpConfig = this.scaleUpConfig,
                images = this.images,
                rotateDuration = this.rotateDuration,
                isClockwise = this.isClockwise,
                isRotating = this.isRotating,
                imagesSize = this.imagesSize,
                startAngle = this.startAngle,
                clickImageListener = this.clickImageListener,
                centerListener = this.centerListener
        )
        this.gestureDetector = GestureDetector(context, InteractGestureListener(
                circleDrawable,
                viewConfig.clickImageListener,
                viewConfig.centerListener)
        )
        this.circleDrawable.build(viewConfig)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    fun getLocationCenter(point: PointF) {
        circleDrawable.getLocationCenter(point)
    }

    fun startAnimation() = circleDrawable.startAnimation()

    fun stopAnimation() = circleDrawable.stopAnimation()

    fun resumeAnimation() = circleDrawable.resumeAnimation()

    fun pauseAnimation() = circleDrawable.pauseAnimation()

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