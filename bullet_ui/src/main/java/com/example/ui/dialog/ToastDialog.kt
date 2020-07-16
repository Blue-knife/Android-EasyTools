package com.example.ui.dialog

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.example.ui.R
import com.example.ui.customView.ProgressView
import com.example.ui.dialog.base.FastDialog

/**
 * @name ToastDialog
 * @package com.example.ui.dialog
 * @author 345 QQ:1831712732
 * @time 2020/5/18 21:54
 * @description 通用的 Toast 对话框
 */

object ToastDialog {


    private var fastDialog: FastDialog? = null

    private fun createDialog(context: Context) {
        fastDialog = FastDialog.Builder(context)
                .setContentView(R.layout.dialog_toast)
                .setAlpha(1f)
                .build()
        fastDialog?.setOnDismissListener {
            fastDialog = null
        }
    }

    fun loading(context: Context) {
        createDialog(context)
        fastDialog?.getView<AppCompatImageView>(R.id.iv_toast_icon)?.visibility = View.GONE
        fastDialog?.getView<ProgressView>(R.id.pw_progress)?.visibility = View.VISIBLE
        fastDialog?.setText(R.id.tv_toast_message, "加载中")
        fastDialog?.show()
    }

    fun error(context: Context) {
        createDialog(context)
        show("错误", R.drawable.ic_dialog_error)
    }

    fun warn(context: Context) {
        createDialog(context)
        show("警告", R.drawable.ic_dialog_warning)
    }

    fun finish(context: Context) {
        createDialog(context)
        show("完成", R.drawable.ic_dialog_finish)
    }

    fun stop() {
        if (fastDialog != null) {
            fastDialog?.dismiss()
            fastDialog = null
        }
    }

    fun unLoading() {
        fastDialog?.dismiss()
    }

    fun show(context: Context, text: String) {
        createDialog(context)
        fastDialog?.getView<ProgressView>(R.id.pw_progress)?.visibility = View.GONE
        fastDialog?.getView<AppCompatImageView>(R.id.iv_toast_icon)?.visibility = View.GONE
        fastDialog?.setText(R.id.tv_toast_message, text)
        fastDialog?.show()
    }


    private fun show(text: String, imageResId: Int) {
        hideLoad()
        fastDialog?.getView<AppCompatImageView>(R.id.iv_toast_icon)
                ?.setBackgroundResource(imageResId)
        fastDialog?.setText(R.id.tv_toast_message, text)
        fastDialog?.show()
    }


    private fun hideLoad() {
        fastDialog?.getView<ProgressView>(R.id.pw_progress)?.visibility = View.GONE
        fastDialog?.getView<AppCompatImageView>(R.id.iv_toast_icon)?.visibility = View.VISIBLE
    }

}