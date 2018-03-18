package org.shaolin.uimaster.app.customeview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.adpter.StyleAdapter;
import org.shaolin.uimaster.app.adpter.StyleItem;
import org.shaolin.uimaster.app.aty.AppManager;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;

public class BottomWebviewDialog extends Dialog {

    private WebView webview;
    private AjaxContext ajaxContext;

    private static final String absPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    private Bundle arguments;

    public BottomWebviewDialog(Context context, Bundle arguments) {
        // 在构造方法里, 传入主题
        super(context, R.style.BottomDialogStyle);
        // 拿到Dialog的Window, 修改Window的属性
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        // 获取Window的LayoutParams
        LayoutParams attributes = window.getAttributes();
        attributes.width = LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
        this.arguments = arguments;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_dialog);
        initView();
        initData(this.arguments);
    }

    private void initView() {
        webview = (WebView)findViewById(R.id.webview);
        WebView parentWebView = AppManager.getAppManager().popWebView(arguments.getString("parentWebView"));
        ajaxContext = WebFragment.initWebView(null, parentWebView, webview, null);
    }

    private void initData(Bundle argus) {

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
        sb.append("var UPLOAD_CONTEXTPATH=\"").append(URLData.UPLOAD_URL).append("\";\n");
        sb.append("var AJAX_SERVICE_URL=\"").append(URLData.AJAX_SERVICE_URL).append("\";\n");
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
        //useless now for jquery-form.js
        //sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-form.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/controls/swiper/swiper.jquery.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-dataTable.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/jquery-jstree.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/uimaster.js\"></script>\n");
        sb.append("<script type=\"text/javascript\" src=\"file:///").append(root).append("/js/uimaster-widget.js\"></script>\n");
        String loadjs = argus.get("loadjs").toString();
        loadjs = loadjs.replace("file://"+absPath+"/uimaster/", "file://"+absPath+"/.uimaster/");
        sb.append(loadjs);
        sb.append("</head>\n");
        sb.append("<body data-role=\"page\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||AJAX_EXCEPTION_REQUEST_WAIT\" msg=\"请求处理中，请稍候...\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||VERIFY_FAIL\" msg=\"校验不通过.\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||ALLOW_BLANK\" msg=\"不允许为空.\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||REGULAR_EXPRESSION\" msg=\"输入值不符合规定的格式.\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||MINIMUM_LENGTH\" msg=\"输入值的长度不符合要求.\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||MUST_CHECK\" msg=\"必须勾选中其中某项.\">\n");
        sb.append("<input type=\"hidden\" name=\"__resourcebundle\" value=\"Common||SELECT_VALUE\" msg=\"必须选择其中某项.\">\n");
        sb.append("<form action=\"#\" method=\"post\" name=\"everything\"");
        sb.append(" onsubmit=\"return false;\" _framePrefix=\"").append(argus.getString("_framePrefix")).append("\">\n");
        sb.append(argus.get("data"));
        sb.append("\n");
        sb.append("</form>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("var defaultname = new Object();\n");
        sb.append("defaultname.initPageJs = function(){};\n");
        sb.append("$(document).ready(function(){\n");
        sb.append("getElementList();\n");
        sb.append(argus.get("js"));
        sb.append("postInit();\n");
        sb.append("});\n</script>\n</body>\n</html>");

        webview.loadDataWithBaseURL("", sb.toString(), "text/html", "UTF-8", "");
    }

}
