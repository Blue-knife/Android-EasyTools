package com.business.tools.basedialog.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.business.toos.R;

/**
 * Created by Petterp
 * on 2019-12-16
 * Function:自定义图片旋转dialog
 */
public class CustomSpinDialog extends BaseFragDialog {

    private int width = 200;
    private int height = 200;
    private int time = 2000;
    private int imgResource= R.drawable.ic_dialog_spin;

    public CustomSpinDialog(Object view, float alpha, boolean autoDismiss, boolean cancelable, int animation, int gravity) {
        super(view, alpha, autoDismiss, cancelable, animation, gravity);
    }

    public static DialogBuilder<CustomSpinDialog> CustomSpinBuilder() {
        return new DialogBuilder<>(CustomSpinDialog.class);
    }

    public CustomSpinDialog setWidth(int width) {
        this.width = width;
        return this;
    }

    public CustomSpinDialog setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public void initView(View view) {
        ImageView imageView = view.findViewById(R.id.img_dialog_spin);
        imageView.setImageResource(imgResource);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(time);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        //取消背景遮罩
        setBackgroundDimEnabled(false);
    }

    public CustomSpinDialog setDuration(int time) {
        this.time = time;
        return this;
    }

    public CustomSpinDialog setResouce(int resouce) {
        this.imgResource = resouce;
        return this;
    }

    @Override
    protected void setWindow(Window window) {
        super.setWindow(window);
        window.getAttributes().width = width;
        window.getAttributes().height = height;
    }
}
