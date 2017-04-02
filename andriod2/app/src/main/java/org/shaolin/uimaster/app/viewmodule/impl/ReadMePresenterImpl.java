package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.DownFileBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.inter.IMainModuleView;

import java.util.List;

import static org.shaolin.uimaster.app.data.UrlData.GET_DOWNLOAD_RESOURCES;

/**
 * Created Administrator
 * on 2017/1/11
 * deprecated:
 */

public class ReadMePresenterImpl extends BasePresenterImpl<IMainModuleView> {
    private AssetManager asset;
    private Context mContext;
    public ReadMePresenterImpl(AssetManager asset, String url, Context context) {
        super(null);
        this.asset = asset;
        mContext = context;
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(this);
    }


    @Override
    public void onResponse(String response) {
        super.onResponse(response);
        List<DownFileBean> downFileBeans = jsonToArrayList(response,DownFileBean.class);
        if (downFileBeans != null && downFileBeans.size() != 0){
            DownFileBean downFileBean = downFileBeans.get(0);
            if (downFileBean != null && !TextUtils.isEmpty(downFileBean.version)){
                String localFileVersion = PreferencesUtils.getString(mContext, ConfigData.FILE_VERSION, "");
                if (!localFileVersion.equals(downFileBean.version)){
                    // || !FileUtil.checkFilePathExists(FileData.APP_ROOT_FILE + "/uimaster.js")
                    PreferencesUtils.putString(mContext, ConfigData.FILE_VERSION, downFileBean.version);

                    //下载服务端资源文件
                    StringBuffer url = new StringBuffer(GET_DOWNLOAD_RESOURCES);
                    url.append(downFileBean.resource);
                    DownFilePresenterImpl downFilePresenter = new DownFilePresenterImpl(mContext, url.toString(), downFileBean.resource);
                }
            }
        }
    }
}
