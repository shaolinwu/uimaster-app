package org.shaolin.uimaster.app.aty;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.CookiesBean;
import org.shaolin.uimaster.app.bean.LoginBean;
import org.shaolin.uimaster.app.bean.VerificationCodeBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.UrlData;
import org.shaolin.uimaster.app.utils.PositionUtils;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.utils.UrlParse;
import org.shaolin.uimaster.app.viewmodule.impl.LoginPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.impl.VerificationCodePresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.ILoginView;
import org.shaolin.uimaster.app.viewmodule.inter.IVerificationCodeView;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2017/1/18.
 */

public class LoginActivity extends BaseActivity implements IVerificationCodeView, ILoginView, AMapLocationListener {
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
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;

    private AMapLocation location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setToolBarTitle(R.string.login);
        VerificationCodePresenterImpl verificationCodePresenter = new VerificationCodePresenterImpl(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    protected void initView() {

        String userName = PreferencesUtils.getString(this, ConfigData.USER_NAME);
        //String userPassword = PreferencesUtils.getString(this, ConfigData.USER_PASSWORD);
        if (!TextUtils.isEmpty(userName)) {
            etUsername.setText(userName);
            //etPassword.setText(userPassword);
        }

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(10000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.setLocationCacheEnable(true);
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            this.location = location;
            //解析定位结果
            String result = PositionUtils.getLocationStr(location);
            Log.i("UIMaster", "GPS定位成功: " + result);
            //Toast.makeText(this, "GPS定位成功: " + result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "GPS定位失败，将影响您的订单推送功能!", Toast.LENGTH_SHORT).show();
        }
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

        Map<String, String> urlParse = new HashMap<String, String>();
        urlParse.put("username", etUsername.getText().toString());
        urlParse.put("pwd", LoginActivity.genPasswordHash(etPassword.getText().toString()));
        urlParse.put("verifyCode", etVerifycode.getText().toString());
        if (location != null && location.getErrorCode() == 0) {
            urlParse.put("latitude", location.getLatitude() + "");
            urlParse.put("longitude", location.getLongitude() + "");
            //Toast.makeText(this, "latitude: " + location.getLatitude() + ",longitude" + location.getLongitude(), Toast.LENGTH_LONG);
        }
        LoginPresenterImpl loginPresenter = new LoginPresenterImpl(this, urlParse);
    }

    @OnClick(R.id.btn_register)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.et_verifycodequestion)
    public void changeCodeQuestion() {
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
            PreferencesUtils.putString(this, ConfigData.USER_PASSWORD, "");
            PreferencesUtils.putString(this, ConfigData.USER_LOGIN_SUMCHECK, "");
        } else {
            saveUserInfo(loginBean);
            if (location != null && location.getErrorCode() == 0) {
                loginBean.latitude = location.getLatitude();
                loginBean.longitude = location.getLongitude();
            }
            EventBus.getDefault().post(loginBean);
            CookiesBean cookiesBean = new CookiesBean();
            cookiesBean.cookies = loginBean.cookies;
            PreferencesUtils.putString(this, ConfigData.USER_COOKIES, loginBean.cookies);
            EventBus.getDefault().post(cookiesBean);
            finish();
        }
    }

    public void saveUserInfo(LoginBean loginBean) {
        PreferencesUtils.putString(this, ConfigData.USER_NAME, etUsername.getText().toString());
        PreferencesUtils.putString(this, ConfigData.USER_PASSWORD, LoginActivity.genPasswordHash(etPassword.getText().toString()));
        PreferencesUtils.putString(this, ConfigData.USER_LOGIN_SUMCHECK,  loginBean.sumCheck);
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stopLocation();
        super.onDestroy();
    }

    public static synchronized String genPasswordHash(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes("UTF-8"));
            byte[] hash = md5.digest();
            return byte2hex(hash);
        } catch (Exception e) {
            return password;
        }
    }

    private static String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer(32);
        for (int n = 0; n < b.length; n++) {
            String hex = Integer.toHexString(b[n] & 0XFF).toUpperCase();
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return new String(sb);
    }
}
