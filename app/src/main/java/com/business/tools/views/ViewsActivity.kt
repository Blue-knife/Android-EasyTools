package com.business.tools.views

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.business.toos.R

/**
 * @author 345
 * @date 2019/12/25
 */
class ViewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_views)
        init()

    }

    private fun init() {
        val textView = findViewById<CustomTextView>(R.id.tv)
        textView.setText("Android-EasyTools" + "一个通用的业务代码解决方案")

        textView.setTvs(arrayOf("Android-EasyTools", "通用", "解决方案"),
                intArrayOf(Color.BLUE, Color.GREEN, Color.RED))
        textView.notifyTv()
    }
}
