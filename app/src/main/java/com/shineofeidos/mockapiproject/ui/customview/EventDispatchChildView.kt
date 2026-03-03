package com.shineofeidos.mockapiproject.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

class EventDispatchChildView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    var onEvent: ((String, String, Int) -> Unit)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        onEvent?.invoke("子元素", "dispatch", ev.actionMasked)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        onEvent?.invoke("子元素", "touch", event.actionMasked)
        return super.onTouchEvent(event)
    }
}
