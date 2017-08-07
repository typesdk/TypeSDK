/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.baidu;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baidu.gamesdk.ActivityAdPage;
import com.baidu.gamesdk.ActivityAdPage.Listener;
import com.baidu.gamesdk.ActivityAnalytics;
import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.OnGameExitListener;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.BDGameSDKSetting.Orientation;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;

import android.app.NotificationManager;
import android.content.Intent;
import com.type.sdk.android.TypeSDKData.BaseData;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public ActivityAdPage mActivityAdPage;
	private ActivityAnalytics mActivityAnalytics;
	
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
		if (isInit) 
		{
			TypeSDKNotify_baidu notify = new TypeSDKNotify_baidu();
			notify.Init();
			return;
		}
		
		this.baiduInit();
		mActivityAnalytics = new ActivityAnalytics(appActivity);
		mActivityAdPage = new ActivityAdPage(appActivity, new Listener() {
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("onClose");
			}
		});
		BDGameSDK.setSessionInvalidListener(new IResponse<Void>() {
			
			@Override
			public void onResponse(int resultCode, String arg1, Void arg2) {
				// TODO Auto-generated method stub
				if(resultCode == ResultCode.SESSION_INVALID){
					TypeSDKLogger.e("SESSION_INVALID");
					TypeSDKNotify_baidu notify = new TypeSDKNotify_baidu();
					notify.Logout();
				}
			}
		});
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.baiduLogin();

	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		BDGameSDK.logout();
		TypeSDKNotify_baidu notify_baidu = new TypeSDKNotify_baidu();
		notify_baidu.Logout();
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
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.baiduPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		if(!this.isInit)
		{
			this.baiduInit();
			return "error";
		}
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
		return BDGameSDK.isLogined() == true ? 1 : 0;
//		return 0;
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
			
			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName", userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.e("上传用户信息:string=" + userJsonExData);
			this.userInfo.CopyAttByData(userData);
			// this.ucSdkSendInfo(userJsonExData);
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(final Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		mActivityAdPage.onDestroy();
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if(exitGameListenser()){
					BDGameSDK.gameExit(_in_context, new OnGameExitListener() {
			
					@Override
					public void onGameExit() {
						// TODO Auto-generated method stub
						System.exit(0);
					}
				});	
				}
				
			}
		});
		
