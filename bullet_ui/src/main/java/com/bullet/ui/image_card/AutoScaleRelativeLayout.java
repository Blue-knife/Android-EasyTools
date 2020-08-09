package com.bullet.ui.image_card;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bullet.ui.R;


/**
 * author : pixiu
 * qq     : 1326227115
 * date   : 2019/10/29
 * 这是一个锁定宽高比的RelativeLayout
 */

public class AutoScaleRelativeLayout extends RelativeLayout {
    //宽高比例
    private float widthHeightRate = 0.35f;

    public AutoScaleRelativeLayout(Context context) {
        this(context, null);
    }

    public AutoScaleRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScaleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //通过布局获取宽高比例
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.card, 0, 0);
        widthHeightRate = a.getFloat(R.styleable.card_widthHeightRate, widthHeightRate);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 调整高度
        int width = getMeasuredWidth();
        int height = (int) (width * widthHeightRate);
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = height;
        setLayoutParams(lp);
    }
}
