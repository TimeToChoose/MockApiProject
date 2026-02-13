package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.shineofeidos.mockapiproject.ui.customview.ComposeSkillRadar
import com.shineofeidos.mockapiproject.ui.customview.ComposeWaveBall
import com.shineofeidos.mockapiproject.ui.customview.SkillRadarView
import com.shineofeidos.mockapiproject.ui.customview.WaveProgressView

@Composable
fun CustomViewLearningScreen(
    modifier: Modifier = Modifier
) {
    // 定义一个 Compose 状态来管理进度
    var waveProgress by remember { mutableFloatStateOf(50f) }
    
    // 演示：滚动状态
    val scrollState = rememberScrollState()

    // ✅ 优化手段 1：使用 derivedStateOf 进行“降频”
    // 我们不再监听每一像素的变化，而是只关心“滚动是否超过了 500 像素”
    // 只有这个布尔值发生变化时，相关的 UI 才会重组
    val isScrolledPastLimit by remember {
        derivedStateOf { scrollState.value > 500 }
    }

    // ✅ 优化手段 2：局部重组（Localization）
    HeavyRecompositionReporter(isScrolledPastLimit)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("自定义 View 进阶学习", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        // 1. 基础进阶：雷达图 (Native View)
        Text("1. 传统 View 实现 (SkillRadarView)", style = MaterialTheme.typography.titleMedium)
        Text(
            "继承 View, 重写 onMeasure/onDraw",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        AndroidView(
            modifier = Modifier.size(300.dp),
            factory = { context ->
                SkillRadarView(context)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
        
        // 1.5 Compose 版雷达图
        Text("1.5 Compose 实现 (ComposeSkillRadar)", style = MaterialTheme.typography.titleMedium)
        Text(
            "声明式 UI, 无需 invalidate, DrawScope API",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        ComposeSkillRadar(
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(32.dp))

        // 2. 高级进阶：波浪进度球
        Text("2. 贝塞尔曲线与图层混合 (WaveProgressView)", style = MaterialTheme.typography.titleMedium)
        Text(
            "知识点：Bezier 曲线, PorterDuffXfermode, ValueAnimator, 属性动画",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        AndroidView(
            modifier = Modifier.size(200.dp),
            factory = { context ->
                WaveProgressView(context)
            },
            update = { view ->
                // 核心：当 waveProgress 改变时，Compose 会自动重新运行这个 update 块
                // 我们在这里把新进度设置给传统的 View
                view.setProgress(waveProgress.toInt())
            },
            onRelease = { view ->
                // 当 Composable 销毁时，确保停止动画释放资源
                view.stopAnimation()
                println("WaveProgressView: 动画已在 onRelease 中安全停止")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        // 添加一个 Slider 来控制进度
        Text(text = "控制进度: ${waveProgress.toInt()}%")
        Slider(
            value = waveProgress,
            onValueChange = { waveProgress = it },
            valueRange = 0f..100f,
            modifier = Modifier.width(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2.5 纯 Compose 实现波浪球
        Text("2.5 纯 Compose 实现 (ComposeWaveBall)", style = MaterialTheme.typography.titleMedium)
        Text(
            "Canvas API, InfiniteTransition, clipPath",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        ComposeWaveBall(
            progress = waveProgress,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(32.dp))

        // 3. 动画进阶：3D 翻牌效果
        Text("3. 3D 翻牌动画 (FlipCardAnimation)", style = MaterialTheme.typography.titleMedium)
        Text(
            "知识点：animateFloatAsState, graphicsLayer, 3D 旋转",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        FlipCardAnimation(
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HeavyRecompositionReporter(isLimitReached: Boolean) {
    // 只有当 isLimitReached 这个布尔值从 true 变 false，或者 false 变 true 时
    // 这个函数才会运行。在滚动的几千像素中，这种情况只会发生两次！
    println("🚀 局部重组触发！当前限制状态: $isLimitReached")
    
    repeat(2000000) { 
        Math.hypot(Math.random(), Math.random()) 
    }
    
    Text(
        text = if (isLimitReached) "已超过 500px 限制" else "还在 500px 以内",
        color = if (isLimitReached) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(8.dp)
    )
}

/**
 * 3D 翻牌组件
 */
@Composable
fun FlipCardAnimation(
    modifier: Modifier = Modifier
) {
    var rotated by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "CardRotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable { rotated = !rotated },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation <= 90f) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎴", fontSize = 50.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "点击翻转卡片", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                Box(
                    Modifier.graphicsLayer {
                        rotationY = 180f 
                    }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "✨", fontSize = 50.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "恭喜你！", 
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "学会了 Compose 3D 动画",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
