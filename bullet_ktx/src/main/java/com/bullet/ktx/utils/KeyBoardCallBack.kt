package com.bullet.ktx.utils

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.*
import androidx.core.view.ViewCompat

class KeyboardListener {
    // 当前状态
    internal var isVisible: Boolean = false

    // 高度变化监听
    var onHeight: ((isVisible: Boolean, height: Int) -> Unit)? = null

    // 键盘即将显示前
    var onShowPrepare: (() -> Unit)? = null

    // 键盘完全显示结束
    var onShowEnd: (() -> Unit)? = null

    // 键盘正在隐藏前
    var onHidePrepare: (() -> Unit)? = null

    // 键盘完全隐藏时
    var onHideEnd: (() -> Unit)? = null
}

/**
 * 用于监听软键盘的改变
 * @author petterp
 */
fun View.observeKeyBoard(listener: KeyboardListener.() -> Unit) {
    val keyBoardCallBack = KeyBoardCallBack(KeyboardListener().apply(listener))
    ViewCompat.setWindowInsetsAnimationCallback(this, keyBoardCallBack)
    ViewCompat.setOnApplyWindowInsetsListener(this, keyBoardCallBack)
}

class KeyBoardCallBack(val listener: KeyboardListener) :
    WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE),
    OnApplyWindowInsetsListener {

    private var view: View? = null
    private var lastWindowInsets: WindowInsetsCompat? = null

    private var deferredInsets = false

    override fun onPrepare(animation: WindowInsetsAnimationCompat) {
        if (animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            deferredInsets = true
            val visible = !(lastWindowInsets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false)
            listener.isVisible = visible
            if (visible) {
                listener.onShowPrepare?.invoke()
            } else {
                listener.onHidePrepare?.invoke()
            }
        }
    }

    override fun onProgress(
        insets: WindowInsetsCompat,
        runningAnimations: MutableList<WindowInsetsAnimationCompat>
    ): WindowInsetsCompat {
        if (deferredInsets) {
            // 处理其他系统bar和软键盘同时显示情况高度计算问题
            listener.onHeight?.let {
                val typesInset = insets.getInsets(WindowInsetsCompat.Type.ime())
                val otherInset = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val subtract = Insets.subtract(typesInset, otherInset)
                val diff = Insets.max(subtract, Insets.NONE)
                // 获取键盘实时高度变化
                val height = diff.bottom
                it(listener.isVisible, height)
            }
        }
        return insets
    }

    override fun onApplyWindowInsets(v: View?, insets: WindowInsetsCompat?): WindowInsetsCompat {
        view = v
        lastWindowInsets = insets
        return WindowInsetsCompat.CONSUMED
    }

    override fun onEnd(animation: WindowInsetsAnimationCompat) {
        if (deferredInsets && animation.typeMask and WindowInsetsCompat.Type.ime() != 0) {
            deferredInsets = false
            var visible = false
            // 软键盘是否显示
            if (lastWindowInsets != null) {
                visible = lastWindowInsets!!.isVisible(WindowInsetsCompat.Type.ime())
            }
            listener.isVisible = visible
            if (visible) {
                listener.onShowEnd?.invoke()
            } else {
                listener.onHideEnd?.invoke()
            }
            if (view != null && lastWindowInsets != null)
                ViewCompat.dispatchApplyWindowInsets(view!!, lastWindowInsets!!)
        }
    }
}
