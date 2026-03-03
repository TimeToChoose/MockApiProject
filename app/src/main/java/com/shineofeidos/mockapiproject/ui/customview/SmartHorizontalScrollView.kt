package com.shineofeidos.mockapiproject.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import kotlin.math.abs

class SmartHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    var smartEnabled: Boolean = true
    var onGestureStateChanged: ((String, Boolean) -> Unit)? = null

    private var startX = 0f
    private var startY = 0f
    private var gestureLocked = false
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!smartEnabled) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                gestureLocked = false
                parent?.requestDisallowInterceptTouchEvent(false)
                onGestureStateChanged?.invoke("检测中", true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev.x - startX
                val dy = ev.y - startY
                if (!gestureLocked && (abs(dx) > 4f || abs(dy) > 4f)) {
                    if (abs(dx) > abs(dy)) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                        onGestureStateChanged?.invoke("水平", false)
                    } else {
                        parent?.requestDisallowInterceptTouchEvent(false)
                        onGestureStateChanged?.invoke("垂直", true)
                    }
                    gestureLocked = true
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                onGestureStateChanged?.invoke("未检测", true)
                gestureLocked = false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
