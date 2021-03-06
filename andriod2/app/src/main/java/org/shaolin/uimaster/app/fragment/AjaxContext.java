package org.shaolin.uimaster.app.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import org.shaolin.uimaster.app.customeview.BottomStyleDialog;
import org.shaolin.uimaster.app.customeview.BottomWebviewDialog;
import org.shaolin.uimaster.app.utils.FileLog;

import android.view.ContextThemeWrapper;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.aty.AppManager;
import org.shaolin.uimaster.app.aty.ChatActivity;
import org.shaolin.uimaster.app.aty.ImageActivity;
import org.shaolin.uimaster.app.aty.LoginActivity;
import org.shaolin.uimaster.app.aty.WebViewActivity;
import org.shaolin.uimaster.app.aty.WebViewDialogActivity;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.pay.alipay.PayResult;
import org.shaolin.uimaster.app.pay.wepay.PayManager;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.MD5Util;
import org.shaolin.uimaster.app.utils.StringUtils;
import org.shaolin.uimaster.app.utils.UrlParse;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

    private static final String absPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    public AjaxContext(BaseFragment f, WebView parentWebView, WebView myWebView, BaseActivity activity) {
        this.fragment = f;
        this.parentWebView = parentWebView;
        this.myWebView = myWebView;
        this.activity = activity;

        myWebView.setWebViewClient(new WebViewClientA());
        myWebView.setWebChromeClient(new WebChromeClientA());
        //myWebView.("file:///", data, "text/html", "UTF-8",null);
    }

    public WebView getWebView() {
        return myWebView;
    }

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        String content = response.body().string();
        return content;
    }

    @Override
    public void onError(Call call, Exception e) {
        FileLog.e("UIMaster", "Ajax call exception : " + e);
    }

    @Override
    public void onResponse(final String response) {
        try {
            String response0 = response.replace("file://" + absPath + "/uimaster", "file://" + absPath + "/.uimaster");
            final JSONArray array = new JSONArray(response0);
            int length = array.length();
            myWebView.loadUrl("javascript:UIMaster.ui.mask.close()");
            final List<JSONObject> loadJsItem = new ArrayList<JSONObject>();
            for (int i = 0; i < length; i++) {
                JSONObject item = array.getJSONObject(i);
                String jsHandler = item.getString("jsHandler");
                if ("closewindow".equals(jsHandler)) {
                    if (myWebView.getContext() instanceof Activity) {
                        ((Activity) myWebView.getContext()).finish();
                    } else if (activity instanceof BaseActivity){
                        ((BaseActivity) activity).finish();
                    }
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
                                        FileLog.w("UIMaster", "execute js command error: " + e.getMessage(), e);
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
        } catch (Exception e) {
            FileLog.w("UIMaster", "Failed to load data: ", e);
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
            JSONObject item1 = array.getJSONObject(i + 1);
            if ("openwindow".equals(item1.getString("jsHandler"))) {
                loadJsItem.add(item);//prepare for openwindow.
            } else {
                String uiid = item.getString("uiid");
                final String itemJson = item.toString();
                webView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify([" + itemJson + "]),'','200')");
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

            JSONObject details = new JSONObject(item.getString("sibling"));
            if (details != null && details.getBoolean("openInBottomDialog")) {
                BottomWebviewDialog bottomStyleDialog = new BottomWebviewDialog(this.activity, arguments);
                bottomStyleDialog.show();
            } else {
                Intent intent = new Intent(activity, WebViewDialogActivity.class);
                intent.putExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS, arguments);
                activity.startActivity(intent);
            }
        } else if ("form_refresh".equals(jsHandler) && activity instanceof WebViewDialogActivity) {
            //TODO: dialog update.

        } else if ("pay".equals(jsHandler)) {
            //TODO:
        } else {
            String uiid = item.getString("uiid");
            final String itemJson = item.toString();
            webView.loadUrl("javascript:UIMaster.cmdHandler(JSON.stringify([" + itemJson + "]),'','200')");
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
            FileLog.d("UIMaster", "url: " + url);
            //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            CookieManager cookieManager = CookieManager.getInstance();
            //WebView获取加载网页的cookie
            //String CookieStr = cookieManager.getCookie(url);

            super.onPageFinished(view, url);
            if (pageLoaded != null) {
                pageLoaded.run();
            }
            if (fragment != null) {
                ((WebFragment) fragment).hideProgress();
                ((WebFragment) fragment).refreshComplete();
            }
            if (view.getContext() != null && view.getContext() instanceof BaseActivity) {
                Activity activity = (Activity) view.getContext();
                ((BaseActivity) activity).hideProgress();
                if (activity instanceof WebViewActivity) {
                    ((WebViewActivity) activity).refreshComplete();
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
            FileLog.d("[" + consoleMessage.messageLevel() + "]", consoleMessage.message()
                    + " -- From line " + consoleMessage.lineNumber()
                    + " of " + consoleMessage.sourceId());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            FileLog.d("UIMaster", "WebView closed" + window.getOriginalUrl());
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
            } else if (activity != null) {
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
     * async: false,
     * url: AJAX_SERVICE_URL,
     * type: 'POST',
     * data:{_ajaxUserEvent: action==undefined?true:action,
     * _uiid: uiid,
     * _actionName: actionName,
     * _framePrefix: UIMaster.getFramePrefix(UIMaster.El(uiid).get(0)),
     * _actionPage: entityName,
     * _sync: UIMaster.ui.sync()},
     * beforeSend: UIMaster.ui.mask.open(),
     * success: UIMaster.cmdHandler
     * };
     *
     * @param jsonStr
     * @throws JSONException
     */
    @JavascriptInterface
    public void ajax(String jsonStr) {
        try {
            FileLog.d("UIMaster", "invoke ajax with data: " + jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            JSONObject data = json.getJSONObject("data");

            PostFormBuilder form = OkHttpUtils.post().url(URLData.AJAX_SERVICE_URL);
            Iterator<String> i = (Iterator<String>) data.keys();
            while (i.hasNext()) {
                String key = i.next();
                form.addParams(key, data.getString(key));
            }
            form.addParams("_appstore", Environment.getExternalStorageDirectory().getAbsolutePath());
            form.build().execute(this);
        } catch (JSONException e) {
            FileLog.w("UIMaster", "ajax invocation error: " + jsonStr);
        }
    }

    private String uploadFileUIID;

    @JavascriptInterface
    public void uploadImage(final String url, final String uiid, final String filePath) {
        this.uploadFileUIID = uiid;
        if (activity != null && activity.selectedUploadFile != null && activity.selectedUploadFile.length > 0) {
            File[] files = new File[activity.selectedUploadFile.length];
            for (int i = 0; i < activity.selectedUploadFile.length; i++) {
                files[i] = new File(activity.selectedUploadFile[i]);
            }
            UrlParse.uploadImage(this, activity, url, files, null);
            activity.selectedUploadFile = null;
        } else {
            UrlParse.uploadImage(this, activity, url, new File[]{new File(filePath)}, null);
        }
    }

    @JavascriptInterface
    public void uploadFile(final String url, final String uiid, final String filePath) {
        this.uploadFileUIID = uiid;
        if (activity != null && activity.selectedUploadFile != null) {
            File[] files = new File[activity.selectedUploadFile.length];
            for (int i = 0; i < activity.selectedUploadFile.length; i++) {
                files[i] = new File(activity.selectedUploadFile[i]);
            }
            UrlParse.uploadImage(this, activity, url, files, null);
            activity.selectedUploadFile = null;
        } else {
            UrlParse.uploadImage(this, activity, url, new File[]{new File(filePath)}, null);
        }
    }

    public void onProgress(final long totalBytes, final long remainingBytes, final boolean done) {
        final long percent = (totalBytes - remainingBytes) * 100 / totalBytes;
        FileLog.d("UIMaster", "uploading file percentage: " + percent);
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:defaultname." + uploadFileUIID + ".options.uploadProgress(null," + remainingBytes + "," + totalBytes + "," + percent + ");");
            }
        });
    }

    /**
     * this is a notification after callback.
     *
     * @param state 1 success, 0 fail
     */
    @JavascriptInterface
    public void fileUploaded(final int state) {
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:defaultname." + uploadFileUIID + ".appCallback(" + state + ");");
            }
        });
    }

    @JavascriptInterface
    public void downloadImage(final String url) {
        String output = "vogerp-output-" + ((int) (Math.random() * 10000)) + url.substring(url.lastIndexOf("."));
        File f = new File(FileData.APP_ROOT_FILE + "/download");
        if (!f.exists()) {
            f.mkdirs();
        } else if (!f.isDirectory() && f.canWrite()) {
            f.delete();
            f.mkdirs();
        }
        UrlParse.download(url, new File(f, output));
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "下载成功！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void close() {
        ((Activity) myWebView.getContext()).finish();
    }

    @JavascriptInterface
    public void addResource(final String url) {
        FileLog.d("UIMaster", "dynamic loading URL: " + url);
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
    public void openImage(final String url) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void openURLDialog(String title, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", URLData.RESOURCE_URL + url);
        intent.putExtra("static_res", "true");

        activity.startActivity(intent);
    }

    @JavascriptInterface
    public void openChatWindow(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Bundle arguments = new Bundle();
            arguments.putBoolean("isAdmin", jsonObject.getBoolean("isAdmin"));
            arguments.putString("sessionId", jsonObject.getString("sessionId"));
            arguments.putString("orderInfo", jsonObject.getString("orderInfo"));
            arguments.putString("price", jsonObject.getString("price"));
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
    public void registerSuccessNotify() {
        Toast.makeText(activity, "恭喜您的手机帐号注册成功！请登录加工达人。", Toast.LENGTH_LONG).show();
        this.close();
    }

    @JavascriptInterface
    public void showSelectDialog() {
        Bundle arguments = new Bundle();
        arguments.putString("js", "");
        arguments.putString("data", "");
        arguments.putString("uiid", "uiid");
        arguments.putString("_framePrefix", "");
        arguments.putString("title", "test");
        arguments.putString("loadjs", "");

        BottomWebviewDialog bottomStyleDialog = new BottomWebviewDialog(this.activity, arguments);
        bottomStyleDialog.show();
    }

    @JavascriptInterface
    public void appPay(String orderInfo, String paymethod) {
        if ("alipay".equalsIgnoreCase(paymethod)) {
            Toast.makeText(activity, "正常调起支付宝", Toast.LENGTH_SHORT).show();
            PayTask alipay = new PayTask(this.activity);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            PayResult payResult = new PayResult(result);
            /**
             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                Toast.makeText(activity, "您的支付宝已支付成功。", Toast.LENGTH_SHORT).show();
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                Toast.makeText(activity, "支付宝支付失败!请刷新页面重试，谢谢！", Toast.LENGTH_SHORT).show();
            }
        } else if ("wepay".equalsIgnoreCase(paymethod)) {
            try {
                JSONObject json = new JSONObject(orderInfo);
                PayReq req = new PayReq();
                req.appId = json.getString("appid");
                req.partnerId = json.getString("mch_id");
                req.prepayId = json.getString("prepay_id");
                req.nonceStr = StringUtils.genRandomAlphaBits(32);//json.getString("nonce_str");
                req.timeStamp = genTimeStamp() + "";
                req.packageValue = "Sign=WXPay";
                //req.sign			= json.getString("sign");

                //https://pay.weixin.qq.com/wiki/tools/signverify/
                //final SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
                StringBuffer keyValues = new StringBuffer();
                keyValues.append("appid=").append(req.appId);
                keyValues.append("&noncestr=").append(req.nonceStr);
                keyValues.append("&package=").append(req.packageValue);
                keyValues.append("&partnerid=").append(req.partnerId);
                keyValues.append("&prepayid=").append(req.prepayId);
                keyValues.append("&timestamp=").append(req.timeStamp);
                keyValues.append("&key=").append(json.getString("mch_skey"));

                // 调用获得签名的方法,这里直接把服务器返回来的sign给覆盖了,
                // 所以我不是很明白服务器为什么返回这个sign值,然后调起支付,mch_id基本上就可以了.
                req.sign = MD5Util.MD5Encode(keyValues.toString(), "UTF-8").toUpperCase();

                IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, req.appId, true);
                msgApi.registerApp(req.appId);
                boolean result = msgApi.sendReq(req);
                //Toast.makeText(activity, "正常调起微信支付: " + keyValues.toString() + " sign="+req.sign+", result: " + result, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, "微信支付调用异常!" + StringUtils.getException(e), Toast.LENGTH_SHORT).show();
            }
        } else {
            throw new UnsupportedOperationException("Unsupported payment method: " + paymethod);
        }
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
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
            socket = IO.socket(URLData.CHAT_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static void closeWebSocket(long userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("partyId", userId);
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

    /**
     * 设置Cookie
     *
     * @param url
     * @param cookie  格式：uid=21233 如需设置多个，需要多次调用
     */
    @JavascriptInterface
    public void synCookies(String url, String cookie) {
        CookieSyncManager.createInstance(myWebView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookie);//cookies是在HttpClient中获得的cookie
        CookieSyncManager.getInstance().sync();
    }


}
