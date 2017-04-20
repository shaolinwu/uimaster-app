package org.shaolin.uimaster.app.pay.wepay;

import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONObject;
import org.shaolin.uimaster.app.base.BaseActivity;

/**
 */
public class PayManager {

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static String APP_ID = "wxd930ea5d5a258f4f";

    public static IWXAPI getWXAPI(BaseActivity activity) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, APP_ID);
        msgApi.registerApp(APP_ID);
        return msgApi;
    }

}
