package org.shaolin.uimaster.app.viewmodule.impl;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.viewmodule.inter.IFindPwdView;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/20.
 */

public class FindPwdPresenterImpl extends BasePresenterImpl<IFindPwdView> {

    public FindPwdPresenterImpl(IFindPwdView view, Map<String, String> values) {
        super(view);
        PostFormBuilder postForm = OkHttpUtils.post();
        for (Map.Entry<String, String> entry: values.entrySet()) {
            postForm.addParams(entry.getKey(), entry.getValue());
        }
        postForm.url(URLData.FINDPWD_URL).build().execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        //TODO:
        mViewRef.get().smsSent();
    }
}
