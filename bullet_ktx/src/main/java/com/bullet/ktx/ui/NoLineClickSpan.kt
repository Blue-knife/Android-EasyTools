package com.bullet.ktx.ui

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ForegroundColorSpan

/**
 * @Author petterp
 * @Date 2020/6/9-7:07 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
// 无下划线超链接，使用textColorLink、textColorHighlight分别修改超链接前景色和按下时的颜色
class NoLineClickSpan(val color: Int = Color.parseColor("#0085BA")) : ForegroundColorSpan(color) {

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = false // 去掉下划线
    }
}
