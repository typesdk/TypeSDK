package com.type.sdk.android.ewan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import cn.ewan.supersdk.open.CollectInfo;
import cn.ewan.supersdk.open.InitInfo;
import cn.ewan.supersdk.open.PayInfo;
import cn.ewan.supersdk.open.SuperCollectRoleData;
import cn.ewan.supersdk.open.SuperInitListener;
import cn.ewan.supersdk.open.SuperLogin;
import cn.ewan.supersdk.open.SuperLoginListener;
import cn.ewan.supersdk.open.SuperLogoutListener;
import cn.ewan.supersdk.open.SuperPayListener;
import cn.ewan.supersdk.open.SuperPlatform;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKLogger;

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
			TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
			notify.Init();
			return;
		}
		
		this.ewanInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.ewanLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.ewanLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
//		CwPlatform.getInstance().enterCwPlatformView(appContext);
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
		this.ewanPay(_in_pay);

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
			
			/*
			 * 参数一：1为登录动作，2为创建角色（必传） 
			 * 参数二：区服标识id（必传） 
			 * 参数三：区服名称（必传）
			 * 参数四：角色id（必传） 
			 * 参数五：角色名称（必传） 
			 * 参数六：角色等级（必传） 
			 * 参数七：扩展字段（可选）
			 */
			CollectInfo info = new CollectInfo(SuperCollectRoleData.getCollectRoleDataType(SuperCollectRoleData.createRole), 
					userInfo.GetData(AttName.SERVER_ID), 
					userInfo.GetData(AttName.SERVER_NAME), 
					userInfo.GetData(AttName.ROLE_ID), 
					userInfo.GetData(AttName.ROLE_NAME), 
					userInfo.GetInt(AttName.ROLE_LEVEL), 
					"create role");
			SuperPlatform.getInstance().collectData(appActivity, info);
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
			@Override
			public void run() {
				SuperPlatform.getInstance().logout(appActivity, new SuperLogoutListener() {
					
					@Override
					public void onGamePopExitDialog() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("onGamePopExitDialog");
						SuperPlatform.getInstance().exit(appActivity);
						System.exit(0);
					}
					
					@Override
					public void onGameExit() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("onGameExit");
						SuperPlatform.getInstance().exit(appActivity);
						System.exit(0);
					}
				});
				
			}
		});			
		}	
		
		
	}

	public void onResume(Context context) {
		TypeSDKLogger.e("onResume");
		SuperPlatform.getInstance().onResume(context);
	}
	
	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
		SuperPlatform.getInstance().onNewIntent(appContext, intent);
	}
	
	public void onRestart() {
		TypeSDKLogger.e("onRestart");
		SuperPlatform.getInstance().onRestart(appContext);
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		SuperPlatform.getInstance().onPause(appContext);
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		SuperPlatform.getInstance().onStop(appContext);
	}
	
	public void onStart(Context context) {
		TypeSDKLogger.e("onStart");
		SuperPlatform.getInstance().onStart(context);
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		SuperPlatform.getInstance().onDestroy(appContext);
	}

	private void ewanInit() {

		TypeSDKLogger.e("init begin");

		// final Activity runActivi;
		// if (UnityPlayer.currentActivity != null)
		// runActivi = UnityPlayer.currentActivity;
		// else
		// runActivi = appActivity;

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.e("APP_ID:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					TypeSDKLogger.e("SDK_CP_ID:" + platform.GetData(AttName.SDK_CP_ID));
					// SDK初始化
					
					// 封装初始化所需要的信息
					InitInfo info = new InitInfo();
					info.setAppId(platform.GetData(AttName.APP_ID));// 申请的应用ID
					info.setSignKey(platform.GetData(AttName.APP_KEY));// 申请的客户端签名key
					info.setPacketid(platform.GetData(AttName.SDK_CP_ID));// 包id
					
					/**
					 * 初始化接口
					 */
					SuperPlatform.getInstance().init(appActivity, info, new SuperInitListener()
					{
						@Override
						public void onSuccess()
						{
							// TODO 自动生成的方法存根
							TypeSDKLogger.e("init success");
							isInit = true;
							TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
							notify.Init();
						}

						@Override
						public void onFail(String msg)
						{
							// TODO 自动生成的方法存根
							TypeSDKLogger.e("init fail:" + msg);
						}
					});
												
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void ewanLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				
				/**
				 * 益玩登入接口
				 */
				SuperPlatform.getInstance().login(appActivity, new SuperLoginListener()
				{
					@Override
					public void onLoginSuccess(SuperLogin login)
					{
						// TODO 自动生成的方法存根
						//正常登入成功的回调
						TypeSDKLogger.e("login success");
						TypeSDKLogger.e("getOpenId:" + login.getOpenid());
						TypeSDKLogger.e("getToken:" + login.getToken());
						TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
						notify.sendToken(login.getToken(), login.getOpenid());
					}
					@Override
					public void onLoginFail(String msg)
					{
						// TODO 自动生成的方法存根
						//正常登入失败的回调
						TypeSDKLogger.e("login fail:" + msg);
						// ewanLogin();
					}
					@Override
					public void onLoginCancel()
					{
						// TODO 自动生成的方法存根
						TypeSDKLogger.e("login cancel");
						// ewanLogin();
					}
					@Override
					public void onNoticeGameToSwitchAccount()
					{
						// TODO 自动生成的方法存根
						//游戏弹出登入页面
						TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
						notify.Logout();
					}
					@Override
					public void onSwitchAccountSuccess(SuperLogin login)
					{
						// TODO 自动生成的方法存根
						//切换帐号成功的回调
						TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
						notify.reLogin(login.getToken(), login.getOpenid());
					}
				});
				
			}
		});

	}

	private void ewanLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (SuperPlatform.getInstance().isHasSwitchAccount()) {
					TypeSDKLogger.e("isHasSwitchAccount is true");
					SuperPlatform.getInstance().switchAccount(appActivity);
					
				}
				TypeSDKLogger.e("ewanLogout_start");
			}
		});
		
	}

	private void ewanPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_COUNT:" + _in_pay.GetData(AttName.ITEM_COUNT));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SERVER_ID:" + userInfo.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("SERVER_NAME:" + userInfo.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.e("ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
					TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
					TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
					
					float price;
					if(platform.GetData("mode").equals("debug")){
						price = 1f;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE) * 0.01f;
					}
					
					PayInfo payinfo = new PayInfo();
					//x元对应x*10元宝
					payinfo.setPrice(price);//_in_pay.GetInt(AttName.REAL_PRICE)/100
					payinfo.setServerId(userInfo.GetData(AttName.SERVER_ID));
					payinfo.setProductName(_in_pay.GetData(AttName.ITEM_NAME));
					payinfo.setProductNumber(_in_pay.GetInt(AttName.ITEM_COUNT));
					payinfo.setCutsomInfo(_in_pay.GetData(AttName.BILL_NUMBER));
					
					SuperPlatform.getInstance().pay(appActivity, payinfo, new SuperPayListener(){
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_ewan notify = new TypeSDKNotify_ewan();
						@Override
						public void onComplete(){
							// TODO 自动生成的方法存根
							TypeSDKLogger.e("pay_success");
							
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}

						@Override
						public void onCancel(){
							// TODO 自动生成的方法存根
							TypeSDKLogger.e("pay_cancel");
							
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
							notify.Pay(payResult.DataToString());
						}

						@Override
						public void onFail(String msg){
							// TODO 自动生成的方法存根
							TypeSDKLogger.e("return fail");
							
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}
					});
										
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
				}

			}
		});

	}
	
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
