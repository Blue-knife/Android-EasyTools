package com.bullet.camera.camera

import android.net.Uri

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.tools.camera
 * @time 2019/12/11 23:25
 * @description 保存 url
 */
class CameraImageBean private constructor() {
    var path: Uri? = null

    companion object {
        val instance = CameraImageBean()
    }
}