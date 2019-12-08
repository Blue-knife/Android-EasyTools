package com.business.toos;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by Petterp
 * on 2019-10-26
 * Function:
 */
public class ContextTools extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

}
