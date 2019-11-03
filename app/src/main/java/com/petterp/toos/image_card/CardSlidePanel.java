/*
package com.petterp.toos.image_card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;


import com.petterp.toos.R;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
*/
/**
 * author : pixiu
 * qq     : 1326227115
 * date   : 2019/10/29
 * 卡片滑动面板，主要逻辑实现类
 *//*

@SuppressLint({"HandlerLeak", "NewApi", "ClickableViewAccessibility"})
public class CardSlidePanel extends ViewGroup {
    private List<CardItemView> viewList = new ArrayList<CardItemView>(); // 存放的是每一层的view，从顶到底
    private List<View> releasedViewList = new ArrayList<View>(); // 手指松开后存放的view列表

    */
/* 拖拽工具类 *//*

    private final ViewDragHelper mDragHelper; // 这个跟原生的ViewDragHelper差不多，修改了Interpolator
    private int initCenterViewX = 0, initCenterViewY = 0; // 最初时，中间View的x位置,y位置
    private int allWidth = 0; // 面板的宽度
    private int allHeight = 0; // 面板的高度
    private int childWith = 0; // 每一个子View对应的宽度

    private static final float SCALE_STEP = 0.08f; // view叠加缩放的步长
    private static final int MAX_SLIDE_DISTANCE_LINKAGE = 500; // 水平距离+垂直距离

    // 超过这个值
    // 则下一层view完成向上一层view的过渡
    private View bottomLayout; // 卡片下边的三个按钮布局

    private int itemMarginTop = 10; // 卡片距离顶部的偏移量
    private int bottomMarginTop = 40; // 底部按钮与卡片的margin值
    private int yOffsetStep = 40; // view叠加垂直偏移量的步长
    private int mTouchSlop = 5; // 判定为滑动的阈值，单位是像素

    private static final int X_VEL_THRESHOLD = 800;
    private static final int X_DISTANCE_THRESHOLD = 300;

    public static final int VANISH_TYPE_LEFT = 0;
    public static final int VANISH_TYPE_RIGHT = 1;

    private CardSwitchListener cardSwitchListener; // 回调接口
    private List<CardDataItem> dataList; // 存储的数据链表
    private int isShowing = 0; // 当前正在显示的小项
    private View leftBtn, rightBtn;
    private boolean btnLock = false;
    private GestureDetectorCompat moveDetector;
    private OnClickListener btnListener;
    private Point downPoint = new Point();

    public CardSlidePanel(Context context) {
        this(context, null);
    }

    public CardSlidePanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardSlidePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.card);

        itemMarginTop = (int) a.getDimension(R.styleable.card_itemMarginTop, itemMarginTop);
        bottomMarginTop = (int) a.getDimension(R.styleable.card_bottomMarginTop, bottomMarginTop);
        yOffsetStep = (int) a.getDimension(R.styleable.card_yOffsetStep, yOffsetStep);
        // 滑动相关类
        mDragHelper = ViewDragHelper
                .create(this, 10f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
        a.recycle();
        btnListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.maskView) {
                    // 点击的是卡片
                    if (null != cardSwitchListener && view.getScaleX() > 1 - SCALE_STEP) {
                        cardSwitchListener.onItemClick(view, isShowing);
                    }
                } else {
                    // 点击的是bottomLayout里面的一些按钮
                    btnLock = true;
                    int type = -1;
                    if (view == leftBtn) {
                        type = VANISH_TYPE_LEFT;
                    } else if (view == rightBtn) {
                        type = VANISH_TYPE_RIGHT;
                    }
                    vanishOnBtnClick(type);
                }
            }
        };

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        moveDetector = new GestureDetectorCompat(context,
                new MoveDetector());
        moveDetector.setIsLongpressEnabled(false);
    }
    //onFinishInflate 当View中所有的子控件均被映射成xml后触发
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 渲染完成，初始化卡片view列表
        viewList.clear();
        int num = getChildCount();
        for (int i = num - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            if (childView.getId() == R.id.card_bottom_layout) {
                bottomLayout = childView;
                initBottomLayout();
            } else {
                // for循环取view的时候，是从外层往里取
                CardItemView viewItem = (CardItemView) childView;
                viewItem.setParentView(this);
                viewItem.setTag(i + 1);
                viewItem.maskView.setOnClickListener(btnListener);
                viewList.add(viewItem);
            }
        }

        CardItemView bottomCardView = viewList.get(viewList.size() - 1);
        bottomCardView.setAlpha(0);
    }

    private void initBottomLayout() {
        leftBtn = bottomLayout.findViewById(R.id.card_left_btn);
        rightBtn = bottomLayout.findViewById(R.id.card_right_btn);
        leftBtn.setOnClickListener(btnListener);
        rightBtn.setOnClickListener(btnListener);
    }

    class MoveDetector extends SimpleOnGestureListener {
        //在屏幕上拖动事件
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            // 拖动了，touch不往下传递
            return Math.abs(dy) + Math.abs(dx) > mTouchSlop;
        }
    }


    */
