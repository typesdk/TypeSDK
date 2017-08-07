package @package@.wxapi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import @package@.R;
import com.type.sdk.android.longxia.ToastUtil;
import com.mchsdk.open.MCApiFactory;
import com.mchsdk.paysdk.config.MCHConstant;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mch_pay_result);
		api = WXAPIFactory.createWXAPI(this, MCHConstant.getInstance().getWxAppId());
		api.handleIntent(getIntent(), this);
		api.registerApp(MCHConstant.getInstance().getWxAppId());
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
	public void onResp(BaseResp baseResp) {
		String result = "";
		switch (baseResp.errCode) {
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			ToastUtil.show(getApplicationContext(), "支付失败：认证被否决");
			result="-4"+"."+"支付失败：认证被否决";
			break;
		case BaseResp.ErrCode.ERR_COMM:
			ToastUtil.show(getApplicationContext(), "支付失败：微信支付错误");
			result="-100"+"."+"支付失败：微信支付错误";
			break;
		case BaseResp.ErrCode.ERR_OK:
			ToastUtil.show(getApplicationContext(), "微信支付成功");
			result="100"+"."+"微信支付成功";
			break;
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			ToastUtil.show(getApplicationContext(), "支付失败：发送失败");
			result="-3"+"."+"支付失败：发送失败";
			break;
		case BaseResp.ErrCode.ERR_UNSUPPORT:
			ToastUtil.show(getApplicationContext(), "支付失败：不支持错误");
			result="-5"+"."+"支付失败：不支持错误";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			ToastUtil.show(getApplicationContext(), "支付失败：用户取消");
			result="0"+"."+"支付失败：用户取消";
			break;
		default:
			break;
		}
//		Log.e(TAG, "fun#onResp errCode = " + baseResp.errCode + " errStr = " + baseResp.errStr + " openId = "+ baseResp.openId);
		 if(5 == baseResp.getType()){
			 Log.e(TAG, "fun#onResp errCode = " + baseResp.errCode);
			 MCApiFactory.getMCApi().wxResult(result);
		 }
//		 }else{
//			 Toast.makeText(getApplicationContext(), "微信支付失败，请重试！", Toast.LENGTH_SHORT).show();
//		 }
		WXPayEntryActivity.this.finish();
	}
}