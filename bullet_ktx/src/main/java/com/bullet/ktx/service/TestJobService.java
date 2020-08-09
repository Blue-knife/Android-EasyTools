package com.bullet.ktx.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.service.TestJobService
 * @time 2019/12/30 12:49
 * @description
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TestJobService extends JobService {

    private static final String TAG = "TestJobService";

    public static final int JOB_ID = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    public static void start(Context context) {
        Intent intent = new Intent(context, TestJobService.class);
        context.startService(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo(startId));
        //粘性方式启动 Service
        return START_STICKY;
    }


    @Override
    public boolean onStartJob(final JobParameters params) {
        Toast.makeText(mContext, "Job 任务开始", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟执行任务
                    Thread.sleep(5000);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //任务执行完成
                            //传入 true 表示服务在适当的时候会继续运行
                            Toast.makeText(mContext, "Job 任务结束", Toast.LENGTH_SHORT).show();
                            jobFinished(params, true);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //返回 true 表示需要耗时操作，耗时完成后需要调用 jobFinished
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Toast.makeText(mContext, "JobService：销毁", Toast.LENGTH_SHORT).show();
        //中断任务时调用，例如用户需要服务在充电时才允许，如果在任务开始后  JobFinished 之前拔掉充电器，
        //onStopJob 就会被调用，也就是说，一切任务立即停止。
        //返回 true 表示 任务应该在下次继续，false 表示 事情到此结束，没有下次了。
        return false;
    }

    private void scheduleJob(JobInfo jobInfo) {
        JobScheduler scheduler = null;
        scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            if (scheduler.schedule(jobInfo) <= 0) {
                Log.e(TAG, "scheduleJob: 启动失败");
            } else {
                Log.e(TAG, "scheduleJob: 启动成功");
            }
        }
    }

    private JobInfo getJobInfo(int startId) {
        //代表一个任务
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, TestJobService.class));
        //Android 7.0 以上，设置周期执行时间，会强制按照 getMinperiodMills 阈值执行，
        //此时设置任务执行最小时间间隔解决该问题
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            builder
                    .setPeriodic(3000)
                    .setMinimumLatency(0)
                    //最低延时时间
                    .setOverrideDeadline(2500);
        } else {
            //间隔 15 分钟
            builder.setPeriodic(15 * 60 * 1000);
        }
        builder
//                设置重试策略
                .setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR)
                //需要连接网络
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                //设置持久化
                .setPersisted(true);
        //充电时执行
//                .setRequiresCharging(true);

        return builder.build();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(mContext, "JobService：销毁", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
