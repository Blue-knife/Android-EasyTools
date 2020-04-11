package com.business.tools.views.page

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * @name Android Business Toos
 * @class name：com.business.tools.views.page
 * @author 345 QQ:1831712732
 * @time 2020/4/11 20:47
 * @description 给 Scroller 添加了一个监听
 */
class ScrollerExt : Scroller {

    /**
     * 滚动操作是否完成，true为未完成，false 即完成
     */
    interface ScrollListener {
        fun isScroll(isScroll: Boolean)
    }

    var listener: ScrollListener? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, interpolator: Interpolator) : super(context, interpolator)
    constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(
            context,
            interpolator,
            flywheel
    )


    override fun computeScrollOffset(): Boolean {
        val offset = super.computeScrollOffset()
        if (listener != null) {
            listener?.isScroll(offset)
        }
        return offset
    }
}
