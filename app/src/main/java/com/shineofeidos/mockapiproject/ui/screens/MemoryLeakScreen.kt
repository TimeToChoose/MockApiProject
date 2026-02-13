package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryLeakScreen(
    onBack: () -> Unit
) {
    var leakCount by remember { mutableIntStateOf(0) }

    // 每次进入此页面时自动触发一次泄漏
    LaunchedEffect(Unit) {
        leakCount++
        LeakingManager.registerListener { 
            // 模拟回调逻辑
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("内存泄漏案例演示") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🚨 警告：此页面会导致内存泄漏",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "原理：单例持有 Activity 内部匿名类的引用，且没有在生命周期结束时解注册。每次进入该页面都会申请 100MB 物理内存。",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "当前页面已触发泄漏次数",
                style = MaterialTheme.typography.labelLarge
            )
            
            Text(
                text = "$leakCount",
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "累计泄漏约: ${leakCount * 100} MB",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("返回并再次进入（制造更多泄漏）")
            }

            Text(
                text = "提示：请配合 Android Studio Profiler 观察 Java Heap 的阶梯式上涨",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
