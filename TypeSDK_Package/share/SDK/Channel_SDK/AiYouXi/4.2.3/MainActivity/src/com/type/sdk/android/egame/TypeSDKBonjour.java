package com.type.sdk.android.egame;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import cn.egame.terminal.paysdk.EgameExitListener;
import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

import egame.terminal.usersdk.CallBackListener;
import egame.terminal.usersdk.EgameUser;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		TypeSDKLogger.i("isInit=" + isInit);
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_EGame notify = new TypeSDKNotify_EGame();
			notify.Init();
			return;
		}
		eGameSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.eGameSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.eGameSdkLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.eGameSdkPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);

		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
	}

	@Override
	public void ExitGame(Context _in_context) {
		eGameSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		EgameUser.onResume(appActivity);
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
		EgameUser.onPause(appActivity);
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
	}

	private void eGameSdkInit() {
		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				EgamePay.init(appActivity);
				isInit = true;
				TypeSDKNotify_EGame notify = new TypeSDKNotify_EGame();
				notify.Init();
			}
		});
	}

	private void eGameSdkLogin() {
		TypeSDKLogger.e("eGameSdkLogin start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				EgameUser.start(appActivity,
						Integer.parseInt(platform.GetData(AttName.APP_ID)),
						new CallBackListener() {

							@Override
							public void onSuccess(String code) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login code:" + code);
								TypeSDKNotify_EGame notify = new TypeSDKNotify_EGame();
								notify.sendToken(code, null);
							}

							@Override
							public void onFailed(int code) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login onFailed:errorCode:" + code);
							}

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login onCancel");
							}
						});
			}
		});

	}

	private void eGameSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKNotify_EGame notify = new TypeSDKNotify_EGame();
				notify.Logout();
				TypeSDKLogger.e("LOGOUT SUCCESS");
			}
		});
	}

	private void eGameSdkExit() {
		TypeSDKLogger.i("exit game");
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				EgamePay.exit(appActivity, new EgameExitListener() {
					
					@Override
					public void exit() {
						// TODO Auto-generated method stub
						System.exit(0);
					}
					
					@Override
					public void cancel() {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("exit cancel");
					}
				});
			}
		});
	}

	private void eGameSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String price = "0";
				if(TypeSDKTool.isPayDebug){
					price = "1";
				}else{
					price = "" + (_in_pay.GetInt(AttName.REAL_PRICE) / 100);
				}
				
				HashMap<String, String> payParams=new HashMap<String, String>();
				payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE, price);
				payParams.put(EgamePay.PAY_PARAMS_KEY_CP_PARAMS, _in_pay.GetData(AttName.BILL_NUMBER));
				payParams.put(EgamePay.PAY_PARAMS_KEY_PRIORITY, "other");//sms表示优先选择话费支付，other表示优先出现支付列表
				payParams.put(EgamePay.PAY_PARAMS_KEY_USE_SMSPAY, "false");//表示此次计费屏蔽话费支付，如果此时只有话费支付这一个支付通道，sdk会返回-108无可用支付通道，若不设置则默认显示短代支付。
	            
				EgamePay.pay(appActivity, payParams, new EgamePayListener() {
					
					PayResultData payResult = new PayResultData();
					TypeSDKNotify_EGame notify = new TypeSDKNotify_EGame();
					@Override
					public void paySuccess(Map<String, String> arg0) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("paySuccess");
						payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
						notify.Pay(payResult.DataToString());
					}
					
					@Override
					public void payFailed(Map<String, String> arg0, int arg1) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("payFail:errorCode:" + arg1);
						payResult.SetData(AttName.PAY_RESULT, "0");
            			payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
						notify.Pay(payResult.DataToString());
						
					}
					
					@Override
					public void payCancel(Map<String, String> arg0) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("payCancel");
						payResult.SetData(AttName.PAY_RESULT, "2");
            			payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_Cancel");
						notify.Pay(payResult.DataToString());
					}
				});
			}
		});
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		SetPlayerInfo(_in_context, _in_data);
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

}
