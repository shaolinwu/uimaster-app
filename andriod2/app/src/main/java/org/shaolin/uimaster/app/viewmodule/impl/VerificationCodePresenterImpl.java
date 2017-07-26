package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.VerificationCodeBean;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.viewmodule.inter.IVerificationCodeView;

/**
 * Created by Administrator on 2017/1/18.
 */

public class VerificationCodePresenterImpl extends BasePresenterImpl<IVerificationCodeView> {
    public VerificationCodePresenterImpl(IVerificationCodeView view) {
        super(view);
        OkHttpUtils.get()
                .url(URLData.GET_VERIFICATION_CODE)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        if (!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            VerificationCodeBean bean = gson.fromJson(response,VerificationCodeBean.class);
            if (bean != null){
                mViewRef.get().showVerificationCode(bean);
            }
        }
    }
}
