package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;

import java.io.File;

import okhttp3.Call;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class AdFilePresenterImpl {
    public AdFilePresenterImpl(final Context context, String url, final String fileName, final String zipFileName) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(FileData.APP_ROOT_FILE, fileName)
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
                        File file1 = new File(FileData.APP_ROOT_FILE + File.separator + zipFileName);
                        if (!file1.exists()){
                            file1.mkdir();
                        }
                        Runnable runnable = new AdFilePresenterImpl.ZipRunnable(context, file.getAbsolutePath(), file1.getAbsolutePath());
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
                        File adRootFile = new File(outPathString);
                        File[] files = adRootFile.listFiles();
                        PreferencesUtils.putString(mContext, ConfigData.AD_PATH,files[0].getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
            }
        }
    }

}
