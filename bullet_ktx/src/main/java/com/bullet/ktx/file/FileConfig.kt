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
    var APP_FOLDER_PATH: String = "EasyTools"

    /** app内部缓存路径 */
    val PRIVATE_CACHE_DIR by lazy(LazyThreadSafetyMode.NONE) {
        fileContext.externalCacheDir?.absolutePath ?: ""
    }

    /** app内部文件路径 */
    val PRIVATE_FILE_DIR by lazy(LazyThreadSafetyMode.NONE) {
        fileContext.filesDir?.absolutePath ?: ""
    }

    /** 外部存储目录[仅限于Android9以下以及使用了 requestLegacyExternalStorage 属性的Android10使用] */
    val SDCARD_DIR: String by lazy(LazyThreadSafetyMode.NONE) {
        Environment.getExternalStorageDirectory().absolutePath
    }

    /** 获取外部存储位置中应用专属目录,Android10-11无需权限 */
    val FILE_DIRECTORY_PICTURES by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory("${Environment.DIRECTORY_PICTURES}/$APP_FOLDER_PATH")
    }
    val FILE_DIRECTORY_DOWNLOADS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory("${Environment.DIRECTORY_DOWNLOADS}/$APP_FOLDER_PATH/")
    }
    val FILE_DIRECTORY_DCIM by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory("${Environment.DIRECTORY_DCIM}/$APP_FOLDER_PATH/")
    }
    val FILE_DIRECTORY_DOCUMENTS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory("${Environment.DIRECTORY_DOCUMENTS}/$APP_FOLDER_PATH/")
    }

    fun getDirectory(path: String): File =
        File(path).apply {
            if (!this.exists()) {
                this.mkdir()
            }
        }
}
