package com.business.tools.test

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.business.toos.R
import com.example.core.base.BaseSkinActivity
import com.example.ui.dialog.ToastDialog
import com.example.ui.dialog.base.FastDialog
import kotlinx.android.synthetic.main.activity_dialog_test.*


/**
 * @author 345
 */

class DialogActivity : BaseSkinActivity(), View.OnClickListener {

    override fun layout(): Int {
        return R.layout.activity_dialog_test
    }

    override fun bindView() {
        init()
    }

    private fun init() {
        dialog_test_one.setOnClickListener(this)
        dialog_test_two.setOnClickListener(this)
        dialog_test_three.setOnClickListener(this)
        dialog_test_spin.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dialog_test_one -> {
                FastDialog.Builder(this)
                        .setContentView(R.layout.dialog_hint)
                        .setText(R.id.tv_dialog_title, "标题")
                        .setWidth(0.6f)
                        .setText(R.id.tv_dialog_message, "我是 345")
                        .setOnClickListener(R.id.tv_dialog_confirm) {
                            Toast.makeText(this@DialogActivity, "确定", Toast.LENGTH_SHORT).show()
                        }
                        .setOnClickListener(R.id.tv_dialog_cancel) {
                            Toast.makeText(this@DialogActivity, "关闭", Toast.LENGTH_SHORT).show()
                            it.second.dismiss()
                        }
                        .show()
            }
            R.id.dialog_test_two -> {
                val dialog = FastDialog.Builder(this)
                        .setContentView(R.layout.dialog_bottom)
                        .setAlpha(1f)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .fromBottom(true)
                        .setCancelable(true)
                        .show()
                val edit = dialog.getView<AppCompatEditText>(R.id.dialog_edit)
                dialog.setOnClickListener(R.id.dialog_text) {
                    Toast.makeText(this@DialogActivity, edit?.text.toString(), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.dialog_test_three -> {
                ToastDialog.loading(this)
//                ToastDialog.error(this)
//                ToastDialog.finish(this)
//                ToastDialog.warn(this)
            }
//            R.id.dialog_test_spin -> CustomSpinDialog.CustomSpinBuilder()
//                    .setAlpha(1f)
//                    .setContentView(R.layout.dialog_custom_spin)
//                    .build()
//                    .setHeight(200)
//                    .setWidth(200)
//                    .setResouce(R.drawable.ic_dialog_spin)
//                    .show(supportFragmentManager, "spin")
            else -> {
            }
        }
    }

}
