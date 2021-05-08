package com.bullet.ktx.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bullet.ktx.file.FileUtilsKtx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @Author petterp
 * @Date 4/23/21-7:12 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */

@SuppressLint("WrongConstant")
/** 判断app有没有安装指定apk */
fun Context.isAlreadyInstalled(packageName: String): Boolean {
    val packageManager = packageManager
    try {
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        if (packageInfo != null) {
            return true
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return false
}

// FIXME: 4/23/21 因微博sdk在Android10停用了分区存储 故这里采用系统分享
/** 调用微博分享 */
fun shareSinaText(context: Context, url: String, message: String) {
    val sinaPageName = "com.sina.weibo"
    if (!context.isAlreadyInstalled(sinaPageName)) {
        Toast.makeText(context, "未安装微博", Toast.LENGTH_SHORT).show()
        return
    }
    Toast.makeText(context, "开始分享...", Toast.LENGTH_SHORT).show()
    try {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName(sinaPageName, "com.sina.weibo.composerinde.ComposerDispatchActivity")
            putExtra(Intent.EXTRA_TEXT, "$url $message")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
suspend fun shareSinaImage(context: Context, filePath: String, bitmap: Bitmap?) {
    val sinaPageName = "com.sina.weibo"
    if (!context.isAlreadyInstalled(sinaPageName)) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "未安装微博", Toast.LENGTH_SHORT).show()
        }
        return
    }
    withContext(Dispatchers.Main) {
        Toast.makeText(context, "开始分享...", Toast.LENGTH_SHORT).show()
    }
    try {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            component = ComponentName(sinaPageName, "com.sina.weibo.composerinde.ComposerDispatchActivity")
            if (filePath.isEmpty()) {
                if (bitmap == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "分享发生错误,截图失败", Toast.LENGTH_SHORT).show()
                    }
                }
                val uri = FileUtilsKtx.saveBitmapToPictures(
                    bitmap!!, context,
                    "${System.currentTimeMillis()}.jpg", mimeType = "image/*"
                )
                putExtra(Intent.EXTRA_STREAM, uri)
            } else {
                val file = File(filePath)
                when {
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q -> {
                        val uri = FileUtilsKtx.saveFileToStorage(file, context, file.name, mimeType = "image/*", path = "Pictures/lanfan")
                        putExtra(Intent.EXTRA_STREAM, uri)
                    }
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N -> {
                        val imageUri: Uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider", // (use your app signature + ".provider" )
                            file
                        )
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                    }
                    else -> {
                        putExtra(Intent.EXTRA_STREAM, file)
                    }
                }
            }
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
