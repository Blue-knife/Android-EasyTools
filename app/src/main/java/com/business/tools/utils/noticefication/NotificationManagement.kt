package com.business.tools.utils.noticefication

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlin.random.Random


/**
 * 适用于Android KK-Q 系列的通知快速工具
 */

/**
 * Application，全局维护
 */
object NoticeManagerInit {
    var application: Application? = null
}

/** 是否为Android 0以上 */
val isSdkO by lazy {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
}


/** 通知ID使用 */
val random by lazy {
    Random(50000)
}


/** NoticeManager */
val manager by lazy {
    if (NoticeManagerInit.application == null) {
        throw KotlinNullPointerException(" NoticeManagerInit context Null!")
    }
    NoticeManagerInit.application!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

/**
 *  添加渠道
 *  [Build.VERSION_CODES.O]以上，相比之前的写法，需要新增一个渠道
 *  需要注意的是，此渠道不可通过代码删除，只能添加和判断是否打开
 */
@RequiresApi(Build.VERSION_CODES.O)
fun addNotificationChannel(channelId: String, channelName: String, importance: Int = NotificationManager.IMPORTANCE_HIGH) {
    val channel = NotificationChannel(channelId, channelName, importance)
    //运行显示角标
    channel.setShowBadge(true)
    manager.createNotificationChannel(channel)
}


/**
 * 检查渠道是否可用,不可用时执行相应逻辑
 * [chatId] 传入的渠道ID
 * [context] 可能业务有不同操作，这里留了Context是否填入，如果未传入context，则默认使用Application进行跳转
 * [obj] 函数式，你可以进行一些自己的操作，返回的Boolean决定是否使用默认的跳转，默认会进入设置页
 *
 * 需要注意的是 在Android N-Android O,无需添加 flag 即可使用Application跳转，这是一个有趣的Bug
 * 其他版本默认需要加上[FLAG_ACTIVITY_NEW_TASK] ,即创建一个Activity栈
 */
@RequiresApi(Build.VERSION_CODES.O)
fun isChannelAvailable(chatId: String, obj: () -> Boolean = { true }) {
    val channel = manager.getNotificationChannel(chatId)
    if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
        if (obj()) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    NoticeManagerInit.application?.packageName
            )
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            NoticeManagerInit.application?.startActivity(intent)
        }
    }
}


/** 通知配置相关方法-不全 */
// *   //通知标题
//.setContentTitle("测试")
////通知内容
//.setContentText("撒娇快点哈架空地板噶几快点吧健康的把控那么多不能Sam东南部萨")
////设置未读通知数，即app图标那里显示个数
//.setNumber(3)
////pedIntent
//.setContentIntent(pedIntent)
//.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.test1))
////点击后是否消失
//.setAutoCancel(true)
////设置通知优先级
//.setPriority(NotificationCompat.PRIORITY_MAX)
////通知小图标
//.setSmallIcon(R.mipmap.test1)
////自定义通知View
//.setCustomContentView(remoteViews)
////自定义扩展视图View
//.setCustomBigContentView(remoteViews2)
////设置通知显示时间
//.setWhen(System.currentTimeMillis())

/** 自定义视图相关 */
//  val remoteViews = RemoteViews(packageName, R.layout.notice_layout)
/** 自定义通知栏显示网络图片方法，需要用到 Glide */
//val tag = NotificationTarget(this, 你的布局layout, remoteViews, notification, 通知id)
//            Glide.with(this).asBitmap()
//                .load("https://tva1.sinaimg.cn/large/007S8ZIlly1ge8quxhrysj30dw0dwaaf.jpg")
//                .into(tag)


/**
 * 发送通知
 * [NoticeMessageBean] 为需要的参数，包含了标准通知所需的基本信息
 * [obj] ->  函数当参数，回调 [NotificationCompat.Builder] ,可以在具体调用处实现自己的一些逻辑，默认null实现
 */
fun sendNoticeMessage(
        noticeMessageBean: NoticeMessageBean? = null,
        objO: (NotificationCompat.Builder, Int) -> Notification = { builder, _ ->
            builder.build()
        },
        obj: (Notification.Builder, Int) -> Notification = { builder, _ ->
            builder.build()
        }
) {
    noticeMessageBean?.apply {
        //更多的参数由自己决定如何配置,这里只是配置最基本的参数
        val id = random.nextInt()
        if (isSdkO) {
            val sendNoticeO = sendNoticeO(chanelId!!, title, content, smallIcon)
            manager.notify(id, objO(sendNoticeO, id))
        } else {
            val sendNotice = sendNotice(title, content, smallIcon)

            manager.notify(id, obj(sendNotice, id))
        }
    }
}


/** 通知配置 Android8.0以上 */
private fun sendNoticeO(
        chanelId: String,
        title: String,
        content: String,
        smallIcon: Int
): NotificationCompat.Builder =
        NotificationCompat.Builder(NoticeManagerInit.application!!, chanelId)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)

/** 通知配置 Android8.0以下 */
fun sendNotice(
        title: String,
        content: String,
        smallIcon: Int
): Notification.Builder {
    return Notification.Builder(NoticeManagerInit.application)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(smallIcon)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
}


/** 生成PedIntent */
fun createPedIntent(
        context: Context,
        requestCode: Int,
        cls: Class<*>,
        flags: Int = PendingIntent.FLAG_UPDATE_CURRENT
): PendingIntent = PendingIntent.getActivity(
        context, requestCode
        , Intent(context, cls)
        , flags
)


/** 基本通知信息 */
data class NoticeMessageBean(
        val title: String,
        val content: String,
        val smallIcon: Int,
        var chanelId: String? = null
)

