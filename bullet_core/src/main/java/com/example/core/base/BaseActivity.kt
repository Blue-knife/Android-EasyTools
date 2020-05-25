package com.example.core.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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