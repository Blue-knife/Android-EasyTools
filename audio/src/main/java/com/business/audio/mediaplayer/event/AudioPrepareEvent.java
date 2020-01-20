package com.business.audio.mediaplayer.event;

import com.business.audio.mediaplayer.model.AudioBean;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.event
 * @time 2019/12/26 22:10
 * @description 准备播放事件
 */
public class AudioPrepareEvent {
    public AudioBean audioBean;

    public AudioPrepareEvent(AudioBean audioBean) {
        this.audioBean = audioBean;
    }
}
