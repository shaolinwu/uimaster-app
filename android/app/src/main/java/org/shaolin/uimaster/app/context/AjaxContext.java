package org.shaolin.uimaster.app.context;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.api.HttpClientService;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.SimpleBackPage;
import org.shaolin.uimaster.app.util.UIHelper;

import java.util.Iterator;

/**
 * Created by wushaol on 10/22/2015.
 */
public class AjaxContext {

    private final WebView myWebView;

    private final Activity activity;

    private Runnable pageLoaded;

    private Runnable pageClosed;

    public AjaxContext(WebView myWebView, Activity activity) {
        this.myWebView = myWebView;
        this.activity = activity;

        myWebView.setWebViewClient(new WebViewClientA());
        myWebView.setWebChromeClient(new WebChromeClientA());
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    class WebViewClientA extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("MyLog", "url: " + url);
            //UIHelper.showUrlRedirect(view.getContext(), url);
            return true;
        }
        public void onPageFinished(WebView view, String url) {
            Activity activity = (Activity) view.getContext();
            if (activity instanceof BaseActivity) {
                ((BaseActivity) activity).hideWaitDialog();
            }
            if (pageLoaded != null) {
                pageLoaded.run();
            }
        }
    }

    class WebChromeClientA extends WebChromeClient {

        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("[" + consoleMessage.messageLevel() + "]", consoleMessage.message()
                    + " -- From line " + consoleMessage.lineNumber()
                    + " of " + consoleMessage.sourceId());
            return true;
        }
        public void onCloseWindow(WebView window) {
            Log.d("WebView closed", window.getOriginalUrl());
            if (pageClosed != null) {
                pageClosed.run();
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
            Log.d("JS Invocation", jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            JSONObject data = json.getJSONObject("data");

            RequestParams params = new RequestParams();
            Iterator<String> i = (Iterator<String>) data.keys();
            while(i.hasNext()) {
                String key = i.next();
                params.put(key, data.getString(key));
            }

            HttpClientService.post(AppConfig.AJAX_SERVICE_URL, "", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBytes) {
                    String jsonStr = new String(responseBytes);
                    try {
                        JSONArray array = new JSONArray(jsonStr);
                        int length = array.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject item = array.getJSONObject(i);
                            String jsHandler = item.getString("jsHandler");
                            if ("sessiontimeout".equals(jsHandler)) {
                                UIHelper.showLoginActivity(activity);
                                break;
                            } else if ("nopermission".equals(jsHandler)) {
                                UIHelper.showNoPermission(activity);
                                break;
                            } else if ("error".equals(jsHandler)) {
                                //TODO: UIHelper.show
                                break;
                            } else if ("openwindow".equals(jsHandler)) {
                                Bundle arguments = new Bundle();
                                arguments.putString("dialog", "yes");
                                arguments.putString("js", item.getString("js"));
                                arguments.putString("data", item.getString("data"));
                                arguments.putString("uiid", item.getString("uiid"));
                                JSONObject dialogInfo = new JSONObject(item.getString("sibling"));
                                arguments.putString("title", dialogInfo.getString("title"));
                                arguments.putString("icon", dialogInfo.getString("icon"));
                                UIHelper.showSimpleBack(activity, SimpleBackPage.FUNCTION, arguments);
                            } else if ("closewindow".equals(jsHandler)) {
                                ((Activity)myWebView.getContext()).finish();
                                break;
                            } else {
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

                @Override
                public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                      Throwable arg3) {
                    Log.w("Failed to load data: ", arg3);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void addResource(final String url) {
        Log.d("dynamic loading URL: ", url);
        this.myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl(url);
            }
        });
    }

}
