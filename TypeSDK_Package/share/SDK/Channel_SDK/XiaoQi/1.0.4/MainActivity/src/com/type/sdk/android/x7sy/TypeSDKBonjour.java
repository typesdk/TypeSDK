package com.type.sdk.android.x7sy;

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.smwl.smsdk.abstrat.SMInitListener;
import com.smwl.smsdk.abstrat.SMLoginListener;
import com.smwl.smsdk.abstrat.SMLogoutListener;
import com.smwl.smsdk.abstrat.SMPayListener;
import com.smwl.smsdk.app.SMPlatformManager;
import com.smwl.smsdk.bean.PayInfo;
import com.smwl.smsdk.bean.SMUserInfo;

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
			TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
			notify.Init();
			return;
		}
		this.x7syInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.x7syLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		isLogin = false;
		TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
		notify.Logout();
		//this.x7syLogout();
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
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		TypeSDKLogger.e(_in_OrderID);
		this.x7syPay(_in_pay);
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
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SendInfo");
		try {

			userInfo.StringToData(_in_data);

			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
			} else if (userInfo.GetData("role_type").equals("levelUp")) {
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");
			} else {
				TypeSDKLogger.e("datatype error:" + "提交的数据不合法");
			}

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",
					userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName",
					userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.e("上传用户信息:string=" + userJsonExData);
			// this.userInfo.CopyAttByData(userData);
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		TypeSDKData.BaseData exitResult = new TypeSDKData.BaseData();
		exitResult.SetData("exitreason", "quit game");
		TypeSDKEventManager.Instance().SendUintyEvent(
				ReceiveFunction.MSG_EXITGAMECANCEL, exitResult.DataToString());
		System.exit(0);
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if(isLogin){
			SMPlatformManager.getInstance().Float(appActivity);
		}
		
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		SMPlatformManager.getInstance().hintFloat();
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void x7syInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				SMPlatformManager.getInstance().init(appActivity,
						platform.GetData(AttName.APP_KEY),
						new SMInitListener() {

							@Override
							public void onFail(String arg0) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("init failed");
							}

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
								notify.Init();
							}

						});
			}
		});
		TypeSDKLogger.v("init done");
	}

	private void x7syLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SMPlatformManager.getInstance().Login(appActivity,
						new SMLoginListener() {

							@Override
							public void onLoginCancell() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login cancle");
							}

							@Override
							public void onLoginFailed(String arg0) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("login failed");
							}

							@Override
							public void onLoginSuccess(SMUserInfo arg0) {
								// TODO Auto-generated method stub
								isLogin = true;
								x7syLogout();
								String token = arg0.getTokenkey();
								TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
								notify.sendToken(token);
							}
						});
			}
		});
	}

	private void x7syLogout() {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SMPlatformManager.getInstance().Logout(appActivity,
						new SMLogoutListener() {
							@Override
							public void onLogoutFailed(String arg0) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("logout failed");
							}

							@Override
							public void onLogoutSuccess() {
								// TODO Auto-generated method stub
								isLogin = false;
								TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
								notify.Logout();
							}
						});
			}
		});

	}

	private void x7syPay(PayInfoData _in_pay) {
		TypeSDKLogger.e("pay start:" + _in_pay.DataToString());
		
		String price = "0";
		if(TypeSDKTool.isPayDebug){
			price = "1";
		}else{
			price = "" + (_in_pay.GetInt(AttName.REAL_PRICE)/100);
		}
		
		TypeSDKLogger.e("payid:" + platform.GetData(AttName.PRODUCT_ID));
		TypeSDKLogger.e("game_orderid:" + _in_pay.GetData(AttName.BILL_NUMBER));
		TypeSDKLogger.e("game_price:" + price);
		TypeSDKLogger.e("server_name:" + _in_pay.GetData(AttName.SERVER_NAME));
		TypeSDKLogger.e("subject:" + _in_pay.GetData(AttName.ITEM_NAME));
		
		final PayInfo payinfo = new PayInfo();
		payinfo.payid = platform.GetData(AttName.PRODUCT_ID);
		payinfo.game_orderid = _in_pay.GetData(AttName.BILL_NUMBER);
		payinfo.game_price = price;
		payinfo.server_id = "";
		payinfo.server_name = _in_pay.GetData(AttName.SERVER_NAME);
		payinfo.subject = _in_pay.GetData(AttName.ITEM_NAME);
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SMPlatformManager.getInstance().Pay(appActivity, payinfo,
						new SMPayListener() {
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_x7sy notify = new TypeSDKNotify_x7sy();
							@Override
							public void onPayCancell() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("pay cancled");
								payResult.SetData(AttName.PAY_RESULT, "2");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
								notify.Pay(payResult.DataToString());
							}

							@Override
							public void onPayFailed(Object arg0) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("pay failed");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
							}

							@Override
							public void onPaySuccess(Object arg0) {
								// TODO Auto-generated method stub
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
								TypeSDKLogger.v("PAY_SUCCESS");
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

}
