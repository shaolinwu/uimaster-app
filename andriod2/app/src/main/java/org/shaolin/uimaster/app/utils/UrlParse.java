package org.shaolin.uimaster.app.utils;

import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created Administrator
 * on 2017/1/12
 * deprecated:
 */

public class UrlParse {
    protected Map<String, String> mMap = new LinkedHashMap<String, String>();
    private StringBuilder mHeaderBuilder;

    public Map<String, String> getMap() {
        return mMap;
    }

    public UrlParse(String url) {
        iniUrl(url);
    }

    public UrlParse() {

    }

    public StringBuilder getHeaderBuilder() {
        return mHeaderBuilder;
    }

    public void reset() {
        mMap.clear();
        mHeaderBuilder = new StringBuilder("");
    }

    /**
     * 初始化URL
     *
     * @param url
     */
    public void iniUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            mHeaderBuilder = new StringBuilder("");
            return;
        }

        mMap.clear();
        int pos = url.indexOf("?");
        if (pos == -1) {
            mHeaderBuilder = new StringBuilder(url);
            return;
        }

        mHeaderBuilder = new StringBuilder(url.substring(0, pos));
        String temp = url.substring(pos + 1);
        StringTokenizer token = new StringTokenizer(temp, "&", false);
        while (token.hasMoreElements()) {
            String[] str = token.nextToken().split("=");
            if (str != null && str.length == 2) {
                putValue(str[0], str[1]);
            }
        }

    }

    /**
     * 替换URL，保存参数
     *
     * @param url
     */
    public void replaceUrl(String url) {
        int pos = url.indexOf("?");
        if (pos == -1) {
            mHeaderBuilder = new StringBuilder(url);
            return;
        }
        mHeaderBuilder = new StringBuilder(url.substring(0, pos));
        String temp = url.substring(pos + 1);
        StringTokenizer token = new StringTokenizer(temp, "&", false);
        while (token.hasMoreElements()) {
            String[] str = token.nextToken().split("=");
            if (str.length == 2) {
                putValue(str[0], str[1]);
            }
        }

    }

    protected String decodeUtf8(String str) {
        try {
            if (str == null || "".equals(str)) {
                return str;
            }
            return URLDecoder.decode(str, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取对应的UTF8值。
     *
     * @param key
     * @return
     */
    public String getUtf8Value(String key) {
        String temp = mMap.get(key.toLowerCase());
        return decodeUtf8(temp);
    }

    public int getInteger(String key, int def) {
        String value = getValue(key);

        if (TextUtils.isEmpty(value))
            return def;

        try {
            return Integer.valueOf(value);
        } catch (Exception ex) {
            return def;
        }
    }

    /**
     * 获取对应的值。
     *
     * @param key
     * @return
     */
    public String getValue(String key) {
        return mMap.get(key.toLowerCase());
    }

    public boolean containsKey(String key) {
        return mMap.containsKey(key.toLowerCase());
    }

    /**
     * 设置对应的值,如果其中有一个为空，不设置。</br> 当参数存在的时候，会代替已存在的参数。
     */
    public UrlParse putValue(String key, String value) {
        if (key == null || value == null)
            return this;
        mMap.put(key, value);
        return this;
    }

    public UrlParse putValue(String key, int value) {
        return putValue(key, String.valueOf(value));
    }

    /**
     * 移除值
     *
     * @param key
     */
    public void removeValue(String key) {
        mMap.remove(key.toLowerCase());
    }

    /**
     * 移除值
     *
     * @param key
     */
    public void optRemoveValue(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        if (mMap.containsKey(key)) {
            mMap.remove(key.toLowerCase());
        }
    }

    /**
     * 获取解析后的URL地址 the method is old instead of toString method.
     *
     * @return URL
     */
    @Override
    public String toString() {
        // 设置通用参数 在getUrl时设置通用参数，保证通用参数放在url的最后，便于调试
        // TODO LBN
        //UrlManager.setCommonUrlParam(this);
        return toStringWithoutParam();
    }

    public String toStringWithoutParam() {
        StringBuilder sb = new StringBuilder(mHeaderBuilder);
        String param = getUrlParam();
        if (!TextUtils.isEmpty(param)) {
            sb.append("?");
            sb.append(param);
        }
        return sb.toString();
    }

    /**
     * 获取解析后的URL参数
     *
     * @return URL
     */
    public String getUrlParam() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = mMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            sb.append(key);
            sb.append("=");
            sb.append(mMap.get(key));
            sb.append("&");
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 向URL加上域，这里并不考虑特殊情况</br>例如：http://www.google.com/a.aspx
     * appendRegion("http://www.google.com/a.aspx","hell.aspx");
     * 所得到的是：http://www.google.com/a.aspx/hell.aspx.
     *
     * @param region
     * @return
     */
    public UrlParse appendRegion(String region) {
        String str = mHeaderBuilder.toString();
        if (str.endsWith("/")) {
            mHeaderBuilder.append(region);
        } else {
            mHeaderBuilder.append("/").append(region);
        }
        return this;
    }

    public static String encode(String url) {
        Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]");
        Matcher m = pattern.matcher(url);
        while (m.find()) {
            String cn = m.group();
            url = url.replace(cn, URLEncoder.encode(cn));
        }
        return url;
    }

    public static void download(final String url, final File dest) {
        OkHttpUtils.get().url(url).build().execute(new Callback<ResponseBody>() {
            public ResponseBody parseNetworkResponse(Response response) throws Exception {
                return response.body();
            }
            public void onError(Call call, Exception e) {
                Log.w("SyncResourcesFailed", e);
            }
            @SuppressWarnings("resource")
            public void onResponse(ResponseBody body) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = FileUtil.getSDRoot();
                try {
                    is = body.byteStream();
                    long total = body.contentLength();
                    fos = new FileOutputStream(dest);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功: " + dest.getAbsolutePath());
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败: " + url, e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
