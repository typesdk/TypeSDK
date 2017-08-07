/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.cmge;

import com.cmge.sdk.CmgeSdkManager;
import com.cmge.sdk.ILoginCallback;
import com.cmge.sdk.LoginResult;
import com.cmge.sdk.pay.CmgePayListener;
import com.cmge.sdk.pay.common.entity.PayCallbackInfo;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.Intent;
import com.type.sdk.android.TypeSDKData.BaseData;
//import com.type.sdk.notification.PushService;
//import com.type.sdk.notification.SharedPreferencesUtil;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
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
			TypeSDKNotify_cmge notify = new TypeSDKNotify_cmge();
			notify.Init();
			return;
		}
		
		this.cmgeInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.cmgeLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.cmgeLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
		CmgeSdkManager.getInstance().openUserManagementCenter(_in_context);
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
		CmgeSdkManager.getInstance().showDragonController(_in_context, 0, 10);
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
		CmgeSdkManager.getInstance().hideDragonController(_in_context);
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.cmgePay(_in_pay);
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
		try {
			userInfo.StringToData(_in_data);
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息:"+_in_data);
				return;
				
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息:"+_in_data);
				return;
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息:"+_in_data);
				return;
			}else{
				TypeSDKLogger.e("datatype error:"+"提交的数据类型不合法");
			}						
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}
	@Override
	public void ExitGame(final Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			System.exit(0);			
		}
		
		/* if (exitGameListenser()) {
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					if (exitBy2Click(_in_context)) {
						CmgeSdkManager.getInstance().onGameQuit(appContext);
						 TypeSDKData.BaseData exitResult = new TypeSDKData.BaseData();
						 exitResult.SetData("exitreason", "quit game");
						 TypeSDKEventManager.Instance().SendUintyEvent(ReceiveFunction.MSG_EXITGAMECANCEL, exitResult.DataToString());
						 System.exit(0);
					}
				}
			});
		} */
	}
	

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		CmgeSdkManager.getInstance().showDragonController(_in_context, 0, 300);
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		CmgeSdkManager.getInstance().hideDragonController(appContext);
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void cmgeInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					isInit = true;
					// SDK初始化
					TypeSDKEventManager.Instance().SendUintyEvent(
							ReceiveFunction.MSG_INITFINISH,
							platform.DataToString());
					TypeSDKEventManager.Instance().SendUintyEvent(
							ReceiveFunction.MSG_UPDATEFINISH,
							platform.DataToString());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void cmgeLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				CmgeSdkManager.getInstance().launchLoginActivity(appActivity, new ILoginCallback(){
					@Override
					public void callback(int resultCode, String resultDescription, LoginResult loginResult){
						if (resultCode == ILoginCallback.SUCCEEDED){	
							new TypeSDKBonjour().ShowToolBar(appContext);
							TypeSDKLogger.e("login success");
							TypeSDKLogger.e("sign:" + loginResult.sign);
							TypeSDKLogger.e("timestamp:" + loginResult.timestamp);
							TypeSDKLogger.e("userId:" + loginResult.userId);
							TypeSDKNotify_cmge notify = new TypeSDKNotify_cmge();
							notify.sendToken(loginResult.sign+"|"+loginResult.timestamp + "", loginResult.userId + "");
							
						}else if (resultCode == ILoginCallback.USER_QUIT){
							TypeSDKLogger.e("login cancel");
//							cmgeLogin();
						}
					}
				}, false);
			}
		});

	}

	private void cmgeLogout() {
		TypeSDKLogger.e("logout_success");
		CmgeSdkManager.getInstance().onGameQuit(appContext);
		TypeSDKNotify_cmge notify = new TypeSDKNotify_cmge();
		notify.Logout();
	}

	private void cmgePay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SERVER_ID:" + _in_pay.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("SERVER_NAME:" + _in_pay.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.e("ROLE_ID:" + _in_pay.GetData(AttName.ROLE_ID));
					TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
					TypeSDKLogger.e("ROLE_NAME:" + _in_pay.GetData(AttName.ROLE_NAME));
					TypeSDKLogger.e("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
					TypeSDKLogger.e("callbackinfo:" + _in_pay.GetData(AttName.EXTRA) + "|" + _in_pay.GetData(AttName.BILL_NUMBER));
				
					int price;
					if(platform.GetData("mode").equals("debug")){
						price = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE)/100;
					}
                    if(price <=0)price = 1;
					/**
					 * context：Context 实例。 
					 * 游戏 区服 ID ，SDK服务器 充值回调 CP服务器 需要用到 
					 * 游戏区服名  ，SDK服务器充值回调  CP服务器 需要用到
					 * 游戏角色 ID ，SDK 服务器充值回调 CP服务器需要用到
					 * 戏角色名称 ，SDK 服务器充值回调 CP 服务器需要用到
					 * 道具 ID ，仅用于运营商支付.这个道具 ID 是 SDK自定义的道具  ID ，而不是 在运营商申请的 道具 ID 或者计费点代码 .
					 * 充值金额（单位：元）  ，int 整型 数字不超过 9999 9999且必须大于 0
					 * 充值界面显示的 标题，如不需要显示标题可传空字符串 
					 * cp自定义数据
					 * 充值类型（0代表不定额充值，1定额充值，2话费快充）
					 * 特殊支付源（当前场景不支持短代支付），0默认非特殊支付源，1特殊支付源，如无特殊需求则传0
					 * 支付回调接口
					 */
					CmgeSdkManager.getInstance().pay(appContext, _in_pay.GetData(AttName.SERVER_ID), 
							"SERVER_NAME", 
							_in_pay.GetData(AttName.ROLE_ID), _in_pay.GetData(AttName.ROLE_NAME), _in_pay.GetData(AttName.ITEM_SERVER_ID), 
							price, _in_pay.GetData(AttName.ITEM_NAME), _in_pay.GetData(AttName.EXTRA) + "|" + _in_pay.GetData(AttName.BILL_NUMBER), 
							1, 0, new CmgePayListener() { 
						PayResultData payResult = new PayResultData();
								@Override
								public void onPayFinish(PayCallbackInfo callback) {
									// TODO Auto-generated method stub
									if (callback.statusCode == PayCallbackInfo.STATUS_SUCCESS) {
										// 支付成功
										TypeSDKLogger.e("pay_success");
										payResult.SetData(AttName.PAY_RESULT, "1");
										TypeSDKNotify_cmge notify = new TypeSDKNotify_cmge();
										notify.Pay(payResult.DataToString());
									}
									if (callback.statusCode == PayCallbackInfo.STATUS_FAILURE) {
										// 支付失败
										TypeSDKLogger.e("return Error");
										payResult.SetData(AttName.PAY_RESULT, "0");
										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
										TypeSDKNotify_cmge notify = new TypeSDKNotify_cmge();
										notify.Pay(payResult.DataToString());						
									}
								}
							});
					
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
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
		return"";
	}

	
}
