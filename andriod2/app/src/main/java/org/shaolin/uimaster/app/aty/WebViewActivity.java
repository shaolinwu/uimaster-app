package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.shaolin.uimaster.app.R;

import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/22.
 */

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webview;
    AjaxContext ajaxContext;

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

        WebView parentWebView = webview;
        ajaxContext = WebFragment.initWebView(null, parentWebView, webview, this);
    }

    private void loadWebView() {
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(url)){
            webview.loadUrl(url);
        }
        if (!TextUtils.isEmpty(title)){
            setToolBarTitle(title);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
