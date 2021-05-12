package com.business.tools.test.storage

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bullet.core.ToastUtils
import com.bullet.ktx.file.FileKtx
import com.bullet.ktx.file.UriExt
import com.cloudx.ktx.core.viewModelScopeIO
import com.cloudx.ktx.core.withContextMain
import java.io.File

/**
 * @Author petterp
 * @Date 5/8/21-3:33 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
class StorageViewModel : ViewModel() {
    // 1
    var bitMap: Bitmap? = null

    // 2
    var uri: Uri? = null

    // 3
    var file: File? = null

    fun saveBitmapFromDir(saveName: String) {
        if (bitMap == null) return
        viewModelScopeIO {
            val fileName =
                if (saveName.isEmpty()) "${System.currentTimeMillis()}.jpeg" else "$saveName.jpeg"
            file = FileKtx.saveBitmapToDir(bitMap!!, fileName)
            withContextMain {
                if (file != null) {
                    val fileName = file!!.name
                    val fileSize = file!!.length()
                    val path = file!!.path
                    ToastUtils.showLongText("文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
                    Log.e("petterp", "\n 文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
                }
            }
        }
    }

    fun fileFromUri() {
        if (file == null) return
        uri = UriExt.fileFromUri(file!!)
        ToastUtils.showText(uri?.toString())
    }

    fun uriFromFile() {
        if (uri == null) return
        val file = UriExt.uriFromFile(uri!!)
        file?.let {
            val fileName = file.name
            val fileSize = file.length()
            val path = file.path
            ToastUtils.showLongText("文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
            Log.e("petterp", "\n 文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
        }
    }

    fun saveFileToStorage() {
        try {
            if (file == null) return
            val uri = FileKtx.saveFileToDownload(file!!, "${file!!.name}", mimeType = "image/jpeg")
            ToastUtils.showText(uri?.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteUri() {
        if (uri == null) return
        FileKtx.delete(uri!!)
    }
}
