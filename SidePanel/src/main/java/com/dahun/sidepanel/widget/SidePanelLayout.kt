package com.dahun.sidepanel.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.dahun.sidepanel.R
import java.util.ArrayList

import com.dahun.sidepanel.provider.IPanelDataProvider
import com.dahun.sidepanel.controller.SidePanelGestureController
import java.lang.IllegalStateException


class SidePanelLayout: FrameLayout, IPanelDataProvider,
    SidePanelGestureController.OnSidePanelGestureListener {

    /**
     * Slider
     */
    private var mPanelSlider: View? = null

    /**
     * Controller
     */
    private val panelGestureController = SidePanelGestureController(context, this, this)

    /**
     * 슬라이드 민감도
     */
    private var mSlideSensitive: Float = 0.8f

    /**
     * Alpha Max Value
     */
    private var mMaxDimAlpha: Float = 0.7f

    /**
     * Paint
     */
    private val mPaint = Paint()

    /**
     * Dim Background Rect
     */
    private val mDimBackgroundRect = RectF()

    /**
     * Dim Background Color
     */
    private var mDimBackgroundColor: Int = Color.GRAY

    /**
     * default closed
     */
    private var mDefaultClosed: Boolean = false

    constructor(context: Context): super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        initView()
        initAttrSet(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        initView()
        initAttrSet(context, attrs)
    }

    private fun initView() {
        setWillNotDraw(false)
    }

    private fun initAttrSet(context: Context, attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SidePanelAttr, 0, 0).apply {
            try {
                mDefaultClosed = getBoolean(R.styleable.SidePanelAttr_defaultClosed, false)
                setPanelSlideSensitive(getFloat(R.styleable.SidePanelAttr_slideSensitive, 0.8f))
                setDimBackgroundMaxAlpha(getFloat(R.styleable.SidePanelAttr_maxDimAlpha, 0.7f))
                mDimBackgroundColor = getColor(R.styleable.SidePanelAttr_dimBackgroundColor, Color.GRAY)
            } finally {
                recycle()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDimBackgroundRect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        panelGestureController.panelTranslationX = if(mDefaultClosed) getPanelWidth().toFloat() else 0f
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val params = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }

        getAllChild().forEach {
            if(it is SidePanelView) {
                it.layoutParams = params
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.color = mDimBackgroundColor
        mPaint.alpha = (255 * panelGestureController.dimAlpha).toInt()

        canvas?.drawRect(mDimBackgroundRect, mPaint)

        getAllChild().forEach {
            if(it !is SidePanelView) {
                it?.alpha = panelGestureController.alpha
            } else {
                it?.translationX = panelGestureController.panelTranslationX
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(isTouchedPanel(event)) {
            event?.let { panelGestureController.onTouchEvent(it) }
            return true
        }

        if(isTouchedSlider(event) && getPanelState() == SidePanelGestureController.STATE_CLOSED || getPanelState() == SidePanelGestureController.STATE_DRAGGING) {
            event?.let { panelGestureController.onTouchEvent(it) }
            return true
        }

        if(event?.actionMasked == MotionEvent.ACTION_UP) {
            return if(getPanelState() == SidePanelGestureController.STATE_OPENED) {
                dismissPanel()
                true
            } else if(getPanelState() != SidePanelGestureController.STATE_CLOSED) {
                panelGestureController.onTouchEvent(event)
            } else {
                false
            }
        }

        return getPanelState() != SidePanelGestureController.STATE_CLOSED
    }

    fun bindSlider(view: View) {
        mPanelSlider = view
    }

    fun showPanel() {
        panelGestureController.requestOpen()
    }

    fun dismissPanel() {
        panelGestureController.requestClose()
    }

    fun getPanelState() = panelGestureController.panelState

    override fun onSlide(translation: Float) {
        postInvalidate()
    }

    private fun isTouchedSlider(event: MotionEvent?): Boolean {
        return event?.y ?: 0f in (mPanelSlider?.top?.toFloat() ?: 0f) .. (mPanelSlider?.bottom?.toFloat() ?: 0f) && event?.x ?: 0f in (mPanelSlider?.left?.toFloat() ?: 0f) .. (mPanelSlider?.right?.toFloat() ?: 0f)
    }

    private fun isTouchedPanel(event: MotionEvent?): Boolean {
        val sidePanelView = getAllChild().find { it is SidePanelView }
        sidePanelView?.let { panel ->
            return event?.y ?: 0f in panel.y .. (panel.y + panel.height) && event?.x ?: 0f in panel.x .. (panel.x + panel.width)
        }

        return false
    }

    override fun getPanelSlideSensitive(): Float {
        return mSlideSensitive
    }

    override fun getMaxDimAlpha(): Float {
        return mMaxDimAlpha
    }

    override fun getPanelWidth(): Int {
        getAllChild().find { it is SidePanelView }?.let {
            return it.measuredWidth
        }

        return 0
    }

    fun setPanelSlideSensitive(sensitive: Float) {
        if(mSlideSensitive in 0.0 .. 1.0) {
            this.mSlideSensitive = sensitive
        } else {
            throw IllegalStateException("Slide Sensitive can only be set between 0.0 and 1.0.")
        }
        invalidate()
    }

    fun setDimBackgroundMaxAlpha(alpha: Float) {
        if(alpha in 0.0 .. 1.0) {
            this.mMaxDimAlpha = alpha
        } else {
            throw IllegalStateException("Background Max Alpha can only be set between 0.0 and 1.0.")
        }
        invalidate()
    }

    fun setDimBackgroundColor(color: Int) {
        this.mDimBackgroundColor = color
        invalidate()
    }

    private fun getAllChild(): ArrayList<View> {
        val child: ArrayList<View> = ArrayList()
        for(i in 0 .. childCount) {
            child.add(getChildAt(i))
        }

        return child
    }

}