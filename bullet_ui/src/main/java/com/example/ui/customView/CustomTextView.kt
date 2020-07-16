package com.example.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.example.ui.R
import java.util.*

/**
 * Created by Administrator on 2019/10/17.
 */

class CustomTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {

    /**
     * 要显示文字
     */
    private var mText: String? = null

    /**
     * 要改变颜色的文字
     */
    private var mTv: String? = null

    /**
     * 文字默认大小 15sp
     */
    private var mTextSize = 15


    /**
     * 文字默认颜色
     */
    private var mTextColor = Color.BLACK

    /**
     * 需要改变的颜色
     */
    private var mTvColor = Color.RED


    /**
     * 画笔
     */
    private val mPaint: TextPaint
    private var wRect: Rect? = null
    private var hRect: Rect? = null

    private var startPos = -1
    private var endPos = -1

    private var mTvs: Array<String>? = null
    private var mColors: IntArray? = null


    /**
     * 获取基线
     */
    private val baseLine: Int
        get() {
            val fontMetricsInt = mPaint.fontMetricsInt
            val dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom
            return height / 2 + dy
        }

    init {

        // 获取自定义属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)
        // 获取文字
        mText = typedArray.getString(R.styleable.CustomTextView_text)
        //获取设置颜色的文字
        mTv = typedArray.getString(R.styleable.CustomTextView_tv)
        // 获取文字大小
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_textSize, sp2px(mTextSize))
        // 获取文字颜色
        typedArray.getColor(R.styleable.CustomTextView_textColor, mTextColor)
        startPos = typedArray.getInt(R.styleable.CustomTextView_startPosInteger, -1)
        endPos = typedArray.getInt(R.styleable.CustomTextView_endPosInteger, -1)
        mTvColor = typedArray.getInt(R.styleable.CustomTextView_tvColor, Color.RED)
        // 释放资源
        typedArray.recycle()

        // 创建画笔
        mPaint = TextPaint()
        // 设置抗锯齿，让文字比较清晰，同时文字也会变得圆滑
        mPaint.isAntiAlias = true
        // 设置文字大小
        mPaint.textSize = mTextSize.toFloat()
        // 设置画笔颜色
        mPaint.color = mTextColor

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        // 如果在布局文件中设置的宽高都是固定值[比如100dp、200dp等]，就用下边方式直接获取宽高
        var width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        // 如果在布局中设置宽高都是wrap_content[对应AT_MOST]，必须用mode计算
        if (widthMode != View.MeasureSpec.EXACTLY) {
            // 文字宽度 与字体大小和长度有关
            if (wRect == null) {
                wRect = Rect()
            }
            // 获取文本区域 param1__测量的文字 param2__从位置0开始 param3__到文字长度
            mPaint.getTextBounds(mText, 0, mText!!.length, wRect)
            // 文字宽度 getPaddingLeft宽度 getPaddingRight高度 写这两个原因是为了在布局文件中设置padding属性起作用
            width = wRect!!.width() + paddingLeft + paddingRight
        }


        if (hRect == null) {
            hRect = Rect()
        }
        if (heightMode != View.MeasureSpec.EXACTLY) {
            mPaint.getTextBounds(mText, 0, mText!!.length, hRect)
            height = hRect!!.height() + paddingTop + paddingBottom
        }


