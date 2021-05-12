package com.bullet.ui.customView.page

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewConfigurationCompat
import com.business.tools.views.page.PageAdapter
import com.business.tools.views.page.ScrollerExt
import java.lang.Math.abs

/**
 * @name Android Business Toos
 * @class name：com.business.tools.views.page
 * @author 345 QQ:1831712732
 * @time 2020/4/11 20:46
 * @description 循环banner
 */

class ScrollerLayout : ViewGroup {

    /**
     * 用于完成滚动操作的实例
     */
    private var mScroller: ScrollerExt

    /**
     * 判定为拖动的最小移动像素
     */
    private var mTouchSlop = 0

    /**
     * 按下时的屏幕坐标
     */
    private var mXDown = 0f

    /**
     * 移动时所处的屏幕坐标
     */
    private var mXMove = 0f

    /**
     * 手指离开屏幕的坐标
     */
    private var mXUp = 0f

    /**
     * 上次触发 MOVE 事件的屏幕坐标
     */
    private var mXLastMove = 0f

    /**
     * 界面可滚动的左边界
     */
    private var leftBorder = 0

    /**
     * 界面可滚动的由边界
     */
    private var rightBorder = 0

    /**
     * 滑动后的位置
     */
    private var targetIndex = -1

    /**
     * 下标
     */
    var mPosition: Int = 0

    private var layoutInflater = LayoutInflater.from(context)

    /**
     * 适配器
     */
    var adapter: PageAdapter? = null
        set(value) {
            field = value
            startItem()
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        mScroller = ScrollerExt(context)
        mScroller.listener = listener
        // 最小移动距离
        mTouchSlop = ViewConfigurationCompat.getScaledHoverSlop(ViewConfiguration.get(context))

        // 让当前view 可以点击
        isClickable = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            // 测量子控件的大小
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * 创建视图
     */
    private fun startItem() {
        val count = adapter?.count()
        val listOf = mutableListOf<View>()

        val fist = layoutInflater.inflate(
            adapter?.layoutRes!!, this, false
        ) as AppCompatImageView
        listOf.add(fist)
        for (i in 0 until count!!) {
            val iv =
                layoutInflater.inflate(
                    adapter?.layoutRes!!, this, false
                ) as AppCompatImageView
            listOf.add(iv)
        }
        val end = layoutInflater.inflate(
            adapter?.layoutRes!!, this, false
        ) as AppCompatImageView
        listOf.add(end)

        drawItem(listOf)
    }

    /**
     * 绘制 Item，并回调适配器
     */
    private fun drawItem(views: MutableList<View>) {
        views.forEach {
            addView(it)
        }
        invalidate()

        for (i in 1..views.size - 2) {
            adapter?.view(views[i], i - 1)
        }
        // 布局绘制成功后回调，直接滑动到相应的位置
        viewTreeObserver.addOnGlobalLayoutListener {
            scrollTo(width, 0)
            /**
             * 给左右多出来的Item添加默认视图
             */
            if ((views[childCount - 2] as AppCompatImageView).drawable != null) {
                val bitmap =
                    (((views[childCount - 2] as AppCompatImageView).drawable) as BitmapDrawable).bitmap
                (views[0] as AppCompatImageView).setImageBitmap(bitmap)
            }

            if ((views[1] as AppCompatImageView).drawable != null) {
                val bitmap =
                    (((views[1] as AppCompatImageView).drawable) as BitmapDrawable).bitmap
                (views[views.size - 1] as AppCompatImageView).setImageBitmap(bitmap)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed && childCount > 0) {
            val childCount = childCount
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                // 给每一个子控件在水平方向上布局
                childView.layout(
                    i * childView.measuredWidth, 0,
                    (i + 1) * childView.measuredWidth, childView.measuredHeight
                )
            }
            // 获取左右边界
            leftBorder = getChildAt(0).left
            rightBorder = getChildAt(childCount - 1).right
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                // rawX ，相对于屏幕的横坐标
                mXDown = ev.rawX
                mXLastMove = mXDown
            }
            MotionEvent.ACTION_MOVE -> {
                mXMove = ev.rawX
                // 移动的距离
                val diff = kotlin.math.abs(mXMove - mXDown)
                mXLastMove = mXMove
                // 当手指拖动大于 TouchSlop 值时，认为应该进行滚动，拦截子控件的时间
                if (diff > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mXMove = event.rawX
                // 移动的距离
                val scrolledX = (mXLastMove - mXMove).toInt()
                // 边界处理，拖出边界后就使用 scrollTo 回到边界位置
                if (scrollX + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0)
                    return true
                } else if (scrollX + width + scrolledX > rightBorder) {
                    scrollTo(rightBorder - width, 0)
                    return true
                }
                // 移动
                scrollBy(scrolledX, 0)
                mXLastMove = mXMove
            }
            MotionEvent.ACTION_UP -> {
                mXUp = event.rawX
                // 当手指抬起时，根据当前滚动值来判定应该滚动到那个子控件界面
                // 计算收松开后要显示的页面 index
                targetIndex = if (mXDown > event.rawX) {
                    (scrollX + (width * 0.7).toInt()) / width
                } else {
                    (scrollX + (width * 0.3).toInt()) / width
                }
                val dx = targetIndex * width - scrollX
                // 调用 startScroll 方法来初始化数据并刷新界面
                mScroller.startScroll(scrollX, 0, dx, 0)

                when (targetIndex) {
                    0 -> {
                    }
                    (childCount - 1) -> {
                    }
                    else -> {
                        mPosition = targetIndex - 1
                    }
                }
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        // 重新 computeScroll 方法，并在内部完成平滑滚动逻辑
        // 判断滚动操作是否完成了，如果没有完成就继续滚动
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    private val listener = object : ScrollerExt.ScrollListener {
        override fun isScroll(isScroll: Boolean) {
            if (!isScroll) {
                if (abs(mXDown - mXUp) > mTouchSlop) {
                    if (targetIndex == childCount - 1) {
                        targetIndex = -1
                        mPosition = 0
                        scrollTo(width, 0)
                    } else if (targetIndex == 0) {
                        targetIndex = -1
                        mPosition = childCount - 2
                        scrollTo(width * (childCount - 2), 0)
                    }
                }
            }
        }
    }
}
