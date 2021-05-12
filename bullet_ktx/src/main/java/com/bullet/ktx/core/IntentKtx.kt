package com.bullet.ktx.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri

/**
 * @Author petterp
 * @Date 2020/4/29-3:13 PM
 * @Email ShiyihuiCloud@163.com
 * @Function IntentKtx-Activity-core
 */

/**
 * Activity-Uri跳转简化
 * [isFish] 是否销毁activity,[obj]一个回调，万一你想添加一些有趣的东西呢？
 * */
inline fun Context.startActivityUriKtx(
    uri: Uri,
    isFish: Boolean = false,
    obj: (Intent) -> Unit = {}
) {
    val intent = Intent(ACTION_VIEW)
    intent.data = uri
    obj(intent)
    this.startActivity(intent)
    if (isFish && this is Activity) {
        this.finish()
    }
}

/**
 * Activity-Uri跳转简化
 * [isFish] 是否销毁activity,[obj]一个回调，万一你想添加一些有趣的东西呢？
 * */
inline fun Context.startActivityKtx(
    cls: Class<*>,
    isFish: Boolean = false,
    obj: (Intent) -> Unit = {}
) {
    val intent = Intent(this, cls)
    // 一个有趣的bug.[Android n-Android O]无需添加
    if (this is Application) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    obj(intent)
    this.startActivity(intent)
    if (isFish && this is Activity) {
        this.finish()
    }
}

/**
 * ActivityForResult简化
 * 貌似Activity1.x 已经有了更好的回调，但为什么依然使用旧的呢？
 * [requestCode]你的code,[cls]class类，[obj]一个回调，万一你想添加一些有趣的东西呢？
 * */
inline fun Activity.startActivityForResultKtx(
    requestCode: Int,
    cls: Class<*>,
    obj: (Intent) -> Unit = {}
) {
    val intent = Intent(this, cls)
    obj(intent)
    this.startActivityForResult(intent, requestCode)
}

/** 跳转到指定Activity并清空当前Activity栈，业务常用于账号被人顶下线，退出登录等 */
inline fun Context.startClearAllActivity(cls: Class<*>, obj: (Intent) -> Unit = {}) {
    if (javaClass == cls.javaClass) {
        return
    }
    val intent = Intent(this, cls)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    obj(intent)
    if (this is Application) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}
