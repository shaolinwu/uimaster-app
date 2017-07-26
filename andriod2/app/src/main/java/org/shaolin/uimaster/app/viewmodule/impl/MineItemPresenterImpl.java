package org.shaolin.uimaster.app.viewmodule.impl;

import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.MainModuleBean;
import org.shaolin.uimaster.app.viewmodule.inter.IMineView;

import java.util.List;

import static org.shaolin.uimaster.app.data.URLData.MINE_ITEMS_URL;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class MineItemPresenterImpl extends BasePresenterImpl<IMineView> {
    public MineItemPresenterImpl(IMineView view) {
        super(view);
        OkHttpUtils.get()
                .url(MINE_ITEMS_URL)
                .build()
                .execute(this);
    }

    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        if (!TextUtils.isEmpty(response)){
            List<MainModuleBean> mainModuleBeanList = jsonToArrayList(response,MainModuleBean.class);
            if (mainModuleBeanList != null && mainModuleBeanList.size() != 0){
                mViewRef.get().initMineItem(mainModuleBeanList);
            }
        }
    }
}
