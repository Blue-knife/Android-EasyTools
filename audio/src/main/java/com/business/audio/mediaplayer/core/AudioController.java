package com.business.audio.mediaplayer.core;

import com.business.audio.mediaplayer.event.AudioPlayModeEvent;
import com.business.audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 22:59
 * @description 控制播放逻辑
 */
public class AudioController {

    private static class SingletonHolder {
        public static final AudioController M_INSTANCE = new AudioController();
    }

    public static AudioController getInstance() {
        return SingletonHolder.M_INSTANCE;
    }

    /**
     * 核心播放器
     */
    private AudioPlayer mAudioPlayer;
    /**
     * 歌曲队列
     */
    private ArrayList<AudioBean> mQueue;
    /**
     * 当前播放索引
     */
    private int mQueueIndex;
    /**
     * 循环模式
     */
    private PlayMode mPlayMode;

    private AudioController() {
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    /**
     *
     * @return 获取播放队列
     */
    public ArrayList<AudioBean> getQueue() {
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    /**
     * @param beans 设置播放队列
     */
    public void setQueue(ArrayList<AudioBean> beans) {
        setQueue(beans, 0);
    }

    /**
     * 设置播放队列，指定播放索引
     *
     * @param beans      数据
     * @param queueIndex 索引
     */
    public void setQueue(ArrayList<AudioBean> beans, int queueIndex) {
        mQueue = beans;
        mQueueIndex = queueIndex;
    }

    /**
     * @param bean 添加歌曲
     */
    public void setAudio(AudioBean bean) {
        setAudio(0, bean);
    }

    /**
     * 添加歌曲到指定位置
     *
     * @param index 位置
     * @param bean 歌曲
     */
    public void setAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new NullPointerException("当前播放队列为 NULL");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没有添加
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean audioBean = getNowPlaying();
            if (!audioBean.id.equals(bean.id)) {
                //已经添加过且没有播放
                setPlayIndex(query);
            }
        }
    }


    /**
     *
     * @return 获取当前播放模式
     */
    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    /**
     *
     * @param mPlayMode  设置播放模式
     */
    public void setPlayMode(PlayMode mPlayMode) {
        this.mPlayMode = mPlayMode;
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }


    /**
     *
     * @return 获取当前播放索引
     */
    public int getPlayIndex() {
        return mQueueIndex;
    }

    /**
     *
     * @param mQueueIndex 设置索引并播放
     */
    public void setPlayIndex(int mQueueIndex) {
        if (mQueue == null) {
            throw new NullPointerException("当前播放队列为空，请设置播放队列");
        }
        this.mQueueIndex = mQueueIndex;
        play();
    }


    /**
     * @return 是否为播放状态
     */
    public boolean isStartState() {
        return Status.STARTED == getStatus();
    }

    /**
     *
     * @return 是否为暂停状态
     */
    public boolean isPauseState() {
        return Status.PAUSED == getStatus();
    }

    /**
     * 准备播放
     */
    public void prepare() {
        mAudioPlayer.prepare(getNowPlaying());
    }

    /**
     * 开始播放
     */
    public void play() {
        mAudioPlayer.load(getNowPlaying());
    }

    /**
     * 暂停播放
     */
    public void pause() {
        mAudioPlayer.pause();
    }

    /**
     * 恢复播放
     */
    public void resume() {
        mAudioPlayer.resume();
    }

    /**
     * 清空资源
     */
    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 播放下一首
     */
    public void next() {
        mAudioPlayer.load(getNextPlaying());
    }

    /**
     * 播放上一首
     */
    public void previous() {
        mAudioPlayer.load(getPreviousPlaying());
    }


    /**
     * 播放/暂停
     */
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        } else {
            play();
        }
    }

    /**
     *
     * @return 获取当前的 音樂
     */
    public AudioBean getNowPlaying() {
        return mQueue.get(mQueueIndex);
    }

    /**
     *
     * @return 获取下一首
     */
    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                if (mQueueIndex < mQueue.size() - 1) {
                    mQueueIndex++;
                } else {
                    mQueueIndex = 0;
                }
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size());
                break;
            case REPEAT:
            default:
                break;
        }
        return getPlaying();
    }

    /**
     *
     * @return 获取上一首
     */
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                if (mQueueIndex > 0) {
                    mQueueIndex--;
                } else {
                    mQueueIndex = mQueue.size() - 1;
                }
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size());
                break;
            case REPEAT:
            default:
                break;
        }
        return getPlaying();
    }

    /**
     *
     *
     * @return 获取当前播放状态
     */
    private Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    private AudioBean getPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue.size()) {
            return mQueue.get(mQueueIndex);
        } else {
            throw new NullPointerException("队列为 null 或者 索引越界");
        }
    }


    private void addCustomAudio(int index, AudioBean bean) {
        mQueue.set(index, bean);
    }

    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }
}
