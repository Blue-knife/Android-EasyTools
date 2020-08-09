package com.cloudx.ktx.core

import androidx.lifecycle.LiveData

/**
 * @Author petterp
 * @Date 2020/6/3-11:08 AM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
open class MutableNoNullLiveData<T> : LiveData<T> {
    /**
     * Creates a MutableLiveData initialized with the given `value`.
     *
     * @param value initial value
     */
    constructor(value: T) : super(value) {}

    /**
     * Creates a MutableLiveData with no value assigned to it.
     */
    constructor() : super() {}

    override fun postValue(value: T) {
        value?.let {
            super.postValue(value)
        }
    }

    override fun setValue(value: T) {
        value?.let {
            super.setValue(value)
        }
    }
}
