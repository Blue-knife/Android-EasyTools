package com.business.tools.test

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ktx.download.DownLoadLaunch
import com.example.ktx.download.OnStateListener
import com.business.toos.R
import kotlinx.android.synthetic.main.activity_down_load.*
import java.io.File

class DownLoadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_load)
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        fileUrl.setText("https://kotlinlang.org/docs/kotlin-docs.pdf")
        fileName.setText("Kotlin-Docs.pdf")
        downloadButton.setOnClickListener {
            DownLoadLaunch.create(this, fileUrl.text.toString(),
                    fileName.text.toString(), object : OnStateListener {
                override fun start() {
                    Toast.makeText(this@DownLoadActivity, "", Toast.LENGTH_LONG).show()
                }

                override fun process(value: Int) {
                    downloadButton.text = "Downloading $value"
                }

                override fun error(throwable: Throwable) {
                    Toast.makeText(this@DownLoadActivity, "下载出错：${throwable.message}", Toast.LENGTH_LONG).show()
                    downloadButton.text = "DownLoad"
                }

                override fun donal(file: File) {
                    downloadButton.text = "下载成功"
                    downloadPath.setText(file.absolutePath)
                    Toast.makeText(this@DownLoadActivity, "下载完成：" + file.path, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}