//		appActivity.finish();
	}

	public void onResume() {
		if (isInit) {
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					mActivityAdPage.onResume();
					TypeSDKLogger.i("mActivityAdPage.onResume");
				}
			});
			mActivityAnalytics.onResume();
			BDGameSDK.onResume(appActivity);
			TypeSDKLogger.e("onResume");
		}
	}

	public void onPause() {
		if (isInit) {
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					mActivityAdPage.onPause();
					TypeSDKLogger.i("mActivityAdPage.onPause");

				}
			});
			mActivityAnalytics.onPause();
			BDGameSDK.onPause(appActivity);
			TypeSDKLogger.e("onPause");
		}
	}

	public void onStop() {
		mActivityAdPage.onStop();
		TypeSDKLogger.e("onStop");
	}
	
	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		BDGameSDK.closeFloatView(appActivity);
		BDGameSDK.destroy();
	}

	// baidu
	private void baiduInit() 
	{
		TypeSDKLogger.e("init begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("APP_ID:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
					mBDGameSDKSetting.setAppID(platform.GetInt(AttName.APP_ID));
					mBDGameSDKSetting.setAppKey(platform.GetData(AttName.APP_KEY));
					//xnz 2015.12.1 添加判断DEBUG模式
					mBDGameSDKSetting.setDomain(Domain.DEBUG);
//					if(TypeSDKTool.isPayDebug){
//						mBDGameSDKSetting.setDomain(Domain.DEBUG);
//					}else{
//						mBDGameSDKSetting.setDomain(Domain.RELEASE);
//					}
					mBDGameSDKSetting.setOrientation(Orientation.LANDSCAPE);
					if (TypeSDKTool.isScreenOriatationPortrait(appContext))
						mBDGameSDKSetting.setOrientation(Orientation.PORTRAIT);
					else
						mBDGameSDKSetting.setOrientation(Orientation.LANDSCAPE);
					TypeSDKLogger.i("BaiduSDK do init function begin :"+mBDGameSDKSetting.toString());
					BDGameSDK.init(appActivity, mBDGameSDKSetting,
							new IResponse<Void>() {
								@Override
								public void onResponse(int resultCode,
										String resultDesc, Void extraData) 
								{
									
									switch (resultCode) {
									case ResultCode.INIT_SUCCESS:
										TypeSDKLogger.e("INIT_SUCCESS");
										isInit = true;
										//关闭录屏功能
//										BDGameSDK.setSupportScreenRecord(false);
										TypeSDKNotify_baidu notify = new TypeSDKNotify_baidu();
										notify.Init();
//										BDGameSDK.getAnnouncementInfo(appActivity);
										break;
									case ResultCode.INIT_FAIL:
										TypeSDKLogger.e("INIT_FAIL");
										break;
									default:
										break;
									}

								}

							});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

		
		setSuspendWindowChangeAccountListener();
	}

	private void baiduLogin() {
		TypeSDKLogger.e("baiduLogin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				BDGameSDK.login(new IResponse<Void>() {

					@Override
					public void onResponse(int resultCode, String resultDesc,
							Void extraData) {
						TypeSDKLogger.e("resultCode:" + resultCode);
						TypeSDKLogger.e("resultDesc:" + resultCode);
						switch (resultCode) {
						case ResultCode.LOGIN_SUCCESS:
							TypeSDKLogger.e("LOGIN_SUCCESS");
							TypeSDKLogger.e("AccessToken:" + BDGameSDK.getLoginAccessToken());
							TypeSDKNotify_baidu notify = new TypeSDKNotify_baidu();
							notify.sendToken(BDGameSDK.getLoginUid(),BDGameSDK.getLoginAccessToken());
							TypeSDKLogger.e("BDGameSDK.getLoginUid():"+BDGameSDK.getLoginUid()+"BDGameSDK.getLoginAccessToken():"+BDGameSDK.getLoginAccessToken());
							BDGameSDK.showFloatView(appActivity);
							break;
						case ResultCode.LOGIN_CANCEL:
							TypeSDKLogger.e("LOGIN_CANCEL");
//							baiduLogin();
							break;
						case ResultCode.LOGIN_FAIL:
							TypeSDKLogger.e("LOGIN_FAIL");
							TypeSDKLogger.e("LOGIN_resultDesc" + resultDesc);
							break;
						default:
							break;
						}
					}
				});
			}
		});
		
	}

	private void baiduPay(final PayInfoData _in_pay) {
		TypeSDKLogger.v("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
		TypeSDKLogger.v("EXTRA:" + _in_pay.GetData(AttName.EXTRA));
		TypeSDKLogger.v("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PayOrderInfo payOrderInfo = new PayOrderInfo();
				payOrderInfo.setCooperatorOrderSerial(_in_pay.GetData(AttName.BILL_NUMBER));
				int price = 0;
				if(TypeSDKTool.isPayDebug){
					price = 1;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE);
				}
				payOrderInfo.setTotalPriceCent((long)price);
				payOrderInfo.setExtInfo(_in_pay.GetData(AttName.EXTRA));
				payOrderInfo.setRatio(1); //兑换比例，此时不生效
				payOrderInfo.setProductName(_in_pay.GetData(AttName.ITEM_NAME));
				payOrderInfo.setCpUid(BDGameSDK.getLoginUid());
				BDGameSDK.pay(payOrderInfo, platform.GetData(AttName.PAY_CALL_BACK_URL), new IResponse<PayOrderInfo>() {

					@Override
					public void onResponse(int resultCode, String resultDesc,
							PayOrderInfo extraData) {
						PayResultData payResult = new PayResultData();

						switch (resultCode) {
						case ResultCode.PAY_SUCCESS:// 
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
							TypeSDKLogger.v("PAY_SUCCESS");
							break;
						case ResultCode.PAY_CANCEL:// 
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
							TypeSDKLogger.v("PAY_CANCEL");
							break;
						case ResultCode.PAY_FAIL:// 
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							TypeSDKLogger.v("PAY_FAIL");
							break;
						case ResultCode.PAY_SUBMIT_ORDER:// 
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON,
									"PAY_SUBMIT_ORDER");
							TypeSDKLogger.v("PAY_SUBMIT_ORDER");
							break;
						}
						
						TypeSDKNotify_baidu notify = new TypeSDKNotify_baidu();
						notify.Pay(payResult.DataToString());
				
					}
				});
			}
		});
	}

	private void setSuspendWindowChangeAccountListener() {
		new Handler(Looper.getMainLooper()).post(new Runnable(){
    	       @Override
    	       public void run(){
		BDGameSDK.setSuspendWindowChangeAccountListener(new IResponse<Void>() {

			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) {
				switch (resultCode) {
				case ResultCode.LOGIN_SUCCESS:
					TypeSDKLogger.v("AccessToken:" + BDGameSDK.getLoginAccessToken());
					TypeSDKNotify_baidu notify_baidu = new TypeSDKNotify_baidu();
//					notify_baidu.Logout();
					TypeSDKData.UserInfoData data = new TypeSDKData.UserInfoData();
					String userToken = BDGameSDK.getLoginAccessToken();
					String uid = BDGameSDK.getLoginUid();
					
					data.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
					data.SetData(AttName.USER_ID, uid);
					
					data.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.CP_ID);
					data.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.SDK_NAME);
					data.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.PLATFORM);
					notify_baidu.reLogin(data);
					TypeSDKLogger.v("LOGIN_SUCCESS");
					break;
				case ResultCode.LOGIN_FAIL:
					TypeSDKLogger.v("LOGIN_FAIL");
					break;
				case ResultCode.LOGIN_CANCEL:
					TypeSDKLogger.v("LOGIN_CANCEL");
					break;
				default:
					break;
				}
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
		return"";
	}

}
