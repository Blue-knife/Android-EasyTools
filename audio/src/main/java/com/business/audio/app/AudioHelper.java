package com.business.audio.app;

import android.annotation.SuppressLint;
import android.content.Context;

import com.business.audio.mediaplayer.core.MusicService;
import com.business.audio.mediaplayer.model.AudioBean;

import java.util.ArrayList;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.app
 * @time 2019/12/17 21:40
 * @description 唯一与外界通信的帮助类
 */
public class AudioHelper {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 开启 Service
     *
     * @param audios 音乐列表
     */
    public static void startMusicService(ArrayList<AudioBean> audios) {
        MusicService.startMusicService(audios);
    }
}
