package com.type.sdk.android.qiren;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.xsdk.common.LoginCallData;
import com.xsdk.common.LoginErrorMsg;
import com.xsdk.common.OnLoginListener;
import com.xsdk.common.OnPayListener;
import com.xsdk.common.PayCallData;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean isLogin = false;

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
			TypeSDKNotify_qiren notify = new TypeSDKNotify_qiren();
			notify.Init();
			return;
		}
		this.qirenInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.qirenLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.qirenLogout();
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
		this.qirenPay(_in_pay);

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
		super.ShowShare(_in_context, _in_data);
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
			
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e( "进入游戏时的角色信息");
				
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e( "创建角色时的角色信息");
				
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e( "角色升级时角色信息");
			}else{
				TypeSDKLogger.e( "datatype error:"+"提交的数据不合法");
			}

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			if (isInit) {
				isInit = false;
			}
			System.exit(0);
		}
		
	}
	
	public void onCreate(Context _in_context) {
		TypeSDKLogger.e("onCreate");
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			if(isLogin){
			}
			
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		if (isInit) {
			
		}
	}

	private void qirenInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					com.xsdk.matrix.Matrix.init(appActivity);
					isInit = true;
					TypeSDKNotify_qiren notify = new TypeSDKNotify_qiren();
					notify.Init();
					TypeSDKLogger.i("init success");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void qirenLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				
				com.xsdk.matrix.Matrix.login(appActivity, new OnLoginListener() {
					
					@Override
					public void onLoginSuccess(LoginCallData callData) {
						// TODO Auto-generated method stub
						String uin = callData.getUserId();
						String token = callData.getToken();
						TypeSDKLogger.i("login Success");
						isLogin = true;
						TypeSDKNotify_qiren notify = new TypeSDKNotify_qiren();
						notify.sendToken(token, uin);
					}
					
					@Override
					public void onLoginFailer(LoginErrorMsg errorMsg) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("loginError:" + errorMsg.getCode() + ":" + errorMsg.getErrMsg());
					}
				});
						
			}
		});

	}

	private void qirenLogout() {
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				isLogin = false;
				TypeSDKNotify_qiren notify = new TypeSDKNotify_qiren();
				notify.Logout();
			}
		});
			
		
	}

	private void qirenPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					String price = "0";
					if(TypeSDKTool.isPayDebug){
						price = "1";
					}else{
						price = "" + (_in_pay.GetInt(AttName.REAL_PRICE)/100);
					}
					
					String extra_param = _in_pay.GetData(AttName.BILL_NUMBER);
					com.xsdk.matrix.Matrix.pay(appActivity, price, extra_param, new OnPayListener() {
						
						@Override
						public void onPayResult(PayCallData callData) {
							// TODO Auto-generated method stub
							TypeSDKNotify_qiren notify = new TypeSDKNotify_qiren();
							PayResultData payResult = new PayResultData();
							int code = callData.getCode();// code 是0表示成功，其他为失败
							String msg = callData.getMsg();
							TypeSDKLogger.i("payResult:" + code + ":" + msg);
							if(code == 0){
								TypeSDKLogger.i("onPaySucceed");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
							} else {
								TypeSDKLogger.e("onPayFailed");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
							}
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
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return null;
	}

}
