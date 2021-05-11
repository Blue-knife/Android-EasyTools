package com.bullet.ktx.file

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
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

    private const val TAG = "FileUtilsKtx"

    /** 是否可写入外部存储 */
    val isExternalStorageWritable: Boolean
        get() =
            FileExt.isExternalStorageWritable()

    /** 是否可读取外部存储 */
    val isExternalStorageReadable: Boolean
        get() =
            FileExt.isExternalStorageReadable()

    /**
     * 保存bitmap到公共存储目录
     * fileName 文件名
     * dirName 文件夹名字 示例 easy/blue
     * */
    fun saveBitmapToPictures(
        bitmap: Bitmap,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        quality: Int = 80,
        mimeType: String = "image/*",
        context: Context = FileConfig.fileContext,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): Uri? {
        // 当前不可写入
        if (!isExternalStorageWritable) return null
        val path = FileExt.getAppDirectoryPath(Environment.DIRECTORY_PICTURES, folderPath)
        val uri = FileExt.createStorageUri(context, fileName, path, mimeType)
        saveBitMap(context.contentResolver, uri, bitmap, quality, compressFormat)
        return uri
    }

    /**
     * 保存文件到公共存储目录
     * */
    fun saveFileToStorage(
        file: File,
        fileName: String,
        path: String = FileConfig.APP_FOLDER_PATH,
        context: Context = FileConfig.fileContext,
        mimeType: String = "*/*"
    ): Uri? {
        if (!isExternalStorageWritable) return null
        val uri = FileExt.createStorageUri(context, fileName, path, mimeType)
        saveFile(context.contentResolver, uri, file.inputStream())
        return uri
    }

    fun delete(fileUri: Uri, context: Context = FileConfig.fileContext) {
        context.contentResolver.delete(fileUri, null, null)
    }

    @SuppressLint("NewApi")
    fun uriToFile(uri: Uri, context: Context = FileConfig.fileContext): File? {
        val sdkVersion = Build.VERSION.SDK_INT
        return if (uri.scheme == ContentResolver.SCHEME_FILE)
            uri.toFile()
        else if (uri.scheme == ContentResolver.SCHEME_CONTENT && sdkVersion >= Build.VERSION_CODES.Q) {
            if (sdkVersion == Build.VERSION_CODES.Q && Environment.isExternalStorageLegacy()) {
                UriExt.uri2FileReal(uri, context)
            } else {
                FileExt.saveUriToCacheDir(uri, context)
            }
        } else {
            UriExt.uri2FileReal(uri, context)
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

    private fun saveFile(resolver: ContentResolver, uri: Uri?, inputStream: FileInputStream) =
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
