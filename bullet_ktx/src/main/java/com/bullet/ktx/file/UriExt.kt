package com.bullet.ktx.file

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import java.io.File
import java.lang.reflect.Array
import java.lang.reflect.Array.getLength
import java.lang.reflect.Method

/**
 * @Author petterp
 * @Date 2021/5/10-6:48 下午
 * @Email ShiyihuiCloud@163.com
 * @Function UriExt
 */
class UriExt {
    companion object {

        /**
         * file转Uri
         * 需要设置了FileProvider
         * */
        fun fileFromUri(file: File, context: Context = FileConfig.fileContext): Uri? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val authority =
                    "${context.packageName}.fileprovider"
                FileProvider.getUriForFile(context, authority, file)
            } else {
                Uri.fromFile(file)
            }
        }

        /** uri转File */
        @SuppressLint("NewApi")
        fun uriFromFile(uri: Uri, context: Context = FileConfig.fileContext): File? {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (uri.scheme == ContentResolver.SCHEME_FILE)
                uri.toFile()
            else if (uri.scheme == ContentResolver.SCHEME_CONTENT && sdkVersion >= Build.VERSION_CODES.Q) {
                // 如果是Q且使用了旧版本存储
                if (sdkVersion == Build.VERSION_CODES.Q && Environment.isExternalStorageLegacy()) {
                    uri2FileReal(uri, context)
                } else {
                    // 去应用沙盒里创建一个临时file
                    FileExt.saveUriToCacheDir(uri, context)
                }
            } else {
                uri2FileReal(uri, context)
            }
        }

        /** Uri转File */
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun uri2FileReal(uri: Uri, context: Context = FileConfig.fileContext): File? {
            Log.d("UriUtils", uri.toString())
            val authority: String? = uri.authority
            val scheme: String? = uri.scheme
            val path: String? = uri.path
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && path != null) {
                val externals = arrayOf("/external/", "/external_path/", "/rc_external_path/")
                var file: File? = null
                for (external: String in externals) {
                    if (path.startsWith(external)) {
                        file = File(
                            Environment.getExternalStorageDirectory().absolutePath +
                                path.replace(external, "/")
                        )
                        if (file.exists()) {
                            Log.d("UriUtils", uri.toString().toString() + " -> " + external)
                            return file
                        }
                    }
                }
                file = null
                when {
                    path.startsWith("/files_path/") -> {
                        file = File(
                            context.filesDir.absolutePath
                                .toString() + path.replace("/files_path/", "/")
                        )
                    }
                    path.startsWith("/cache_path/") -> {
                        file = File(
                            context.cacheDir.absolutePath
                                .toString() + path.replace("/cache_path/", "/")
                        )
                    }
                    path.startsWith("/external_files_path/") -> {
                        file = File(
                            context.getExternalFilesDir(null)?.absolutePath
                                .toString() + path.replace("/external_files_path/", "/")
                        )
                    }
                    path.startsWith("/external_cache_path/") -> {
                        file = File(
                            context.externalCacheDir?.absolutePath
                                .toString() + path.replace("/external_cache_path/", "/")
                        )
                    }
                }
                if (file != null && file.exists()) {
                    Log.d("UriUtils", uri.toString().toString() + " -> " + path)
                    return file
                }
            }
            if ((ContentResolver.SCHEME_FILE == scheme)) {
                if (path != null) return File(path)
                Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 0")
                return null
            } // end 0
            else if ((
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    DocumentsContract.isDocumentUri(context, uri)
                )
            ) {
                if (("com.android.externalstorage.documents" == authority)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return File(
                            Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                        )
                    } else {
                        // Below logic is how External Storage provider build URI for documents
                        // http://stackoverflow.com/questions/28605278/android-5-sd-card-label
                        val mStorageManager =
                            context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
                        try {
                            val storageVolumeClazz =
                                Class.forName("android.os.storage.StorageVolume")
                            val getVolumeList: Method =
                                mStorageManager.javaClass.getMethod("getVolumeList")
                            val getUuid: Method = storageVolumeClazz.getMethod("getUuid")
                            val getState: Method = storageVolumeClazz.getMethod("getState")
                            val getPath: Method = storageVolumeClazz.getMethod("getPath")
                            val isPrimary: Method = storageVolumeClazz.getMethod("isPrimary")
                            val isEmulated: Method = storageVolumeClazz.getMethod("isEmulated")
                            val result: Any = getVolumeList.invoke(mStorageManager)
                            val length: Int = getLength(result)
                            for (i in 0 until length) {
                                val storageVolumeElement: Any = Array.get(result, i)
                                // String uuid = (String)  getUuid.invoke(storageVolumeElement);
                                val mounted =
                                    (
                                        (
                                            Environment.MEDIA_MOUNTED == getState.invoke(
                                                storageVolumeElement
                                            )
                                            ) || (
                                            Environment.MEDIA_MOUNTED_READ_ONLY == getState.invoke(
                                                storageVolumeElement
                                            )
                                            )
                                        )

                                // if the media is not mounted, we need not get the volume details
                                if (!mounted) continue

                                // Primary storage is already handled.
                                if ((
                                    isPrimary.invoke(storageVolumeElement) as Boolean &&
                                        isEmulated.invoke(storageVolumeElement) as Boolean
                                    )
                                ) {
                                    continue
                                }
                                val uuid: String? = getUuid.invoke(storageVolumeElement) as String?
                                if (uuid != null && (uuid == type)) {
                                    return File(
                                        getPath.invoke(storageVolumeElement)
                                            .toString() + "/" + split[1]
                                    )
                                }
                            }
                        } catch (ex: Exception) {
                            Log.d(
                                "UriUtils",
                                uri.toString()
                                    .toString() + " parse failed. " + ex.toString() + " -> 1_0"
                            )
                        }
                    }
                    Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 1_0")
                    return null
                } // end 1_0
                else if (("com.android.providers.downloads.documents" == authority)) {
                    var id = DocumentsContract.getDocumentId(uri)
                    if (TextUtils.isEmpty(id)) {
                        Log.d(
                            "UriUtils",
                            uri.toString().toString() + " parse failed(id is null). -> 1_1"
                        )
                        return null
                    }
                    if (id.startsWith("raw:")) {
                        return File(id.substring(4))
                    } else if (id.startsWith("msf:")) {
                        id = id.split(":").toTypedArray()[1]
                    }
                    var availableId: Long = 0
                    try {
                        availableId = id.toLong()
                    } catch (e: Exception) {
                        return null
                    }
                    val contentUriPrefixesToTry = arrayOf(
                        "content://downloads/public_downloads",
                        "content://downloads/all_downloads",
                        "content://downloads/my_downloads"
                    )
                    for (contentUriPrefix: String in contentUriPrefixesToTry) {
                        val contentUri: Uri =
                            ContentUris.withAppendedId(Uri.parse(contentUriPrefix), availableId)
                        try {
                            val file = getFileFromUri(contentUri, "1_1", context)
                            if (file != null) {
                                return file
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 1_1")
                    return null
                } // end 1_1
                else if (("com.android.providers.media.documents" == authority)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val contentUri: Uri = when (split[0]) {
                        "image" -> {
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                        else -> {
                            Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 1_2")
                            return null
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    return getFileFromUri(contentUri, selection, selectionArgs, "1_2", context)
                } // end 1_2
                else if ((ContentResolver.SCHEME_CONTENT == scheme)) {
                    return getFileFromUri(uri, "1_3", context)
                } // end 1_3
                else {
                    Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 1_4")
                    return null
                } // end 1_4
            } // end 1
            else if ((ContentResolver.SCHEME_CONTENT == scheme)) {
                return getFileFromUri(uri, "2", context)
            } // end 2
            else {
                Log.d("UriUtils", uri.toString().toString() + " parse failed. -> 3")
                return null
            } // end 3
        }

        /** 从Uri转File */
        private fun getFileFromUri(uri: Uri, code: String, context: Context): File? {
            return getFileFromUri(uri, null, null, code, context)
        }

        private fun getFileFromUri(
            uri: Uri,
            selection: String?,
            selectionArgs: kotlin.Array<String>?,
            code: String,
            context: Context
        ): File? {
            if ("com.google.android.apps.photos.content" == uri.authority) {
                if (!TextUtils.isEmpty(uri.lastPathSegment)) {
                    return File(uri.lastPathSegment)
                }
            } else if ("com.tencent.mtt.fileprovider" == uri.authority) {
                val path = uri.path
                if (!TextUtils.isEmpty(path)) {
                    val fileDir = Environment.getExternalStorageDirectory()
                    return File(fileDir, path!!.substring("/QQBrowser".length, path.length))
                }
            } else if ("com.huawei.hidisk.fileprovider" == uri.authority) {
                val path = uri.path
                if (!TextUtils.isEmpty(path)) {
                    return File(path!!.replace("/root", ""))
                }
            }
            val cursor: Cursor? = context.contentResolver.query(
                uri, arrayOf("_data"), selection, selectionArgs, null
            )
            if (cursor == null) {
                Log.d("UriUtils", "$uri parse failed(cursor is null). -> $code")
                return null
            }
            return try {
                if (cursor.moveToFirst()) {
                    val columnIndex: Int = cursor.getColumnIndex("_data")
                    if (columnIndex > -1) {
                        File(cursor.getString(columnIndex))
                    } else {
                        Log.d(
                            "UriUtils",
                            "$uri parse failed(columnIndex: $columnIndex is wrong). -> $code"
                        )
                        null
                    }
                } else {
                    Log.d("UriUtils", "$uri parse failed(moveToFirst return false). -> $code")
                    null
                }
            } catch (e: java.lang.Exception) {
                Log.d("UriUtils", "$uri parse failed. -> $code")
                null
            } finally {
                cursor.close()
            }
        }
    }
}
