package com.petterp.cloud.bullet.base.download

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

/**
 * @name Android Business Toos
 * @class name：com.business.tools.utils.download
 * @author 345 QQ:1831712732
 * @time 2020/3/16 20:19
 * @description 下载管理
 */

object DownLoadManager {

    private var context: Context? = null

    fun initContext(context: Context) {
        this.context = context
    }

    // 创建文件
    private val downloadDirectory by lazy {
        checkNotNull(context) {
            "DownloadManager context notNull!!"
        }
        File(context!!.filesDir, "download").also { it.mkdirs() }
    }

    sealed class DownloadStatus {
        object None : DownloadStatus()
        class Progress(val value: Int) : DownloadStatus()
        class Error(val throwable: Throwable) : DownloadStatus()
        class Donel(val file: File) : DownloadStatus()
    }

    fun download(url: String, fileName: String): Flow<DownloadStatus> {
        val file = File(downloadDirectory, fileName)
        return flow {
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient.Builder().build()
                .newCall(request).execute()
            if (response.isSuccessful) {
                response.body!!.let { body ->
                    // 总大小
                    val total = body.contentLength()
                    // 当前值
                    var emittedProcess = 0L
                    file.outputStream().use { output ->
                        body.byteStream().use { input ->
                            input.copyTo(output) { bytesCopied ->
                                // 计算百分比
                                val progress = bytesCopied * 100 / total
                                // 当前的值大于上一次的就进行通知
                                if (progress - emittedProcess > 1) {
                                    // 发射，外部的 collect 会执行
                                    emit(DownloadStatus.Progress(progress.toInt()))
                                    emittedProcess = progress
                                }
                            }
                        }
                    }
                    // 下载完成
                    emit(DownloadStatus.Donel(file))
                }
            } else {
                throw Exception(response.message)
            }
        }.catch {

            file.delete()
            emit(DownloadStatus.Error(it))
            // 保留最新的值
        }.conflate()
    }
}
