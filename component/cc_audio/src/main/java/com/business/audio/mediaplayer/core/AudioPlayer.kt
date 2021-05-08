package com.business.audio.mediaplayer.core

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.widget.Toast
import com.business.audio.app.AudioHelper
import com.business.audio.mediaplayer.event.*
import com.business.audio.mediaplayer.model.AudioBean
import org.greenrobot.eventbus.EventBus

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 21:11
 * @description 播放音频，对外发送各种类型的事件
 */
class AudioPlayer : MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioFocusListener {

    /**
     * 是否因为短暂失去焦点而停止播放
     */
    private var isPauseByFocusLossTransient: Boolean = false
    /**
     * 负责音频的播放
     */
    private var mMediaPlayer: CustomMediaPlayer? = null
    /**
     * 后台保活
     */
    private var mWifiLock: WifiManager.WifiLock? = null

    /**
     * 音频焦点监听器
     */
    private var mAudioFocusManager: AudioFocusManager? = null

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TIME_MSG -> {
                }
                else -> {
                }
            }
        }
    }

    /**
     *
     * @return 获取播放器当前状态
     */
    val status: Status?
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.status
        } else Status.STOPPLED

    init {
        init()
    }

    private fun init() {
        mMediaPlayer = CustomMediaPlayer()
        mMediaPlayer!!.setWakeMode(AudioHelper.context, PowerManager.PARTIAL_WAKE_LOCK)
        // 设置此MediaPlayer的音频流类型
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.setOnCompletionListener(this)
        mMediaPlayer!!.setOnPreparedListener(this)
        mMediaPlayer!!.setOnBufferingUpdateListener(this)
        mMediaPlayer!!.setOnErrorListener(this)
        // 初始化 WifiLocal
        mWifiLock = (
            AudioHelper.context!!
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
            )
            .createWifiLock(WifiManager.WIFI_MODE_FULL, TAG)
        mAudioFocusManager = AudioFocusManager(AudioHelper.context!!, this)
    }

    fun prepare(audioBean: AudioBean) {
        EventBus.getDefault().post(AudioPrepareEvent(audioBean))
    }

    /**
     * 对外提供的加载方法
     *
     */
    fun load(audioBean: AudioBean) {
        try {
            mMediaPlayer!!.reset()
            mMediaPlayer!!.setDataSource(audioBean.mUrl)
            mMediaPlayer!!.prepareAsync()
            // 对外发送 load 事件
            EventBus.getDefault().post(AudioLoadEvent(audioBean))
        } catch (e: Exception) {
            // 对外发送 Error 事件
            EventBus.getDefault().post(AudioErrorEvent())
        }
    }

    /**
     * 对外提供暂停
     */
    fun pause() {
        if (status == Status.STARTED) {
            mMediaPlayer!!.pause()
            // 释放WifiLock
            if (mWifiLock!!.isHeld) {
                mWifiLock!!.release()
            }
            if (mAudioFocusManager != null) {
                // 释放音频焦点
                mAudioFocusManager!!.abandonAudioFocus()
            }
            // 发送暂停事件
            EventBus.getDefault().post(AudioPauseEvent())
        }
    }

    /**
     * 对外提供恢复
     */
    fun resume() {
        if (status == Status.PAUSED) {
            // 继续播放
            start()
        }
    }

    /**
     * 清空播放器占用的资源
     */
    fun release() {
        if (mMediaPlayer == null) {
            return
        }
        mMediaPlayer!!.release()
        mMediaPlayer = null
        if (mAudioFocusManager != null) {
            mAudioFocusManager!!.abandonAudioFocus()
        }
        if (mWifiLock!!.isHeld) {
            mWifiLock!!.release()
        }
        mAudioFocusManager = null
        mWifiLock = null
        // 发送 release 销毁事件
        EventBus.getDefault().post(AudioReleaseEvent())
    }

    /**
     * 设置音量
     *
     * @param left
     * @param right
     */
    private fun setVolume(left: Float, right: Float) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.setVolume(left, right)
        }
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        // 缓存进度回调
    }

    /**
     * 播放完成后的回调
     *
     * @param mp
     */
    override fun onCompletion(mp: MediaPlayer) {
        EventBus.getDefault().post(AudioCompleteEvent())
    }

    /**
     * 开始播放
     */
    private fun start() {
        if (!mAudioFocusManager!!.requestAudioFocus()) {
            Toast.makeText(AudioHelper.context, "获取焦点失败", Toast.LENGTH_SHORT).show()
            return
        }
        mMediaPlayer!!.start()
        mWifiLock!!.acquire()
        // 对外发送 start 事件
        EventBus.getDefault().post(AudioStartEvent())
    }

    /**
     * Error 相关回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        // 播放出错回调
        EventBus.getDefault().post(AudioErrorEvent())
        return true
    }

    /**
     * 准备好播放时调用
     *
     * @param mp
     */
    override fun onPrepared(mp: MediaPlayer) {
        start()
    }

    override fun audioFocusGrant() {
        // 再次获得音频焦点
        setVolume(1.0f, 1.0f)
        if (isPauseByFocusLossTransient) {
            resume()
        }
        isPauseByFocusLossTransient = false
    }

    override fun audioFocusLoss() {
        // 永久失去焦点
        pause()
    }

    override fun audioFocusLossTransient() {
        // 短暂性失去焦点
        pause()
        isPauseByFocusLossTransient = true
    }

    override fun audioFocusLossDuck() {
        // 瞬间失去焦点，如短信通知
        setVolume(0.5f, 0.5f)
    }

    companion object {

        private val TAG = "AudioPlayer"
        private val TIME_MSG = 0x01
        private val TIME_INVAL = 100
    }
}
