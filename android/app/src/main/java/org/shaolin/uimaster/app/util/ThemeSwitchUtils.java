package org.shaolin.uimaster.app.util;

import org.shaolin.uimaster.app.context.AppContext;
import org.shaolin.uimaster.app.R;

import android.content.Context;


/**
 * 白天和夜间模式切换
 * Created by  on 15/5/26.
 */
public class ThemeSwitchUtils {

    public static void switchTheme(Context context) {

    }

    public static int getTitleReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_infoTextColor;
        } else {
            return R.color.day_infoTextColor;
        }
    }

    public static int getTitleUnReadedColor() {
        if (AppContext.getNightModeSwitch()) {
            return R.color.night_textColor;
        } else {
            return R.color.day_textColor;
        }
    }

    public static String getWebViewBodyString() {
        if (AppContext.getNightModeSwitch()) {
            return "<body class='night'><div class='contentstyle' id='article_body'>";
        } else {
            return "<body ><div class='contentstyle' id='article_body'>";
        }
    }
}
