package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.MenuItem;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.viewmodule.inter.IMenuView;

import java.util.List;

import okhttp3.Request;

/**
 * Created by Administrator on 2017/1/20.
 */

public class MenuItemPresenterImpl extends BasePresenterImpl<IMenuView> {

    public MenuItemPresenterImpl(IMenuView view) {
        super(view);
        OkHttpUtils.get()
                .url(UrlData.MENU_ITEMS_URL)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);

        if (!TextUtils.isEmpty(response)){
            Gson gson = new Gson();
            List<MenuItem> menuItemLists = jsonToArrayList(response,MenuItem.class);
            if (menuItemLists != null && menuItemLists.size() != 0){
                mViewRef.get().showMenuList(menuItemLists);
            }
        }
    }

    @Override
    public void onBefore(Request request) {

    }

    @Override
    public void onAfter() {
    }
}
