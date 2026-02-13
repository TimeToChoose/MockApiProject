package com.shineofeidos.mockapiproject.ui.customview

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shineofeidos.mockapiproject.ui.theme.MockApiProjectTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * =========================================================================================
 * Compose 版：雷达能力图 (ComposeSkillRadar)
 * =========================================================================================
 *
 * 对比传统 View 的区别：
 * 1. 无需继承 View，直接使用 Canvas Composable
 * 2. 无需 onMeasure，大小由 Modifier 控制
 * 3. 无需 invalidate，数据变化会自动重组 (Recomposition)
 * 4. API 差异：使用 DrawScope 进行绘制 (drawLine, drawPath)
 */
@Composable
fun ComposeSkillRadar(
    modifier: Modifier = Modifier
) {
    // 数据 (在实际开发中通常作为参数传入)
    val abilities = remember { listOf("架构", "源码", "算法", "Kotlin", "性能", "UI") }
    val data = remember { floatArrayOf(80f, 90f, 60f, 95f, 75f, 85f) }
    val count = abilities.size

    // 颜色定义 (Compose Color)
    val gridColor = Color.Gray
    val dataColor = Color(0xFF4CAF50)
    val textColor = Color.Black

    Canvas(modifier = modifier) {
        // 1. 计算中心点和半径
        // DrawScope 提供了 size 属性，直接获取当前 Canvas 的宽高
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radius = (min(size.width, size.height) / 2f) * 0.8f
        val angleStep = 360f / count

        // 2. 绘制网格 (5层)
        for (i in 1..5) {
            val currentRadius = radius * (i / 5f)
            val path = Path()
            
            for (j in 0 until count) {
                val angle = Math.toRadians((angleStep * j).toDouble())
                val x = centerX + currentRadius * cos(angle).toFloat()
                val y = centerY + currentRadius * sin(angle).toFloat()
                
                if (j == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            path.close()
            
            // Compose 的 drawPath
            drawPath(
                path = path,
                color = gridColor,
                style = Stroke(width = 2f)
            )
        }

        // 3. 绘制连接线 (骨架)
        for (j in 0 until count) {
            val angle = Math.toRadians((angleStep * j).toDouble())
            val x = centerX + radius * cos(angle).toFloat()
            val y = centerY + radius * sin(angle).toFloat()
            
            drawLine(
                color = gridColor,
                start = Offset(centerX, centerY),
                end = Offset(x, y),
                strokeWidth = 2f
            )
        }

        // 4. 绘制数据区域
        val dataPath = Path()
        for (j in 0 until count) {
            val value = data[j] / 100f
            val currentRadius = radius * value
            val angle = Math.toRadians((angleStep * j).toDouble())
            val x = centerX + currentRadius * cos(angle).toFloat()
            val y = centerY + currentRadius * sin(angle).toFloat()
            
            if (j == 0) {
                dataPath.moveTo(x, y)
            } else {
                dataPath.lineTo(x, y)
            }
            
            // 绘制小圆点
            drawCircle(
                color = dataColor,
                radius = 6f,
                center = Offset(x, y)
            )
        }
        dataPath.close()
        
        // 绘制填充
        drawPath(
            path = dataPath,
            color = dataColor.copy(alpha = 0.5f) // Compose 处理透明度很方便
        )
        // 绘制边框
        drawPath(
            path = dataPath,
            color = dataColor,
            style = Stroke(width = 4f)
        )

        // 5. 绘制文字
        // Compose 的 Canvas 暂时没有直接的 drawText API (TextMeasurer 是较新的 API)
        // 这里为了演示与原生 Canvas 的互操作性，我们使用 drawContext.canvas.nativeCanvas
        drawContext.canvas.nativeCanvas.apply {
            val textPaint = Paint().apply {
                color = textColor.toArgb()
                textSize = 40f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            
            val fontMetrics = textPaint.fontMetrics
            val fontHeight = fontMetrics.descent - fontMetrics.ascent

            for (j in 0 until count) {
                val angle = Math.toRadians((angleStep * j).toDouble())
                // 文字距离再远一点
                val textRadius = radius + 40
                val x = centerX + textRadius * cos(angle).toFloat()
                val y = centerY + textRadius * sin(angle).toFloat()
                
                drawText(abilities[j], x, y + fontHeight / 4, textPaint)
            }
        }
    }
}

/**
 * =========================================================================================
 * Compose Preview：预览功能
 * =========================================================================================
 * 
 * 优势：
 * 1. 实时渲染：修改代码后右侧窗口立即更新，无需运行到模拟器。
 * 2. 稳定性：不会因为 remember 块的逻辑修改而导致 App 闪退。
 * 3. 多样性：可以同时定义多个 Preview，查看不同主题、不同数据下的 UI。
 */
@Preview(showBackground = true, name = "浅色模式")
@Composable
fun PreviewComposeSkillRadar() {
    MockApiProjectTheme {
        ComposeSkillRadar(
            modifier = Modifier.size(300.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "深色模式")
@Composable
fun PreviewComposeSkillRadarDark() {
    MockApiProjectTheme {
        ComposeSkillRadar(
            modifier = Modifier.size(300.dp)
        )
    }
}
