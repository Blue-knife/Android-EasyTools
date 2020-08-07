package com.business.audio.mediaplayer.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.text.TextUtils
import android.widget.Toast
import com.business.audio.app.AudioHelper
import com.business.audio.mediaplayer.event.AudioLoadEvent
import com.business.audio.mediaplayer.event.AudioPauseEvent
import com.business.audio.mediaplayer.event.AudioReleaseEvent
import com.business.audio.mediaplayer.event.AudioStartEvent
import com.business.audio.mediaplayer.model.AudioBean
import com.business.audio.mediaplayer.view.NotificationHelper
import com.business.audio.mediaplayer.view.NotificationHelper.Companion.NOTIFICATION_ID
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


/**
 * 音乐后台服务,并更新notification状态
 *
 * @author 345
 */
class MusicService : Service(), NotificationHelper.NotificationHelperListener {

    private var mAudioBeans: ArrayList<AudioBean>? = null

    private var mReceiver: NotificationReceiver? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        registerBroadcastReceiver()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mAudioBeans = intent.getSerializableExtra(DATA_AUDIOS) as ArrayList<AudioBean>
        if (ACTION_START == intent.action) {
            //准备播放
            prepareMusic()
            //初始化前台Notification
            NotificationHelper.instance.init(this)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun prepareMusic() {
        if ( AudioController.instance.queue.size == 0) {
            AudioController.instance.setQueue(mAudioBeans!!, 1)
            AudioController.instance.prepare()
        } else {
            Toast.makeText(this, "执行", Toast.LENGTH_SHORT).show()
            EventBus.getDefault().post(AudioLoadEvent(AudioController.instance.nowPlaying))
        }
    }

    /**
     * 注册广播
     */
    private fun registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = NotificationReceiver()
            val filter = IntentFilter()
            //我们的广播接收器先监听什么样的广播，就在这个添加相应的action
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR)
            //注册动态广播
            registerReceiver(mReceiver, filter)
        }
    }

    private fun unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            //取消注册
            unregisterReceiver(mReceiver)
        }
    }

    override fun onNotificationInit() {
        //service与Notification绑定
        startForeground(NOTIFICATION_ID, NotificationHelper.instance.notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        unRegisterBroadcastReceiver()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioLoadEvent(event: AudioLoadEvent) {
        //更新notification为load状态
        NotificationHelper.instance.showLoadStatus(event.mAudioBean)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPauseEvent(event: AudioPauseEvent) {
        //更新notification为暂停状态
        NotificationHelper.instance.showPauseStatus()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioStartEvent(event: AudioStartEvent) {
        //更新notification为播放状态
        NotificationHelper.instance.showPlayStatus()
    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
    //更新notifacation收藏状态
    //        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
    //    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioReleaseEvent(event: AudioReleaseEvent) {
        //移除notifacation
    }

    /**
     * 接收Notification发送的广播
     */
    class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent == null || TextUtils.isEmpty(intent.action)) {
                return
            }
            val extra = intent.getStringExtra(EXTRA)
            when (extra) {
                EXTRA_PLAY ->
                    //处理播放暂停事件,可以封到AudioController中
                    AudioController.instance.playOrPause()
                EXTRA_PRE -> AudioController.instance.previous() //不管当前状态，直接播放
                EXTRA_NEXT -> AudioController.instance.next()
                EXTRA_FAV -> {
                }
                else -> {
                }
            }//                    AudioController.getInstance().changeFavourite();
        }

        companion object {
            val ACTION_STATUS_BAR = AudioHelper.context!!.getPackageName() + ".NOTIFICATION_ACTIONS"
            val EXTRA = "extra"
            val EXTRA_PLAY = "play_pause"
            val EXTRA_NEXT = "play_next"
            val EXTRA_PRE = "play_previous"
            val EXTRA_FAV = "play_favourite"
        }
    }

    companion object {

        private val DATA_AUDIOS = "AUDIOS"

        private val ACTION_START = "ACTION_START"

        /**
         * 外部直接service方法
         */
        fun startMusicService(audioBeans: ArrayList<AudioBean>) {
            val intent = Intent(AudioHelper.context, MusicService::class.java)
            intent.action = ACTION_START
            //还需要传list数据进来
            intent.putExtra(DATA_AUDIOS, audioBeans)
            AudioHelper.context!!.startService(intent)
        }
    }
}
