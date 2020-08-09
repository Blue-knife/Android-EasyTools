package com.bullet.camera.camera

import android.os.Build

object VersionUtils{

    /**
     * 判断是否是Android L版本
     *
     * @return
     */
    @JvmStatic
    fun isAndroidL(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    /**
     * 判断是否是Android N版本
     *
     * @return
     */
    @JvmStatic
    fun isAndroidN(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    /**
     * 判断是否是Android P版本
     *
     * @return
     */
    @JvmStatic
    fun isAndroidP(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    /**
     * 判断是否是Android Q版本
     *
     * @return
     */
    @JvmStatic
    fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }
}