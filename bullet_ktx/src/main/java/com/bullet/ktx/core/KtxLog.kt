package com.bullet.ktx.core

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**
 * @Author petterp
 * @Date 2020/5/31-6:54 PM
 * @Email ShiyihuiCloud@163.com
 * @Function Ktx-Log-Logger
 */
object KtxLog {
    const val VERBOSE = 1
    const val DEBUG = 2
    const val INFO = 3
    const val WARN = 4
    const val ERROR = 5
    const val NOTHING = 6
    const val KEY_TAG = "petterp"

    // 控制log等级
    var LEVEL = VERBOSE

    fun init(level: Int = VERBOSE) {
        LEVEL = level
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    fun v(tag: String = KEY_TAG, message: String) {
        if (LEVEL <= VERBOSE) {
            Logger.t(tag).v(message)
        }
    }

    fun d(message: Any, tag: String = KEY_TAG) {
        if (LEVEL <= DEBUG) {
            Logger.t(tag).d(message)
        }
    }

    fun i(tag: String = KEY_TAG, message: String) {
        if (LEVEL <= INFO) {
            Logger.t(tag).i(message)
        }
    }

    @JvmStatic
    fun w(tag: String = KEY_TAG, message: String) {
        if (LEVEL <= WARN) {
            Logger.t(tag).w(message)
        }
    }

    @JvmStatic
    fun json(tag: String = KEY_TAG, message: String) {
        if (LEVEL <= WARN) {
            Logger.t(tag).json(message)
        }
    }

    @JvmStatic
    fun e(tag: String = KEY_TAG, message: String) {
        if (LEVEL <= ERROR) {
            Logger.t(tag).e(message)
        }
    }

    @JvmStatic
    fun e(message: String) {
        if (LEVEL <= ERROR) {
            Logger.t("petterp").e(message)
        }
    }
}
