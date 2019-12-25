package com.business.tools.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.business.toos.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/10/17.
 */

public class CustomTextView extends AppCompatTextView {

    /**
     * 要显示文字
     */
    private String mText;
    /**
     * 要改变颜色的文字
     */
    private String mTv ;
    /**
     * 文字默认大小 15sp
     */
    private int mTextSize = 15;


    /**
     * 文字默认颜色
     */
    private int mTextColor = Color.BLACK;

    /**
     * 需要改变的颜色
     */
    private int mTvColor = Color.RED;


    /**
     * 画笔
     */
    private TextPaint mPaint;
    private Rect wRect;
    private Rect hRect;

    private int startPos = -1;
    private int endPos = -1;

    private String[] mTvs = null;
    private Integer[] mColors = null;

    /**
     * 这种调用第1个构造方法
     * TextView tv = new TextView(this)：
     */
    public CustomTextView(Context context) {
        this(context, null);
    }

    /**
     * 这种调用第2个构造方法
     * <com.novate.test.customview.MyTextView
     * ......>
     */
    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        // 获取文字
        mText = typedArray.getString(R.styleable.CustomTextView_text);
        //获取设置颜色的文字
        mTv = typedArray.getString(R.styleable.CustomTextView_tv);
        // 获取文字大小
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_textSize, sp2px(mTextSize));
        // 获取文字颜色
        typedArray.getColor(R.styleable.CustomTextView_textColor, mTextColor);
        startPos = typedArray.getInt(R.styleable.CustomTextView_startPosInteger, -1);
        endPos = typedArray.getInt(R.styleable.CustomTextView_endPosInteger, -1);
        mTvColor = typedArray.getInt(R.styleable.CustomTextView_tvColor, Color.RED);
        // 释放资源
        typedArray.recycle();

        // 创建画笔
        mPaint = new TextPaint();
        // 设置抗锯齿，让文字比较清晰，同时文字也会变得圆滑
        mPaint.setAntiAlias(true);
        // 设置文字大小
        mPaint.setTextSize(mTextSize);
        // 设置画笔颜色
        mPaint.setColor(mTextColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        // 如果在布局文件中设置的宽高都是固定值[比如100dp、200dp等]，就用下边方式直接获取宽高
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        // 如果在布局中设置宽高都是wrap_content[对应AT_MOST]，必须用mode计算
        if (widthMode != MeasureSpec.EXACTLY) {
            // 文字宽度 与字体大小和长度有关
            if (wRect == null) {
                wRect = new Rect();
            }
            // 获取文本区域 param1__测量的文字 param2__从位置0开始 param3__到文字长度
            mPaint.getTextBounds(mText, 0, mText.length(), wRect);
            // 文字宽度 getPaddingLeft宽度 getPaddingRight高度 写这两个原因是为了在布局文件中设置padding属性起作用
            width = wRect.width() + getPaddingLeft() + getPaddingRight();
        }


        if (hRect == null) {
            hRect = new Rect();
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            mPaint.getTextBounds(mText, 0, mText.length(), hRect);
            height = hRect.height() + getPaddingTop() + getPaddingBottom();
        }


        // 给文字设置宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 绘制文字
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (mTv != null && !mTv.isEmpty()) {
            int start = mText.indexOf(mTv);
            int end = start + mTv.length();
            startToEnd(canvas, start, end, mTvColor);
        } else if (mTvs != null) {
            if (mColors != null && mColors.length == mTvs.length) {
                sort();
                drawTvs(Arrays.asList(mTvs), Arrays.asList(mColors), canvas);
            } else {
                drawTvs(Arrays.asList(mTvs), canvas);
            }
        } else if (startPos > -1 && endPos > -1) {
            startToEnd(canvas, startPos, endPos, mTvColor);
        } else if (startPos > -1) {
            startToEnd(canvas, startPos, startPos + 1, mTvColor);
        } else {
            int baseLine = getBaseLine();
            mPaint.setColor(mTextColor);
            canvas.drawText(mText, getPaddingLeft(), baseLine, mPaint);
        }
    }


    /**
     * 指定位置的文字变为指定的颜色
     *
     * @param canvas   画布
     * @param startPos 开始的位置
     * @param endPos   结束的位置
     * @param colorPos 指定的颜色
     */
    private void startToEnd(Canvas canvas, int startPos, int endPos, int colorPos) {
        if (startPos <= -1 && endPos <= -1) {
            throw new IndexOutOfBoundsException("索引异常，检查需要修改的文字是否设置正确");
        }
        int baseLine = getBaseLine();
        int paddingLeft = getPaddingLeft();

        String start = mText.substring(0, startPos);
        String red = mText.substring(startPos, endPos);
        String end = mText.substring(endPos, mText.length());
        canvas.drawText(start, paddingLeft, baseLine, mPaint);

        mPaint.setColor(colorPos);
        float redLength = Layout.getDesiredWidth(start, mPaint) + paddingLeft;
        canvas.drawText(red, redLength, baseLine, mPaint);

        mPaint.setColor(mTextColor);
        float endLength = Layout.getDesiredWidth(red, mPaint) + Layout.getDesiredWidth(start, mPaint) + paddingLeft;
        canvas.drawText(end, endLength, baseLine, mPaint);
    }

    private void drawTvs(List<String> tvs, Canvas canvas) {
        mColors = new Integer[mTvs.length];
        for (int i = 0; i < mColors.length; i++) {
            mColors[i] = mTvColor;
        }
        drawTvs(tvs, Arrays.asList(mColors), canvas);
    }

    private void drawTvs(List<String> tvs, List<Integer> colors, Canvas canvas) {
        int baseLine = getBaseLine();
        int paddingLeft = getPaddingLeft();
        final float tvWidth = getWidth() - getPaddingLeft() - getPaddingRight(); //控件可用宽度
        for (int i = 0; i < tvs.size(); i++) {
            int start = mText.indexOf(tvs.get(i));
            int end = start + tvs.get(i).length();
            String startPos = mText.substring(0, start);
            String redPos = mText.substring(start, end);
            String endPos;

            if (tvs.size() > i + 1) {
                endPos = mText.substring(end, mText.indexOf(tvs.get(i + 1)));
            } else {
                endPos = mText.substring(end, mText.length());
            }
            if (i == 0) {
                canvas.drawText(startPos, paddingLeft, baseLine, mPaint);
            }
            if (mColors != null && colors.size() > i) {
                mPaint.setColor(colors.get(i));
            } else {
                mPaint.setColor(mTvColor);
            }
            float redLength = Layout.getDesiredWidth(startPos, mPaint) + paddingLeft;
            canvas.drawText(redPos, redLength, baseLine, mPaint);

            mPaint.setColor(mTextColor);
            float endLength = Layout.getDesiredWidth(redPos, mPaint) + Layout.getDesiredWidth(startPos, mPaint) + paddingLeft;
            canvas.drawText(endPos, endLength, baseLine, mPaint);
        }
    }

    /**
     * 开始的位置，可单独使用
     *
     * @param startPos 开始的位置，未设置结束位置则结束位置为 startPos + 1
     */
    public void startPos(int startPos) {
        this.startPos = startPos;
    }

    /**
     * 结束位置
     *
     * @param endPos 结束位置，必须大于开始位置
     */
    public void endPos(int endPos) {
        this.endPos = endPos;
    }

    /**
     * 设置要改变颜色的文字，和设置 位置 选择其一即可
     *
     * @param colorText 改变颜色的文字
     */
    public void setColorTv(String colorText) {
        this.mTv = colorText;
    }

    /**
     * 多个地方的颜色需要修改可以使用此方法
     *
     * @param tvs 需要修改的文字数组
     */
    public void setTvs(String[] tvs) {
        this.mTvs = tvs;
    }

    public void setTvs(String[] tvs, Integer[] colors) {
        this.mTvs = tvs;
        this.mColors = colors;
    }

    /**
     * 设置普通文字的颜色
     *
     * @param color 颜色
     */
    public void setTextColor(@ColorInt int color) {
        this.mTextColor = color;
    }

    /**
     * 设置要文字需要改变的颜色
     *
     * @param color
     */
    public void setTvColor(@ColorInt int color) {
        this.mTvColor = color;
    }

    /**
     * 设置 text
     *
     * @param mText text
     */
    public void setText(String mText) {
        this.mText = mText;
    }

    public void setTv(String changeTxt) {
        this.mTv = changeTxt;
    }

    /**
     * 设置默认文本颜色
     *
     * @param mTextSize size
     */
    public void setTestSize(int mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void notifyTv() {
        invalidate();
    }


    /**
     * 获取基线
     */
    private int getBaseLine() {
        Paint.FontMetricsInt fontMetricsInt = mPaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        return getHeight() / 2 + dy;
    }

    /**
     * sp转为px
     */
    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private void sort() {
        for (int i = 0; i < mTvs.length; i++) {
            for (int j = i + 1; j < mTvs.length; j++) {
                if (mText.indexOf(mTvs[i]) > mText.indexOf(mTvs[j])) {
                    String text = mTvs[i];
                    mTvs[i] = mTvs[j];
                    mTvs[j] = text;
                    int color = mColors[i];
                    mColors[i] = mColors[j];
                    mColors[j] = color;
                }
            }
        }
    }

    /**
     * 获取textview一行最大能显示几个字(需要在TextView测量完成之后)
     *
     * @param text     文本内容
     * @param paint    textview.getPaint()
     * @param maxWidth textview.getMaxWidth()/或者是指定的数值,如200dp
     */
    private int getLineMaxNumber(String text, TextPaint paint, int maxWidth) {
        if (null == text || "".equals(text)) {
            return 0;
        }
        StaticLayout staticLayout = new StaticLayout(text, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL
                , 1.0f, 0, false);
        //获取第一行最后显示的字符下标
        return staticLayout.getLineEnd(0);
    }


}
