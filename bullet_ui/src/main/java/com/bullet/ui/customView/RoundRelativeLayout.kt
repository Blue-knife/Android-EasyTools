package com.bullet.ui.customView

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.bullet.ui.customView.RoundViewHelper.Companion.setViewOutLine

/**
 * 圆角 RelativeLayout
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RoundRelativeLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {
    fun setViewOutLine(radius: Int, radiusSize: Int) {
        setViewOutLine(this, radius, radiusSize)
    }

    init {
        setViewOutLine(this, attrs, defStyleAttr, 0)
    }
}
