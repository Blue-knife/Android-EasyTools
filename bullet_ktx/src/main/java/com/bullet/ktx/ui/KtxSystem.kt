package com.bullet.ktx.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri


/**
 * @Author petterp
 * @Date 2020/6/6-8:20 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
/**
 * 拨打电话（直接拨打电话）
 * @param phoneNum 电话号码
 */
@SuppressLint("MissingPermission")
fun Context.callPhone(phoneNum: String) {
    val intent = Intent(Intent.ACTION_CALL)
    val data = Uri.parse("tel:$phoneNum")
    intent.data = data
    startActivity(intent)
}