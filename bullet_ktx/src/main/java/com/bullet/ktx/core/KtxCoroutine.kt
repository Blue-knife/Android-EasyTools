package com.bullet.ktx.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author petterp
 * @Date 2020/6/3-10:21 AM
 * @Email ShiyihuiCloud@163.com
 * @Function 协程相关简化
 */

val ktxCoroutineScope =
    CoroutineScope(Dispatchers.Default)

inline fun launchKtxUI(crossinline obj: suspend CoroutineScope.() -> Unit) {
    ktxCoroutineScope.launch(Dispatchers.Main) {
        obj()
    }
}

inline fun launchKtxIO(crossinline obj: suspend CoroutineScope.() -> Unit) {
    ktxCoroutineScope.launch(Dispatchers.IO) {
        obj()
    }
}
