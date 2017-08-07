package com.type.sdk.android.longxia;

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
import com.mchsdk.open.GPExitResult;
import com.mchsdk.open.GPUserResult;
import com.mchsdk.open.IGPExitObsv;
import com.mchsdk.open.IGPUserObsv;
import com.mchsdk.open.MCApiFactory;
import com.mchsdk.open.OrderInfo;
import com.mchsdk.open.PayCallback;
import com.mchsdk.paysdk.utils.MCLog;
import com.mchsdk.open.IGPSDKInitObsv;
import com.mchsdk.open.GPSDKInitResult;

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
			TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
			notify.Init();
			return;
		}
		this.longxiaSDKInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.longxiaSDKLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.longxiaSDKLogout();
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
		if(exitGameListenser()){
			if (isInit) {
				TypeSDKLogger.e("执行ExitGame方法");
				Handler mainHandler = new Handler(Looper.getMainLooper());
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						MCApiFactory.getMCApi().exit(appContext, new IGPExitObsv() {
					
							@Override
							public void onExitFinish(GPExitResult exitResult) {
								// TODO Auto-generated method stub
								switch (exitResult.mResultCode) {
								case GPExitResult.GPSDKExitResultCodeError:
									TypeSDKLogger.e("exitFail");
									break;
								case GPExitResult.GPSDKExitResultCodeExitGame:
									TypeSDKLogger.i("exit");
									// 注销
									MCApiFactory.getMCApi().sdkLogOff(new IGPExitObsv() {
								
										@Override
										public void onExitFinish(GPExitResult arg0) {
											// TODO Auto-generated method stub
											switch (arg0.mResultCode) {
											case GPExitResult.GPSDKResultCodeOfLogOffSucc:
												TypeSDKLogger.i("LogOffSuccess");
												break;
											case GPExitResult.GPSDKResultCodeOfLogOffFail:
												TypeSDKLogger.e("LogOffFail");
												break;
											}
										}
									});
									// 关闭悬浮窗
									MCApiFactory.getMCApi().stopFloating(appContext);
									// 你自己的退出逻辑，退出程序
									isInit = false;
									System.exit(0);
									break;
								case GPExitResult.GPSDKExitResultCodeCloseWindow:
									TypeSDKLogger.i("exitCancel");
									break;
						
								}

							}
						});
					}
				});
			}
		}
		
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			MCApiFactory.getMCApi().startFloating(appContext);
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			MCApiFactory.getMCApi().stopFloating(appContext);
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

	private void longxiaSDKInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					MCLog.isDebug = true;//TypeSDKTool.isPayDebug;
					TypeSDKLogger.i("PRODUCT_ID:" + platform.GetData(AttName.PRODUCT_ID));
					TypeSDKLogger.i("promoteAccount:" + platform.GetData("promoteAccount"));
					TypeSDKLogger.i("gameId:" + platform.GetData("gameId"));
					TypeSDKLogger.i("APP_NAME:" + platform.GetData(AttName.APP_NAME));
					TypeSDKLogger.i("APP_ID:" + platform.GetData(AttName.APP_ID));
					MCApiFactory.getMCApi().initTestParams(platform.GetData(AttName.PRODUCT_ID), 
							platform.GetData("promoteAccount"), 
							platform.GetData("gameId"), 
							platform.GetData(AttName.APP_NAME), 
							platform.GetData(AttName.APP_ID));//promoteId, promoteAccount, gameId, gameName, gameAppid
					// 2.初始化方法
					MCApiFactory.getMCApi().init(appContext, new IGPSDKInitObsv() {
						
						@Override
						public void onInitFinish(GPSDKInitResult initResult) {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("initcode:" + initResult.mInitErrCode);
							TypeSDKLogger.i("init success code:" + GPSDKInitResult.GPInitErrorCodeNone);
							switch (initResult.mInitErrCode) {
							case GPSDKInitResult.GPInitErrorCodeConfig:// 配置错误
								TypeSDKLogger.e("initFail");
								break;

							case GPSDKInitResult.GPInitErrorCodeNeedUpdate:// SDK需要更新
								TypeSDKLogger.e("init updata");
								break;

							case GPSDKInitResult.GPInitErrorCodeNet:// 网络不可用
								TypeSDKLogger.e("net error");
								break;
							case GPSDKInitResult.GPInitErrorCodeNone:// 初始化成功
								TypeSDKLogger.i("init success");
								isInit = true;
								TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
								notify.Init();
								break;
							default:
								break;
							}
						}
					});
					MCApiFactory.getMCApi().initExitFromPersonInfoParams(new IGPExitObsv() {
						
						@Override
						public void onExitFinish(GPExitResult gpExitResult) {
							// TODO Auto-generated method stub
							switch (gpExitResult.mResultCode) {
							case GPExitResult.GPSDKResultCodeOfPersonInfo:
								TypeSDKLogger.i("退出回调:执行SDK个人中心退出方法");
								// 关闭悬浮窗
								MCApiFactory.getMCApi().stopFloating(appContext);
								// 下面是退出逻辑
								TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
								notify.Logout();
								break;
							default:
								break;
							}
						}
					});
					isInit = true;
					TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
					notify.Init();
					TypeSDKLogger.e("initSDK_end");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void longxiaSDKLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					if(appContext == null){
						TypeSDKLogger.e("appContext is null");
					} else {
						TypeSDKLogger.i("appContext:" + appContext);
					}
					MCApiFactory.getMCApi().startlogin(appContext, new IGPUserObsv() {
					
						@Override
						public void onFinish(GPUserResult result) {
							// TODO Auto-generated method stub
							switch (result.getmErrCode()) {
							case GPUserResult.USER_RESULT_LOGIN_FAIL:
								TypeSDKLogger.e("登录回调:登录失败");
								break;
							case GPUserResult.USER_RESULT_LOGIN_SUCC:
								MCApiFactory.getMCApi().startFloating(appContext);
								String uid = result.getAccountNo();// 登录用户id
								String token = result.getToken();
								result.getAccount();
								TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
								notify.sendToken(token, uid);
								// 将这里返回的用户id和token返回给cp服务器，cp服务器发送给sdk服务器验证登录结果
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

	}

	private void longxiaSDKLogout() {
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				MCApiFactory.getMCApi().sdkLogOff(new IGPExitObsv() {
					
					@Override
					public void onExitFinish(GPExitResult arg0) {
						// TODO Auto-generated method stub
						switch (arg0.mResultCode) {
						case GPExitResult.GPSDKResultCodeOfLogOffSucc:
							TypeSDKLogger.i("LogOffSuccess");
							TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
							notify.Logout();
							break;
						case GPExitResult.GPSDKResultCodeOfLogOffFail:
							TypeSDKLogger.e("LogOffFail");
							break;
						}
					}
				});
				
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
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}
					
					OrderInfo o = new OrderInfo();
					o.setAmount(price);// 物品价格,单位分
					o.setExtendInfo(_in_pay.GetData(AttName.USER_ID) + "|" + _in_pay.GetData(AttName.BILL_NUMBER)); // 用于确认交易给玩家发送商品
					o.setProductDesc(_in_pay.GetData(AttName.ITEM_NAME)); // 物品描述
					o.setProductName(platform.GetData("ProductName"));// 物品名称
					MCApiFactory.getMCApi().pay(appContext, null, o, new PayCallback() {
						
						@Override
						public void callback(String result) {
							// TODO Auto-generated method stub
							TypeSDKNotify_longxiasdk notify = new TypeSDKNotify_longxiasdk();
							PayResultData payResult = new PayResultData();
							if (result.equals("1")) {
								// 支付成功
								TypeSDKLogger.i("paySuccess");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
								return;
							} else {
								TypeSDKLogger.e("payFail:" + result);
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
