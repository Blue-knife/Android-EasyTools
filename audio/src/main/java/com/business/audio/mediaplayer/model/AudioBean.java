package com.business.audio.mediaplayer.model;

import java.io.Serializable;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.model
 * @time 2019/12/17 22:50
 * @description
 */
public class AudioBean implements Serializable {

    public String id;
    /**
     * 地址
     */
    public String mUrl;
    /**
     * 歌名
     */
    public String name;
    /**
     * 作者
     */
    public String author;
    /**
     * 所属专辑
     */
    public String album;
    /**
     * 专辑信息
     */
    public String albumInfo;
    /**
     * 专辑封面
     */
    public String albumPic;
    /**
     * 时长
     */
    public String totalTime;

    public AudioBean(String id, String mUrl, String name, String author, String album, String albumInfo, String albumPic, String totalTime) {
        this.id = id;
        this.mUrl = mUrl;
        this.name = name;
        this.author = author;
        this.album = album;
        this.albumInfo = albumInfo;
        this.albumPic = albumPic;
        this.totalTime = totalTime;
    }
}
