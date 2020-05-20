package com.business.tools.main


import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.business.audio.mediaplayer.core.MusicPlayerActivity
import com.business.tools.test.DialogActivity
import com.business.tools.test.*
import com.business.toos.R
import com.example.core.base.BaseSkinActivity
import com.example.core.base.skin.SkinManager
import com.example.core.base.skin.config.SkinPreUtils
import com.example.ui.dialog.ToastDialog

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseSkinActivity() {

    override fun layout(): Int {
        return R.layout.activity_main
    }

    override fun bindView() {
        initRv()
        copyCache()
        main_share.setOnClickListener(::shareClick)
        main_Skin.setOnClickListener(::skinClick)
    }

    private fun copyCache() {
        val cacheDir = cacheDir
        val outFile = File(cacheDir, "easy.skin")
        val input = resources.assets.open("easy.skin")
        input.copyTo(outFile.outputStream())
    }


    private fun skinClick(view: View) {
        val image = view as AppCompatImageView
        if (SkinPreUtils.getTag()) {
            SkinManager.restoreDefault()
            SkinPreUtils.setTag(false)
            image.setImageResource(R.drawable.main_daytime)
        } else {
            val code = SkinManager.loadSkin(cacheDir.path + "/easy.skin")
//            ToastDialog.show(this, "$code")
            SkinPreUtils.setTag(true)
            image.setImageResource(R.drawable.main_night)
        }
    }


    private fun shareClick(view: View) {
        val shareDialog = ShareDialog(this)
        shareDialog.setShareContent("https://github.com/Blue-knife/Android-EasyTools")
        shareDialog.show()
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


}
