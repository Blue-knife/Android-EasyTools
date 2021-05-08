package com.business.tools.test

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.bullet.core.ToastUtils
import com.bullet.core.base.BaseSkinActivity
import com.bullet.ui.dialog.ToastDialog
import com.bullet.ui.dialog.base.FastDialog
import com.business.toos.R
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
                        it.second.cancel()
                    }
                    .setOnClickListener(R.id.tv_dialog_cancel) {
                        Toast.makeText(this@DialogActivity, "关闭", Toast.LENGTH_SHORT).show()
                        it.second.cancel()
                    }
                    .build()
                    .show()
            }
            R.id.dialog_test_two -> {
                FastDialog.Builder(this)
                    .setContentView(R.layout.dialog_bottom)
                    .setAlpha(1f)
                    .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                    .fromBottom(true)
                    .setCancelable(true)
                    .setOnClickListener(R.id.dialog_edit) {
                        Toast.makeText(this@DialogActivity, (it.first as AppCompatEditText).text.toString(), Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }

            R.id.dialog_test_three -> {
                ToastDialog.loading(this)
            }
            R.id.dialog_test_spin -> {
                val dialog = FastDialog.Builder(this)
                    .setContentView(R.layout.dialog_custom_spin)
                    .stGravity(Gravity.CENTER)
                    .setWidth(200)
                    .setHeight(200)
                    .show()
                val view = dialog.getView<AppCompatImageView>(R.id.img_dialog_spin)!!
                val anim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
                anim.duration = 1000
                anim.start()
                anim.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) = Unit

                    override fun onAnimationEnd(animation: Animator?) {
                        if (dialog.isShowing) {
                            anim.start()
                        } else {
                            ToastUtils.showText("动画取消")
                            anim.cancel()
                            anim.removeAllListeners()
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) = Unit
                    override fun onAnimationStart(animation: Animator?) = Unit
                })
            }
            else -> {
            }
        }
    }
}
