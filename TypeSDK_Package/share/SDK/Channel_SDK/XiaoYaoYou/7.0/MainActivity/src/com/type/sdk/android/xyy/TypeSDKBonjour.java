/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.xyy;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.game.sdk.HuosdkManager;
import com.game.sdk.domain.CustomPayParam;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.domain.RoleInfo;
import com.game.sdk.listener.OnInitSdkListener;
import com.game.sdk.listener.OnLoginListener;
import com.game.sdk.listener.OnLogoutListener;
import com.game.sdk.listener.OnPaymentListener;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKDefine.AttName;
//import com.type.sdk.android.uc.TypeSDKNotify;
import com.type.sdk.android.TypeSDKLogger;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean exit = false;
	public boolean isNoInit = false;
	boolean mCurrentInDebugMode = false;
	HuosdkManager sdkManager;
	private int initState = 0;
	public boolean isAllowSendInitNotify = true;
	private TypeSDKNotify notify;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
        TypeSDKLogger.d("initState " + initState);
		if (initState == 0) {
			
			notify = new TypeSDKNotify();
			
			TypeSDKLogger.d("initSDK");
			appContext = _in_context;
			appActivity = (Activity) appContext;
			SdkInit();
		} else if (initState == 2) {
			if (isAllowSendInitNotify) {
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.Init();
				return;
			}
		}
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
		userInfo.StringToData(_in_data);
		RoleInfo roleInfo = getRoleInfo();
		sdkManager.setRoleInfo(roleInfo, null);
	}

	private RoleInfo getRoleInfo() {
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.setRole_type(userInfo.GetInt(AttName.ROLE_TYPE));
        if("0" == userInfo.GetData(AttName.SERVER_ID)
           ||"" == userInfo.GetData(AttName.SERVER_ID))
        {
            roleInfo.setServer_id("s");
        }
        else
        {
		roleInfo.setServer_id(userInfo.GetData(AttName.SERVER_ID));
        }
		roleInfo.setServer_name(userInfo.GetData(AttName.SERVER_NAME));
		roleInfo.setRole_id(userInfo.GetData(AttName.ROLE_ID));
		roleInfo.setRole_name(userInfo.GetData(AttName.ROLE_NAME));
		roleInfo.setParty_name(userInfo.GetData("role_party"));
		roleInfo.setRole_level(userInfo.GetData(AttName.ROLE_LEVEL));
		roleInfo.setRole_vip(userInfo.GetData(AttName.VIP_LEVEL));
		roleInfo.setRole_balence(userInfo.GetFloat(AttName.SAVED_BALANCE));
		roleInfo.setRolelevel_ctime(userInfo.GetData(AttName.ROLE_CREATE_TIME));
		roleInfo.setRolelevel_mtime(userInfo.GetData(AttName.ROLE_LEVELUP_TIME));
		return roleInfo;
	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.d("执行ExitGame方法");
			if (!exit) {
				exit = true;
				this.SdkExit();
			}
		}

	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.d("onResume");
		sdkManager.showFloatView();
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.d("onPause");
	}

	public void onStop() {
		TypeSDKLogger.d("onStop");
		sdkManager.removeFloatView();
	}

	public void onDestroy() {
		sdkManager.recycle(null);
		TypeSDKLogger.d("onDestroy");
	}

	private void SdkInit() {

		TypeSDKLogger.i("init  begain");
		initState = 1;
		sdkManager = HuosdkManager.getInstance();
		sdkManager.addLoginListener(new OnLoginListener() {

			@Override
			public void loginError(int arg0, LoginErrorMsg arg1) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("HuosdkManager login error, code=" + arg1.code
						+ ", message=" + arg1.msg);
				SdkLogin(appContext);
			}

			@Override
			public void loginSuccess(int arg0, LogincallBack arg1) {
				// TODO Auto-generated method stub
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.sendToken(arg1.user_token, arg1.mem_id);
				sdkManager.showFloatView();
				TypeSDKLogger.d("HuosdkManager login success");
			}

		});
		sdkManager.initSdk(appContext, new OnInitSdkListener() {

			@Override
			public void initError(String arg0, String arg1) {
				// TODO Auto-generated method stub
				initState = 0;
				TypeSDKLogger.e("HuosdkManager init error, code=" + arg0
						+ ", msg=" + arg1);
			}

			@Override
			public void initSuccess(String arg0, String arg1) {
				// TODO Auto-generated method stub
				initState = 2;
				notify.Init();
				TypeSDKLogger.d("HuosdkManager init success");
			}
		});
		TypeSDKLogger.d("initSDK_end");
	}

	public void SdkLogin(Context _context) {
		TypeSDKLogger.i("login start");
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sdkManager.showLogin(true);
			}
		});
	}

	private void SdkLogout() {
		TypeSDKLogger.i("logout start");
		sdkManager.logout(new OnLogoutListener() {

			@Override
			public void logoutError(String arg0, String arg1) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("Logout error.");
			}

			@Override
			public void logoutSuccess(String arg0, String arg1) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("Logout success.");
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.Logout();
			}

		});
	}

	public void SdkExit() {

		TypeSDKLogger.d("SdkExit start");
		System.exit(0);
	}

	protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay) {

		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		int price = _in_pay.GetInt(AttName.REAL_PRICE);
		float realprice = price * 0.01f;
		String cpOrderId = _in_pay.GetData(AttName.BILL_NUMBER);
		String productId = _in_pay.GetData(AttName.ITEM_SERVER_ID);
		if("" == productId)
		{
			productId = "1";
		}
		String productName = _in_pay.GetData(AttName.ITEM_NAME);
		String productDesc = _in_pay.GetData(AttName.ITEM_DESC);
		int exchangeRate = 0;
		String currencyName = "";
		String ext = cpOrderId;
		int prouductCount = 1;
		String serverId = userInfo.GetData(AttName.SERVER_ID);

		final CustomPayParam customPayParam = new CustomPayParam();
		customPayParam.setCp_order_id(cpOrderId);
		customPayParam.setProduct_price(realprice);
		customPayParam.setProduct_count(prouductCount);
		customPayParam.setProduct_id(productId);
		customPayParam.setProduct_name(productName);
		customPayParam.setProduct_desc(productDesc);
		customPayParam.setExchange_rate(exchangeRate);
		customPayParam.setCurrency_name(currencyName);
		customPayParam.setServer_id(serverId);
		customPayParam.setExt(ext);
		RoleInfo roleInfo = getRoleInfo();
		roleInfo.setRole_type(4);
		customPayParam.setRoleinfo(roleInfo);

		sdkManager.showPay(customPayParam, new OnPaymentListener() {

			@Override
			public void paymentError(PaymentErrorMsg arg0) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("Pay Error, code=" + arg0.code + ", msg="
						+ arg0.msg);
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.Pay(payResult.DataToString());
			}

			@Override
			public void paymentSuccess(PaymentCallbackInfo arg0) {
				// TODO Auto-generated method stub
				TypeSDKLogger.i("Pay Success, msg=" + arg0.msg);
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
				TypeSDKNotify notify = new TypeSDKNotify();
				notify.Pay(payResult.DataToString());
			}
		});

	}

	// @Override
	// protected void SdkPayCancel() {
	// TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
	// payResult.SetData(AttName.PAY_RESULT, "0");
	// payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
	// TypeSDKNotify notify = new TypeSDKNotify();
	// notify.Pay(payResult.DataToString());
	// };

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

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKData.PayInfoData data = new TypeSDKData.PayInfoData();
		data.StringToData(_in_data);
		SdkPay(_in_context, data);
		return null;
	}
}
