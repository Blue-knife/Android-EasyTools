package com.business.audio.app

import android.annotation.SuppressLint
import android.content.Context
import com.business.audio.mediaplayer.core.MusicService
import com.business.audio.mediaplayer.model.AudioBean
import java.util.ArrayList

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.app
 * @time 2019/12/17 21:40
 * @description 唯一与外界通信的帮助类
 */
object AudioHelper {

    @SuppressLint("StaticFieldLeak")
    var context: Context? = null
        private set

    fun init(context: Context) {
        this.context = context
    }

    /**
     * 开启 Service
     *
     * @param audios 音乐列表
     */
    fun startMusicService(audios: ArrayList<AudioBean>) {
        MusicService.startMusicService(audios)
    }
}
