package com.business.tools.test.selectimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.business.toos.R
import com.bullet.core.ToastUtils
import com.bullet.core.base.BaseSkinActivity
import com.bullet.ui.dialog.ToastDialog
import kotlinx.android.synthetic.main.activity_upload_photo.*
import java.io.File

/**
 * @name UpLoadPhotoActivity
 * @package com.business.tools.test.selectImage
 * @author 345 QQ:1831712732
 * @time 2020/5/24 17:19
 * @description
 */

class UpLoadPhotoActivity : BaseSkinActivity() {

    private val images = ArrayList<Uri>()

    private var upLoadRvAdapter: UpLoadRvAdapter? = null

    override fun layout(): Int {
        return R.layout.activity_upload_photo
    }

    override fun bindView() {

        activity_upload_rv.layoutManager = GridLayoutManager(this, 3)
        if (upLoadRvAdapter == null) {
            upLoadRvAdapter = UpLoadRvAdapter(images, ::loadMore)
        }
        activity_upload_rv.addItemDecoration(GridDividerDecoration(GridLayoutManager.VERTICAL))
        activity_upload_rv.itemAnimator = null
        activity_upload_rv.adapter = upLoadRvAdapter

        activity_upload_btn.setOnClickListener {
            ToastDialog.loading(this)
            if (images.size == 0) {
                ToastUtils.showText("请选择图片")
                return@setOnClickListener
            }
            val map = mutableMapOf<String, File>()
            images.forEach {
                val query = contentResolver.query(it, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
                query?.moveToFirst()
                val index = query?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val path = query!!.getString(index!!)
                if (path == null) {
                    Log.e("------upLoad--->", "未成功")
                    query.close()
                    return@forEach
                }
                map[it.toString()] = File(path)
                query.close()
            }
        }
    }

    private fun loadMore() {
        val intent = Intent(this, SelectImageActivity::class.java)
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_COUNT, 9)
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_MODE, SelectImageActivity.MODE_SINGLE)
        intent.putParcelableArrayListExtra(SelectImageActivity.EXTRA_DEFAULT_SELECTED_LIST, images)
        intent.putExtra(SelectImageActivity.EXTRA_SHOW_CAMERA, false)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0 && data != null) {
                val list: ArrayList<Uri>? =
                        data.getParcelableArrayListExtra(SelectImageActivity.EXTRA_RESULT)
                Toast.makeText(this@UpLoadPhotoActivity, "${list?.size}", Toast.LENGTH_LONG)
                        .show()
                images.clear()
                images.addAll(list!!)
                upLoadRvAdapter?.notifyDataSetChanged()
            } else {
                Toast.makeText(this@UpLoadPhotoActivity, "空", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

}