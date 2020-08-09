package com.petterp.cloud.bullet.base.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn

/**
 * @name Android Business Toos
 * @class name：com.business.tools.utils.download
 * @author 345 QQ:1831712732
 * @time 2020/3/16 20:30
 * @description
 */
class DownloadModel : ViewModel() {

    val downloadStateLiveData =
            MutableLiveData<DownLoadManager.DownloadStatus>(DownLoadManager.DownloadStatus.None)


    suspend fun download(url: String, fileName: String) {
        DownLoadManager.download(url, fileName)
                .flowOn(Dispatchers.IO)
                .collect {
                    //发送数据
                    downloadStateLiveData.value = it
                }
    }
}