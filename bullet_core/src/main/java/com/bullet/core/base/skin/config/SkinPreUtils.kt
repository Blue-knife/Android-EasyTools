package com.bullet.core.base.skin.config

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object SkinPreUtils {

    var context: Context? = null
    fun init(context: Context) {
        SkinPreUtils.context = context.applicationContext
    }


    /**
     * 报错当前皮肤路径
     */
    fun saveSkinPath(skinPath: String?) {
        context!!.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(SkinConfig.SKIN_PATH_NAME, skinPath)
                .apply()
    }

    /**
     * 返回当前皮肤路径
     */
    fun getSkinPath(): String? {
        return context!!.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE)
                .getString(SkinConfig.SKIN_PATH_NAME, null)
    }

    /**
     * 清空皮肤路径
     */
    fun clearSkinInfo() {
        saveSkinPath(null)
    }

    /**
     * 添加一个标记
     */
    fun setTag(boolean: Boolean) {
        context!!.getSharedPreferences(SkinConfig.TAG, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SkinConfig.TAG, boolean)
                .apply()
    }

    /**
     * 获取标记
     */
    fun getTag(): Boolean {
        return context!!.getSharedPreferences(SkinConfig.TAG, Context.MODE_PRIVATE)
                .getBoolean(SkinConfig.TAG, false)
    }
}