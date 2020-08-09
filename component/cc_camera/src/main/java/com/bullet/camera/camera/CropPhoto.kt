package com.bullet.camera.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File

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
        val imageUrl: Uri
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

        // 裁切后图片保存的路径
        imageUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //10.0 获取保存路径
            FileQUtils.saveImageWithAndroidQ(activity, "Crop" + System.currentTimeMillis() + ".png", "CaiFu")
        } else {
            //创建一个文件，路径为系统相册，第二个参数为名字
            val tempFile = File(FileUtils.CAMERA_PHOTO_DIR + File.separator, "CaiFu-Crop" + System.currentTimeMillis() + ".png")
            if (tempFile.parentFile != null) {
                tempFile.parentFile?.takeIf {
                    it.exists()
                }?.mkdirs()
            }
            Uri.fromFile(tempFile)
        }

        //保存图片路径
        CameraImageBean.instance.path = imageUrl
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUrl)
        activity.startActivityForResult(intent, RequestCode.CROP_PHOTO)
    }
}