package com.type.sdk.android.lvan;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.lvanclub.opensdk.LvanGameSDK;
import com.lvanclub.opensdk.LvanSDKStatusCode;
import com.lvanclub.opensdk.iface.Listener.LvanCallbackListener;
import com.lvanclub.opensdk.iface.Listener.LvanCallbackListenerNullException;
import com.lvanclub.opensdk.pay.bean.PayArgs;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKBonjour extends TypeSDKBaseBonjour 
{

	public Context appContext;
	public Activity appActivity;
	private boolean isDebug;
	
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
		isDebug = TypeSDKTool.isPayDebug;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
			notify.Init();
			return;
		}
		this.lvancInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.lvancLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		LvanGameSDK.defaultSDK().logout();
		TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
		notify.Logout();
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
		this.lvancPay(_in_pay);
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
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			this.lvancExitGame();			
		}
		
//		System.exit(0);
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		LvanGameSDK.defaultSDK().onResume(appActivity);
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		LvanGameSDK.defaultSDK().onStop(appActivity);
		TypeSDKLogger.e("onPause is end");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		LvanGameSDK.defaultSDK().onStop(appActivity);
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}
	
	private void lvancInit() {

		TypeSDKLogger.e("init begin");
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!isDebug){
					LvanGameSDK.defaultSDK().setSandboxOn(false);
				}
				
				try {
					LvanGameSDK.defaultSDK().initSDK(appActivity, new LvanCallbackListener<String>() {
						
						@Override
						public void callback(int code, String data) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("init:code:" + code + "data:" + data);
							if(LvanSDKStatusCode.INIT_SUCCESS == code){
								TypeSDKLogger.e("init_SUCCESS");
								try {
									LvanGameSDK.defaultSDK().setSwitchUserListener(appActivity, new LvanCallbackListener<String>() {
										
										@Override
										public void callback(int code, String data) {
											// TODO Auto-generated method stub
											TypeSDKLogger.e("init:code:" + code + "data:" + data);
											if (code == LvanSDKStatusCode.LOGIN_SUCCESS) {
												TypeSDKLogger.e("setSwitchUserListener_LOGIN_SUCCESS");
												TypeSDKBonjour bonjour = new TypeSDKBonjour();
												bonjour.ShowLogout(appContext);
												String token = LvanGameSDK.defaultSDK().getToken();
												int userid = LvanGameSDK.defaultSDK().getUid();
												TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
												notify.sendToken(userid, token);
											} else {
												TypeSDKLogger.e("setSwitchUserListener_login_FAIL");
											}
										}
									});
								} catch (LvanCallbackListenerNullException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								isInit = true;
								TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
								notify.Init();
							} else {
								TypeSDKLogger.e("init_FAIL");
							}
						}
					});
				} catch (LvanCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		TypeSDKLogger.e("init done");

	}
	
	private void lvancLogin() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("login begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					LvanGameSDK.defaultSDK().login(appActivity, new LvanCallbackListener<String>() {
						
						@Override
						public void callback(int code, String data) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("init:code:" + code + "data:" + data);
							if(LvanSDKStatusCode.LOGIN_SUCCESS == code){
								TypeSDKLogger.e("login_SUCCESS");
								String token = LvanGameSDK.defaultSDK().getToken();
								int userid = LvanGameSDK.defaultSDK().getUid();
								TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
								notify.sendToken(userid, token);
							} else {
								TypeSDKLogger.e("login_FAIL");
							}
						}
					});
				} catch (LvanCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	private void lvancPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				float price;
				if(isDebug){
					price = 0.01f;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE)*0.01f;
				}
				
				PayArgs payArg = new PayArgs();
				// 设置金额
				payArg.amount = price; // 必传入
				payArg.customerOrderId = _in_pay.GetData(AttName.BILL_NUMBER); // 订单号,必传入
				payArg.productName = _in_pay.GetData(AttName.ITEM_NAME); // 必传入
				payArg.body = _in_pay.GetData(AttName.ITEM_DESC);// 必传入
				payArg.productId = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));// 必传入
				payArg.zoneName = _in_pay.GetData(AttName.SERVER_NAME);// 必传入
				payArg.extra = _in_pay.GetData(AttName.EXTRA);// 选填
				
				TypeSDKLogger.e("amount" + price);
				TypeSDKLogger.e("customerOrderId" + _in_pay.GetData(AttName.BILL_NUMBER));
				TypeSDKLogger.e("productName" + _in_pay.GetData(AttName.ITEM_NAME));
				TypeSDKLogger.e("body" + _in_pay.GetData(AttName.ITEM_DESC));
				TypeSDKLogger.e("productId" + itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)));
				TypeSDKLogger.e("zoneName" + _in_pay.GetData(AttName.SERVER_NAME));
				TypeSDKLogger.e("extra" + _in_pay.GetData(AttName.EXTRA));
				
				try {
					LvanGameSDK.defaultSDK().pay(appActivity, payArg, new LvanCallbackListener<PayArgs>() {
						
						@Override
						public void callback(int code, PayArgs data) {
							// TODO Auto-generated method stub
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_lvan notify = new TypeSDKNotify_lvan();
							if (code == LvanSDKStatusCode.PAY_SUCCESS) {
								TypeSDKLogger.e("支付成功，返回给主调用活动的payinfo=" + data);
								// 成功充值
								if (data != null) {
									String ordereId = data.lvanOrderId;// 获取lvan订单号
									float orderAmount = data.amount;// 获取订单金额
									int payWay = data.payway;// 获取支付方式编号
									String payWayName = data.getPaywayname(payWay);// 获取支付方式名
									
									payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
									payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
									notify.Pay(payResult.DataToString());
									TypeSDKLogger.e("支付信息：" + ordereId + "," + orderAmount + "," + payWay + "," + payWayName);
								}
							}
							if (code == LvanSDKStatusCode.PAY_FAIL) {
								// 支付失败
								TypeSDKLogger.e("支付失败" + data);
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "0");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "FAIL");
								notify.Pay(payResult.DataToString());
							}
							if (code == LvanSDKStatusCode.PAY_CANCLE) {
								// 用户退出充值界面。
								TypeSDKLogger.e("支付取消" + data);
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "2");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "CANCLE");
								notify.Pay(payResult.DataToString());
							}
							TypeSDKLogger.e("pay_end");
						}
					});
				} catch (LvanCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void lvancExitGame() {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					LvanGameSDK.defaultSDK().exit(appActivity, new LvanCallbackListener<String>() {
						
						@Override
						public void callback(int code, String data) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("init:code:" + code + "data:" + data);
							if(LvanSDKStatusCode.EXIT_SUCCESS == code){
								TypeSDKLogger.e("EXIT_SUCCESS");
								System.exit(0);
							} else {
								TypeSDKLogger.e("EXIT_FAIL");
							}
						}
					});
				} catch (LvanCallbackListenerNullException e) {
					// TODO Auto-generated catch block
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
