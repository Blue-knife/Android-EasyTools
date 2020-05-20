package com.example.core.base.skin.attr

import android.annotation.TargetApi
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.core.base.skin.SkinManager
import com.example.core.base.skin.SkinResource

enum class SkinType(val resName: String) {

    TEXT_COLOR("textColor") {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun skin(view: View, resName: String) {
            val skinResource = getSkinResource()
            val color = skinResource.getColorByName(resName)
            if (color != null) {
                if (view is AppCompatButton) {
                    view.setTextColor(color)
                } else if (view is AppCompatTextView) {
                    view.setTextColor(color)
                }
            }
        }
    },
    BACKGROUND("background") {
        @TargetApi(Build.VERSION_CODES.M)
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun skin(view: View, resName: String) {
            //背景可能是图片，也可能是颜色
            val skinResource = getSkinResource()
            val drawable = skinResource.getDrawableByName(resName)
            if (drawable != null) {
                view.background = drawable
            } else {
                //可能是颜色
                val color = skinResource.getColorByName(resName)
                if (color != null) {
                    view.setBackgroundColor(color.defaultColor)
                }
            }
        }
    },
    SRC("src") {
        override fun skin(view: View, resName: String) {
            val skinResource = getSkinResource()
            val drawable = skinResource.getDrawableByName(resName)
            if (drawable != null) {
                (view as AppCompatImageView).setImageDrawable(drawable)
            }
        }
    };

    abstract fun skin(view: View, resName: String)

    fun getSkinResource(): SkinResource {
        return SkinManager.getSkinResource()
    }
}