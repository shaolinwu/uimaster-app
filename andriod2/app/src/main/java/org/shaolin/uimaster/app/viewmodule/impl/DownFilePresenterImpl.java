package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;

import java.io.File;

import okhttp3.Call;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class DownFilePresenterImpl {
    public DownFilePresenterImpl(final Context mContext, String url,String fileName) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(UrlData.APP_ROOT_FILE, fileName)
                {
                    @Override
                    public void inProgress(float progress, long total) {
                        Log.d("UIMaster","progress =" + progress + "===" + "total =" + total);
                    }

                    @Override
                    public void inProgress(float progress){
                        Log.d("UIMaster","progress =" + progress);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("e =", e.toString());
                    }

                    @Override
                    public void onResponse(File file){
                       Runnable runnable = new ZipRunnable(mContext, file.getAbsolutePath(), UrlData.APP_ROOT_FILE);
                        new Thread(runnable).start();
                    }
                });
    }


    public static class ZipRunnable implements Runnable {
        private Context mContext;
        private String zipFileString;
        private String outPathString;

        public ZipRunnable(Context mContext, String zipFileString, String outPathString){
            this.mContext = mContext;
            this.zipFileString = zipFileString;
            this.outPathString = outPathString;
        }

        @Override
        public void run() {
            try {
                FileUtil.UnZipFolder(zipFileString, outPathString);
                File file = new File(zipFileString);
                if (file.exists()){
                    file.delete();
                }
            } catch (Exception e) {
                PreferencesUtils.putString(mContext, ConfigData.FILE_VERSION,"0");
                e.printStackTrace();
            }
        }
    }
}
