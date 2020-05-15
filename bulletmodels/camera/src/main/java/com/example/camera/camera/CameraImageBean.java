package com.example.camera.camera;

import android.net.Uri;

import java.io.File;

/**
 * @author 345 QQ:1831712732
 * @name Android Business Toos
 * @class name：com.business.tools.camera
 * @time 2019/12/11 23:25
 * @description 保存 url 和 file
 */
public final class CameraImageBean {

    private Uri mPath = null;
    private File file = null;

    private CameraImageBean() {
    }

    private static final CameraImageBean INSTANCE = new CameraImageBean();

    public static CameraImageBean getInstance() {
        return INSTANCE;
    }

    public Uri getPath() {
        return mPath;
    }

    public void setPath(Uri mPath) {
        this.mPath = mPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

