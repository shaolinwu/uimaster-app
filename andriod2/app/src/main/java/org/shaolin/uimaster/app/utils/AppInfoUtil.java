package org.shaolin.uimaster.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created Administrator
 * on 2017/3/22
 * deprecated:
 */

public class AppInfoUtil {
    //版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    public static String getAppVersionName(Context context){
        return getPackageInfo(context).versionName;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }
}
