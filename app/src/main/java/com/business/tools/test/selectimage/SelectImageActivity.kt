package com.business.tools.test.selectimage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.GridLayoutManager
import com.business.toos.R
import com.example.core.base.BaseSkinActivity
import kotlinx.android.synthetic.main.activity_select_image.*
import kotlinx.android.synthetic.main.activity_upload_photo.*

/**
 * @name SelectImageActivity
 * @package com.business.tools.test.selectImage
 * @author 345 QQ:1831712732
 * @time 2020/5/23 20:25
 * @description
 */
class SelectImageActivity : BaseSkinActivity() {

    companion object {
        //多选
        const val MODE_MULTI = 0x001

        //单选
        const val MODE_SINGLE = 0x002

        //查询全部
        const val LOADER_TYPE = 0x003

        //KEY
        // 是否显示相机的EXTRA_KEY
        const val EXTRA_SHOW_CAMERA = "EXTRA_SHOW_CAMERA"

        // 总共可以选择多少张图片的EXTRA_KEY
        const val EXTRA_SELECT_COUNT = "EXTRA_SELECT_COUNT"

        // 原始的图片路径的EXTRA_KEY
        const val EXTRA_DEFAULT_SELECTED_LIST = "EXTRA_DEFAULT_SELECTED_LIST"

        // 选择模式的EXTRA_KEY
        const val EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE"

        // 返回选择图片列表的EXTRA_KEY
        const val EXTRA_RESULT = "EXTRA_RESULT"
    }

    //单选或者多选
    private var mMode = MODE_MULTI

    //是否显示拍照按钮
    var mShowCamera = true

    // int 类型的图片张数
    private var mMaxCount = 8

    // List 选择好的图片列表
    var mResultList: ArrayList<Uri>? = null

    private var selectAdapter: SelectRvAdapter? = null

    override fun layout(): Int {
        return R.layout.activity_select_image
    }

    override fun params(intent: Intent) {
        // 1.获取传递过来的参数
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode)
        mMaxCount = intent.getIntExtra(EXTRA_SELECT_COUNT, mMaxCount)
        mShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mShowCamera)
        mResultList = intent.getParcelableArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST)

        if (mResultList == null) {
            mResultList = ArrayList()
        }
        if (mMode == MODE_SINGLE) {
            mMaxCount = 1
        }

        select_back.setOnClickListener {
            finish()
        }
    }

    override fun bindView() {
        LoaderManager.getInstance(this).initLoader(LOADER_TYPE, null, mLoaderCallback)

        changeView()

        select_save.setOnClickListener {
            val mIntent = Intent()
            mIntent.putParcelableArrayListExtra(EXTRA_RESULT, mResultList)
            setResult(Activity.RESULT_OK, mIntent)
            finish()
        }
    }

    /**
     * 加载图片
     */
    private val mLoaderCallback =
            object : LoaderManager.LoaderCallbacks<Cursor?> {
                private val IMAGE_PROJECTION = arrayOf(
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media._ID
                )

                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor?> {
                    return CursorLoader(
                            this@SelectImageActivity,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            IMAGE_PROJECTION,
                            IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR "
                                    + IMAGE_PROJECTION[3] + "=? ",
                            arrayOf("image/jpeg", "image/png"),
                            IMAGE_PROJECTION[2] + " DESC"
                    )
                }

                override fun onLoadFinished(loader: Loader<Cursor?>, data: Cursor?) {
                    if (data != null && data.count > 0) {
                        val images = arrayListOf<Uri?>()
                        if (mShowCamera) images.add(null)
                        while (data.moveToNext()) {
                            val id =
                                    data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                            val url = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                            )
                            images.add(url)
                        }
                        showImageList(images)
                    }
                }

                override fun onLoaderReset(loader: Loader<Cursor?>) = Unit
            }

    private fun showImageList(images: java.util.ArrayList<Uri?>) {
        image_list_rv.layoutManager = GridLayoutManager(this, 4)
        image_list_rv.itemAnimator = null
        image_list_rv.addItemDecoration(GridDividerDecoration(GridLayoutManager.VERTICAL))
        if (selectAdapter == null) {
            selectAdapter = SelectRvAdapter(images, mResultList!!, mMaxCount, ::changeView)
        }
        image_list_rv.adapter = selectAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun changeView() {
        select_preview.isEnabled = mResultList!!.size > 0
        if (mResultList!!.size > 0) {
            select_save.visibility = View.VISIBLE
            select_save.text = "完成(${mResultList!!.size} / $mMaxCount)"
        } else {
            select_save.visibility = View.GONE
            select_save.text = ""
        }
    }
}