package com.business.audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import com.business.audio.app.AudioHelper;
import com.business.audio.mediaplayer.event.AudioCompleteEvent;
import com.business.audio.mediaplayer.event.AudioErrorEvent;
import com.business.audio.mediaplayer.event.AudioLoadEvent;
import com.business.audio.mediaplayer.event.AudioPauseEvent;
import com.business.audio.mediaplayer.event.AudioPrepareEvent;
import com.business.audio.mediaplayer.event.AudioReleaseEvent;
import com.business.audio.mediaplayer.event.AudioStartEvent;
import com.business.audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 21:11
 * @description 播放音频，对外发送各种类型的事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    /**
     * 是否因为短暂失去焦点而停止播放
     */
    private boolean isPauseByFocusLossTransient;
    /**
     * 负责音频的播放
     */
    private CustomMediaPlayer mMediaPlayer;
    /**
     * 后台保活
     */
    private WifiManager.WifiLock mWifiLock;

    /**
     * 音频焦点监听器
     */
    private AudioFocusManager mAudioFocusManager;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    break;
                default:
                    break;
            }
        }
    };


    public AudioPlayer() {
        init();
    }

    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //设置此MediaPlayer的音频流类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        //初始化 WifiLocal
        mWifiLock = ((WifiManager) AudioHelper.getContext()
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }


    public void prepare(AudioBean audioBean) {
        EventBus.getDefault().post(new AudioPrepareEvent(audioBean));
    }

    /**
     * 对外提供的加载方法
     *
     */
    public void load(AudioBean audioBean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //对外发送 load 事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (Exception e) {
            //对外发送 Error 事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 对外提供暂停
     */
    public void pause() {
        if (getStatus() == Status.STARTED) {
            mMediaPlayer.pause();
            //释放WifiLock
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            if (mAudioFocusManager != null) {
                //释放音频焦点
                mAudioFocusManager.abandonAudioFocus();
            }
            //发送暂停事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }


    /**
     * 对外提供恢复
     */
    public void resume() {
        if (getStatus() == Status.PAUSED) {
            //继续播放
            start();
        }
    }

    /**
     * 清空播放器占用的资源
     */
    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mAudioFocusManager = null;
        mWifiLock = null;
        //发送 release 销毁事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**
     *
     * @return 获取播放器当前状态
     */
    public Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getStatus();
        }
        return Status.STOPPLED;
    }


    /**
     * 设置音量
     *
     * @param left
     * @param right
     */
    private void setVolume(float left, float right) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(left, right);
        }
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度回调
    }

    /**
     * 播放完成后的回调
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    /**
     * 开始播放
     */
    private void start() {
        if (!mAudioFocusManager.requestAudioFocus()) {
            Toast.makeText(AudioHelper.getContext(), "获取焦点失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaPlayer.start();
        mWifiLock.acquire();
        //对外发送 start 事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    /**
     * Error 相关回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放出错回调
        EventBus.getDefault().post(new AudioErrorEvent());
        return true;
    }

    /**
     * 准备好播放时调用
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        start();
    }


    @Override
    public void audioFocusGrant() {
        //再次获得音频焦点
        setVolume(1.0f, 1.0f);
        if (isPauseByFocusLossTransient) {
            resume();
        }
        isPauseByFocusLossTransient = false;
    }


    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂性失去焦点
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点，如短信通知
        setVolume(0.5f, 0.5f);
    }
}
