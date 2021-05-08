@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.bullet.core.base.skin

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import android.widget.Toast
import com.bullet.core.base.skin.attr.SkinView
import com.bullet.core.base.skin.callback.ISkinChangeListener
import com.bullet.core.base.skin.config.SkinConfig
import com.bullet.core.base.skin.config.SkinPreUtils
import java.io.File

@SuppressLint("StaticFieldLeak")
object SkinManager {

    private var mContext: Context? = null

    private val mSkinViews = mutableMapOf<ISkinChangeListener, MutableList<SkinView>>()

    private var skinResource: SkinResource? = null

    fun init(context: Context) {
        SkinPreUtils.init(context)
        this.mContext = context.applicationContext
        // 每次打开应用都会到这里来，做一系列的预防，防止皮肤被删除
        val currentSkinPath = SkinPreUtils.getSkinPath()
        if (!TextUtils.isEmpty(currentSkinPath)) {
            if (!isFile(currentSkinPath!!)) return
            else {
                if (!isPackageName(currentSkinPath)) return
            }
        } else {
            return
        }
        // 做一些初始化的工作
        skinResource = SkinResource(mContext!!, currentSkinPath)
    }

    /**
     * 加载新皮肤
     */
    fun loadSkin(skinPath: String): Int {
        if (equalsSkinPath(skinPath)) return SkinConfig.SKIN_CHANGE_NOTHING
        if (!isFile(skinPath)) return SkinConfig.SKIN_FILE_NOT_FOUND
        if (!isPackageName(skinPath)) return SkinConfig.SKIN_FILE_ERROR

        skinResource = SkinResource(mContext!!, skinPath)
        // 改变皮肤
        checkSkin()
        // 保存皮肤的状态
        saveSkinStatus(skinPath)
        return SkinConfig.SKIN_CHANGE_SUCCESS
    }

    /**
     * 恢复默认
     */
    fun restoreDefault(): Int {
        SkinPreUtils.getSkinPath() ?: return SkinConfig.SKIN_CHANGE_NOTHING
        SkinPreUtils.clearSkinInfo()
        // 当前app资源路径
        val resPath = mContext!!.packageResourcePath
        skinResource = SkinResource(mContext!!, resPath)
        // 设置为默认皮肤
        checkSkin()
        return SkinConfig.SKIN_CHANGE_SUCCESS
    }

    /**
     * 判断是否需要换肤，如果需要则进行换肤
     */
    fun checkChangeSkin(skinView: SkinView) {
        val currentSkinPath = SkinPreUtils.getSkinPath()
        if (!TextUtils.isEmpty(currentSkinPath)) {
            // 换肤
            skinView.skin()
        }
    }

    /**
     * 换肤
     */
    private fun checkSkin() {
        mSkinViews.forEach { maps ->
            val skinViews = maps.value
            skinViews.forEach {
                it.skin()
            }
            maps.key.changeSkin(skinResource!!)
        }
    }

    /**
     * 判断皮肤地址是否相同
     */
    private fun equalsSkinPath(skinPath: String): Boolean {
        val path = SkinPreUtils.getSkinPath()
        if (path != null && path == skinPath) {
            Toast.makeText(mContext, "请务重复更换皮肤", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    /**
     * 文件是否存在，true 表示存在
     */
    private fun isFile(filePath: String): Boolean {
        if (!File(filePath).exists()) {
            SkinPreUtils.clearSkinInfo()
            return false
        }
        return true
    }

    /**
     * 保存当前使用的皮肤
     */
    private fun saveSkinStatus(skinPath: String) {
        SkinPreUtils.saveSkinPath(skinPath)
    }

    /**
     * 包名是否为空，true 表示不为空
     */
    private fun isPackageName(currentSkinPath: String): Boolean {
        val packageName = mContext!!.packageManager.getPackageArchiveInfo(
            currentSkinPath, PackageManager.GET_ACTIVITIES
        ).packageName
        if (TextUtils.isEmpty(packageName)) {
            SkinPreUtils.clearSkinInfo()
            return false
        }
        return true
    }

    /**
     * 获取 List<SkinView> 通过 activity
     */
    fun getSkinViews(changeListener: ISkinChangeListener): MutableList<SkinView>? {
        return mSkinViews[changeListener]
    }

    /**
     * 注册
     */
    fun register(changeListener: ISkinChangeListener, skinViews: MutableList<SkinView>) {
        mSkinViews[changeListener] = skinViews
    }

    /**
     * 获取当前皮肤资源
     */
    fun getSkinResource(): SkinResource {
        return skinResource!!
    }

    fun unregister(changeListener: ISkinChangeListener) {
        mSkinViews.remove(changeListener)
    }
}
