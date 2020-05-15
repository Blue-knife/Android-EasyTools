package com.business.audio.mediaplayer.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

import com.business.audio.R
import com.business.audio.mediaplayer.event.AudioLoadEvent
import com.business.audio.mediaplayer.event.AudioPauseEvent
import com.business.audio.mediaplayer.event.AudioPrepareEvent
import com.business.audio.mediaplayer.event.AudioStartEvent
import com.business.audio.mediaplayer.model.AudioBean
import com.business.audio.mediaplayer.utils.ImageLoaderManager

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author 345 QQ:1831712732
 * @name TTMusic
 * @class name：com.car.lib_audio.mediaplayer.view
 * @time 2019/12/23 22:31
 * @description
 */
class BottomMusicView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(mContext, attrs, defStyleAttr) {

    /*
     * View
     */
    private val mLeftView: AppCompatImageView? = null
    private val mTitleView: AppCompatTextView? = null
    private val mAlbumView: AppCompatTextView? = null
    private val mPlayView: AppCompatImageView? = null
    private val mRightView: AppCompatImageView? = null
    /*
     * data
     */
    private var mAudioBean: AudioBean? = null

    init {
        EventBus.getDefault().register(this)
        initView()
    }

    private fun initView() {
        //        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bottom_view, this);
        //        rootView.setOnClickListener(new OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                MusicPlayerActivity.start((Activity) mContext);
        //            }
        //        });
        //        mLeftView = rootView.findViewById(R.id.album_view);
        val animator = ObjectAnimator.ofFloat(mLeftView, View.ROTATION.name, 0f, 360f)
        animator.setDuration(10000)
        animator.setInterpolator(LinearInterpolator())
        animator.setRepeatCount(-1)
        animator.start()

        //        mTitleView = rootView.findViewById(R.id.audio_name_view);
        //        mAlbumView = rootView.findViewById(R.id.audio_album_view);
        //        mPlayView = rootView.findViewById(R.id.play_view);
        mPlayView!!.setOnClickListener {
            //                AudioController.getInstance().playOrPause();
        }
        //        mRightView = rootView.findViewById(R.id.show_list_view);
        mRightView!!.setOnClickListener { }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPrepareEvent(event: AudioPrepareEvent) {
        mAudioBean = event.audioBean
        ImageLoaderManager.instance.displayImageForCircle(mLeftView!!, mAudioBean!!.albumPic)
        mTitleView!!.text = mAudioBean!!.name
        mAlbumView!!.text = mAudioBean!!.album
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioLoadEvent(event: AudioLoadEvent) {
        //监听加载事件
        mAudioBean = event.mAudioBean
        showLoadingView()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioStartEvent(event: AudioStartEvent) {
        showPlayView()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPauseEvent(event: AudioPauseEvent) {
        showPauseView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private fun showLoadingView() {
        //目前loading状态的UI处理与pause逻辑一样，分开为了以后好扩展
        if (mAudioBean != null) {
            ImageLoaderManager.instance.displayImageForCircle(mLeftView!!, mAudioBean!!.albumPic)
            mTitleView!!.text = mAudioBean!!.name
            mAlbumView!!.text = mAudioBean!!.album
            mPlayView!!.setImageResource(R.mipmap.note_btn_pause_white)
        }
    }

    private fun showPlayView() {
        mPlayView!!.setImageResource(R.mipmap.note_btn_pause_white)

    }

    private fun showPauseView() {
        mPlayView!!.setImageResource(R.mipmap.note_btn_play_white)

    }
}
