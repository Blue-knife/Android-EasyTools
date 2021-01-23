package com.bullet.ui.customView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.Scroller
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewConfigurationCompat
import com.business.tools.views.page.PageAdapter


/**
 * @name PageLayout
 * @package com.bullet.ui.customView
 * @author 345 QQ:1831712732
 * @time 2021/01/22 13:07
 * @description
 */
class PageLayout : ViewGroup {

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
        mScroller = Scroller(context)
        //最小移动距离
        mTouchSlop = ViewConfigurationCompat.getScaledHoverSlop(ViewConfiguration.get(context))

        //让当前view 可以点击
        isClickable = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            //测量子控件的大小
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
                // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
                childView.layout(i * childView.measuredWidth, 0, (i + 1) * childView.measuredWidth, childView.measuredHeight)
            }
            // 初始化左右边界值
            leftBorder = getChildAt(0).left
            rightBorder = getChildAt(getChildCount() - 1).right
        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mXDown = ev.rawX
                mXLastMove = mXDown
            }
            MotionEvent.ACTION_MOVE -> {
                mXMove = ev.rawX
                val diff = Math.abs(mXMove - mXDown)
                mXLastMove = mXMove
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                mXMove = event.rawX
                val scrolledX = (mXLastMove - mXMove).toInt()
                if (scrollX + scrolledX < leftBorder) {
                    scrollTo(leftBorder, 0)
                    return true
                } else if (scrollX + width + scrolledX > rightBorder) {
                    scrollTo(rightBorder - width, 0)
                    return true
                }
                scrollBy(scrolledX, 0)
                mXLastMove = mXMove
            }
            MotionEvent.ACTION_UP -> {
                //当手指抬起时，根据当前滚动值来判定应该滚动到那个子控件界面
                //计算收松开后要显示的页面 index
                targetIndex = if (mXDown > event.rawX) {
                    (scrollX + (width * 0.7).toInt()) / width
                } else {
                    (scrollX + (width * 0.3).toInt()) / width
                }
                val dx = targetIndex * width - scrollX
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                Log.e("-------->", "onTouchEvent: $targetIndex   $scrollX ")
                mScroller.startScroll(scrollX, 0, dx, 0)
                invalidate()
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
}