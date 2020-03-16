package com.business.tools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.business.tools.basedialog.test.DialogActivity
import com.business.tools.camera.CameraActivity
import com.business.tools.flowlayout.test.FlowLayoutActivity
import com.business.tools.image_card.test.CardaActivity
import com.business.tools.utils.download.DownLoadActivity
import com.business.tools.views.ViewsActivity
import com.business.toos.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRv()
    }

    private fun initRv() {
        activity_main_recycle.layoutManager = LinearLayoutManager(this)
        activity_main_recycle.adapter = MainRvAdapter(initData(), this);
    }

    private fun initData(): List<Pair<String, Any?>> {
        return listOf(
                Pair("文件下载(未提供demo)", null),
                Pair("线程池工具(未提供demo)", null),
                Pair("3d图片展示", CardaActivity::class.java),
                Pair("通用Dialog", DialogActivity::class.java),
                Pair("流式布局FlowLayout", FlowLayoutActivity::class.java),
                Pair("状态栏工具", null),
                Pair("拍照，裁剪，扫描生成二维码等", CameraActivity::class.java),
                Pair("自定义Tv颜色", ViewsActivity::class.java),
                Pair("开启 JobService", null),
                Pair("一键式文件下载", DownLoadActivity::class.java)
        )
    }

}
