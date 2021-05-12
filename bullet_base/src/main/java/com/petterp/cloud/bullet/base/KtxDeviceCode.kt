package com.petterp.cloud.bullet.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*

/**
 * @Author petterp
 * @Date 2020/8/10-2:12 PM
 * @Email ShiyihuiCloud@163.com
 * @Function 确定设备唯一码
 */
object KtxDeviceCode {

    /** Google推荐的方式，用于追踪非登录用户id
     ANDROID_ID生成规则：签名+设备信息+设备用户
     ANDROID_ID重置规则：设备恢复出厂设置时，ANDROID_ID将被重置
     * */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /** 使用硬件信息生成 设备id
     *  需要 READ_PHONE_STATE 权限
     *  有一定概率的重复性，推荐使用第一种
     * */
    @SuppressLint("MissingPermission")
    fun getUUID(): String {
        val mSzDevIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.SUPPORTED_ABIS[0].length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 // 13 位
        val serial = try {
            val serialId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Build.getSerial()
            } else {
                Build.SERIAL
            }
            // API>=9 使用serial号
            return UUID(mSzDevIDShort.hashCode().toLong(), serialId.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            // 异常时随便初试化一个
            "serial"
        }
        // 使用硬件信息拼凑出来的15位号码
        return UUID(mSzDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
    }

    /** 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限) */
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getImei(ctx: Context): String? {
        val tm = ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        return tm.deviceId
    }
}
