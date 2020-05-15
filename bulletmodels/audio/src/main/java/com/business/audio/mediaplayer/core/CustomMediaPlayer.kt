package com.business.audio.mediaplayer.core

import android.media.MediaPlayer

import java.io.IOException

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/16 23:12
 * @description 带状态的 MediaPlay
 */
class CustomMediaPlayer : MediaPlayer(), MediaPlayer.OnCompletionListener {

    /**
     * 获取当前状态
     *
     * @return
     */
    var status: Status? = null
        private set
    private var mCompletionListener: MediaPlayer.OnCompletionListener? = null

    /**
     * 是否播放完成
     *
     * @return
     */
    val isComplete: Boolean
        get() = status == Status.COMPLETED

    init {
        status = Status.INITIAL
        super.setOnCompletionListener(this)
    }

    override fun reset() {
        super.reset()
        status = Status.INITIAL
    }

    @Throws(IOException::class, IllegalArgumentException::class, IllegalStateException::class, SecurityException::class)
    override fun setDataSource(path: String) {
        super.setDataSource(path)
        status = Status.INITIALIZED
    }

    @Throws(IllegalStateException::class)
    override fun start() {
        super.start()
        status = Status.STARTED
    }

    @Throws(IllegalStateException::class)
    override fun pause() {
        super.pause()
        status = Status.PAUSED
    }

    @Throws(IllegalStateException::class)
    override fun stop() {
        super.stop()
        status = Status.STOPPLED
    }

    override fun onCompletion(mp: MediaPlayer) {
        status = Status.COMPLETED
        mCompletionListener!!.onCompletion(mp)
    }

    /**
     * 播放完成后调用
     *
     * @param listener
     */
    fun setCompleteListener(listener: MediaPlayer.OnCompletionListener) {
        this.mCompletionListener = listener
    }
}
