package org.shaolin.uimaster.app.viewmodule.impl;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.utils.FileUtil;

import java.io.File;

import okhttp3.Call;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class DownFilePresenterImpl {

    public DownFilePresenterImpl(String url,String fileName) {

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(FileData.APP_ROOT_FILE, fileName)
                {
                    @Override
                    public void inProgress(float progress, long total) {
                        Log.e("linbin","progress =" + progress + "===" + "total =" + total);
                    }

                    @Override
                    public void inProgress(float progress){
                        Log.e("linbin","progress =" + progress);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Log.e("e =", e.toString());
                    }

                    @Override
                    public void onResponse(File file){
                       Runnable runnable = new ZipRunnable(file.getAbsolutePath(),FileData.APP_ROOT_FILE);
                        new Thread(runnable).start();
                    }
                });
    }


    public static class ZipRunnable implements Runnable {
        private String zipFileString;
        private String outPathString;

        public ZipRunnable(String zipFileString, String outPathString){
            this.zipFileString = zipFileString;
            this.outPathString = outPathString;
        }

        @Override
        public void run() {
            try {
                FileUtil.UnZipFolder(zipFileString,outPathString);
                File file = new File(zipFileString);
                if (file.exists()){
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
