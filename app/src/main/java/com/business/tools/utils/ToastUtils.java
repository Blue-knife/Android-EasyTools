package com.business.tools.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.business.tools.ContextTools;


/**
 * @author Petterp on 2019/8/26
 * Summary:
 * 邮箱：1509492795@qq.com
 */
public class ToastUtils {
    private static Toast mToast;
//    @SuppressLint("StaticFieldLeak")
//    private static Context context;
//
//    /**
//     * 初始化Toast
//     *
//     * @param context
//     */
//    public static void initToast(Context context) {
//        ToastUtils.context = context;
//    }


    public static void showText(String res) {
        cancelToast();
        mToast = Toast.makeText(ContextTools.Companion.getContext(), res, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public static void showLongText(String res) {
        cancelToast();
        mToast = Toast.makeText(ContextTools.Companion.getContext(), res, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void showCenterText(String res) {
        cancelToast();
        mToast = Toast.makeText(ContextTools.Companion.getContext(), res, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

}
