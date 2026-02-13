package com.shineofeidos.mockapiproject.ui.customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.min
import androidx.core.graphics.toColorInt

/**
 * =========================================================================================
 * 进阶实战：波浪进度球 (WaveProgressView)
 * =========================================================================================
 *
 * 知识点：
 * 1. 贝塞尔曲线 (Bezier Curve): 使用 rQuadTo 绘制平滑波浪
 * 2. 图层混合 (Xfermode): 使用 SRC_IN 将波浪限制在圆形区域内
 * 3. 属性动画 (ValueAnimator): 改变波浪偏移量实现流动效果
 */
class WaveProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#4CAF50".toColorInt() // 波浪颜色
        style = Paint.Style.FILL
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 50f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val wavePath = Path()
    private val circlePath = Path() // 用于裁剪圆形的路径 (如果不用 Xfermode 的另一种简单做法，但为了演示 Xfermode，我们这里主要用作形状参考)
    
    private var waveLength = 0f
    private var waveHeight = 40f
    private var waveOffset = 0f
    private var progress = 50 // 0-100
    
    private var widthSize = 0
    private var heightSize = 0
    
    private var animator: ValueAnimator? = null

    // 离屏缓冲 Bitmap (用于 Xfermode)
    private lateinit var srcBitmap: Bitmap
    private lateinit var dstBitmap: Bitmap
    private val xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    init {
        startAnimation()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h
        waveLength = w.toFloat()
        
        // 创建圆形遮罩 (DST)
        dstBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val dstCanvas = Canvas(dstBitmap)
        dstCanvas.drawCircle(w / 2f, h / 2f, w / 2f, Paint(Paint.ANTI_ALIAS_FLAG))
        
        // 创建波浪层 (SRC) - 大小一致
        srcBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 1. 绘制波浪到 srcBitmap
        drawWaveToBitmap()
        
        // 2. 使用 Xfermode 绘制：先画圆 (DST)，再画波浪 (SRC) 并取交集
        // 必须使用 saveLayer 创建离屏图层
        val layerId = canvas.saveLayer(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), null)
        
        canvas.drawBitmap(dstBitmap, 0f, 0f, null) // DST
        wavePaint.xfermode = xfermode
        canvas.drawBitmap(srcBitmap, 0f, 0f, wavePaint) // SRC
        wavePaint.xfermode = null // 还原
        
        canvas.restoreToCount(layerId)
        
        // 3. 绘制文字 (覆盖在最上层)
        val fontMetrics = textPaint.fontMetrics
        val baseline = heightSize / 2f - (fontMetrics.bottom + fontMetrics.top) / 2f
        canvas.drawText("$progress%", widthSize / 2f, baseline, textPaint)
    }
    
    private fun drawWaveToBitmap() {
        val srcCanvas = Canvas(srcBitmap)
        // 清空 bitmap
        srcCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        
        wavePath.reset()
        // 这里的计算稍微复杂一点：
        // 波浪需要比 View 宽，才能移动。我们画两个波长的宽度。
        // 起点从屏幕左侧外开始
        val startX = -waveLength + waveOffset
        val waterLevel = heightSize * (1 - progress / 100f) // 水位高度
        
        wavePath.moveTo(startX, waterLevel)
        
        // 绘制两个波长
        var i = -waveLength
        while (i < widthSize + waveLength) {
            // rQuadTo 是相对坐标：(控制点x, 控制点y, 终点x, 终点y)
            // 一个完整的波浪由两个二阶贝塞尔曲线组成：一个波峰，一个波谷
            wavePath.rQuadTo(waveLength / 4f, -waveHeight, waveLength / 2f, 0f)
            wavePath.rQuadTo(waveLength / 4f, waveHeight, waveLength / 2f, 0f)
            i += waveLength
        }
        
        // 封闭路径形成水体
        wavePath.lineTo(widthSize.toFloat(), heightSize.toFloat())
        wavePath.lineTo(0f, heightSize.toFloat())
        wavePath.close()
        
        srcCanvas.drawPath(wavePath, wavePaint)
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val fraction = it.animatedValue as Float
                waveOffset = fraction * waveLength
                invalidate()
            }
            start()
        }
    }
    
    fun stopAnimation() {
        animator?.cancel()
        animator = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
    
    fun setProgress(p: Int) {
        this.progress = p.coerceIn(0, 100)
        invalidate()
    }
}
