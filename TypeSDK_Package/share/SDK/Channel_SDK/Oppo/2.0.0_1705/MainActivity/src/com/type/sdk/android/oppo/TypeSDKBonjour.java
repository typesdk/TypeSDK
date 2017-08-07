package com.type.sdk.android.oppo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.GameCenterSettings;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.game.sdk.common.model.biz.ReportUserGameInfoParam;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.common.model.ApiResult;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean isDebug;
	private String token;
	int age = 0;
	
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
		isDebug = TypeSDKTool.isPayDebug;
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_oppo notify = new TypeSDKNotify_oppo();
			notify.Init();
			return;
		}
		this.oppoInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.oppoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.oppoLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
//		GameCenterSDK.getInstance().doShowGameCenter(new ApiCallback() {
//		@Override
//		public void onSuccess(String content, int code) {
//			// TODO Auto-generated method stub
//			TypeSDKLogger.e("切换账号成功2");
//			SharkSDKNotify_oppo notify = new SharkSDKNotify_oppo();
//			notify.Logout();
//		}
//		
//		@Override
//		public void onFailure(String content, int code) {
//			// TODO Auto-generated method stub
//			TypeSDKLogger.e("切换账号失败2");
//			oppoLogin();
//		}
//	}, appActivity);
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
		this.oppoPay(_in_pay);
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
			
			GameCenterSDK.getInstance().doReportUserGameInfoData( 
					new ReportUserGameInfoParam(platform.GetData(AttName.APP_ID), 
							userInfo.GetData(AttName.SERVER_NAME), 
							userInfo.GetData(AttName.ROLE_ID), 
							userInfo.GetData(AttName.ROLE_LEVEL)), new ApiCallback() {
						
						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFailure(String arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					});
			
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
				// TODO Auto-generated method stub
				if (isInit) {
					GameCenterSDK.getInstance().onExit(appActivity, new GameExitCallback() {
					
						@Override
						public void exitGame() {
							// TODO Auto-generated method stub
							appActivity.finish();
							System.exit(0);
						}
					});
				} else {
					appActivity.finish();
					System.exit(0);
				}
				
			}
		});			
		}
		
		
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			GameCenterSDK.getInstance().onResume(appActivity);			
		}
		
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			GameCenterSDK.getInstance().onPause();
		}
		
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void oppoInit() {

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
					
					/* GameCenterSettings gameCenterSettings = new GameCenterSettings(
							false,    // 网游固定为false
							platform.GetData(AttName.APP_KEY),
							platform.GetData(AttName.SECRET_KEY), 
							isDebug,     // 调试开关 true 打印log，false 关闭log，正式上线请设置为false
							false);   // 将游戏横竖屏状态传递给sdk， true为竖屏  false为横屏 */
					GameCenterSDK.init(platform.GetData(AttName.SECRET_KEY), appContext);
					
					TypeSDKNotify_oppo notify = new TypeSDKNotify_oppo();
					notify.Init();
					isInit = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}
	
	ApiCallback apiCallback = new ApiCallback() {
		
		@Override
		public void onSuccess(String arg0) {
			// TODO Auto-generated method stub
			try{
				age = Integer.parseInt(arg0);
			} catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void onFailure(String arg0, int arg1) {
			// TODO Auto-generated method stub
			if(arg1 == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_RESUME_GAME){
				age = 0;
			} else if (arg1 == ApiResult.RESULT_CODE_VERIFIED_FAILED_AND_STOP_GAME){
				age = -1;
			}
		}
	};

	private void oppoLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				
				GameCenterSDK.getInstance().doLogin(appContext, new ApiCallback() {
					
					@Override
					public void onSuccess(String content) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login success");
						TypeSDKLogger.e("accessToken:" + content);
						GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {
							
							@Override
							public void onSuccess(String content) {
								// TODO Auto-generated method stub
								try{
									JSONObject json = new JSONObject(content);
									try {										
										String token = URLEncoder.encode(json.getString("token"), "utf-8");
										String ssoid = json.getString("ssoid");
									
										TypeSDKLogger.e("accessToken:" + token);
										TypeSDKLogger.e("ssoid:" + ssoid);
										GameCenterSDK.getInstance().doGetVerifiedInfo(apiCallback);
										TypeSDKNotify_oppo notify = new TypeSDKNotify_oppo();
										TypeSDKLogger.e("age:" + age);
										if(age != -1){											
											notify.sendToken(token, ssoid);
										}
										
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} catch(JSONException e) {
									e.printStackTrace();
								}
							}
							
							@Override
							public void onFailure(String content, int code) {
								// TODO Auto-generated method stub
								
							}
						});
					}
					
					@Override
					public void onFailure(String content, int code) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login_fail:content:" + content + ";code" + code);
					}
				});
			}
		});

	}


	private void oppoLogout() {
		TypeSDKNotify_oppo notify = new TypeSDKNotify_oppo();
		notify.Logout();
	}
	
	public static int getItemCount(String itemName){
		int count = 0;
		String countString = "";
		for (int i = 0; i < itemName.length(); i++) {
			if ('0' <= itemName.charAt(i) && itemName.charAt(i) <= '9') {
				countString += itemName.charAt(i);
			}
		}
		if (!countString.isEmpty()) {
			count = Integer.parseInt(countString);
		}
		TypeSDKLogger.e("getItemCount:" + count);
		return count;
	}
	
	public static String getItemName(String itemName){
		String countString = "";
		for (int i = 0; i < itemName.length(); i++) {
			if ('0' <= itemName.charAt(i) && itemName.charAt(i) <= '9') {
				
			}else {
				countString += itemName.charAt(i);
			}
		}
		TypeSDKLogger.e("getItemName:" + countString);
		return countString;
	}

	private void oppoPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e("ITEM_COUNT:" + _in_pay.GetInt(AttName.ITEM_COUNT));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("PAY_CALL_BACK_URL:" + platform.GetData(AttName.PAY_CALL_BACK_URL));
					
					int price;
					if(isDebug){
						price = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}
					
					final PayInfo payInfo = new PayInfo(_in_pay.GetData(AttName.BILL_NUMBER),
							userInfo.GetData(AttName.USER_ID), price);//
					//payInfo.setProductDesc(_in_pay.GetData(AttName.ITEM_NAME));
					payInfo.setProductDesc("测试商品介绍");
					//payInfo.setProductName(_in_pay.GetData(AttName.ITEM_NAME));
					payInfo.setProductName("测试商品名");
					payInfo.setCallbackUrl(platform.GetData(AttName.PAY_CALL_BACK_URL));
