/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.android.ch;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract.Attendees;

import com.chsdk.api.CHSdk;
import com.chsdk.api.EnterGameCallBack;
import com.chsdk.api.ExitCallBack;
import com.chsdk.api.LoginCallBack;
import com.chsdk.api.PayCallBack;
import com.chsdk.api.SplashDismissListener;
import com.chsdk.api.SwitchAccountCallBack;
import com.chsdk.api.UpdateGameCallBack;
import com.chsdk.api.UpdateRoleCallBack;
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
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
			notify.Init();
			return;
		}
		appContext = _in_context;
		appActivity = (Activity) appContext;
		this.caohuaInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.caohuaLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.caohuaLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		TypeSDKLogger.d("ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.d("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.caohuaPay(_in_pay);

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
			TypeSDKLogger.i("进入游戏时的角色信息");
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息				
				TypeSDKLogger.e("进入游戏时的角色信息");
				CHSdk.enterGame(userInfo.GetData(AttName.SERVER_ID),
						userInfo.GetData(AttName.SERVER_NAME),
						userInfo.GetData(AttName.ROLE_ID),
						userInfo.GetData(AttName.ROLE_NAME),
						userInfo.GetInt(AttName.ROLE_LEVEL), new EnterGameCallBack() {
							
							@Override
							public void success() {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void failed(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
				return;
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");
				CHSdk.updateLevel(
						userInfo.GetData(AttName.ROLE_NAME),
						userInfo.GetInt(AttName.ROLE_LEVEL),
						new UpdateRoleCallBack() {
							
							@Override
							public void success() {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void failed(String arg0) {
								// TODO Auto-generated method stub
								
							}
						});				
			}else{
				TypeSDKLogger.e("datatype error:"+"提交的数据类型不合法");
			}						

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.d("执行ExitGame方法");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if(exitGameListenser()){
					if(appActivity!=null){
					CHSdk.handleBackAction(appActivity, new ExitCallBack() {
						
						@Override
						public void exit() {
							// TODO Auto-generated method stub
							appActivity.finish();
							System.exit(0);
						}
					});
					}else{
						TypeSDKLogger.e("appActivity is null");
					}	
				}
				
			}
			
		});
		
	}

	
	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void caohuaInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// 1.SDK初始化
					CHSdk.init(appActivity, CHSdk.PORTRAIT, new SplashDismissListener() {
						
						@Override
						public void dismiss(boolean arg0) {
							// TODO Auto-generated method stub
							
						}
					}, new UpdateGameCallBack() {
						
						@Override
						public void dismiss() {
							// TODO Auto-generated method stub
							
						}
					});
					isInit = true;
					TypeSDKLogger.e("init success");
					TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
					notify.Init();					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void caohuaLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				
				CHSdk.login(appActivity, new LoginCallBack() {
					
					@Override
					public void success(String arg0, String userId, String token) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login success");
						TypeSDKLogger.e("userID:" + userId);
						TypeSDKLogger.e("token:" + token);

						TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
						notify.sendToken(token, userId);
					}
					
					@Override
					public void failed(String arg0) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login fail:"+arg0);
					}
					
					@Override
					public void exit() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login cancel");
					}
				}, true);

			}
		});

	}

	private void caohuaLogout() {
		TypeSDKLogger.i("logout_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// CHSDKInstace.CH_UserLogout();
				CHSdk.switchAccount(appActivity, new SwitchAccountCallBack() {
					
					@Override
					public void finish() {
						// TODO Auto-generated method stub
						TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
						notify.Logout();
						TypeSDKLogger.i("logout_success");
					}
				});
				
			}
			
		});
						
	}

	private void caohuaPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("BILL_NUMBER:"+ _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SEVER_ID:"+ _in_pay.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("SEVER_NAME:" + _in_pay.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.e("ROLE_ID:" + _in_pay.GetData(AttName.ROLE_ID));
					TypeSDKLogger.e("ROLE_NAME:" + _in_pay.GetData(AttName.ROLE_NAME));
					TypeSDKLogger.e("ITEM_COUNT:" + _in_pay.GetInt(AttName.ITEM_COUNT));
					String billNum = _in_pay.GetData(AttName.BILL_NUMBER);
					int price = 0;
					int price1=0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
						price1 = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE)/100;
						price1 = Integer.parseInt("" + price) * 10;
					}
					
					CHSdk.onlinePay(appActivity, billNum,
							price, price1,_in_pay.GetData(AttName.ITEM_NAME), userInfo.GetData(AttName.USER_ID)  , new PayCallBack() {
								
								@Override
								public void success(String arg0) {
									// TODO Auto-generated method stub
									TypeSDKLogger.e("pay_success");
									PayResultData payResult = new PayResultData();
									payResult.SetData(AttName.PAY_RESULT, "1");
									TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
									notify.Pay(payResult.DataToString());
								}
								
								@Override
								public void failed(String arg0) {
									// TODO Auto-generated method stub
									TypeSDKLogger.e("pay_fail"+"支付错误");
									PayResultData payResult = new PayResultData();
									payResult.SetData(AttName.PAY_RESULT, "0");
									TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
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
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return null;
	}	



}
