package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 事件总线学习主页面
 * 包含Handler、自研BusEvent、EventBus三个学习模块
 */
@Composable
fun EventBusMainScreen(
    onNavigateToHandler: () -> Unit,
    onNavigateToCustomBusEvent: () -> Unit,
    onNavigateToEventBusComparison: () -> Unit,
    onNavigateToEventBusAdvanced: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBack) {
                Text("返回")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "事件总线学习",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 学习路径说明
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "学习路径",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "建议按以下顺序学习：\n\n" +
                            "1. Handler学习 - 理解Android消息机制基础\n" +
                            "2. 自研BusEvent - 基于Handler实现简单事件总线\n" +
                            "3. EventBus对比 - 对比自研实现和开源库的差异\n" +
                            "4. EventBus高级功能 - 了解生产级事件总线的特性",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 模块1：Handler学习
        LearningModuleCard(
            title = "1. Handler 学习",
            description = "理解Android消息机制的核心组件",
            details = "• Looper、Handler、MessageQueue的关系\n" +
                    "• 消息的发送和处理流程\n" +
                    "• 主线程和后台线程的切换\n" +
                    "• 延迟消息和重复消息",
            onClick = onNavigateToHandler
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 模块2：自研BusEvent
        LearningModuleCard(
            title = "2. 自研 BusEvent",
            description = "基于Handler实现轻量级事件总线",
            details = "• 单例模式管理事件总线\n" +
                    "• HandlerThread创建后台线程\n" +
                    "• 主线程/后台线程事件分发\n" +
                    "• 延迟事件支持",
            onClick = onNavigateToCustomBusEvent
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 模块3：EventBus对比
        LearningModuleCard(
            title = "3. EventBus 对比学习",
            description = "对比自研实现与GreenRobot EventBus",
            details = "• 使用方式对比\n" +
                    "• 线程模式对比\n" +
                    "• 功能特性对比\n" +
                    "• 性能和代码复杂度对比",
            onClick = onNavigateToEventBusComparison
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 模块4：EventBus高级功能
        LearningModuleCard(
            title = "4. EventBus 高级功能",
            description = "生产级事件总线的高级特性",
            details = "• 粘性事件（Sticky Events）\n" +
                    "• 多种线程模式（POSTING/MAIN/BACKGROUND/ASYNC）\n" +
                    "• 事件优先级\n" +
                    "• 订阅者索引优化",
            onClick = onNavigateToEventBusAdvanced
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 核心概念总结
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "核心概念",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "• 事件总线模式：发布-订阅模式，解耦发送者和接收者\n\n" +
                            "• Handler机制：Android消息处理的基础，通过Looper实现线程间通信\n\n" +
                            "• 线程切换：在主线程更新UI，在后台线程处理耗时操作\n\n" +
                            "• 粘性事件：事件发送后注册的订阅者也能收到，适合状态同步",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun LearningModuleCard(
    title: String,
    description: String,
    details: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("开始学习")
            }
        }
    }
}
