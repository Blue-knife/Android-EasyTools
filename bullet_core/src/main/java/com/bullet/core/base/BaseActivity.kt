package com.bullet.core.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout())
        params(intent)
        bindView()
    }

    abstract fun layout(): Int

    open fun params(intent: Intent) = Unit

    abstract fun bindView()
}
