package com.example.core.base.skin.attr

import android.view.View

class SkinAttr(private val resName: String, private val  type: SkinType) {

    fun skin(view: View) {
        type.skin(view,resName)
    }
}