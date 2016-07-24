package org.shaolin.uimaster.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;


import org.apache.http.Header;
import org.shaolin.uimaster.app.context.AjaxContext;
import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.bean.Report;
import org.shaolin.uimaster.app.bean.Result;
import org.shaolin.uimaster.app.bean.ResultBean;
import org.shaolin.uimaster.app.cache.CacheManager;
import org.shaolin.uimaster.app.emoji.OnSendClickListener;
import org.shaolin.uimaster.app.ui.DetailActivity;
import org.shaolin.uimaster.app.ui.ReportDialog;
import org.shaolin.uimaster.app.ui.ShareDialog;
import org.shaolin.uimaster.app.ui.SimpleBackActivity;
import org.shaolin.uimaster.app.ui.empty.EmptyLayout;
import org.shaolin.uimaster.app.util.DialogHelp;
import org.shaolin.uimaster.app.util.FontSizeUtils;
import org.shaolin.uimaster.app.util.HTMLUtil;
import org.shaolin.uimaster.app.util.TDevice;
import org.shaolin.uimaster.app.util.UIHelper;
import org.shaolin.uimaster.app.util.XmlUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * Showing the html details on a fragment.
 *
 * @param <T>
 */
public abstract class WebViewlFragment<T extends Serializable> extends BaseFragment implements OnSendClickListener {

    protected int mId;

    protected EmptyLayout mEmptyLayout;

    protected int mCommentCount = 0;

    protected WebView mWebView;

    protected T mDetail;

    private AsyncTask<String, Void, T> mCacheTask;

    private AjaxContext ajaxContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container,
                false);
        mCommentCount = getActivity().getIntent().getIntExtra("comment_count",
                0);
        mId = getActivity().getIntent().getIntExtra("id", 0);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        requestData(true);
        return view;
    }

    @Override
    public void initView(View view) {
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        setCommentCount(mCommentCount);
        mWebView = (WebView) view.findViewById(R.id.webview);
        ajaxContext = UIHelper.initWebView(this, mWebView, this.getActivity());
        ajaxContext.addPageLoadedListener(new Runnable() {
            @Override
            public void run() {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        });
        ajaxContext.addPageClosedListener(new Runnable() {
            @Override
            public void run() {
                //TODO:
            }
        });
    }

    protected void setCommentCount(int commentCount) {
        if (getActivity() instanceof DetailActivity) {
            ((DetailActivity) getActivity()).toolFragment
                    .setCommentCount(commentCount);
        }
    }

    private void requestData(boolean refresh) {
        String key = getCacheKey();
        sendRequestDataForNet();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ajaxContext.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        recycleWebView();
        super.onDestroyView();
    }

    private void recycleWebView() {
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    private void readCacheData(String cacheKey) {
        cancelReadCache();
        mCacheTask = new CacheTask(getActivity()).execute(cacheKey);
    }

    private void cancelReadCache() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    protected AsyncHttpResponseHandler mDetailHeandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                T detail = parseData(new ByteArrayInputStream(arg2));
                if (detail != null) {
                    executeOnLoadDataSuccess(detail);
                    saveCache(detail);
                } else {
                    executeOnLoadDataError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            readCacheData(getCacheKey());
        }
    };

    private class CacheTask extends AsyncTask<String, Void, T> {
        private final WeakReference<Context> mContext;

        private CacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected T doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (T)seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T detail) {
            super.onPostExecute(detail);
            if (detail != null) {
                executeOnLoadDataSuccess(detail);
            } else {
                executeOnLoadDataError();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
    }

    protected void executeOnLoadDataSuccess(T detail) {
        this.mDetail = detail;
        String body = this.getWebViewBody(detail);
        if (this.mDetail == null || TextUtils.isEmpty(body)) {
            executeOnLoadDataError();
            return;
        }
        executeOnLoadDataSuccess0(body);
    }

    protected void executeOnLoadDataSuccess0(String body) {
        mWebView.loadDataWithBaseURL("", body, "text/html", "UTF-8", "");

//        mWebView.loadUrl(FontSizeUtils.getSaveFontSize());
//        boolean favoriteState = getFavoriteState() == 1;
//        setFavoriteState(favoriteState);

        // 判断最新的评论数是否大于
//        if (getCommentCount() > mCommentCount) {
//            mCommentCount = getCommentCount();
//        }
//        setCommentCount(mCommentCount);
    }

    protected void executeOnLoadDataError() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mState = STATE_REFRESH;
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
    }

    protected void saveCache(T detail) {
        new SaveCacheTask(getActivity(), detail, getCacheKey()).execute();
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    int i = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                sendRequestDataForNet();
                return false;
            case R.id.font_size:
                showChangeFontSize();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    AlertDialog fontSizeChange;

    private void showChangeFontSize() {

        final String[] items = getResources().getStringArray(
                R.array.font_size);
        fontSizeChange = DialogHelp.getSingleChoiceDialog(getActivity(), items, FontSizeUtils.getSaveFontSizeIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 更改字体大小
                FontSizeUtils.saveFontSize(i);
                mWebView.loadUrl(FontSizeUtils.getFontSize(i));
                fontSizeChange.dismiss();
            }
        }).show();
    }

    // 收藏或者取消收藏
    public void handleFavoriteOrNot() {

    }

    private void setFavoriteState(boolean isFavorited) {
        if (getActivity() instanceof DetailActivity) {
            ((DetailActivity) getActivity()).toolFragment.setFavorite(isFavorited);
        }
    }

    // 举报帖子
    public void onReportMenuClick() {

    }
    // 分享
    public void handleShare() {
        if (mDetail == null || TextUtils.isEmpty(getShareContent())
                || TextUtils.isEmpty(getShareUrl()) || TextUtils.isEmpty(getShareTitle())) {
            AppContext.showToast("内容加载失败...");
            return;
        }
        final ShareDialog dialog = new ShareDialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(R.string.share_to);
        dialog.setShareInfo(getShareTitle(), getShareContent(), getShareUrl());
        dialog.show();
    }

    // 显示评论列表
    public void onCilckShowComment() {
        showCommentView();
    }

    // 刷新数据
    protected void refresh() {
        sendRequestDataForNet();
    }

    // 发表评论
    @Override
    public void onClickSendButton(Editable str) {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_network_error);
            return;
        }
        if (!AppContext.getInstance().isLogin()) {
            UIHelper.showLoginActivity(getActivity());
            return;
        }
    }

    @Override
    public void onClickFlagButton() {

    }

    /***
     * 获取去除html标签的body
     *
     * @param body
     * @return
     */
    protected String getFilterHtmlBody(String body) {
        if (body == null)
            return "";
        return HTMLUtil.delHTMLTag(body.trim());
    }

    // 获取缓存的key
    protected abstract String getCacheKey();
    // 从网络中读取数据
    protected abstract void sendRequestDataForNet();
    // 解析数据
    protected abstract T parseData(InputStream is);
    // 返回填充到webview中的内容
    protected abstract String getWebViewBody(T detail);
    // 显示评论列表
    protected abstract void showCommentView();

    protected abstract String getShareTitle();
    protected abstract String getShareContent();
    protected abstract String getShareUrl();

}
