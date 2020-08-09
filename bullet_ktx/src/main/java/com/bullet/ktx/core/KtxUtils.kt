package com.cloudx.ktx.core

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever


/**
 * Created by Petterp
 * on 2020-01-28
 * Function: Kt常用顶级函数-命名[todo]
 */



fun getNetVideoBitmap(videoUrl: String): Bitmap? {
    var bitmap: Bitmap? = null

    val retriever = MediaMetadataRetriever();
    try {
        //根据url获取缩略图
        retriever.setDataSource(videoUrl, HashMap())
        //获得第一帧图片
        bitmap = retriever.frameAtTime
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return bitmap
}