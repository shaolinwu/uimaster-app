package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.aty.MyApplication;
import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.AppInfoUtil;
import org.shaolin.uimaster.app.viewmodule.inter.IUpdateAppView;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class UpdateAppPresenterImpl extends BasePresenterImpl<IUpdateAppView> {
    public UpdateAppPresenterImpl(IUpdateAppView view) {
        super(view);
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
            String version = jsonObject.getString("version");

            if (!TextUtils.isEmpty(version) && !version.equals(AppInfoUtil.getAppVersionName(MyApplication.mContext))){
                mViewRef.get().showUpdateAppDialog(jsonObject);
            }

        } catch (JSONException e) {

        }
    }
}
