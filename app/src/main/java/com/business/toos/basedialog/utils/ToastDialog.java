package com.business.toos.basedialog.utils;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.business.toos.R;


/**
 * Created by Administrator on 2019/10/9.
 */

public class ToastDialog extends BaseFragDialog {

    private Type mType;
    private String mMessage;
    private AppCompatImageView icon;
    private AppCompatTextView message;
    private long mUptimeMillis;
    private ProgressView progressView;

    public ToastDialog(Object view, float alpha, boolean autoDismiss, boolean cancelable, int animation, int gravity) {
        super(view, alpha, autoDismiss, cancelable, animation, gravity);
    }

    public static DialogBuilder<ToastDialog> ToastBuilder() {
        return new DialogBuilder<>(ToastDialog.class);
    }

    @Override
    public void initView(View view) {
        icon = view.findViewById(R.id.iv_toast_icon);
        message = view.findViewById(R.id.tv_toast_message);
        progressView = view.findViewById(R.id.pw_progress);
        if (mType == null) {
            throw new NullPointerException("没有设置类型，请调用 setType() 设置类型");
        }
        crate();
        //取消背景遮罩
        setBackgroundDimEnabled(false);
    }

    private void crate() {
        icon.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        switch (mType) {
            case FINISH:
                icon.setImageResource(R.drawable.ic_dialog_finish);
                break;
            case ERROR:
                icon.setImageResource(R.drawable.ic_dialog_error);
                break;
            case WARN:
                icon.setImageResource(R.drawable.ic_dialog_warning);
                break;
            case LOADING:
                icon.setVisibility(View.GONE);
                progressView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        if (mMessage == null) {
            message.setVisibility(View.GONE);
        } else {
            message.setVisibility(View.VISIBLE);
            message.setText(mMessage);
        }
        if (mUptimeMillis > 0) {
            postAtTime(mUptimeMillis, new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            });
        }
    }

    /**
     * 设置显示类型
     *
     * @param type 类型
     */
    public ToastDialog setType(Type type) {
        this.mType = type;
        return this;
    }

    /**
     * 设置消息内容
     */
    public ToastDialog setMessage(String message) {
        this.mMessage = message;
        return this;
    }

    /**
     * 延时关闭
     *
     * @param uptimeMillis 时间，毫秒为单位
     */
    public ToastDialog postAtTime(long uptimeMillis) {
        this.mUptimeMillis = uptimeMillis;
        return this;
    }


    /**
     * 显示的类型
     */
    public enum Type {
        // 完成，错误，警告,  加载中
        FINISH, ERROR, WARN, LOADING
    }
}
