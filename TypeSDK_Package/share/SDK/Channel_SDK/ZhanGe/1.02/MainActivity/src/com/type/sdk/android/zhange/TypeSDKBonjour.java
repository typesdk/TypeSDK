package com.type.sdk.android.zhange;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.game.sdk.YTSDKManager;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public boolean isNoInit = false;
	private Boolean isDebug;
	public OnLoginListener onLoginListener;
	public OnPaymentListener onPaymentListene;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		if (isInit) {
			TypeSDKLogger.i("already init" + isInit);
			TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
			notify.Init();
			return;
		}
		isDebug = TypeSDKTool.isPayDebug;
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		this.zhanGeSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.zhanGeSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.zhanGeSdkLogout();
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
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
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
		try {
			TypeSDKLogger.e("_in_data:" + _in_data);
			userInfo.StringToData(_in_data);

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);

			TypeSDKLogger.e("上传用户信息:string=" + userData.DataToString());

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.e("执行ExitGame方法");
			System.exit(0);
		}

	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		YTSDKManager.getInstance(_in_context).showFloatView();
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
	}

	public void onStop(Context _in_context) {
		TypeSDKLogger.e("onStop");
		YTSDKManager.getInstance(_in_context).removeFloatView();
	}

	public void onDestroy(Context _in_context) {
		TypeSDKLogger.e("onDestroy");
		YTSDKManager.getInstance(_in_context).recycle();
	}

	private void zhanGeSdkInit() {
		TypeSDKLogger.e("initSDK_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
				notify.Init();
				isInit = true;
				TypeSDKLogger.i("init_SUCCESS");
				onLoginListener = new OnLoginListener() {
					@Override
					public void loginSuccess(LogincallBack logincallBack) {
						TypeSDKLogger.e("login_SUCCESS");
						TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
						notify.sendToken(logincallBack.username + "|"
								+ logincallBack.logintime, logincallBack.sign);
						isInit = true;
						TypeSDKLogger.i(logincallBack.username + "|"
								+ logincallBack.logintime + "|"
								+ logincallBack.sign);
						YTSDKManager.getInstance(appContext).showFloatView();
					}

					@Override
					public void loginError(LoginErrorMsg errorMsg) {
						TypeSDKLogger.e("login_FAIL:" + errorMsg.code
								+ errorMsg.msg);
					}
				};

				onPaymentListene = new OnPaymentListener() {

					@Override
					public void paymentSuccess(
							PaymentCallbackInfo paymentCallbackInfo) {
						TypeSDKLogger.i("pay_SUCCESS");
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
						payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
						notify.Pay(payResult.DataToString());
					}

					@Override
					public void paymentError(PaymentErrorMsg errorMsg) {

						TypeSDKLogger.e("pay_FAIL:" + errorMsg.code + "|"
								+ errorMsg.money + "|" + errorMsg.msg);
						PayResultData payResult = new PayResultData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
						notify.Pay(payResult.DataToString());
					}
				};
			}
		});

		TypeSDKLogger.e("initSDK_end");
	}

	private void zhanGeSdkLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.i("login_start");
				YTSDKManager.getInstance(appContext).showLogin(appContext,
						true, onLoginListener);
			}
		});

	}

	private void zhanGeSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKNotify_ZhanGe notify = new TypeSDKNotify_ZhanGe();
				notify.Logout();
				TypeSDKLogger.i("logout_SUCCCESS");
			}
		});
	}

	@Override
	protected void SdkPay(Context _in_context,
			final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				int price;
				if (isDebug) {
					price = 1;
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
				}
				TypeSDKLogger.i("pay start");
				YTSDKManager.getInstance(appContext).showPay(appContext,
						_in_pay.GetData(AttName.ROLE_ID), price + "",
						_in_pay.GetData(AttName.SERVER_ID),
						_in_pay.GetData(AttName.ITEM_NAME),
						_in_pay.GetData(AttName.ITEM_DESC),
						_in_pay.GetData(AttName.BILL_NUMBER), onPaymentListene);
			}
		});

	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
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
