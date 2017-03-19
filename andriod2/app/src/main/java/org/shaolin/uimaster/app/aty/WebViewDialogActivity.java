package org.shaolin.uimaster.app.aty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import java.util.Calendar;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/22.
 */

public class WebViewDialogActivity extends BaseActivity implements IHTMLWebView {

    public final static String BUNDLE_KEY_ARGS = "BUNDLE_KEY_ARGS";

    @BindView(R.id.webview)
    WebView webview;
    AjaxContext ajaxContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadWebView(getIntent().getBundleExtra(BUNDLE_KEY_ARGS));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    private void loadWebView(Bundle argus) {
        webview = (WebView)findViewById(R.id.webview);

        WebView parentWebView = AppManager.getAppManager().popWebView(argus.getString("parentWebView"));
        ajaxContext = WebFragment.initWebView(null, parentWebView, webview, this);

        if (!TextUtils.isEmpty(argus.getString("title"))){
            setToolBarTitle(argus.getString("title"));
        }

        String frameId = (String)argus.get("uiid");
        //showing ajax dialog
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE html><html><head><title>");
        sb.append(argus.get("title"));
        sb.append("</title>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("<meta http-equiv=\"x-ua-compatible\" content=\"ie=7\" />\n");
        sb.append("<meta name=\"viewport\" id=\"WebViewport\" content=\"width=device-width,initial-scale=1.0,minimum-scale=0.5,maximum-scale=1.0,user-scalable=1\" />\n");
        sb.append("<meta name=\"apple-mobile-web-app-title\" content=\"UIMaster\">\n");
        sb.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\">\n");
        sb.append("<meta name=\"apple-mobile-web-app-status-bar-style\" content=\"black-translucent\">\n");
        sb.append("<meta name=\"format-detection\" content=\"telephone=no\">\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("var USER_CONSTRAINT_IMG=\"/images/uimaster_constraint.gif\";\n");
        sb.append("var USER_CONSTRAINT_LEFT=false;\n");
        sb.append("var CURRENCY_GROUP_SEPARATOR=\",\";\n");
        sb.append("var CURRENCY_MONETARY_SEPARATOR=\".\";\n");
        sb.append("var CURTIME=");
        sb.append(String.valueOf(System.currentTimeMillis()));
        sb.append(";\nvar TZOFFSET=");
        sb.append(String.valueOf(Calendar.getInstance().getTimeZone().getOffset(System.currentTimeMillis())));
        sb.append(";\nvar WEB_CONTEXTPATH=\"/uimaster\";\n");
        sb.append("var RESOURCE_CONTEXTPATH=\"https://www.vogerp-res.com:8082/uimaster\";\n");
        sb.append("var FRAMEWRAP=\"/uimaster\";\n");
        sb.append("var IS_SERVLETMODE=true;\n");
        sb.append("var IS_MOBILEVIEW=true;\n");
        sb.append("var MOBILE_APP_TYPE=\"andriod\";\n");
        sb.append("var UPLOAD_CONTEXTPATH=\"").append(UrlData.UPLOAD_URL).append("\";\n");
        sb.append("var AJAX_SERVICE_URL=\"").append(UrlData.AJAX_SERVICE_URL).append("\";\n");
        sb.append("</script>\n");
        String root = FileData.APP_ROOT_FILE;
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/js/controls/swiper/swiper.css\" type=\"text/css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/css/jquery-dataTable.css\" type=\"text/css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/css/jquery-jstree.css\" type=\"text/css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/css/jquery-ui.css\" type=\"text/css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/css/jquery-mobile.css\" type=\"text/css\">\n");
        sb.append("<link rel=\"stylesheet\" href=\"file:///").append(root).append("/css/iumaster-mob.css\" type=\"text/css\">\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-ui.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-mobile.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-form.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/controls/swiper/swiper.jquery.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-dataTable.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-jstree.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/uimaster.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/uimaster-widget.js\"></script>\n");
        sb.append(argus.get("loadjs"));
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("<form action=\"#\" method=\"post\" name=\"everything\"");
        sb.append(" onsubmit=\"return false;\" _framePrefix=\"").append(argus.getString("_framePrefix")).append("\">\n");
        sb.append(argus.get("data"));
        sb.append("\n");
        sb.append("</form>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("getElementList();\n");
        sb.append("var defaultname = new Object();\n");
        sb.append("defaultname.initPageJs = function(){};\n");
        sb.append(argus.get("js"));
        sb.append("\ndefaultname.initPageJs();\n");
        sb.append("\n</script>\n</body>\n</html>");

        webview.loadDataWithBaseURL("", sb.toString(), "text/html", "UTF-8", "");
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

}
