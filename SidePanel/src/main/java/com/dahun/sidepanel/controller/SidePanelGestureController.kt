package com.dahun.sidepanel.controller

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.GestureDetectorCompat
import com.dahun.sidepanel.provider.IPanelDataProvider
import kotlin.math.max
import kotlin.math.min

class SidePanelGestureController(
    context : Context,
    private val dataProvider: IPanelDataProvider,
    private val listener: OnSidePanelGestureListener
): GestureDetector.OnGestureListener {

    /**
     * 패널의 현재 상태
     */
    var panelState: Int = STATE_CLOSED
        private set

    /**
     * 슬라이드 중인 방향
     */
    private var slideDirection: Int = DIRECTION_IDLE

    /**
     * 현재 패널의 위치
     */
    var panelTranslationX: Float = 0f
        set(value) {
            dimAlpha = min(dataProvider.getMaxDimAlpha(), max(1 - (value / dataProvider.getPanelWidth()), 0f))
            alpha = min(1f, max(1 - (value / dataProvider.getPanelWidth()), 0f))
            field = value
        }

    /**
     * 백그라운드 Dim Alpha
     */
    var dimAlpha: Float = 0f
        private set

    /**
     * Alpha
     */
    var alpha: Float = 0f
        private set

    /**
     * 애니메이션의 동작 여부
     */
    private var isAnimationRun: Boolean = false
    private val gestureDetector = GestureDetectorCompat(context, this)


    fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)

        if(event.actionMasked == MotionEvent.ACTION_UP) {
            when(getPanelIdleDirection()) {
                DIRECTION_LEFT -> requestOpen()
                DIRECTION_RIGHT -> requestClose()
                DIRECTION_IDLE -> {
                    if(panelTranslationX == 0f) panelState = STATE_OPENED
                    else if(panelTranslationX == dataProvider.getPanelWidth().toFloat()) panelState = STATE_CLOSED

                }
            }
        }

        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return if(panelState == STATE_CLOSED) {
            requestOpen()
            true
        } else {
            false
        }
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if(!isAnimationRun) {
            move(distanceX)
        }
        return true
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if(velocityX < 0) {
            requestOpen()
        } else {
            requestClose()
        }
        return true
    }

    /**
     * startValue ~ endValue 까지의 ValueAnimator 시작
     */
    private fun startAnimation(startValue: Float, endValue: Float, onEnd: () -> Unit) {
        if(!isAnimationRun) {
            ValueAnimator.ofFloat(startValue, endValue)
                .apply {
                    duration = 400
                    interpolator = DecelerateInterpolator()
                    addUpdateListener { animator ->
                        apply(animator.animatedValue as Float)
                    }
                    doOnStart {
                        isAnimationRun = true
                    }
                    doOnEnd {
                        isAnimationRun = false
                        onEnd()
                    }
                    start()
                }
        }
    }

    /**
     * 패널 위치 이동
     */
    private fun move(translation: Float) {
        slideDirection = when {
            translation > 0f -> DIRECTION_LEFT
            translation == 0f -> slideDirection
            else -> DIRECTION_RIGHT
        }

        panelTranslationX = when(slideDirection) {
            DIRECTION_LEFT -> max(0f, panelTranslationX - translation)
            DIRECTION_RIGHT -> min(dataProvider.getPanelWidth().toFloat(), panelTranslationX - translation)
            else -> panelTranslationX
        }

        panelState = STATE_DRAGGING
        listener.onSlide(panelTranslationX)
    }

    /**
     * 패널의 위치 적용
     */
    private fun apply(translation: Float) {
        panelTranslationX = translation
        panelState = STATE_MOVED
        listener.onSlide(panelTranslationX)
    }

    /**
     * 패널의 현재 위치에서 열리는 애니메이션 요청
     */
    fun requestOpen() {
        startAnimation(panelTranslationX, 0f) {
            panelState = STATE_OPENED
        }
    }

    /**
     * 패널의 현재 위치에서 닫히는 애니메이션 요청
     */
    fun requestClose() {
        startAnimation(panelTranslationX, dataProvider.getPanelWidth().toFloat()) {
            panelState = STATE_CLOSED
        }
    }

    /**
     * 패널이 완전히 닫히지 않아 정착되어야 할 경우, 정찰되어야 할 위치를 가져오는 함수
     *      -1 : left
     *      0 : idle
     *      1 : right
     */
    private fun getPanelIdleDirection(): Int {
        return when(slideDirection) {
            DIRECTION_LEFT -> {
                if(panelTranslationX > 0f && panelTranslationX < dataProvider.getPanelWidth() * dataProvider.getPanelSlideSensitive()) {
                    DIRECTION_LEFT
                } else if(panelTranslationX == 0f) {
                    DIRECTION_IDLE
                } else {
                    DIRECTION_RIGHT
                }
            }

            DIRECTION_RIGHT -> {
                if(panelTranslationX > dataProvider.getPanelWidth() * (1 - dataProvider.getPanelSlideSensitive()) && panelTranslationX < dataProvider.getPanelWidth()) {
                    DIRECTION_RIGHT
                } else if(panelTranslationX == dataProvider.getPanelWidth().toFloat()) {
                    DIRECTION_IDLE
                } else {
                    DIRECTION_LEFT
                }
            }

            else -> DIRECTION_IDLE
        }
    }

    interface OnSidePanelGestureListener {
        fun onSlide(translation: Float)
    }

    companion object {
        private const val DIRECTION_LEFT = -1
        private const val DIRECTION_RIGHT = 1
        private const val DIRECTION_IDLE = 0

        const val STATE_CLOSED = 2
        const val STATE_MOVED =  3
        const val STATE_DRAGGING = 4
        const val STATE_OPENED = 5
    }
}