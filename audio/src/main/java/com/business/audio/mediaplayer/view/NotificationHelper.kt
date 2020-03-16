package com.business.audio.mediaplayer.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

import androidx.core.app.NotificationCompat

import com.business.audio.R
import com.business.audio.app.AudioHelper
import com.business.audio.mediaplayer.core.AudioController
import com.business.audio.mediaplayer.core.MusicService
import com.business.audio.mediaplayer.model.AudioBean
import com.business.audio.mediaplayer.utils.ImageLoaderManager

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.view
 * @time 2019/12/24 20:45
 * @description 音乐 Notification 帮助类
 * 完成Notification 的创建和初始化
 * 对外提供的更新方法
 */
class NotificationHelper {

    /**
     * ui 相关
     */
    /**
     * 获取通知
     *
     * @return
     */
    var notification: Notification? = null
        private set
    private var mRemoteViews: RemoteViews? = null
    private var mSmallRemoteViews: RemoteViews? = null
    private var mNotificationManager: NotificationManager? = null

    /**
     * data
     */
    private var mListener: NotificationHelperListener? = null
    private var packageName: String? = null
    private var mAudioBean: AudioBean? = null

    /**
     * 与音乐service的回调通信
     */
    interface NotificationHelperListener {
        /**
         * 綁定服務
         */
        fun onNotificationInit()
    }


    private object NotificationHelperHolder {
        val M_INSTANCE = NotificationHelper()
    }

    fun init(listener: NotificationHelperListener) {
        mNotificationManager = AudioHelper.context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        packageName = AudioHelper.context!!.getPackageName()
        mAudioBean = AudioController.instance.nowPlaying
        initNotification()
        mListener = listener
        if (mListener != null) {
            mListener!!.onNotificationInit()
        }
    }

    /**
     * 初始化 notification
     */
    private fun initNotification() {
        if (notification == null) {
            initRemoteViews()
            //适配8.0的消息渠道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
                channel.enableLights(false)
                //设置不震动
                channel.enableVibration(false)
                channel.vibrationPattern = longArrayOf(0)
                mNotificationManager!!.createNotificationChannel(channel)
            }
            val builder = NotificationCompat.Builder(AudioHelper.context!!, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //大布局
                    .setCustomBigContentView(mRemoteViews)
                    //小布局
                    .setContent(mSmallRemoteViews)
            notification = builder.build()
            showLoadStatus(mAudioBean)
        }
    }

    /**
     * 创建 Notification 的布局，默认布局为Loading 状态
     */
    private fun initRemoteViews() {
        val layoutId = R.layout.notification_big_layout
        mRemoteViews = RemoteViews(packageName, layoutId)
        mRemoteViews!!.setTextViewText(R.id.title_view, mAudioBean!!.name)
        mRemoteViews!!.setTextViewText(R.id.tip_view, mAudioBean!!.album)

        val smallLayoutId = R.layout.notification_small_layout
        mSmallRemoteViews = RemoteViews(packageName, smallLayoutId)
        mSmallRemoteViews!!.setTextViewText(R.id.title_view, mAudioBean!!.name)
        mSmallRemoteViews!!.setTextViewText(R.id.tip_view, mAudioBean!!.album)

        //点击播放暂停需要发送的广播
        val playIntent = Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR)
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(AudioHelper.context, 1, playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews!!.setOnClickPendingIntent(R.id.play_view, playPendingIntent)
        mRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white)
        mSmallRemoteViews!!.setOnClickPendingIntent(R.id.play_view, playPendingIntent)
        mSmallRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white)

        //点击上一首按钮广播
        val previousIntent = Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR)
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE)
        //第四个参数：更新策略，有变化就会更新
        val previousPendingIntent = PendingIntent.getBroadcast(AudioHelper.context, 2, previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews!!.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent)
        mRemoteViews!!.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white)

        //点击下一首按钮广播
        val nextIntent = Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR)
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE)
        val nextPendingIntent = PendingIntent.getBroadcast(AudioHelper.context, 3, nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews!!.setOnClickPendingIntent(R.id.next_view, nextPendingIntent)
        mRemoteViews!!.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white)
        mSmallRemoteViews!!.setOnClickPendingIntent(R.id.next_view, nextPendingIntent)
        mSmallRemoteViews!!.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white)

        //点击收藏按钮广播
        val favouriteIntent = Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR)
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV)
        val favouritePendingIntent = PendingIntent.getBroadcast(AudioHelper.context, 4, favouriteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        mRemoteViews!!.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent)

    }


    /**
     * 更新为加载状态
     */
    fun showLoadStatus(bean: AudioBean?) {
        mAudioBean = bean
        if (mRemoteViews != null) {
            mRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white)
            mRemoteViews!!.setTextViewText(R.id.title_view, mAudioBean!!.name)
            mRemoteViews!!.setTextViewText(R.id.tip_view, mAudioBean!!.album)
            //加载图片
            ImageLoaderManager.instance
                    .displayImageForNotification(AudioHelper.context!!,
                            mRemoteViews!!,
                            R.id.image_view,
                            notification!!,
                            NOTIFICATION_ID,
                            mAudioBean!!.albumPic)
            //更新收藏状态


            //更新小布局
            mSmallRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white)
            mSmallRemoteViews!!.setTextViewText(R.id.title_view, mAudioBean!!.name)
            mSmallRemoteViews!!.setTextViewText(R.id.tip_view, mAudioBean!!.album)
            ImageLoaderManager.instance
                    .displayImageForNotification(AudioHelper.context!!,
                            mSmallRemoteViews!!,
                            R.id.image_view,
                            notification!!,
                            NOTIFICATION_ID,
                            mAudioBean!!.albumPic)

            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * 更新为播放状态
     */
    fun showPlayStatus() {
        if (mRemoteViews != null) {
            mRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white)
            mSmallRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white)
            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * 更新Wie暂停状态
     */
    fun showPauseStatus() {
        if (mRemoteViews != null) {
            mRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white)
            mSmallRemoteViews!!.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white)
            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        }
    }

    companion object {

        val CHANNEL_ID = "channel_id_audio"
        val CHANNEL_NAME = "channel_name_audio"
        val NOTIFICATION_ID = 0x111

        val instance: NotificationHelper
            get() = NotificationHelperHolder.M_INSTANCE
    }
}
