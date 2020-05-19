package com.example.core.base.skin.support

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import com.example.core.base.skin.attr.SkinAttr
import com.example.core.base.skin.attr.SkinType

/**
 * 皮肤属性解析的支持类
 */
class SkinAttrSupport {

    companion object {

        /**
         * 获取 SkinAttr 的属性
         */
        fun getSkinAttrs(context: Context, attr: AttributeSet): List<SkinAttr> {
            val skinAttrs = mutableListOf<SkinAttr>()
            val count = attr.attributeCount
            for (i in 0 until count) {
                //获名称 ,值
                val attrName = attr.getAttributeName(i)
                val attrValue = attr.getAttributeValue(i)
                val skinType = getSkinType(attrName)
                if (skinType != null) {
                    val resName = getResName(context, attrValue)
                    if (TextUtils.isEmpty(resName)) {
                        continue
                    } else {
                        //resName 资源名称，skinType 资源类型
                        //目前只支持 background src textColor
                        val skinAttr = SkinAttr(resName!!, skinType)
                        skinAttrs.add(skinAttr)
                    }
                }
            }
            return skinAttrs
        }

        /**
         * 获取资源名称
         */
        private fun getResName(context: Context, attrValue: String): String? {
            if (attrValue.startsWith("@")) {
                val value = attrValue.substring(1)
                val resId = value.toInt()
                return context.resources.getResourceEntryName(resId)
            }
            return null
        }

        /**
         * 通过名称获取 SkinType
         */
        private fun getSkinType(attrName: String?): SkinType? {
            val skinTypes = SkinType.values()
            skinTypes.forEach {
                if (it.resName == attrName) {
                    return it
                }
            }
            return null
        }
    }

}