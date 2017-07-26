package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.LoginBean;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.viewmodule.inter.ILoginView;

import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/1/20.
 */

public class LoginPresenterImpl extends BasePresenterImpl<ILoginView> {

    public LoginPresenterImpl(ILoginView view, Map<String, String> values) {
        super(view);
        PostFormBuilder postForm = OkHttpUtils.post();
        for (Map.Entry<String, String> entry: values.entrySet()) {
            postForm.addParams(entry.getKey(), entry.getValue());
        }
        postForm.url(URLData.LOGIN_URL).build().execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);

        if (!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            LoginBean bean = gson.fromJson(response, LoginBean.class);
            CookieJarImpl cookieJar = (CookieJarImpl) OkHttpUtils.getInstance().getOkHttpClient().cookieJar();
            HttpUrl httpUrl = HttpUrl.parse(URLData.LOGIN_URL);
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
