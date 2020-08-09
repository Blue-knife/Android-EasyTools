package com.bullet.core.base.skin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable

/**
 * 皮肤的资源管理器
 */
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@SuppressLint("DiscouragedPrivateApi")
class SkinResource(private val context: Context, private val skinPath: String) {

    /**
     * 资源通过这个对象获取
     */
    private var mSkinResource: Resources? = null

    private var mPackageName: String? = null

    init {
        tryCatch {
            val superRes = context.resources
            val asset = AssetManager::class.java.newInstance()
            val method =
                AssetManager::class.java.getDeclaredMethod("addAssetPath", String::class.java)
            method.invoke(asset, skinPath)
            mSkinResource = Resources(asset, superRes.displayMetrics, superRes.configuration)

            // 获取 skinPath 包名
            mPackageName = context.packageManager.getPackageArchiveInfo(
                skinPath, PackageManager.GET_ACTIVITIES
            ).packageName
        }
    }

    /**
     * 通过名字获取 Drawable
     */
    fun getDrawableByName(resName: String): Drawable? {
        return tryCatch {
            val resId = mSkinResource!!.getIdentifier(resName, "drawable", mPackageName)
            mSkinResource!!.getDrawable(resId)
        }
    }

    /**
     * 通过名字获取颜色
     */
    fun getColorByName(resName: String): ColorStateList? {
        return tryCatch {
            val resId = mSkinResource!!.getIdentifier(resName, "color", mPackageName)
            mSkinResource!!.getColorStateList(resId)
        }
    }

    private inline fun <T> tryCatch(block: () -> T): T? {
        return try {
            block()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }
}