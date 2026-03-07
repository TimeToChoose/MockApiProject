package com.shineofeidos.mockapiproject.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shineofeidos.mockapiproject.event.AppEvent
import com.shineofeidos.mockapiproject.event.Bus
import com.shineofeidos.mockapiproject.event.GreenRobotEvent
import com.shineofeidos.mockapiproject.event.IBusListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class EventBusComparisonScreen : IBusListener {
    private val TAG = "EventBusComparison"
    private var customBusEventReceived by mutableStateOf("")
    private var eventBusEventReceived by mutableStateOf("")

    // 自研BusEvent的回调
    override fun onBusEvent(event: com.shineofeidos.mockapiproject.event.BusEvent) {
        if (event is AppEvent) {
            val message = "Custom BusEvent: ${event.type?.name ?: "Unknown"}, success: ${event.isSuccess}"
            Log.d(TAG, message)
            customBusEventReceived = message
        }
    }

    // EventBus的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: GreenRobotEvent) {
        val message = "EventBus: ${event.type?.name ?: "Unknown"}, success: ${event.isSuccess}, message: ${event.message}"
        Log.d(TAG, message)
        eventBusEventReceived = message
    }

    @Composable
    fun Content() {
        val context = LocalContext.current

        // 注册监听器
        DisposableEffect(Unit) {
            // 注册自研BusEvent
            Bus.getInstance().addListener(this@EventBusComparisonScreen)
            // 注册EventBus
            EventBus.getDefault().register(this@EventBusComparisonScreen)
            onDispose {
                // 取消注册自研BusEvent
                Bus.getInstance().removeListener(this@EventBusComparisonScreen)
                // 取消注册EventBus
                EventBus.getDefault().unregister(this@EventBusComparisonScreen)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Event Bus 对比学习",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // 自研BusEvent部分
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
                        text = "自研 BusEvent",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = customBusEventReceived.ifEmpty { "No events received yet" },
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            val event = AppEvent(true, AppEvent.EventTypeEnum.DEMO_LOGIN)
                            event.send()
                        }) {
                            Text("发送登录事件")
                        }
                        Button(onClick = {
                            val event = AppEvent(AppEvent.EventTypeEnum.DEMO_LOAD_DATA)
                            event.sendOnThread()
                        }) {
                            Text("发送加载事件 (线程)")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EventBus部分
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
                        text = "EventBus (GreenRobot)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = eventBusEventReceived.ifEmpty { "No events received yet" },
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            val event = GreenRobotEvent(true, GreenRobotEvent.EventType.LOGIN)
                            event.post()
                        }) {
                            Text("发送登录事件")
                        }
                        Button(onClick = {
                            val event = GreenRobotEvent("Hello from EventBus")
                            event.postSticky()
                        }) {
                            Text("发送粘性事件")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 对比说明
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
                        text = "对比说明",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Custom BusEvent:\n" +
                                "- 基于Handler实现\n" +
                                "- 支持主线程和后台线程切换\n" +
                                "- 支持延迟事件\n" +
                                "- 实现简单，代码量少\n" +
                                "- 功能相对基础\n\n" +
                                "EventBus (GreenRobot):\n" +
                                "- 基于发布/订阅模式\n" +
                                "- 支持多种线程模式\n" +
                                "- 支持粘性事件\n" +
                                "- 支持事件优先级\n" +
                                "- 功能丰富，性能优化\n" +
                                "- 社区活跃，文档完善",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EventBusComparisonScreenContent() {
    val screen = remember { EventBusComparisonScreen() }
    screen.Content()
}