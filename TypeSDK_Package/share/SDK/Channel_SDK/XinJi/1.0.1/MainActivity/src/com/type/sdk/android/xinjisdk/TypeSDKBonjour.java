package com.type.sdk.android.xinjisdk;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.shengpay.express.smc.vo.OrderInfo;
import com.xinji.sdk.callback.AuthCallBack;
import com.xinji.sdk.callback.ExitCallBack;
import com.xinji.sdk.callback.LoginCallBack;
import com.xinji.sdk.callback.LogoutAccountCallBack;
import com.xinji.sdk.callback.PayCallBack;
import com.xinji.sdk.callback.RegisterCallBack;
import com.xinji.sdk.entity.UserInfo;
import com.xinji.sdk.http.data.request.BuildOrderRequest;
import com.xinji.sdk.manager.UserManager;
import com.xinji.sdk.util.XJGame;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import com.xinji.sdk.util.ScreenType;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;

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
			TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
			notify.Init();
			return;
		}
		this.xinjiSDKInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.xinjiSDKLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.xinjiSDKLogout();
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
		this.longxiaSDKPay(_in_pay);

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
				TypeSDKLogger.e("进入游戏时的角色信息");
				XJGame.submitUserGameRole(appContext,
						userInfo.GetData(XJGame.getUserName()),
						userInfo.GetData(AttName.ROLE_LEVEL),
						userInfo.GetData(AttName.ROLE_ID),
						userInfo.GetData(AttName.ROLE_NAME),
						userInfo.GetData(AttName.SERVER_ID));
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");

			} else if (userInfo.GetData("role_type").equals("levelUp")) {
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");
			} else {
				TypeSDKLogger.e("datatype error:" + "提交的数据不合法");
			}

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if (exitGameListenser()) {
			if (isInit) {
				XJGame.exit(appContext, new ExitCallBack() {

					@Override
					public void onExit() {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("exit game");
						isInit = false;
						System.exit(0);
					}
				});

			}
		}

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			//XJGame.showSdkFloatWindow(appActivity);
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			//XJGame.closeSdkFloatWindow(appActivity);
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

	private void xinjiSDKInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					
					XJGame.registerLogoutCallBack(new LogoutAccountCallBack() {

						@Override
						public void onSwitch() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("XJSDK Switch");
							TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
							notify.Logout();
						}

						@Override
						public void onLogout() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("XJSDK Logout");
							TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
							notify.Logout();
						}
					});
					
					// SDK初始化
					PackageInfo info = appContext.getPackageManager()
							.getPackageInfo(appContext.getPackageName(), 0);
					int gameVersion = info.versionCode;
					
					ApplicationInfo appInfo = appContext.getPackageManager().getApplicationInfo(
						appContext.getPackageName(), PackageManager.GET_META_DATA);
					String value = "" + appInfo.metaData.get("introduction");
					
					TypeSDKLogger.i("pid:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.i("screenType:" + ScreenType.SCREEN_LAND);
					TypeSDKLogger.i("fullScreen:" + "true");
					TypeSDKLogger.i("gameVersion:" + gameVersion + "");
					TypeSDKLogger.i("gameName:" + platform.GetData(AttName.APP_NAME));
					TypeSDKLogger.i("introduction:" + value);

					HashMap<String, String> configData = new HashMap<String, String>();
					configData.put("pid", platform.GetData(AttName.APP_ID));
					configData.put("screentype", ScreenType.SCREEN_LAND + "");
					configData.put("fullScreen", "true");
					configData.put("gameVersion", gameVersion + "");
					configData.put("gameName",
							platform.GetData(AttName.APP_NAME));
					configData.put("introduction", value);
					XJGame.authorization(appContext, configData,
							new AuthCallBack() {

								@Override
								public void onAuthSuccess() {
									// TODO Auto-generated method stub
									TypeSDKLogger.i("initSuccess");

									isInit = true;
									TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
									notify.Init();
								}

								@Override
								public void onAuthFailed() {
									// TODO Auto-generated method stub
									TypeSDKLogger.e("init Error");
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

	private void xinjiSDKLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				XJGame.login(appActivity, new LoginCallBack() {

					@Override
					public void onLoginSuccess(UserInfo info) {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("Login success");
						String uid = info.getUserNo();
						String token = info.getToken();
						TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
						notify.sendToken(token, uid);
						//XJGame.showSdkFloatWindow(appActivity);
					}

					@Override
					public void onLoginFail() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("loginFail");
					}

					@Override
					public void onLoginCancel() {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("login Cancel");
					}
				});
			}
		});

	}

	private void xinjiSDKLogout() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				XJGame.logoutAccount();
				TypeSDKLogger.i("LogOffSuccess");

			}
		});

	}

	private void longxiaSDKPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
					}
					
					TypeSDKLogger.i("pay data:");
					TypeSDKLogger.i("setLoginName:" + XJGame.getUserName());
					TypeSDKLogger.i("setAmount:" + price);
					TypeSDKLogger.i("setOrder:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.i("setGamersRole:" + userInfo.GetData(AttName.ROLE_NAME));
					TypeSDKLogger.i("setGameName:" + platform.GetData(AttName.APP_NAME));
					TypeSDKLogger.i("setProductName:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.i("setServerNum:" + _in_pay.GetData(AttName.SERVER_ID));
					TypeSDKLogger.i("setUserNo:" + _in_pay.GetData(AttName.USER_ID));
					
					BuildOrderRequest request = new BuildOrderRequest();
					request.setLoginName(XJGame.getUserName());
					request.setAmount(price); //金额
					request.setOrder(_in_pay.GetData(AttName.BILL_NUMBER)); //厂商订单ID
					request.setGamersRole(userInfo.GetData(AttName.ROLE_NAME)); //忘记角色
					request.setGameName(platform.GetData(AttName.APP_NAME)); //游戏名称
					request.setProductName(_in_pay.GetData(AttName.ITEM_NAME)); //产品名称
					request.setServerNum(userInfo.GetData(AttName.SERVER_ID));//服务器
					request.setExtra(userInfo.GetData(AttName.ROLE_ID) + "|" + _in_pay.GetData(AttName.BILL_NUMBER)); //备注
					request.setUserNo(userInfo.GetData(AttName.USER_ID));

					XJGame.pay(appActivity, request, new PayCallBack() {
						TypeSDKNotify_xinjisdk notify = new TypeSDKNotify_xinjisdk();
						PayResultData payResult = new PayResultData();
						@Override
						public void onPaySuccess() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("paySuccess");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onPayFailed() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("payFail");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onPayChecking() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("onPayChecking");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onPayCancel() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("payFail");
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
							notify.Pay(payResult.DataToString());
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
