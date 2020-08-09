package com.bullet.ui.customView

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.bullet.ui.R

/**
 * @author 345 QQ:1831712732
 * @name Android Business Tools
 * @class name：com.business.tools.views
 * @time 2020/8/1 11:25
 * @description 自定义 Recycler 的最大高度
 */
class MaxRecyclerView : RecyclerView {
    private var mMaxHeight = 0

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.MaxRecyclerView)
        mMaxHeight = arr.getLayoutDimension(R.styleable.MaxRecyclerView_maxHeight, mMaxHeight)
        arr.recycle()
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val height = measuredHeight
        if (height > mMaxHeight) {
            setMeasuredDimension(widthSpec, mMaxHeight)
        }
    }
}