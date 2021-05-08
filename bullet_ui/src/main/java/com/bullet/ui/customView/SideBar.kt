package com.bullet.ui.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.bullet.ui.R

/**
 * Created by Petterp
 * on 2020/4/20
 * Function: 数字||字母侧边栏
 */
class SideBar : View {
    // 触摸事件
    private var onTouchingLetterChangedListener: OnTouchingLetterChangedListener? = null

    // 选中
    private var choose = -1
    private val paint = Paint()
    private var mTextDialog: TextView? = null

    /** 文字大小 */
    var height = 30f

    /** 文字间隔 */
    var mergeHeight = 5f

    /** 默认显示字体颜色 */
    var fontColor = Color.parseColor("#858c94")

    /** 选中字体颜色 */
    var selectFontColor = Color.parseColor("#FFFFFF")

    @SuppressLint("ResourceType")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?) : super(context) {}

    private fun init(attrs: AttributeSet?) {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.SideBar)
        height = array.getFloat(R.styleable.SideBar_fontSize, 30f)
        mergeHeight = array.getFloat(R.styleable.SideBar_mergeHeight, 5f)
        fontColor = array.getColor(R.styleable.SideBar_fontColor, Color.parseColor("#858c94"))
        selectFontColor = array.getColor(R.styleable.SideBar_selectFontColor, Color.WHITE)
        array.recycle()
    }

    /**
     * 为SideBar显示字母的TextView
     *
     * @param mTextDialog
     */
    fun setTextView(mTextDialog: TextView?) {
        this.mTextDialog = mTextDialog
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode != MeasureSpec.EXACTLY) {
            width = widthMeasureSpec + paddingLeft + paddingRight
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            val size = b.size
            height = (this.height * size + (size - 1) * mergeHeight).toInt()
        }
        setMeasuredDimension(width, height)
    }

    /*** 重写的onDraw的方法 */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width // 获取对应的宽度
        for (i in b.indices) {
            paint.color = fontColor // 所有字母的默认颜色 目前为灰色(右侧字体颜色)
            paint.typeface = Typeface.DEFAULT // (右侧字体样式)
            paint.isAntiAlias = true
            paint.textSize = height // (右侧字体大小)
            // 选中的状态
            if (i == choose) {
                paint.color = selectFontColor // 选中字母的颜色 目前为白
                paint.isFakeBoldText = true // 设置是否为粗体文字
            }
            // x坐标等于=中间-字符串宽度的一般
            val xPos = width / 2 - paint.measureText(b[i]) / 2
            val yPos = height * i + height + mergeHeight * i
            canvas.drawText(b[i], xPos, yPos, paint)
            paint.reset() // 重置画笔
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y = event.y // 点击y坐标
        val oldChoose = choose
        val listener = onTouchingLetterChangedListener
        val c = (y / (height + mergeHeight)).toInt() // 点击y坐标所占高度的比例*b数组的长度就等于点击b中的个数
        when (action) {
            MotionEvent.ACTION_UP -> {
                choose = -1
                invalidate()
                if (mTextDialog != null) {
                    mTextDialog!!.visibility = INVISIBLE
                }
            }
            else -> {
                if (oldChoose != c) {
                    if (c >= 0 && c < b.size) {
                        listener?.onTouchingLetterChanged(b[c])
                        if (mTextDialog != null) {
                            mTextDialog!!.text = b[c]
                            mTextDialog!!.visibility = VISIBLE
                        }
                        choose = c
                        invalidate()
                    }
                }
            }
        }
        return true
    }

    /**
     * 向外松开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    fun setOnTouchingLetterChangedListener(
        onTouchingLetterChangedListener: OnTouchingLetterChangedListener?
    ) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener
    }

    /**
     *
     * 接口
     *
     * @author
     */
    interface OnTouchingLetterChangedListener {
        fun onTouchingLetterChanged(s: String?)
    }

    companion object {
        // 默认26个字母
        var b = arrayOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"
        )
    }

    /** 重新设置显示的字母 */
    fun setArray(array: Array<String>) {
        b = array
        requestLayout()
    }
}
