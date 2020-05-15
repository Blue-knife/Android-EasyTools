package com.business.tools.test

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.business.tools.views.page.PageAdapter
import com.business.toos.R
import com.example.ui.customView.CustomTextView
import kotlinx.android.synthetic.main.activity_views.*

/**
 * @author 345
 * @date 2019/12/25
 */
class ViewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_views)
        init()
        scroll.adapter = ScrollerAdapter(R.layout.item)
    }

    private fun init() {
        val textView = findViewById<CustomTextView>(R.id.tv)
        textView.setText("Android-EasyTools" + "一个通用的业务代码解决方案")

        textView.setTvs(arrayOf("Android-EasyTools", "通用", "解决方案"),
                intArrayOf(Color.BLUE, Color.GREEN, Color.RED))
        textView.notifyTv()
        textView.setOnClickListener {
            Toast.makeText(this, "${scroll.mPosition}", Toast.LENGTH_LONG).show()
        }
    }
}

class ScrollerAdapter(itemRes: Int) : PageAdapter(itemRes) {

    private val url = arrayOf(
            "https://wanandroid.com/blogimgs/7a8c08d1-35cb-43cd-a302-ce9b0f89fc59.png",
            "https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png",
            "https://www.wanandroid.com/blogimgs/50c115c2-cf6c-4802-aa7b-a4334de444cd.png",
            "https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png"
    )

    override fun count(): Int {
        return url.size
    }

    override fun view(view: View, position: Int) {
        view.setOnClickListener {
            Toast.makeText(view.context, "$position", Toast.LENGTH_LONG).show()
        }
        Glide.with(view.context)
                .load(url[position])
                .into((view as AppCompatImageView))
    }

}
