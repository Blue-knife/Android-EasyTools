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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
        @JvmStatic
        fun createStorageUri(
            context: Context,
            fileName: String,
            path: String,
            mimeType: String = "*/*"
        ): Uri? {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
            } else {
                val fileDir = File(path)
                if (!fileDir.exists()) {
                    fileDir.mkdir()
                }
                contentValues.put(MediaStore.MediaColumns.DATA, path + fileName)
            }
            val external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver = context.contentResolver
            // 健壮性判断,如果文件夹被删除，则让系统去寻找位置
            return try {
                resolver.insert(external, contentValues)
            } catch (e: IllegalArgumentException) {
                contentValues.remove(MediaStore.Images.Media.RELATIVE_PATH)
                resolver.insert(external, contentValues)
            }
        }

        /** file转Uri
         * - 1. Version >=Android11 的处理(可直接操作共有目录File)
         * - 2. Version = Android10 的处理(不可操作File,需要复制到沙盒完成转换)
         * - 3. Version >= Android-N 的处理(FileProvider,读写权限)
         * - 4. Version -> Android-M 的处理(读写权限)
         * - 5. Version -> 默认处理
         * */
        @JvmStatic
        fun fileToShareUri(file: File, context: Context = FileConfig.fileContext): Uri? {
            return when {
                // 拿不到外部存储的File,所以得先把file转存到外部储存
                Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                    val mimeType = file.mimeType
                    FileUtilsKtx.saveFileToStorage(file, file.name, mimeType = mimeType)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider", // (use your app signature + ".provider" )
                        file
                    )
                }
                // Android11支持直接操作File
                else -> {
                    file.toUri()
                }
            }
        }
    }
}
