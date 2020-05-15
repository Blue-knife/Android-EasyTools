package com.example.camera.camera;

import android.app.Activity;
import android.net.Uri;


/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.toos.camera
 * @time 2019/12/11 23:14
 * @description
 */
public class ToolsCamera {
    public static Uri createCropFile() {
        return Uri.parse(FileUtils.createFile("crop_image",
                FileUtils.getFileNameByTime("IMG", "jpg")).getPath());
    }

    public static void start(Activity activity) {
        new CameraHandler(activity).beginCameraDialog();
    }
}
