package com.business.tools.utils

import android.content.SharedPreferences
import com.business.tools.ContextTools

object ToolsPreference {

    private const val USER = "user"
    private const val DEFAULT = "default"


    private fun getAppPreferenceEdit(str: String = DEFAULT): SharedPreferences.Editor {
        return ContextTools.context.getSharedPreferences(str, 0).edit()
    }

    private fun getAppPreference(str: String = DEFAULT): SharedPreferences {
        return ContextTools.context.getSharedPreferences(str, 0)
    }

    /**
     * 保存登陆状态
     */
    fun putLogin(isLogin: Boolean) {
        getAppPreferenceEdit(USER)
                .putBoolean("isLogin", isLogin)
                .apply()
    }

    /**
     * 获取登陆状态
     */
    fun getLogin(): Boolean {
        return getAppPreference(USER).getBoolean("isLogin", false)
    }


    /**
     * 保存 Token
     */
    fun putTokenId(tokenId: String) {
        getAppPreferenceEdit(DEFAULT)
                .putString("tokenId", tokenId)
                .apply()
    }

    /**
     * 获取 ToKen
     */
    fun getTokenId(): String? {
        return getAppPreference(DEFAULT).getString("tokenId", null)
    }


}