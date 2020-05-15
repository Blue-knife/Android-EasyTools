package com.business.tools.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.business.tools.service.TestJobService
import com.business.toos.R

class MainRvAdapter(data: List<Pair<String, Any?>>, context: Context) : RecyclerView.Adapter<MainRvAdapter.Holder>() {

    val list: List<Pair<String, Any?>> = data
    private val mContext = context

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_rv_item, null, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val pair = list[position]
        holder.tv.text = pair.first
        val second = pair.second
        holder.tv.setOnClickListener {
            if (pair.first == "开启 JobService") {
                startJobService()
            } else if (second != null) {
                //智能转换
                if (second is Class<*>) startIntent(second)
            }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: AppCompatButton = itemView.findViewById(R.id.item_btn)
    }

    private fun startIntent(name: Class<*>) {
        val intent = Intent(mContext, name)
        mContext.startActivity(intent)
    }

    private fun startJobService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            TestJobService.start(mContext)
        }
    }
}