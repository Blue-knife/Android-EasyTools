package com.business.tools.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView

/**
 * 解决 嵌套RecyclerView 出现的问题
 */
class CustomNestedScrollView : NestedScrollView {
    private var mDownX: Int = 0
    private var mDownY: Int = 0
    private var mTouchSlop: Int = 0

    constructor(context: Context) : super(context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        val action = e.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = e.rawX.toInt()
                mDownY = e.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveY = e.rawY.toInt()
                if (Math.abs(moveY - mDownY) > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }
}
