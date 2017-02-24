package org.shaolin.uimaster.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.aty.LoginActivity;
import org.shaolin.uimaster.app.aty.WebViewActivity;
import org.shaolin.uimaster.app.aty.WebViewDialogActivity;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.data.UrlData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wushaol on 10/22/2015.
 */
public class AjaxContext extends Callback<String> {

    private final WebView parentWebView;

    private final WebView myWebView;

    private final Activity activity;

    private final BaseFragment fragment;

    /** File upload callback for platform versions prior to Android 5.0 */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /** File upload callback for Android 5.0+ */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;

    private Runnable pageLoaded;

    private Runnable pageClosed;

    public AjaxContext(BaseFragment f, WebView parentWebView, WebView myWebView, Activity activity) {
        this.fragment = f;
        this.parentWebView = myWebView;
        this.myWebView = myWebView;
        this.activity = activity;

        myWebView.setWebViewClient(new WebViewClientA());
        myWebView.setWebChromeClient(new WebChromeClientA());
        //myWebView.loadDataWithBaseURL("file:///", data, "text/html", "UTF-8",null);
    }

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        String content = response.body().string();
        return content;
    }

    @Override
    public void onError(Call call, Exception e) {
        Log.e("UIMaster", "Ajax call exception : " + e);
    }

    @Override
    public void onResponse(final String response) {
        try {
            JSONArray array = new JSONArray(response);
            int length = array.length();
            JSONObject loadJsItem = null;
            for (int i = 0; i < length; i++) {
                JSONObject item = array.getJSONObject(i);
                String jsHandler = item.getString("jsHandler");
                if ("sessiontimeout".equals(jsHandler)) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    break;
                } else if ("nopermission".equals(jsHandler)) {
                    Toast.makeText(activity, "对不起！您没有访问权限！", Toast.LENGTH_SHORT).show();
                    break;
                } else if ("load_js".equals(jsHandler)) {
                    JSONObject item1 = array.getJSONObject(i+1);
                    if ("openwindow".equals(item1.getString("jsHandler"))) {
                        loadJsItem = item;//prepare for openwindow.
                    } else {
                        String uiid = item.getString("uiid");
                        final String itemJson = item.toString();
                        myWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                myWebView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify(["+itemJson+"]),'','200')");
                            }
                        });
                    }
                } else if ("openwindow".equals(jsHandler)) {
                    Bundle arguments = new Bundle();
                    arguments.putString("dialog", "yes");
                    arguments.putString("js", item.getString("js"));
                    arguments.putString("data", item.getString("data"));
                    arguments.putString("uiid", item.getString("uiid"));
                    arguments.putString("_framePrefix", item.getString("frameInfo"));
                    JSONObject dialogInfo = new JSONObject(item.getString("sibling"));
                    arguments.putString("title", dialogInfo.getString("title"));
                    arguments.putString("icon", dialogInfo.getString("icon"));
                    if (loadJsItem != null) {
                        arguments.putString("loadjs", loadJsItem.getString("data"));
                    }

                    Intent intent = new Intent(activity, WebViewDialogActivity.class);
                    intent.putExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS, arguments);
                    activity.startActivity(intent);
                } else if ("closewindow".equals(jsHandler)) {
                    ((Activity)myWebView.getContext()).finish();
                    break;
                } else {
                    // find out the parent webview if neccesary.
                    // parentWebView.;
                    String uiid = item.getString("uiid");
                    final String itemJson = item.toString();
                    myWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify(["+itemJson+"]),'','200')");
                        }
                    });
                }
            }
        } catch (Exception e){
            Log.w("Failed to load data: ", e);
        }
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    class WebViewClientA extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("UIMaster", "url: " + url);
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Activity activity = (Activity) view.getContext();

            if (pageLoaded != null) {
                pageLoaded.run();
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors.
        }
    }

    class WebChromeClientA extends WebChromeClient {

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("[" + consoleMessage.messageLevel() + "]", consoleMessage.message()
                    + " -- From line " + consoleMessage.lineNumber()
                    + " of " + consoleMessage.sourceId());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Log.d("UIMaster", "WebView closed" + window.getOriginalUrl());
            if (pageClosed != null) {
                pageClosed.run();
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, null);
        }

        // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileInput(uploadMsg, null);
        }

        // file upload callback (Android 5.0 (API level 21) -- current) (public method)
        @SuppressWarnings("all")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            openFileInput(null, filePathCallback);
            return true;
        }

        @SuppressLint("NewApi")
        protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond) {
            if (mFileUploadCallbackFirst != null) {
                mFileUploadCallbackFirst.onReceiveValue(null);
            }
            mFileUploadCallbackFirst = fileUploadCallbackFirst;

            if (mFileUploadCallbackSecond != null) {
                mFileUploadCallbackSecond.onReceiveValue(null);
            }
            mFileUploadCallbackSecond = fileUploadCallbackSecond;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");

            fragment.startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), 30);
        }

        protected String getFileUploadPromptLabel() {
            return "选择一个文件";
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 30) {
            //file chooser.
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    }
                    else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                        }
                        catch (Exception e) {
                            dataUris = null;
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            }
            else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                }
                else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

    public void addPageLoadedListener(Runnable listener) {
        this.pageLoaded = listener;
    }

    public void addPageClosedListener(Runnable listener) {
        this.pageClosed = listener;
    }

    /**
     * var opt = {
     async: false,
     url: AJAX_SERVICE_URL,
     type: 'POST',
     data:{_ajaxUserEvent: action==undefined?true:action,
     _uiid: uiid,
     _actionName: actionName,
     _framePrefix: UIMaster.getFramePrefix(UIMaster.El(uiid).get(0)),
     _actionPage: entityName,
     _sync: UIMaster.ui.sync()},
     beforeSend: UIMaster.ui.mask.open(),
     success: UIMaster.cmdHandler
     };
     * @param jsonStr
     * @throws JSONException
     */
    @JavascriptInterface
    public void ajax(String jsonStr) {
        try {
            Log.d("UIMaster", "ajax invocation " + jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            JSONObject data = json.getJSONObject("data");

            PostFormBuilder form = OkHttpUtils.post().url(UrlData.AJAX_SERVICE_URL);
            Iterator<String> i = (Iterator<String>) data.keys();
            while(i.hasNext()) {
                String key = i.next();
                form.addParams(key, data.getString(key));
            }
            form.addParams("_appstore", Environment.getExternalStorageDirectory().getAbsolutePath());
            form.build().execute(this);
        } catch (JSONException e) {
            Log.w("UIMaster", "ajax invocation error: " + jsonStr);
        }
    }

    @JavascriptInterface
    public void close(){
        ((Activity)myWebView.getContext()).finish();
    }

    @JavascriptInterface
    public void addResource(final String url) {
        Log.d("UIMaster", "dynamic loading URL: " + url);
        this.myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl(url);
            }
        });
    }

    @JavascriptInterface
    public int getScreenHeight() {
        return 0;
    }

    @JavascriptInterface
    public String getServerHost() {
        return "www.vogerp.com";
    }


    @JavascriptInterface
    public Object getWebSocket() {
        return null;
    }

    public void closeWebSocket() {

    }

    private Object connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://wwww.vogerp.com:8090/uimaster/wschart");
        } catch (URISyntaxException e) {
            Log.w(e.getMessage(), e);
            return null;
        }

        return null;
    }
}
