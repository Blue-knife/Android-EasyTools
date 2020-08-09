package com.bullet.ui.customView

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import com.bullet.ui.customView.RoundViewHelper.Companion.setViewOutLine

/**
 * 圆角 LinearLayout
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RoundLinearLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    fun setViewOutLine(radius: Int, radiusSize: Int) {
        setViewOutLine(this, radius, radiusSize)
    }

    init {
        setViewOutLine(this, attrs, defStyleAttr, 0)
    }
}