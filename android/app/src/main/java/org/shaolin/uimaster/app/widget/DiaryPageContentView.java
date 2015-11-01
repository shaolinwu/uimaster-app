package org.shaolin.uimaster.app.widget;

import java.util.List;


import org.apache.http.Header;
import org.kymjs.kjframe.http.KJAsyncTask;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.cache.CacheManager;
import org.shaolin.uimaster.app.ui.empty.EmptyLayout;
import org.shaolin.uimaster.app.util.UIHelper;
import org.shaolin.uimaster.app.util.XmlUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * 周报每个Page,单独拿出来写
 * 
 * @author  (http://www..com)
 */
public class DiaryPageContentView {

    private final RelativeLayout rootView;
    private final ListView listview;
    private final SwipeRefreshLayout pullHeadView;
    private final EmptyLayout errorLayout;
    private final Activity cxt;

    private final int teamId;
    private final int year;
    private final int week;

    /** 只允许new View的形式创建 */
    public DiaryPageContentView(Context context, int teamId, int year, int week) {
        this.teamId = teamId;
        this.year = year;
        this.week = week;
        this.cxt = (Activity) context;

        rootView = (RelativeLayout) View.inflate(context,
                R.layout.pager_item_diary, null);
        listview = (ListView) rootView.findViewById(R.id.diary_listview);
        pullHeadView = (SwipeRefreshLayout) rootView
                .findViewById(R.id.swiperefreshlayout);
        errorLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
        initView();
        requestData(true);
    }

    private void initView() {
        errorLayout.setOnLayoutClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData(true);
            }
        });
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

            }
        });

        pullHeadView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (BaseFragment.mState == BaseFragment.STATE_REFRESH) {
                    return;
                } else {
                    errorLayout.setErrorMessage("本周无人提交周报");
                    // // 设置顶部正在刷新
                    // setSwipeRefreshLoadingState(pullHeadView);
                    requestData(false);
                }
            }
        });
        pullHeadView.setColorSchemeResources(R.color.swiperefresh_color1,
                R.color.swiperefresh_color2, R.color.swiperefresh_color3,
                R.color.swiperefresh_color4);
    }

    private void requestData(final boolean isFirst) {

    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        BaseFragment.mState = BaseFragment.STATE_REFRESH;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    public RelativeLayout getView() {
        return rootView;
    }

    /**
     * 设置顶部加载完毕的状态
     */
    private void setSwipeRefreshLoadedState(
            SwipeRefreshLayout mSwipeRefreshLayout) {
        BaseFragment.mState = BaseFragment.STATE_NOMORE;
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }
}
