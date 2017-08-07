/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.uc;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import cn.uc.gamesdk.UCGameSdk;
import cn.uc.gamesdk.even.SDKEventKey;
import cn.uc.gamesdk.even.SDKEventReceiver;
import cn.uc.gamesdk.even.Subscribe;
import cn.uc.gamesdk.exception.AliLackActivityException;
import cn.uc.gamesdk.exception.AliNotInitException;
import cn.uc.gamesdk.open.GameParamInfo;
import cn.uc.gamesdk.open.OrderInfo;
import cn.uc.gamesdk.open.UCLogLevel;
import cn.uc.gamesdk.open.UCOrientation;
import cn.uc.gamesdk.param.SDKParamKey;
import cn.uc.gamesdk.param.SDKParams;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.BaseData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.HttpUtil;
import com.type.utils.MD5Util;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public boolean isNoInit = false;
	boolean mCurrentInDebugMode = false;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.d("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.Init();
			return;
		}

		UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
		SdkInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.d("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		appContext = _in_context;
		appActivity = (Activity) appContext;
		SdkLogin(_in_context);
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.SdkLogout();
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
		// TODO Auto-generated method stub

	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub

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

		TypeSDKLogger.d("SetPlayerInfo, _in_data=" + _in_data);
		appContext = _in_context;
		appActivity = (Activity) appContext;
		userInfo.StringToData(_in_data);
		SDKParams params = new SDKParams();
		String role_id = userInfo.GetData(AttName.ROLE_ID);
		String role_name = userInfo.GetData(AttName.ROLE_NAME);
		long role_level = userInfo.GetInt(AttName.ROLE_LEVEL);
		long role_create = userInfo.GetInt(AttName.ROLE_CREATE_TIME);
		String zone_id = userInfo.GetData(AttName.SERVER_ID);
		String zone_name = userInfo.GetData(AttName.SERVER_NAME);

		params.put(SDKParamKey.STRING_ROLE_ID, role_id);
		params.put(SDKParamKey.STRING_ROLE_NAME, role_name);
		params.put(SDKParamKey.LONG_ROLE_LEVEL, role_level);
		params.put(SDKParamKey.LONG_ROLE_CTIME, role_create);
		params.put(SDKParamKey.STRING_ZONE_ID, zone_id);
		params.put(SDKParamKey.STRING_ZONE_NAME, zone_name);
		try {
			UCGameSdk.defaultSdk().submitRoleData(appActivity, params);
		} catch (IllegalArgumentException e) {
			TypeSDKLogger.e("SetPlayerInfo error, msg=" + e.getMessage());
			e.printStackTrace();
		} catch (AliNotInitException e) {
			TypeSDKLogger.e("SetPlayerInfo error, msg=" + e.getMessage());
			e.printStackTrace();
		} catch (AliLackActivityException e) {
			TypeSDKLogger.e("SetPlayerInfo error, msg=" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			TypeSDKLogger.e("SetPlayerInfo error, msg=" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.d("执行ExitGame方法");
			this.SdkExit();
		}

	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.d("onResume");
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.d("onPause");
	}

	public void onStop() {
		TypeSDKLogger.d("onStop");
	}

	public void onDestroy() {
		UCGameSdk.defaultSdk().unregisterSDKEventReceiver(receiver);
		TypeSDKLogger.d("onDestroy");
	}

	SDKEventReceiver receiver = new SDKEventReceiver() {
		@Subscribe(event = SDKEventKey.ON_INIT_SUCC)
		private void onInitSucc() {
			isInit = true;
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.Init();

			TypeSDKLogger.d("UCSDK init success");
		}

		@Subscribe(event = SDKEventKey.ON_INIT_FAILED)
		private void onInitFailed() {
			TypeSDKLogger.e("UCSDK init error");
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
		private void onLoginSucc(String sid) {
			TypeSDKNotify notify = new TypeSDKNotify();
			notify.sendToken(sid);
			TypeSDKLogger.d("UCSDK login success");
		}

		@Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
		private void onLoginFailed(String desc) {
			TypeSDKLogger.e("UCSDK login error, message" + desc);
			SdkLogin(appContext);
		}

		@Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
		private void onCreateOrderSucc(OrderInfo orderInfo) {
			if (orderInfo != null) {
				TypeSDKLogger.i("Pay Success");
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.Pay(payResult.DataToString());
			} else {

			}
		}

		@Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
		private void onPayUserExit(OrderInfo orderInfo) {
			if (orderInfo != null) {
				TypeSDKLogger.i("Pay Exit");
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify_UC = new TypeSDKNotify();
				notify_UC.Pay(payResult.DataToString());
			} else {

			}
		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_SUCC)
		private void onLogoutSucc() {
			TypeSDKNotify notify_UC = new TypeSDKNotify();
			notify_UC.Logout();
		}

		@Subscribe(event = SDKEventKey.ON_LOGOUT_FAILED)
		private void onLogoutFailed() {
			TypeSDKLogger.e("UC Logout error.");
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
		private void onExitSucc() {
			TypeSDKLogger.d("UC Exit success");
			System.exit(0);
		}

		@Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
		private void onExitCanceled() {
			TypeSDKLogger.d("UC Exit canceled");
		}
	};

	private void SdkInit() {

		TypeSDKLogger.i("init  begain");
		TypeSDKLogger.d("SDK_CP_ID:" + platform.GetInt(AttName.SDK_CP_ID) + "");
		TypeSDKLogger.d("APP_ID:" + platform.GetInt(AttName.APP_ID) + "");
		final GameParamInfo gpi = new GameParamInfo();
		gpi.setGameId(platform.GetInt(AttName.APP_ID));
		gpi.setEnablePayHistory(true);
		gpi.setEnableUserChange(false);
		if (TypeSDKTool.isScreenOriatationPortrait(appContext)) {
			gpi.setOrientation(UCOrientation.PORTRAIT);
		} else {
			gpi.setOrientation(UCOrientation.LANDSCAPE);
		}
		final SDKParams sdkParams = new SDKParams();
		sdkParams.put(SDKParamKey.LOG_LEVEL, UCLogLevel.WARN);
		sdkParams.put(SDKParamKey.DEBUG_MODE, mCurrentInDebugMode);
		sdkParams.put(SDKParamKey.GAME_PARAMS, gpi);

		TypeSDKLogger.e("initSDK_begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					UCGameSdk.defaultSdk().initSdk(appActivity, sdkParams);
				} catch (Exception e) {
					TypeSDKLogger.e("UCSDK init error. message="
							+ e.getMessage());
				}
			}
		});
		TypeSDKLogger.d("initSDK_end");
	}

	@Override
	public void SdkLogin(Context _context) {
		TypeSDKLogger.i("login start");
		try {
			UCGameSdk.defaultSdk().login(appActivity, null);
		} catch (AliNotInitException e) {
			TypeSDKLogger.e("UC login error, message=" + e.getMessage());
			e.printStackTrace();
		} catch (AliLackActivityException e) {
			TypeSDKLogger.e("UC login error, message=" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			TypeSDKLogger.e("UC login error, message=" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void SdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.d("ucSdkLogout");
				try {
					UCGameSdk.defaultSdk().logout(appActivity, null);
				} catch (AliLackActivityException e) {
					TypeSDKLogger.e("UC logout activity error, message="
							+ e.getMessage());
					e.printStackTrace();
				} catch (AliNotInitException e) {
					TypeSDKLogger.e("UC logout init error, message="
							+ e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					TypeSDKLogger.e("UC logout error, message="
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	public void SdkExit() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.d("SdkExit start");
					UCGameSdk.defaultSdk().exit(appActivity, null);
				} catch (AliLackActivityException e) {
					TypeSDKLogger.e("UC Exit error, msg=" + e.getMessage());
					e.printStackTrace();
				} catch (AliNotInitException e) {
					TypeSDKLogger.e("UC Exit error, msg=" + e.getMessage());
					e.printStackTrace();
				} catch (Exception e) {
					TypeSDKLogger.e("UC Exit error, msg=" + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay) {

		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		final SDKParams params = new SDKParams();
		String sign = "";
		int price = _in_pay.GetInt(AttName.REAL_PRICE);
		java.text.DecimalFormat df = new java.text.DecimalFormat("######0.00");
		double realprice = price * 0.01f;
		String priceString = df.format(realprice);
		String user_id = userInfo.GetData(AttName.USER_ID);
		String notifyUrl = platform.GetData(AttName.PAY_CALL_BACK_URL);
		String cpOrderId = _in_pay.GetData(AttName.BILL_NUMBER);
		String url = platform.GetData("url");
		BaseData orderSign = new BaseData();
		try {
			String signString = "accountId=" + user_id + "amount="
					+ priceString + "cpOrderId=" + cpOrderId + "notifyUrl="
					+ notifyUrl;
			TypeSDKLogger.d(signString);
			orderSign.SetData("playerid", "253");
			orderSign.SetData("cporder", "1");
			orderSign.SetData("price", "1");
			orderSign
					.SetData("subject", URLEncoder.encode(signString, "UTF-8"));

			String clientSign = "253|1|1|"
					+ URLEncoder.encode(signString, "UTF-8") + "|clientKey";
			orderSign.SetData("sign", MD5Util.md5(clientSign));
			TypeSDKLogger.d(orderSign.DataToString());
		} catch (Exception e) {
			TypeSDKLogger.e(e.getMessage());
		}
		TypeSDKLogger.d("Create Order url=" + url);

		String res = HttpUtil.jsonHttpPost(url, orderSign.DataToString());
		TypeSDKLogger.d(res);
		BaseData signData = new BaseData();
		signData.StringToData(res);
		if (signData.GetData("code").equals("0")) {

			sign = signData.GetData("data");

			params.put(SDKParamKey.ACCOUNT_ID, user_id);
			params.put(SDKParamKey.AMOUNT, priceString);
			params.put(SDKParamKey.CP_ORDER_ID, cpOrderId);
			params.put(SDKParamKey.NOTIFY_URL, notifyUrl);
			params.put(SDKParamKey.SIGN_TYPE, "MD5");
			params.put(SDKParamKey.SIGN, sign);

			TypeSDKLogger.d(params.toString());
			try {
				UCGameSdk.defaultSdk().pay(appActivity, params);
			} catch (IllegalArgumentException e) {
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify_UC = new TypeSDKNotify();
				notify_UC.Pay(payResult.DataToString());
			} catch (AliLackActivityException e) {
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify_UC = new TypeSDKNotify();
				notify_UC.Pay(payResult.DataToString());
			} catch (AliNotInitException e) {
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify_UC = new TypeSDKNotify();
				notify_UC.Pay(payResult.DataToString());
			} catch (Exception e) {
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify_UC = new TypeSDKNotify();
				notify_UC.Pay(payResult.DataToString());
			}
		} else {
			TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
			payResult.SetData(AttName.PAY_RESULT, "0");
			payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
			TypeSDKNotify notify_UC = new TypeSDKNotify();
			notify_UC.Pay(payResult.DataToString());
		}
	}

	@Override
	protected void SdkPayCancel() {
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
		payResult.SetData(AttName.PAY_RESULT, "0");
		payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		TypeSDKNotify notify_UC = new TypeSDKNotify();
		notify_UC.Pay(payResult.DataToString());
	};

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
