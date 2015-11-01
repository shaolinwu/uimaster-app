package org.shaolin.uimaster.app.ui;


import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.bean.Constants;
import org.shaolin.uimaster.app.ui.dialog.CommonDialog;
import org.shaolin.uimaster.app.util.TDevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * 分享界面dialog
 *
 * @author 
 *
 */
public class ShareDialog extends CommonDialog implements
        android.view.View.OnClickListener {

    private Context context;
    private String title;
    private String content;
    private String link;

        private ShareDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
        this.context = context;
    }

    @SuppressLint("InflateParams")
    private ShareDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
        View shareView = getLayoutInflater().inflate(
                R.layout.dialog_cotent_share, null);
        shareView.findViewById(R.id.ly_share_qq).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_copy_link)
                .setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_more_option).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_sina_weibo).setOnClickListener(
                this);
        shareView.findViewById(R.id.ly_share_weichat).setOnClickListener(this);
        shareView.findViewById(R.id.ly_share_weichat_circle)
                .setOnClickListener(this);
        setContent(shareView, 0);
    }

    public ShareDialog(Context context) {
        this(context, R.style.dialog_bottom);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    // 设置需要分享的内容
    public void setShareInfo(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_share_copy_link:
                TDevice.copyTextToBoard(this.link);
                break;
            case R.id.ly_share_more_option:
                TDevice.showSystemShareOption((Activity)this.context,
                        this.content, this.title);
                break;
            default:
                break;
        }
        this.dismiss();
    }

}
