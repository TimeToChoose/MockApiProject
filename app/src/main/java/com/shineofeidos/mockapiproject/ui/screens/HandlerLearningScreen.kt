package com.shineofeidos.mockapiproject.ui.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class HandlerLearningScreen {
    private val TAG = "HandlerLearning"
    
    // 主线程Handler
    private val mainHandler = Handler(Looper.getMainLooper())
    
    // 消息ID常量
    private companion object {
        const val MSG_SIMPLE = 1
        const val MSG_WITH_DATA = 2
        const val MSG_DELAYED = 3
        const val MSG_REPEAT = 4
    }
    
    // 状态变量
    private var messageLog by mutableStateOf("等待消息...")
    private var counter by mutableStateOf(0)
    private var isRunning by mutableStateOf(false)

    // 传统写法
/*    private val han = object: Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            TODO("Not yet implemented")
        }
    }*/
    // 处理消息的回调
    private val handlerCallback = Handler.Callback { msg ->
        when (msg.what) {
            MSG_SIMPLE -> {
                messageLog = "收到简单消息: ${msg.what}"
                Log.d(TAG, "收到简单消息: ${msg.what}")
            }
            MSG_WITH_DATA -> {
                val data = msg.data.getString("message") ?: "无数据"
                messageLog = "收到带数据消息: $data"
                Log.d(TAG, "收到带数据消息: $data")
            }
            MSG_DELAYED -> {
                messageLog = "收到延迟消息: ${msg.what}"
                Log.d(TAG, "收到延迟消息: ${msg.what}")
            }
            MSG_REPEAT -> {
                counter++
                messageLog = "重复消息计数: $counter"
                Log.d(TAG, "重复消息计数: $counter")
                if (isRunning) {
                    mainHandler.sendEmptyMessageDelayed(MSG_REPEAT, 1000)
                }
            }
        }
        true
    }

    // 创建Handler - 使用显式 Looper 避免弃用警告
    private val handler = Handler(Looper.getMainLooper(), handlerCallback)
    
    @Composable
    fun Content() {
        DisposableEffect(Unit) {
            onDispose {
                handler.removeCallbacksAndMessages(null)
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
                text = "Handler 学习",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // 消息显示区域
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = messageLog,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 基础消息发送
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
                        text = "基础消息",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            handler.sendEmptyMessage(MSG_SIMPLE)
                        }) {
                            Text("发送简单消息")
                        }
                        Button(onClick = {
                            val msg = Message.obtain(handler, MSG_WITH_DATA)
                            msg.data.putString("message", "Hello Handler!")
                            handler.sendMessage(msg)
                        }) {
                            Text("发送带数据消息")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 延迟消息
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
                        text = "延迟消息",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            handler.sendEmptyMessageDelayed(MSG_DELAYED, 2000)
                            messageLog = "已发送2秒延迟消息..."
                        }) {
                            Text("发送2秒延迟消息")
                        }
                        Button(onClick = {
                            handler.removeMessages(MSG_DELAYED)
                            messageLog = "已取消延迟消息"
                        }) {
                            Text("取消延迟消息")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 重复消息
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
                        text = "重复消息 (计时器)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "计数: $counter",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            isRunning = true
                            counter = 0
                            handler.sendEmptyMessage(MSG_REPEAT)
                        }) {
                            Text("开始计时")
                        }
                        Button(onClick = {
                            isRunning = false
                            handler.removeMessages(MSG_REPEAT)
                            messageLog = "计时已停止"
                        }) {
                            Text("停止计时")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Handler原理说明
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
                        text = "Handler 原理说明",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "1. Handler: 用于发送和处理消息\n" +
                                "2. Message: 消息对象，包含what、data等字段\n" +
                                "3. Looper: 消息循环器，从MessageQueue中取出消息\n" +
                                "4. MessageQueue: 消息队列，存储待处理的消息\n\n" +
                                "工作流程:\n" +
                                "1. 发送消息到MessageQueue\n" +
                                "2. Looper从队列中取出消息\n" +
                                "3. Handler处理消息\n" +
                                "4. 根据消息类型执行相应操作\n\n" +
                                "BusEvent原理:\n" +
                                "BusEvent基于Handler实现，使用HandlerThread创建后台线程\n" +
                                "通过Handler发送事件，根据isMainThread()决定在主线程或后台线程处理",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 常见问题解答
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
                        text = "常见问题解答",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Q1: 为什么Handler.Callback可以用lambda简写?\n" +
                                "A: 因为Handler.Callback是函数式接口(只有一个抽象方法)，可以用lambda表达式。如果有两个抽象方法就不能用lambda了。\n\n" +
                                "Q2: handleMessage为什么会收到消息?在哪里唤起的?\n" +
                                "A: 流程: sendMessage → MessageQueue → Looper.loop() → dispatchMessage → handleMessage。Looper在后台不断循环，从队列取消息并分发。\n\n" +
                                "Q3: Looper是什么?\n" +
                                "A: Looper是消息循环器，负责不停从MessageQueue取消息并分发给Handler。主线程默认有Looper，普通线程需要手动创建。\n\n" +
                                "Q4: 主线程的Looper在哪里创建的?\n" +
                                "A: 在ActivityThread.main()方法中，系统调用Looper.prepareMainLooper()创建。这是Android系统源码，应用层看不到。\n\n" +
                                "形象比喻:\n" +
                                "• MessageQueue = 快递传送带(存放待处理的快递)\n" +
                                "• Looper = 分拣员(不停地从传送带取快递)\n" +
                                "• Handler = 快递员(负责派送快递到指定地点)\n" +
                                "• Message = 快递包裹(包含数据和指令)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun HandlerLearningScreenContent() {
    val screen = remember { HandlerLearningScreen() }
    screen.Content()
}