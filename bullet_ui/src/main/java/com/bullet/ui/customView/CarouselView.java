package com.bullet.ui.customView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bullet.ui.R;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author 345 QQ:1831712732
 * @name CarSteward
 * @class name：com.car.core.ui.view
 * @time 2019/11/12 21:46
 * @description 轮播 View
 * LifecycleObserver ：生命周期观察者，对 activity 的生命周期进行观察
 * 注意需要绑定生命周期：lifecycle.addObserver(CarouselView)
 */
public class CarouselView extends ViewSwitcher implements LifecycleObserver {

    private int mCutItem;
    /**
     * 循环时间
     */
    private int loopTime;
    private MyHandler myHandler;
    private ArrayList<String> listString;
    private boolean isStart = false;


    public CarouselView(Context context) {
        this(context, null);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
        initAnimation();
    }

    /**
     * 初始化一些变量
     */
    private void initData() {
        listString = new ArrayList<>();
        myHandler = new MyHandler(this);
    }

    /**
     * 初始化进入和出去动画
     */
    private void initAnimation() {
        setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_in));
        setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_out));
    }

    /**
     * 设置数据源并展示view，外部调用
     *
     * @param mList
     * @param time
     */
    public void upDataListAndView(ArrayList mList, int time) {
        mCutItem = -1;
        loopTime = time;
        if (null == mList) {
            return;
        }
        listString.clear();
        listString.addAll(mList);
        showNextView();
    }

    /**
     * 展示下一条广告
     */
    public void showNextView() {
        if (null == listString || listString.size() < 2) {
            return;
        }
        mCutItem = mCutItem == listString.size() - 1 ? 0 : mCutItem + 1;
        updataView(String.valueOf(listString.get(mCutItem)), getNextView(), mCutItem);
        showNext();
    }

    /**
     * 启动轮播
     */
    public void startLooping() {
        if (null == listString || listString.size() < 2) {
            return;
        }
        myHandler.removeMessages(0);
        if (loopTime > 0) {
            myHandler.sendEmptyMessageDelayed(0, loopTime);
        }
    }

    /**
     * 停止轮播
     */
    public void stopLooping() {
        myHandler.removeMessages(0);
    }

    /**
     * 在当前view上设置数据
     *
     * @param text
     * @param view
     */
    private void updataView(String text, View view, final int mCutItem) {
        AppCompatTextView textView = (AppCompatTextView) view;
        textView.setText(text);
        textView.setOnClickListener(v -> {
            if (null != onClickItemListener) {
                onClickItemListener.onClick(mCutItem);
            }
        });
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        stopLooping();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void OnResume() {
        if (isStart) {
            startLooping();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        myHandler.removeCallbacksAndMessages(0);
        myHandler = null;
    }


    /**
     * @description 主线程Handler
     * @note 因为存在定时任务，并且TextSwitcherView持有Activity的引用
     * 所以这里采用弱引用，主要针对内存回收的时候Activity泄露
     **/
    private static class MyHandler extends Handler {

        private WeakReference<CarouselView> mRef;

        public MyHandler(CarouselView view) {
            mRef = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            CarouselView mView = (CarouselView) this.mRef.get();
            if (mView != null) {
                mView.showNextView();//展示下一条广告，会调用shownext方法展示下一条广告
                mView.startLooping();//启动轮播，间隔后展示下一条
            } else {
                //如果 mView 为空，表示当前 mView 被回收，清除 handler中的所有任务
                removeCallbacksAndMessages(null);
            }
        }
    }

    OnClickItemListener onClickItemListener;

    /**
     * 定义一个接口回调
     */
    public interface OnClickItemListener {
        /**
         * 回调方法
         *
         * @param position 位置
         */
        void onClick(int position);
    }

    /**
     * 接口
     *
     * @param onClickListener
     */
    public void setOnItemClickListener(OnClickItemListener onClickListener) {
        this.onClickItemListener = onClickListener;
    }
}
