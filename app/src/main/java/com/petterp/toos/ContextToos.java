package com.petterp.toos;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by Petterp
 * on 2019-10-26
 * Function:
 */
public class ContextToos extends Application {
    private static class Client {
        @SuppressLint("StaticFieldLeak")
        private static ContextToos contexToos = new ContextToos();
    }

    public static ContextToos Builder() {
        return Client.contexToos;
    }

    private ContextToos() {
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
