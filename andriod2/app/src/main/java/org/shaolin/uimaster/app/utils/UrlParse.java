package org.shaolin.uimaster.app.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import org.shaolin.uimaster.app.aty.MainActivity;
import org.shaolin.uimaster.app.fragment.AjaxContext;

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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

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

    public static void uploadImage(final AjaxContext ajaxContext, final Context context, final String url, final File file, final Map<String, Object> map) {
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            //RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            //String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("headImage", file.getName(), createCustomRequestBody(MediaType.parse("image/*"), file, ajaxContext));
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    requestBody.addFormDataPart(entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).tag(context).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("UIMaster" ,"onFailure", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Log.i("UIMaster", response.message() + " , body " + str);
                    //invoke javascript listener.
                    ajaxContext.fileUploaded(1);
                    //Toast.makeText(context, "上传成功！", Toast.LENGTH_SHORT);
                } else {
                    ajaxContext.fileUploaded(0);
                    //Toast.makeText(context, "上传失败！", Toast.LENGTH_SHORT);
                    Log.i("UIMaster" ,"upload file error: body " + response.body().string());
                }
            }
        });

    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final AjaxContext ajaxContext) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return contentType;
            }

            @Override public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        ajaxContext.onProgress(contentLength(), remaining -= readCount, remaining == 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * http://blog.csdn.net/qq_18833399/article/details/51555000
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     **
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
