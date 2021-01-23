package com.business.tools.test

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.business.tools.views.page.PageAdapter
import com.business.toos.R
import com.bullet.core.ToastUtils
import com.bullet.core.base.BaseSkinActivity
import com.bullet.ui.customView.CustomTextView
import com.bullet.ui.customView.DrawingView
import com.bullet.ui.dialog.base.FastDialog
import kotlinx.android.synthetic.main.activity_views.*
import java.util.*

/**
 * @author 345
 * @date 2019/12/25
 */
class ViewsActivity : BaseSkinActivity() {


    override fun layout(): Int {
        return R.layout.activity_views
    }

    override fun bindView() {
        init()
    }

    private fun init() {
        val textView = findViewById<CustomTextView>(R.id.tv)
        textView.setText("Android-EasyTools" + "一个通用的业务代码解决方案")

        textView.setTvs(arrayOf("Android-EasyTools", "通用", "解决方案"),
                intArrayOf(Color.BLUE, Color.GREEN, Color.RED))
        textView.notifyTv()


        views_banner.setOnClickListener {
            startActivity(Intent(this,PageBannerActivity::class.java))
        }

        val list = ArrayList<String>()
        list.add("我是第一条数据")
        list.add("我是第二条数据")
        list.add("我是第三条数据")
        list.add("我是第四条数据")

        lifecycle.addObserver(carouse)
        carouse.upDataListAndView(list, 3000)
        carouse.startLooping()
        carouse.setOnItemClickListener { position -> ToastUtils.showText(list[position]) }


        //画板
        activity_drawing.setOnClickListener {
            val dialog = FastDialog.Builder(this)
                    .setContentView(R.layout.layout_drawing)
                    .setWidth(0.7f)
                    .build()
            dialog.show()
            val drawingView = dialog.getView<DrawingView>(R.id.layout_drawing)
            dialog.setOnClickListener(R.id.layout_save) {
                val file = drawingView?.getFile()
                val bitmap = BitmapFactory.decodeFile("${file?.path}")
                activity_views_image.visibility = View.VISIBLE
                activity_views_image.setImageBitmap(bitmap)
            }
            dialog.setOnClickListener(R.id.layout_reset) {
                ToastUtils.showCenterText("重置")
                drawingView?.resetCanvas()
            }
        }

    }




}

