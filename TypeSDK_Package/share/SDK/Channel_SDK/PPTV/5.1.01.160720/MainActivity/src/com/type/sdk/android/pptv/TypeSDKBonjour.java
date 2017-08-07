package com.type.sdk.android.pptv;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.pptv.vassdk.aservice.login.ALoginResult;
import com.pptv.vassdk.aservice.pay.APayResult;
import com.pptv.vassdk.plug.agent.PptvVasPlugAgent;
import com.pptv.vassdk.plug.agent.Listener.ExitDialogListener;
import com.pptv.vassdk.plug.agent.Listener.LoginListener;
import com.pptv.vassdk.plug.agent.Listener.PayListener;
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
			TypeSDKNotify_pptv notify = new TypeSDKNotify_pptv();
			notify.Init();
			return;
		}
		isInit = true;
		this.pptvInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.pptvLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.pptvLogout();
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
		this.pptvPay(_in_pay);

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
			// PptvVasPlugAgent.getInstance().statisticCreateRole(appActivity);
			// PptvVasPlugAgent.getInstance().statisticEnterGame(appActivity);

		} catch (Exception e) {
			TypeSDKLogger.d("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
				@Override
				public void run() {

					PptvVasPlugAgent.getInstance().onExit(appActivity,
							new ExitDialogListener() {

								@Override
								public void onExit() {
									// TODO Auto-generated method stub
									appActivity.finish();
								}

								@Override
								public void onContinue() {
									// TODO Auto-generated method stub

								}
							});

				}
			});
		}
	}

	public void onResume() {
		
		if (isInit) {
			TypeSDKLogger.d("startFwindow");
			PptvVasPlugAgent.getInstance().startFwindow(appActivity);
		}
	}

	public void onPause() {
		if (isInit) {
			TypeSDKLogger.d("stopFwindow");
			PptvVasPlugAgent.getInstance().stopFwindow(appActivity);
		}
	}

	public void onStop() {

	}

	public void onDestroy() {
		if (isInit) {
			TypeSDKLogger.d("stopFwindow");
			PptvVasPlugAgent.getInstance().stopFwindow(appActivity);
		}
	}

	private void pptvInit() {

		TypeSDKLogger.d("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.d("initSDK_start");
					// SDK初始化
					PptvVasPlugAgent.getInstance().init(appContext,
							platform.GetData(AttName.APP_ID), "", "", -1);

					TypeSDKLogger.d("init success");
					TypeSDKNotify_pptv notify = new TypeSDKNotify_pptv();
					notify.Init();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.d("init done");

	}

	private void pptvLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PptvVasPlugAgent.getInstance().startLogin(appActivity,
						new LoginListener() {

							@Override
							public void onLoginSuccess(ALoginResult loginResult) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login success");
								TypeSDKLogger.e("getSessionId:"
										+ loginResult.getSessionId());
								TypeSDKLogger.e("getUserId:"
										+ loginResult.getUserId());
								TypeSDKLogger.e("getBindUsrName:"
										+ loginResult.getBindUsrName());
								TypeSDKNotify_pptv notify = new TypeSDKNotify_pptv();
								notify.sendToken(loginResult.getSessionId()
										+ "|" + loginResult.getBindUsrName(),
										"" + loginResult.getUserId());
								PptvVasPlugAgent.getInstance().startFwindow(
										appActivity);
							}

							@Override
							public void onLoginCancel() {
								// TODO Auto-generated method stub
								pptvLogin();
							}
						});
			}
		});
	}

	private void pptvLogout() {
		// PptvVasAgent.onExit(appActivity, new ExitDialogListener() {
		//
		// @Override
		// public void onExit() {
		// // TODO Auto-generated method stub
		// TypeSDKLogger.e("logout_success");
		TypeSDKNotify_pptv notify = new TypeSDKNotify_pptv();
		notify.Logout();
		// }
		//
		// @Override
		// public void onContinue() {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	private void pptvPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.d("pay_start");
					TypeSDKLogger.d("ITEM_NAME:"
							+ _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.d("BILL_NUMBER:"
							+ _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.d("SERVER_ID:"
							+ userInfo.GetData(AttName.SERVER_ID));
					TypeSDKLogger.d("USER_ID:"
							+ userInfo.GetData(AttName.USER_ID));
					TypeSDKLogger.d("REAL_PRICE:"
							+ _in_pay.GetData(AttName.REAL_PRICE));

					String price;
					if (TypeSDKTool.isPayDebug) {
						price = "1";
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE) * 0.01f + "";
					}

					PptvVasPlugAgent.getInstance().startPay(appActivity,
							userInfo.GetData(AttName.SERVER_ID),
							userInfo.GetData(AttName.ROLE_ID),
							_in_pay.GetData(AttName.BILL_NUMBER), 1, price,
							_in_pay.GetData(AttName.ITEM_NAME),
							new PayListener() {
								PayResultData payResult = new PayResultData();
								TypeSDKNotify_pptv notify = new TypeSDKNotify_pptv();

								@Override
								public void onPayWait(APayResult arg0) {
									// TODO Auto-generated method stub
									TypeSDKLogger.d("pay_wait");
								}

								@Override
								public void onPaySuccess(APayResult arg0) {
									// TODO Auto-generated method stub
									// 支付成功
									TypeSDKLogger.d("pay_success");

									payResult.SetData(AttName.PAY_RESULT, "1");
									payResult.SetData(
											AttName.PAY_RESULT_REASON,
											"SUCCESS");
									notify.Pay(payResult.DataToString());
								}

								@Override
								public void onPayFinish() {
									// TODO Auto-generated method stub
									TypeSDKLogger.d("pay_finish");
								}

								@Override
								public void onPayFail(APayResult arg0) {
									// TODO Auto-generated method stub
									TypeSDKLogger.d("return fail");

									payResult.SetData(AttName.PAY_RESULT, "2");
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

}
