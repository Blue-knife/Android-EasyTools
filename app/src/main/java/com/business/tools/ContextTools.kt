package com.business.tools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.business.audio.app.AudioHelper

/**
 * Created by Petterp
 * on 2019-10-26
 * Function:
 */
class ContextTools : Application() {

    override fun onCreate() {
        super.onCreate()
        //音频SDK初始化
        AudioHelper.init(this);
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

}
