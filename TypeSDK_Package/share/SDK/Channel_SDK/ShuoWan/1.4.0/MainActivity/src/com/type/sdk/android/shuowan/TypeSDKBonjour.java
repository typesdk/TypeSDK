package com.type.sdk.android.shuowan;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKTool;
import com.weizhong.shuowan.sdk.ShuoWanSDK;
import com.weizhong.shuowan.sdk.listener.InitListener;
import com.weizhong.shuowan.sdk.listener.OnExitListener;
import com.weizhong.shuowan.sdk.yt.LoginCallback;
import com.weizhong.shuowan.sdk.yt.LoginErrorMsg;
import com.weizhong.shuowan.sdk.yt.OnLoginListener;
import com.weizhong.shuowan.sdk.yt.OnPaymentListener;
import com.weizhong.shuowan.sdk.yt.PaymentCallbackInfo;
import com.weizhong.shuowan.sdk.yt.PaymentErrorMsg;
import com.weizhong.shuowan.sdk.listener.LogoutNotifyListener;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

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
		if (isInit) {
			TypeSDKLogger.i("already init"+isInit);
			TypeSDKNotify_SW notify = new TypeSDKNotify_SW();
			notify.Init();
			return;
		}
		isDebug = TypeSDKTool.isPayDebug;
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		swSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.swSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.swSdkLogout();
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
		if(appActivity!=null){
			ShuoWanSDK.defaultSDK().showFloatView(appActivity);
		}else{
			TypeSDKLogger.e("appActivity is null");
		}
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
		ShuoWanSDK.defaultSDK().removeFloatView();
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.swSdkPay(_in_pay);
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
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				swSdkExit();
			}
		});

	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		TypeSDKLogger.e("onResume test");

	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e( "onPause");
	}

	public void onStop() {
		TypeSDKLogger.e( "onStop");
		ShuoWanSDK.defaultSDK().removeFloatView();
	}

	public void onDestroy() {
		TypeSDKLogger.e( "onDestroy");
	}



	private void swSdkInit() {
		TypeSDKLogger.e( "initSDK_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				ShuoWanSDK.defaultSDK().init(appContext,new InitListener() {

					@Override
					public void initSuccess() {
						TypeSDKLogger.i("init success");
						TypeSDKNotify_SW notify=new TypeSDKNotify_SW();
						notify.Init();
						isInit=true;
					}

					@Override
					public void initFailed(String arg0) {
						TypeSDKLogger.i("init fail"+arg0);
					}
				});
			}
		});
	}



	private void swSdkLogin() {
		TypeSDKLogger.i("login start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				ShuoWanSDK.defaultSDK().showLogin(appContext,true,new OnLoginListener(){

					@Override
					public void loginError(LoginErrorMsg errorMsg) {
						TypeSDKLogger.i("login fail"+errorMsg.msg+errorMsg.code);

					}

					@Override
					public void loginSuccess(LoginCallback logincallback) {
						TypeSDKLogger.i("login success");
						String userName=logincallback.username;
						String loginTime=logincallback.logintime+"";
						String sign=logincallback.sign;
						TypeSDKLogger.i(userName+"|"+loginTime+"|"+sign);
						TypeSDKNotify_SW notify=new TypeSDKNotify_SW();
						notify.sendToken(userName+"|"+loginTime,sign);
						ShuoWanSDK.defaultSDK().showFloatView(appActivity);
						// 退出登陆回调监听，用户在个人中心修改密码或退出登录时调用
						ShuoWanSDK.defaultSDK().setLougoutNotifyListener(new LogoutNotifyListener() {

							@Override
							public void onLogout() {
								TypeSDKNotify_SW notify=new TypeSDKNotify_SW();
								notify.Logout();
								TypeSDKLogger.i("login out success");
							}
						});
					}
				});
			}
		});
	}

	private void swSdkLogout() {
		TypeSDKLogger.i("login out");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				ShuoWanSDK.defaultSDK().logout();
			}
		});
	}

	private void swSdkExit() {
		TypeSDKLogger.i("exitGame");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				ShuoWanSDK.defaultSDK().exitSDK(appContext, new OnExitListener() {

					@Override
					public void exit() {
						ShuoWanSDK.defaultSDK().onExit();
						TypeSDKLogger.i("exitGame success");
					}

					@Override
					public void backToGame() {

					}
				});
			}
		});
	}

	private void swSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				double price;
				if(isDebug){
					price = 1;
				}else{
					price = (double) (_in_pay.GetInt(AttName.REAL_PRICE)/100);
				}
				String roleid=_in_pay.GetData(AttName.ROLE_ID);
				String serverid=_in_pay.GetData(AttName.SERVER_ID);
				String productname=_in_pay.GetData(AttName.ITEM_NAME);
				String productdesc=_in_pay.GetData(AttName.ITEM_DESC);
				String billNymber=_in_pay.GetData(AttName.BILL_NUMBER);
				ShuoWanSDK.defaultSDK().showPay(appContext, roleid, price, serverid, productname, productdesc, billNymber,new OnPaymentListener() {
					PayResultData payResult = new PayResultData();
					TypeSDKNotify_SW notify=new TypeSDKNotify_SW();
					@Override
					public void paymentSuccess(PaymentCallbackInfo arg0) {
						TypeSDKLogger.i("pay success ");
						payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
						notify.Pay(payResult.DataToString());
					}
					@Override
					public void paymentError(PaymentErrorMsg msg) {
						switch (msg.code) {
						case PaymentErrorMsg.CODE_CANCEL:
							TypeSDKLogger.i("pay cancel ");
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
							notify.Pay(payResult.DataToString());
							break;
						case PaymentErrorMsg.CODE_FAILED:
							TypeSDKLogger.i("pay fail ");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
							notify.Pay(payResult.DataToString());
							break;
						case PaymentErrorMsg.CODE_UNKNOW:
							TypeSDKLogger.i("pay 未知错误 ");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
							notify.Pay(payResult.DataToString());
							break;
						}
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

}
