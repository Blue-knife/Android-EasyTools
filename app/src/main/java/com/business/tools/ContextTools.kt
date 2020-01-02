package com.business.tools

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * Created by Petterp
 * on 2019-10-26
 * Function:
 */
class ContextTools : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

}
