package org.shaolin.uimaster.app.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.dtr.zxing.activity.CaptureActivity;


import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.context.AppConfig;
import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.base.BaseListFragment;
import org.shaolin.uimaster.app.bean.Active;
import org.shaolin.uimaster.app.bean.Comment;
import org.shaolin.uimaster.app.bean.Constants;
import org.shaolin.uimaster.app.bean.Notice;
import org.shaolin.uimaster.app.bean.ShakeObject;
import org.shaolin.uimaster.app.bean.SimpleBackPage;
import org.shaolin.uimaster.app.context.AjaxContext;
import org.shaolin.uimaster.app.fragment.CommentFrament;
import org.shaolin.uimaster.app.interf.ICallbackResult;
import org.shaolin.uimaster.app.interf.OnWebViewImageListener;
import org.shaolin.uimaster.app.service.DownloadService;
import org.shaolin.uimaster.app.service.NoticeService;
import org.shaolin.uimaster.app.service.DownloadService.DownloadBinder;
import org.shaolin.uimaster.app.ui.DetailActivity;
import org.shaolin.uimaster.app.ui.EventLocationActivity;
import org.shaolin.uimaster.app.ui.ImagePreviewActivity;
import org.shaolin.uimaster.app.ui.LoginActivity;
import org.shaolin.uimaster.app.ui.SimpleBackActivity;
import org.shaolin.uimaster.app.widget.AvatarView;

/**
 * 界面帮助类
 * 
 * @author 
 * @version 创建时间：2014年10月10日 下午3:33:36
 * 
 */
public class UIHelper {

    public static final String WEB_LOAD_IMAGES = "<script type=\"text/javascript\"> var allImgUrls = getAllImgSrc(document.body.innerHTML);</script>";

    private static final String SHOWIMAGE = "ima-api:action=showImage&data=";

    /**
     * 显示登录界面
     * 
     * @param context
     */
    public static void showLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void showNoPermission(Context context) {
        //Intent intent = new Intent(context, LoginActivity.class);
        //context.startActivity(intent);
    }

    /**
     * 显示Team界面
     * 
     * @param context
     */
    public static void showTeamMainActivity(Context context) {
        //Intent intent = new Intent(context, TeamMainActivity.class);
        //context.startActivity(intent);
    }

