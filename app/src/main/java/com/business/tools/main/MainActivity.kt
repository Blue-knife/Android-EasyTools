package com.business.tools.main

import NoticeActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.business.audio.mediaplayer.core.MusicPlayerActivity
import com.business.tools.test.DialogActivity
import com.business.tools.test.*
import com.business.toos.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.hide()
        initRv()
        main_share.setOnClickListener(listener)
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
                Pair("自定义View", ViewsActivity::class.java),
                Pair("开启 JobService", null),
                Pair("播放 Music", MusicPlayerActivity::class.java),
                Pair("一键式文件下载", DownLoadActivity::class.java),
                Pair("通知相关", NoticeActivity::class.java)
        )
    }

    private val listener = View.OnClickListener {
        val shareDialog = ShareDialog(this)
        shareDialog.setShareContent("https://github.com/Blue-knife/Android-EasyTools")
        shareDialog.show()
    }

}
