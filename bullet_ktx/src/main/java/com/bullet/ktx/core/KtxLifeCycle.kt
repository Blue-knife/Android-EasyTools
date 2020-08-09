package com.cloudx.ktx.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @Author petterp
 * @Date 2020/6/1-4:15 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
inline fun Fragment.launchIO(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) {
        obj()
    }
}

inline fun Fragment.launchDefault(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.Default) {
        obj()
    }
}

inline fun Fragment.launchMain(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.Main) {
        obj()
    }
}

inline fun FragmentActivity.launchIO(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) {
        obj()
    }
}

inline fun FragmentActivity.launchDefault(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.Default) {
        obj()
    }
}

inline fun FragmentActivity.launchMain(crossinline obj: suspend () -> Unit) {
    lifecycleScope.launch(Dispatchers.Main) {
        obj()
    }
}

suspend inline fun withContextMain(crossinline obj: suspend () -> Unit) {
    withContext(Dispatchers.Main) {
        obj()
    }
}