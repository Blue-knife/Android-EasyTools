package com.business.audio.mediaplayer.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.business.audio.R;
import com.business.audio.app.AudioHelper;
import com.business.audio.mediaplayer.core.AudioController;
import com.business.audio.mediaplayer.core.MusicService;
import com.business.audio.mediaplayer.model.AudioBean;
import com.business.audio.mediaplayer.utils.ImageLoaderManager;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.view
 * @time 2019/12/24 20:45
 * @description 音乐 Notification 帮助类
 * 完成Notification 的创建和初始化
 * 对外提供的更新方法
 */
public class NotificationHelper {

    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        /**
         * 綁定服務
         */
        void onNotificationInit();
    }

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    /**
     * ui 相关
     */
    private Notification mNotification;
    private RemoteViews mRemoteViews;
    private RemoteViews mSmallRemoteViews;
    private NotificationManager mNotificationManager;

    /**
     * data
     */
    private NotificationHelperListener mListener;
    private String packageName;
    private AudioBean mAudioBean;


    private static class NotificationHelperHolder {
        public static final NotificationHelper M_INSTANCE = new NotificationHelper();
    }

    public static NotificationHelper getInstance() {
        return NotificationHelperHolder.M_INSTANCE;
    }

    public void init(NotificationHelperListener listener) {
        mNotificationManager = (NotificationManager) AudioHelper.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        packageName = AudioHelper.getContext().getPackageName();
        mAudioBean = AudioController.getInstance().getNowPlaying();
        initNotification();
        mListener = listener;
        if (mListener != null) {
            mListener.onNotificationInit();
        }
    }

    /**
     * 获取通知
     *
     * @return
     */
    public Notification getNotification() {
        return mNotification;
    }

    /**
     * 初始化 notification
     */
    private void initNotification() {
        if (mNotification == null) {
            initRemoteViews();
            //适配8.0的消息渠道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                //设置不震动
                channel.enableVibration(false);
                channel.setVibrationPattern(new long[]{0});
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //大布局
                    .setCustomBigContentView(mRemoteViews)
                    //小布局
                    .setContent(mSmallRemoteViews);
            mNotification = builder.build();
            showLoadStatus(mAudioBean);
        }
    }

    /**
     * 创建 Notification 的布局，默认布局为Loading 状态
     */
    private void initRemoteViews() {
        int layoutId = R.layout.notification_big_layout;
        mRemoteViews = new RemoteViews(packageName, layoutId);
        mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

        int smallLayoutId = R.layout.notification_small_layout;
        mSmallRemoteViews = new RemoteViews(packageName, smallLayoutId);
        mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

        //点击播放暂停需要发送的广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 1, playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);

        //点击上一首按钮广播
        Intent previousIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        //第四个参数：更新策略，有变化就会更新
        PendingIntent previousPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 2, previousIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);
        mRemoteViews.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white);

        //点击下一首按钮广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 3, nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);

        //点击收藏按钮广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 4, favouriteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);

    }


    /**
     * 更新为加载状态
     */
    public void showLoadStatus(AudioBean bean) {
        mAudioBean = bean;
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            //加载图片
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(),
                            mRemoteViews,
                            R.id.image_view,
                            mNotification,
                            NOTIFICATION_ID,
                            mAudioBean.albumPic);
            //更新收藏状态


            //更新小布局
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(),
                            mSmallRemoteViews,
                            R.id.image_view,
                            mNotification,
                            NOTIFICATION_ID,
                            mAudioBean.albumPic);

            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新为播放状态
     */
    public void showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 更新Wie暂停状态
     */
    public void showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }
}
