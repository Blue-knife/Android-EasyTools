package com.business.tools.basedialog.test

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.business.tools.basedialog.utils.BaseFragDialog
import com.business.tools.basedialog.utils.CustomSpinDialog
import com.business.tools.basedialog.utils.DateDialog
import com.business.tools.basedialog.utils.ToastDialog
import com.business.toos.R
import kotlinx.android.synthetic.main.activity_dialog_test.*


/**
 * @author 345
 */

class DialogActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_test)

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
            R.id.dialog_test_one -> BaseFragDialog.Builder()
                    .setAlpha(1f)
                    .setAnimation(R.style.BottomAnimStyle)
                    .setGravity(Gravity.CENTER)
                    .setContentView(R.layout.dialog_hint)
                    .build()
                    .setWidth(this, 0.6f)
                    .setText(R.id.tv_dialog_title, "我是名字")
                    .setText(R.id.tv_dialog_message, "我是内容")
                    .setListener(R.id.tv_dialog_confirm) { dialog, view ->
                        Toast.makeText(this@DialogActivity, "成功", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .show(supportFragmentManager, "345")
            R.id.dialog_test_two -> DateDialog.DateBuilder()
                    .setContentView(R.layout.dialog_hint)
                    .setCancelable(false)
                    .build()
                    .setWidth(this, 0.6f)
                    .setListener(R.id.tv_dialog_cancel) { dialog, view ->
                        Toast.makeText(this@DialogActivity, "关闭", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setListener(R.id.tv_dialog_confirm) { dialog, view ->
                        Toast.makeText(this@DialogActivity, "确定", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .show(supportFragmentManager, "345")
            R.id.dialog_test_three -> ToastDialog.ToastBuilder()
                    .setContentView(R.layout.dialog_toast)
                    .setCancelable(false)
                    .build()
                    .postAtTime(3000)
                    .setMessage("加载中")
                    //多种 type ，加载，加载完成，警告，失败等
                    .setType(ToastDialog.Type.LOADING)
                    .show(supportFragmentManager, "345")
            R.id.dialog_test_spin -> CustomSpinDialog.CustomSpinBuilder()
                    .setAlpha(1f)
                    .setContentView(R.layout.dialog_custom_spin)
                    .build()
                    .setHeight(200)
                    .setWidth(200)
                    .setResouce(R.drawable.ic_dialog_spin)
                    .show(supportFragmentManager, "spin")
            else -> {
            }
        }
    }

}
