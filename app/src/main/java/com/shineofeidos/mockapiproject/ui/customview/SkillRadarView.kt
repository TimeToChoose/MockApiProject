package com.shineofeidos.mockapiproject.ui.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import androidx.core.graphics.toColorInt

/**
 * =========================================================================================
 * 进阶实战：雷达能力图 (SkillRadarView)
 * =========================================================================================
 *
 * 这是一个经典的自定义 View 案例，涵盖了：
 * 1. 坐标系转换 (Trigonometry)
 * 2. Path 路径绘制
 * 3. 测量逻辑 (onMeasure)
 * 4. 绘制逻辑 (onDraw)
 */
class SkillRadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 数据
    private val abilities = listOf("架构", "源码", "算法", "Kotlin", "性能", "UI")
    private var data = floatArrayOf(80f, 90f, 60f, 95f, 75f, 85f) // 百分制
    private val count = abilities.size

    // 画笔
    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 2f
    }
    
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        color = "#4CAF50".toColorInt() // 绿色
        alpha = 150 // 半透明
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    private var radius = 0f
    private var centerX = 0f
    private var centerY = 0f
    private val path = Path()

    /**
     * 1. 测量阶段 (Measure)
     * 系统会告诉我们父容器给我们的限制 (MeasureSpec)，我们需要计算出自己的大小。
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        // 处理 wrap_content 的情况 (知识点：AT_MOST)
        val desiredWidth = 600
        val desiredHeight = 600

        val finalWidth = if (widthMode == MeasureSpec.AT_MOST) min(widthSize, desiredWidth) else widthSize
        val finalHeight = if (heightMode == MeasureSpec.AT_MOST) min(heightSize, desiredHeight) else heightSize

        setMeasuredDimension(finalWidth, finalHeight)
    }

    /**
     * 2. 布局阶段 (Layout)
     * 确定 View 的大小后，可以在这里计算一些只需计算一次的坐标。
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        // 半径取宽高的最小值的一半，再留出一点边距给文字
        radius = (min(w, h) / 2f) * 0.8f
    }

    /**
     * 3. 绘制阶段 (Draw)
     * 这里的代码会非常频繁地执行，所以绝对【不能】在这里创建对象 (new Object)！
     * 否则会造成内存抖动 (Memory Churn) -> GC 频繁 -> 界面卡顿。
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制网格
        drawPolygonGrid(canvas)
        
        // 绘制连接线
        drawLines(canvas)
        
        // 绘制数据区域
        drawDataRegion(canvas)
        
        // 绘制文字
        drawText(canvas)
    }

    private fun drawPolygonGrid(canvas: Canvas) {
        val angle = 360f / count
        mainPaint.style = Paint.Style.STROKE
        
        // 绘制 5 层网格
        for (i in 1..5) {
            val r = radius * (i / 5f)
            path.reset()
            for (j in 0 until count) {
                if (j == 0) {
                    path.moveTo(centerX + r, centerY)
                } else {
                    // 数学公式：x = r * cos(θ), y = r * sin(θ)
                    // 注意：Math.toRadians 将角度转为弧度
                    val x = centerX + r * cos(Math.toRadians((angle * j).toDouble())).toFloat()
                    val y = centerY + r * sin(Math.toRadians((angle * j).toDouble())).toFloat()
                    path.lineTo(x, y)
                }
            }
            path.close()
            canvas.drawPath(path, mainPaint)
        }
    }

    private fun drawLines(canvas: Canvas) {
        val angle = 360f / count
        for (j in 0 until count) {
            path.reset()
            path.moveTo(centerX, centerY)
            val x = centerX + radius * cos(Math.toRadians((angle * j).toDouble())).toFloat()
            val y = centerY + radius * sin(Math.toRadians((angle * j).toDouble())).toFloat()
            path.lineTo(x, y)
            canvas.drawPath(path, mainPaint)
        }
    }

    private fun drawDataRegion(canvas: Canvas) {
        val angle = 360f / count
        path.reset()
        
        for (j in 0 until count) {
            val value = data[j] / 100f // 归一化
            val r = radius * value
            val x = centerX + r * cos(Math.toRadians((angle * j).toDouble())).toFloat()
            val y = centerY + r * sin(Math.toRadians((angle * j).toDouble())).toFloat()
            
            if (j == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            // 绘制小圆点
            canvas.drawCircle(x, y, 6f, valuePaint)
        }
        path.close()
        
        valuePaint.style = Paint.Style.FILL
        valuePaint.alpha = 127
        canvas.drawPath(path, valuePaint)
        
        // 绘制边框
        valuePaint.style = Paint.Style.STROKE
        valuePaint.alpha = 255
        valuePaint.strokeWidth = 4f
        canvas.drawPath(path, valuePaint)
    }

    private fun drawText(canvas: Canvas) {
        val angle = 360f / count
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent
        
        for (j in 0 until count) {
            val x = centerX + (radius + 40) * cos(Math.toRadians((angle * j).toDouble())).toFloat()
            val y = centerY + (radius + 40) * sin(Math.toRadians((angle * j).toDouble())).toFloat()
            
            // 简单的文字位置微调，实际项目中需要更复杂的计算
            canvas.drawText(abilities[j], x, y + fontHeight / 4, textPaint)
        }
    }
}
