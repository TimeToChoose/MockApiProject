package com.shineofeidos.mockapiproject.ui.customview

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * =========================================================================================
 * 纯 Compose 版波浪进度球 (ComposeWaveBall)
 * =========================================================================================
 * 
 * 知识点对比：
 * 1. 动画：使用 rememberInfiniteTransition 替代 ValueAnimator。
 * 2. 裁剪：使用 clipPath 替代 Xfermode (代码量减少 70%)。
 * 3. 绘制：使用 DrawScope 的 drawPath 替代 Canvas.drawPath。
 */
@Composable
fun ComposeWaveBall(
    progress: Float, // 0f - 100f
    modifier: Modifier = Modifier,
    waveColor: Color = MaterialTheme.colorScheme.primaryContainer,
    velocity: Int = 2000 // 动画周期
) {
    // 1. 创建无限循环的动画状态（控制波浪左右移动）
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(velocity, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveOffset"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val radius = width / 2f
            
            // 2. 创建圆形路径用于裁剪
            val circlePath = Path().apply {
                addOval(androidx.compose.ui.geometry.Rect(0f, 0f, width, height))
            }

            // 3. 在圆形区域内绘制
            clipPath(circlePath) {
                // 绘制背景
                drawCircle(color = waveColor.copy(alpha = 0.2f), radius = radius)

                // 4. 绘制波浪路径
                val wavePath = Path()
                val waveWidth = width
                val waveHeight = 20.dp.toPx() // 波浪高度
                val waterLevel = height * (1f - progress / 100f) // 根据进度计算水位

                wavePath.moveTo(-waveWidth + (waveOffset * waveWidth), waterLevel)
                
                // 绘制简单的正弦波 (这里用贝塞尔曲线模拟)
                var x = -waveWidth
                while (x < width + waveWidth) {
                    wavePath.relativeQuadraticTo(
                        waveWidth / 4f, -waveHeight,
                        waveWidth / 2f, 0f
                    )
                    wavePath.relativeQuadraticTo(
                        waveWidth / 4f, waveHeight,
                        waveWidth / 2f, 0f
                    )
                    x += waveWidth
                }
                
                // 封闭路径
                wavePath.lineTo(width, height)
                wavePath.lineTo(0f, height)
                wavePath.close()

                drawPath(path = wavePath, color = waveColor)
            }
        }

        // 5. 绘制文字 (Compose 的 Text 组件比 Canvas.drawText 好用太多)
        Text(
            text = "${progress.toInt()}%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComposeWaveBall() {
    MaterialTheme {
        Box(Modifier.size(300.dp), contentAlignment = Alignment.Center) {
            ComposeWaveBall(progress = 65f, modifier = Modifier.size(200.dp))
        }
    }
}
