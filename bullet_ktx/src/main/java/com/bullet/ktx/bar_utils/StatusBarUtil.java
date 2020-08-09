package com.bullet.ktx.bar_utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Petterp
 * on 2019-12-03
 * Function: Os-Status-Bar Utils
 */
public class StatusBarUtil {

    //初始化，基类使用

    /**
     * 避免状态栏重叠，此时状态栏字体颜色按照 boolean设置
     *
     * @param barView  title_bar
     * @param activity Activity_Context
     * @param dark     字体颜色模式
     */
    public static void initStatusBarTop(View barView, Activity activity, boolean dark) {
        barView.setPadding(0, 25, 0, 0);
        setStatusBarFontColor(activity, dark);
    }


    /**
     * 避免状态栏重叠，并根据标题栏颜色动态设置字体颜色
     *
     * @param barView  title_Bar
     * @param activity activity-context
     * @param color    title_bar_backColor
     */
    public static void initStatusBarTop(View barView, Activity activity, int color) {
        barView.setPadding(0, 25, 0, 0);
        //按照状态栏背景色识别
        setStatusBarFontColor(activity, isLightColor(color));
    }


    public static void initStatusBarTop( Activity activity, int color) {
//        barView.setPadding(0, 25, 0, 0);
        //按照状态栏背景色识别
//        Toast.makeText(activity, isLightColor(color)+"", Toast.LENGTH_SHORT).show();
        setStatusBarFontColor(activity, isLightColor(color));
    }

    /**
     * 判断当前颜色深浅状态
     *
     * @param color
     * @return
     */
    private static boolean isLightColor(@ColorInt int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    }

    /**
     * 设置状态栏字体颜色白色-原生使用
     */
    @TargetApi(23)
    public static void setTypeFaceColorLight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    /**
     * 设置状态栏字体颜色黑色-原生使用
     */
    @TargetApi(23)
    public static void setTypeFaceColorDark(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }


    /**
     * 隐藏状态栏
     */
    public static void hideBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 显示状态栏
     */
    public static void showBar(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 设置透明状态栏
     *
     * @param activity
     */
    @TargetApi(19)
    public static void setTranSparentBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    //状态栏颜色设置

    /**
     * 修改状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        //Android6.0（API 23）以上，系统方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColorV21(activity, color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarColorV19(activity, color);
        }
    }

    /**
     * 修改状态栏颜色-21
     *
     * @param activity
     * @param color
     */
    @TargetApi(21)
    private static void setStatusBarColorV21(Activity activity, @ColorInt int color) {
        Window window = activity.getWindow();
        window.setStatusBarColor(color);
    }

    /**
     * 修改状态栏颜色-19
     *
     * @param activity
     * @param color
     */
    @TargetApi(19)
    private static void setStatusBarColorV19(Activity activity, @ColorInt int color) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());
        //获取statusBar高度
        int statusBarHeight = getStatusBarHeight(window.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        decorViewGroup.addView(statusBarView);

        //设置标题栏下移
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setFitsSystemWindows(true);
        rootView.setClipToPadding(true);
    }


    //状态栏字体颜色设置

    /**
     * 设置状态栏字体颜色
     *
     * @param activity
     * @param dark
     */
    private static void setStatusBarFontColor(Activity activity, boolean dark) {
        switch (OsUtils.getName()) {
            case OsUtils.ROM_MIUI:
                setMIUISetStatusBarFontColor(activity, dark);
                break;
            case OsUtils.ROM_FLYME:
                setFlymeLightStatusBarFontColor(activity, dark);
                break;
            default:
                if (dark) {
                    setTypeFaceColorDark(activity);
                } else {
                    setTypeFaceColorLight(activity);
                }
                break;
        }
    }

    /**
     * MIUI状态栏字体颜色设置
     *
     * @param activity
     * @param dark
     * @return
     */
    @SuppressLint("PrivateApi")
    public static boolean setMIUISetStatusBarFontColor(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        setTypeFaceColorDark(activity);
                    } else {
                        setTypeFaceColorLight(activity);
                    }
                }
            } catch (Exception ignored) {

            }
        }
        return result;
    }


    /**
     * Flyme状态栏字体颜色设置
     *
     * @param activity
     * @param dark
     * @return
     */
    private static boolean setFlymeLightStatusBarFontColor(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }
}

