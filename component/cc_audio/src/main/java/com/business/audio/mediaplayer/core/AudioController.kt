package com.business.audio.mediaplayer.core

import com.business.audio.mediaplayer.event.AudioPlayModeEvent
import com.business.audio.mediaplayer.model.AudioBean
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 22:59
 * @description 控制播放逻辑
 */
class AudioController private constructor() {

    /**
     * 核心播放器
     */
    private val mAudioPlayer: AudioPlayer
    /**
     * 歌曲队列
     */
    private var mQueue: ArrayList<AudioBean>? = null
    /**
     * 当前播放索引
     */
    private var mQueueIndex: Int = 0
    /**
     * 循环模式
     */
    private var mPlayMode: PlayMode? = null

    /**
     *
     * @return 获取播放队列
     */
    /**
     * @param beans 设置播放队列
     */
    var queue: ArrayList<AudioBean>
        get() = if (mQueue == null) ArrayList() else mQueue!!
        set(beans) = setQueue(beans, 0)

    /**
     *
     * @return 获取当前播放模式
     */
    /**
     *
     * @param mPlayMode  设置播放模式
     */
    var playMode: PlayMode?
        get() = mPlayMode
        set(mPlayMode) {
            this.mPlayMode = mPlayMode
            EventBus.getDefault().post(AudioPlayModeEvent(mPlayMode!!))
        }

    /**
     *
     * @return 获取当前播放索引
     */
    /**
     *
     * @param mQueueIndex 设置索引并播放
     */
    var playIndex: Int
        get() = mQueueIndex
        set(mQueueIndex) {
            if (mQueue == null) {
                throw NullPointerException("当前播放队列为空，请设置播放队列")
            }
            this.mQueueIndex = mQueueIndex
            play()
        }

    /**
     * @return 是否为播放状态
     */
    val isStartState: Boolean
        get() = Status.STARTED == status

    /**
     *
     * @return 是否为暂停状态
     */
    val isPauseState: Boolean
        get() = Status.PAUSED == status

    /**
     *
     * @return 获取当前的 音樂
     */
    val nowPlaying: AudioBean
        get() = mQueue!![mQueueIndex]

    /**
     *
     * @return 获取下一首
     */
    private val nextPlaying: AudioBean
        get() {
            when (mPlayMode) {
                PlayMode.LOOP -> if (mQueueIndex < mQueue!!.size - 1) {
                    mQueueIndex++
                } else {
                    mQueueIndex = 0
                }
                PlayMode.RANDOM -> mQueueIndex = Random().nextInt(mQueue!!.size)
                PlayMode.REPEAT -> {
                }
                else -> {
                }
            }
            return playing
        }

    /**
     *
     * @return 获取上一首
     */
    private val previousPlaying: AudioBean
        get() {
            when (mPlayMode) {
                PlayMode.LOOP -> if (mQueueIndex > 0) {
                    mQueueIndex--
                } else {
                    mQueueIndex = mQueue!!.size - 1
                }
                PlayMode.RANDOM -> mQueueIndex = Random().nextInt(mQueue!!.size)
                PlayMode.REPEAT -> {
                }
                else -> {
                }
            }
            return playing
        }

    /**
     *
     *
     * @return 获取当前播放状态
     */
    private val status: Status?
        get() = mAudioPlayer.status

    private val playing: AudioBean
        get() = if (mQueue != null && !mQueue!!.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue!!.size) {
            mQueue!![mQueueIndex]
        } else {
            throw NullPointerException("队列为 null 或者 索引越界")
        }

    private object SingletonHolder {
        val M_INSTANCE = AudioController()
    }

    init {
        mAudioPlayer = AudioPlayer()
        mQueue = ArrayList()
        mQueueIndex = 0
        mPlayMode = PlayMode.LOOP
    }

    /**
     * 设置播放队列，指定播放索引
     *
     * @param beans      数据
     * @param queueIndex 索引
     */
    fun setQueue(beans: ArrayList<AudioBean>, queueIndex: Int) {
        mQueue = beans
        mQueueIndex = queueIndex
    }

    /**
     * @param bean 添加歌曲
     */
    fun setAudio(bean: AudioBean) {
        setAudio(0, bean)
    }

    /**
     * 添加歌曲到指定位置
     *
     * @param index 位置
     * @param bean 歌曲
     */
    fun setAudio(index: Int, bean: AudioBean) {
        if (mQueue == null) {
            throw NullPointerException("当前播放队列为 NULL")
        }
        val query = queryAudio(bean)
        if (query <= -1) {
            // 没有添加
            addCustomAudio(index, bean)
            playIndex = index
        } else {
            val audioBean = nowPlaying
            if (audioBean.id != bean.id) {
                // 已经添加过且没有播放
                playIndex = query
            }
        }
    }

    /**
     * 准备播放
     */
    fun prepare() {
        mAudioPlayer.prepare(nowPlaying)
    }

    /**
     * 开始播放
     */
    fun play() {
        mAudioPlayer.load(nowPlaying)
    }

    /**
     * 暂停播放
     */
    fun pause() {
        mAudioPlayer.pause()
    }

    /**
     * 恢复播放
     */
    fun resume() {
        mAudioPlayer.resume()
    }

    /**
     * 清空资源
     */
    fun release() {
        mAudioPlayer.release()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 播放下一首
     */
    operator fun next() {
        mAudioPlayer.load(nextPlaying)
    }

    /**
     * 播放上一首
     */
    fun previous() {
        mAudioPlayer.load(previousPlaying)
    }

    /**
     * 播放/暂停
     */
    fun playOrPause() {
        if (isStartState) {
            pause()
        } else if (isPauseState) {
            resume()
        } else {
            play()
        }
    }

    private fun addCustomAudio(index: Int, bean: AudioBean) {
        mQueue!![index] = bean
    }

    private fun queryAudio(bean: AudioBean): Int {
        return mQueue!!.indexOf(bean)
    }

    companion object {

        val instance: AudioController
            get() = SingletonHolder.M_INSTANCE
    }
}
