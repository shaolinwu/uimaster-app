package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.shaolin.uimaster.app.R;

import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.data.UrlData;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/20.
 */

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle(R.string.register);
        initView();
    }

    private void initView() {
        webview.loadUrl(UrlData.REGISTER_URL);
        WebSettings wSet = webview.getSettings();
        wSet.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
