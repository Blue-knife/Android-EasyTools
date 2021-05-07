package com.bullet.ktx.file

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream

/**
 * @Author petterp
 * @Date 4/21/21-5:24 PM
 * @Email ShiyihuiCloud@163.com
 * @Function 适配了 分区存储 的文件读写工具
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
object FileUtilsKtx {

    /**
     * fileName 文件名
     * dirName 文件夹名字 示例 easy/blue
     * */
    lateinit var applicationContext: Context
    private const val APP_FOLDER_NAME = "lanfan"
    val FILE_DIRECTORY_PICTURES by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(applicationContext, Environment.DIRECTORY_PICTURES)
    }
    val FILE_DIRECTORY_DOWNLOADS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(applicationContext, Environment.DIRECTORY_DOWNLOADS)
    }
    val FILE_DIRECTORY_DCIM by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(applicationContext, Environment.DIRECTORY_DCIM)
    }
    val FILE_DIRECTORY_DOCUMENTS by lazy(LazyThreadSafetyMode.NONE) {
        getDirectory(applicationContext, Environment.DIRECTORY_DOCUMENTS)
    }

    suspend fun saveBitmapToPictures(
        bitmap: Bitmap,
        context: Context,
        fileName: String,
        folderPath: String = APP_FOLDER_NAME,
        quality: Int = 80,
        mimeType: String = "image/*",
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Uri? {
        val path = getAppDirectoryPath(Environment.DIRECTORY_PICTURES, folderPath)
        val uri = getStorageUri(context, fileName, path, mimeType)
        saveBitMap(context.contentResolver, uri, bitmap, quality, compressFormat)
        return uri
    }

    suspend fun saveFileToStorage(
        file: File,
        context: Context,
        fileName: String,
        path: String = APP_FOLDER_NAME,
        mimeType: String = "*/*"
    ): Uri? {
        val uri = getStorageUri(context, fileName, path, mimeType)
        saveFile(context.contentResolver, uri, file.inputStream())
        return uri
    }

    /**
     * 将Uri转为File
     * - 1. Version >=Android11 的处理(可直接操作共有目录File)
     * - 2. Version = Android10 的处理(不可操作File,需要复制到沙盒完成转换)
     * - 3. Version >= Android-N 的处理(FileProvider,读写权限)
     * - 4. Version -> Android-M 的处理(读写权限)
     * - 5. Version -> 默认处理
     * */
    fun uriToFile() {
        // TODO: 4/23/21  
    }

    private fun getDirectory(context: Context, name: String): File =
        File(context.getExternalFilesDir(name), APP_FOLDER_NAME).apply {
            if (!this.exists()) {
                this.mkdir()
            }
        }

    private fun getAppDirectoryPath(directory: String, folderPath: String): String {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // 获取外部存储目录
            "${Environment.getExternalStorageDirectory().absolutePath}/" +
                "$directory/$folderPath/"
        } else {
            // relative path
            "$directory/$folderPath/"
        }
    }

    private fun getStorageUri(
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

    private fun saveBitMap(
        resolver: ContentResolver,
        uri: Uri?,
        bitmap: Bitmap,
        quality: Int = 80,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ) {
        uri?.let {
            resolver.openOutputStream(it)?.use { os ->
                bitmap.compress(compressFormat, quality, os)
            }
        }
    }

    private fun saveFile(resolver: ContentResolver, uri: Uri?, inputStream: FileInputStream): Uri? =
        uri?.also { it ->
            resolver.openOutputStream(it)?.use { os ->
                val buffer = ByteArray(1024)
                var bytes = inputStream.read(buffer)
                while (bytes >= 0) {
                    os.write(buffer, 0, bytes)
                    os.flush()
                    bytes = inputStream.read(buffer)
                }
            }
        }
}
