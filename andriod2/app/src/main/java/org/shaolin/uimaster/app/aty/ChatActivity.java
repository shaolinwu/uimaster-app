package org.shaolin.uimaster.app.aty;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

import static com.zhy.http.okhttp.OkHttpUtils.delete;

/**
 * Created by Administrator on 2017/1/22.
 */

public class ChatActivity extends BaseActivity implements View.OnClickListener, DropdownListView.OnRefreshListenerHeader {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_order_info)
    TextView tvOrderInfo;
    @BindView(R.id.tv_order_price)
    TextView tvOrderPrice;
    @BindView(R.id.tv_order_sender)
    TextView tvOrderSender;
    @BindView(R.id.tv_sender_recipient)
    TextView tvSenderRecipient;
    @BindView(R.id.tv_order_)
    TextView tvOrder;
    @BindView(R.id.tv_sender_name)
    TextView tvSenderName;
    @BindView(R.id.image_face)
    ImageView imageFace;
    @BindView(R.id.send_sms)
    Button sendSms;
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

    private Socket mSocket;

    private String userId;

    private String sessionId;

    private String toUserId;

    private String orgId;

    private SimpleDateFormat sd;
    private LinkedList<ChatInfo> infos = new LinkedList<ChatInfo>();
    private ChatLVAdapter mLvAdapter;
    private List<String> staticFacesList;
    // 7列3行
    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<View>();
    private String reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(savedInstanceState);
        initListener();
        initStaticFaces();
        initViewPager();
    }



    private void initListener() {
        inputSms.setOnClickListener(this);
        imageFace.setOnClickListener(this);
        sendSms.setOnClickListener(this);
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    private void initView() {

    }

    private void initData(Bundle bundle) {
//        sessionId = bundle.getString("sessionId");
//        userId = bundle.getString("userId");//this.sentPartyIdUI.value;
//        toUserId = bundle.getString("toUserId");//this.receivedPartyIdUI.value;
//        orgId = bundle.getString("orgId");//this.orgIdUI.value;
//
//        try {
//            mSocket = IO.socket("https://www.vogerp.com:8090");
//            mSocket.on("connect", connect);
//            mSocket.on("history", history);
//            mSocket.on("chatTo", chat);
//            mSocket.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

        sd = new SimpleDateFormat("MM-dd HH:mm");
        //模拟收到信息
        infos.add(getChatInfoFrom("你好啊！"));
        infos.add(getChatInfoFrom("认识你很高兴#[face/png/f_static_018.png]#"));
        mLvAdapter = new ChatLVAdapter(this, infos);
        mListView.setAdapter(mLvAdapter);
        mViewPager.setOnPageChangeListener(new PageChange());
    }

    private Emitter.Listener connect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fromPartyId", userId);
                jsonObject.put("toPartyId", toUserId);
                jsonObject.put("sessionId", sessionId);
                mSocket.emit("history", jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener history = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray data = (JSONArray) args[0];
                    for (int i = 0; i < data.length(); i++) {
                        //TODO:
//                var color = ((i%2==0)?"uimaster_chat_item_even":"uimaster_chat_item_old");
//                var row = "<div class=\"swiper-slide uimaster_chat_item_to "+color+"\"><div><div class=\"uimaster_chat_time\">"
//                        + e[i].CREATEDATE + "</div><div class=\"uimaster_chat_message\"> " + e[i].MESSAGE + "</div></div></div>"

                    }
                }
            });
        }
    };

    private Emitter.Listener chat = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject jsonObject = (JSONObject) args[0];
//            var row = "<div class=\"swiper-slide uimaster_chat_item_to "+color+"\"><div><div class=\"uimaster_chat_time\">"
//                    + new Date() + "</div><div class=\"uimaster_chat_message\"> " + e.content + "</div></div></div>"
            //TODO
        }
    };

    private void sendMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("taskId", 0);
            jsonObject.put("fromPartyId", userId);
            jsonObject.put("toPartyId", toUserId);
            jsonObject.put("sessionId", sessionId);
            jsonObject.put("content", message);

            mSocket.emit("chatTo", jsonObject);
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

    /*------------------------------------------------------------------*/

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mLvAdapter.setList(infos);
                    mLvAdapter.notifyDataSetChanged();
                    mListView.onRefreshCompleteHeader();
                    break;
            }
        }
    };

    /**
     * 接收的信息
     * @param message
     * @return
     */
    private ChatInfo getChatInfoFrom(String message) {
        ChatInfo info = new ChatInfo();
        info.content = message;
        info.fromOrTo = 0;
        info.time=sd.format(new Date());
        return info;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.input_sms:
                if(chat_face_container.getVisibility()==View.VISIBLE){
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://表情
                hideSoftInputView();//隐藏软键盘
                if(chat_face_container.getVisibility()==View.GONE){
                    chat_face_container.setVisibility(View.VISIBLE);
                }else{
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.send_sms://发送
                reply=inputSms.getText().toString();
                if (!TextUtils.isEmpty(reply)) {
                    infos.add(getChatInfoTo(reply));
                    mLvAdapter.setList(infos);
                    mLvAdapter.notifyDataSetChanged();
                    mListView.setSelection(infos.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            infos.add(getChatInfoFrom(reply));
                            mLvAdapter.setList(infos);
                            mLvAdapter.notifyDataSetChanged();
                            mListView.setSelection(infos.size() - 1);
                        }
                    }, 1000);
                    inputSms.setText("");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    Message msg = mHandler.obtainMessage(0);
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
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
}