//					payInfo.setGoodsCount(getItemCount(_in_pay.GetData(AttName.ITEM_NAME)) == 0 ? _in_pay.GetInt(AttName.ITEM_COUNT) : 
//						getItemCount(_in_pay.GetData(AttName.ITEM_NAME)));
					//payInfo.setAttach(userInfo.GetData(AttName.USER_ID));
					GameCenterSDK.getInstance().doPay(appContext , payInfo, new ApiCallback() {
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_oppo notify = new TypeSDKNotify_oppo();
						@Override
						public void onFailure(String content, int code) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("return Error:content:" + content + "code:" + code);
							payResult.SetData(AttName.PAY_RESULT,
									"0");
							payResult.SetData(
									AttName.PAY_RESULT_REASON,
									"PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}

						@Override
						public void onSuccess(String arg0) {
							// TODO Auto-generated method stub
							// 支付成功
							TypeSDKLogger.e("pay_success");
							payResult.SetData(AttName.PAY_RESULT,
									"1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
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
	
	public void getAge(){
		String ageStr = "0";
		if(age < 18){
			ageStr = "1";
		} else {
			ageStr = "2";
		}
		TypeSDKData.BaseData msgData = new TypeSDKData.BaseData();
						msgData.SetData("age_code",
								ageStr);
						msgData.SetData(AttName.EXTRA_MSG_TYPE, "age_code");
						TypeSDKEventManager.Instance().SendUintyEvent(
								ReceiveFunction.MSG_EXTRA_FUNCTION, msgData.DataToString());
	}

}
