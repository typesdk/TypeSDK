package com.type.sdk.android.haomeng;

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
import com.weedong.gamesdk.base.WdGameSDK;
import com.weedong.gamesdk.base.WdResultCode;
import com.weedong.gamesdk.bean.OrderInfo;
import com.weedong.gamesdk.bean.PayInfo;
import com.weedong.gamesdk.bean.UserInfo;
import com.weedong.gamesdk.listener.WdExitListener;
import com.weedong.gamesdk.listener.WdLoginListener;
import com.weedong.gamesdk.listener.WdLogoutListener;
import com.weedong.gamesdk.listener.WdPayListener;
import com.weedong.gamesdk.utils.L;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public static int screen = 0;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
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
			TypeSDKNotify_haomengsdk notify = new TypeSDKNotify_haomengsdk();
			notify.Init();
			return;
		}
		this.haomengSDKInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.haomengSDKLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.haomengSDKLogout();
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
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.haomengSDKPay(_in_pay);

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

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				TypeSDKLogger.e("执行ExitGame方法");
				if (exitGameListenser()) {
					if (isInit) {
						WdGameSDK.getInstance().exit(appActivity, new WdExitListener() {
							@Override
							public void onCallBack(int paramInt, String message) {
								if (paramInt == WdResultCode.EXIT_SUCCESS) {
									isInit = false;
									appActivity.finish();
									System.exit(0);

								}else if(paramInt == WdResultCode.EXIT_CANCEL){
							
								}
							}
						});
					}
				}
			}
		});
		

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			WdGameSDK.getInstance().showGameBar();
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			WdGameSDK.getInstance().hideGameBar();
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		WdGameSDK.getInstance().destroyGameBar();
	}

	private void haomengSDKInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// 打印日志正式环境建议关闭日志
					L.allowD = false;
					// SDK初始
					if (TypeSDKTool.isScreenOriatationPortrait(appContext)) {
						screen = 1;
					}
					WdGameSDK.getInstance().initialize(appActivity,
							platform.GetData(AttName.APP_ID),
							platform.GetData(AttName.APP_KEY), screen);

					// 创建游戏浮标
					WdGameSDK.getInstance().createGameBar(appActivity);
					isInit = true;
					TypeSDKNotify_haomengsdk notify = new TypeSDKNotify_haomengsdk();
					notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void haomengSDKLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				WdGameSDK.getInstance().login(appActivity,
						new WdLoginListener() {
							@Override
							public void onCallBack(int paramInt,
									UserInfo paramResponse) {
								if (paramInt == WdResultCode.LOGIN_SUCCESS) {
									TypeSDKLogger.d("login_success");
									TypeSDKNotify_haomengsdk notify = new TypeSDKNotify_haomengsdk();
									notify.sendToken(
											paramResponse.getSessionid(),
											paramResponse.getUid());
								} else if (paramInt == WdResultCode.LOGIN_CANCEL) {
									TypeSDKLogger.d("login_success_CANCEL");
								} else {
									TypeSDKLogger.d("login_fail");
								}
							}
						});
			}
		});

	}

	private void haomengSDKLogout() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				WdGameSDK.getInstance().logout(appActivity, new WdLogoutListener() {
					@Override
					public void onCallBack(int paramInt, String paramResponse) {
						if (paramInt == WdResultCode.LOGOUT_SUCCESS) {
							TypeSDKNotify_haomengsdk notify = new TypeSDKNotify_haomengsdk();
							notify.Logout();
						} else if (paramInt == WdResultCode.LOGOUT_FAIL) {
							TypeSDKLogger.e("LOGOUT_FAIL");
						} else if (paramInt == WdResultCode.LOGOUT_NOT_LOGIN) {
							TypeSDKLogger.e("LOGOUT_NOT_LOGIN");
						}
					}
				});
				
			}
		});

	}

	private void haomengSDKPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					int price = 0;
					if (TypeSDKTool.isPayDebug) {
						price = 1;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 10;
					}
					if (WdGameSDK.getInstance().isLogined()) {
						PayInfo orderInfo = new PayInfo();
						orderInfo.setCpOrderId(_in_pay
								.GetData(AttName.BILL_NUMBER)); // cp_order_id(不能为空)
						orderInfo.setCpServerId("1"); // 默认
						orderInfo.setAmount(price); // amount参数的单位是游戏货币单位，我方服务器配置的默认比例为10:1
						WdGameSDK.getInstance().payment(appActivity, orderInfo,
								new WdPayListener<OrderInfo>() {

									@Override
									public void onCallBack(int paramInt,
											OrderInfo paramResponse) {
										// TODO Auto-generated method stub
										PayResultData payResult = new PayResultData();
										if (paramInt == WdResultCode.PAY_SUCCESS) {
											TypeSDKLogger.d("PAY_SUCCESS");
											payResult.SetData(
													AttName.PAY_RESULT, "1");
											payResult
													.SetData(
															TypeSDKDefine.AttName.PAY_RESULT_REASON,
															"SUCCESS");
										} else if (paramInt == WdResultCode.PAY_CANCEL) {
											TypeSDKLogger.d("PAY_CANCEL");
											payResult.SetData(
													AttName.PAY_RESULT, "2");
											payResult.SetData(
													AttName.PAY_RESULT_REASON,
													"PAY_CANCEL");
										} else if (paramInt == WdResultCode.PAY_DEBUG) {
											TypeSDKLogger
													.d("PAY_DEBUG_SUCCESS");
											payResult.SetData(
													AttName.PAY_RESULT, "1");
											payResult
													.SetData(
															TypeSDKDefine.AttName.PAY_RESULT_REASON,
															"SUCCESS");
										} else {
											TypeSDKLogger.d("PAY_fail");
											payResult.SetData(
													AttName.PAY_RESULT, "0");
											payResult.SetData(
													AttName.PAY_RESULT_REASON,
													"PAY_FAIL");
										}
										TypeSDKNotify_haomengsdk notify = new TypeSDKNotify_haomengsdk();
										notify.Pay(payResult.DataToString());
									}
								});
					}
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: "
							+ exception.toString());
				}

			}
		});

	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return null;
	}

}
