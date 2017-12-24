package org.shaolin.uimaster.app.viewmodule.impl;

import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.viewmodule.inter.IMineView;

/**
 * Created by Administrator on 2017/1/20.
 */

public class LoginOutPresenterImpl extends BasePresenterImpl<IMineView> {

    public LoginOutPresenterImpl(IMineView view) {
        super(view);
        OkHttpUtils.get()
                .url(URLData.LOGIN_OUT_URL)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        mViewRef.get().logout();
    }
}
