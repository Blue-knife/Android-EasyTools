package com.business.toos;

import android.view.Gravity;
import android.widget.Toast;


/**
 * @author Petterp on 2019/8/26
 * Summary:
 * 邮箱：1509492795@qq.com
 */
public class ToastUtils {
    public static void showText(String res) {
        Toast.makeText(ContextTools.context, res, Toast.LENGTH_SHORT).show();
    }

    public static void showLongText(String res) {
        Toast.makeText(ContextTools.context, res, Toast.LENGTH_LONG).show();
    }

    public static void showCenterText(String res) {
        Toast centerToast = Toast.makeText(ContextTools.context, res, Toast.LENGTH_SHORT);
        centerToast.setGravity(Gravity.CENTER, 0, 0);
        centerToast.show();
    }

}
