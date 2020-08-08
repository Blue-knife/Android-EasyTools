package com.business.audio.mediaplayer.model

import java.io.Serializable

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.model
 * @time 2019/12/17 22:50
 * @description
 */
class AudioBean(var id: String,
                /**
                 * 地址
                 */
                var mUrl: String,
                /**
                 * 歌名
                 */
                var name: String,
                /**
                 * 作者
                 */
                var author: String,
                /**
                 * 所属专辑
                 */
                var album: String,
                /**
                 * 专辑信息
                 */
                var albumInfo: String,
                /**
                 * 专辑封面
                 */
                var albumPic: String,
                /**
                 * 时长
                 */
                var totalTime: String) : Serializable
