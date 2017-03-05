package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.aty.UpdateApp;
import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.DownFileBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.inter.IMainModuleView;

import java.util.List;

import static org.shaolin.uimaster.app.data.UrlData.GET_DOWNLOAD_RESOURCES;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class UpdateAppPresenterImpl extends BasePresenterImpl {
    private UpdateApp updater;
    private Context mContext;
    public UpdateAppPresenterImpl(UpdateApp updater, Context context) {
        super(null);
        this.updater = updater;
        this.mContext = context;
        OkHttpUtils.get()
                .url(UrlData.RESOURCE_URL + "download/appupdate.json")
                .build()
                .execute(this);
    }


    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject.getString("version");
            updater.showNoticeDialog();
            //TODO:
        } catch (JSONException e) {

        }
    }
}
