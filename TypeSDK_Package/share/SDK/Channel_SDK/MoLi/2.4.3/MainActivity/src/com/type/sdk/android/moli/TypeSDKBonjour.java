package com.type.sdk.android.moli;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
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
	public YTSDKManager sdkmanager;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	public void onCreate(Context _in_context) {
		TypeSDKLogger.i("onCreate");
		sdkmanager = YTSDKManager.getInstance(_in_context);
		isInit = true;
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.i("onResume");
		sdkmanager.showFloatView();// 显示悬浮窗口
		sdkmanager.onResume();
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.i("onPause");
	}

	public void onStop(Context _in_context) {
		TypeSDKLogger.i("onStop");
		sdkmanager.removeFloatView();// 隐藏悬浮窗口
		sdkmanager.onStop();
	}

	public void onDestroy() {
		TypeSDKLogger.i("onDestroy");
		sdkmanager.recycle();// 游戏退出必须调用
		sdkmanager.onDestroy();
	}
	
	public void onStart(Context _in_context){
		TypeSDKLogger.i("onStart");
		sdkmanager.onStart();
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.i("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			System.currentTimeMillis();
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.i("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.moliLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.i("ShowLogout");
		this.moliLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		TypeSDKLogger.i("ShowToolBar");
		if(sdkmanager!=null){
			sdkmanager.removeFloatView();
		}
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("HideToolBar");
		if(sdkmanager!=null){
			sdkmanager.showFloatView();
		}
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);
		this.moliPay(_in_pay);
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		return _in_OrderID;
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		try {
			TypeSDKLogger.e("_in_data:" + _in_data);
			userInfo.StringToData(_in_data);
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.i("执行ExitGame方法");
		System.exit(0);
	}
	private void moliLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				sdkmanager.showLogin(appContext, true, new OnLoginListener() {					
					@Override
					public void loginSuccess(LogincallBack logincallback) {
						TypeSDKNotify_MoLi notify = new TypeSDKNotify_MoLi();
						notify.sendToken(logincallback.username+"|"+logincallback.logintime,logincallback.sign);
						sdkmanager.showFloatView();// 显示浮点
					}

					@Override
					public void loginError(LoginErrorMsg errorMsg) {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("LoginError:"+"\n"+"code:"+errorMsg.code+"\n"+"msg:"+errorMsg.msg);

					}
				});
			}
		});
	}

	private void moliLogout() {
		TypeSDKNotify_MoLi notify = new TypeSDKNotify_MoLi();
		notify.Logout();
	}
	private void moliPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.d("pay_start");
				TypeSDKLogger.i("payInfo:" + _in_pay.DataToString());
				int price;
				if (TypeSDKTool.isPayDebug) {
					price = 1;
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
				}
				TypeSDKLogger.e("price:" + String.valueOf(price));
				sdkmanager.showPay(appContext,
						_in_pay.GetData(AttName.ROLE_ID),
						String.valueOf(price),
						_in_pay.GetData(AttName.SERVER_ID),
						_in_pay.GetData(AttName.ITEM_NAME),
						_in_pay.GetData(AttName.ITEM_DESC),
						_in_pay.GetData(AttName.BILL_NUMBER),
						new OnPaymentListener() {
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_MoLi notify = new TypeSDKNotify_MoLi();

							@Override
							public void paymentSuccess(
									PaymentCallbackInfo callbackInfo) {
								// TODO Auto-generated method stub
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_DATA,
										"充值金额数：" + callbackInfo.money
												+ " 消息提示：" + callbackInfo.msg);
								notify.Pay(payResult.DataToString());
							}

							@Override
							public void paymentError(PaymentErrorMsg errorMsg) {
								// TODO Auto-generated method stub
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_DATA,
										"充值失败：code:" + errorMsg.code
												+ "  ErrorMsg:" + errorMsg.msg
												+ "  预充值的金额：" + errorMsg.money);
								notify.Pay(payResult.DataToString());
							}
						});

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

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}
	
}
