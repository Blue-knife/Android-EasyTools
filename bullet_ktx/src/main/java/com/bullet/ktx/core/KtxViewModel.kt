package com.cloudx.ktx.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * @Author petterp
 * @Date 2020/5/31-12:09 AM
 * @Email ShiyihuiCloud@163.com
 * @Function ktx-viewModel
 */

/** 一个简化使用，默认UI线程 */
inline fun ViewModel.viewModelScopeUI(crossinline block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.Main) {
        block()
    }
}

/** 一个简化使用，默认IO线程 */
inline fun ViewModel.viewModelScopeIO(crossinline block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
        block(this)
    }
}

/** 一个简化使用，默认线程 */
inline fun ViewModel.viewModelScopeDefault(
    context: CoroutineContext = Dispatchers.IO,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewModelScope.launch(context) {
        block()
    }
}
