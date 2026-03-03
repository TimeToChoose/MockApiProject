package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TouchEventLearningScreen(
    onBack: () -> Unit,
    onNavigateToGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eventLogs = remember { mutableStateListOf<String>() }
    val maxLogs = 20
    val appendLog: (String) -> Unit = { text ->
        val timestamp = System.currentTimeMillis() % 100000
        eventLogs.add(0, "${timestamp}ms  $text")
        if (eventLogs.size > maxLogs) {
            eventLogs.removeAt(eventLogs.lastIndex)
        }
    }

    var parentPressCount by remember { mutableIntStateOf(0) }
    var childPressCount by remember { mutableIntStateOf(0) }

    var smartInterceptEnabled by remember { mutableStateOf(true) }
    var parentScrollEnabled by remember { mutableStateOf(true) }
    var gestureDirection by remember { mutableStateOf("未检测") }

    val parentScrollState = rememberScrollState()
    val columnScrollEnabled = if (smartInterceptEnabled) parentScrollEnabled else true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("点击分发与滑动冲突") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(parentScrollState, enabled = columnScrollEnabled)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("点击事件分发", style = MaterialTheme.typography.titleLarge)
            Text(
                "观察父子容器事件顺序与 onClick 触发",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("父容器触发次数: $parentPressCount")
                    Text("子元素 onClick 次数: $childPressCount")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        when (event.type) {
                                            PointerEventType.Press -> {
                                                parentPressCount++
                                                appendLog("父容器 Initial DOWN")
                                            }
                                            PointerEventType.Release -> {
                                                appendLog("父容器 Initial UP")
                                            }
                                            else -> Unit
                                        }
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Final)
                                        when (event.type) {
                                            PointerEventType.Press -> {
                                                appendLog("父容器 Final DOWN")
                                            }
                                            PointerEventType.Release -> {
                                                appendLog("父容器 Final UP")
                                            }
                                            else -> Unit
                                        }
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "父容器",
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(140.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent(PointerEventPass.Main)
                                            when (event.type) {
                                                PointerEventType.Press -> {
                                                    appendLog("子元素 Main DOWN")
                                                }
                                                PointerEventType.Release -> {
                                                    appendLog("子元素 Main UP")
                                                }
                                                else -> Unit
                                            }
                                        }
                                    }
                                }
                                .clickable {
                                    childPressCount++
                                    appendLog("子元素 onClick")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("子元素", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { eventLogs.clear() }) {
                        Text("清空日志")
                    }
                    Button(onClick = onNavigateToGuide) {
                        Text("查看学习指南")
                    }
                }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("事件日志", style = MaterialTheme.typography.titleSmall)
                            if (eventLogs.isEmpty()) {
                                Text(
                                    text = "暂无事件",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                eventLogs.take(8).forEach { log ->
                                    Text(
                                        text = log,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Text("滑动冲突处理", style = MaterialTheme.typography.titleLarge)
            Text(
                "在竖向滚动中嵌入横向滑动区域",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("智能拦截")
                        Switch(
                            checked = smartInterceptEnabled,
                            onCheckedChange = {
                                smartInterceptEnabled = it
                                if (!it) {
                                    parentScrollEnabled = true
                                    gestureDirection = "未检测"
                                }
                            }
                        )
                    }

                    Text("父级滚动: ${if (columnScrollEnabled) "已启用" else "已禁用"}")
                    Text("当前手势: $gestureDirection")

                    val horizontalAreaModifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .pointerInput(smartInterceptEnabled) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    when (event.type) {
                                        PointerEventType.Press -> {
                                            if (smartInterceptEnabled) {
                                                parentScrollEnabled = true
                                            }
                                            gestureDirection = "检测中"
                                        }
                                        PointerEventType.Move -> {
                                            val change = event.changes.firstOrNull()
                                            if (change != null) {
                                                val dx = change.position.x - change.previousPosition.x
                                                val dy = change.position.y - change.previousPosition.y
                                                if (abs(dx) > abs(dy) && abs(dx) > 4f) {
                                                    gestureDirection = "水平"
                                                    if (smartInterceptEnabled) {
                                                        parentScrollEnabled = false
                                                    }
                                                } else if (abs(dy) > abs(dx) && abs(dy) > 4f) {
                                                    gestureDirection = "垂直"
                                                    if (smartInterceptEnabled) {
                                                        parentScrollEnabled = true
                                                    }
                                                }
                                            }
                                        }
                                        PointerEventType.Release,
                                        PointerEventType.Exit -> {
                                            parentScrollEnabled = true
                                            gestureDirection = "未检测"
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                        }

                    Box(modifier = horizontalAreaModifier) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(10) { index ->
                                Card(
                                    modifier = Modifier.size(width = 120.dp, height = 80.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("卡片 ${index + 1}")
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        "水平拖动卡片区域，观察父级滚动状态变化",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
