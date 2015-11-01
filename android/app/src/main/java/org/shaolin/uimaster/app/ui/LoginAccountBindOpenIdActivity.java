package org.shaolin.uimaster.app.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpResponseHandler;


import org.apache.http.Header;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.LoginUserBean;
import org.shaolin.uimaster.app.util.DialogHelp;
import org.shaolin.uimaster.app.util.XmlUtils;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 第三方登陆账号绑定操作
 * Created by zhangdeyi on 15/7/21.
 */
public class LoginAccountBindOpenIdActivity extends BaseActivity {

    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_password)
    EditText etPassword;

    private String catalog;
    private String openIdInfo;

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.login;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_bind_openid;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            catalog = intent.getStringExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_CATALOG);
            openIdInfo = intent.getStringExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_OPENIDINFO);
        }
    }

    @OnClick(R.id.bt_bind)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_bind:
                toBind();
                break;
        }
    }

    private void toBind() {
        if (checkInputInof()) {
            return;
        }

        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        final ProgressDialog waitDialog = DialogHelp.getWaitDialog(this, "加载中...");
        RService.bind_openid(catalog, openIdInfo, username, password, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LoginUserBean loginUserBean = XmlUtils.toBean(LoginUserBean.class, responseBody);
                if (loginUserBean != null && loginUserBean.getResult().OK()) {
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(LoginActivity.BUNDLE_KEY_LOGINBEAN, loginUserBean);
                    data.putExtras(bundle);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    DialogHelp.getMessageDialog(LoginAccountBindOpenIdActivity.this, loginUserBean.getResult().getErrorMessage()).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public void onStart() {
                super.onStart();
                waitDialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                waitDialog.dismiss();
            }
        });
    }

    private boolean checkInputInof() {
        if (etUsername.length() == 0) {
            etUsername.setError("请输入用户名");
            etUsername.requestFocus();
            return true;
        }

        if (etPassword.length() == 0) {
            etPassword.setError("请输入密码");
            etPassword.requestFocus();
            return true;
        }
        return false;
    }
}
