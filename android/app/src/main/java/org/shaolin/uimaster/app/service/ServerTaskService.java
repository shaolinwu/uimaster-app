package org.shaolin.uimaster.app.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.api.AsyncResponseHandler;
import org.shaolin.uimaster.app.api.remote.RService;
import org.shaolin.uimaster.app.bean.Comment;
import org.shaolin.uimaster.app.bean.Result;
import org.shaolin.uimaster.app.bean.ResultBean;
import org.shaolin.uimaster.app.util.XmlUtils;


import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class ServerTaskService extends IntentService {
    private static final String SERVICE_NAME = "ServerTaskService";
    public static final String ACTION_PUB_BLOG_COMMENT = "org.shaolin.uimaster.app.ACTION_PUB_BLOG_COMMENT";
    public static final String ACTION_PUB_COMMENT = "org.shaolin.uimaster.app.ACTION_PUB_COMMENT";
    public static final String ACTION_PUB_POST = "org.shaolin.uimaster.app.ACTION_PUB_POST";
    public static final String ACTION_PUB_TWEET = "org.shaolin.uimaster.app.ACTION_PUB_TWEET";
    public static final String ACTION_PUB_SOFTWARE_TWEET = "org.shaolin.uimaster.app.ACTION_PUB_SOFTWARE_TWEET";

    public static final String KEY_ADAPTER = "adapter";

    public static final String BUNDLE_PUB_COMMENT_TASK = "BUNDLE_PUB_COMMENT_TASK";
    public static final String BUNDLE_PUB_POST_TASK = "BUNDLE_PUB_POST_TASK";
    public static final String BUNDLE_PUB_TWEET_TASK = "BUNDLE_PUB_TWEET_TASK";
    public static final String BUNDLE_PUB_SOFTWARE_TWEET_TASK = "BUNDLE_PUB_SOFTWARE_TWEET_TASK";
    public static final String KEY_SOFTID = "soft_id";

    private static final String KEY_COMMENT = "comment_";
    private static final String KEY_TWEET = "tweet_";
    private static final String KEY_SOFTWARE_TWEET = "software_tweet_";
    private static final String KEY_POST = "post_";

    public static List<String> penddingTasks = new ArrayList<String>();

	public ServerTaskService() {
		super("vogerp");
	}

    class PublicCommentResponseHandler extends AsyncResponseHandler {

	public PublicCommentResponseHandler(Looper looper, Object... args) {
	    super(looper, args);
	}

	@Override
	public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
		throws Exception {
	    PublicCommentTask task = (PublicCommentTask) args[0];
	    final int id = task.getId() * task.getUid();
	    ResultBean resB = XmlUtils.toBean(ResultBean.class, is);
	    Result res = resB.getResult();
	    if (res.OK()) {
		final Comment comment = resB.getComment();
		// UIHelper.sendBroadCastCommentChanged(ServerTaskService.this,
		// isBlog, task.getId(), task.getCatalog(),
		// Comment.OPT_ADD, comment);
		notifySimpleNotifycation(id,
			getString(R.string.comment_publish_success),
			getString(R.string.comment_blog),
			getString(R.string.comment_publish_success), false,
			true);
		removePenddingTask(KEY_COMMENT + id);
	    } else {
		onFailure(100, res.getErrorMessage(), args);
	    }
	}

	@Override
	public void onFailure(int code, String errorMessage, Object[] args) {
	    PublicCommentTask task = (PublicCommentTask) args[0];
	    int id = task.getId() * task.getUid();
	    notifySimpleNotifycation(id,
		    getString(R.string.comment_publish_faile),
		    getString(R.string.comment_blog),
		    code == 100 ? errorMessage
			    : getString(R.string.comment_publish_faile), false,
		    true);
	    removePenddingTask(KEY_COMMENT + id);
	}

	@Override
	public void onFinish() {
	    tryToStopServie();
	}
    }

    private synchronized void tryToStopServie() {
	if (penddingTasks == null || penddingTasks.size() == 0) {
	    stopSelf();
	}
    }

    private synchronized void addPenddingTask(String key) {
	penddingTasks.add(key);
    }

    private synchronized void removePenddingTask(String key) {
	penddingTasks.remove(key);
    }

    public ServerTaskService(String name) {
	super(name);
    }

    @Override
    public void onCreate() {
	super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
	String action = intent.getAction();

	if (ACTION_PUB_BLOG_COMMENT.equals(action)) {
	    PublicCommentTask task = intent
		    .getParcelableExtra(BUNDLE_PUB_COMMENT_TASK);
	    if (task != null) {
		publicBlogComment(task);
	    }
	} else if (ACTION_PUB_COMMENT.equals(action)) {
	    PublicCommentTask task = intent
		    .getParcelableExtra(BUNDLE_PUB_COMMENT_TASK);
	    if (task != null) {
		publicComment(task);
	    }
	}
    }

    private void publicBlogComment(final PublicCommentTask task) {

    }

    private void publicComment(final PublicCommentTask task) {
//	int id = task.getId() * task.getUid();
//	addPenddingTask(KEY_COMMENT + id);
//
//	notifySimpleNotifycation(id, getString(R.string.comment_publishing),
//		getString(R.string.comment_blog),
//		getString(R.string.comment_publishing), true, false);
//
//	RService.publicComment(task.getCatalog(), task.getId(),
//			task.getUid(), task.getContent(), task.getIsPostToMyZone(),
//			new PublicCommentResponseHandler(getMainLooper(), task, false));
    }


    private void notifySimpleNotifycation(int id, String ticker, String title,
	    String content, boolean ongoing, boolean autoCancel) {
	NotificationCompat.Builder builder = new NotificationCompat.Builder(
		this)
		.setTicker(ticker)
		.setContentTitle(title)
		.setContentText(content)
		.setAutoCancel(true)
		.setOngoing(false)
		.setOnlyAlertOnce(true)
		.setContentIntent(
			PendingIntent.getActivity(this, 0, new Intent(), 0))
		.setSmallIcon(R.drawable.ic_notification);

	// if (AppContext.isNotificationSoundEnable()) {
	// builder.setDefaults(Notification.DEFAULT_SOUND);
	// }

	Notification notification = builder.build();

	NotificationManagerCompat.from(this).notify(id, notification);
    }

}