/**
     * 这是viewdraghelper拖拽效果的主要逻辑
     *//*

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            onViewPosChanged((CardItemView) changedView);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 如果数据List为空，或者子View不可见，则不予处理
            if (child == bottomLayout || dataList == null || dataList.size() == 0
                    || child.getVisibility() != View.VISIBLE || child.getScaleX() <= 1.0f - SCALE_STEP) {
                // 一般来讲，如果拖动的是第三层、或者第四层的View，则直接禁止
                // 此处用getScale的用法来巧妙回避
                return false;
            }
            if (btnLock) {
                return false;
            }
            // 只捕获顶部view(rotation=0)
            int childIndex = viewList.indexOf(child);
            if (childIndex > 0) {
                return false;
            }
            ((CardItemView) child).onStartDragging();
            boolean shouldCapture = ((CardItemView) child).shouldCapture(downPoint.x, downPoint.y);
            if (shouldCapture) {
                getParent().requestDisallowInterceptTouchEvent(shouldCapture);
            }
            return shouldCapture;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            // 这个用来控制拖拽过程中松手后，自动滑行的速度
            return 256;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            animToSide((CardItemView) releasedChild, (int) xvel, (int) yvel);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }


    public void onViewPosChanged(CardItemView changedView) {
        // 调用offsetLeftAndRight导致viewPosition改变，会调到此处，所以此处对index做保护处理
        int index = viewList.indexOf(changedView);
        if (index + 2 > viewList.size()) {
            return;
        }

        processLinkageView(changedView);
    }

    */
/**
     * 对View重新排序
     *//*

    private void orderViewStack() {
        if (releasedViewList.size() == 0) {
            return;
        }

        CardItemView changedView = (CardItemView) releasedViewList.get(0);
        if (changedView.getLeft() == initCenterViewX) {
            releasedViewList.remove(0);
            return;
        }
        // 1. 消失的卡片View位置重置，由于大多手机会重新调用onLayout函数，所以此处大可以不做处理
        changedView.offsetLeftAndRight(initCenterViewX
                - changedView.getLeft());
        changedView.offsetTopAndBottom(initCenterViewY
                - changedView.getTop() + yOffsetStep * 2);
        float scale = 1.0f - SCALE_STEP * 2;
        changedView.setScaleX(scale);
        changedView.setScaleY(scale);
        changedView.setAlpha(0);

        // 2. 卡片View在ViewGroup中的顺次调整
        int num = viewList.size();
        for (int i = num - 1; i > 0; i--) {
            CardItemView tempView = viewList.get(i);
            tempView.setAlpha(1);
            tempView.bringToFront();
        }

        // 3. changedView填充新数据
        int newIndex = isShowing + 4;
        if (newIndex < dataList.size()) {
            CardDataItem dataItem = dataList.get(newIndex);
            changedView.fillData(dataItem);
        } else {
            changedView.setVisibility(View.INVISIBLE);
        }

        // 4. viewList中的卡片view的位次调整
        viewList.remove(changedView);
        viewList.add(changedView);
        releasedViewList.remove(0);

        // 5. 更新showIndex、接口回调
        if (isShowing + 1 < dataList.size()) {
            isShowing++;
        }
        if (null != cardSwitchListener) {
            cardSwitchListener.onShow(isShowing);
        }
    }

    */
