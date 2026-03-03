package com.shineofeidos.mockapiproject.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToNetwork: () -> Unit,
    onNavigateToCustomView: () -> Unit,
    onNavigateToMemoryLeak: () -> Unit,
    onNavigateToTouchEvent: () -> Unit,
    onNavigateToTouchEventLegacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    
    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime
            Toast.makeText(context, "再按一次退出应用", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Android 进阶学习",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LearningCard(
            title = "网络架构 (Network)",
            description = "Retrofit, OkHttp, MVVM, Repository",
            onClick = onNavigateToNetwork
        )
        
        LearningCard(
            title = "自定义 View (Custom View)",
            description = "Measure, Draw, Bezier, Xfermode",
            onClick = onNavigateToCustomView
        )

        LearningCard(
            title = "内存优化 (Memory Leak)",
            description = "单例泄漏案例, Profiler 阶梯图分析",
            onClick = onNavigateToMemoryLeak
        )

        LearningCard(
            title = "触摸分发与滑动冲突 (Touch)",
            description = "事件分发顺序, 滑动冲突与拦截策略",
            onClick = onNavigateToTouchEvent
        )

        LearningCard(
            title = "触摸分发与滑动冲突 (Legacy View)",
            description = "View 事件分发, 滑动冲突与拦截策略",
            onClick = onNavigateToTouchEventLegacy
        )
    }
}

@Composable
fun LearningCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
