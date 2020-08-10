package com.bullet.camera.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider

/**
 * @author 345 QQ:1831712732
 * @name Android Business Tools
 * @class name：com.business.tools.camera
 * @time 2019/12/11 23:28
 * @description
 */
object CropPhoto {
    /**
     * 裁切图片
     *
     * @param activity activity
     * @param uri      uri
     */
    @JvmStatic
    fun cropPhoto(activity: Activity, uri: Uri?) {
        var imageUrl: Uri? = null
        //打开系统自带的裁剪图片的intent
        val intent = Intent("com.android.camera.action.CROP")
        // 注意一定要添加该项权限，否则会提示无法裁剪
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("scale", true)
        // 取消人脸识别
        intent.putExtra("noFaceDetection", true)
        // 图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())

        // 若为false则表示不返回数据
        intent.putExtra("return-data", false)

        // 裁切后图片保存的路径，兼容10.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageUrl = ImageUtils.saveImageWithAndroidQ(activity)
        } else {
            // 注意7.0及以上与之前获取的uri不一样了，返回的是provider路径，需在清单中注册
            val tempFile = ImageUtils.saveImageFile()
            if (tempFile != null) {
                imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(activity,
                            "${activity.packageName}.fileProvider", tempFile)
                } else {
                    Uri.fromFile(tempFile)
                }
            }
        }

        //保存图片路径
        CameraImageBean.instance.path = imageUrl
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl)
        activity.startActivityForResult(intent, RequestCode.CROP_PHOTO)
    }
}