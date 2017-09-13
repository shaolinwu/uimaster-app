package org.shaolin.uimaster.app.viewmodule.impl;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class HTMLPresenterImpl extends BasePresenterImpl<IHTMLWebView> {
    public HTMLPresenterImpl(IHTMLWebView view, String url) {
        super(view);
        // all page links only supports GET method from the server side.
//    PostFormBuilder form = OkHttpUtils.post().url(url);
//    form.addParams("_appstore", Environment.getExternalStorageDirectory().getAbsolutePath());
//    form.build().execute(this);
        String requestURL = url + "&_appstore=" + Environment.getExternalStorageDirectory().getAbsolutePath();
        //TODO: set request timeout
        OkHttpUtils.get().url(requestURL).build().execute(this);
    }

    @Override
    public void onError(Call call, Exception e) {
        Log.e("UIMaster","Exception ="+ e.toString());
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html><html><head><title>Oops! 打开本功能出错了。</title>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
        sb.append("<meta http-equiv=\"x-ua-compatible\" content=\"ie=7\" />");
        sb.append("<meta name=\"viewport\" id=\"WebViewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=0.5,maximum-scale=1.0,user-scalable=1\" />\n");
        sb.append("<meta name=\"apple-mobile-web-app-title\" content=\"UIMaster\">");
        sb.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">");
        sb.append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\">");
        sb.append("<meta name=\"format-detection\" content=\"telephone=no\">");
        sb.append("</head>");
        sb.append("<body><div>非常抱歉！打开本功能出错了。请您重新刷新本页面。</div>");
        sb.append("<div>异常信息： ").append(e.getMessage()).append("</div>");
        sb.append("</body></html>");
        mViewRef.get().received(sb.toString());
        mViewRef.get().hideProgress();
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        if (!TextUtils.isEmpty(response)){
            mViewRef.get().received(response);
        }
    }

    @Override
    public void onBefore(Request request) {

    }

    @Override
    public void onAfter() {
    }
}
