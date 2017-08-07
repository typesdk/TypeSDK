package com.type.sdk.android.typesdk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;

import com.goldautumn.sdk.floatview.GAGameFloat;
import com.goldautumn.sdk.minterface.GAGameResult;
import com.goldautumn.sdk.minterface.GAGameSDK;
import com.goldautumn.sdk.minterface.GAGameSDK.initCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.loginCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.logoutCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.payCallback;
import com.goldautumn.sdk.minterface.Data.ShareData;
import com.goldautumn.sdk.minterface.Finaldata;
import com.goldautumn.sdk.minterface.GAGameSDK.ShareCallback;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}
	

	public void onCreate(Context _in_context) {
		if (initState == 0) {
		TypeSDKLogger.i("initSDK: real begin");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		String buffStr = TypeSDKTool.getFromAssets(appActivity, "CPSettings.txt");
		platform.StringToData(buffStr);
		SdkInit();
		}
	}


	public void initSDK(Context _in_context, String _in_data) {
	
		TypeSDKLogger.w("initSDK: begin");

		TypeSDKLogger.w("intiSDK: initState=" + initState);
		if (initState == 0) {
			TypeSDKLogger.i("initSDK: real begin");
			appContext = _in_context;
			appActivity = (Activity) appContext;
			SdkInit();
		} else if (initState == 2) {
			TypeSDKLogger.i("error init do again");
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.Init();
			return;
		}
	}

	public void onResume() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("onResume");
		if (GAGameResult.isFloatInitSuccess()) {
			GAGameFloat.Instance().onResume();
		}
	}

	public void onPause() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("onPause");
		
		if (GAGameResult.isFloatInitSuccess()) {
			TypeSDKLogger.d("图标已初始化");
			GAGameFloat.Instance().onPause();
		}else{
			TypeSDKLogger.d("图标未初始化");
		}
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("destory");
		if (GAGameResult.isFloatInitSuccess()) {
			GAGameFloat.Instance().onDestroy();
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		TypeSDKLogger.d("onActivityResult");
		GAGameSDK.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ShowToolBar(Context _in_context) {

	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		return ShowPay(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		ShareData shareData = new ShareData();
				shareData.SetData(Finaldata.SHARE_URL, "http://www.baidu.com/");
				shareData.SetData(Finaldata.SHARE_TITLE, "test_title");
				shareData.SetData(Finaldata.SHARE_IMG_URL, "https://lh3.googleusercontent.com/5GXzlBcS3mIhBvtaXDIm8TbwgrqDNk4f4nwokooW6knIPgB6Or0WUY2-8Nfmd5SzLWE=h80-rw");
				shareData.SetData(Finaldata.SHARE_MSG, "test_msg");
				shareData.SetData(Finaldata.APP_NAME, "GAGameSDK");
				GAGameSDK.share(_in_context, appActivity, shareData, new ShareCallback() {
					
					@Override
					public void shareSuccess() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void shareFail(String error) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void shareCancel() {
						// TODO Auto-generated method stub
						
					}
				});
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		TypeSDKLogger.d("SetPlayerInfo");
		try {
			userInfo.StringToData(_in_data);
			TypeSDKLogger.d("_in_data:" + _in_data);
			TypeSDKLogger.d("userInfo:" + userInfo.DataToString());
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}

	protected boolean getLandscape(Context context) {
		if (context == null) {
			return false;
		}
		boolean landscape = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
		return landscape;
	}

	private void SdkInit() {
		initState = 1;
		TypeSDKLogger.d("SdkInit begin");
		
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				GAGameSDK.init(appContext, appActivity,
						platform.GetData(AttName.APP_ID),
						platform.GetData(AttName.APP_KEY), 
						platform.GetData("wx_appid"), 
						platform.GetData("qq_appid"), typesdkInitCallback,
						typesdkLogoutCallback);
				TypeSDKLogger.d("SDK init over");
				
			}
		});
	}

	private initCallback typesdkInitCallback = new initCallback() {

		@Override
		public void initSuccess() {
			// TODO Auto-generated method stub
			initState = 2;
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.Init();
			TypeSDKLogger.d("GAGameSDK init success");
		}

		@Override
		public void initFail() {
			// TODO Auto-generated method stub
			initState = 0;
			TypeSDKLogger.e("GAGameSDK init error");
		}

	};

	private logoutCallback typesdkLogoutCallback = new logoutCallback() {

		@Override
		public void logoutSuccess() {
			// TODO Auto-generated method stub
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.Logout();
			TypeSDKLogger.d("GAGameSDK logout success");
		}
	};

	@Override
	public void SdkLogin(Context _context) {
		TypeSDKLogger.d("SdkLogin begin");
		
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.d("SdkInit, call sdk login");
				
				GAGameSDK.login(appContext, typesdkLoginCallback);
			}
		});
	}

	private loginCallback typesdkLoginCallback = new loginCallback() {

		@Override
		public void loginSuccess(GAGameResult result) {
			// TODO Auto-generated method stub
			TypeSDKLogger.d("GAGame Login success");
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.sendToken(result.getToken(), result.getUserId());
		}

		@Override
		public void loginFail(String message) {
			// TODO Auto-generated method stub
			TypeSDKLogger.e("GAGame Login fail, message=" + message);
		}
	};

	@Override
	protected void SdkExit() {
		TypeSDKLogger.d("exit_start");
		GAGameSDK.onExit();
	}

	@Override
	protected void SdkPay(final Context _in_context, final TypeSDKData.PayInfoData _in_pay) {
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				
				TypeSDKLogger.d("receive pay data: " + _in_pay.DataToString());
				
				String item = _in_pay.GetData(AttName.ITEM_NAME);
				String price;
				if (TypeSDKTool.isPayDebug) {
					price = "1";
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE) + "";
				}
				String extrString = _in_pay.GetData(AttName.BILL_NUMBER);
				String itemDesc = _in_pay.GetData(AttName.ITEM_DESC);
				GAGameSDK.pay(appActivity, appContext,
					platform.GetData(AttName.APP_ID), item, price, extrString,
					itemDesc, new payCallback() {

					@Override
					public void paySuccess() {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("pay_success");
						SdkPaySuccess();
					}

					@Override
					public void payCancel() {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("pay_Cancel");
						SdkPayCancel();
					}

					@Override
					public void payFail() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("pay_Fail");
						SdkPayFail();
					}
				});
				
			}
		});
	}

	@Override
	protected void SdkPaySuccess() {
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
		TypeSDKNotify notify = new TypeSDKNotify();
		notify.Pay(payResult.DataToString());
	}

	@Override
	protected void SdkPayFail() {
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "0");
		payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		TypeSDKNotify notify = new TypeSDKNotify();
		notify.Pay(payResult.DataToString());
	}

	@Override
	protected void SdkPayCancel() {
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "2");
		payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_Cancel");
		TypeSDKNotify notify = new TypeSDKNotify();
		notify.Pay(payResult.DataToString());
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
		GAGameSDK.logout(appContext, appActivity, typesdkLogoutCallback);
			}
		});
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}
}
