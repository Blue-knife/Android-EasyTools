package com.cloudx.ktx.core

import androidx.lifecycle.MutableLiveData

/**
 * @Author petterp
 * @Date 2020/5/30-8:05 PM
 * @Email ShiyihuiCloud@163.com
 * @Function ktx-LiveData noNull
 */
fun <T> MutableLiveData<T>.norPostValue(value: T?) {
    value?.let {
        postValue(it)
    }
}

fun <T> MutableLiveData<T>.norSetValue(value: T?) {
    value?.let {
        setValue(it)
    }
}

/** 非null */
val MutableLiveData<Boolean>.booleanValue
    get() = value ?: false

val MutableLiveData<String>.stringValue
    get() = value ?: ""
