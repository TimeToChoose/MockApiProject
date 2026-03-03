package com.shineofeidos.mockapiproject.ui.screens

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Switch
import androidx.activity.ComponentActivity
import android.app.Activity.OVERRIDE_TRANSITION_CLOSE
import com.shineofeidos.mockapiproject.R
import com.shineofeidos.mockapiproject.ui.customview.EventDispatchChildView
import com.shineofeidos.mockapiproject.ui.customview.EventDispatchParentLayout
import com.shineofeidos.mockapiproject.ui.customview.SmartHorizontalScrollView
import java.util.ArrayDeque

class TouchEventLegacyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_event_legacy)

        val parentCountView = findViewById<TextView>(R.id.parentCount)
        val childCountView = findViewById<TextView>(R.id.childCount)
        val logView = findViewById<TextView>(R.id.eventLogs)
        val clearButton = findViewById<Button>(R.id.clearLogs)
        val parentLayout = findViewById<EventDispatchParentLayout>(R.id.eventParent)
        val childView = findViewById<EventDispatchChildView>(R.id.eventChild)
        val smartSwitch = findViewById<Switch>(R.id.switchSmart)
        val parentScrollState = findViewById<TextView>(R.id.parentScrollState)
        val gestureState = findViewById<TextView>(R.id.gestureState)
        val horizontalScroll = findViewById<SmartHorizontalScrollView>(R.id.horizontalScroll)
        val backButton = findViewById<Button>(R.id.backButton)
        val guideButton = findViewById<Button>(R.id.guideButton)

        val logs = ArrayDeque<String>()
        val maxLogs = 20
        var parentPressCount = 0
        var childPressCount = 0

        fun actionLabel(action: Int): String = when (action) {
            MotionEvent.ACTION_DOWN -> "DOWN"
            MotionEvent.ACTION_MOVE -> "MOVE"
            MotionEvent.ACTION_UP -> "UP"
            MotionEvent.ACTION_CANCEL -> "CANCEL"
            else -> action.toString()
        }

        fun appendLog(source: String, stage: String, action: Int) {
            val timestamp = System.currentTimeMillis() % 100000
            logs.addFirst("${timestamp}ms  $source $stage ${actionLabel(action)}")
            while (logs.size > maxLogs) {
                logs.removeLast()
            }
            logView.text = logs.joinToString("\n")
        }

        fun updateCounts() {
            parentCountView.text = "父容器触发次数: $parentPressCount"
            childCountView.text = "子元素 onClick 次数: $childPressCount"
        }

        parentLayout.onEvent = { source, stage, action ->
            appendLog(source, stage, action)
            if (stage == "dispatch" && action == MotionEvent.ACTION_DOWN) {
                parentPressCount++
                updateCounts()
            }
        }

        childView.onEvent = { source, stage, action ->
            appendLog(source, stage, action)
        }

        childView.setOnClickListener {
            childPressCount++
            updateCounts()
            appendLog("子元素", "onClick", MotionEvent.ACTION_UP)
        }

        clearButton.setOnClickListener {
            logs.clear()
            logView.text = "暂无事件"
        }

        smartSwitch.setOnCheckedChangeListener { _, isChecked ->
            horizontalScroll.smartEnabled = isChecked
            if (!isChecked) {
                parentScrollState.text = "父级滚动: 已启用"
                gestureState.text = "当前手势: 未检测"
            }
        }

        horizontalScroll.onGestureStateChanged = { direction, parentEnabled ->
            gestureState.text = "当前手势: $direction"
            parentScrollState.text = "父级滚动: ${if (parentEnabled) "已启用" else "已禁用"}"
        }

        backButton.setOnClickListener { finish() }

        guideButton.setOnClickListener {
            // 在传统 Activity 中，我们也跳转到 Compose 的 Markdown 页面
            // 这里为了演示方便，直接利用已经注册好的 MainActivity 导航
            // 但因为这是两个 Activity，最简单的方式是直接启动一个包含 Compose 的 Activity 或者
            // 这里我们先做一个简单的提示，或者你可以要求我新建一个展示 MD 的 Activity
            android.widget.Toast.makeText(this, "请在 Compose 版本页面查看指南", android.widget.Toast.LENGTH_SHORT).show()
        }

        horizontalScroll.smartEnabled = smartSwitch.isChecked
        parentScrollState.text = "父级滚动: 已启用"
        gestureState.text = "当前手势: 未检测"
        updateCounts()
        logView.text = "暂无事件"
    }

    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.fade_in, R.anim.fade_out)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
