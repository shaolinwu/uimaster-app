package org.shaolin.uimaster.app.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.shaolin.uimaster.app.R;

import org.shaolin.uimaster.app.base.BaseFragment;

/**
 * Created Administrator
 * on 2017/1/14
 * deprecated:
 */

public class WebFragment extends BaseFragment {

    private String url;
    private WebView mWebView;
    private AjaxContext ajaxContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = View.inflate(mContext, R.layout.web_fragment_layout, null);
        initData();
        initView();
    }

    private void initData() {
        url = (String) getArguments().get("url");
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webview);
        mWebView.loadUrl(url);
        WebView parentWebView = mWebView;
        ajaxContext = WebFragment.initWebView(this, parentWebView, mWebView, this.getActivity());
    }

    public static AjaxContext initWebView(BaseFragment f, WebView parent, WebView webView, Activity activity) {
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = webView.getSettings();
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        settings.setDefaultFontSize(15);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);
        int sysVersion = Build.VERSION.SDK_INT;

        // 高度自适应。
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);//关键点
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int mDensity = metrics.densityDpi;
        Log.d("maomao", "densityDpi = " + mDensity);
        if (mDensity == 240) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if(mDensity == 120) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }else if(mDensity == DisplayMetrics.DENSITY_XHIGH){
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else if (mDensity == DisplayMetrics.DENSITY_TV){
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }else{
            settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        }

        AjaxContext ajaxContext = new AjaxContext(f, parent, webView, activity);
        webView.addJavascriptInterface(ajaxContext, "_mobContext");

        return ajaxContext;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static WebFragment newWebFragment(Bundle args) {

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {

        mContext = null;
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

}
