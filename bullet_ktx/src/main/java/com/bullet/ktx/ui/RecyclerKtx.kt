package com.bullet.ktx.ui


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author petterp
 * @Date 2020/5/29-11:15 PM
 * @Email ShiyihuiCloud@163.com
 * @Function Ktx-UI-core
 */



/**
 * RecyclerView 移动到当前位置，
 *
 * @param manager       设置RecyclerView对应的manager
 * @param mRecyclerView 当前的RecyclerView
 * @param n             要跳转的位置
 */
fun RecyclerView.moveToPosition(
        n: Int
) {
    val firstItem: Int = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    val lastItem: Int = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
    if (n <= firstItem) {
        scrollToPosition(n)
    } else if (n <= lastItem) {
        val top = getChildAt(n - firstItem).top
        scrollBy(0, top)
    } else {
        scrollToPosition(n)
    }
}
