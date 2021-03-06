package com.bullet.ui.customView

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.bullet.ktx.ui.dip2px
import com.bullet.ui.R

class MaterialEditText(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 文字高度
    private val mTextSize = dip2px(12f)

    // 文字和输入框间距
    private val mTextMargin = dip2px(8f)

    // 垂直偏移
    private val mTextVerticalOffset = dip2px(22f)

    // 纵向偏移
    private val mTextHorizontalOffset = dip2px(5f)

    // 文字动画偏移量
    private val mTextAnimationOffset = dip2px(16f)

    private var isFloatingLabel: Boolean = true

    val backgroundPadding = Rect()

    val animator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(
            this@MaterialEditText, "floatingLabelFaction", 0f, 1f
        )
    }

    // 动画是否显示
    private var floatingLabelShown = false

    private var floatingLabelFaction = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        val typeArray = context?.obtainStyledAttributes(attrs, R.styleable.MaterialEditText)
        isFloatingLabel = typeArray!!.getBoolean(R.styleable.MaterialEditText_floatingLabel, true)
        typeArray.recycle()
        init()
    }

    fun init() {

        background.getPadding(backgroundPadding)
        onFloatingLabelChange()
        paint.textSize = mTextSize.toFloat()

        addChangeListener()
    }

    private fun addChangeListener() {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFloatingLabel) {
                    if (floatingLabelShown && TextUtils.isEmpty(s)) {
                        animator.reverse()
                        floatingLabelShown = false
                    } else if (!floatingLabelShown && !TextUtils.isEmpty(s)) {
                        floatingLabelShown = true
                        animator.start()
                    }
                }
            }
        })
    }

    fun isFloatingLabel(isFloatingLabel: Boolean) {
        if (this.isFloatingLabel != isFloatingLabel) {
            this.isFloatingLabel = isFloatingLabel
            onFloatingLabelChange()
        }
    }

    private fun onFloatingLabelChange() {
        if (isFloatingLabel) {
            setPadding(
                paddingLeft, backgroundPadding.top + (mTextSize + mTextMargin).toInt(),
                paddingRight, paddingBottom
            )
        } else {
            setPadding(
                paddingLeft, backgroundPadding.top,
                paddingRight, paddingBottom
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.alpha = (floatingLabelFaction * 0xff).toInt()

        // 计算偏移量
        val extraOffset = mTextAnimationOffset * (1 - floatingLabelFaction)
        // 绘制提示信息
        canvas.drawText(
            hint as String, mTextHorizontalOffset.toFloat(),
            mTextVerticalOffset + (-extraOffset),
            paint
        )
    }
}
