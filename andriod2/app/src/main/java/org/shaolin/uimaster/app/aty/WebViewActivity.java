package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.impl.HTMLPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/22.
 */

public class WebViewActivity extends BaseActivity implements IHTMLWebView {

    @BindView(R.id.webview)
    WebView webview;
    AjaxContext ajaxContext;
    private LinearLayout loadingLayout;
    private ImageView ivLoading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        loadWebView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private void initView() {
        webview = (WebView)findViewById(R.id.webview);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
        WebView parentWebView = webview;
        ajaxContext = WebFragment.initWebView(null, parentWebView, webview, this);
        String cookies = PreferencesUtils.getString(this.getBaseContext(), ConfigData.USER_COOKIES,"");
        if (!TextUtils.isEmpty(cookies)){
            setWebViewCookies(cookies);
        }
    }

    private void loadWebView() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(url)){
            //webview.loadUrl(url);

            showProgress();
            HTMLPresenterImpl presenter = new HTMLPresenterImpl(this, url);
        }
        if (!TextUtils.isEmpty(title)){
            setToolBarTitle(title);
        }
    }

    public void received(String html) {
        webview.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setWebViewCookies(String cookies){
        CookieSyncManager.createInstance(this.getBaseContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(getIntent().getStringExtra("url"), cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public void showProgress() {
        loadingLayout.setVisibility(View.VISIBLE);
        Animation mRotateAnim = AnimationUtils.loadAnimation(this, R.anim.loading_rotate);
        ivLoading.startAnimation(mRotateAnim);
    }

    @Override
    public void hideProgress() {
        ivLoading.clearAnimation();
        loadingLayout.setVisibility(View.GONE);
    }
}
