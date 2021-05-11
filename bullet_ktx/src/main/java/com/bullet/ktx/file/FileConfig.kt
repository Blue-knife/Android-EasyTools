package com.bullet.ktx.file

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File

/**
 * @Author petterp
 * @Date 5/8/21-4:36 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("StaticFieldLeak")
object FileConfig {

    lateinit var fileContext: Context

    // app要保存的位置
    var APP_FOLDER_PATH: String = "File"

    // 外部存储目录[仅限于Android9以下以及使用了 request属性的Android10使用]
    val SDCARD_DIR = Environment.getExternalStorageDirectory().path

    /** 获取外部存储位置中应用专属目录,Android10-11无需权限 */
    val FILE_DIRECTORY_PICTURES by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(fileContext, Environment.DIRECTORY_PICTURES)
    }
    val FILE_DIRECTORY_DOWNLOADS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(fileContext, Environment.DIRECTORY_DOWNLOADS)
    }
    val FILE_DIRECTORY_DCIM by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(fileContext, Environment.DIRECTORY_DCIM)
    }
    val FILE_DIRECTORY_DOCUMENTS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(fileContext, Environment.DIRECTORY_DOCUMENTS)
    }

    private fun getDirectory(context: Context, name: String): File =
        File(context.getExternalFilesDir(name), APP_FOLDER_PATH).apply {
            if (!this.exists()) {
                this.mkdir()
            }
        }
}
