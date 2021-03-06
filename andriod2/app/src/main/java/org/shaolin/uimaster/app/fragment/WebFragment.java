package org.shaolin.uimaster.app.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baoyz.widget.PullRefreshLayout;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.bean.CookiesBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.impl.HTMLPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created Administrator
 * on 2017/1/14
 * deprecated:
 */

public class WebFragment extends BaseFragment implements IHTMLWebView {

    private AjaxContext ajaxContext;
    private LinearLayout loadingLayout;
    private ImageView ivLoading;
    private PullRefreshLayout refreshLayout;

    private static final String absPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mView = View.inflate(mContext, R.layout.web_fragment_layout, null);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        url = (String) getArguments().get("url");
    }

    private void initView() {
        mWebView = (WebView) mView.findViewById(R.id.webview);
        loadingLayout = (LinearLayout) mView.findViewById(R.id.loading_layout);
        ivLoading = (ImageView) mView.findViewById(R.id.iv_loading);
        refreshLayout = (PullRefreshLayout) mView.findViewById(R.id.refresh_layout);
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
        //WebView parentWebView = mWebView;
        ajaxContext = WebFragment.initWebView(this, null, mWebView, (BaseActivity)this.getActivity());
        String cookies = PreferencesUtils.getString(getContext(), ConfigData.USER_COOKIES, "");
        if (!TextUtils.isEmpty(cookies)) {
            setWebViewCookies(cookies);
        }
        showProgress();
        HTMLPresenterImpl presenter = new HTMLPresenterImpl(this, url);
    }

    private boolean isRefreshing = false;
    private boolean isRefreshNextTime = false;

    private void initListener() {
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                manualRefresh();
            }
        });
    }

    /**
     * for re-login support.
     */
    public void refreshNextTime() {
        this.isRefreshNextTime = true;
    }

    public boolean isRefreshAgain() {
        if (this.isRefreshNextTime) {
            this.isRefreshNextTime = false;
            return true;
        }
        return false;
    }

    public void manualRefresh() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        if (loadingLayout == null) {
            //not initialized
            return;
        }
        showProgress();
        HTMLPresenterImpl presenter = new HTMLPresenterImpl(WebFragment.this, url);
    }

    public void received(String html) {
        //mWebView.loadUrl(url); please DON'T use this one.
        html = html.replace("file://"+absPath+"/uimaster", "file://"+absPath+"/.uimaster");
        mWebView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    public static AjaxContext initWebView(BaseFragment f, WebView parent, WebView webView, BaseActivity activity) {
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
        settings.setAllowContentAccess(true);
        // 高度自适应。
        settings.setLoadWithOverviewMode(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);//关键点
        DisplayMetrics metrics = new DisplayMetrics();
        if (activity != null && activity.getWindowManager() != null) {
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        }
        int mDensity = metrics.densityDpi;
        Log.d("maomao", "densityDpi = " + mDensity);
        if (mDensity == 240) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == 160) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else if (mDensity == 120) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (mDensity == DisplayMetrics.DENSITY_TV) {
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else {
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
        super.onDestroy();
        mContext = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshWebView(CookiesBean bean) {
        setWebViewCookies(bean.cookies);
        mWebView.loadUrl(url);
    }

    public void setWebViewCookies(String cookies) {
        CookieSyncManager.createInstance(mContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();//移除
        cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }


    @Override
    public void toast(String msg) {

    }

    @Override
    public void showProgress() {
        loadingLayout.setVisibility(View.VISIBLE);
        Animation mRotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.loading_rotate);
        ivLoading.startAnimation(mRotateAnim);
    }

    @Override
    public void hideProgress() {
        isRefreshing = false;
        ivLoading.clearAnimation();
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void refreshComplete(){
        refreshLayout.setRefreshing(false);
    }
}
