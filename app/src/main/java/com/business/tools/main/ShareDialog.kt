package com.business.tools.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.business.tools.utils.ToastUtils
import com.business.tools.utils.ToolsUtils
import com.business.tools.views.RoundFrameLayout
import com.business.tools.views.RoundViewHelper
import com.business.toos.R
import java.io.File
import java.io.FileOutputStream

/**
 * @name Android Business Toos
 * @class name：com.business.tools.main
 * @author 345 QQ:1831712732
 * @time 2020/4/20 21:06
 * @description
 */

class ShareDialog(context: Context) : AlertDialog(context) {

    private var shareContent: String? = null
    private lateinit var shareAdapter: ShareAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val layout = RoundFrameLayout(context)
        layout.setBackgroundColor(Color.WHITE)
        layout.setViewOutLine(ToolsUtils.dip2px(20f), RoundViewHelper.RADIUS_TOP)
        val param = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        param.height = ToolsUtils.screenHeight / 2
        layout.layoutParams = param
        setContentView(layout)

        val gridView = RecyclerView(context)
        gridView.layoutManager = GridLayoutManager(context, 4)
        val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )
        params.leftMargin = ToolsUtils.dip2px(30f)
        params.rightMargin = ToolsUtils.dip2px(20f)
        params.topMargin = ToolsUtils.dip2px(10f)
        params.bottomMargin = ToolsUtils.dip2px(10f)
        params.gravity = Gravity.CENTER
        layout.addView(gridView, params)

        window?.setGravity(Gravity.BOTTOM)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val shareItems = ToolsUtils.queryShareItems()
        if (shareContent != null) {
            shareAdapter =
                    ShareAdapter(shareItems, context.packageManager, shareContent!!)
            gridView.adapter = shareAdapter
        } else {
            ToastUtils.showText("分享内容为空")
        }

    }

    fun setShareContent(shareContent: String) {
        this.shareContent = shareContent
    }


    private class ShareAdapter(
            val item: MutableList<ResolveInfo>?,
            val packageManager: PackageManager,
            val shareContent: String
    ) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_share_item, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int {
            return item?.size ?: 0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (item != null) {
                val resolveInfo = item[position]
                val shareImage = holder.itemView.findViewById<AppCompatImageView>(R.id.share_icon)
                shareImage.setImageDrawable(resolveInfo.loadIcon(packageManager))
                val shareText = holder.itemView.findViewById<AppCompatTextView>(R.id.share_text)
                shareText.text = resolveInfo.loadLabel(packageManager)
                shareImage.setOnClickListener {

                    val pkg = resolveInfo.activityInfo.packageName
                    val cls = resolveInfo.activityInfo.name
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.type = "text/plain,image/*"; // 纯文本
//                    intent.type = "image/*"
                    intent.component = ComponentName(pkg, cls)

                    //分享的内容
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent)
//                    intent.putExtra(Intent.EXTRA_STREAM, getUri(shareImage))
                    val shareIntent = Intent.createChooser(intent, "Android EasyTools")
                    shareImage.context.startActivity(shareIntent)
                }
            }

        }

        private fun getUri(shareImage: AppCompatImageView): Uri {
            val bitmap = BitmapFactory.decodeResource(
                    shareImage.context.resources,
                    R.mipmap.icon
            )
            val file = File(shareImage.context.externalCacheDir, "shareImage.png")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            return FileProvider.getUriForFile(
                    shareImage.context,
                    "${shareImage.context.packageName}.fileProvider", file
            )
        }


    }
}