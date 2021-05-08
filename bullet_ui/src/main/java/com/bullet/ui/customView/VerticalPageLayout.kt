package com.bullet.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewConfigurationCompat
import com.business.tools.views.page.PageAdapter
import kotlin.math.abs

/**
 * @name PageLayout
 * @package com.bullet.ui.customView
 * @author 345 QQ:1831712732
 * @time 2021/01/22 13:07
 * @description
 */
class VerticalPageLayout : ViewGroup {

    /**
     * 用于完成滚动操作的实例
     */
    private var mScroller: Scroller

    /**
     * 判定为拖动的最小移动像素
     */
    private var mTouchSlop = 0

    /**
     * 按下时的屏幕坐标
     */
    private var mYDown = 0f

    /**
     * 移动时所处的屏幕坐标
     */
    private var mYMove = 0f

    /**
     * 上次触发 MOVE 事件的屏幕坐标
     */
    private var mYLastMove = 0f

    /**
     * 界面可滚动的左边界
     */
    private var topBorder = 0

    /**
     * 界面可滚动的由边界
     */
    private var bottomBorder = 0

    /**
     * 滑动后的位置
     */
    private var targetIndex = -1

    private var layoutInflater = LayoutInflater.from(context)

    private var velocityTracker: VelocityTracker? = null

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
        mScroller = Scroller(context)
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

        for (i in 0 until count!!) {
            val iv = layoutInflater.inflate(
                adapter?.layoutRes!!, this, false
            ) as AppCompatImageView
            listOf.add(iv)
        }
        drawItem(listOf)
    }

    /**
     * 绘制 Item，并回调适配器
     */
    private fun drawItem(views: MutableList<View>) {
        views.forEach {
            addView(it)
        }

        for (i in 0 until views.size) {
            adapter?.view(views[i], i)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            val childCount = childCount
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                // 为ScrollerLayout中的每一个子控件在垂直方向上进行布局
                childView.layout(0, i * childView.measuredHeight, childView.measuredWidth, (i + 1) * childView.measuredHeight)
            }
            // 初始化左右边界值
            topBorder = getChildAt(0).top
            bottomBorder = getChildAt(getChildCount() - 1).bottom
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mYDown = ev.rawY
                mYLastMove = mYDown
            }
            MotionEvent.ACTION_MOVE -> {
                mYMove = ev.rawY
                val diff = abs(mYMove - mYDown)
                mYLastMove = mYMove
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain()
        velocityTracker!!.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mYMove = event.rawY
                val scrolledY = (mYLastMove - mYMove).toInt()
                if (scrollY + scrolledY < topBorder - 500) {
                    mScroller.startScroll(0, scrollY, 0, topBorder)
                    return true
                } else if (scrollY + height + scrolledY > bottomBorder + 500) {
                    mScroller.startScroll(0, scrollY, 0, bottomBorder - height)
                    return true
                }
                scrollBy(0, scrolledY)
                mYLastMove = mYMove
            }
            MotionEvent.ACTION_UP -> {
                // 当手指抬起时，根据当前滚动值来判定应该滚动到那个子控件界面
                // 计算收松开后要显示的页面 index

                // 计算当前速度，units 是单位表示
                velocityTracker!!.computeCurrentVelocity(1000)
                // 判断 y 轴速度
                if (abs(velocityTracker!!.yVelocity) > 1000) {
                    // 向下滑动
                    targetIndex = if (velocityTracker!!.yVelocity > 1000) {
                        val index = if (targetIndex == 0) 0 else targetIndex - 1
                        val dy = index * height - scrollY
                        mScroller.startScroll(0, scrollY, 0, dy)
                        index
                    } else {
                        // 向上滑动
                        val index = if (targetIndex == adapter!!.count() - 1) adapter!!.count() - 1 else targetIndex + 1
                        val dy = index * height - scrollY
                        mScroller.startScroll(0, scrollY, 0, dy)
                        index
                    }
                } else {
                    targetIndex = if (mYDown > event.rawY) {
                        (scrollY + (height * 0.8).toInt()) / height
                    } else {
                        (scrollY + (height * 0.2).toInt()) / height
                    }
                    if (targetIndex >= adapter!!.count() - 1) targetIndex--
                    val dy = targetIndex * height - scrollY
                    mScroller.startScroll(0, scrollY, 0, dy)
                }
                invalidate()
                release()
            }
            MotionEvent.ACTION_CANCEL -> {
                release()
            }
        }
        return true
    }

    override fun computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
    }

    fun release() {
        velocityTracker?.clear()
        velocityTracker?.recycle()
        velocityTracker = null
    }
}
