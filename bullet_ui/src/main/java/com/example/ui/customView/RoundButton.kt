package com.example.ui.customView

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.ui.customView.RoundViewHelper.Companion.setViewOutLine

/**
 * 圆角 Button
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RoundButton @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatButton(context, attrs, defStyleAttr) {
    fun setViewOutLine(radius: Int, radiusSize: Int) {
        setViewOutLine(this, radius, radiusSize)
    }

    init {
        setViewOutLine(this, attrs, defStyleAttr, 0)
    }
}