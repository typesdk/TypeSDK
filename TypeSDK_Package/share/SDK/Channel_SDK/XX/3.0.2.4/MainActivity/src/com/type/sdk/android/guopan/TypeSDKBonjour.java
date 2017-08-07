package com.type.sdk.android.guopan;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPSDKPlayerInfo;
import com.flamingo.sdk.access.GPUploadPlayerInfoResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUploadPlayerInfoObsv;
import com.flamingo.sdk.access.IGPUserObsv;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
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
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_guopan notify = new TypeSDKNotify_guopan();
			notify.Init();
			return;
		}
		this.guopanInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.guopanLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.guopanLogout();
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
		TypeSDKLogger.i("pay begin");
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.guopanPay(_in_pay);
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

//	@Override
//	public void ShowShare(Context _in_context, String _in_data, String WXClassName) {
//		// TODO Auto-generated method stub
//		TypeSDKLogger.e("ShowShare");
//	}

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
			
			String extendInfo = new StringBuilder()
			.append("gameId=").append(platform.GetData(AttName.APP_ID))
			.append("&service=").append(userInfo.GetData(AttName.SERVER_NAME))
			.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
			.append("&grade=").append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);
			
			GPSDKPlayerInfo gpsdkPlayerInfo = new GPSDKPlayerInfo();
			gpsdkPlayerInfo.mGameLevel = userInfo.GetData(AttName.ROLE_LEVEL);
			gpsdkPlayerInfo.mPlayerId = userInfo.GetData(AttName.ROLE_ID);
			gpsdkPlayerInfo.mPlayerNickName = userInfo.GetData(AttName.ROLE_NAME);
			gpsdkPlayerInfo.mServerId = userInfo.GetData(AttName.SERVER_ID);
			gpsdkPlayerInfo.mServerName = userInfo.GetData(AttName.SERVER_NAME);
			GPApiFactory.getGPApi().uploadPlayerInfo(gpsdkPlayerInfo, new IGPUploadPlayerInfoObsv() {
				
				@Override
				public void onUploadFinish(GPUploadPlayerInfoResult gpUploadPlayerInfoResult) {
					// TODO Auto-generated method stub
					if (gpUploadPlayerInfoResult.mResultCode == GPUploadPlayerInfoResult.GPSDKUploadSuccess){
						TypeSDKLogger.e("上报数据回调:成功");
					}else {
						TypeSDKLogger.e("上报数据回调:失败");
					}
					
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
				System.exit(0);
			}
		});			
		}
		
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

	private void guopanInit() {

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
					// 打开日志、发布状态切记不要打开
			        GPApiFactory.getGPApi().setLogOpen(false);
			        
			        GPApiFactory.getGPApi().initSdk(appContext, platform.GetData(AttName.APP_ID), platform.GetData(AttName.APP_KEY), new IGPSDKInitObsv() {
			            @Override
			            public void onInitFinish(GPSDKInitResult initResult) {
			                TypeSDKLogger.e("GPSDKInitResult mInitErrCode: " + initResult.mInitErrCode);
			                TypeSDKLogger.e("initSDK_success");
			                isInit = true;
			                TypeSDKNotify_guopan notify = new TypeSDKNotify_guopan();
			    			notify.Init();
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

	private void guopanLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				GPApiFactory.getGPApi().login(appContext, new IGPUserObsv() {
					
					@Override
					public void onFinish(GPUserResult result) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("LOGIN_SUCCESS");
						TypeSDKLogger.d("login result:" + result.toString());
						TypeSDKLogger.e("getAccountName:" + GPApiFactory.getGPApi().getAccountName());
	                    TypeSDKLogger.e("getLoginUin:" + GPApiFactory.getGPApi().getLoginUin());
		                TypeSDKLogger.d("getLoginToken:" + GPApiFactory.getGPApi().getLoginToken());
		                // 发送登录SDK成功广播通知
		                TypeSDKNotify_guopan notify = new TypeSDKNotify_guopan();
    					notify.sendToken(GPApiFactory.getGPApi().getLoginToken(), GPApiFactory.getGPApi().getLoginUin());
					}
				});
				
			}
		});

	}

	private void guopanLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				GPApiFactory.getGPApi().logout();
				TypeSDKNotify_guopan notify = new TypeSDKNotify_guopan();
				notify.Logout();
			}
		});
		
	}
	
	private void guopanPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("PRODUCT_ID:" + _in_pay.GetData(AttName.PRODUCT_ID));
					TypeSDKLogger.e("ITEM_LOCAL_ID:" + _in_pay.GetData(AttName.ITEM_LOCAL_ID));
					TypeSDKLogger.e("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
					TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
					
					GPSDKGamePayment payParam = new GPSDKGamePayment();
                    payParam.mItemName = _in_pay.GetData(AttName.ITEM_NAME); //订单商品的名称      
                    payParam.mPaymentDes = _in_pay.GetData(AttName.ITEM_DESC);//订单的介绍
                    if(TypeSDKTool.isPayDebug){
                    	payParam.mItemPrice = 0.01f;
    				}else{
    					payParam.mItemPrice = _in_pay.GetInt(AttName.REAL_PRICE)/100;
    				}
//                    payParam.mItemPrice = _in_pay.GetInt(AttName.REAL_PRICE)/100;//订单的价格（以元为单位）
                    payParam.mCurrentActivity = appActivity;//用户当前的activity
                    payParam.mSerialNumber = _in_pay.GetData(AttName.BILL_NUMBER);//订单号，这里用时间代替（用户需填写订单的订单号）
                    //payParam.mItemId = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));//商品编号ID
                    payParam.mItemId = _in_pay.GetData(AttName.ITEM_SERVER_ID);
                    payParam.mReserved = userInfo.GetData(AttName.USER_ID);//透传字段

                    GPApiFactory.getGPApi().buy(payParam, new IGPPayObsv() {
						
						@Override
						public void onPayFinish(GPPayResult result) {
							// TODO Auto-generated method stub
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_guopan notify = new TypeSDKNotify_guopan();
							switch (result.mErrCode) {
		                    case GPPayResult.GPSDKPayResultCodeSucceed:
		                    	// 支付成功
								TypeSDKLogger.e("pay_success");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodePayBackground:
		                    	TypeSDKLogger.e("后台正在轮循购买");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeBackgroundSucceed:
		                    	TypeSDKLogger.e("后台购买成功");
		                    	// 支付成功
								TypeSDKLogger.e("pay_success");
								payResult.SetData(AttName.PAY_RESULT, "1");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeBackgroundTimeOut:
		                    	TypeSDKLogger.e("后台购买超时");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeCancel:
		                    	TypeSDKLogger.e("用户取消");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeNotEnough:
		                    	TypeSDKLogger.e("余额不足");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeOtherError:
		                    	TypeSDKLogger.e("其他错误");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodePayForbidden:
		                    	TypeSDKLogger.e("用户被限制");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodePayHadFinished:
		                    	TypeSDKLogger.e("该订单已经完成");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultCodeServerError:
		                    	TypeSDKLogger.e("服务器错误");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultNotLogined:
		                    	TypeSDKLogger.e("无登陆");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    case GPPayResult.GPSDKPayResultParamWrong:
		                    	TypeSDKLogger.e("参数错误");
		                    	TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
								notify.Pay(payResult.DataToString());
		                        break;
		                    default:
		                    	TypeSDKLogger.e("fail " + result.toString());
		                        break;

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
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
