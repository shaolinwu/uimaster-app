package org.shaolin.uimaster.app.util;

import org.shaolin.uimaster.app.R;

import android.graphics.Paint;
import android.widget.TextView;

/**
 * View工具类
 * 
 * ViewUtils.java
 * 
 */
public class ViewUtils {
    
    /***
     * 设置TextView的划线状态
     * @author 
     * 2015-3-11 上午11:46:10
     *
     * @return void
     * @param tv
     * @param flag
     */
    public static void setTextViewLineFlag(TextView tv, int flags) {
	tv.getPaint().setFlags(flags);
	tv.invalidate();
    }
}

