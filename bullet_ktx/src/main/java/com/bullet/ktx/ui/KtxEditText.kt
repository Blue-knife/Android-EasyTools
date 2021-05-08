package com.bullet.ktx.ui

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * @Author petterp
 * @Date 2020/6/6-7:28 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */

/** 隐藏键盘 */
fun Activity.hideSoftInput() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        window.decorView.windowToken, 0
    )
}

/**
 * EditText获取焦点并显示软键盘
 * 注意：此方法必须在 onResume中
 */
fun EditText.showSoftInputFromWindow() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    val imm = context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(
        InputMethodManager.SHOW_IMPLICIT,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

/**
 * 显示键盘
 *
 * @param et 输入焦点
 */
fun Activity.showInput(et: EditText) {
    et.requestFocus()
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT)
}

/** EditText失去焦点
 * 注意：此方法必须在控件初始化后中 */
fun ViewGroup.clearFocus() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
}
