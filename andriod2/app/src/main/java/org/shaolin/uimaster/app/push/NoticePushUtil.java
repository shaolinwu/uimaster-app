package org.shaolin.uimaster.app.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.aty.WebViewActivity;
import org.shaolin.uimaster.app.base.BasePresenterImpl;
import org.shaolin.uimaster.app.bean.NoticeBean;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.utils.PreferencesUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/3/3.
 */

public class NoticePushUtil {

    public static  NoticePushUtil mInstance;
    private NotificationManager manager;
    private int id = 0;


    public NoticePushUtil(Context context){
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public static NoticePushUtil getInstance(Context context){
        if (mInstance == null){
            mInstance = new NoticePushUtil(context);
        }

        return mInstance;
    }

    public void  showNoticePush(Context context, String response){
        if (!TextUtils.isEmpty(response)){
            List<NoticeBean> noticeList = BasePresenterImpl.jsonToArrayList(response,NoticeBean.class);
            if (noticeList != null && noticeList.size() != 0) {
                for (NoticeBean bean : noticeList) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("url", PreferencesUtils.getString(context, ConfigData.MESSAGE_ACTIVITY_URL));
                    intent.putExtra("title", PreferencesUtils.getString(context, ConfigData.MESSAGE_ACTIVITY_TITLE));

                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent, 0);
                    // 通过Notification.Builder来创建通知，注意API Level
                    // API11之后才支持
                    Notification notify2 = new Notification.Builder(context)
                            .setTicker("您有新短消息，请注意查收！")// 设置在status
                            .setSmallIcon(R.mipmap.ic_launcher)
                            // bar上显示的提示文字
                            .setContentTitle(bean.SUBJECT)// 设置在下拉status
                            // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                            .setContentText(escapeHtml(bean.DESCRIPTION))// TextView中显示的详细内容
                            .setContentIntent(pendingIntent2) // 关联PendingIntent
                            .getNotification(); // 需要注意build()是在API level 16及之后增加的，在API11中可以使用getNotificatin()来代替
                    notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                    manager.notify(id++, notify2);
                }
            }
        }
    }

    private static String escapeHtml(String description) {
        StringBuffer sb = new StringBuffer();
        String[] htmlClips = description.split("<");
        for (String clip : htmlClips) {
            if (clip.indexOf('>') >= 0 ){
                sb.append(clip.substring(clip.indexOf('>')+1));
            } else {
                sb.append(clip);
            }
        }
        return sb.toString();
    }

    public void showChatPush(Context context, JSONObject jsonObject) throws JSONException {
        // 通过Notification.Builder来创建通知，注意API Level
        // API11之后才支持
        Notification notify2 = new Notification.Builder(context)
                .setTicker("您有新短消息，请注意查收！")// 设置在status
                .setSmallIcon(R.mipmap.ic_launcher)
                // bar上显示的提示文字
                .setContentTitle("来信留言: " + jsonObject.getString("fromPartyName") + "--" + jsonObject.getString("content"))// 设置在下拉status
                .getNotification(); // 需要注意build()是在API level 16及之后增加的，在API11中可以使用getNotificatin()来代替
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;
        long[] vibrates = { 0, 1000, 1000, 1000 };
        notify2.vibrate = vibrates;
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        notify2.sound = uri;
        //notify2.defaults = Notification.DEFAULT_ALL;
        manager.notify(id++, notify2);
    }

}
