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
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;
import org.shaolin.uimaster.app.viewmodule.impl.HTMLPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/20.
 */

public class RegisterActivity extends BaseActivity implements IHTMLWebView {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;
    AjaxContext ajaxContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle(R.string.register);
        initView();
    }

    private void initView() {
        //webview.loadUrl(UrlData.REGISTER_URL);
        WebView parentWebView = webview;
        ajaxContext = WebFragment.initWebView(null, parentWebView, webview, this);

        showProgress();
        HTMLPresenterImpl presenter = new HTMLPresenterImpl(this, UrlData.REGISTER_URL);
    }

    public void received(String html) {
        webview.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
        hideProgress();
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
