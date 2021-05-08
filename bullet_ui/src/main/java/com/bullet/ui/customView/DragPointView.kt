package com.bullet.ui.customView

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.AbsListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager

/**
 * 自定义底部拖动小气泡
 */
@SuppressLint("AppCompatCustomView")
class DragPointView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {
    private var initBgFlag = false
    var dragListencer: OnDragListencer? = null
    private var backgroundColor = Color.parseColor("#f43530")
    private var pointView: PointView? = null
    private var x = 0
    private var y = 0
    private var r = 0
    private var scrollParent: ViewGroup? = null
    private val p = IntArray(2)

    fun getBackgroundColor(): Int {
        return backgroundColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = measuredWidth
        val h = measuredHeight
        if (w != h) { // 简单的将宽高搞成一样的,如果有更好的方法欢迎在我博客下方留言!
            val x = Math.max(w, h)
            setMeasuredDimension(x, x)
        }
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
        setBackgroundDrawable(createStateListDrawable((if (height > width) height else width) / 2, backgroundColor))
    }

    private fun initbg() {
        gravity = Gravity.CENTER
        viewTreeObserver.addOnPreDrawListener(
            ViewTreeObserver.OnPreDrawListener {
                if (!initBgFlag) {
                    setBackgroundDrawable(
                        createStateListDrawable(
                            (if (height > width) height else width) / 2, backgroundColor
                        )
                    )
                    initBgFlag = true
                    return@OnPreDrawListener false
                }
                true
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val root = rootView
        if (root == null || root !is ViewGroup) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                root.getLocationOnScreen(p)
                scrollParent = getScrollParent()
                if (scrollParent != null) {
                    scrollParent!!.requestDisallowInterceptTouchEvent(true)
                }
                val location = IntArray(2)
                getLocationOnScreen(location)
                x = location[0] + width / 2 - p[0]
                y = location[1] + height / 2 - p[1]
                r = (width + height) / 4
                pointView = PointView(context)
                pointView!!.layoutParams = ViewGroup.LayoutParams(root.getWidth(), root.getHeight())
                isDrawingCacheEnabled = true
                pointView?.catchBitmap = drawingCache
                pointView!!.setLocation(x.toFloat(), y.toFloat(), r.toFloat(), event.rawX - p[0], event.rawY - p[1])
                root.addView(pointView)
                visibility = View.INVISIBLE
            }
            MotionEvent.ACTION_MOVE -> pointView!!.refrashXY(event.rawX - p[0], event.rawY - p[1])
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (scrollParent != null) {
                    scrollParent!!.requestDisallowInterceptTouchEvent(false)
                }
                if (!pointView!!.broken) { // 没有拉断
                    pointView!!.cancel()
                } else if (pointView!!.nearby) { // 拉断了,但是又回去了
                    pointView!!.cancel()
                } else { // 彻底拉断了
                    pointView!!.broken()
                }
            }
            else -> {
            }
        }
        return true
    }

    private fun getScrollParent(): ViewGroup? {
        var p: View = this
        while (true) {
            var v: View
            v = try {
                p.parent as View
            } catch (e: ClassCastException) {
                return null
            }
            if (v == null) return null
            if (v is AbsListView || v is ScrollView || v is ViewPager) {
                return v as ViewGroup
            }
            p = v
        }
    }

    interface OnDragListencer {
        fun onDragOut()
    }

    internal inner class PointView(context: Context?) : View(context) {
        var catchBitmap: Bitmap? = null
        private var c1: Circle? = null
        private var c2: Circle? = null
        private var paint: Paint? = null
        private val path = Path()
        private val maxDistance = 8 // 10倍半径距离视为拉断
        var broken = // 是否拉断过
            false
        private var out = // 放手的时候是否拉断
            false
        var nearby = false
        var brokenProgress = 0
        fun init() {
            paint = Paint()
            paint!!.color = backgroundColor
            paint!!.isAntiAlias = true
        }

        fun setLocation(c1X: Float, c1Y: Float, r: Float, endX: Float, endY: Float) {
            broken = false
            c1 = Circle(c1X, c1Y, r)
            c2 = Circle(endX, endY, r)
        }

        fun refrashXY(x: Float, y: Float) {
            c2!!.x = x
            c2!!.y = y
            // 以前的半径应该根据距离缩小点了
            // 计算出距离
            val distance = c1!!.getDistance(c2)
            val rate = 10
            c1!!.r = (c2!!.r * c2!!.r * rate / (distance + c2!!.r * rate)).toFloat()
            Log.i("info", "c1: " + c1!!.r)
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawColor(Color.TRANSPARENT)
            if (out) {
                val dr = c2!!.r / 2 + c2!!.r * 4 * (brokenProgress / 100f)
                Log.i("info", "dr$dr")
                canvas.drawCircle(c2!!.x, c2!!.y, c2!!.r / (brokenProgress + 1), paint!!)
                canvas.drawCircle(c2!!.x - dr, c2!!.y - dr, c2!!.r / (brokenProgress + 2), paint!!)
                canvas.drawCircle(c2!!.x + dr, c2!!.y - dr, c2!!.r / (brokenProgress + 2), paint!!)
                canvas.drawCircle(c2!!.x - dr, c2!!.y + dr, c2!!.r / (brokenProgress + 2), paint!!)
                canvas.drawCircle(c2!!.x + dr, c2!!.y + dr, c2!!.r / (brokenProgress + 2), paint!!)
            } else {
                // 绘制手指跟踪的圆形
                if (catchBitmap == null || catchBitmap != null && catchBitmap!!.isRecycled) {
                    return
                }
                canvas.drawBitmap(catchBitmap!!, c2!!.x - c2!!.r, c2!!.y - c2!!.r, paint)
                path.reset()
                val deltaX = c2!!.x - c1!!.x
                val deltaY = -(c2!!.y - c1!!.y)
                val distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY.toDouble())
                val sin = deltaY / distance
                val cos = deltaX / distance
                nearby = distance < c2!!.r * maxDistance
                if (nearby && !broken) {
                    canvas.drawCircle(c1!!.x, c1!!.y, c1!!.r, paint!!)
                    path.moveTo((c1!!.x - c1!!.r * sin).toFloat(), (c1!!.y - c1!!.r * cos).toFloat())
                    path.lineTo((c1!!.x + c1!!.r * sin).toFloat(), (c1!!.y + c1!!.r * cos).toFloat())
                    path.quadTo(
                        (c1!!.x + c2!!.x) / 2, (c1!!.y + c2!!.y) / 2, (c2!!.x + c2!!.r * sin).toFloat(),
                        (
                            c2!!.y + c2!!.r
                                * cos
                            ).toFloat()
                    )
                    path.lineTo((c2!!.x - c2!!.r * sin).toFloat(), (c2!!.y - c2!!.r * cos).toFloat())
                    path.quadTo(
                        (c1!!.x + c2!!.x) / 2, (c1!!.y + c2!!.y) / 2, (c1!!.x - c1!!.r * sin).toFloat(),
                        (
                            c1!!.y - c1!!.r
                                * cos
                            ).toFloat()
                    )
                    canvas.drawPath(path, paint!!)
                } else {
                    broken = true // 已经拉断了
                }
            }
        }

        fun cancel() {
            val duration = 150
            val set = AnimatorSet()
            val animx = ValueAnimator.ofFloat(c2!!.x, c1!!.x)
            animx.duration = duration.toLong()
            animx.interpolator = OvershootInterpolator(2f)
            animx.addUpdateListener { animation ->
                c2!!.x = animation.animatedValue as Float
                invalidate()
            }
            val animy = ValueAnimator.ofFloat(c2!!.y, c1!!.y)
            animy.duration = duration.toLong()
            animy.interpolator = OvershootInterpolator(2f)
            animy.addUpdateListener { animation ->
                c2!!.y = animation.animatedValue as Float
                invalidate()
            }
            set.playTogether(animx, animy)
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val vg = this@PointView.parent as ViewGroup
                    vg.removeView(this@PointView)
                    this@DragPointView.visibility = VISIBLE
                }
            })
            set.start()
        }

        fun broken() {
            out = true
            val duration = 500
            val a = ValueAnimator.ofInt(0, 100)
            a.duration = duration.toLong()
            a.interpolator = LinearInterpolator()
            a.addUpdateListener { animation ->
                brokenProgress = animation.animatedValue as Int
                invalidate()
            }
            a.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    val vg = this@PointView.parent as ViewGroup
                    vg.removeView(this@PointView)
                }
            })
            a.start()
            if (dragListencer != null) {
                dragListencer!!.onDragOut()
            }
        }

        internal inner class Circle(var x: Float, var y: Float, var r: Float) {
            fun getDistance(c: Circle?): Double {
                val deltaX = x - c!!.x
                val deltaY = y - c.y
                return Math.sqrt(deltaX * deltaX + deltaY * deltaY.toDouble())
            }
        }

        init {
            init()
        }
    }

    companion object {
        /**
         * @param radius 圆角角度
         * @param color  填充颜色
         * @return StateListDrawable 对象
         * @author zy
         */
        fun createStateListDrawable(radius: Int, color: Int): StateListDrawable {
            val bg = StateListDrawable()
            val gradientStateNormal = GradientDrawable()
            gradientStateNormal.setColor(color)
            gradientStateNormal.shape = GradientDrawable.RECTANGLE
            gradientStateNormal.cornerRadius = 50f
            gradientStateNormal.setStroke(0, 0)
            bg.addState(View.EMPTY_STATE_SET, gradientStateNormal)
            return bg
        }
    }

    init {
        initbg()
    }
}
