package com.shineofeidos.mockapiproject.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class EventDispatchParentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onEvent: ((String, String, Int) -> Unit)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        onEvent?.invoke("父容器", "dispatch", ev.actionMasked)
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        onEvent?.invoke("父容器", "intercept", ev.actionMasked)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        onEvent?.invoke("父容器", "touch", event.actionMasked)
        return super.onTouchEvent(event)
    }
}
