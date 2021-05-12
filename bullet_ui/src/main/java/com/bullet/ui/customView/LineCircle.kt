package com.bullet.ui.customView

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.*

/**
 * @author 345 QQ:1831712732
 * @name Android Business Tools
 * @class name：com.business.tools.views
 * @time 2020/8/1 11:20
 * @description
 */
class LineCircle(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    /**
     * 线的颜色
     */
    private var mPathColor = Color.parseColor("#eeeeee")

    /**
     * 外圆的颜色
     */
    private val outerCircleColor = Color.parseColor("#96ccff")

    /**
     * 内圆的颜色
     */
    private val innerCircleColor = Color.parseColor("#2988e2")

    /**
     * 外圆半径
     */
    private val outRadius = dp2px(5f).toInt()

    /**
     * 内圆半径
     */
    private val inRadius = dp2px(2.5f).toInt()

    /**
     * 通过 [.add] 添加位置的圆的半径
     */
    private val listRadius = dp2px(2.5f).toInt()

    /**
     * 是否为头部
     */
    private var mHead = false

    /**
     * 是否发为尾部
     */
    private var mEnd = false

    /**
     * 是否选中
     */
    private var isSelect = false

    // 偏移
    private var offsetY = 0

    /**
     * 默认的圆在高度上的百分比
     */
    private var mScale = 0.3f

    /**
     * 原点 y 轴位置
     */
    private val mList = ArrayList<Int>()
    override fun onDraw(canvas: Canvas) {
        val width = width

        // 默认圆的高度
        val scaleHeight =
            if (offsetY == 0) {
                (height * mScale).toInt()
            } else {
                offsetY
            }
        val center = width / 2
        paint.color = mPathColor
        paint.strokeWidth = dp2px(1f)
        path.moveTo(center.toFloat(), 50f)
        path.lineTo(center.toFloat(), height.toFloat())

        // 画线
        if (mHead) {
            canvas.drawLine(center.toFloat(), scaleHeight.toFloat(), center.toFloat(), height.toFloat(), paint)
        } else if (mEnd) {
            if (mList.size > 0 && mList[mList.size - 1] > scaleHeight) {
                canvas.drawLine(center.toFloat(), 0f, center.toFloat(), mList[mList.size - 1].toFloat(), paint)
            } else {
                canvas.drawLine(center.toFloat(), 0f, center.toFloat(), scaleHeight.toFloat(), paint)
            }
        } else {
            canvas.drawLine(center.toFloat(), 0f, center.toFloat(), height.toFloat(), paint)
        }
        // 画圆
        if (isSelect) {
            paint.color = outerCircleColor
            canvas.drawCircle(center.toFloat(), scaleHeight.toFloat(), outRadius.toFloat(), paint)
            paint.color = innerCircleColor
            canvas.drawCircle(center.toFloat(), scaleHeight.toFloat(), inRadius.toFloat(), paint)
        } else {
            paint.color = mPathColor
            canvas.drawCircle(center.toFloat(), scaleHeight.toFloat(), listRadius.toFloat(), paint)
        }
        if (mList.size > 0) {
            for (i in mList.indices) {
                paint.color = mPathColor
                canvas.drawCircle(center.toFloat(), mList[i].toFloat(), listRadius.toFloat(), paint)
            }
        }
    }

    /**
     * 添加别的圆点
     * 需传入具体的高度，以像素为单位
     *
     *
     * 全部添加完成后需调用 [.addSave] ,
     * 注意，需按顺序添加，从小往大
     *
     *
     * @param offsetY 偏移
     */
    fun add(offsetY: Int) {
        mList.add(offsetY)
    }

    fun add(offsetY: List<Int>?) {
        mList.clear()
        mList.addAll(offsetY!!)
    }

    /**
     * 添加完成后执行
     */
    fun addSave() {
        invalidate()
    }

    fun clear() {
        mList.clear()
    }

    /**
     * 是否为第一个
     */
    fun setHead(head: Boolean) {
        mHead = head
        invalidate()
    }

    /**
     * 是否为最后一个
     *
     * @param end
     */
    fun setEnd(end: Boolean) {
        mEnd = end
        invalidate()
    }

    /**
     * 设置线的颜色
     *
     * @param color
     */
    fun pathColor(color: Int) {
        mPathColor = color
        invalidate()
    }

    /**
     * 设置圆高度的百分比
     *
     * @param mScale
     */
    fun setScale(mScale: Float) {
        this.mScale = mScale
        invalidate()
    }

    /**
     * 是否选中默认的圆。
     * 高度使用  [.setSelect] 进行设置
     *
     * @param isSelect
     */
    fun setSelect(isSelect: Boolean) {
        setSelect(isSelect, 0)
    }

    /**
     * 是否选中默认的圆，以及对应的位置
     *
     * @param isSelect
     */
    fun setSelect(isSelect: Boolean, offsetY: Int) {
        this.isSelect = isSelect
        this.offsetY = offsetY
        invalidate()
    }

    /**
     * dp 转像素
     *
     * @param dp
     * @return
     */
    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )
    }
}
