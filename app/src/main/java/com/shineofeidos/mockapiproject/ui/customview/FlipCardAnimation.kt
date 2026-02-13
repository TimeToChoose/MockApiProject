package com.shineofeidos.mockapiproject.ui.customview

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shineofeidos.mockapiproject.ui.theme.MockApiProjectTheme

/**
 * =========================================================================================
 * 动画进阶：3D 翻牌效果 (FlipCardAnimation)
 * =========================================================================================
 * 
 * 技术点：
 * 1. animateFloatAsState: 声明式动画，自动处理数值过渡。
 * 2. graphicsLayer: 提供 3D 转换（rotationY）和透视效果（cameraDistance）。
 * 3. 逻辑判断：根据旋转角度动态切换卡片正面和反面的内容。
 */
@Composable
fun FlipCardAnimation(
    modifier: Modifier = Modifier,
    frontContent: @Composable () -> Unit = { DefaultFront() },
    backContent: @Composable () -> Unit = { DefaultBack() }
) {
    var rotated by remember { mutableStateOf(false) }

    // 使用 animateFloatAsState 创建旋转角度动画
    // 当 rotated 改变时，rotation 会在 0f 和 180f 之间平滑过渡
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
                // 1. 设置旋转角度
                rotationY = rotation
                // 2. 设置相机距离（透视感），数值越大透视感越弱
                cameraDistance = 12f * density
            }
            .clickable { rotated = !rotated },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 根据旋转角度决定显示正面还是反面
            // 当旋转超过 90 度时，翻转内容并镜像回来，防止文字变成反的
            if (rotation <= 90f) {
                frontContent()
            } else {
                Box(
                    Modifier.graphicsLayer {
                        rotationY = 180f // 抵消父级的 180 度旋转，使文字正向显示
                    }
                ) {
                    backContent()
                }
            }
        }
    }
}

@Composable
fun DefaultFront() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "🎴", fontSize = 50.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "点击翻转卡片", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DefaultBack() {
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

@Preview(showBackground = true)
@Composable
fun PreviewFlipCard() {
    MockApiProjectTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            FlipCardAnimation()
        }
    }
}