    /**
     * 显示新闻详情
     * 
     * @param context
     * @param newsId
     */
    public static void showNewsDetail(Context context, int newsId,
            int commentCount) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", newsId);
        intent.putExtra("comment_count", commentCount);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_NEWS);
        context.startActivity(intent);
    }

    /**
     * 显示博客详情
     * 
     * @param context
     * @param blogId
     */
    public static void showBlogDetail(Context context, int blogId, int count) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", blogId);
        intent.putExtra("comment_count", count);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_BLOG);
        context.startActivity(intent);
    }

    /**
     * 显示帖子详情
     * 
     * @param context
     * @param postId
     */
    public static void showPostDetail(Context context, int postId, int count) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", postId);
        intent.putExtra("comment_count", count);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_POST);
        context.startActivity(intent);
    }

    /**
     * 显示活动详情
     * 
     * @param context
     * @param eventId
     */
    public static void showEventDetail(Context context, int eventId) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", eventId);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_EVENT);
        context.startActivity(intent);
    }

    /**
     * 显示软件详情
     *
     * @param context
     * @param ident
     */
    public static void showSoftwareDetail(Context context, String ident) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("ident", ident);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
    }

    public static void showSoftwareDetailById(Context context, int id) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("ident", "");
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_SOFTWARE);
        context.startActivity(intent);
    }

    /**
     * 动态点击跳转到相关新闻、帖子等
     * 
     * @param context context
     * @param active 动态实体类
     *            0其他 1新闻 2帖子 3动弹 4博客
     */
    public static void showActiveRedirect(Context context, Active active) {
        String url = active.getUrl();
        // url为空-旧方法
        if (StringUtils.isEmpty(url)) {
            int id = active.getObjectId();
            int catalog = active.getCatalog();
            switch (catalog) {
            case Active.CATALOG_OTHER:
                // 其他-无跳转
                break;
            case Active.CATALOG_NEWS:
                showNewsDetail(context, id, active.getCommentCount());
                break;
            case Active.CATALOG_POST:
                showPostDetail(context, id, active.getCommentCount());
                break;
            case Active.CATALOG_BLOG:
                showBlogDetail(context, id, active.getCommentCount());
                break;
            default:
                break;
            }
        } else {
            showUrlRedirect(context, url);
        }
    }

    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    @JavascriptInterface
    public static AjaxContext initWebView(WebView webView, Activity activity) {
	    webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }

        AjaxContext ajaxContext = new AjaxContext(webView, activity);
        webView.addJavascriptInterface(ajaxContext, "_mobContext");
        return ajaxContext;
    }

    /**
     * 添加网页的点击图片展示支持
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    @JavascriptInterface
    public static void addWebImageShow(final Context cxt, WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new OnWebViewImageListener() {
            @Override
            @JavascriptInterface
            public void showImagePreview(String bigImageUrl) {
                if (bigImageUrl != null && !StringUtils.isEmpty(bigImageUrl)) {
                    UIHelper.showImagePreview(cxt, new String[]{bigImageUrl});
                }
            }
        }, "mWebViewImageListener");
    }

    public static String setHtmlCotentSupportImagePreview(String body) {
        // 读取用户设置：是否加载文章图片--默认有wifi下始终加载图片
        if (AppContext.get(AppConfig.KEY_LOAD_IMAGE, true)
                || TDevice.isWifiOpen()) {
            // 过滤掉 img标签的width,height属性
            body = body.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
            body = body.replaceAll("(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
            // 添加点击图片放大支持
            // 添加点击图片放大支持
            body = body.replaceAll("(<img[^>]+src=\")(\\S+)\"",
                    "$1$2\" onClick=\"showImagePreview('$2')\"");
        } else {
            // 过滤掉 img标签
            body = body.replaceAll("<\\s*img\\s+([^>]*)\\s*>", "");
        }
        return body;
    }

    /**
     * 摇一摇点击跳转
     * 
     * @param obj
     */
    public static void showUrlShake(Context context, ShakeObject obj) {
        if (StringUtils.isEmpty(obj.getUrl())) {
            if (ShakeObject.RANDOMTYPE_NEWS.equals(obj.getRandomtype())) {
                UIHelper.showNewsDetail(context,
                        StringUtils.toInt(obj.getId()),
                        StringUtils.toInt(obj.getCommentCount()));
            }
        } else {
            if (!StringUtils.isEmpty(obj.getUrl())) {
                UIHelper.showUrlRedirect(context, obj.getUrl());
            }
        }
    }

    /**
     * url跳转
     * 
     * @param context
     * @param url
     */
    public static void showUrlRedirect(Context context, String url) {
        if (url == null)
            return;
        if (url.contains("webflow.do?")) {
            int id = StringUtils.toInt(url.substring(url.lastIndexOf('/') + 1));
            UIHelper.showEventDetail(context, id);
            return;
        }

        if (url.startsWith(SHOWIMAGE)) {
            String realUrl = url.substring(SHOWIMAGE.length());
            try {
                JSONObject json = new JSONObject(realUrl);
                int idx = json.optInt("index");
                String[] urls = json.getString("urls").split(",");
                showImagePreview(context, idx, urls);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        URLsUtils urls = URLsUtils.parseURL(url);
        if (urls != null) {
            showLinkRedirect(context, urls.getObjType(), urls.getObjId(),
                    urls.getObjKey());
        } else {
            openBrowser(context, url);
        }
    }

    public static void showLinkRedirect(Context context, int objType,
            int objId, String objKey) {
        switch (objType) {
        case URLsUtils.URL_OBJ_TYPE_NEWS:
            showNewsDetail(context, objId, -1);
            break;
        case URLsUtils.URL_OBJ_TYPE_QUESTION:
            showPostDetail(context, objId, 0);
            break;
        case URLsUtils.URL_OBJ_TYPE_SOFTWARE:
            showSoftwareDetail(context, objKey);
            break;
        case URLsUtils.URL_OBJ_TYPE_ZONE:
            showUserCenter(context, objId, objKey);
            break;
        case URLsUtils.URL_OBJ_TYPE_BLOG:
            showBlogDetail(context, objId, 0);
            break;
        case URLsUtils.URL_OBJ_TYPE_OTHER:
            openBrowser(context, objKey);
            break;
        case URLsUtils.URL_OBJ_TYPE_TEAM:
            openSysBrowser(context, objKey);
            break;
        case URLsUtils.URL_OBJ_TYPE_GIT:
            openSysBrowser(context, objKey);
            break;
        }
    }

    /**
     * 打开内置浏览器
     * 
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {

        if (StringUtils.isImgUrl(url)) {
            ImagePreviewActivity.showImagePrivew(context, 0,
                    new String[] { url });
            return;
        }

        AppContext.showToastShort("无法浏览此网页");
    }

    /**
     * 打开系统中的浏览器
     * 
     * @param context
     * @param url
     */
    public static void openSysBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            AppContext.showToastShort("无法浏览此网页");
        }
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, String[] imageUrls) {
        ImagePreviewActivity.showImagePrivew(context, 0, imageUrls);
    }

    @JavascriptInterface
    public static void showImagePreview(Context context, int index,
            String[] imageUrls) {
        ImagePreviewActivity.showImagePrivew(context, index, imageUrls);
    }


    public static void showSimpleBack(Context context, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page,
            Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showComment(Context context, int id, int catalog) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(CommentFrament.BUNDLE_KEY_ID, id);
        intent.putExtra(CommentFrament.BUNDLE_KEY_CATALOG, catalog);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE,
                DetailActivity.DISPLAY_COMMENT);
        context.startActivity(intent);
    }

     public static SpannableString parseActiveAction(int objecttype,
            int objectcatalog, String objecttitle) {
        String title = "";
        int start = 0;
        int end = 0;

        SpannableString sp = new SpannableString(title);
        // 设置标题字体大小、高亮
        if (!StringUtils.isEmpty(objecttitle)) {
            start = title.indexOf(objecttitle);
            if (objecttitle.length() > 0 && start > 0) {
                end = start + objecttitle.length();
                sp.setSpan(new AbsoluteSizeSpan(14, true), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                sp.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#0e5986")),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return sp;
    }

    /**
     * 组合动态的回复文本
     * 
     * @param name
     * @param body
     * @return
     */
    public static SpannableStringBuilder parseActiveReply(String name,
            String body) {
        Spanned span = Html.fromHtml(body.trim());
        SpannableStringBuilder sp = new SpannableStringBuilder(name + "：");
        sp.append(span);
        // 设置用户名字体加粗、高亮
        // sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
        // name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#008000")), 0,
                name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
    }

    /**
     * 发送App异常崩溃报告
     * 
     * @param context
     */
    public static void sendAppCrashReport(final Context context) {

        DialogHelp.getConfirmDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 退出
                System.exit(-1);
            }
        }).show();
    }

    /**
     * 发送通知广播
     * 
     * @param context
     * @param notice
     */
    public static void sendBroadCast(Context context, Notice notice) {
        if (!((AppContext) context.getApplicationContext()).isLogin()
                || notice == null)
            return;
        Intent intent = new Intent(Constants.INTENT_ACTION_NOTICE);
        Bundle bundle = new Bundle();
        bundle.putSerializable("notice_bean", notice);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    /**
     * 发送通知广播
     * 
     * @param context
     */
    public static void sendBroadcastForNotice(Context context) {
        Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
        context.sendBroadcast(intent);
    }

    /**
     * 显示用户中心页面
     * 
     * @param context
     * @param hisuid
     * @param hisuid
     * @param hisname
     */
    public static void showUserCenter(Context context, int hisuid,
            String hisname) {
        if (hisuid == 0 && hisname.equalsIgnoreCase("匿名")) {
            AppContext.showToast("提醒你，该用户为非会员");
            return;
        }
        Bundle args = new Bundle();
        args.putInt("his_id", hisuid);
        args.putString("his_name", hisname);
        showSimpleBack(context, SimpleBackPage.USER_CENTER, args);
    }

    /**
     * 显示用户头像大图
     * 
     * @param context
     * @param avatarUrl
     */
    public static void showUserAvatar(Context context, String avatarUrl) {
        if (StringUtils.isEmpty(avatarUrl)) {
            return;
        }
        String url = AvatarView.getLargeAvatar(avatarUrl);
        ImagePreviewActivity.showImagePrivew(context, 0, new String[]{url});
    }

    /**
     * 显示扫一扫界面
     * 
     * @param context
     */
    public static void showScanActivity(Context context) {
        Intent intent = new Intent(context, CaptureActivity.class);
        context.startActivity(intent);
    }

    /**
     * 显示用户的消息中心
     * 
     * @param context
     */
    public static void showMyMes(Context context) {
        showSimpleBack(context, SimpleBackPage.MY_MES);
    }

    /**
     * 显示用户收藏界面
     * 
     * @param context
     */
    public static void showUserFavorite(Context context, int uid) {

        Bundle args = new Bundle();
        args.putInt(BaseListFragment.BUNDLE_KEY_CATALOG, uid);
        showSimpleBack(context, SimpleBackPage.USER_FAVORITE);
    }

    /**
     * 显示设置界面
     * 
     * @param context
     */
    public static void showSetting(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTING);
    }

    /**
     * 显示通知设置界面
     * 
     * @param context
     */
    public static void showSettingNotification(Context context) {
        showSimpleBack(context, SimpleBackPage.SETTING_NOTIFICATION);
    }

    /**
     * 显示关于界面
     * 
     * @param context
     */
    public static void showAboutOSC(Context context) {
        showSimpleBack(context, SimpleBackPage.ABOUT_OSC);
    }

    /**
     * 清除app缓存
     * 
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToastShort("缓存清除成功");
                } else {
                    AppContext.showToastShort("缓存清除失败");
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public static void openDownLoadService(Context context, String downurl,
            String tilte) {
        final ICallbackResult callback = new ICallbackResult() {

            @Override
            public void OnBackResult(Object s) {}
        };
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {}

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadBinder binder = (DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();

            }
        };
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(DownloadService.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 发送广播告知评论发生变化
     * 
     * @param context
     * @param isBlog
     * @param id
     * @param catalog
     * @param operation
     * @param replyComment
     */
    public static void sendBroadCastCommentChanged(Context context,
            boolean isBlog, int id, int catalog, int operation,
            Comment replyComment) {
        Intent intent = new Intent(Constants.INTENT_ACTION_COMMENT_CHANGED);
        Bundle args = new Bundle();
        args.putInt(Comment.BUNDLE_KEY_ID, id);
        args.putInt(Comment.BUNDLE_KEY_CATALOG, catalog);
        args.putBoolean(Comment.BUNDLE_KEY_BLOG, isBlog);
        args.putInt(Comment.BUNDLE_KEY_OPERATION, operation);
        args.putParcelable(Comment.BUNDLE_KEY_COMMENT, replyComment);
        intent.putExtras(args);
        context.sendBroadcast(intent);
    }

    /**
     * 显示活动地址地图信息
     * 
     * @param context
     */
    public static void showEventLocation(Context context, String city,
            String location) {
        Intent intent = new Intent(context, EventLocationActivity.class);
        intent.putExtra("city", city);
        intent.putExtra("location", location);
        context.startActivity(intent);
    }

}
