package com.business.audio.mediaplayer.core;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/16 23:26
 * @description MediaPlay Status
 */
public enum Status {
    /**
     * 初始状态
     */
    INITIAL,
    /**
     * 初始化状态
     */
    INITIALIZED,
    /**
     * 开始
     */
    STARTED,
    /**
     * 暂停状态
     */
    PAUSED,
    /**
     * 关闭状态
     */
    STOPPLED,
    /**
     * 完成状态
     */
    COMPLETED
}
