package com.business.tools.views.page

import android.view.View

/**
 * @name Android Business Toos
 * @class name：com.business.tools.views.page
 * @author 345 QQ:1831712732
 * @time 2020/4/11 20:48
 * @description
 */
abstract class PageAdapter(val layoutRes: Int) {

    abstract fun count(): Int

    abstract fun view(view: View, position: Int)
}
