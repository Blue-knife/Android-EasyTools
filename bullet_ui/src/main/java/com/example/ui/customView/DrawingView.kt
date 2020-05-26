package com.example.ui.customView

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.io.*


/**
 * @name DrawingView
 * @package com.example.ui.customView
 * @author 345 QQ:1831712732
 * @time 2020/5/26 20:46
 * @description
 */

class DrawingView : View {

    private lateinit var mPaint: Paint
    private lateinit var mPath: Path
    private var cacheBitmap //用户保存签名的Bitmap
            : Bitmap? = null
    private var cacheCanvas //用户保存签名的Canvas
            : Canvas? = null

    private var lift = 0f
    private var right: Float = 0f
    private var top: Float = 0f
    private var bottom: Float = 0f


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() { //初始化画笔
        mPaint = Paint()
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 10f
        mPath = Path()
    }

    /**
     * 在控件大小发生改变是调用
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        cacheBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(cacheBitmap!!)
        //设置背景色为透明
        cacheCanvas?.drawColor(Color.WHITE)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //路径起点
                mPath.moveTo(event.x, event.y)
                if (lift == 0f) {
                    lift = event.x
                }
                if (right == 0f) {
                    lift = event.x
                }
                if (top == 0f) {
                    top = event.y
                }
                if (bottom == 0f) {
                    bottom = event.x
                }
                setVertexCoordinates(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(event.x, event.y)
                postInvalidate()
                setVertexCoordinates(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> //将签名绘制到缓存画布上
                cacheCanvas?.drawPath(mPath, mPaint)
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制签名路径
        canvas.drawPath(mPath, mPaint)
    }

    /**
     * 重置画布
     */
    fun resetCanvas() {
        mPath.reset()
        invalidate()
        cacheBitmap = null
        cacheCanvas = null
        cacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas(cacheBitmap!!)
        //设置背景色为透明
        cacheCanvas?.drawColor(Color.WHITE)
        bottom = 0f
        top = bottom
        right = top
        lift = right
    }

    /**
     * 设置顶点坐标
     */
    private fun setVertexCoordinates(x: Float, y: Float) {
        if (lift > x) lift = x
        if (right < x) right = x
        if (top > y) top = y
        if (bottom < y) bottom = y
    }


    /**
     * 剪裁画布把多余的画布去掉只保留签名部分
     *
     * @param blank 边距
     */
    private fun cropCanvas(blank: Int): Bitmap {
        return cacheBitmap!!
    }

    /**
     * 判断有没有签名
     * @return true 有 反之没有
     */
    fun isEmpty(): Boolean {
        return mPath.isEmpty()
    }

    /**
     * 返回 bitmap
     */
    fun getBitmap(blank: Int): Bitmap {
        return cropCanvas(blank)
    }

    /**
     * 保存签名到本地
     *
     * @param path  保存路径
     * @param blank 边距
     */
    @SuppressLint("WrongThread")
    @Throws(IOException::class)
    fun save(path: String?, fileName: String?, blank: Int) {
        val mBitmap = cropCanvas(blank)
        val foder = File(path)
        if (!foder.exists()) foder.mkdirs()
        val myCaptureFile = File(path, fileName)
        if (!myCaptureFile.exists()) myCaptureFile.createNewFile()
        try {
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(myCaptureFile))
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}