package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.os.Environment;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.utils.FileLog;
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
                .execute(new FileCallBack(FileData.APP_ROOT_FILE, fileName)
                {
                    @Override
                    public void inProgress(float progress, long total) {
                        FileLog.d("UIMaster","donwload file progress =" + progress + "===" + "total =" + total);
                    }

                    @Override
                    public void inProgress(float progress){
                        FileLog.d("UIMaster","donwload file progress =" + progress);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        FileLog.e("e =", e.toString());
                    }

                    @Override
                    public void onResponse(File file){
                       Runnable runnable = new ZipRunnable(mContext, file.getAbsolutePath(), FileData.APP_ROOT_FILE);
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
                FileUtil.unZipToHidden(mContext, "uimaster.zip", Environment.getExternalStorageDirectory().getAbsolutePath(),false);

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
