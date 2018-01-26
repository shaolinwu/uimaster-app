package org.shaolin.uimaster.app.aty;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
	}

	@Override
	public void onReq(BaseReq req) {
		Toast.makeText(this, "basereq.getType = " + req.getType() + ", transaction = " + req.transaction, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		
		Toast.makeText(this, "baseresp.getType = " + resp.getType() + ", errCode = " + resp.errCode, Toast.LENGTH_SHORT).show();
	}
	
}