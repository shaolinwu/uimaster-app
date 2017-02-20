package org.shaolin.uimaster.app.viewmodule.impl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;

import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.DownFileBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.FileUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.inter.IMainModuleView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                if (!FileUtil.checkFilePathExists(UrlData.APP_ROOT_FILE)) {
                    Log.w("UIMaster", "Resource directory does not exit! path=" + UrlData.APP_ROOT_FILE);
                    FileUtil.createPath(UrlData.APP_ROOT_FILE);
                }
                String localFileVersion = PreferencesUtils.getString(mContext, ConfigData.FILE_VERSION,"");
                if (!localFileVersion.equals(downFileBean.version) || !FileUtil.checkFilePathExists(UrlData.APP_ROOT_FILE)){
                    PreferencesUtils.putString(mContext,ConfigData.FILE_VERSION,downFileBean.version);

                    //copy uimaster.zip from assets.
                    try {
                        deepFile(asset, "uimaster.zip");
                        Runnable runnable = new DownFilePresenterImpl.ZipRunnable(
                                mContext, UrlData.APP_ROOT_FILE + "/uimaster.zip", UrlData.APP_ROOT_FILE);
                        new Thread(runnable).start();
                    } catch (IOException e) {
                        Log.e("UIMaster", "Assert directory does not exit!", e);
                    }

                    //下载服务端资源文件
                    StringBuffer url = new StringBuffer(GET_DOWNLOAD_RESOURCES);
                    url.append(downFileBean.resource);
                    DownFilePresenterImpl downFilePresenter = new DownFilePresenterImpl(mContext, url.toString(),downFileBean.resource);

                }
            }
        }
    }

    public void deepFile(AssetManager asset, String path) throws IOException {
        String str[] = asset.list(path);
        if (str.length > 0) {//如果是目录
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), path);
            file.mkdirs();
            for (String string : str) {
                path = path + "/" + string;
                deepFile(asset, path);
                path = path.substring(0, path.lastIndexOf('/'));
            }
        } else {//如果是文件
            InputStream is = asset.open(path);
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath(), path));
            byte[] buffer = new byte[1024];
            int count = 0;
            while (true) {
                count++;
                int len = is.read(buffer);
                if (len == -1) {
                    break;
                }
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();
        }
    }
}
