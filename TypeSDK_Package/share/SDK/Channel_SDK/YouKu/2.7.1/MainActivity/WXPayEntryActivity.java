package @package@.wxapi;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import @package@.R;
import com.youku.gamesdk.act.YKPlatform;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, YKPlatform.getWXappid());

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
				try {
				synchronized(this){
						Intent intent = new Intent();
						intent.setAction("yk_tenpay_callback" + YKPlatform.getAppId());
						if(resp.errCode==0){
							intent.putExtra("isSuccess", "支付成功");
						}else{
							intent.putExtra("isSuccess", "支付失败");
						}
						intent.putExtra("isRecharge", YKPlatform.getIsRecharge());
						//发送 一个无序广播
						sendBroadcast(intent);
						finish();
						return ;
					}
				} catch (Exception e) {
			}
			builder.show();
		}
		finish();
	}
}