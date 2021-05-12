package com.business.tools.test

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.bullet.camera.camera.CameraImageBean.Companion.instance
import com.bullet.camera.camera.CropPhoto.cropPhoto
import com.bullet.camera.camera.ImageUtils
import com.bullet.camera.camera.RequestCode
import com.bullet.camera.camera.ToolsCamera.start
import com.bullet.camera.camera.zxing.android.CaptureActivity
import com.bumptech.glide.Glide
import com.business.tools.test.selectimage.UpLoadPhotoActivity
import com.business.toos.R
import com.cloudx.ktx.core.launchIO
import com.cloudx.ktx.core.withContextMain
import com.petterp.cloud.bullet.base.zxing.CrCodeImageUtils
import java.io.File

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:02
 * @description 一键式调用相机，相册，系统裁剪库
 */
class CameraActivity : AppCompatActivity(), View.OnClickListener {
    var mImage: AppCompatImageView? = null
    var clickView: View? = null
    private var mQRContent: AppCompatEditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mImage = findViewById(R.id.activity_camera_image)
        mQRContent = findViewById(R.id.activity_camera_edit)
        findViewById<View>(R.id.activity_camera_start).setOnClickListener(this)
        findViewById<View>(R.id.activity_camera_scan).setOnClickListener(this)
        findViewById<View>(R.id.activity_camera_qr_code).setOnClickListener(this)
        findViewById<View>(R.id.activity_upload_photo).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        clickView = v
        checkPermission(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    fun checkPermission(permission: Array<String?>) {
        if (Build.VERSION.SDK_INT < 23 || permission.isEmpty()) {
            start()
        } else {
            requestPermissions(permission, REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            start()
        }
    }

    /**
     * 调用
     */
    fun start() {
        if (clickView != null) {
            when (clickView!!.id) {
                R.id.activity_camera_start -> start(this)
                R.id.activity_camera_scan -> {
                    val intent = Intent(this, CaptureActivity::class.java)
                    startActivityForResult(intent, RequestCode.SCAN)
                }
                R.id.activity_camera_qr_code -> {
                    if (mQRContent!!.text != null) {
                        val s = mQRContent!!.text.toString()
                        if (!s.isEmpty()) {
                            val qrCodeBitmap = CrCodeImageUtils.createQRCodeBitmap(mQRContent!!.text.toString())
                            if (qrCodeBitmap != null) {
                                mImage!!.setImageBitmap(qrCodeBitmap)
                            } else {
                                Toast.makeText(this, "生成错误", Toast.LENGTH_SHORT).show()
                            }
                            return
                        }
                    }
                    Toast.makeText(this, "字符串不允许为空", Toast.LENGTH_SHORT).show()
                }
                R.id.activity_upload_photo -> startActivity(Intent(this, UpLoadPhotoActivity::class.java))
                else -> {
                }
            }
        }
    }

    /**
     * 回调返回过来
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RequestCode.TAKE_PHOTO -> {
                    val resultUri = instance.path
                    // 裁剪
                    cropPhoto(this, resultUri)
//                    if (resultUri != null) {
//                        setImage(resultUri)
//                    }
                }
                RequestCode.PICK_PHOTO -> if (data != null) {
                    // 裁剪
                    val pickPath = data.data
                    cropPhoto(this, pickPath)
                }
                RequestCode.CROP_PHOTO -> {
                    val uri = instance.path
                    if (uri != null) {
                        setImage(uri)
                    }
                }
                RequestCode.SCAN -> if (data != null) {
                    // 返回的文本内容
                    val content = data.getStringExtra(DECODED_CONTENT_KEY)
                    // 返回的BitMap图像
                    val scan = data.getParcelableExtra<Bitmap>(DECODED_BITMAP_KEY)
                    Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
                    if (scan != null) {
                        mImage!!.setImageBitmap(scan)
                    }
                }
                else -> {
                }
            }
        }
    }

    fun setImage(uri: Uri) {
        launchIO {
            ImageUtils.getPath(this, uri)?.let {
                withContextMain {
                    Glide.with(this)
                        .load(File(it))
                        .into(mImage!!)
                }
            }
        }
    }

    companion object {
        private const val DECODED_CONTENT_KEY = "codedContent"
        private const val DECODED_BITMAP_KEY = "codedBitmap"
        const val REQUEST_CODE = 0x01
    }
}
