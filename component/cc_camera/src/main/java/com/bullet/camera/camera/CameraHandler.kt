package com.bullet.camera.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.FileProvider
import com.bullet.camera.R
import com.bullet.ui.dialog.base.FastDialog

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:18
 * @description
 */
class CameraHandler internal constructor(private val activity: Activity) : View.OnClickListener {
    private val mFastDialog: FastDialog?

    init {
        mFastDialog = FastDialog.Builder(activity)
            .setWidth(LinearLayoutCompat.LayoutParams.MATCH_PARENT)
            .setContentView(R.layout.dialog_camera_panel)
            .stGravity(Gravity.BOTTOM)
            .addDefaultAnimation()
            .build()
    }

    fun beginCameraDialog() {
        mFastDialog!!.show()
        mFastDialog.getView<View>(R.id.photodialog_btn_cancel)!!.setOnClickListener(this)
        mFastDialog.getView<View>(R.id.photodialog_btn_native)!!.setOnClickListener(this)
        mFastDialog.getView<View>(R.id.photodialog_btn_take)!!.setOnClickListener(this)
    }

    /**
     * 打开相机
     */
    private fun takePhoto() {
        // 拍照意图
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var uri: Uri? = null
        // 兼容10.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = ImageUtils.saveImageWithAndroidQ(activity)
        } else {
            // 注意7.0及以上与之前获取的uri不一样了，返回的是provider路径，需在清单中注册
            val tempFile = ImageUtils.saveImageFile()
            if (tempFile != null) {
                uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(
                        activity,
                        "${activity.packageName}.fileProvider", tempFile
                    )
                } else {
                    Uri.fromFile(tempFile)
                }
            }
        }
        CameraImageBean.instance.path = uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        activity.startActivityForResult(intent, RequestCode.TAKE_PHOTO)
        mFastDialog?.cancel()
    }

    /**
     * 打开 选择图片
     */
    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        // 打开相册
        activity.startActivityForResult(intent, RequestCode.PICK_PHOTO)
        mFastDialog?.cancel()
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.photodialog_btn_take) {
            takePhoto()
        } else if (id == R.id.photodialog_btn_native) {
            pickPhoto()
        } else if (id == R.id.photodialog_btn_cancel) {
            mFastDialog!!.cancel()
        }
    }
}
