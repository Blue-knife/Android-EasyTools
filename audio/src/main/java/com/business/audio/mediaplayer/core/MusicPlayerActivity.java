package com.business.audio.mediaplayer.core;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;

import com.business.audio.R;
import com.business.audio.app.AudioHelper;
import com.business.audio.mediaplayer.event.AudioLoadEvent;
import com.business.audio.mediaplayer.event.AudioPauseEvent;
import com.business.audio.mediaplayer.event.AudioPrepareEvent;
import com.business.audio.mediaplayer.event.AudioStartEvent;
import com.business.audio.mediaplayer.model.AudioBean;
import com.business.audio.mediaplayer.utils.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.core
 * @time 2019/12/25 23:18
 * @description
 */
public class MusicPlayerActivity extends AppCompatActivity {

    private AudioBean mAudioBean;
    private PlayMode mPlayMode = PlayMode.LOOP;
    private RelativeLayout mBgView;
    private AppCompatTextView mInfoView;
    private AppCompatTextView mAuthorView;
    private AppCompatTextView mStartTimeView;
    private AppCompatTextView mTotalTimeView;
    private AppCompatSeekBar mProgressView;
    private AppCompatImageView mPreViousView;
    private AppCompatImageView mPlayModeView;
    private ArrayList<AudioBean> mLists = new ArrayList<>();
    private ImageView mPlayView;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MusicPlayerActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noActionBar();
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_service_layout);
        AudioHelper.startMusicService(getData());
        initView();
    }

    private void noActionBar() {
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }
        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private void initView() {
        mBgView = findViewById(R.id.root_layout);
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mInfoView = findViewById(R.id.album_view);
        mInfoView.requestFocus();//跑马灯效果，获取焦点

        mAuthorView = findViewById(R.id.author_view);


        mStartTimeView = findViewById(R.id.start_time_view);
        mTotalTimeView = findViewById(R.id.total_time_view);
        mProgressView = findViewById(R.id.progress_view);
        mProgressView.setProgress(0);
        mProgressView.setEnabled(false);


        mPlayModeView = findViewById(R.id.play_mode_view);
        mPlayModeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        upDataPlayModeView();

        mPreViousView = findViewById(R.id.previous_view);
        mPreViousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上一首
                AudioController.getInstance().previous();
            }
        });

        mPlayView = findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().playOrPause();
            }
        });

        ImageView mNextView = findViewById(R.id.next_view);
        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioController.getInstance().next();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPrepareEvent(AudioPrepareEvent event) {
        mAudioBean = event.audioBean;
        upData();
    }


    private void upDataPlayModeView() {
        switch (mPlayMode) {
            case LOOP:
                mPlayModeView.setImageResource(R.mipmap.player_loop);
                break;
            case REPEAT:
                mPlayModeView.setImageResource(R.mipmap.player_random);
                break;
            case RANDOM:
                mPlayModeView.setImageResource(R.mipmap.player_once);
                break;
            default:
                break;
        }
    }

    /**
     * 暂停事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        mPlayView.setImageDrawable(getResources().getDrawable(R.mipmap.audio_aj7));

    }

    /**
     * 开始播放
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        mPlayView.setImageDrawable(getResources().getDrawable(R.mipmap.audio_aj6));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        mAudioBean = event.mAudioBean;
        upData();
    }

    private void upData() {
        ImageLoaderManager.getInstance().displayImageForViewGroup(mBgView, mAudioBean.albumPic);
        mInfoView.setText(mAudioBean.albumInfo);
        mAuthorView.setText(mAudioBean.author);
        mTotalTimeView.setText(mAudioBean.totalTime);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private ArrayList<AudioBean> getData() {
        mLists.add(new AudioBean("100001", "http://sp-sycdn.kuwo.cn/resource/n2/85/58/433900159.mp3",
                "以你的名字喊我", "周杰伦", "七里香", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698076304&di=e6e99aa943b72ef57b97f0be3e0d2446&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201401%2F04%2F20140104170315_XdG38.jpeg",
                "4:30"));
        mLists.add(
                new AudioBean("100002", "http://sq-sycdn.kuwo.cn/resource/n1/98/51/3777061809.mp3", "勇气",
                        "汪峰", "灿烂的你", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698193627&di=711751f16fefddbf4cbf71da7d8e6d66&imgtype=jpg&src=http%3A%2F%2Fimg0.imgtn.bdimg.com%2Fit%2Fu%3D213168965%2C1040740194%26fm%3D214%26gp%3D0.jpg",
                        "4:40"));
        mLists.add(
                new AudioBean("100003", "http://sp-sycdn.kuwo.cn/resource/n2/52/80/2933081485.mp3", "灿烂如你",
                        "汪峰", "春天里", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698239736&di=3433a1d95c589e31a36dd7b4c176d13a&imgtype=0&src=http%3A%2F%2Fpic.zdface.com%2Fupload%2F201051814737725.jpg",
                        "3:20"));
        mLists.add(
                new AudioBean("100004", "http://sr-sycdn.kuwo.cn/resource/n2/33/25/2629654819.mp3", "小情歌",
                        "五月天", "小幸运", "电影《不能说的秘密》主题曲,尤其以最美的不是下雨天,是与你一起躲过雨的屋檐最为经典",
                        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1559698289780&di=5146d48002250bf38acfb4c9b4bb6e4e&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fp%2F20131220%2Fbki-20131220170401-1254350944.jpg",
                        "2:45"));
        return mLists;
    }

}
