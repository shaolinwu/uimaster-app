package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.os.Environment;
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

import com.baoyz.widget.PullRefreshLayout;

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
    private PullRefreshLayout refreshLayout;
    private String url;
    private boolean isRefreshing = false;

    private static final String absPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private void initView() {
        webview = (WebView)findViewById(R.id.webview);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
        WebView parentWebView = webview;
        ajaxContext = WebFragment.initWebView(null, null, webview, this);
        String cookies = PreferencesUtils.getString(this.getBaseContext(), ConfigData.USER_COOKIES,"");
        if (!TextUtils.isEmpty(cookies)){
            setWebViewCookies(cookies);
        }
        showProgress();
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(url)){
            if (getIntent().getStringExtra("static_res") != null) {
                webview.loadUrl(url);
            } else {
                HTMLPresenterImpl presenter = new HTMLPresenterImpl(this, url);
            }
        }
        if (!TextUtils.isEmpty(title)){
            setToolBarTitle(title);
        }
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                if (isRefreshing) {
                    return;
                }
                isRefreshing = true;
                showProgress();
                HTMLPresenterImpl presenter = new HTMLPresenterImpl(WebViewActivity.this, url);
            }
        });
    }

    public void received(String html) {
        html = html.replace("file://"+absPath+"/uimaster", "file://"+absPath+"/.uimaster");
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
        isRefreshing = false;
        ivLoading.clearAnimation();
        loadingLayout.setVisibility(View.GONE);
    }

    public void refreshComplete(){
        refreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        //TODO: send a http notification {serviceName:"closepage",_framePrefix:UIMaster.getFramePrefix()}
        super.onDestroy();
    }
}
