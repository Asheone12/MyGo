package com.muen.mygo.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.withTranslation
import com.muen.mygo.R
import kotlin.math.abs

class LoadingView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var mWidth = 0
    private var mHeight = 0

    //文字X坐标
    private var textX = 0f

    //文字Y坐标
    private var textY = 0f
    private var text = "Loading"
    private var textRect = Rect()

    //文字距离上方的距离
    private var textTopMargin = 40f
    private var rectF = RectF()
    private var radius = 120f

    //大圆弧开始角度
    private var mainStartAngle = 180f

    //大圆弧每帧移动的角度
    private val mainMoveAngle = 24f

    //大圆弧扫过角度（负值表示顺时针）
    private var mainArcSweepAngle = -180f

    //小圆弧开始角度
    private var unitStartAngle = 60f

    //小圆弧每帧移动的角度
    private var unitMoveAngle = -8f

    //小圆弧旋转角度 （负值表示顺时针）
    private var unitSweepAngle = -12f

    //小圆弧之间间隔角度 （负值表示顺时针）
    private var unitIntervalAngle = -60f

    //大圆弧路径
    private var mainArcPath = Path()

    //小圆弧路径
    private var unitArcPath = Path()

    /**
     * 圆弧画笔
     */
    private var mPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 50f
        isAntiAlias = true
        isDither = true
        color = context.getColor(R.color.white)
    }

    /**
     * 矩形画笔
     */
    private var rectPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        isDither = true
        strokeWidth = 4f
        color = context.getColor(R.color.white)
    }

    /**
     * 文字画笔
     */
    private var textPaint = Paint().apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.BUTT
        isAntiAlias = true
        isDither = true
        textSize = 50f
        color = context.getColor(R.color.white)
    }


    //动画当前值
    private var value = 0f

    //小圆点数目
    private var circleNum = 0

    //小圆点的间距
    private var circleEndMargin = 10f
    private var circleRadius = 3f

    //声明动画
    private var animator: ValueAnimator = ValueAnimator.ofFloat(0f, 15f)

    init {
        rectF.apply {
            left = -radius
            right = radius
            top = -radius
            bottom = radius
        }
        initAnimator()
    }

    /**
     * 初始化动画
     */
    private fun initAnimator() {
        animator.apply {
            duration = 2000
            //无线重复
            repeatCount = ValueAnimator.INFINITE
            //重复模式-重头
            repeatMode = ValueAnimator.RESTART
        }
        animator.addUpdateListener {
            //获取当前值
            value = (it.animatedValue) as Float
            //生成Loading后小圆的数目
            circleNum = (value % 3).toInt()
            invalidate()
        }
        //设置插值器
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    /**
     * 覆写onSizeChanged,获取设备屏幕宽高
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        textPaint.getTextBounds(text, 0, text.length, textRect)
        calculateTextPos()
    }

    /**
     * 覆写onDraw,
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //将画布移动到屏幕中央
        canvas.withTranslation(mWidth / 2f, mHeight / 2f) {
            //绘制矩形
            //drawRect(rectF, rectPaint)
            //绘制大圆弧
            drawMainArc(this)
            //绘制两个小圆弧
            drawUnitArc(this)
        }
    }

    /**
     * 绘制主圆弧[step 2  - 不断改变开始角度]
     */
    private fun drawMainArc(canvas: Canvas) {
        mPaint.color = context.getColor(R.color.colorPrimary)
        //不断去更改startAngle
        mainArcPath.reset()
        mainArcPath.addArc(rectF, mainStartAngle + mainMoveAngle * 2 * value, mainArcSweepAngle)
        canvas.drawPath(mainArcPath, mPaint)
    }

    /**
     * 绘制小圆弧[step 3 - 小圆弧数目增加到6个]
     */
    private fun drawUnitArc(canvas: Canvas) {
        unitArcPath.reset()
        for(i in 0 until 6) {
            mPaint.color = context.getColor(R.color.colorPrimary)
            //计算小圆弧路径,这里也是不断地改变起始角度startAngle
            unitArcPath.addArc(
                rectF,
                -unitStartAngle * i + unitIntervalAngle +  unitMoveAngle * value  ,
                -unitSweepAngle
            )
            //绘制小圆弧路径
            canvas.drawPath(unitArcPath, mPaint)
        }
    }

    /**
     * 绘制文字
     */
    private fun drawTextAndLoading(canvas: Canvas) {
        canvas.drawText(text, textX, textY + radius + textTopMargin, textPaint)
    }


    /**
     * 计算文字绘制坐标
     */
    private fun calculateTextPos() {
        textX = -abs(textRect.right - textRect.left) / 2f
        textY = (abs(textRect.top) + abs(textRect.bottom)).toFloat()
    }

}