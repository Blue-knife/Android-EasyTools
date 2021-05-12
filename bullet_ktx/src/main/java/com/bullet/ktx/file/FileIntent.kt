package com.bullet.ktx.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import java.io.File

/**
 * @Author petterp
 * @Date 2021/5/11-11:56 上午
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
/**
 * Android 7.0+ 用于打开相应文件
 */
object FileIntent {
    fun openFile(filePath: String, context: Context, fileType: String? = null) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
            }
            val file = File(filePath)
            val data = Uri.fromFile(file)
            val mimeType = fileType ?: file.mimeType
            intent.setDataAndType(data, mimeType)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openFile(uri: Uri, context: Context, fileType: String) {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
            }
            intent.setDataAndType(uri, fileType)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
