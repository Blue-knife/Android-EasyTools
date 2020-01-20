package com.business.audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 21:18
 * @description 音频焦点的监听器
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = AudioFocusManager.class.getSimpleName();
    private AudioFocusListener mAudioFocusListener;
    private AudioManager audioManager;


    public AudioFocusManager(Context context, AudioFocusListener listener) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = listener;
    }

    /**
     * 请求音频焦点
     *
     * @return
     */
    public boolean requestAudioFocus() {
        //1，监听焦点变化 2，请求的音频焦点影响的那种类型流 3，焦点的持续时间
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    /**
     * 释放音频焦点
     */
    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (mAudioFocusListener != null) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mAudioFocusListener.audioFocusGrant();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioFocusListener.audioFocusLoss();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mAudioFocusListener.audioFocusLossTransient();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mAudioFocusListener.audioFocusLossDuck();
                    break;
                default:
                    break;
            }
        }
    }

}