/**
     * 顶层卡片View位置改变，底层的位置需要调整
     *
     * @param changedView 顶层的卡片view
     *//*

    private void processLinkageView(View changedView) {
        int changeViewLeft = changedView.getLeft();
        int changeViewTop = changedView.getTop();
        int distance = Math.abs(changeViewTop - initCenterViewY)
                + Math.abs(changeViewLeft - initCenterViewX);
        float rate = distance / (float) MAX_SLIDE_DISTANCE_LINKAGE;

        float rate1 = rate;
        float rate2 = rate - 0.1f;

        if (rate > 1) {
            rate1 = 1;
        }

        if (rate2 < 0) {
            rate2 = 0;
        } else if (rate2 > 1) {
            rate2 = 1;
        }

        ajustLinkageViewItem(changedView, rate1, 1);
        ajustLinkageViewItem(changedView, rate2, 2);

        CardItemView bottomCardView = viewList.get(viewList.size() - 1);
        bottomCardView.setAlpha(rate2);
    }

    // 由index对应view变成index-1对应的view
    private void ajustLinkageViewItem(View changedView, float rate, int index) {
        int changeIndex = viewList.indexOf(changedView);
        int initPosY = yOffsetStep * index;
        float initScale = 1 - SCALE_STEP * index;

        int nextPosY = yOffsetStep * (index - 1);
        float nextScale = 1 - SCALE_STEP * (index - 1);

        int offset = (int) (initPosY + (nextPosY - initPosY) * rate);
        float scale = initScale + (nextScale - initScale) * rate;

        View ajustView = viewList.get(changeIndex + index);
        ajustView.offsetTopAndBottom(offset - ajustView.getTop()
                + initCenterViewY);
        ajustView.setScaleX(scale);
        ajustView.setScaleY(scale);
    }

    */
/**
     * 松手时处理滑动到边缘的动画
     *//*

    private void animToSide(CardItemView changedView, int xvel, int yvel) {
        int finalX = initCenterViewX;
        int finalY = initCenterViewY;
        int flyType = -1;

        // 1. 下面这一坨计算finalX和finalY，要读懂代码需要建立一个比较清晰的数学模型才能理解，不信拉倒
        int dx = changedView.getLeft() - initCenterViewX;
        int dy = changedView.getTop() - initCenterViewY;

        // yvel < xvel * xyRate则允许以速度计算偏移
        final float xyRate = 3f;
        if (xvel > X_VEL_THRESHOLD && Math.abs(yvel) < xvel * xyRate) {
            // x正方向的速度足够大，向右滑动消失
            finalX = allWidth;
            finalY = yvel * (childWith + changedView.getLeft()) / xvel + changedView.getTop();
            flyType = VANISH_TYPE_RIGHT;
        } else if (xvel < -X_VEL_THRESHOLD && Math.abs(yvel) < -xvel * xyRate) {
            // x负方向的速度足够大，向左滑动消失
            finalX = -childWith;
            finalY = yvel * (childWith + changedView.getLeft()) / (-xvel) + changedView.getTop();
            flyType = VANISH_TYPE_LEFT;
        } else if (dx > X_DISTANCE_THRESHOLD && Math.abs(dy) < dx * xyRate) {
            // x正方向的位移足够大，向右滑动消失
            finalX = allWidth;
            finalY = dy * (childWith + initCenterViewX) / dx + initCenterViewY;
            flyType = VANISH_TYPE_RIGHT;
        } else if (dx < -X_DISTANCE_THRESHOLD && Math.abs(dy) < -dx * xyRate) {
            // x负方向的位移足够大，向左滑动消失
            finalX = -childWith;
            finalY = dy * (childWith + initCenterViewX) / (-dx) + initCenterViewY;
            flyType = VANISH_TYPE_LEFT;
        }
        // 如果斜率太高，就折中处理
        if (finalY > allHeight) {
            finalY = allHeight;
        } else if (finalY < -allHeight / 2) {
            finalY = -allHeight / 2;
        }
        // 如果没有飞向两侧，而是回到了中间，需要谨慎处理
        if (finalX == initCenterViewX) {
            changedView.animTo(initCenterViewX, initCenterViewY);
        } else {
            // 2. 向两边消失的动画
            releasedViewList.add(changedView);
            if (mDragHelper.smoothSlideViewTo(changedView, finalX, finalY)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

            // 3. 消失动画即将进行，listener回调
            if (flyType >= 0 && cardSwitchListener != null) {
                cardSwitchListener.onCardVanish(isShowing, flyType);
            }
        }
    }

    */
