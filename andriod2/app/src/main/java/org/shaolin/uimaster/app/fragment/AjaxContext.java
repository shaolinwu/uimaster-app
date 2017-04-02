package org.shaolin.uimaster.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
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

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.aty.AppManager;
import org.shaolin.uimaster.app.aty.ChatActivity;
import org.shaolin.uimaster.app.aty.LoginActivity;
import org.shaolin.uimaster.app.aty.WebViewActivity;
import org.shaolin.uimaster.app.aty.WebViewDialogActivity;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.pay.alipay.PayActivity;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.UrlParse;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by wushaol on 10/22/2015.
 */
public class AjaxContext extends Callback<String> {

    private final WebView parentWebView;

    private final WebView myWebView;

    private final BaseActivity activity;

    private final BaseFragment fragment;

    private Runnable pageLoaded;

    private Runnable pageClosed;

    public AjaxContext(BaseFragment f, WebView parentWebView, WebView myWebView, BaseActivity activity) {
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
            final JSONArray array = new JSONArray(response);
            int length = array.length();
            final List<JSONObject> loadJsItem = new ArrayList<JSONObject>();
            for (int i = 0; i < length; i++) {
                JSONObject item = array.getJSONObject(i);
                String jsHandler = item.getString("jsHandler");
                if ("closewindow".equals(jsHandler)) {
                    ((Activity) myWebView.getContext()).finish();
                    if (((i + 1) < array.length()) && parentWebView != null) {
                        final int start = i + 1;
                        parentWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                loadJsItem.clear();
                                int start0 = start;
                                for (; start0 < array.length(); start0++) {
                                    try {
                                        handle(parentWebView, start0, array, array.getJSONObject(start0), loadJsItem);
                                    } catch (JSONException e) {
                                        Log.w("UIMaster", "execute js command error: "+ e.getMessage(), e);
                                    }
                                }
                            }
                        });
                        break;
                    }
                } else {
                    handle(myWebView, i, array, item, loadJsItem);
                }
            }
        } catch (Exception e){
            Log.w("UIMaster", "Failed to load data: ", e);
        }
    }

    private void handle(final WebView webView, final int i, final JSONArray array,
                        final JSONObject item, List<JSONObject> loadJsItem) throws JSONException {
        String jsHandler = item.getString("jsHandler");
        if ("sessiontimeout".equals(jsHandler)) {
            Toast.makeText(activity, "您的在线会话过期，请重新登录！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        } else if ("nopermission".equals(jsHandler)) {
            Toast.makeText(activity, "对不起！您没有访问权限！", Toast.LENGTH_SHORT).show();
        } else if ("load_js".equals(jsHandler)) {
            JSONObject item1 = array.getJSONObject(i+1);
            if ("openwindow".equals(item1.getString("jsHandler"))) {
                loadJsItem.add(item);//prepare for openwindow.
            } else {
                String uiid = item.getString("uiid");
                final String itemJson = item.toString();
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify(["+itemJson+"]),'','200')");
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
            if (loadJsItem != null && loadJsItem.size() > 0) {
                arguments.putString("loadjs", loadJsItem.get(0).getString("data"));
            }
            arguments.putString("parentWebView", webView.hashCode() + "");
            AppManager.getAppManager().addWebWiew(webView.hashCode() + "", webView);

            Intent intent = new Intent(activity, WebViewDialogActivity.class);
            intent.putExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS, arguments);

            activity.startActivity(intent);
        } else if ("pay".equals(jsHandler)) {
            Bundle arguments = new Bundle();
            arguments.putString("js", item.getString("js"));
            arguments.putString("data", item.getString("data"));
            arguments.putString("uiid", item.getString("uiid"));
            arguments.putString("_framePrefix", item.getString("frameInfo"));

            Intent intent = new Intent(activity, PayActivity.class);
            intent.putExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS, arguments);
            activity.startActivity(intent);
        } else {
            String uiid = item.getString("uiid");
            final String itemJson = item.toString();
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify(["+itemJson+"]),'','200')");
                }
            });
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
            if (fragment != null){
                ((WebFragment)fragment).hideProgress();
                ((WebFragment)fragment).refreshComplete();
            }
            if (activity != null) {
                ((BaseActivity)activity).hideProgress();
                if (activity instanceof WebViewActivity){
                    ((WebViewActivity)activity).refreshComplete();
                }
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
            if (activity.mFileUploadCallbackFirst != null) {
                activity.mFileUploadCallbackFirst.onReceiveValue(null);
            }
            activity.mFileUploadCallbackFirst = fileUploadCallbackFirst;

            if (activity.mFileUploadCallbackSecond != null) {
                activity.mFileUploadCallbackSecond.onReceiveValue(null);
            }
            activity.mFileUploadCallbackSecond = fileUploadCallbackSecond;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");

            if (fragment != null) {
                fragment.startActivityForResult(createDefaultOpenableIntent(), 30);
            } else if (activity != null){
                activity.startActivityForResult(createDefaultOpenableIntent(), 30);
            }
        }

        protected String getFileUploadPromptLabel() {
            return "选择一个文件";
        }

    }

    private Intent createDefaultOpenableIntent() {
        // Create and return a chooser with the default OPENABLE
        // actions including the camera, camcorder and sound
        // recorder where available.
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");

        Intent chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(),
                createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "File Chooser");
        return chooser;
    }

    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File externalDataDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() +
                File.separator + "browser-photos");
        cameraDataDir.mkdirs();
        String mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator +
                System.currentTimeMillis() + ".jpg";
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
        return cameraIntent;
    }

    private Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
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
            Log.d("UIMaster", "invoke ajax with data: " + jsonStr);
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

    private String uploadFileUIID;

    @JavascriptInterface
    public void uploadImage(final String url, final String uiid, final String filePath) {
        this.uploadFileUIID = uiid;
        if (activity != null && activity.selectedUploadFile != null) {
            UrlParse.uploadImage(this, activity, url, new File(activity.selectedUploadFile), null);
        } else {
            UrlParse.uploadImage(this, activity, url, new File(filePath), null);
        }
    }

    @JavascriptInterface
    public void uploadFile(final String url, final String uiid, final String filePath) {
        this.uploadFileUIID = uiid;
        if (activity != null && activity.selectedUploadFile != null) {
            UrlParse.uploadImage(this, activity, url, new File(activity.selectedUploadFile), null);
        } else {
            UrlParse.uploadImage(this, activity, url, new File(filePath), null);
        }
    }

    public void onProgress(final long totalBytes,final long remainingBytes, final boolean done) {
        final long percent = (totalBytes - remainingBytes) * 100 / totalBytes;
        Log.d("UIMaster", "uploading file percentage: " + percent);
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:defaultname."+uploadFileUIID+".options.uploadProgress(null,"+remainingBytes+","+totalBytes+","+percent+");");
            }
        });
    }

    /**
     * this is a notification after callback.
     * @param state 1 success, 0 fail
     */
    @JavascriptInterface
    public void fileUploaded(final int state) {
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:defaultname."+uploadFileUIID+".appCallback("+state+");");
            }
        });
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
                if (url.endsWith(".js")) {
                    myWebView.loadData(FileUtil.read(myWebView.getContext(), url), "text/javascript", "UTF-8");
                } else if (url.endsWith(".css")) {
                    myWebView.loadData(FileUtil.read(myWebView.getContext(), url), "text/css", "UTF-8");
                } else {
                    //unsupported!
                }
            }
        });
    }

    @JavascriptInterface
    public void openChatWindow(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Bundle arguments = new Bundle();
            arguments.putBoolean("isAdmin", jsonObject.getBoolean("isAdmin"));
            arguments.putString("sessionId", jsonObject.getString("sessionId"));
            arguments.putLong("orgId", jsonObject.getLong("orgId"));
            arguments.putLong("taskId", jsonObject.getLong("taskId"));
            arguments.putLong("sentPartyId", jsonObject.getLong("sentPartyId"));
            arguments.putLong("receivedPartyId", jsonObject.getLong("receivedPartyId"));
            arguments.putString("sentPartyName", jsonObject.getString("sentPartyName"));
            arguments.putString("recievedPartyName", jsonObject.getString("recievedPartyName"));
            Intent intent = new Intent(this.activity, ChatActivity.class);
            intent.putExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS, arguments);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public int getScreenHeight() {
        return 0;
    }

    @JavascriptInterface
    public String getServerHost() {
        return "www.vogerp.com";
    }

    public static Socket socket = null;

    public static Socket getWebService() {
        if (socket != null) {
            return socket;
        }
        try {
            socket = IO.socket(UrlData.CHAT_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static void closeWebSocket(long userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("partyId",userId);
            socket.emit("unregister", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.close();
        socket = null;
    }

    @JavascriptInterface
    public Object getWebSocket() {
        return socket;
    }
}
