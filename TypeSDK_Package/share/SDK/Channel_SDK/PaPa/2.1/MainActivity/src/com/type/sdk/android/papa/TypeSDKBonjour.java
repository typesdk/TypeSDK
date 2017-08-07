/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.papa;

import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.papa91.pay.callback.PPLoginCallBack;
import com.papa91.pay.callback.PPayCallback;
import com.papa91.pay.callback.PpaLogoutCallback;
import com.papa91.pay.core.StringUtils;
import com.papa91.pay.pa.activity.PaayActivity;
import com.papa91.pay.pa.business.LoginResult;
import com.papa91.pay.pa.business.PPayCenter;
import com.papa91.pay.pa.business.PaayArg;
import com.papa91.pay.pa.business.PayArgsCheckResult;
import com.papa91.pay.pa.dto.LogoutResult;
import com.unity3d.player.UnityPlayer;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean exit = false;
	private int openUid;
	private String token;
	public boolean isNoInit = false;

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
			TypeSDKLogger.e("error init do again");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}
		paPaSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.paPaSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.paPaSdkLogout();
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
		this.paPaSdkPay(_in_pay);
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

		TypeSDKLogger.e("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			PPayCenter.enterGame(userInfo.GetData(AttName.ROLE_NAME),
					Integer.parseInt(userInfo.GetData(AttName.ZONE_ID)),
					Integer.parseInt(userInfo.GetData(AttName.SERVER_ID)));

			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
				PPayCenter.enterGame(userInfo.GetData(AttName.ROLE_NAME),
						Integer.parseInt(userInfo.GetData(AttName.ZONE_ID)),
						Integer.parseInt(userInfo.GetData(AttName.SERVER_ID)));

			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
				PPayCenter.createRole(userInfo.GetData(AttName.ROLE_NAME),
						Integer.parseInt(userInfo.GetData(AttName.ZONE_ID)),
						Integer.parseInt(userInfo.GetData(AttName.SERVER_ID)));

			} else if (userInfo.GetData("role_type").equals("levelUp")) {
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");
			} else {
				TypeSDKLogger.e("datatype error:" + "提交的数据不合法");

			}

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		paPaSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		PPayCenter.destroy();
	}

	private void paPaSdkInit() {
		TypeSDKLogger.i("paPaSdkInit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PPayCenter.init(appActivity);
				TypeSDKNotify_PaPa notify = new TypeSDKNotify_PaPa();
				notify.Init();
				isInit = true;
				TypeSDKLogger.e("paPaSdkInit success");

			}
		});

	}

	private void paPaSdkLogin() {
		TypeSDKLogger.i("paPaSdkLogin start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PPayCenter.login(appActivity, new PPLoginCallBack() {

					@Override
					public void onLoginFinish(LoginResult result) {
						// TODO Auto-generated method stub
						switch (result.getCode()) {
						case LoginResult.LOGIN_CODE_APPID_NOT_FOUND:
							// 没找到appid
							TypeSDKLogger.e("没找到appid");
							break;
						case LoginResult.LOGIN_CODE_SUCCESS:// 登录成功
							openUid = result.getOpenUid();// 返回openUid
							token = result.getToken();
							TypeSDKLogger.i("222" + openUid);
							TypeSDKLogger.i(String.valueOf(openUid));
							TypeSDKNotify_PaPa notify = new TypeSDKNotify_PaPa();
							notify.sendToken(token, "" + openUid);
							break;
						case LoginResult.LOGIN_CODE_FAILED:// 登录失败
							String message = result.getMessage();// 失败详情
							TypeSDKLogger.e("失败详情：" + message);
							break;
						case LoginResult.LOGIN_CODE_CANCEL:// 登录取消
							TypeSDKLogger.e("LOGIN_CODE_CANCEL");
							break;
						case LoginResult.NOT_INIT:// 没有调用
													// PPayCenter.init(activity);
							TypeSDKLogger.e("NOT_INIT");
							isNoInit = true;
							paPaSdkInit();
							break;

						}
					}
				});

			}
		});
	}

	private void paPaSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKNotify_PaPa notify = new TypeSDKNotify_PaPa();
				notify.Logout();
				TypeSDKLogger.e("LOGOUT SUCCESS");
			}
		});
	}

	private void paPaSdkExit() {
		TypeSDKLogger.i("paPaSdkExit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PPayCenter.loginOut(appActivity, openUid,
						new PpaLogoutCallback() {

							@Override
							public void onLoginOut(LogoutResult logoutResult) {
								// TODO Auto-generated method stub
								switch (logoutResult.getCode()) {
								case LogoutResult.LOGOUT_CODE_OUT:
									TypeSDKLogger.i("退出app");
									// finish();
									System.exit(0);
									break;
								case LogoutResult.LOGOUT_CODE_BBS:
									TypeSDKLogger.i("进入论坛");
									break;
								}
							}
						});
			}
		});
	}

	private void paPaSdkPay(TypeSDKData.PayInfoData _in_pay) {

		// String money=pamoney.getText().toString().trim();
		TypeSDKLogger.i("payinfo:" + _in_pay.DataToString());
		final PaayArg paayArg = new PaayArg();
		PackageManager pm = appContext.getPackageManager();
		String appName = appContext.getApplicationInfo().loadLabel(pm)
				.toString();
		TypeSDKLogger.i("appName:" + appName);
		paayArg.APP_NAME = appName;
		paayArg.APP_ORDER_ID = _in_pay.GetData(AttName.BILL_NUMBER);
		paayArg.APP_DISTRICT = Integer.parseInt(_in_pay
				.GetData(AttName.SERVER_ID));
		paayArg.APP_SERVER = Integer.parseInt(_in_pay
				.GetData(AttName.SERVER_ID));
		paayArg.APP_USER_ID = _in_pay.GetData(AttName.ROLE_ID).replace(":", "_");
		paayArg.APP_USER_NAME = _in_pay.GetData(AttName.ROLE_NAME);
		int price = 0;
		if (TypeSDKTool.isPayDebug) {
			price = 1;
		} else {
			price = _in_pay.GetInt(AttName.REAL_PRICE);
		}
		paayArg.MONEY_AMOUNT = String.valueOf(price * 0.01f);
		//paayArg.MONEY_AMOUNT = String.valueOf(1.0);

		// paayArg.NOTIFY_URI =
		// "http://sdkapi.papa91.com/index.php/pay_center/test";
		paayArg.NOTIFY_URI = platform.GetData(AttName.PAY_CALL_BACK_URL);
		paayArg.PRODUCT_ID = _in_pay.GetData(AttName.ITEM_SERVER_ID);
		paayArg.PRODUCT_NAME = _in_pay.GetData(AttName.ITEM_NAME);
		paayArg.PA_OPEN_UID = openUid;// 调用登录方法，得到该值
		paayArg.APP_EXT1 = "0";
		paayArg.APP_EXT2 = "0";

		TypeSDKLogger.i("paayArg:--" + "paayArg.APP_NAME:" + paayArg.APP_NAME
				+ ";paayArg.APP_ORDER_ID:" + paayArg.APP_ORDER_ID
				+ ";paayArg.APP_DISTRICT:" + paayArg.APP_DISTRICT
				+ ";paayArg.APP_SERVER:" + paayArg.APP_SERVER
				+ ";paayArg.APP_USER_ID:" + paayArg.APP_USER_ID
				+ ";paayArg.APP_USER_NAME:" + paayArg.APP_USER_NAME
				+ ";paayArg.MONEY_AMOUNT:" + paayArg.MONEY_AMOUNT
				+ ";paayArg.NOTIFY_URI:" + paayArg.NOTIFY_URI
				+ ";paayArg.PRODUCT_ID:" + paayArg.PRODUCT_ID
				+ ";paayArg.PRODUCT_NAME:" + paayArg.PRODUCT_NAME
				+ ";paayArg.PA_OPEN_UID:" + paayArg.PA_OPEN_UID);

		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				PPayCenter.pay(paayArg, new PPayCallback() {
					@Override
					public void onPayFinished(int status) {
						TypeSDKNotify_PaPa notify_PaPa = new TypeSDKNotify_PaPa();
						TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
						TypeSDKLogger.i("支付结果:" + status + "");
						switch (status) {

						case PayArgsCheckResult.CHECK_RESULT_PAY_CALLBACK_NULL:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:回调函数未配置");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:回调函数未配置:");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_AMOUNT:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:金额无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:金额无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_APP_NAME:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:游戏名称无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:游戏名称无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_ORDER_ID:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:APP_APP_ORDER_ID无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:APP_APP_ORDER_ID无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_APP_USER_ID:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:APP_USER_ID无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:APP_USER_ID无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_APP_USER_NAME:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:APP_USER_NAME无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:APP_USER_NAME无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_NOTIFY_URI:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:NOTIFY_URI无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:NOTIFY_URI无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_OPEN_UID:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:OPEN_UID无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:OPEN_UID无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_PRODUCT_ID:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:PRODUCT_ID无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:PRODUCT_ID无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_PRODUCT_NAME:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:PRODUCT_NAME无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:PRODUCT_NAME无效");
							break;
						case PayArgsCheckResult.CHECK_RESULT_PAY_INVALID_APP_KEY:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"参数错误:APP_KEY无效");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("参数错误:APP_KEY无效");
							break;
						case PaayActivity.PAPAPay_RESULT_CODE_SUCCESS:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "1");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"支付成功");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("支付成功");
							break;
						case PaayActivity.PAPAPay_RESULT_CODE_FAILURE:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"支付失败");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("支付失败");
							break;
						case PaayActivity.PAPAPay_RESULT_CODE_CANCEL:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "2");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"支付取消");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("支付取消");
							break;
						case PaayActivity.PAPAPay_RESULT_CODE_WAIT:
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT, "0");
							payResult.SetData(
									TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"支付等待");
							notify_PaPa.Pay(payResult.DataToString());
							TypeSDKLogger.i("支付等待");
							break;
						}
						// msg.setText(mmm + "");
					}
				});
			}
		});

	}

	// private void paPaSdkSendInfo(final JSONObject _jsonExData) {}

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
