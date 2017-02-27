package org.shaolin.uimaster.app.aty;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.CookiesBean;
import org.shaolin.uimaster.app.bean.LoginBean;
import org.shaolin.uimaster.app.bean.VerificationCodeBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.utils.UrlParse;
import org.shaolin.uimaster.app.viewmodule.impl.LoginPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.impl.VerificationCodePresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.ILoginView;
import org.shaolin.uimaster.app.viewmodule.inter.IVerificationCodeView;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/18.
 */

public class LoginActivity extends BaseActivity implements IVerificationCodeView, ILoginView {
    @BindView(R.id.et_username)
    AppCompatEditText etUsername;
    @BindView(R.id.et_password)
    AppCompatEditText etPassword;
    @BindView(R.id.et_verifycodequestionlabel)
    TextView etVerifycodequestionlabel;
    @BindView(R.id.et_verifycodequestion)
    TextView etVerifycodequestion;
    @BindView(R.id.et_verifycode)
    AppCompatEditText etVerifycode;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolBarTitle(R.string.login);
        VerificationCodePresenterImpl verificationCodePresenter = new VerificationCodePresenterImpl(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            Toast.makeText(this, R.string.please_edit_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            Toast.makeText(this, R.string.please_edit_password, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etVerifycode.getText().toString())) {
            Toast.makeText(this, R.string.please_edit_verificationcode, Toast.LENGTH_SHORT).show();
            return;
        }
        UrlParse urlParse = new UrlParse(UrlData.LOGIN_URL);
        urlParse.putValue("username", etUsername.getText().toString());
        urlParse.putValue("pwd", etPassword.getText().toString());
        urlParse.putValue("verifyCode", etVerifycode.getText().toString());
        LoginPresenterImpl loginPresenter = new LoginPresenterImpl(this, urlParse.toString());
    }

    @OnClick(R.id.btn_register)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.et_verifycodequestion)
    public void changeCodeQuestion(){
        VerificationCodePresenterImpl verificationCodePresenter = new VerificationCodePresenterImpl(this);
    }


    @Override
    public void showVerificationCode(VerificationCodeBean bean) {
        etVerifycodequestion.setText(bean.value);
    }

    @Override
    public void toast(String msg) {

    }

    @Override
    public void showProgress() {
        loadingLayout.setVisibility(View.VISIBLE);
        Animation mRotateAnim = AnimationUtils.loadAnimation(this, R.anim.loading_rotate);
        ivLoading.startAnimation(mRotateAnim);
    }

    @Override
    public void hideProgress() {
        ivLoading.clearAnimation();
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void loginResult(LoginBean loginBean) {
        if (!TextUtils.isEmpty(loginBean.error) || loginBean == null || TextUtils.isEmpty(loginBean.userName)) {
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
        } else {
            EventBus.getDefault().post(loginBean);
            CookiesBean cookiesBean = new CookiesBean();
            cookiesBean.cookies = loginBean.cookies;
            PreferencesUtils.putString(this, ConfigData.USER_COOKIES, loginBean.cookies);
            EventBus.getDefault().post(cookiesBean);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
