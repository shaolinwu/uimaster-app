package org.shaolin.uimaster.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.shaolin.uimaster.app.base.AppConfig;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.UrlParse;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wushaol on 2017/2/16.
 */
public class SyncServerResources extends Service{


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (FileUtil.checkSaveLocationExists()) {
            try {
                OkHttpUtils.get()
                        .url(UrlData.GET_RESOURCES_README)
                        .build()
                        .execute(new Callback<String>() {
                            @Override
                            public String parseNetworkResponse(Response response) throws Exception {
                                return response.body().string();
                            }

                            @Override
                            public void onError(Call call, Exception e) {
                                Log.w("SyncResourcesFailed", e);
                            }

                            @Override
                            public void onResponse(String response) {
                                //download server js, css & image resources.
                                Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
                                ArrayList<JsonObject> jsonObjects = new Gson().fromJson(response, type);
                                for (JsonObject jsonObject : jsonObjects) {
                                    UrlParse.download(UrlData.GET_DOWNLOAD_RESOURCES + jsonObject.get("resource").getAsString(),
                                            new File(FileUtil.getSDRoot() + "/uimaster", jsonObject.get("resourceType").getAsString()));
                                }
                            }
                        });
            } catch (Throwable e) {
                Log.w("SyncResourcesFailed", e);
            }
            SyncServerResources.this.stopSelf();
        }
        //接受传递过来的intent的数据等
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}


