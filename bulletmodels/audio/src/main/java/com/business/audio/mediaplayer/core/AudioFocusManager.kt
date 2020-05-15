package com.business.audio.mediaplayer.core

import android.content.Context
import android.media.AudioManager

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 21:18
 * @description 音频焦点的监听器
 */
class AudioFocusManager(context: Context, private val mAudioFocusListener: AudioFocusListener?) : AudioManager.OnAudioFocusChangeListener {
    private val audioManager: AudioManager


    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 请求音频焦点
     *
     * @return
     */
    fun requestAudioFocus(): Boolean {
        //1，监听焦点变化 2，请求的音频焦点影响的那种类型流 3，焦点的持续时间
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    /**
     * 释放音频焦点
     */
    fun abandonAudioFocus() {
        audioManager.abandonAudioFocus(this)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (mAudioFocusListener != null) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> mAudioFocusListener.audioFocusGrant()
                AudioManager.AUDIOFOCUS_LOSS -> mAudioFocusListener.audioFocusLoss()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mAudioFocusListener.audioFocusLossTransient()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> mAudioFocusListener.audioFocusLossDuck()
                else -> {
                }
            }
        }
    }

    companion object {

        private val TAG = AudioFocusManager::class.java.simpleName
    }

}
