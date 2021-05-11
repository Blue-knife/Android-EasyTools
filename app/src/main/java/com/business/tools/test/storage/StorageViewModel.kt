package com.business.tools.test.storage

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bullet.core.ToastUtils
import com.bullet.ktx.file.FileExt
import com.bullet.ktx.file.FileUtilsKtx
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

    fun saveFile(saveName: String) {
        if (bitMap == null) return
        viewModelScopeIO {
            val fileName =
                if (saveName.isEmpty()) "${System.currentTimeMillis()}.jpeg" else "$saveName.jpeg"
            uri = FileUtilsKtx.saveBitmapToPictures(bitMap!!, fileName)
            withContextMain {
                if (uri != null) {
                    ToastUtils.showText("写入成功，地址--> $uri")
                }
            }
        }
    }

    fun readerFile() {
        if (uri == null) {
            ToastUtils.showText("生成的图片未保存到存储中")
            return
        }
        viewModelScopeIO {
            try {
                file = FileUtilsKtx.uriToFile(uri!!) ?: return@viewModelScopeIO
                withContextMain {
                    val fileName = file?.name
                    val fileSize = file?.length()
                    val path = file?.path
                    ToastUtils.showLongText("文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
                    Log.e("petterp", "\n 文件名-> $fileName   \n 文件大小-> $fileSize   \n  文件路径-> $path")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return
    }

    fun fileFromUri() {
        if (file == null) return
        val uri = FileExt.fileToShareUri(file!!)
        ToastUtils.showText(uri?.toString())
    }
}
