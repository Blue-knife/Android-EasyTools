package com.bullet.ktx.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Petterp
 * on 2020-02-12
 * Function: 保存图片到相册
 */

/**
 * base64转bitmap
 * 需要去掉字符串的data:image/png;base64
 * 含有“data:image/xxx;base64”的头的编码，在decode的时候就要去掉，否则是无法还原成功的
 */
fun stringtoBitmap(base64Code: String): Bitmap? {
    return try {
        val bitmapArray = Base64.decode(base64Code.split(",")[1], Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


/**
 * bitmap转 base64
 */
fun bitmaptoString(bitmap: Bitmap): String? {
    return try {
        val bSystem = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bSystem)
        val bytes = bSystem.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 保存图片至相册
 */
fun decoderBaseFile(context: Context, base64Code: String, savePath: String) {
    try {
        val fileName = "${System.currentTimeMillis()}.jpg"
        //Android10自动保存到相册
        if (Build.VERSION.SDK_INT > 28) {
            FileQUtils.saveImageWithAndroidQ(context, fileName, "")
        } else {
            val file = File(savePath, fileName)
            val buffer = Base64.decode(base64Code, Base64.DEFAULT)
            FileOutputStream(file).use {
                it.write(buffer)
                //插入文件图库
                MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    file.absolutePath, fileName, null
                )
                //通知相册刷新
                FileUtils.refreshDCIM(context)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
