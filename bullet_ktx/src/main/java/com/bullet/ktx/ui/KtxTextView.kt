package com.bullet.ktx.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

/**
 * @Author petterp
 * @Date 2020/6/2-11:16 AM
 * @Email ShiyihuiCloud@163.com
 * @Function TextView-Ktx-core
 */

/** 设置下划线
 * [startRes]下划线开始前.[res]要加下划线的文本,[endRes]下划线后要补充的语句
 * */
fun TextView.underLine(
    res: String,
    startRes: String = "",
    endRes: String = "",
    @SuppressLint("InlinedApi") flags: Int = Html.FROM_HTML_MODE_LEGACY
) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml("$startRes<u>$res</u>$endRes", flags)
    } else {
        Html.fromHtml("<u>$res</u>")
    }
}

/** 设置TextView的图片 */
fun TextView.setPaddingDraw(img: Int) {
    val drawable = resources.getDrawable(img)
    // 这一步必须要做,否则不会显示.
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(drawable, null, null, null)
}

fun TextView.underLines(
    splitter: String,
    values: List<String>,
    startRes: String = "",
    endRes: String = "",
    @SuppressLint("InlinedApi") flags: Int = Html.FROM_HTML_MODE_LEGACY
) {
    val stringBuild = StringBuilder()
    stringBuild.append(startRes)
    values.forEach { s ->
        stringBuild.append("<u>$s</u>$splitter")
    }
    var res: String = stringBuild.toString()
    if (values.isNotEmpty()) {
        res = res.substring(0, res.length - 1)
    }
    fromHtml("$res$endRes")
}

// <font color=#ff0000> </font>

fun TextView.fromHtml(
    res: String,
    @SuppressLint("InlinedApi") flags: Int = Html.FROM_HTML_MODE_LEGACY
) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(res, flags)
    } else {
        Html.fromHtml(res)
    }
}

/** 指定单个字符颜色改变 */
fun TextView.disColorSingle(color: Int, selectRes: String, allRes: String) {
    val spannableString = SpannableString(allRes)
    GlobalScope.launch(Dispatchers.IO) {
        disColorNoBack(spannableString, color, selectRes)
        withContext(Dispatchers.Main) {
            text = spannableString
        }
    }
}

/** 指定字符集颜色背景改变 */
fun TextView.disColors(color: Int, selectRes: List<String>?, allRes: String) {
    GlobalScope.launch(Dispatchers.IO) {
        val spannableString = SpannableString(allRes)
        selectRes?.forEach {
            disColor(spannableString, color, it)
        }
        withContext(Dispatchers.Main) {
            text = spannableString
        }
    }
}

private fun disColor(
    spannableString: SpannableString,
    color: Int,
    selectRes: String
) {
    val pattern = Pattern.compile(selectRes)
    val matcher = pattern.matcher(spannableString)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        spannableString.setSpan(
            BackgroundColorSpan(color),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

private fun disColorNoBack(
    spannableString: SpannableString,
    color: Int,
    selectRes: String
) {
    val pattern = Pattern.compile(selectRes)
    val matcher = pattern.matcher(spannableString)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()

        spannableString.setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@SuppressLint("SetTextI18n")
fun TextView.appendLike(likeRes: String, context: String, likeResColor: Int, obj: (View) -> Unit) {
    val spanString = SpannableString(likeRes)
    spanString.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                obj(widget)
            }
        },
        0, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spanString.setSpan(
        NoLineClickSpan(likeResColor),
        0,
        spanString.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = "$context "
    append(spanString)
    movementMethod = LinkMovementMethod.getInstance()
}

/** 无下划线 */
@SuppressLint("SetTextI18n")
fun TextView.appendNoLike(
    likeRes: String,
    context: String,
    likeResColor: Int,
    obj: (View) -> Unit
) {
    val spanString = SpannableString(likeRes)
    spanString.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                obj(widget)
            }
        },
        0, spanString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    spanString.setSpan(
        NoLineClickSpan(likeResColor),
        0,
        spanString.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = "$context "
    append(spanString)
    movementMethod = LinkMovementMethod.getInstance()
}

/** 复制文本到剪切板 */
fun Context.copyText(content: String) {
    val cm: ClipboardManager? = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val mClipData = ClipData.newPlainText("Label", content)
    cm?.setPrimaryClip(mClipData)
}
