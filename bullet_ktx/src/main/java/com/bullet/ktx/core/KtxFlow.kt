package com.cloudx.ktx.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * @Author petterp
 * @Date 2020/5/29-7:17 PM
 * @Email ShiyihuiCloud@163.com
 * @Function Flow-> 流相关的简化
 */

/** 发起一个耗时操作，然后在ui线程执行，记得最后发射 */
@ExperimentalCoroutinesApi
suspend inline fun <T> ktxFlow(
    crossinline obj: suspend (FlowCollector<T>) -> Unit,
    crossinline observer: (T) -> Unit,
    context: CoroutineContext = Dispatchers.Main
) {
    flow<T> {
        obj(this)
    }.flowOn(Dispatchers.IO).collect {
        withContext(context) {
            observer(it)
        }
    }
}