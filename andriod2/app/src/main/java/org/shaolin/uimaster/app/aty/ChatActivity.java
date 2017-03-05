package org.shaolin.uimaster.app.aty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.shaolin.uimaster.app.R;
import org.shaolin.uimaster.app.base.BaseActivity;
import org.shaolin.uimaster.app.data.ConfigData;
import org.shaolin.uimaster.app.fragment.AjaxContext;
import org.shaolin.uimaster.app.fragment.WebFragment;
import org.shaolin.uimaster.app.push.NoticePushUtil;
import org.shaolin.uimaster.app.utils.PreferencesUtils;
import org.shaolin.uimaster.app.viewmodule.impl.HTMLPresenterImpl;
import org.shaolin.uimaster.app.viewmodule.inter.IHTMLWebView;

import java.net.URISyntaxException;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/1/22.
 */

public class ChatActivity extends BaseActivity {

    private Socket mSocket;

    private String userId;

    private String sessionId;

    private String toUserId;

    private String orgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    private void initView() {

    }

    private void initData(Bundle bundle) {
        sessionId = bundle.getString("sessionId");
        userId = bundle.getString("userId");//this.sentPartyIdUI.value;
        toUserId = bundle.getString("toUserId");//this.receivedPartyIdUI.value;
        orgId = bundle.getString("orgId");//this.orgIdUI.value;

        try {
            mSocket = IO.socket("https://www.vogerp.com:8090");
            mSocket.on("connect", connect);
            mSocket.on("history", history);
            mSocket.on("chatTo", chat);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener connect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fromPartyId",userId);
                jsonObject.put("toPartyId",toUserId);
                jsonObject.put("sessionId",sessionId);
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
                    for (int i=0; i<data.length(); i++) {
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
            JSONObject jsonObject = (JSONObject)args[0];
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
}
