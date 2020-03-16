package com.business.audio.mediaplayer.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_image_loader.app
 * @time 2019/12/10 21:52
 * @description 图片加载类，支持为各种 view ，notification ，appwidget ，ViewGroup 加载图片
 */
class ImageLoaderManager private constructor() {


    private object SignletonHolder {
        val INSTANCE = ImageLoaderManager()
    }


    /**
     * 为 ImageView 加载图片
     */
    fun displayImageForView(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView)
    }

    /**
     * 为 ImageView 加载圆形图片
     *
     * @param imageView iv
     * @param url       url
     */
    fun displayImageForCircle(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(object : BitmapImageViewTarget(imageView) {
                    //将 imageView 包装成 target
                    override fun setResource(resource: Bitmap?) {
                        val drawable = RoundedBitmapDrawableFactory.create(imageView.resources, resource)
                        //设置圆形 drawable
                        drawable.isCircular = true
                        imageView.setImageDrawable(drawable)
                    }
                })
    }

    /**
     * 为 ViewGroup 设置背景，并模糊处理
     *
     * @param group view
     * @param ulr   url
     */
    fun displayImageForViewGroup(group: ViewGroup, ulr: String) {
        Glide.with(group.context)
                .asBitmap()
                .load(ulr)
                .apply(initCommonRequestOption())
                .into(object : SimpleTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @SuppressLint("CheckResult")
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val drawable = BitmapDrawable(
                                Utils.doBlur(resource, 100, true))
                        group.background = drawable

                    }
                })
    }


    /**
     * 为notification加载图
     */
    fun displayImageForNotification(context: Context, rv: RemoteViews, id: Int,
                                    notification: Notification, notificationId: Int, url: String) {
        this.displayImageForTarget(context, initNotificationTarget(context, id, rv, notification, notificationId), url)
    }

    private fun initNotificationTarget(context: Context, id: Int, rv: RemoteViews, notification: Notification, notificationid: Int): Target<Bitmap> {
        return NotificationTarget(context, id, rv, notification, notificationid)
    }


    private fun displayImageForTarget(context: Context, target: Target<Bitmap>, url: String) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target)
    }

    @SuppressLint("CheckResult")
    private fun initCommonRequestOption(): RequestOptions {
        val options = RequestOptions()
        options.placeholder(android.R.mipmap.sym_def_app_icon)
                .error(android.R.mipmap.sym_def_app_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL)
        return options
    }

    companion object {

        val instance: ImageLoaderManager
            get() = SignletonHolder.INSTANCE
    }

}
