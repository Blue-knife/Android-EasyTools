package com.business.audio.mediaplayer.core

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/17 21:20
 * @description 音频焦点改变，接口回调
 */
interface AudioFocusListener {

    /**
     * 获得焦点回调处理
     */
    fun audioFocusGrant()

    /**
     * 永久失去焦点回调处理，如被其他播放器抢占
     */
    fun audioFocusLoss()

    /**
     * 短暂失去焦点回调处理，如来电
     */
    fun audioFocusLossTransient()

    /**
     * 瞬间失去焦点回调，如通知
     */
    fun audioFocusLossDuck()
}
