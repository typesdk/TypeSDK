package com.type.sdk.android.wdj;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.application.TypeApplication;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi.ExitCallback;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnCheckLoginCompletedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLoginFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.WandouAccountListener;
import com.wandoujia.mariosdk.plugin.api.model.model.LoginFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.LogoutFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;
import com.wandoujia.mariosdk.plugin.api.model.model.UnverifiedPlayer;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private WandouGamesApi wandouGamesApi;
	private boolean hasLogin = false;// 判断用户是否在游戏主界面
	private boolean bl = false;//判断是否有过悬浮登出回调

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
		hasLogin = false;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
			notify.Init();
			return;
		}

		this.wdjInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.wdjLogin();
		
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.wdjLogout();
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
		this.wdjPay(_in_pay);

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

	boolean wdjIsLogin = false;

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		/**
		 * 检查豌豆荚账户是否登录
		 * 
		 * @param listener
		 *            {@link com.wandoujia.mariosdk.plugin.api.model.callback.OnCheckLoginCompletedListener}
		 */
		wandouGamesApi.isLoginned(new OnCheckLoginCompletedListener() {
			@Override
			public void onCheckCompleted(boolean isLogin) {
				wdjIsLogin = isLogin;
				TypeSDKLogger.e("检查豌豆荚账户是否登录");
			}
		});
		// return 0;
		return wdjIsLogin == true ? 1 : 0;
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
		TypeSDKLogger.e("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			//hasLogin = true;
			wandouGamesApi.postGameInformation("createRole",
					userInfo.GetData(AttName.ROLE_ID),
					userInfo.GetData(AttName.ROLE_NAME),
					userInfo.GetInt(AttName.ROLE_LEVEL),
					userInfo.GetInt(AttName.SERVER_ID),
					userInfo.GetData(AttName.SERVER_NAME), 0, 1, "无帮派");
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.e("执行ExitGame方法");
			wandouGamesApi.exit(appActivity, new ExitCallback() {
				
				@Override
				public void onGameExit() {
					// TODO Auto-generated method stub
					// appActivity.finish();
					System.exit(0);
				}
				
				@Override
				public void onChannelExit() {
					// TODO Auto-generated method stub
					
				}
			}, true);
			
		}

	}

	public void onResume() {
		if (wandouGamesApi != null) {
			wandouGamesApi.onResume(appActivity);
		}
		TypeSDKLogger.e("onResume");
	}

	public void onPause() {
		if (wandouGamesApi != null) {
			wandouGamesApi.onPause(appActivity);
		}
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		if (wandouGamesApi != null) {
			wandouGamesApi.onStop(appActivity);
		}
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void wdjInit() {
		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					wandouGamesApi = TypeApplication.getWandouGamesApi();
					wandouGamesApi.init(appActivity);
					wandouGamesApi.onCreate(appActivity);
					wandouGamesApi.addWandouAccountListener(new WandouAccountListener() {
						
						@Override
						public void onLogoutSuccess() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("WandouAccountListener onLogoutSuccess");
							hasLogin = true;
							bl = true;
						}
						
						@Override
						public void onLoginSuccess() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("WandouAccountListener onLoginSuccess");
							
						}
						
						@Override
						public void onLoginFailed(int arg0, String arg1) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("WandouAccountListener onLoginFailed:"+arg0+"reason:"+arg1);
							
						}
					});
					//hasLogin = true;
					isInit = true;
					TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
					notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void wdjLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				wandouGamesApi.login(new OnLoginFinishedListener() {
					@Override
					public void onLoginFinished(LoginFinishType arg0,
							UnverifiedPlayer arg1) {
						if (arg0.equals(LoginFinishType.CANCEL)) {
							TypeSDKLogger.e("login cancel");
							if (hasLogin) {
								//wdjLogout();
								if(bl){
									TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
									notify.Logout();
									hasLogin = false;
									bl = false;
								}
							}
						} else if (hasLogin) {
							TypeSDKLogger.e("login success");
							String token = arg1.getToken();
							String uid = arg1.getId();
							TypeSDKLogger
									.e("token:" + token + "; uid:" + uid);
							TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
							notify.reLogin(token, uid);
							hasLogin = true;
							bl = false;
						} else {
							TypeSDKLogger.e("login success");
							String token = arg1.getToken();
							String uid = arg1.getId();
							TypeSDKLogger
									.e("token:" + token + "; uid:" + uid);
							TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
							notify.sendToken(token, uid);
							hasLogin = true;
							bl = false;
						} 
						/* else if (hasLogin) {
							TypeSDKLogger.e("login success");
							String token = arg1.getToken();
							String uid = arg1.getId();
							TypeSDKLogger
									.e("token:" + token + "; uid:" + uid);
							TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
							notify.reLogin(token, uid);
							hasLogin = false;
						} */
					}
				});

			}
		});

	}

	private void wdjLogout() {
		/**
		 * 登出豌豆荚。
		 * 
		 * @param listener
		 *            当登出操作结束后回调
		 *            {@link com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener}
		 */
		wandouGamesApi.logout(new OnLogoutFinishedListener() {
			@Override
			public void onLoginFinished(LogoutFinishType logoutFinishType) {
				if (logoutFinishType.equals(LogoutFinishType.LOGOUT_SUCCESS)) {
					TypeSDKLogger.e("logout_success");
					TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();
					notify.Logout();
					hasLogin = false;
				} else {
					TypeSDKLogger.e("logout_fail");
				}

			}
		});
	}

	private void wdjPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:"
							+ _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("EXTRA:" + _in_pay.GetData(AttName.EXTRA));
					TypeSDKLogger.e("BILL_NUMBER:"
							+ _in_pay.GetData(AttName.BILL_NUMBER));
					int price = 0;
					if (TypeSDKTool.isPayDebug) {
						price = 1;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}

					wandouGamesApi.pay(appActivity,
							_in_pay.GetData(AttName.ITEM_NAME), (long) price,
							(long) 1, _in_pay.GetData(AttName.BILL_NUMBER),
							new OnPayFinishedListener() {

								PayResultData payResult = new PayResultData();
								TypeSDKNotify_wdj notify = new TypeSDKNotify_wdj();

								@Override
								public void onPaySuccess(PayResult result) {
									TypeSDKLogger.e("pay_success");

									payResult.SetData(AttName.PAY_RESULT, "1");
									payResult.SetData(
											AttName.PAY_RESULT_REASON,
											"SUCCESS");
									notify.Pay(payResult.DataToString());
								}

								@Override
								public void onPayFail(PayResult result) {
									TypeSDKLogger.e("pay_fail");

									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(
											AttName.PAY_RESULT_REASON,
											"PAY_FAIL");
									notify.Pay(payResult.DataToString());
								}

							});

				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: "
							+ exception.toString());
				}

			}
		});

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	public void onActivityResult() {
		// TODO Auto-generated method stub
		if (wandouGamesApi != null) {
			wandouGamesApi.onActivityResult(appActivity, 0, 0, null);
		}
		TypeSDKLogger.e("onStop");

	}

	public void onNewIntent() {
		// TODO Auto-generated method stub
		if (wandouGamesApi != null) {
			wandouGamesApi.onNewIntent(appActivity);
		}
		TypeSDKLogger.e("onStop");
	}

	public void onRestart() {
		// TODO Auto-generated method stub
		if (wandouGamesApi != null) {
			wandouGamesApi.onRestart(appActivity);
		}
		TypeSDKLogger.e("onStop");
	}

}
