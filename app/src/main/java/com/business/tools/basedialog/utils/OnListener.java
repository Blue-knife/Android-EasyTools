package com.business.tools.basedialog.utils;

import android.view.View;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.tools.basedialog.utils
 * @time 2019/12/23 21:53
 * @description 监听 dialog 的事件
 */
public interface OnListener {
    /**
     * 事件监听
     *
     * @param dialog dialog
     * @param view   view
     */
    void onClick(BaseFragDialog dialog, View view);
}