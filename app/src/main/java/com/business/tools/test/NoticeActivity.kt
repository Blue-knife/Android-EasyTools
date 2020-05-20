package com.business.tools.test

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.example.core.ToastUtils
import com.business.tools.utils.noticefication.*
import com.business.toos.R
import kotlinx.android.synthetic.main.activity_notice.*

/**
 * @Author petterp
 * @Date 2020/4/29-1:07 AM
 * @Email ShiyihuiCloud@163.com
 * @Function
 */
class NoticeActivity : AppCompatActivity(R.layout.activity_notice), View.OnClickListener {

    val channelId = "test"
    val channelName = "test"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NoticeManagerInit.application = application
        btnAddChannel.setOnClickListener(this)
        btnIsChannels.setOnClickListener(this)
        btnSendNotice.setOnClickListener(this)
        btnSendCustomNotice.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAddChannel -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    addNotificationChannel(channelId, channelName)
                }
            }
            R.id.btnIsChannels -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //判断渠道是否打开，未打开执行相应逻辑，默认执行打开设置
                    isChannelAvailable(channelId) {
                        com.example.core.ToastUtils.showCenterText("渠道未打开，执行默认操作")
                        true
                    }
                }
            }
            R.id.btnSendNotice -> {
                //发送基本通知
                sendNoticeMessage(
                        NoticeMessageBean("这是一条测试通知", "我是测试1", R.mipmap.icon, channelId))
            }
            R.id.btnSendCustomNotice -> {
                val noticeMessageBean = NoticeMessageBean("这是一条测试通知", "我是测试1", R.mipmap.icon, channelId)
                val remoteViews = RemoteViews(packageName, R.layout.notice_layout)
                remoteViews.setTextViewText(R.id.tvContent, noticeMessageBean.content)
                sendNoticeMessage(
                        noticeMessageBean,
                        { noticeO, id ->
                            //这里可以借用[noticeO] 做一些额外的操作
                            //【id】 为通知id,万一需要呢，比如自定义通知视图等等
                            noticeO.setCustomContentView(remoteViews)
                            val tag = NotificationTarget(this, R.id.ivIcon, remoteViews, noticeO.build(), id)
                            Glide.with(this).asBitmap()
                                    .load("https://tva1.sinaimg.cn/large/007S8ZIlly1ge8quxhrysj30dw0dwaaf.jpg")
                                    .into(tag)
                            noticeO.build()
                        },
                        { notice, id ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                notice.setCustomContentView(remoteViews)
                                val tag = NotificationTarget(this, R.id.ivIcon, remoteViews, notice.build(), id)
                                Glide.with(this).asBitmap()
                                        .load("https://tva1.sinaimg.cn/large/007S8ZIlly1ge8quxhrysj30dw0dwaaf.jpg")
                                        .into(tag)
                            }
                            notice.build()
                        })
            }
        }
    }
}