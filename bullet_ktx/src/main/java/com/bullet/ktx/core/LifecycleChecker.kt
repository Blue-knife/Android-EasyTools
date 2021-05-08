package com.cloudx.ktx.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner

/**
 * @Author petterp
 * @Date 2020/7/14-3:55 PM
 * @Email ShiyihuiCloud@163.com
 * @Function LifeCycle工具
 */
class LifecycleChecker : LifecycleObserver {

    companion object {
        var isFrontDesk = true
        fun init() {
            ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleChecker())
        }
    }

    /** 应用进入后台 */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackGround() {
        isFrontDesk = false
    }

    /** 应用进入前台 */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeGround() {
        isFrontDesk = true
    }
}
