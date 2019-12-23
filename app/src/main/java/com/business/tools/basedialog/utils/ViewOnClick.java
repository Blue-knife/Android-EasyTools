package com.business.tools.basedialog.utils;

import android.view.View;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.tools.basedialog.utils
 * @time 2019/12/23 21:55
 * @description 对点击事件进行处理
 */
public final class ViewOnClick implements View.OnClickListener {
    private final BaseFragDialog dialog;
    private final OnListener listener;

    ViewOnClick(BaseFragDialog dialog, OnListener listener) {
        this.dialog = dialog;
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(dialog, view);
    }
}