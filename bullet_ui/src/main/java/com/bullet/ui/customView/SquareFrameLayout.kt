package com.bullet.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @name SquareFrameLayout
 * @package com.example.ui.customView
 * @author 345 QQ:1831712732
 * @time 2020/5/24 19:46
 * @description 正方形的 FrameLayout
 */

class SquareFrameLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        //设置宽高一致
        setMeasuredDimension(width, width)
    }
}