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
    private static class Client {
        @SuppressLint("StaticFieldLeak")
        private static ContextTools contexToos = new ContextTools();
    }

    public static ContextTools builder() {
        return Client.contexToos;
    }

    private ContextTools() {
    }

    public Context getContext() {
        return context;
    }

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

}