        // 给文字设置宽高
        setMeasuredDimension(width, height)
    }

    /**
     * 绘制文字
     */
    override fun onDraw(canvas: Canvas) {
        if (mTv != null && !mTv!!.isEmpty()) {
            val start = mText!!.indexOf(mTv!!)
            val end = start + mTv!!.length
            startToEnd(canvas, start, end, mTvColor)
        } else if (mTvs != null) {
            if (mColors != null && mColors!!.size == mTvs!!.size) {
                sort()
                drawTvs(Arrays.asList(*mTvs!!), mColors!!.toList(), canvas)
            } else {
                drawTvs(listOf(*mTvs!!), canvas)
            }
        } else if (startPos > -1 && endPos > -1) {
            startToEnd(canvas, startPos, endPos, mTvColor)
        } else if (startPos > -1) {
            startToEnd(canvas, startPos, startPos + 1, mTvColor)
        } else {
            val baseLine = baseLine
            mPaint.color = mTextColor
            canvas.drawText(mText!!, paddingLeft.toFloat(), baseLine.toFloat(), mPaint)
        }
    }


    /**
     * 指定位置的文字变为指定的颜色
     *
     * @param canvas   画布
     * @param startPos 开始的位置
     * @param endPos   结束的位置
     * @param colorPos 指定的颜色
     */
    private fun startToEnd(canvas: Canvas, startPos: Int, endPos: Int, colorPos: Int) {
        if (startPos <= -1 && endPos <= -1) {
            throw IndexOutOfBoundsException("索引异常，检查需要修改的文字是否设置正确")
        }
        val baseLine = baseLine
        val paddingLeft = paddingLeft

        val start = mText!!.substring(0, startPos)
        val red = mText!!.substring(startPos, endPos)
        val end = mText!!.substring(endPos, mText!!.length)
        canvas.drawText(start, paddingLeft.toFloat(), baseLine.toFloat(), mPaint)

        mPaint.color = colorPos
        val redLength = Layout.getDesiredWidth(start, mPaint) + paddingLeft
        canvas.drawText(red, redLength, baseLine.toFloat(), mPaint)

        mPaint.color = mTextColor
        val endLength = Layout.getDesiredWidth(red, mPaint) + Layout.getDesiredWidth(start, mPaint) + paddingLeft.toFloat()
        canvas.drawText(end, endLength, baseLine.toFloat(), mPaint)
    }

    private fun drawTvs(tvs: List<String>, canvas: Canvas) {
        mColors = IntArray(3)
        for (i in mColors!!.indices) {
            mColors!![i] = mTvColor
        }
        drawTvs(tvs, mColors!!.toList(), canvas)
    }

    private fun drawTvs(tvs: List<String>, colors: List<Int>, canvas: Canvas) {
        val baseLine = baseLine
        val paddingLeft = paddingLeft
        val tvWidth = (width - getPaddingLeft() - paddingRight).toFloat() //控件可用宽度
        for (i in tvs.indices) {
            val start = mText!!.indexOf(tvs[i])
            val end = start + tvs[i].length
            val startPos = mText!!.substring(0, start)
            val redPos = mText!!.substring(start, end)
            val endPos: String

            if (tvs.size > i + 1) {
                endPos = mText!!.substring(end, mText!!.indexOf(tvs[i + 1]))
            } else {
                endPos = mText!!.substring(end, mText!!.length)
            }
            if (i == 0) {
                canvas.drawText(startPos, paddingLeft.toFloat(), baseLine.toFloat(), mPaint)
            }
            if (mColors != null && colors.size > i) {
                mPaint.color = colors[i]
            } else {
                mPaint.color = mTvColor
            }
            val redLength = Layout.getDesiredWidth(startPos, mPaint) + paddingLeft
            canvas.drawText(redPos, redLength, baseLine.toFloat(), mPaint)

            mPaint.color = mTextColor
            val endLength = Layout.getDesiredWidth(redPos, mPaint) + Layout.getDesiredWidth(startPos, mPaint) + paddingLeft.toFloat()
            canvas.drawText(endPos, endLength, baseLine.toFloat(), mPaint)
        }
    }

    /**
     * 开始的位置，可单独使用
     *
     * @param startPos 开始的位置，未设置结束位置则结束位置为 startPos + 1
     */
    fun startPos(startPos: Int) {
        this.startPos = startPos
    }

    /**
     * 结束位置
     *
     * @param endPos 结束位置，必须大于开始位置
     */
    fun endPos(endPos: Int) {
        this.endPos = endPos
    }

    /**
     * 设置要改变颜色的文字，和设置 位置 选择其一即可
     *
     * @param colorText 改变颜色的文字
     */
    fun setColorTv(colorText: String) {
        this.mTv = colorText
    }

    /**
     * 多个地方的颜色需要修改可以使用此方法
     *
     * @param tvs 需要修改的文字数组
     */
    fun setTvs(tvs: Array<String>) {
        this.mTvs = tvs
    }

    fun setTvs(tvs: Array<String>, colors: IntArray) {
        this.mTvs = tvs
        this.mColors = colors

    }

    /**
     * 设置普通文字的颜色
     *
     * @param color 颜色
     */
    override fun setTextColor(@ColorInt color: Int) {
        this.mTextColor = color
    }

    /**
     * 设置要文字需要改变的颜色
     *
     * @param color
     */
    fun setTvColor(@ColorInt color: Int) {
        this.mTvColor = color
    }

    /**
     * 设置 text
     *
     * @param mText text
     */
    fun setText(mText: String) {
        this.mText = mText
    }

    fun setTv(changeTxt: String) {
        this.mTv = changeTxt
    }

    /**
     * 设置默认文本颜色
     *
     * @param mTextSize size
     */
    fun setTestSize(mTextSize: Int) {
        this.mTextSize = mTextSize
    }

    fun notifyTv() {
        invalidate()
    }

    /**
     * sp转为px
     */
    private fun sp2px(sp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(),
                resources.displayMetrics).toInt()
    }

    private fun sort() {
        for (i in mTvs!!.indices) {
            for (j in i + 1 until mTvs!!.size) {
                if (mText!!.indexOf(mTvs!![i]) > mText!!.indexOf(mTvs!![j])) {
                    val text = mTvs!![i]
                    mTvs!![i] = mTvs!![j]
                    mTvs!![j] = text
                    val color = mColors!![i]
                    mColors!![i] = mColors!![j]
                    mColors!![j] = color
                }
            }
        }
    }

    /**
     * 获取textview一行最大能显示几个字(需要在TextView测量完成之后)
     *
     * @param text     文本内容
     * @param paint    textview.getPaint()
     * @param maxWidth textview.getMaxWidth()/或者是指定的数值,如200dp
     */
    private fun getLineMaxNumber(text: String?, paint: TextPaint, maxWidth: Int): Int {
        if (null == text || "" == text) {
            return 0
        }
        val staticLayout = StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, false)
        //获取第一行最后显示的字符下标
        return staticLayout.getLineEnd(0)
    }


}
/**
 * 这种调用第1个构造方法
 * TextView tv = new TextView(this)：
 */
/**
 * 这种调用第2个构造方法
 * <com.novate.test.customview.MyTextView ......>
</com.novate.test.customview.MyTextView> */
