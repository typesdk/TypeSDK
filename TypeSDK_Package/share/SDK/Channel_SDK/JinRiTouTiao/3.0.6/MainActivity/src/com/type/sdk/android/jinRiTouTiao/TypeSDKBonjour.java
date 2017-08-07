package com.type.sdk.android.jinRiTouTiao;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.ss.android.game.sdk.SsGameApi;
import com.ss.android.login.sdk.LogoutCallback;
import com.ss.android.login.sdk.StatusCallback;
import com.ss.android.login.sdk.activity.MobileActivity;
import com.ss.android.sdk.pay.PayRequestData;
import com.ss.android.sdk.pay.SSPayCallback;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public boolean isNoInit = false;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("error init do again");
			TypeSDKLogger.e( "sdk is already init");
			TypeSDKNotify_jinRiTouTiao notify = new TypeSDKNotify_jinRiTouTiao();
			notify.Init();
			return;
		}
		TypeSDKLogger.e("initSDK");
		this.jinRiInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.jinRiSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.jinRiSdkLogout();
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

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.jinRiSdkPay(_in_pay);
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
		TypeSDKLogger.e("执行ExitGame方法");
		this.ucSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e( "onPause");
	}

	public void onStop() {
		TypeSDKLogger.e( "onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e( "onDestroy");
	}



	private void jinRiInit() {
		TypeSDKLogger.e( "initSDK_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				SsGameApi.init(appContext,platform.GetData(AttName.APP_ID),platform.GetData(AttName.SECRET_KEY),platform.GetData(AttName.APP_KEY));
				TypeSDKLogger.e( "initSDK success");
				isInit=true;
				TypeSDKNotify_jinRiTouTiao notify=new TypeSDKNotify_jinRiTouTiao();
				notify.Init();
			}
		});
	}

	private void jinRiSdkLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				SsGameApi.tryLogin(appActivity,MobileActivity.FLOW_LOGIN, new StatusCallback() {
					
					@Override
					public void onSuccess(String token, long uid) {
						TypeSDKLogger.i(token+"||"+uid+"login success");
						TypeSDKNotify_jinRiTouTiao notify=new TypeSDKNotify_jinRiTouTiao();
						notify.sendToken(uid+"",token);
					}
					
					@Override
					public void onException(Exception arg0) {
						TypeSDKLogger.i("login exception  login fail");
					}
				});
			}
		});

	}

	private void jinRiSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				SsGameApi.tryLogout(new LogoutCallback() {
					@Override
					public void onSuccess(boolean arg0) {
						TypeSDKLogger.i("logout success");
						TypeSDKNotify_jinRiTouTiao notify=new TypeSDKNotify_jinRiTouTiao();
						notify.Logout();
					}
				}, true);
			}
		});
	}

	private void ucSdkExit() {
		TypeSDKLogger.i("执行 exitGame");
		System.exit(0);
	}

	private void jinRiSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				int price = 0;
				if(TypeSDKTool.isPayDebug){
					price = 1;
				}else{
					price = (int)_in_pay.GetInt(AttName.REAL_PRICE);
				}
				String outTradeNo=_in_pay.GetData(AttName.BILL_NUMBER);
				String subject=_in_pay.GetData(AttName.ITEM_NAME);
				PayRequestData payData=new PayRequestData(outTradeNo, subject, price);
				SsGameApi.tryPay(appActivity, payData, new SSPayCallback() {

					@Override
					public void onPayResult(int code, String result) {
						TypeSDKLogger.i(code+"|"+result);
						TypeSDKNotify_jinRiTouTiao notify=new TypeSDKNotify_jinRiTouTiao();
						PayResultData payResult=new PayResultData();
						if(code==0){
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							TypeSDKLogger.i("pay success");
						} else if(code==-1){
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_cancle");
							TypeSDKLogger.i("pay cancle");
						}else{
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "pay_fail");
							TypeSDKLogger.i("pay fail");
						}
						notify.Pay(payResult.DataToString());
					}
				});
			}
		});

	}



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
		return"";
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		TypeSDKLogger.i(requestCode+"||"+resultCode);
		 SsGameApi.onActivityResult(requestCode, resultCode, data);
	}

}
