package com.business.tools.test.storage

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bullet.core.ToastUtils
import com.bullet.ktx.file.FileConfig
import com.bullet.ktx.ui.singleClicks
import com.bullet.ktx.ui.toBitMap
import com.business.toos.databinding.ActivityStorageBinding
import com.xcf.lazycook.common.ktx.viewBindings
import kotlinx.android.synthetic.main.activity_storage.*
import java.io.File

/**
 * @Author petterp
 * @Date 5/8/21-12:00 PM
 * @Email ShiyihuiCloud@163.com
 * @Function 存储测试Activity
 */
class StorageActivity : AppCompatActivity(), View.OnClickListener {
    val bing by viewBindings<ActivityStorageBinding>()

    val viewModel by viewModels<StorageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bing.root)
        bing.createBitmap.singleClicks(this)
        bing.ivPlaceHolder.singleClicks(this)
        bing.btnWriteBitMapFromDir.singleClicks(this)
        bing.btnFileFromUri.singleClicks(this)
        bing.btnUriFromFile.singleClicks(this)
        bing.btnSaveFileFromStorage.singleClicks(this)
        bing.deleteUri.singleClicks(this)
        bing.btnTestRead.singleClicks(this)
        initFileConfig()
    }

    // 初始化存储机制
    private fun initFileConfig() {
        FileConfig.fileContext = applicationContext
        FileConfig.APP_FOLDER_PATH = "EasyTools"
        val builder = StringBuilder()
        builder.append("Android版本-").append(Build.VERSION.SDK_INT).append("\n")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            builder.append("是否开启了旧版存储机制(仅针对Android-10)")
                .append(Environment.isExternalStorageLegacy())
        bing.tvPhoneInfo.text = builder.toString()
    }

    override fun onClick(v: View?) {
        when (v) {
            // 生成bitmap
            bing.createBitmap -> {
                val bitmap = bing.root.toBitMap()
                viewModel.bitMap = bitmap
                bing.ivPlaceHolder.setImageBitmap(bitmap)
                ToastUtils.showText("生成成功")
            }
            // 将bitmap保存到内部存储
            bing.btnWriteBitMapFromDir -> {
                val fileName = editFileName.text.toString().trim()
                viewModel.saveBitmapFromDir(fileName)
            }
            // 将file转为Uri
            bing.btnFileFromUri -> {
                viewModel.fileFromUri()
            }

            // 将file保存到外部存储
            bing.btnSaveFileFromStorage -> {
                viewModel.saveFileToStorage()
            }

            // 将uri转为File
            bing.btnUriFromFile -> {
                viewModel.uriFromFile()
            }

            // 删除转过来的Uri
            bing.deleteUri -> {
                viewModel.deleteUri()
            }
            bing.btnTestRead -> {
                val file = File("/storage/emulated/0/Download/EasyTools/1626323928113.jpeg")
                val bitmap = BitmapFactory.decodeFile(file.path)
                bing.ivPlaceHolder.setImageBitmap(bitmap)
            }
        }
    }
}
