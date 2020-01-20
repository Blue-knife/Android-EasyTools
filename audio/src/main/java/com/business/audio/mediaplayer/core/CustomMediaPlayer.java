package com.business.audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/16 23:12
 * @description 带状态的 MediaPlay
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    private Status mStatus;
    private OnCompletionListener mCompletionListener;


    public CustomMediaPlayer() {
        mStatus = Status.INITIAL;
        super.setOnCompletionListener(this);
    }

    @Override
    public void reset() {
        super.reset();
        mStatus = Status.INITIAL;
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mStatus = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mStatus = Status.STARTED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mStatus = Status.PAUSED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mStatus = Status.STOPPLED;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mStatus = Status.COMPLETED;
        mCompletionListener.onCompletion(mp);
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public Status getStatus() {
        return mStatus;
    }

    /**
     * 是否播放完成
     *
     * @return
     */
    public boolean isComplete() {
        return mStatus == Status.COMPLETED;
    }

    /**
     * 播放完成后调用
     *
     * @param listener
     */
    public void setCompleteListener(OnCompletionListener listener) {
        this.mCompletionListener = listener;
    }
}
