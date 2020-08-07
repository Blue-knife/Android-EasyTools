package com.business.tools.test.selectimage

import android.content.ContentResolver
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.business.toos.R
import com.bullet.core.ContextTools
import com.bullet.ui.customView.SquareImageView

/**
 * @name UpLoadRvAdapter
 * @package com.business.tools.test.selectImage
 * @author 345 QQ:1831712732
 * @time 2020/5/24 19:07
 * @description
 */

class UpLoadRvAdapter(private val images: ArrayList<Uri>, private val loadMore: () -> Unit) : RecyclerView.Adapter<UpLoadRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: SquareImageView = view.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == images.size) {
            Glide.with(holder.image.context)
                    .load(imageTranslateUri(R.drawable.ic_more))
                    .into(holder.image)
            holder.image.setBackgroundColor(Color.parseColor("#e3e3e3"))
            holder.image.setPadding(50,50,50,50)
            holder.image.setOnClickListener {
                loadMore()
            }
            return
        }
        val path = images[position]
        //显示图片
        Glide.with(holder.image.context)
                .load(path)
                .into(holder.image)
        holder.image.setOnClickListener { }

    }

    private fun imageTranslateUri(resId: Int): Uri {
        val r = ContextTools.context.resources
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(resId) + "/"
                + r.getResourceTypeName(resId) + "/"
                + r.getResourceEntryName(resId))
    }
}