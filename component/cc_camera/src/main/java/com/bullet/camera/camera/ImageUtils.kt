package com.bullet.camera.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import androidx.core.os.EnvironmentCompat
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    /**
     * 根据 Uri获取图片地址
     *
     * @param context Context
     * @param uri 图片 uri
     * @return 返回正式路径，该路径位于沙箱目录下，图片是经过压缩的
     */
    @JvmStatic
    fun getPath(context: Context, uri: Uri): String? {
        val path =
                if (VersionUtils.isAndroidQ()) {
                    UriUtils.getPathForUri(context, uri)
                } else {
                    val file = UriUtils.getFileByUri(uri, context)
                    file?.absolutePath
                }

        if (path != null) {
            val bitmap = Utils.decodeSampledBitmapFromFile(context, path, 720, 1080)
            if (bitmap != null) {
                val name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.getDefault())).toString()
                var imagePath = Utils.getImageCacheDir(context)
                imagePath = Utils.saveImage(bitmap, imagePath, name)
                return imagePath
            }
        }
        return null
    }

    /**
     * 用于 Android10 保存 image
     *
     * @param context
     * @return 图片 url
     */
    @JvmStatic
    fun saveImageWithAndroidQ(context: Context): Uri? {
        val status = Environment.getExternalStorageState()
        val timeFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val time = System.currentTimeMillis()
        val imageName = timeFormatter.format(Date(time))
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        val values = ContentValues(2)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        return if (status == Environment.MEDIA_MOUNTED) {
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            context.contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values)
        }
    }

    /**
     * 用于 Android10 以下获获取 image 位置
     *
     * @return file
     */
    @JvmStatic
    fun saveImageFile(): File? {
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = String.format("JPEG_%s.jpg", timeStamp)
            val storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES)
            if (!storageDir.exists()) {
                storageDir.mkdir()
            }
            val tempFile = File(storageDir, imageFileName)
            return if (Environment.MEDIA_MOUNTED != EnvironmentCompat.getStorageState(tempFile)) {
                null
            } else tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


}