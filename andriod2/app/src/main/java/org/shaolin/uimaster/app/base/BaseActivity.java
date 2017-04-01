package org.shaolin.uimaster.app.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.ValueCallback;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.utils.UrlParse;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Created by linbin_dian91 on 2016/3/17.
 */
public abstract class BaseActivity<T extends  BasePresenter> extends AppCompatActivity implements BaseView {

    private Toolbar toolbar;
    protected  T mPresenter;

    /** File upload callback for platform versions prior to Android 5.0 */
    public ValueCallback<Uri> mFileUploadCallbackFirst;
    /** File upload callback for Android 5.0+ */
    public ValueCallback<Uri[]> mFileUploadCallbackSecond;

    public final static int FILECHOOSER_RESULTCODE = 30;

    public String selectedUploadFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initToolBar();
    }

    protected void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //显示自动的返回箭头
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    protected abstract int getLayoutId();

    protected  void setToolBarTitle(int title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    protected void setToolBarTitle(String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
    }

    @Subscribe()

    @Override
    public void toast(String msg) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            //file chooser.
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                        //Log.w("UIMaster", "choosen path at first: " + intent.getData().toString());
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                        } catch (Exception e) {
                            dataUris = null;
                        }
                        selectedUploadFile = UrlParse.getRealFilePath(this, dataUris[0]);
                        Log.w("UIMaster", "choosen path: " + selectedUploadFile);
                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                }
                else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }

     @Override
     protected void onResume() {
      super.onResume();
         if (mPresenter != null) {
             mPresenter.onResume();
         }
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         if (mPresenter != null) {
             mPresenter.onDestroy();
         }
         EventBus.getDefault().unregister(this);
     }

}
