package com.example.ui.dialog.base

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import com.example.ui.R
import java.lang.ref.WeakReference

/**
 * @name FastDialog
 * @package com.example.ui.dialog.base
 * @author 345 QQ:1831712732
 * @time 2020/5/18 21:49
 * @description
 */

class FastDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    private val mAlert = FastController(this, window)

    /**
     * 设置文本
     */
    fun setText(viewId: Int, text: CharSequence) {
        mAlert.setText(viewId, text)
    }

    /**
     * 设置事件
     */
    fun setOnClickListener(viewId: Int, listener: (Pair<View, FastDialog>) -> Unit) {
        mAlert.setOnClickListener(viewId, WeakReference(listener))
    }

    /**
     * 获取 Dialog 中的 View
     */
    fun <T : View> getView(viewId: Int): T? {
        return mAlert.getView(viewId)
    }


    /**
     * 建造者
     */
    class Builder {

        private var p: FastController.AlertParams
        private var fastDialog: FastDialog? = null

        constructor(context: Context) : this(context, R.style.dialog)

        constructor(context: Context, themeResId: Int) {
            p = FastController.AlertParams(context, themeResId)
        }


        /**
         * 设置 ContentView
         */
        fun setContentView(view: View): Builder {
            p.mView = view
            p.mViewLayoutResId = 0
            return this
        }

        /**
         * 设置 ContentView Id
         */
        fun setContentView(layoutId: Int): Builder {
            p.mView = null
            p.mViewLayoutResId = layoutId
            return this
        }

        /**
         * 设置最大宽度
         */
        fun fullWidth(): Builder {
            p.mWidth = ViewGroup.LayoutParams.MATCH_PARENT
            return this
        }

        /**
         * 是否开启底部动画
         */
        fun fromBottom(isAnimation: Boolean): Builder {
            if (isAnimation) {
                p.mAnimations = R.style.dialog_from_bottom_anim
            }
            p.mGravity = Gravity.BOTTOM
            return this
        }

        /**
         * 设置宽高
         */
        fun setWidthAndHeight(width: Int, height: Int): Builder {
            p.mWidth = width
            p.mHeight = height
            return this
        }

        fun setWidth(width: Int): Builder {
            p.mWidth = width
            return this
        }

        fun setHeight(height: Int): Builder {
            p.mHeight = height
            return this
        }

        /**
         * 百分比的形式设置宽
         * 范围 0 --- 1 之间
         */
        fun setWidth(width: Float): Builder {
            if (width in 0.0..1.0) p.mPercentWidth = width
            return this
        }

        /**
         * 百分比的形式设置高
         * 范围 0 --- 1 之间
         */
        fun setHeight(height: Float): Builder {
            if (height in 0.0..1.0) p.mPercentHeight = height
            return this
        }

        /**
         * 设置透明度
         * 范围 0 --- 1 之间
         */
        fun setAlpha(alpha: Float): Builder {
            if (alpha in 0.0..1.0) p.mAlpha = alpha
            return this
        }

        /**
         * 设置位置
         */
        fun stGravity(gravity: Int): Builder {
            p.mGravity = gravity
            return this
        }

        /**
         * 设置默认动画
         */
        fun addDefaultAnimation(): Builder {
            p.mAnimations = R.style.dialog_from_bottom_anim
            return this
        }

        /**
         * 添加自定义动画
         */
        fun setAnimations(@StyleRes intStyleAnimations: Int): Builder {
            p.mAnimations = intStyleAnimations
            return this
        }

        /**
         * 是否点击空白取消
         */
        fun setCancelable(cancelable: Boolean): Builder {
            p.mCancelable = cancelable
            return this
        }

        /**
         * 设置文本
         */
        fun setText(viewId: Int, text: CharSequence): Builder {
            p.mTextArray.put(viewId, text)
            return this
        }


        /**
         * 设置点击事件
         */
        fun setOnClickListener(viewId: Int, listener: (Pair<View, FastDialog>) -> Unit): Builder {
            p.mClickArray.put(viewId, WeakReference(listener))
            return this
        }


        private fun create(): FastDialog {
            // Context has already been wrapped with the appropriate theme.
            val dialog = FastDialog(
                    p.mContext,
                    p.mThemeRedId
            )
            p.apply(dialog.mAlert)
            dialog.setCancelable(p.mCancelable)
            if (p.mCancelable) {
                dialog.setCanceledOnTouchOutside(true)
            }
            return dialog
        }


        fun build(): FastDialog {
            if (fastDialog == null) fastDialog = create()
            return fastDialog!!
        }

        fun show(): FastDialog {
            if (fastDialog == null) fastDialog = create()
            fastDialog!!.show()
            return fastDialog!!
        }
    }
}