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

public class AboutUsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setToolBarTitle(R.string.about_us);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aboutus;
    }

    protected void initView() {
    }

}
