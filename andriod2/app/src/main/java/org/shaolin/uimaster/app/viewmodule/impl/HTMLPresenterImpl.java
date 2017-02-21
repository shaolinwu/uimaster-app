package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.MainModuleBean;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;
import org.shaolin.uimaster.app.viewmodule.inter.IMainModuleView;

import java.util.List;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class HTMLPresenterImpl extends BasePresenterImpl<IHTMLWebView> {
    public HTMLPresenterImpl(IHTMLWebView view, String url) {
        super(view);
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        if (!TextUtils.isEmpty(response)){
            mViewRef.get().received(response);
        }
    }
}
