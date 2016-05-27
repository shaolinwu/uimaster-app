package org.shaolin.uimaster.app.bean;


import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.fragment.AboutPlatformFragment;
import org.shaolin.uimaster.app.fragment.CommentFrament;
import org.shaolin.uimaster.app.fragment.FunctionFragment;
import org.shaolin.uimaster.app.fragment.MyInformationFragment;
import org.shaolin.uimaster.app.fragment.MyInformationFragmentDetail;
import org.shaolin.uimaster.app.fragment.SettingsFragment;
import org.shaolin.uimaster.app.fragment.SettingsNotificationFragment;
import org.shaolin.uimaster.app.fragment.UserCenterFragment;
import org.shaolin.uimaster.app.viewpagerfragment.NoticeViewPagerFragment;
import org.shaolin.uimaster.app.viewpagerfragment.SearchViewPageFragment;
import org.shaolin.uimaster.app.viewpagerfragment.UserFavoriteViewPagerFragment;

public enum SimpleBackPage {

    FUNCTION(1, R.string.actionbar_title_function, FunctionFragment.class),

    COMMENT(2, R.string.actionbar_title_comment, CommentFrament.class),

    USER_CENTER(5, R.string.actionbar_title_user_center,
            UserCenterFragment.class),

    MY_INFORMATION(7, R.string.actionbar_title_my_information,
            MyInformationFragment.class),

    MY_MES(9, R.string.actionbar_title_mes, NoticeViewPagerFragment.class),

    USER_FAVORITE(10, R.string.actionbar_title_user_favorite,
            UserFavoriteViewPagerFragment.class),

    SETTING(11, R.string.actionbar_title_setting, SettingsFragment.class),

    SETTING_NOTIFICATION(12, R.string.actionbar_title_setting_notification,
            SettingsNotificationFragment.class),

    ABOUT_OSC(13, R.string.actionbar_title_about_osc, AboutPlatformFragment.class),

    SEARCH(14, R.string.actionbar_title_search, SearchViewPageFragment.class),

    MY_INFORMATION_DETAIL(17, R.string.actionbar_title_my_information,
            MyInformationFragmentDetail.class);

    private int title;
    private Class<?> clz;
    private int value;

    private SimpleBackPage(int value, int title, Class<?> clz) {
        this.value = value;
        this.title = title;
        this.clz = clz;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }
}
