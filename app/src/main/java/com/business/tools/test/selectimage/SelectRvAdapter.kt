package com.business.tools.test.selectimage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bullet.core.ToastUtils
import com.bullet.ui.customView.SquareImageView
import com.bumptech.glide.Glide
import com.business.toos.R

/**
 * @name SelectRvAdapter
 * @package com.business.tools.test.selectImage
 * @author 345 QQ:1831712732
 * @time 2020/5/24 17:43
 * @description
 */

class SelectRvAdapter(
    private val images: ArrayList<Uri?>,
    private val result: ArrayList<Uri>,
    private val maxCount: Int,
    private val block: () -> Unit
) :
    RecyclerView.Adapter<SelectRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: SquareImageView = view.findViewById(R.id.image)
        val selectImage: AppCompatImageView = view.findViewById(R.id.selected_indicator)
        val camera: LinearLayoutCompat = view.findViewById(R.id.camera_ll)
        val mask: View = view.findViewById(R.id.mask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_image_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = images[position]
        if (path == null) {
            holder.mask.visibility = View.VISIBLE
            holder.camera.visibility = View.VISIBLE
            holder.image.visibility = View.GONE
            holder.selectImage.visibility = View.GONE
            holder.camera.setOnClickListener {
                // 调用相机
            }
        } else {
            holder.image.visibility = View.VISIBLE
            holder.selectImage.visibility = View.VISIBLE
            holder.mask.visibility = View.GONE
            holder.camera.visibility = View.GONE

            // 显示图片
            Glide.with(holder.image.context)
                .load(path)
                .into(holder.image)

            if (result.contains(path)) {
                holder.selectImage.setImageResource(R.drawable.chooser_ic_selected)
                holder.selectImage.isSelected = true
            } else {
                holder.selectImage.setImageResource(R.drawable.chooser_ic_unselected)
                holder.selectImage.isSelected = false
            }

            holder.image.setOnClickListener {
                if (result.contains(path)) {
                    result.remove(path)
                } else {
                    if (result.size >= maxCount) {
                        ToastUtils.showCenterText("超过最大设置")
                        return@setOnClickListener
                    }
                    result.add(path)
                }
                block()
                notifyItemChanged(position)
            }
        }
    }
}
