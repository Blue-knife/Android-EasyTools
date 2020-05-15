package com.business.tools.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.business.tools.utils.ImageUtils;
import com.business.toos.R;
import com.example.camera.camera.CameraImageBean;
import com.example.camera.camera.CropPhoto;
import com.example.camera.camera.RequestCode;
import com.example.camera.camera.ToolsCamera;
import com.example.camera.camera.zxing.android.CaptureActivity;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:02
 * @description 一键式调用相机，相册，系统裁剪库
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    public static final int REQUEST_CODE = 0x01;

    public AppCompatImageView mImage;
    public View clickView;
    private AppCompatEditText mQRContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mImage = findViewById(R.id.activity_camera_image);
        mQRContent = findViewById(R.id.activity_camera_edit);
        findViewById(R.id.activity_camera_start).setOnClickListener(this);
        findViewById(R.id.activity_camera_scan).setOnClickListener(this);
        findViewById(R.id.activity_camera_qr_code).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.clickView = v;
        checkPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,});
    }

    public void checkPermission(String[] permission) {
        if (Build.VERSION.SDK_INT < 23 || permission.length == 0) {
            start();
        } else {
            requestPermissions(permission, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            start();
        }
    }

    /**
     * 调用
     */
    public void start() {
        if (clickView != null) {
            switch (clickView.getId()) {
                case R.id.activity_camera_start:
                    ToolsCamera.start(this);
                    break;
                case R.id.activity_camera_scan:
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, RequestCode.SCAN);
                    break;
                case R.id.activity_camera_qr_code:
                    if (mQRContent.getText() != null) {
                        String s = mQRContent.getText().toString();
                        if (!s.isEmpty()) {
                            Bitmap qrCodeBitmap = ImageUtils.createQRCodeBitmap(mQRContent.getText().toString());
                            if (qrCodeBitmap != null) {
                                mImage.setImageBitmap(qrCodeBitmap);
                            } else {
                                Toast.makeText(this, "生成错误", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                    }
                    Toast.makeText(this, "字符串不允许为空", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 回调返回过来
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //相机回调
                case RequestCode.TAKE_PHOTO:
                    final Uri resultUri = CameraImageBean.getInstance().getPath();
                    if (resultUri != null) {
                        //裁剪
                        CropPhoto.cropPhoto(this, true, resultUri);
                    }
                    break;
                //相册回调
                case RequestCode.PICK_PHOTO:
                    if (data != null) {
                        //裁剪
                        final Uri pickPath = data.getData();
                        CropPhoto.cropPhoto(this, false, pickPath);
                    }
                    break;
                //裁剪回调
                case RequestCode.CROP_PHOTO:
                    Bitmap bitmap = BitmapFactory.decodeFile(CameraImageBean.getInstance().getPath().getPath());
                    if (bitmap != null) {
                        mImage.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //二维码回调
                case RequestCode.SCAN:
                    if (data != null) {
                        //返回的文本内容
                        String content = data.getStringExtra(DECODED_CONTENT_KEY);
                        //返回的BitMap图像
                        Bitmap scan = data.getParcelableExtra(DECODED_BITMAP_KEY);
                        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
                        if (scan != null) {
                            mImage.setImageBitmap(scan);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
