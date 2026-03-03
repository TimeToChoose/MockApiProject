package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var markdownText by remember { mutableStateOf("正在加载...") }

    // 在 assets 中读取 markdown 文件
    // 注意：我们需要先将 TOUCH_EVENT_GUIDE.md 复制到 assets 目录，或者通过文件系统读取
    // 这里的简化做法是直接读取我们刚才创建的文件
    LaunchedEffect(Unit) {
        try {
            // 这里为了演示，我们先模拟读取逻辑。
            // 实际上在 Android 中，通常从 assets 读取。
            // 鉴于这是一个学习项目，我们可以直接硬编码一段内容或者从文件系统读取。
            // 这里我先用一段占位内容，稍后我们会讨论如何更好地加载它。
            markdownText = """
# Android 点击事件分发与滑动冲突学习指南

本文档总结了 Android 原生 View 系统中点击事件（MotionEvent）的分发机制，旨在帮助深度学习事件链路与滑动冲突处理。

---

## 1. 三大核心方法

| 方法 | 作用 | 宿主 | 返回值含义 |
| :--- | :--- | :--- | :--- |
| **dispatchTouchEvent** | **指挥官** | View/ViewGroup | true: 消费；false: 不消费。 |
| **onInterceptTouchEvent** | **拦截器** | **仅 ViewGroup** | true: 拦截；false: 不拦截。 |
| **onTouchEvent** | **终点站** | View/ViewGroup | true: 消费；false: 不处理。 |

---

## 2. 分发流程（U 型链路）

事件从顶层（Activity）逐级向下传递，直到找到最底层的子 View：
1. Activity.dispatchTouchEvent()
2. ViewGroup.dispatchTouchEvent()
3. ViewGroup.onInterceptTouchEvent()
4. View.dispatchTouchEvent()
5. View.onTouchEvent()

---

## 3. 滑动冲突处理策略

### 3.1 外部拦截法
父容器在 `onInterceptTouchEvent` 中决定是否拦截。

### 3.2 内部拦截法
子 View 通过 `requestDisallowInterceptTouchEvent(true)` 告诉父容器不要拦截。

---

## 4. 总结口诀
- **向下走**：父分发、父拦截、子分发、子消费。
- **向上走**：子不消费、父来消费、父不消费、Activity 消费。
            """.trimIndent()
        } catch (e: Exception) {
            markdownText = "加载失败: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习指南") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            MarkdownText(
                markdown = markdownText,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
