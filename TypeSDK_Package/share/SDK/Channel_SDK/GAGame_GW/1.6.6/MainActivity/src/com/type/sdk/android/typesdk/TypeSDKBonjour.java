package com.type.sdk.android.typesdk;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;

import com.goldautumn.sdk.minterface.Data.ShareData;
import com.goldautumn.sdk.minterface.Finaldata;
import com.goldautumn.sdk.minterface.GAGameResult;
import com.goldautumn.sdk.minterface.GAGameSDK;
import com.goldautumn.sdk.minterface.GAGameSDK.initCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.loginCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.logoutCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.payCallback;
import com.goldautumn.sdk.minterface.GAGameSDK.shareCallback;
import com.goldautumn.sdk.floatview.GAGameFloat;
import com.goldautumn.sdk.minterface.GAGameSDK.inviteCallback;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.appsflyer.AFInAppEventParameterName;
import com.facebook.FacebookException;
import com.facebook.share.Sharer.Result;
import android.content.Intent;

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
	public void ShowShare(Context _in_context, final String _in_data) {
		// TODO Auto-generated method stub
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.e("POST_STATUS_UPDATE:" + _in_data);
		TypeSDKData.BaseData baseData = new TypeSDKData.BaseData();
		baseData.StringToData(_in_data);
		String shareTitle = baseData.GetData(AttName.SHARE_INFO_TITLE);
		String shareContent = baseData.GetData(AttName.SHARE_INFO_CONTENT);
		String shareImageUrl = baseData.GetData(AttName.SHARE_IMG_URL);
		String shareImageFile = "/storage/emulated/0/DCIM/Camera/IMG_20170425_130648.jpg";
		final String extra = baseData.GetData(AttName.EXTRA);
		ShareData shareData = new ShareData();
		shareData.SetData(Finaldata.SHARE_INFO_TITLE, shareTitle);
		shareData.SetData(Finaldata.SHARE_INFO_CONTENT, shareContent);
		shareData.SetData(Finaldata.SHARE_IMG_URL, shareImageUrl);
		shareData.SetData(Finaldata.SHARE_IMG_FILE, shareImageFile);
		GAGameSDK.share(shareData, new shareCallback() {

			public void shareSuccess() {
				// TODO Auto-generated method stub
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "1");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_SHARERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			}

			@Override
			public void shareFail(String error) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("shareFail:" + error.toString());
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "0");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_SHARERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
					TypeSDKLogger.e("error:" + error);
			}

			@Override
			public void shareCancel() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("shareCancel");
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "0");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_SHARERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			}
		});
			}
		});
	}
	
	public void InvitedFriend(String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("InvitedFriend:" + _in_data);
		TypeSDKData.BaseData baseData = new TypeSDKData.BaseData();
		baseData.StringToData(_in_data);
		String appLinkUrl = baseData.GetData("share_target_url");
		String previewImageUrl = baseData.GetData("share_img_url");
		final String extra = baseData.GetData(AttName.EXTRA);
		GAGameSDK.facebookInvite(appActivity, appLinkUrl,
			previewImageUrl, new inviteCallback() {
							
			@Override
			public void inviteSuccess(
				com.facebook.share.widget.AppInviteDialog.Result result) {
				// TODO Auto-generated method stub
				TypeSDKLogger.d("inviteSuccess:result:" + result.getData().toString());
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "1");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_INVITERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			}
					
			@Override
			public void inviteFail(FacebookException error) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("inviteFail:error:" + error.toString());
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "0");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_INVITERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			}
					
			@Override
			public void inviteCancel() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("inviteCancel");
				TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
				resultData.SetData(AttName.RESULT, "0");
				resultData.SetData(AttName.EXTRA, extra);
				TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_SHARE_RESULT, ReceiveFunction.MSG_INVITERESULT, 
					resultData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
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
						platform.GetData("payPalId"),
						true, 
						platform.GetData("twitter_key"),
						platform.GetData("Twitter_secret"), 
						platform.GetData("appsFlyer"), 
						typesdkInitCallback,
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
	protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay) {
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
		
		String item_id = _in_pay.GetData(AttName.ITEM_SERVER_ID);
		
		GAGameSDK.pay(appActivity, appContext,
				platform.GetData(AttName.APP_ID), item, price, extrString,
				itemDesc, item_id, new payCallback() {

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
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		GAGameSDK.onActivityResult(requestCode, resultCode, data);

	}
}
