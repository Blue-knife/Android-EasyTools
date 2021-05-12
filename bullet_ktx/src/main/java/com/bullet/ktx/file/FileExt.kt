package com.bullet.ktx.file

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

/**
 * @Author petterp
 * @Date 5/8/21-4:36 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */

/** 获取文件mime */
val File.mimeType: String
    get() {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: FileExt.mimeMap[extension] ?: "*/*"
    }

/** 获取文件mime */
val File.extension: String
    get() {
        var suffix = ""
        val file = File(path)
        val name = file.name
        val idx = name.lastIndexOf(".")
        if (idx > 0) {
            suffix = name.substring(idx + 1)
        }
        return suffix
    }

class FileExt {
    companion object {

        @JvmStatic
        val mimeMap by lazy(LazyThreadSafetyMode.NONE) {
            val map: MutableMap<String, String> = HashMap()
            map["rar"] = "application/x-rar-compressed"
            map["jpg"] = "image/jpeg"
            map["png"] = "image/jpeg"
            map["jpeg"] = "image/jpeg"
            map["zip"] = "application/zip"
            map["pdf"] = "application/pdf"
            map["doc"] = "application/msword"
            map["docx"] = "application/msword"
            map["wps"] = "application/msword"
            map["xls"] = "application/vnd.ms-excel"
            map["et"] = "application/vnd.ms-excel"
            map["xlsx"] = "application/vnd.ms-excel"
            map["ppt"] = "application/vnd.ms-powerpoint"
            map["html"] = "text/html"
            map["htm"] = "text/html"
            map["txt"] = "text/html"
            map["mp3"] = "audio/mpeg"
            map["mp4"] = "video/mp4"
            map["3gp"] = "video/3gpp"
            map["wav"] = "audio/x-wav"
            map["avi"] = "video/x-msvideo"
            map["flv"] = "flv-application/octet-stream"
            map[""] = "*/*"
            map
        }

        /** 获取外部存储路径 */
        @JvmStatic
        fun getAppDirectoryPath(directory: String, folderPath: String): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // 获取外部存储目录
                "${Environment.getExternalStorageDirectory().absolutePath}/" +
                    "$directory/$folderPath"
            } else {
                // relative path
                "$directory/$folderPath"
            }
        }

        /** 判断外部存储是否可写入 */
        @JvmStatic
        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        /** 判断外部存储是否可读取 */
        @JvmStatic
        fun isExternalStorageReadable(): Boolean {
            return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
        }

        /** 保存uri到缓存目录 */
        @JvmStatic
        @RequiresApi(Build.VERSION_CODES.Q)
        fun saveUriToCacheDir(uri: Uri, context: Context): File? {
            val contentResolver = context.contentResolver
            val displayName = "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri))
            }"
            val ios = contentResolver.openInputStream(uri)
            return if (ios != null) {
                File("${context.cacheDir.absolutePath}/$displayName")
                    .apply {
                        val fos = FileOutputStream(this)
                        FileUtils.copy(ios, fos)
                        fos.close()
                        ios.close()
                    }
            } else null
        }

        /** 创建共享文件Uri */
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        @JvmStatic
        fun createStorageUri(
            context: Context,
            fileName: String,
            path: String,
            mimeType: String = "*/*"
        ): Uri? {
            val contentValues = ContentValues()
            with(contentValues) {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis())
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, path)
                val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                val resolver = context.contentResolver
                try {
                    resolver.insert(external, contentValues)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    null
                }
            } else {
                UriExt.fileFromUri(getFile(path, fileName))
            }
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun getFile(path: String, fileName: String): File {
            val f = FileConfig.getDirectory(path)
            return File("${f.absolutePath}/$fileName")
        }
    }
}