/**
     * 点击按钮消失动画
     *//*

    private void vanishOnBtnClick(int type) {
        View animateView = viewList.get(0);
        if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
            return;
        }

        int finalX = 0;
        int extraVanishDistance = 100; // 为加快vanish的速度，额外添加消失的距离
        if (type == VANISH_TYPE_LEFT) {
            finalX = -childWith - extraVanishDistance;
        } else if (type == VANISH_TYPE_RIGHT) {
            finalX = allWidth + extraVanishDistance;
        }

        if (finalX != 0) {
            releasedViewList.add(animateView);
            if (mDragHelper.smoothSlideViewTo(animateView, finalX, initCenterViewY + allHeight / 2)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        if (type >= 0 && cardSwitchListener != null) {
            cardSwitchListener.onCardVanish(isShowing, type);
        }

    }
    //计算拖动的位移量、更新背景、设置要显示的屏幕(setCurrentScreen(mCurrentScreen);)。
    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // 动画结束
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                orderViewStack();
                btnLock = false;
            }
        }
    }
    //事件处理
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        // 按下时保存坐标信息
        if (action == MotionEvent.ACTION_DOWN) {
            this.downPoint.x = (int) ev.getX();
            this.downPoint.y = (int) ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    */
/* touch事件的拦截与处理都交给mDraghelper来处理 *//*

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        boolean moveFlag = moveDetector.onTouchEvent(ev);
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // ACTION_DOWN的时候就对view重新排序
            if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_SETTLING) {
                mDragHelper.abort();
            }
            orderViewStack();

            // 保存初次按下时arrowFlagView的Y坐标
            // action_down时就让mDragHelper开始工作，否则有时候导致异常
            mDragHelper.processTouchEvent(ev);
        }

        return shouldIntercept && moveFlag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        try {
            // 统一交给mDragHelper处理，由DragHelperCallback实现拖动效果
            // 该行代码可能会抛异常，正式发布时请将这行代码加上try catch
            mDragHelper.processTouchEvent(e);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
    //计算
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(
                resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

        allWidth = getMeasuredWidth();
        allHeight = getMeasuredHeight();
    }
    //定位
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        // 布局卡片view
        int size = viewList.size();
        for (int i = 0; i < size; i++) {
            View viewItem = viewList.get(i);
            int childHeight = viewItem.getMeasuredHeight();
            int viewLeft = (getWidth() - viewItem.getMeasuredWidth()) / 2;
            viewItem.layout(viewLeft, itemMarginTop, viewLeft + viewItem.getMeasuredWidth(), itemMarginTop + childHeight);
            int offset = yOffsetStep * i;
            float scale = 1 - SCALE_STEP * i;
            if (i > 2) {
                // 备用的view
                offset = yOffsetStep * 2;
                scale = 1 - SCALE_STEP * 2;
            }

            viewItem.offsetTopAndBottom(offset);
            viewItem.setScaleX(scale);
            viewItem.setScaleY(scale);
        }

        // 布局底部按钮的View
        if (null != bottomLayout) {
            int layoutTop = viewList.get(0).getBottom() + bottomMarginTop;
            bottomLayout.layout(left, layoutTop, right, layoutTop
                    + bottomLayout.getMeasuredHeight());
        }

        // 初始化一些中间参数
        initCenterViewX = viewList.get(0).getLeft();
        initCenterViewY = viewList.get(0).getTop();
        childWith = viewList.get(0).getMeasuredWidth();
    }

    */
/**
     * 本数据添加
     *//*

    public void fillData(List<CardDataItem> dataList) {
        this.dataList = dataList;

        int num = viewList.size();
        for (int i = 0; i < num; i++) {
            CardItemView itemView = viewList.get(i);
            itemView.fillData(dataList.get(i));
            itemView.setVisibility(View.VISIBLE);
        }

        if (null != cardSwitchListener) {
            cardSwitchListener.onShow(0);
        }
    }

    */
/**
     * 设置卡片操作回调
     *//*

    public void setCardSwitchListener(CardSwitchListener cardSwitchListener) {
        this.cardSwitchListener = cardSwitchListener;
    }

    */
/**
     * 卡片回调接口
     *//*

    public interface CardSwitchListener {
        */
/**
         * 新卡片显示回调
         *
         * @param index 最顶层显示的卡片的index
         *//*

        public void onShow(int index);

        */
/**
         * 卡片飞向两侧回调
         *
         * @param index 飞向两侧的卡片数据index
         * @param type  飞向哪一侧{@link #VANISH_TYPE_LEFT}或{@link #VANISH_TYPE_RIGHT}
         *//*

        public void onCardVanish(int index, int type);

        */
/**
         * 卡片点击事件
         *
         * @param cardImageView 卡片上的图片view
         * @param index         点击到的index
         *//*

        public void onItemClick(View cardImageView, int index);
    }
}*/
