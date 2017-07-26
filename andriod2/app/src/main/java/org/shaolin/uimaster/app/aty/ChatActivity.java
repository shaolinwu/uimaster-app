package org.shaolin.uimaster.app.aty;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.bean.ChatInfo;
import org.shaolin.uimaster.app.chatview.adapter.ChatLVAdapter;
import org.shaolin.uimaster.app.chatview.adapter.FaceGVAdapter;
import org.shaolin.uimaster.app.chatview.adapter.FaceVPAdapter;
import org.shaolin.uimaster.app.chatview.view.DropdownListView;
import org.shaolin.uimaster.app.chatview.view.MyEditText;
import org.shaolin.uimaster.app.chatview.view.RecordButton;
import org.shaolin.uimaster.app.data.FileData;
import org.shaolin.uimaster.app.data.URLData;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.push.NoticePushUtil;
import org.shaolin.uimaster.app.utils.UrlParse;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.zhy.http.okhttp.OkHttpUtils.delete;

/**
 * Created by Administrator on 2017/1/22.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener,
        DropdownListView.OnRefreshListenerHeader, RecordButton.OnFinishedRecordListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_order_info)
    TextView tvOrderInfo;
    @BindView(R.id.tv_order_price)
    TextView tvOrderPrice;
    @BindView(R.id.tv_sender_name)
    TextView tvSenderName;
    @BindView(R.id.tv_receiver_name)
    TextView tvReceiverName;
    @BindView(R.id.image_face)
    ImageView imageFace;
    @BindView(R.id.image_voice)
    ImageView imageVoice;
    @BindView(R.id.send_sms)
    Button sendSms;
    @BindView(R.id.recordButton)
    RecordButton recordButton;
    @BindView(R.id.face_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.face_dots_container)
    LinearLayout mDotsLayout;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.message_chat_listview)
    DropdownListView mListView;
    @BindView(R.id.input_sms)
    MyEditText inputSms;
    @BindView(R.id.chat_face_container)
    LinearLayout chat_face_container;

    private boolean isAdmin;
    private long orgId;
    private long userId;
    public static String sessionId; //this is shared for status check
    private long taskId;
    private long toUserId;
    private String userName;
    private String toUserName;

    private SimpleDateFormat sd = new SimpleDateFormat("MM-dd HH:mm");
    private LinkedList<ChatInfo> infos = new LinkedList<ChatInfo>();
    private ChatLVAdapter mLvAdapter;
    private List<String> staticFacesList;
    // 7列3行
    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<View>();
    private String reply;
    ChatActivity activity = this;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        initView();
        initData(getIntent().getBundleExtra(WebViewDialogActivity.BUNDLE_KEY_ARGS));
        initListener();
        initStaticFaces();
        initViewPager();
    }

    private void initListener() {
        inputSms.setOnClickListener(this);
        imageFace.setOnClickListener(this);
        sendSms.setOnClickListener(this);
        imageVoice.setOnClickListener(this);
        mListView.setOnRefreshListenerHead(this);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(arg1.getAction()==MotionEvent.ACTION_DOWN){
                    if(chat_face_container.getVisibility()==View.VISIBLE){
                        chat_face_container.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        });
        recordButton.setSavePath(FileData.APP_AUDIO_ROOT + userId+"/"+sessionId);
        recordButton.setOnFinishedRecordListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    private void initView() {
    }

    private void initData(Bundle bundle) {
        isAdmin = bundle.getBoolean("isAdmin");
        sessionId = bundle.getString("sessionId");
        orgId = bundle.getLong("orgId");
        taskId = bundle.getLong("taskId");
        userId = bundle.getLong("sentPartyId");
        toUserId = bundle.getLong("receivedPartyId");
        userName = bundle.getString("sentPartyName");
        toUserName = bundle.getString("recievedPartyName");
        tvSenderName.setText(userName);
        tvReceiverName.setText(toUserName);
        if (bundle.getString("orderInfo") != null) {
            tvOrderInfo.setText("订单信息： " + bundle.getString("orderInfo"));
        } else {
            tvOrderInfo.setText("没有相关订单信息。");
        }
        if (bundle.getString("price") != null) {
            tvOrderPrice.setText("当前出价： " + bundle.getString("price"));
        } else {
            tvOrderPrice.setText("当前出价： 未知");
        }
        Socket mSocket = AjaxContext.getWebService();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromPartyId", userId);
            jsonObject.put("toPartyId", toUserId);
            jsonObject.put("sessionId", sessionId);
            mSocket.emit("history", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.on("history", history);
        mSocket.on("chatTo", chat);

        mLvAdapter = new ChatLVAdapter(this, infos);
        mListView.setAdapter(mLvAdapter);
        mViewPager.setOnPageChangeListener(new PageChange());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionId = null;
        vibrator.cancel();
        Socket mSocket = AjaxContext.getWebService();
        mSocket.off("history", history);
        mSocket.off("chatTo", chat);
    }

    private Emitter.Listener history = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        ChatActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONArray data = (JSONArray) args[0];
                for (int i = 0; i < data.length(); i++) {
                    try {
                        if (data.getJSONObject(i).getLong("RECEIVEDPARTYID") == toUserId) {
                            infos.add(getChatInfoTo(data.getJSONObject(i).getString("CREATEDATE"), data.getJSONObject(i).getString("MESSAGE")));
                        } else {
                            infos.add(getChatInfoFrom(data.getJSONObject(i).getString("CREATEDATE"), data.getJSONObject(i).getString("MESSAGE")));
                        }
                        mLvAdapter.setList(infos);
                        mLvAdapter.notifyDataSetChanged();
                        mListView.onRefreshCompleteHeader();
                        mListView.setSelection(infos.size() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        }
    };

    private Emitter.Listener chat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // received message
            JSONObject jsonObject = (JSONObject) args[0];
            try {
                long [] pattern = {100,400};   // 停止 开启
                vibrator.vibrate(pattern, 1);
                if (jsonObject.getString("sessionId").equals(sessionId)) {
                    // this is the session message.
                    infos.add(getChatInfoFrom(sd.format(new Date()), jsonObject.getString("content")));
                    mLvAdapter.setList(infos);
                    mLvAdapter.notifyDataSetChanged();
                    mListView.onRefreshCompleteHeader();
                    mListView.setSelection(infos.size() - 1);
                } else {
                    // this is another session message
                    NoticePushUtil.getInstance(activity).showChatPush(activity, jsonObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void sendMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("taskId", taskId);
            jsonObject.put("orgId", orgId);
            jsonObject.put("fromPartyId", userId);
            jsonObject.put("fromPartyName", userName);
            jsonObject.put("toPartyId", toUserId);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("content", message);

            AjaxContext.getWebService().emit("chatTo", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    /**
     * 接收的信息
     * @param message
     * @return
     */
    private ChatInfo getChatInfoFrom(String date, String message) {
        ChatInfo info = new ChatInfo();
        info.content = message;
        info.fromOrTo = 0;
        info.time= date;
        return info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.input_sms:
                if(chat_face_container.getVisibility()==View.VISIBLE){
                    chat_face_container.setVisibility(View.GONE);
                }
                if(recordButton.getVisibility()==View.VISIBLE){
                    recordButton.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://表情
                hideSoftInputView();//隐藏软键盘
                if(chat_face_container.getVisibility()==View.GONE){
                    chat_face_container.setVisibility(View.VISIBLE);
                }else{
                    chat_face_container.setVisibility(View.GONE);
                }
                if(recordButton.getVisibility()==View.VISIBLE){
                    recordButton.setVisibility(View.GONE);
                } else {
                    recordButton.setVisibility(View.VISIBLE);
                    inputSms.setVisibility(View.GONE);
                }
                break;
            case R.id.image_voice://声音
                hideSoftInputView();//隐藏软键盘
                if(recordButton.getVisibility()==View.GONE){
                    recordButton.setVisibility(View.VISIBLE);
                    inputSms.setVisibility(View.GONE);
                }else{
                    recordButton.setVisibility(View.GONE);
                    inputSms.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.send_sms://发送
                reply=inputSms.getText().toString();
                if (!TextUtils.isEmpty(reply)) {
                    sendMessage(reply);
                    infos.add(getChatInfoTo(reply));
                    mLvAdapter.setList(infos);
                    mLvAdapter.notifyDataSetChanged();
                    mListView.setSelection(infos.size() - 1);

                    inputSms.setText("");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     * */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
    }

    /*
	 * 初始表情 *
	 */

    private void initViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     * @return
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }

    /**
     * 向输入框里添加表情
     * */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((inputSms.getText()));
        int iCursorEnd = Selection.getSelectionEnd((inputSms.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) inputSms.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((inputSms.getText()));
        ((Editable) inputSms.getText()).insert(iCursor, text);
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 发送的信息
     * @param message
     * @return
     */
    private ChatInfo getChatInfoTo(String message) {
        ChatInfo info = new ChatInfo();
        info.content = message;
        info.fromOrTo = 1;
        info.time=sd.format(new Date());
        return info;
    }

    private ChatInfo getChatInfoTo(String date, String message) {
        ChatInfo info = new ChatInfo();
        info.content = message;
        info.fromOrTo = 1;
        info.time= date;
        return info;
    }

    /**
     * 初始化表情列表staticFacesList
     */
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinishedRecord(File audioFile) {
        Log.i("RECORD!!!", "finished!!!!!!!!!! save to " + audioFile.getAbsolutePath());

        uploadAMRRecord(activity, URLData.CHAT_SEND_AUDIO_URL + "?uid="+userId+"&sid="+sessionId, audioFile);
    }

    public void uploadAMRRecord(final Context context, final String url, final File uploadFile) {
        if (uploadFile == null || !uploadFile.exists()) {
            return;
        }
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody body = UrlParse.createCustomRequestBody(MediaType.parse("audio/amr"), uploadFile, null);
        requestBody.addFormDataPart("headImage", uploadFile.getName(), body);
        //requestBody.addFormDataPart("filename", "audio");

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
                    //Toast.makeText(context, "上传成功！", Toast.LENGTH_SHORT);
                } else {
                    //Toast.makeText(context, "上传失败！", Toast.LENGTH_SHORT);
                    Log.i("UIMaster" ,"upload uploadFile error: body " + response.body().string());
                }

                //send message
                sendMessage("[/audio]:"+userId+"/"+sessionId + "/" + uploadFile.getName());
                infos.add(getChatInfoTo("[/audio]:"+userId+"/"+sessionId + "/" + uploadFile.getName()));
                mLvAdapter.setList(infos);
                mListView.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       mLvAdapter.notifyDataSetChanged();
                                       mListView.setSelection(infos.size() - 1);
                                   }
                               });
                }
        });

    }
}
