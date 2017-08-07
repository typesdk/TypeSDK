package com.type.sdk.android.youku;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.youku.gamesdk.act.YKCallBack;
import com.youku.gamesdk.act.YKCallBackWithContext;
import com.youku.gamesdk.act.YKInit;
import com.youku.gamesdk.act.YKPlatform;
import com.youku.gamesdk.data.Bean;
import com.youku.gamesdk.data.YKGameUser;
import com.youku.gamesdk.data.YKPayBean;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
			notify.Init();
			return;
		}
		this.youkuInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.youkuLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.youkuLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.youkuPay(_in_pay);
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
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			
			String extendInfo = new StringBuilder()
			.append("gameId=").append(platform.GetData(AttName.APP_ID))
			.append("&service=").append(userInfo.GetData(AttName.SERVER_NAME))
			.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
			.append("&grade=").append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
			@Override
			public void run() {
				YKPlatform.quit(appContext, new YKCallBack() {
					
					@Override
					public void onSuccess(Bean bean) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("quit_SUCCESS");
						YKPlatform.closeYKFloat(appContext);
						System.exit(0);
					}
					
					@Override
					public void onFailed(String failReason) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("LOGOUT_FAIL");
					}
				});
			}
		});			
		}
		
	}

	public void onResume() {
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

	private void youkuInit() {

		TypeSDKLogger.e("init begin");

		// final Activity runActivi;
		// if (UnityPlayer.currentActivity != null)
		// runActivi = UnityPlayer.currentActivity;
		// else
		// runActivi = appActivity;

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					new YKInit(appActivity).init(new YKCallBack() {
						
						@Override
						public void onSuccess(Bean arg0) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("initSDK_success");
							isInit = true;
							TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
							notify.Init();
						}
						
						@Override
						public void onFailed(String failReason) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("initSDK_Failed");
							TypeSDKLogger.e("failReason:" + failReason);
						}
					});
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void youkuLogin() {
		TypeSDKLogger.d("LOGIN_bgain");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				YKPlatform.autoLogin(new YKCallBack() {
					
					@Override
					public void onSuccess(Bean bean) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("LOGIN_SUCCESS");
						YKGameUser gameUser = (YKGameUser)bean;
		                TypeSDKLogger.d("getSession:" + gameUser.getSession());
		                TypeSDKLogger.d("getUserName:" + gameUser.getUserName());
		                // 发送登录SDK成功广播通知
		                TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
    					notify.sendToken(gameUser.getSession());
    					YKPlatform.startYKFloat(appContext, new YKCallBackWithContext() {
							
							@Override
							public void callback(Context arg0) {
								// TODO Auto-generated method stub
								YKPlatform.logout(appContext);
								TypeSDKLogger.d("LOGOUT_SUCCESS");
								TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
								notify.Logout();
							}
						});
					}
					
					@Override
					public void onFailed(String failReason) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("LOGIN_FAIL");
						TypeSDKLogger.d(failReason);
//						youkuLogin();
					}
				}, appContext);
			}
		});

	}

	private void youkuLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				YKPlatform.logout(appContext);
				TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
				notify.Logout();
			}
		});
		
	}
	
	private void youkuPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e("PRODUCT_ID:" + _in_pay.GetData(AttName.PRODUCT_ID));
					TypeSDKLogger.e("PAY_CALL_BACK_URL:" + platform.GetData(AttName.PAY_CALL_BACK_URL));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					
					String price;
					if(TypeSDKTool.isPayDebug){
						price = "1";
					}else{
						price = _in_pay.GetData(AttName.REAL_PRICE);
					}
					
					YKPayBean payBean = new YKPayBean();
					payBean.setAmount(price);
					payBean.setAppOrderId(_in_pay.GetData(AttName.BILL_NUMBER));
					payBean.setNotifyUri(platform.GetData(AttName.PAY_CALL_BACK_URL));
					payBean.setProductId("0");
					payBean.setProductName(_in_pay.GetData(AttName.ITEM_NAME));
//					payBean.setAppExt1("");
					YKPlatform.doPay(appActivity, payBean, new YKCallBack() {
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_youku notify = new TypeSDKNotify_youku();
						@Override
						public void onSuccess(Bean arg0) {
							// TODO Auto-generated method stub
							// 支付成功
							TypeSDKLogger.e("pay_success");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onFailed(String failReason) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("return Error");
							TypeSDKLogger.e("failReason:" + failReason);
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}
					});
					
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
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
