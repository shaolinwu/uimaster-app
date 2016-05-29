package org.shaolin.uimaster.app.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 *
 */
public class AppConfig {

    private final static String APP_CONFIG = "config";
    public final static String CONF_COOKIE = "cookie";
    public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";
    public static final String KEY_LOAD_IMAGE = "KEY_LOAD_IMAGE";
    public static final String KEY_NOTIFICATION_ACCEPT = "KEY_NOTIFICATION_ACCEPT";
    public static final String KEY_NOTIFICATION_SOUND = "KEY_NOTIFICATION_SOUND";
    public static final String KEY_NOTIFICATION_VIBRATION = "KEY_NOTIFICATION_VIBRATION";
    public static final String KEY_NOTIFICATION_DISABLE_WHEN_EXIT = "KEY_NOTIFICATION_DISABLE_WHEN_EXIT";
    public static final String KEY_CHECK_UPDATE = "KEY_CHECK_UPDATE";
    public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";
    public static final String KEY_FRITST_START = "KEY_FRIST_START";
    public static final String KEY_NIGHT_MODE_SWITCH="night_mode_switch";

    /**
     * 那到底要如何才能访问到本地电脑上的Web应用呢？在Android中，将我们本地电脑的地址映射为10.0.2.2，
     * 因此，只需要将原先的localhost或者127.0.0.1换成10.0.2.2，就可以在模拟器上访问本地计算机上的Web资源了。
     * */
    public static String HOST = "120.25.146.49";//10.0.2.2
    public static String Origin = "www.vogerp.com";//10.0.2.2:8080
    public static String FUNCTION_DETAILS_URL = Origin + "/uimaster/webflow.do?_appclient=andriod";
    public static String AJAX_SERVICE_URL = Origin + "/uimaster/ajaxservice?_appclient=andriod";
    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "uimaster"
            + File.separator + "osc_img" + File.separator;

    // 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "uimaster"
            + File.separator + "download" + File.separator;

    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }

    public static void updateServerURL(String serverURL) {
        AppConfig.Origin = serverURL;
        AppConfig.HOST = serverURL;
        AppConfig.FUNCTION_DETAILS_URL = AppConfig.Origin + "/uimaster/webflow.do?_appclient=andriod";
        AppConfig.AJAX_SERVICE_URL = AppConfig.Origin + "/uimaster/ajaxservice?_appclient=andriod";
    }

    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }
}
