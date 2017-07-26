package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.VerificationCodeBean;
import org.shaolin.uimaster.app.viewmodule.impl.FindPwdPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.impl.VerificationCodePresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IFindPwdView;
import org.shaolin.uimaster.app.viewmodule.inter.IVerificationCodeView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/1/18.
 */

public class FindPwdActivity extends BaseActivity implements IVerificationCodeView, IFindPwdView {
    @BindView(R.id.et_phonenumber)
    AppCompatEditText etUsername;
    @BindView(R.id.btn_findpwd)
    Button btnFindpwd;
    @BindView(R.id.et_verifycodequestionlabel)
    TextView etVerifycodequestionlabel;
    @BindView(R.id.et_verifycodequestion)
    TextView etVerifycodequestion;
    @BindView(R.id.et_verifycode)
    AppCompatEditText etVerifycode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setToolBarTitle(R.string.findpwd);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_findpwd;
    }

    protected void initView() {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_findpwd)
    public void findPwd() {
        if (TextUtils.isEmpty(etUsername.getText().toString())) {
            Toast.makeText(this, R.string.please_edit_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> urlParse = new HashMap<String, String>();
        urlParse.put("phoneNumber", etUsername.getText().toString());
        urlParse.put("verifyCode", etVerifycode.getText().toString());
        FindPwdPresenterImpl loginPresenter = new FindPwdPresenterImpl(this, urlParse);
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
    public void smsSent() {
        Toast.makeText(this, R.string.findpwd_sentsms, Toast.LENGTH_SHORT).show();
    }
}
