package org.shaolin.uimaster.app.fragment;


import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.bean.SimpleBackPage;
import org.shaolin.uimaster.app.util.TDevice;
import org.shaolin.uimaster.app.util.UIHelper;
import org.shaolin.uimaster.app.util.UpdateManager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class AboutPlatformFragment extends BaseFragment {

    @InjectView(R.id.tv_version)
    TextView mTvVersionStatus;

    @InjectView(R.id.tv_version_name)
    TextView mTvVersionName;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.inject(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        view.findViewById(R.id.rl_check_update).setOnClickListener(this);
        view.findViewById(R.id.rl_feedback).setOnClickListener(this);
        view.findViewById(R.id.rl_grade).setOnClickListener(this);
        view.findViewById(R.id.rl_gitapp).setOnClickListener(this);
        view.findViewById(R.id.tv_oscsite).setOnClickListener(this);
        view.findViewById(R.id.tv_knowmore).setOnClickListener(this);
    }

    @Override
    public void initData() {
        mTvVersionName.setText("V " + TDevice.getVersionName());
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.rl_check_update:
            onClickUpdate();
            break;
        case R.id.rl_feedback:
            showFeedBack();
            break;
        case R.id.rl_grade:
            TDevice.openAppInMarket(getActivity());
            break;
        case R.id.tv_knowmore:
            UIHelper.openBrowser(getActivity(),
                    "https://vogerp.com/about");
            break;
        default:
            break;
        }
    }

    private void onClickUpdate() {
        new UpdateManager(getActivity(), true).checkUpdate();
    }

    private void showFeedBack() {
        UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FEED_BACK);
    }
}
