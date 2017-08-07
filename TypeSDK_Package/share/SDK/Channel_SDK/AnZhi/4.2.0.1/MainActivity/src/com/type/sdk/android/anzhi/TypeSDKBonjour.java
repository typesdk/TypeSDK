package com.type.sdk.android.anzhi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.anzhi.sdk.middle.manage.AnzhiSDK;
import com.anzhi.sdk.middle.manage.GameCallBack;
import com.anzhi.sdk.middle.util.MD5;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.anzhi.util.Des3Util;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private String uid;
	private String Nick;
	private boolean isDebug = false;
	private AnzhiSDK midManage;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	GameCallBack mGameCallBack = new GameCallBack() {

		@Override
		public void callBack(int type, String result) {
			// TODO Auto-generated method stub
			try {
				JSONObject json = null;
				int code = 0;
				TypeSDKNotify_anzhi notify;
				PayResultData payResult;
				if (result != null) {
					TypeSDKLogger.i("result " + result);
					json = new JSONObject(result);
					code = json.optInt("code");
				}
				switch (type) {
				case GameCallBack.SDK_TYPE_INIT:
					if (result == null || result.isEmpty()) {
						TypeSDKLogger.i("init_success");
						notify = new TypeSDKNotify_anzhi();
						notify.Init();
						isInit = true;
					} else {
						TypeSDKLogger.e("init_error:" + result);
					}
					break;
				case GameCallBack.SDK_TYPE_LOGIN:
					if (code == 200) {
						TypeSDKLogger.i("login_success");
						String sid = json.optString("sid");
						uid = json.optString("uid");// uid为安置账号唯一标示
						Nick = json.optString("nick_name");// 获得昵称
						TypeSDKLogger.e("sid:" + sid);
						TypeSDKLogger.e("uid:" + uid);
						notify = new TypeSDKNotify_anzhi();
						notify.sendToken(sid, uid);
						midManage.addPop(appActivity);
					} else {
						TypeSDKLogger.i("login_error:" + result);
					}
					break;

				case GameCallBack.SDK_TYPE_LOGOUT:
					TypeSDKLogger.e("Logout_success ");
					notify = new TypeSDKNotify_anzhi();
					notify.Logout();

				case GameCallBack.SDK_TYPE_PAY:
					json = new JSONObject(result);
					int status = json.optInt("payStatus"); // 支付状态
					if (status == 1 || status == 2) { // 1支付成功/2支付中
						TypeSDKLogger.e("pay_success");
						payResult = new PayResultData();
						payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"success");
						notify = new TypeSDKNotify_anzhi();
						notify.Pay(payResult.DataToString());
					} else { // 支付失败
						TypeSDKLogger.e("pay_fial");
						payResult = new PayResultData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"fial");
						notify = new TypeSDKNotify_anzhi();
						notify.Pay(payResult.DataToString());
					}

					break;

				case GameCallBack.SDK_TYPE_CANCEL_PAY:
					TypeSDKLogger.e("pay_CANCEL");
					payResult = new PayResultData();
					payResult.SetData(AttName.PAY_RESULT, "2");
					payResult
							.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
									"CANCEL");
					notify = new TypeSDKNotify_anzhi();
					notify.Pay(payResult.DataToString());

					break;

				case GameCallBack.SDK_TYPE_EXIT_GAME:
					appActivity.finish();
					System.exit(0);
					break;

				case GameCallBack.SDK_TYPE_CANCEL_EXIT_GAME:
					TypeSDKLogger.e("EXIT_GAME_CANCEL");
					break;

				default:
					break;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_anzhi notify = new TypeSDKNotify_anzhi();
			notify.Init();
			return;
		}
		this.anzhiInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.anzhiLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.anzhiLogout();
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
		this.anzhiPay(_in_pay);
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

			String extendInfo = new StringBuilder().append("gameId=")
					.append(platform.GetData(AttName.APP_ID))
					.append("&service=")
					.append(userInfo.GetData(AttName.SERVER_NAME))
					.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
					.append("&grade=")
					.append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);

			// // 2015.12.14 xnz 上传游戏信息(AnZhi_SDK_3.3之后必接)
			JSONObject info = new JSONObject();
			info.put(AnzhiSDK.GAME_AREA, userInfo.GetData(AttName.SERVER_ID));
			info.put(AnzhiSDK.GAME_LEVEL, userInfo.GetData(AttName.ROLE_LEVEL));
			info.put(AnzhiSDK.ROLE_ID, userInfo.GetData(AttName.ROLE_ID));
			info.put(AnzhiSDK.USER_ROLE, userInfo.GetData(AttName.ROLE_NAME));
			midManage.subGameInfo(info.toString());

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (exitGameListenser()) {
					midManage.exitGame(appActivity);
				}

			}
		});

		// System.exit(0);
	}

	public void onNewIntent(Intent intent) {
		TypeSDKLogger.i("onNewIntent");
		AnzhiSDK.getInstance().onNewIntentInvoked(intent);
	}

	public void onResume() {
		TypeSDKLogger.i("onResume");
		if (isInit) {
			AnzhiSDK.getInstance().onResumeInvoked();
			midManage.setPopVisible(appActivity, true);
		}
	}
	
	public void onStart(){
		TypeSDKLogger.i("onStart");
		AnzhiSDK.getInstance().onStartInvoked();
	}

	public void onStop() {
		TypeSDKLogger.i("onStop");
		AnzhiSDK.getInstance().onStopInvoked();
	}

	public void onPause() {
		TypeSDKLogger.i("onPause");
		AnzhiSDK.getInstance().onPauseInvoked();
		AnzhiSDK.getInstance().setPopVisible(appActivity, false);
	}

	public void onDestroy() {
		TypeSDKLogger.i("onDestroy");
		AnzhiSDK.getInstance().onDestoryInvoked();
		midManage.removePop(appActivity);
	}

	private void anzhiInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.e("SDK_NAME:"
							+ platform.GetData(AttName.SDK_NAME));
					TypeSDKLogger.e("SECRET_KEY:"
							+ platform.GetData(AttName.SECRET_KEY));
					TypeSDKLogger.e("APP_KEY:"
							+ platform.GetData(AttName.APP_KEY));
					isDebug = TypeSDKTool.isPayDebug;
					// 初始化方法
					midManage = AnzhiSDK.getInstance();
					midManage.init(appActivity,
								platform.GetData(AttName.APP_KEY),
								platform.GetData(AttName.SECRET_KEY),
								mGameCallBack);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void anzhiLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				midManage.login(appActivity);
			}
		});

	}

	private void anzhiLogout() {
		AnzhiSDK.getInstance().logout();
		// TypeSDKNotify_anzhi notify = new TypeSDKNotify_anzhi();
		// notify.Logout();
	}

	private void anzhiPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:"
							+ _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_DESC:"
							+ _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e("ITEM_COUNT:"
							+ _in_pay.GetInt(AttName.ITEM_COUNT));
					TypeSDKLogger.e("BILL_NUMBER:"
							+ _in_pay.GetData(AttName.BILL_NUMBER));
					int price = 0;
					if (isDebug) {
						price = 1;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}
					JSONObject payJson = new JSONObject();
					payJson.put("cpOrderId",
							_in_pay.GetData(AttName.BILL_NUMBER));
					payJson.put("cpOrderTime", System.currentTimeMillis());
					payJson.put("amount", price);
					payJson.put("cpCustomInfo",
							_in_pay.GetData(AttName.ITEM_NAME));
					payJson.put("productName",
							_in_pay.GetData(AttName.ITEM_NAME));
					payJson.put("productCode",
							_in_pay.GetData(AttName.ITEM_SERVER_ID));

					AnzhiSDK.getInstance().pay(
							Des3Util.encrypt(payJson.toString(),platform.GetData(AttName.SECRET_KEY)),
							MD5.encodeToString(platform.GetData(AttName.SECRET_KEY)));

				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: "
							+ exception.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		return "";
	}

}
