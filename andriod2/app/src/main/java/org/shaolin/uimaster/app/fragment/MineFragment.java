package org.shaolin.uimaster.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.adpter.MineAdapter;
import org.shaolin.uimaster.app.aty.LoginActivity;
import org.shaolin.uimaster.app.aty.WebViewActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.bean.CookiesBean;
import org.shaolin.uimaster.app.bean.LoginBean;
import org.shaolin.uimaster.app.bean.MainModuleBean;
import org.shaolin.uimaster.app.customeview.CircleImageView;
import org.shaolin.uimaster.app.customeview.DividerItemDecoration;
import org.shaolin.uimaster.app.customeview.RecyclerItemClickListener;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.utils.UrlParse;
import org.shaolin.uimaster.app.viewmodule.impl.LoginOutPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.impl.LoginPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.impl.MineItemPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.ILoginView;
import org.shaolin.uimaster.app.viewmodule.inter.IMineView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created Administrator
 * on 2017/1/15
 * deprecated:
 */

public class MineFragment extends BaseFragment implements IMineView, ILoginView {
    public static MineFragment mineFragmentInstance;
    @BindView(R.id.user_icon)
    CircleImageView userIcon;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_login_out)
    TextView tvLoginOut;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    private MineItemPresenterImpl mineItemPresenter;
    private LoginOutPresenterImpl loginOutPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mView = View.inflate(mContext, R.layout.mine_fragment_layout, null);
        ButterKnife.bind(this, mView);
        mineItemPresenter = new MineItemPresenterImpl(this);
        autoLogin();
    }

    public static MineFragment getInstance() {
        if (mineFragmentInstance == null) {
            mineFragmentInstance = new MineFragment();
        }
        return mineFragmentInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    private void autoLogin() {
        String userName = PreferencesUtils.getString(this.getContext(), ConfigData.USER_NAME);
        String userPassword = PreferencesUtils.getString(this.getContext(), ConfigData.USER_PASSWORD);
        String autoSumCheck = PreferencesUtils.getString(this.getContext(), ConfigData.USER_LOGIN_SUMCHECK);
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPassword) && !TextUtils.isEmpty(autoSumCheck)) {
            Map<String, String> urlParse = new HashMap<String, String>();
            urlParse.put("username", userName);
            urlParse.put("pwd", userPassword);
            urlParse.put("autosumcheck", autoSumCheck);
            LoginPresenterImpl loginPresenter = new LoginPresenterImpl(this, urlParse);
        }
    }

    public void loginResult(LoginBean loginBean) {
        if (loginBean == null || !TextUtils.isEmpty(loginBean.error) || TextUtils.isEmpty(loginBean.userName)) {
            Toast.makeText(this.getContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            PreferencesUtils.putString(this.getContext(), ConfigData.USER_PASSWORD, "");
            PreferencesUtils.putString(this.getContext(), ConfigData.USER_LOGIN_SUMCHECK, "");
        } else {
            PreferencesUtils.putString(this.getContext(), ConfigData.USER_LOGIN_SUMCHECK, loginBean.sumCheck);
            EventBus.getDefault().post(loginBean);
            CookiesBean cookiesBean = new CookiesBean();
            cookiesBean.cookies = loginBean.cookies;
            PreferencesUtils.putString(this.getContext(), ConfigData.USER_COOKIES, loginBean.cookies);
            EventBus.getDefault().post(cookiesBean);
        }
    }

    @OnClick(R.id.user_icon)
    public void login() {
        if (tvLoginOut.getVisibility() != View.VISIBLE){
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.tv_login_out)
    public void performLoginOut() {
        loginOutPresenter = new LoginOutPresenterImpl(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void showUserLoginView(LoginBean bean) {
        if (bean != null) {
            PreferencesUtils.putString(this.getContext(), "userId", bean.userId);
            if (!TextUtils.isEmpty(bean.userName)) {
                username.setText(bean.userName);
            }
            if (!TextUtils.isEmpty(bean.userIcon)) {
                Glide.with(this)
                        .load(bean.userIcon)
                        .into(userIcon);
            }
            tvLoginOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mineItemPresenter.onDestroy();
        loginOutPresenter.onDestroy();
    }

    @Override
    public void initMineItem(final List<MainModuleBean> datas) {
        if (datas != null && datas.size() != 0) {
            PreferencesUtils.putString(mContext, ConfigData.MESSAGE_ACTIVITY_URL, generateWebUrl(datas.get(0)));
            PreferencesUtils.putString(mContext, ConfigData.MESSAGE_ACTIVITY_TITLE, datas.get(0).name);
            MineAdapter adapter = new MineAdapter(getContext(), datas);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mView.getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerview.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL, 1, R.color.black_50));
            recyclerview.setLayoutManager(layoutManager);
            recyclerview.setAdapter(adapter);
            recyclerview.addOnItemTouchListener(new RecyclerItemClickListener(recyclerview) {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(), WebViewActivity.class);
                    String url = generateWebUrl(datas.get(position));
                    intent.putExtra("url", url);
                    intent.putExtra("title", datas.get(position).name);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void loginOut() {
        tvLoginOut.setVisibility(View.GONE);
        username.setText(getString(R.string.unLogin));
        userIcon.setImageResource(R.mipmap.widget_dface);
        PreferencesUtils.removeConfig(getContext(),ConfigData.USER_COOKIES);
        String userId = PreferencesUtils.getString(this.getContext(), "userId");
        AjaxContext.closeWebSocket(Long.valueOf(userId));
        EventBus.getDefault().post("loginOut");
    }

    @Override
    public void toast(String msg) {

    }

    @Override
    public void showProgress() {
        loadingLayout.setVisibility(View.VISIBLE);
        Animation mRotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_rotate);
        ivLoading.startAnimation(mRotateAnim);
    }

    @Override
    public void hideProgress() {
        ivLoading.clearAnimation();
        loadingLayout.setVisibility(View.GONE);
    }

    private String generateWebUrl(MainModuleBean bean) {
        UrlParse urlParse = new UrlParse(URLData.BASE_URL + "webflow.do?");
        urlParse.putValue("_nodename", bean._nodename);
        urlParse.putValue("_chunkname", bean._chunkname);
        urlParse.putValue("_page", bean._page);
        urlParse.putValue("_framename", bean._framename);
        urlParse.putValue("_appclient", "android");
        return urlParse.toString();
    }


}
