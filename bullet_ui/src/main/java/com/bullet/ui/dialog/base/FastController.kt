package com.bullet.ui.dialog.base

import android.content.Context
import android.graphics.Point
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.util.forEach
import java.lang.IllegalArgumentException

/**
 * @name FastController
 * @package com.example.ui.dialog.base
 * @author 345 QQ:1831712732
 * @time 2020/5/18 21:48
 * @description
 */

class FastController(
        private val alertDialog: FastDialog,
        private val window: Window?
) {

    private var viewHelper: DialogViewHelper? = null


    /**
     * 获取 Dialog
     */
    fun getDialog(): FastDialog {
        return alertDialog
    }

    /**
     * 获取 Dialog 的 Window
     */
    fun getWindow(): Window? {
        return window
    }

    /**
     * 设置文本
     */
    fun setText(viewId: Int, text: CharSequence) {
        viewHelper?.setText(viewId, text)
    }

    /**
     * 设置事件
     */
    fun setOnClickListener(viewId: Int, onClick: (Pair<View, FastDialog>) -> Unit) {
        viewHelper?.setOnClickListener(viewId, onClick, alertDialog)
    }

    /**
     * 获取 View
     */
    fun <T : View> getView(viewId: Int): T? {
        return viewHelper?.getView(viewId)
    }


    class AlertParams(val mContext: Context, val mThemeRedId: Int) {

        //布局的 View
        var mView: View? = null

        //布局的 Layout Id
        var mViewLayoutResId: Int = 0

        //点击空白是否能够取消 ,默认可以取消
        var mCancelable: Boolean = true

        //存放字体的修改，SparseArray 比 map 更加高效，条件是 key 必须为 Int
        var mTextArray = SparseArray<CharSequence>()

        //存放点击事件
        var mClickArray = SparseArray<(Pair<View, FastDialog>) -> Unit>()

        //宽度
        var mWidth: Int = ViewGroup.LayoutParams.WRAP_CONTENT

        //高度
        var mHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

        //百分比宽度
        var mPercentWidth = -1f

        // 百分比高度
        var mPercentHeight = -1f

        //透明度
        var mAlpha = 1f

        //动画
        var mAnimations = 0

        //位置
        var mGravity = Gravity.CENTER


        /**
         * 绑定和设置参数
         */
        fun apply(fast: FastController) {
            //设置布局
            var viewHelper: DialogViewHelper? = null
            if (mViewLayoutResId != 0) {
                viewHelper = DialogViewHelper(
                        mContext,
                        mViewLayoutResId
                )
            }
            if (mView != null) {
                viewHelper = DialogViewHelper()
                viewHelper.mContentView = mView!!
            }
            if (viewHelper == null) {
                throw IllegalArgumentException("未调用 setContentView")
            } else {
                fast.viewHelper = viewHelper
            }

            //给 dialog 设置布局
            fast.getDialog().setContentView(viewHelper.mContentView)

            //设置文本
            mTextArray.forEach { key, value ->
                fast.setText(key, value)
            }
            //设置点击事件
            mClickArray.forEach { key, value ->
                fast.setOnClickListener(key, value)
            }

            //配置参数
            val window = fast.window
            if (window != null) {
                window.setGravity(mGravity)
                if (mAnimations != 0) window.setWindowAnimations(mAnimations)
                val params = window.attributes
                params.alpha = mAlpha
                if (mPercentWidth != -1f) {
                    params.width = (getScreenWidth(fast.window) * mPercentWidth).toInt()
                } else {
                    params.width = mWidth
                }
                if (mPercentHeight != -1f) {
                    params.height = (getScreenHeight(fast.window) * mPercentHeight).toInt()
                } else {
                    params.height = mHeight
                }
                window.attributes = params
            }
        }

        /**
         * 获取屏幕宽度
         */
        private fun getScreenWidth(window: Window): Int {
            val defaultDisplay = window.windowManager.defaultDisplay
            val point = Point()
            defaultDisplay.getSize(point)
            return point.x
        }

        /**
         * 获取屏幕高度
         */
        private fun getScreenHeight(window: Window): Int {
            val defaultDisplay = window.windowManager.defaultDisplay
            val point = Point()
            defaultDisplay.getSize(point)
            return point.y
        }

    }

}