package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.AdBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;

import okhttp3.Request;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class AdPresenterImpl extends BasePresenterImpl{
    private Context context;

    public AdPresenterImpl(Context context) {
        super();
        this.context = context;
        OkHttpUtils.get()
                .url(URLData.AD_URL)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        AdBean bean = (new Gson()).fromJson(response,AdBean.class);
        if (!TextUtils.isEmpty(bean.path)){
            StringBuilder sb = new StringBuilder(URLData.RESOURCE_URL_1);
            sb.append(bean.path);
            String adVersion = PreferencesUtils.getString(context, ConfigData.AD_VERSION);
            if (TextUtils.isEmpty(adVersion) || !adVersion.equals(bean.version)){
                AdFilePresenterImpl adFilePresenter = new AdFilePresenterImpl(context,sb.toString(),bean.version + ".zip",bean.version);
            }
        }

    }

    @Override
    public void onBefore(Request request) {

    }

    @Override
    public void onAfter() {
    }
}
