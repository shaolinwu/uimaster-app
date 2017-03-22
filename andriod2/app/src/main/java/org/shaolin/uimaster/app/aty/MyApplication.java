package org.shaolin.uimaster.app.aty;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/1/18.
 */

public class MyApplication extends Application {

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }
}
