package com.example.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.www.net.LvCreator
import com.www.net.LvHttp

/**
 * Created by Petterp
 * on 2019-10-26
 * Function:
 */
class ContextTools : Application() {

    override fun onCreate() {
        super.onCreate()
        //音频SDK初始化
        context = this

        LvCreator.init("http://192.168.43.80:80/")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

}
