package org.shaolin.uimaster.app.ui;


import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.base.BaseFragment;
import org.shaolin.uimaster.app.base.WebViewlFragment;
import org.shaolin.uimaster.app.emoji.KJEmojiFragment;
import org.shaolin.uimaster.app.emoji.OnSendClickListener;
import org.shaolin.uimaster.app.emoji.ToolbarFragment;
import org.shaolin.uimaster.app.emoji.ToolbarFragment.OnActionClickListener;
import org.shaolin.uimaster.app.emoji.ToolbarFragment.ToolAction;
import org.shaolin.uimaster.app.fragment.CommentFrament;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;


/**
 * 详情activity（包括：资讯、博客、软件、问答、动弹）
 *
 * @author 
 * @created 2014年10月11日 上午11:18:41
 */
public class DetailActivity extends BaseActivity implements OnSendClickListener {

    public static final int DISPLAY_NEWS = 0;
    public static final int DISPLAY_BLOG = 1;
    public static final int DISPLAY_SOFTWARE = 2;
    public static final int DISPLAY_POST = 3;
    public static final int DISPLAY_TWEET = 4;
    public static final int DISPLAY_EVENT = 5;
    public static final int DISPLAY_TEAM_ISSUE_DETAIL = 6;
    public static final int DISPLAY_TEAM_DISCUSS_DETAIL = 7;
    public static final int DISPLAY_TEAM_TWEET_DETAIL = 8;
    public static final int DISPLAY_TEAM_DIARY = 9;
    public static final int DISPLAY_COMMENT = 10;

    public static final String BUNDLE_KEY_DISPLAY_TYPE = "BUNDLE_KEY_DISPLAY_TYPE";

    private OnSendClickListener currentFragment;
    public KJEmojiFragment emojiFragment = new KJEmojiFragment();
    public ToolbarFragment toolFragment = new ToolbarFragment();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.actionbar_title_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        int displayType = getIntent().getIntExtra(BUNDLE_KEY_DISPLAY_TYPE,
                DISPLAY_NEWS);
        BaseFragment fragment = null;
        int actionBarTitle = 0;
        switch (displayType) {
            case DISPLAY_COMMENT:
                actionBarTitle = R.string.actionbar_title_comment;
                fragment = new CommentFrament();
            default:
                break;
        }
        setActionBarTitle(actionBarTitle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.container, fragment);
        trans.commitAllowingStateLoss();
        if (fragment instanceof OnSendClickListener) {
            currentFragment = (OnSendClickListener) fragment;
        } else {
            currentFragment = new OnSendClickListener() {
                @Override
                public void onClickSendButton(Editable str) {
                }

                @Override
                public void onClickFlagButton() {
                }
            };
        }
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void initView() {
        if (currentFragment instanceof CommentFrament) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emoji_keyboard, emojiFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.emoji_keyboard, toolFragment).commit();
        }
        toolFragment.setOnActionClickListener(new OnActionClickListener() {
            @Override
            public void onActionClick(ToolAction action) {
                switch (action) {
                    case ACTION_CHANGE:
                    case ACTION_WRITE_COMMENT:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.footer_menu_slide_in,
                                        R.anim.footer_menu_slide_out)
                                .replace(R.id.emoji_keyboard, emojiFragment)
                                .commit();
                        break;
                    case ACTION_FAVORITE:
                        ((WebViewlFragment) currentFragment)
                                .handleFavoriteOrNot();
                        break;
                    case ACTION_REPORT:
                        ((WebViewlFragment) currentFragment).onReportMenuClick();
                        break;
                    case ACTION_SHARE:
                        ((WebViewlFragment) currentFragment).handleShare();
                        break;
                    case ACTION_VIEW_COMMENT:
                        ((WebViewlFragment) currentFragment)
                                .onCilckShowComment();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(Editable str) {
        currentFragment.onClickSendButton(str);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (emojiFragment.isShowEmojiKeyBoard()) {
                    emojiFragment.hideAllKeyBoard();
                    return true;
                }
                if (emojiFragment.getEditText().getTag() != null) {
                    emojiFragment.getEditText().setTag(null);
                    emojiFragment.getEditText().setHint("说点什么吧");
                    return true;
                }
            } catch (NullPointerException e) {
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCommentCount(int count) {
        try {
            toolFragment.setCommentCount(count);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClickFlagButton() {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.footer_menu_slide_in,
                        R.anim.footer_menu_slide_out)
                .replace(R.id.emoji_keyboard, toolFragment).commit();
        try {

        } catch (Exception e) {
        }
    }
}
