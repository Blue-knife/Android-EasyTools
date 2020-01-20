package com.business.audio.mediaplayer.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_image_loader.app
 * @time 2019/12/10 21:52
 * @description 图片加载类，支持为各种 view ，notification ，appwidget ，ViewGroup 加载图片
 */
public class ImageLoaderManager {


    private ImageLoaderManager() {
    }


    private static class SignletonHolder {
        private static final ImageLoaderManager INSTANCE = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance() {
        return SignletonHolder.INSTANCE;
    }


    /**
     * 为 ImageView 加载图片
     */
    public void displayImageForView(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(imageView);
    }

    /**
     * 为 ImageView 加载圆形图片
     *
     * @param imageView iv
     * @param url       url
     */
    public void displayImageForCircle(final ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView) {
                    //将 imageView 包装成 target
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        //设置圆形 drawable
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 为 ViewGroup 设置背景，并模糊处理
     *
     * @param group view
     * @param ulr   url
     */
    public void displayImageForViewGroup(final ViewGroup group, String ulr) {
        Glide.with(group.getContext())
                .asBitmap()
                .load(ulr)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @SuppressLint("CheckResult")
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap res = resource;
                        Drawable drawable = new BitmapDrawable(
                                Utils.doBlur(res, 100, true));
                        group.setBackground(drawable);

                    }
                });
    }


    /**
     * 为notification加载图
     */
    public void displayImageForNotification(Context context, RemoteViews rv, int id,
                                            Notification notification, int notificationId, String url) {
        this.displayImageForTarget(context,
                initNotificationTarget(context, id, rv, notification, notificationId), url);
    }

    private Target initNotificationTarget(Context context, int id, RemoteViews rv, Notification notification, int notificationid) {
        return new NotificationTarget(context, id, rv, notification, notificationid);
    }


    private void displayImageForTarget(Context context, Target target, String url) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);
    }

    @SuppressLint("CheckResult")
    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(android.R.mipmap.sym_def_app_icon)
                .error(android.R.mipmap.sym_def_app_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .priority(Priority.NORMAL);
        return options;
    }

}
