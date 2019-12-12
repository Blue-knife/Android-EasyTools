package com.business.tools.camera;

import android.Manifest;
import android.app.Activity;
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
import androidx.appcompat.widget.AppCompatImageView;
import com.business.toos.R;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:02
 * @description 一键式调用相机，相册，系统裁剪库
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 0x01;

    public AppCompatImageView mImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mImage = findViewById(R.id.activity_camera_image);
        findViewById(R.id.activity_camera_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
        ToolsCamera.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == 1) {
            switch (requestCode) {
                //相机回调
                case RequestCode.TAKE_PHOTO:
                    final Uri resultUri = CameraImageBean.getInstance().getPath();
                    if (resultUri != null) {
                        CropPhoto.cropPhoto(this, true, resultUri);
                    }
                    break;
                case RequestCode.PICK_PHOTO:
                    if (data != null) {
                        final Uri pickPath = data.getData();
                        CropPhoto.cropPhoto(this, false, pickPath);
                    }
                    break;
                case RequestCode.CROP_PHOTO:
                    Bitmap bitmap = BitmapFactory.decodeFile(CameraImageBean.getInstance().getPath().getPath());
                    if (bitmap != null) {
                        mImage.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
