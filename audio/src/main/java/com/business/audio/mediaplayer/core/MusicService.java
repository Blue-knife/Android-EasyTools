package com.business.audio.mediaplayer.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.business.audio.app.AudioHelper;
import com.business.audio.mediaplayer.event.AudioLoadEvent;
import com.business.audio.mediaplayer.event.AudioPauseEvent;
import com.business.audio.mediaplayer.event.AudioReleaseEvent;
import com.business.audio.mediaplayer.event.AudioStartEvent;
import com.business.audio.mediaplayer.model.AudioBean;
import com.business.audio.mediaplayer.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.business.audio.mediaplayer.view.NotificationHelper.NOTIFICATION_ID;


/**
 * 音乐后台服务,并更新notification状态
 *
 * @author 345
 */
public class MusicService extends Service implements NotificationHelper.NotificationHelperListener {

    private static String DATA_AUDIOS = "AUDIOS";

    private static String ACTION_START = "ACTION_START";

    private ArrayList<AudioBean> mAudioBeans;

    private NotificationReceiver mReceiver;

    /**
     * 外部直接service方法
     */
    public static void startMusicService(ArrayList<AudioBean> audioBeans) {
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        intent.setAction(ACTION_START);
        //还需要传list数据进来
        intent.putExtra(DATA_AUDIOS, audioBeans);
        AudioHelper.getContext().startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioBeans = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
        if (ACTION_START.equals(intent.getAction())) {
            //准备播放
            prepareMusic();
            //初始化前台Notification
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void prepareMusic() {
        if (AudioController.getInstance().getQueue() == null ||
                AudioController.getInstance().getQueue().size() == 0) {
            AudioController.getInstance().setQueue(mAudioBeans, 1);
            AudioController.getInstance().prepare();
        } else {
            Toast.makeText(this, "执行", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new AudioLoadEvent(AudioController.getInstance().getNowPlaying()));
        }
    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
        if (mReceiver == null) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            //我们的广播接收器先监听什么样的广播，就在这个添加相应的action
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            //注册动态广播
            registerReceiver(mReceiver, filter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (mReceiver != null) {
            //取消注册
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onNotificationInit() {
        //service与Notification绑定
        startForeground(NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unRegisterBroadcastReceiver();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新notification为load状态
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新notification为暂停状态
        NotificationHelper.getInstance().showPauseStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新notification为播放状态
        NotificationHelper.getInstance().showPlayStatus();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onAudioFavouriteEvent(AudioFavouriteEvent event) {
    //更新notifacation收藏状态
//        NotificationHelper.getInstance().changeFavouriteStatus(event.isFavourite);
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioReleaseEvent(AudioReleaseEvent event) {
        //移除notifacation
    }

    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String extra = intent.getStringExtra(EXTRA);
            switch (extra) {
                case EXTRA_PLAY:
                    //处理播放暂停事件,可以封到AudioController中
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous(); //不管当前状态，直接播放
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
//                    AudioController.getInstance().changeFavourite();
                    break;
                default:
                    break;
            }
        }
    }
}
