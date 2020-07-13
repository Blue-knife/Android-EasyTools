package com.example.ui.customView

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.core.ToastUtils
import java.io.*


/**
 * @name DrawingView
 * @package com.example.ui.customView
 * @author 345 QQ:1831712732
 * @time 2020/5/26 20:46
 * @description 画板
 */

class DrawingView : View {

    private lateinit var mPaint: Paint
    private lateinit var mPath: Path
    private var cacheBitmap //用户保存签名的Bitmap
            : Bitmap? = null
    private var cacheCanvas //用户保存签名的Canvas
            : Canvas? = null

    //位置
    private var mLeft: Float = 0f
    private var mRight: Float = 0f
    private var mTop: Float = 0f
    private var mBottom: Float = 0f

    //边距
    private var mPadding = 20f

    //线宽度
    private var mPaintWidth = 10f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() { //初始化画笔
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = mPaintWidth
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
                if (mLeft == 0f) {
                    mLeft = event.x
                }
                if (mTop == 0f) {
                    mTop = event.y
                }
                setVertexCoordinates(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(event.x, event.y)
                postInvalidate()
                //限制滑动的位置
                if (event.x < width && event.x >= 0) {
                    if (event.y < height && event.y >= 0) {
                        //如果是第二次按下，也需要记录位置
                        setVertexCoordinates(event.x, event.y)
                    }
                }
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
        mBottom = 0f
        mTop = mBottom
        mRight = mTop
        mLeft = mRight
    }

    /**
     * 记录四个边的位置
     */
    private fun setVertexCoordinates(x: Float, y: Float) {
        if (x > mRight) {
            mRight = x
        }
        if (x < mLeft) {
            mLeft = x
        }
        if (y > mBottom) {
            mBottom = y
        }
        if (y < mTop) {
            mTop = y
        }
    }

    /**
     * 返回 bitmap
     * @param blank ：边距
     */
    fun getBitmap(blank: Int): Bitmap? {
        return cropCanvas(blank.toFloat())
    }

    /**
     * 获取图片 file
     */
    fun getFile(): File? {
        val bitmap = getBitmap(15) ?: return null
        val uri = save(bitmap, "${System.currentTimeMillis()}.png")
                ?: throw FileNotFoundException("文件未找到")
        val query = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
        query?.moveToFirst()
        val index = query?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val path = query!!.getString(index!!)
        if (path == null) {
            ToastUtils.showText("获取失败")
            query.close()
        }
        return File(path)
    }

    /**
     * 保存图片到本地
     * @param displayName 图片名称，注意需要加上后缀名 .png
     */
    fun save(displayName: String): Uri? {
        val bitmap = getBitmap(15) ?: return null
        return save(bitmap, displayName)
    }

    fun save(bitmap: Bitmap, displayName: String): Uri? {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(MediaStore.MediaColumns.DATA, "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            }
            ToastUtils.showText("完成")
            return uri
        }
        return null
    }

    /**
     * 剪裁画布把多余的画布去掉只保留签名部分
     *
     * @param blank 边距
     */
    private fun cropCanvas(): Bitmap? {
        return cropCanvas(mPadding)
    }

    private fun cropCanvas(padding: Float): Bitmap? {
        val right = (mRight - mLeft)
        val height = (mBottom - mTop)

        if (right == 0f && height == 0f) {
            ToastUtils.showText("请进行签名")
            return null
        }

        val dip2px = dip2px(padding)
        //裁切签名的部分
        val cropBitmap = Bitmap.createBitmap(cacheBitmap!!, mLeft.toZero(), mTop.toZero(), right.toZero(), height.toZero())

        //设置边距
        val bitmap = Bitmap.createBitmap((cropBitmap.width + (dip2px * 2)), (cropBitmap.height + (dip2px * 2)), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(cropBitmap, dip2px.toFloat(), dip2px.toFloat(), mPaint)
        return bitmap
    }

    private fun Float.toZero(): Int {
        return if (this < 0f) {
            0
        } else {
            this.toInt()
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private fun dip2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}