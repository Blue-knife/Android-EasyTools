package com.business.tools.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.business.tools.utils.file.FileQUtils;
import com.business.tools.utils.file.FileUtils;
import com.business.toos.R;

import java.io.File;

import static com.business.tools.camera.RequestCode.TAKE_PHOTO;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:18
 * @description
 */
public class CameraHandler implements View.OnClickListener {
    private final AlertDialog DIALOG;
    private final Activity activity;

    CameraHandler(Activity activity) {
        this.activity = activity;
        this.DIALOG = new AlertDialog.Builder(activity).create();
    }

    final void beginCameraDialog() {
        DIALOG.show();
        final Window window = DIALOG.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_camera_panel);
            window.setGravity(Gravity.BOTTOM);
            //设置动画
            window.setWindowAnimations(R.style.BottomAnimStyle);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);

            window.findViewById(R.id.photodialog_btn_cancel).setOnClickListener(this);
            window.findViewById(R.id.photodialog_btn_native).setOnClickListener(this);
            window.findViewById(R.id.photodialog_btn_take).setOnClickListener(this);
        }
    }

    private String getPhotoName() {
        return FileUtils.getFileNameByTime("IMG", "jpg");
    }

    /**
     * 打开相机
     */
    private void takePhoto() {
        //获取一个 名字,
        final String currentPhotoName = getPhotoName();
        //拍照意图
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //创建一个文件，路径为系统相册，第二个参数为名字
        final File tempFile = new File(FileUtils.CAMERA_PHOTO_DIR, currentPhotoName);

        Uri imageUri;
        // 注意7.0及以上与之前获取的uri不一样了，返回的是provider路径，需在清单中注册
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(activity,
                    "com.business.tools.fileProvider", tempFile);
        } else {
            imageUri = Uri.fromFile(tempFile);
        }

        CameraImageBean.getInstance().setPath(imageUri);
        CameraImageBean.getInstance().setFile(tempFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, TAKE_PHOTO);

        if (DIALOG != null) {
            DIALOG.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void takePhotoQ() {
        //获取一个 名字,
        final String currentPhotoName = getPhotoName();
        //拍照意图
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri uri = FileQUtils.saveImageWithAndroidQ(activity, currentPhotoName,"123");

        CameraImageBean.getInstance().setPath(uri);
        CameraImageBean.getInstance().setFile(FileQUtils.getFileByUri(uri, activity));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, TAKE_PHOTO);

        if (DIALOG != null) {
            DIALOG.cancel();
        }
    }

    /**
     * 打开 选择图片
     */
    private void pickPhoto() {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        //打开相册
        activity.startActivityForResult(intent, RequestCode.PICK_PHOTO);
        if (DIALOG != null) {
            DIALOG.cancel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.photodialog_btn_take) {
            takePhoto();
//            takePhotoQ();
        } else if (id == R.id.photodialog_btn_native) {
            pickPhoto();
        } else if (id == R.id.photodialog_btn_cancel) {
            DIALOG.cancel();
        }
    }
}

