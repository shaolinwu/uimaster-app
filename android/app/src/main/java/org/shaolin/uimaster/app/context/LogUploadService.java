package org.shaolin.uimaster.app.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


import org.apache.http.Header;
import org.kymjs.kjframe.utils.FileUtils;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.util.StringUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class LogUploadService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final File log = FileUtils.getSaveFile("UIMaster", "UIMaster.log");
        String data = null;
        try {
            FileInputStream inputStream = new FileInputStream(log);
            data = StringUtils.toConvertString(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(data)) {
            RService.uploadLog(data, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int arg0, cz.msebera.android.httpclient.Header[] arg1, byte[] arg2) {
                    log.delete();
                    LogUploadService.this.stopSelf();
                }

                @Override
                public void onFailure(int arg0, cz.msebera.android.httpclient.Header[] arg1, byte[] arg2,
                                      Throwable arg3) {
                    LogUploadService.this.stopSelf();
                }
            });
        } else {
            LogUploadService.this.stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
