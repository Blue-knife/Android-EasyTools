package com.bullet.ktx.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation


/**
 * @Author petterp
 * @Date 2020/5/31-10:37 PM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
/**
 * 便于加载图
 * 默认大图
 */
//fun ImageView.glideL(url: String, @DrawableRes placeImage: Int = null) {
//    Glide.with(KtxContext.context).load(url).placeholder(placeImage)
//        .diskCacheStrategy(DiskCacheStrategy.ALL).error(placeImage).into(this)
//}
//
//fun ImageView.glideMax(url: String, @DrawableRes placeImage: Int = R.mipmap.img_place_default_xxh) {
//    Glide.with(MyApplication.context).asBitmap()
//        .format(DecodeFormat.PREFER_ARGB_8888)//设置图片解码格式
//        .placeholder(placeImage)
//        .diskCacheStrategy(DiskCacheStrategy.ALL)
//        .load(url)
//        .into(this)
//
//}

fun ImageView.glide(url: String) {
    Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
}

fun ImageView.glideNormal(url: String) {
    Glide.with(context).load(url).into(this)
}

fun ImageView.glideNormal(@DrawableRes res: Int) {
    Glide.with(context).load(res).into(this)
}

fun ImageView.glide(bitmap: Bitmap) {
    Glide.with(context).load(bitmap).diskCacheStrategy(DiskCacheStrategy.ALL).into(this)
}

fun ImageView.glide(@DrawableRes placeImage: Int) {
    Glide.with(context).load(placeImage).into(this)
}

/** 圆形图片Url */
fun ImageView.circleUrl(url: String) {
    Glide.with(this)
        .load(url)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

/** 圆形图片BitMap */
fun ImageView.circleBitmap(url: String) {
    Glide.with(this)
        .load(url)
        .apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

fun ImageView.glideNoComPress(url: String) {
    val requestOptions = RequestOptions()
    requestOptions.override(1080, 2200); //指定大小为300*200，无视imageView大小
    Glide.with(context).load(url).apply(requestOptions).into(this)
}

/**
 * 高斯模糊
 * url,模糊度,透明度+颜色，默认60%透明黑
 */
fun ImageView.glideblurry(url: String, radius: Int = 200, colorRes: String = "#99000000") {
    val multi = MultiTransformation(
        BlurTransformation(radius),
        ColorFilterTransformation(Color.parseColor(colorRes))
    )
    Glide.with(context).asBitmap().load(url)
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                try {
                    val paramsLogo: ViewGroup.LayoutParams = this@glideblurry.layoutParams
                    paramsLogo.height = (context as Activity).heightScreen()
                    paramsLogo.width = (context as Activity).widthScreen()
                    this@glideblurry.layoutParams = paramsLogo
                    this@glideblurry.setImageBitmap(resource)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return true
            }

        })
        .dontAnimate()
        .apply(RequestOptions.bitmapTransform(multi)).into(this)
}
//
///**
// * 默认小图
// */
//fun ImageView.glide(url: String, @DrawableRes placeImage: Int = R.mipmap.img_place_default_xh) {
//    Glide.with(MyApplication.context).load(url).placeholder(placeImage)
//        .diskCacheStrategy(DiskCacheStrategy.ALL).error(placeImage).into(this)
//}
