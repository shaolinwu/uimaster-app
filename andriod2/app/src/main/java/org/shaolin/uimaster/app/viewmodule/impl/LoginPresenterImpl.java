package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.LoginBean;
import org.shaolin.uimaster.app.viewmodule.inter.ILoginView;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/1/20.
 */

public class LoginPresenterImpl extends BasePresenterImpl<ILoginView> {

    private String url;
    public LoginPresenterImpl(ILoginView view, String url) {
        super(view);
        this.url = url;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);

        if (!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            LoginBean bean = gson.fromJson(response,LoginBean.class);
            CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
            HttpUrl httpUrl = HttpUrl.parse(url);
            List<Cookie> allCookies = ((MemoryCookieStore)cookieJar.getCookieStore()).get(httpUrl);
            if (allCookies != null && allCookies.size() != 0){
                bean.cookies = allCookies.get(0).toString();
            }
            if (bean != null){
                mViewRef.get().loginResult(bean);
            }
        }
    }
}
