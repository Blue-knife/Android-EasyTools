package com.bullet.ktx.file

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

/**
 * @Author petterp
 * @Date 4/21/21-5:24 PM
 * @Email ShiyihuiCloud@163.com
 * @Function 常用文件读写操作
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
object FileKtxUtils {

    private const val TAG = "FileKtx"

    /**
     * 保存bitmap到公共存储目录
     * fileName 文件名,需要增加类型 如果 xxx.jpeg
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
        if (!FileExt.isExternalStorageWritable()) return null
        val path = FileExt.getAppDirectoryPath(Environment.DIRECTORY_PICTURES, folderPath)
        val uri = FileExt.createStorageUri(context, fileName, path, mimeType)
        saveBitMap(context.contentResolver, uri, bitmap, quality, compressFormat)
        return uri
    }

    /** 保存bitmap到内部存储 */
    fun saveBitmapToDir(
        bitmap: Bitmap,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        quality: Int = 80,
        compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    ): File? {
        // 当前不可写入
        if (!FileExt.isExternalStorageWritable()) return null
        val path = "${FileConfig.PRIVATE_CACHE_DIR}/$folderPath/"
        val file = FileExt.getFile(path, fileName)
        file.outputStream().use { os ->
            bitmap.compress(compressFormat, quality, os)
        }
        return file
    }

    /**
     * 保存文件到公共存储目录,默认保存到了下载目录
     * */
    fun saveFileToDownload(
        file: File,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        context: Context = FileConfig.fileContext,
        mimeType: String = "*/*"
    ): Uri? {
        if (!FileExt.isExternalStorageWritable()) return null
        val path = FileExt.getAppDirectoryPath(Environment.DIRECTORY_DOWNLOADS, folderPath)
        val uri = FileExt.createStorageUri(context, fileName, path, mimeType)
        saveFile(context.contentResolver, uri, file.inputStream())
        return uri
    }

    /** 保存文件到app内部存储-默认路径 */
    fun saveFileToDir(
        inputPath: String,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
    ): File? =
        saveFile(inputPath, "${FileConfig.PRIVATE_FILE_DIR}/$folderPath/", fileName)

    /** 保存文件到app内部存储-缓存 */
    fun saveFileToCache(
        inputPath: String,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
    ): File? =
        saveFile(inputPath, "${FileConfig.PRIVATE_CACHE_DIR}/$folderPath/", fileName)

    /** 保存uri到app内部存储 */
    fun saveUriToDir(
        uri: Uri,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        context: Context = FileConfig.fileContext
    ): File {
        val outFile = FileExt.getFile("${FileConfig.PRIVATE_FILE_DIR}/$folderPath/", fileName)
        saveFile(context.contentResolver, uri, outFile.inputStream())
        return outFile
    }

    /** 保存uri到app内部存储 */
    fun saveUriToCache(
        uri: Uri,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        context: Context = FileConfig.fileContext
    ): File {
        val outFile = FileExt.getFile("${FileConfig.PRIVATE_CACHE_DIR}/$folderPath/", fileName)
        saveFile(context.contentResolver, uri, outFile.inputStream())
        return outFile
    }

    fun saveUriTo(
        uri: Uri,
        fileName: String,
        path: String,
        context: Context = FileConfig.fileContext
    ): File? {
        if (!FileExt.isExternalStorageWritable()) return null
        val outFile = FileExt.getFile(path, fileName)
        saveFile(context.contentResolver, uri, outFile.inputStream())
        return outFile
    }

    fun saveFileToStorageX(
        file: File,
        fileName: String,
        folderPath: String = FileConfig.APP_FOLDER_PATH,
        context: Context = FileConfig.fileContext,
        mimeType: String = "*/*"
    ): Uri? {
        if (!FileExt.isExternalStorageWritable()) return null
        val path = FileExt.getAppDirectoryPath(Environment.DIRECTORY_MOVIES, folderPath)
        val uri = FileExt.createStorageUri(context, fileName, path, mimeType)
        saveFile(context.contentResolver, uri, file.inputStream())
        return uri
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

    private fun saveFile(inputPath: String, outPath: String, fileName: String): File? {
        val file = File(inputPath)
        if (!file.exists()) return null
        val outFile = FileExt.getFile(outPath, fileName)
        val os = outFile.outputStream()
        file.inputStream().use {
            fileAToB(it, os)
        }
        return outFile
    }

    private fun saveFile(resolver: ContentResolver, uri: Uri?, inputStream: FileInputStream) =
        uri?.also { it ->
            resolver.openOutputStream(it)?.use { os ->
                fileAToB(inputStream, os)
            }
        }

    /** fileA读取到file B */
    fun fileAToB(input: FileInputStream, os: OutputStream) {
        val buffer = ByteArray(1024)
        var bytes = input.read(buffer)
        while (bytes >= 0) {
            os.write(buffer, 0, bytes)
            os.flush()
            bytes = input.read(buffer)
        }
    }

    fun delete(fileUri: Uri, context: Context = FileConfig.fileContext) {
        context.contentResolver.delete(fileUri, null, null)
    }
}
