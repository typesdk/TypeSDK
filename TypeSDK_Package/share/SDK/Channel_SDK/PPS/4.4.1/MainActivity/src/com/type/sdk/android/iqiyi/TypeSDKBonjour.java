package com.type.sdk.android.iqiyi;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.iqiyi.sdk.listener.LoginListener;
import com.iqiyi.sdk.listener.PayListener;
import com.iqiyi.sdk.platform.GamePlatform;
import com.iqiyi.sdk.platform.GamePlatformInitListener;
import com.iqiyi.sdk.platform.GameSDKResultCode;
import com.iqiyi.sdk.platform.GameUser;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private static GamePlatform gamePlatform;  
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
			notify.Init();
			return;
		}
		isInit = true;
		this.ppsInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.ppsLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowLogout");
		ppsLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowPersonCenter");
		ppsShowUserCenter();
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.d("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.ppsPay(_in_pay);

		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);

		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			String server_id = platform.GetData(AttName.SERVER_ID);
			if (server_id == null || server_id=="") {
				server_id = "1";
			}
			gamePlatform.createRole(appContext, "ppsmobile_s" + server_id);
			gamePlatform.enterGame(appContext, "ppsmobile_s" + server_id);
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		this.ppsExit();
		
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void ppsInit() {

		TypeSDKLogger.i("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.d("initSDK_start");
					// SDK初始化
					gamePlatform = GamePlatform.getInstance();
					gamePlatform.initPlatform(appActivity, platform.GetData(AttName.APP_ID), new GamePlatformInitListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							TypeSDKLogger.d("init success");
							TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
							notify.Init();
							gamePlatform.addLoginListener(new LoginListener() {
								
								@Override
								public void exitGame() {
									// TODO Auto-generated method stub
									//退出游戏时回调
									TypeSDKLogger.d("exitGame");
									appActivity.finish();
								}
								
								@Override
								public void logout() {
									// TODO Auto-generated method stub
									//注销帐号时回调
									TypeSDKLogger.d("logout_success");
									TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
									notify.Logout();
								}
								
								@Override
								public void loginResult(int arg0, GameUser user) {
									// TODO Auto-generated method stub
									//登录结果回调
									gamePlatform.initSliderBar(appActivity);
									TypeSDKLogger.d("ppsLogin loginResult");
									TypeSDKLogger.d("login success");
									TypeSDKLogger.d("sign:" + user.sign);
									TypeSDKLogger.d("userId:" + user.uid);
									TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
									//notify.sendToken(user.sign, user.uid);
                                                                        notify.sendToken("0|"+user.timestamp + "|" +user.sign, user.uid);
									
									gamePlatform.initSliderBar(appActivity);
									TypeSDKLogger.d("Login loginResult");
								}
							});
							
							gamePlatform.addPaymentListener(new PayListener() {
								
								@Override
								public void paymentResult(int result) {
									// TODO Auto-generated method stub
									
									TypeSDKLogger.d("ppsPayment paymentResult : " + result);
									PayResultData payResult = new PayResultData();
									TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
									if (result == GameSDKResultCode.SUCCESSPAYMENT) {
										// 支付成功
										TypeSDKLogger.d("pay_success");
										payResult.SetData(AttName.PAY_RESULT, "1");
										notify.Pay(payResult.DataToString());
									}else {
										// 支付失败
										TypeSDKLogger.e("return Error");
										payResult.SetData(AttName.PAY_RESULT, "0");
										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
										notify.Pay(payResult.DataToString());
									}
								}
								
								@Override
								public void leavePlatform() {
									// TODO Auto-generated method stub
									TypeSDKLogger.i("leave pay platform");
									PayResultData payResult = new PayResultData();
									TypeSDKNotify_iqiyi notify = new TypeSDKNotify_iqiyi();
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
									notify.Pay(payResult.DataToString());
								}
							});
						}
						
						@Override
						public void onFail(String arg0) {
							// TODO Auto-generated method stub
							
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}

	private void ppsLogin() {
		gamePlatform.iqiyiUserLogin(appActivity);
	}

	private void ppsExit() {
		gamePlatform.iqiyiUserLogout(appActivity);
	}
	
	private void ppsLogout() {
		gamePlatform.iqiyiChangeAccount(appActivity);
	}
	
	private void ppsShowUserCenter() {
		gamePlatform.iqiyiAccountCenter(appActivity);
	}

	private void ppsPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.d("pay_start");
					TypeSDKLogger.d("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.d("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.d("SDK_CP_ID:" + platform.GetData(AttName.SDK_CP_ID));
					TypeSDKLogger.d("SERVER_NAME:" + userInfo.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.d("ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
					TypeSDKLogger.d("ROLE_NAME:" + userInfo.GetData(AttName.ROLE_NAME));
					TypeSDKLogger.d("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
					
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)*0.01f);
					}
					
					String server_id = platform.GetData(AttName.SERVER_ID);
					if (server_id == null || server_id=="") {
						server_id = "1";
					}
					
					gamePlatform.iqiyiPayment(appActivity, 
							price,
							userInfo.GetData(AttName.ROLE_ID), 
							"ppsmobile_s" + server_id,
							_in_pay.GetData(AttName.BILL_NUMBER));
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}
	
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
