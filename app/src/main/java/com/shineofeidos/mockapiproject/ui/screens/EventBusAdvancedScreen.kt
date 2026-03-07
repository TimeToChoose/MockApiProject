package com.shineofeidos.mockapiproject.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shineofeidos.mockapiproject.event.AppEvent
import com.shineofeidos.mockapiproject.event.Bus
import com.shineofeidos.mockapiproject.event.GreenRobotEvent
import com.shineofeidos.mockapiproject.event.IBusListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * EventBus高级功能演示页面
 * 展示EventBus相比自研BusEvent的优势功能
 */
class EventBusAdvancedScreen : IBusListener {
    private val TAG = "EventBusAdvanced"
    
    // 状态变量
    private var logText by mutableStateOf("点击按钮查看效果...\n")
    private var stickyEventReceived by mutableStateOf(false)
    private var isRegistered by mutableStateOf(false)
    
    // 自研BusEvent回调
    override fun onBusEvent(event: com.shineofeidos.mockapiproject.event.BusEvent) {
        if (event is AppEvent) {
            logText += "[自研BusEvent] 收到: ${event.type?.name}\n"
        }
    }
    
    // EventBus普通订阅
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNormalEvent(event: GreenRobotEvent) {
        logText += "[EventBus-普通] 收到: ${event.type?.name}, 线程: ${Thread.currentThread().name}\n"
    }
    
    // EventBus粘性订阅 - 关键特性！
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onStickyEvent(event: GreenRobotEvent) {
        if (!stickyEventReceived) {
            logText += "[EventBus-粘性] 收到历史事件: ${event.type?.name}\n"
            stickyEventReceived = true
        }
    }
    
    // 不同线程模式的订阅
    @Subscribe(threadMode = ThreadMode.POSTING)
    fun onPostingThread(event: GreenRobotEvent) {
        Log.d(TAG, "POSTING线程: ${Thread.currentThread().name}")
    }
    
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onBackgroundThread(event: GreenRobotEvent) {
        Log.d(TAG, "BACKGROUND线程: ${Thread.currentThread().name}")
    }
    
    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onAsyncThread(event: GreenRobotEvent) {
        Log.d(TAG, "ASYNC线程: ${Thread.currentThread().name}")
    }

    @Composable
    fun Content() {
        DisposableEffect(Unit) {
            onDispose {
                if (isRegistered) {
                    EventBus.getDefault().unregister(this@EventBusAdvancedScreen)
                }
                Bus.getInstance().removeListener(this@EventBusAdvancedScreen)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EventBus 高级功能",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 日志显示区域
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = logText,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. 粘性事件演示
            FeatureCard(
                title = "1. 粘性事件 (Sticky Events)",
                description = "事件发送后注册的订阅者也能收到"
            ) {
                Column {
                    Text(
                        text = "演示步骤:\n1. 先发送粘性事件\n2. 再注册订阅者\n3. 订阅者仍能收到之前的事件",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            val event = GreenRobotEvent("粘性事件内容")
                            event.postSticky()
                            logText += "[发送] 粘性事件已发送\n"
                        }) {
                            Text("1. 发送粘性事件")
                        }
                        Button(onClick = {
                            if (!isRegistered) {
                                EventBus.getDefault().register(this@EventBusAdvancedScreen)
                                isRegistered = true
                                logText += "[注册] 订阅者已注册\n"
                            }
                        }) {
                            Text("2. 注册订阅者")
                        }
                    }
                    Button(
                        onClick = {
                            stickyEventReceived = false
                            logText = ""
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("重置")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 线程模式演示
            FeatureCard(
                title = "2. 多种线程模式",
                description = "比自研BusEvent更灵活的线程控制"
            ) {
                Column {
                    Text(
                        text = "POSTING: 发送线程执行\nMAIN: 主线程执行\nBACKGROUND: 后台单线程\nASYNC: 线程池并发执行",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(onClick = {
                        if (!isRegistered) {
                            EventBus.getDefault().register(this@EventBusAdvancedScreen)
                            isRegistered = true
                        }
                        Thread {
                            val event = GreenRobotEvent(GreenRobotEvent.EventType.LOAD_DATA)
                            event.post()
                            logText += "[发送] 从后台线程发送事件\n"
                        }.start()
                    }) {
                        Text("测试线程模式")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 对比自研BusEvent的局限
            FeatureCard(
                title = "3. 自研BusEvent的局限",
                description = "这些功能自研BusEvent不支持"
            ) {
                Column {
                    Text(
                        text = "❌ 不支持粘性事件\n" +
                                "   - 事件发送时无监听器，事件丢失\n\n" +
                                "❌ 不支持事件优先级\n" +
                                "   - 无法控制多个监听器的执行顺序\n\n" +
                                "❌ 线程模式简单\n" +
                                "   - 只有主线程/后台线程两种选择\n\n" +
                                "❌ 无订阅者索引\n" +
                                "   - 每次都要遍历所有监听器",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = {
                            // 演示自研BusEvent的局限
                            val event = AppEvent(AppEvent.EventTypeEnum.DEMO_LOGIN)
                            event.send()
                            logText += "[自研BusEvent] 事件已发送\n"
                            logText += "⚠️ 如果此时无监听器，事件就丢失了\n"
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("测试自研BusEvent")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. 实际应用场景
            FeatureCard(
                title = "4. 实际应用场景",
                description = "为什么需要这些功能？"
            ) {
                Text(
                    text = "粘性事件场景:\n" +
                            "• 登录状态同步 - 页面跳转后仍能收到登录事件\n" +
                            "• 配置信息缓存 - 应用配置在启动时加载，各页面都能获取\n\n" +
                            "线程模式场景:\n" +
                            "• POSTING - 不需要线程切换，性能最好\n" +
                            "• MAIN - UI更新必须在主线程\n" +
                            "• BACKGROUND - 数据库操作，顺序执行避免并发问题\n" +
                            "• ASYNC - 网络请求，并发执行提高效率\n\n" +
                            "事件优先级场景:\n" +
                            "• 权限检查(高优先级)先于业务处理(低优先级)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun EventBusAdvancedScreenContent() {
    val screen = remember { EventBusAdvancedScreen() }
    screen.Content()
}