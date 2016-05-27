package org.shaolin.uimaster.app.ui;


import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;
import org.kymjs.kjframe.http.HttpConfig;
import org.shaolin.uimaster.app.bean.Result;
import org.shaolin.uimaster.app.bean.User;
import org.shaolin.uimaster.app.context.AppConfig;
import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.api.HttpClientService;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.Constants;
import org.shaolin.uimaster.app.bean.LoginUserBean;
import org.shaolin.uimaster.app.bean.OpenIdCatalog;
import org.shaolin.uimaster.app.util.CyptoUtils;
import org.shaolin.uimaster.app.util.DialogHelp;
import org.shaolin.uimaster.app.util.TDevice;
import org.shaolin.uimaster.app.util.TLog;
import org.shaolin.uimaster.app.util.XmlUtils;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import java.util.Map;
import java.util.Set;

/**
 * 用户登录界面
 * */
public class LoginActivity extends BaseActivity {

    public static final int REQUEST_CODE_INIT = 0;
    private static final String BUNDLE_KEY_REQUEST_CODE = "BUNDLE_KEY_REQUEST_CODE";
    protected static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.et_username)
    EditText mEtUserName;

    @InjectView(R.id.et_password)
    EditText mEtPassword;

    @InjectView(R.id.et_verifycodequestion)
    TextView mEtVerifyQuestion;

    @InjectView(R.id.et_verifycode)
    EditText mEtVerifyCode;

    private final int requestCode = REQUEST_CODE_INIT;
    private String mUserName = "";
    private String mPassword = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.login;
    }

    @Override
    @OnClick({R.id.btn_login})
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                handleLogin();
                break;
            case R.id.et_verifycodequestion:
                refreshVerifyCode();
                break;
            // R.id.iv_qq_login, R.id.iv_wx_login
            /**
            case R.id.iv_qq_login:
                qqLogin();
                break;
            case R.id.iv_wx_login:
                wxLogin();
                break;
             */
            default:
                break;
        }
    }

    private void handleLogin() {
        if (prepareForLogin()) {
            return;
        }

        // if the data has ready
        mUserName = mEtUserName.getText().toString();
        mPassword = mEtPassword.getText().toString();
        String verifyCodeAnswer = mEtVerifyCode.getText().toString();
        showWaitDialog(R.string.progress_login);
        RService.login(mUserName, mPassword, verifyCodeAnswer, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    JSONObject json = new JSONObject(new String(arg2, "UTF-8"));
                    if (json.has("verifyCode.error")) {
                        AppContext.showToast("验证码错误");
                        return;
                    }
                    LoginUserBean loginUserBean = new LoginUserBean();
                    Result result = new Result();
                    loginUserBean.setResult(result);
                    if (json.has("authfail")) {
                        result.setErrorCode(2);
                        result.setErrorMessage("登录失败");
                    } else {
                        result.setErrorCode(1);
                        User user = new User();
                        user.setAccount(mUserName);
                        user.setPwd(mPassword);
                        user.setName(json.getString("orgName") + json.getString("userName"));
                        loginUserBean.setUser(user);
                        loginUserBean.setResult(result);
                    }
                    handleLoginBean(loginUserBean);
                    AppContext.getInstance().keepUserSession();
                    AppContext.getInstance().getNavigator().refreshModuleItems();
                } catch (Exception e) {
                    AppContext.showToast("登录失败");
                }
            }
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                AppContext.showToast("网络出错! ");
            }
            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }
        });
    }

    private void refreshVerifyCode() {
        RService.getVerifyCode(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
                try {
                    AppContext.getInstance().keepUserSession();
                    JSONObject json = new JSONObject(new String(arg2, "UTF-8"));
                    mEtVerifyQuestion.setText(json.getString("value"));
                } catch (Exception e) {}
            }
            @Override
            public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                                  Throwable arg3) {
                AppContext.showToast("网络出错! ");
            }
            @Override
            public void onFinish() {
                super.onFinish();
                hideWaitDialog();
            }
        });
    }

    private void handleLoginSuccess() {
        Intent data = new Intent();
        data.putExtra(BUNDLE_KEY_REQUEST_CODE, requestCode);
        setResult(RESULT_OK, data);
        this.sendBroadcast(new Intent(Constants.INTENT_ACTION_USER_CHANGE));
        finish();
    }

    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }
        if (mEtUserName.length() == 0) {
            mEtUserName.setError("请输入邮箱/用户名");
            mEtUserName.requestFocus();
            return true;
        }

        if (mEtPassword.length() == 0) {
            mEtPassword.setError("请输入密码");
            mEtPassword.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    public void initData() {
        mEtUserName.setText(AppContext.getInstance()
                .getProperty("user.account"));
        mEtPassword.setText(CyptoUtils.decode("uimasterApp", AppContext
                .getInstance().getProperty("user.pwd")));
        mEtVerifyQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshVerifyCode();
            }
        });
        refreshVerifyCode();
    }

    BroadcastReceiver receiver;

    public static final int REQUEST_CODE_OPENID = 1000;
    // 登陆实体类
    public static final String BUNDLE_KEY_LOGINBEAN = "bundle_key_loginbean";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_OPENID:
                if (data == null) {
                    return;
                }
                LoginUserBean loginUserBean = (LoginUserBean) data.getSerializableExtra(BUNDLE_KEY_LOGINBEAN);
                if (loginUserBean !=  null) {
                    handleLoginBean(loginUserBean);
                }
                break;
            default:

                break;
        }
    }

    // 处理loginBean
    private void handleLoginBean(LoginUserBean loginUserBean) {
        if (loginUserBean.getResult().OK()) {
            // 保存登录信息
            loginUserBean.getUser().setName(mUserName);
            loginUserBean.getUser().setAccount(mUserName);
            loginUserBean.getUser().setPwd(mPassword);
            loginUserBean.getUser().setRememberMe(true);
            AppContext.getInstance().saveUserInfo(loginUserBean.getUser());
            hideWaitDialog();
            handleLoginSuccess();

        } else {
            AppContext.getInstance().cleanLoginInfo();
            AppContext.showToast(loginUserBean.getResult().getErrorMessage());
        }
    }
}
