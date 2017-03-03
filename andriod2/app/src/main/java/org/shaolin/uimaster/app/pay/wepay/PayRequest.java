package org.shaolin.uimaster.app.pay.wepay;

import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONObject;

/**
 * Created by wushaol on 2017/3/2.
 */
public class PayRequest {

    public void request (Context context, JSONObject orderData) {
        final IWXAPI wxapi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
        wxapi.registerApp("wxd930ea5d5a258f4f");

        PayReq request = new PayReq();
        request.appId = "wxd930ea5d5a258f4f";
        request.partnerId = "1900000109";
        request.prepayId= "1101000000140415649af9fc314aa427";
        request.packageValue = "Sign=WXPay";
        request.nonceStr= "1101000000140429eb40476f8896f4c9";
        request.timeStamp= "1398746574";
        request.sign= "7FFECB600D7157C5AA49810D2D8F28BC2811827B";
        wxapi.sendReq(request);

    }

}
