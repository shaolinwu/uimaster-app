package org.shaolin.uimaster.app.broadcast;

import org.shaolin.uimaster.app.service.NoticeUtils;
import org.shaolin.uimaster.app.util.TLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//TLog.log("onReceive ->org.shaolin.uimaster.app收到定时获取消息");
		NoticeUtils.requestNotice(context);
	}
}
