package com.type.sdk.android.letv;

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
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.le.accountoauth.utils.LeUserInfo;
import com.le.legamesdk.LeGameSDK;
import com.le.legamesdk.LeGameSDK.ExitCallback;
import com.le.legamesdk.LeGameSDK.LoginCallback;
import com.le.legamesdk.LeGameSDK.PayCallback;
import com.letv.lepaysdk.smart.LePayInfo;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private LeGameSDK leGameSDK;
	private boolean isChangeAccount = false;

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
			TypeSDKNotify_letvsdk notify = new TypeSDKNotify_letvsdk();
			notify.Init();
			return;
		}
		this.letvSDKInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.letvSDKLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.letvSDKLogout();
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
		this.letvSDKPay(_in_pay);

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

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if (exitGameListenser()) {
			if (isInit) {
				leGameSDK.onExit(appActivity, new ExitCallback() {
					
					@Override
					public void onSdkExitConfirmed() {
						// TODO Auto-generated method stub
						isInit = false;
						appActivity.finish();
						System.exit(0);
					}
					
					@Override
					public void onSdkExitCanceled() {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
			
		}

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			leGameSDK.onResume(appActivity);
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			leGameSDK.onPause(appActivity);
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void letvSDKInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					leGameSDK = LeGameSDK.getInstance();
					isInit = true;
					TypeSDKNotify_letvsdk notify = new TypeSDKNotify_letvsdk();
					notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void letvSDKLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				leGameSDK.doLogin(appActivity, new LoginCallback() {
					
					@Override
					public void onLoginSuccess(LeUserInfo userInfo) {
						// TODO Auto-generated method stub
						if (userInfo != null) {
			                // 获取access_token
			                String accessToken = userInfo.getAccessToken();
			                // 获取letv_uid
			                String userId = userInfo.getUserId();
			                TypeSDKNotify_letvsdk notify = new TypeSDKNotify_letvsdk();
			                notify.sendToken(accessToken, userId);
			            } else {
			                TypeSDKLogger.i("LoginCallback:Login Failed");
			            }
					}
					
					@Override
					public void onLoginFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("onLoginFailure:errorCode:" + arg0 + "errorMsg:" + arg1);
					}
					
					@Override
					public void onLoginCancel() {
						// TODO Auto-generated method stub
						TypeSDKLogger.w("onLoginCancel:");						
					}
				}, isChangeAccount);
			}
		});

	}

	private void letvSDKLogout() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				isChangeAccount = true;
				TypeSDKNotify_letvsdk notify = new TypeSDKNotify_letvsdk();
				notify.Logout();
			}
		});

	}

	private void letvSDKPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					String price = "0";
					if (TypeSDKTool.isPayDebug) {
						price = "1";
					} else {
						price = ""
								+ (_in_pay.GetInt(AttName.REAL_PRICE) * 0.01f);
					}
					TypeSDKLogger.i("==doPay is called==");
					LePayInfo payInfo = new LePayInfo();
					payInfo.setLetv_user_access_token(_in_pay
							.GetData(AttName.USER_TOKEN));
					payInfo.setLetv_user_id(_in_pay.GetData(AttName.USER_ID));// 乐视集团用户id
					payInfo.setNotify_url(platform
							.GetData(AttName.PAY_CALL_BACK_URL));// 支付结果回调地址
					payInfo.setCooperator_order_no(_in_pay
							.GetData(AttName.BILL_NUMBER));// CP自定义订单号
					payInfo.setPrice(price);// 产品价格
					payInfo.setProduct_name(_in_pay.GetData(AttName.ITEM_NAME));// 商品名称
					payInfo.setProduct_desc(_in_pay.GetData(AttName.ITEM_DESC));// 商品描述

					payInfo.setPay_expire("21600");// 支付结束期限
					payInfo.setProduct_id(_in_pay
							.GetData(AttName.ITEM_SERVER_ID));// 商品id
					payInfo.setCurrency("RMB");// 货币种类
					// payInfo.setProduct_urls("file://android_asset/res/image/common/gold_icon.png");//
					// 商品图片
					payInfo.setExtro_info(_in_pay.GetData(AttName.BILL_NUMBER));// cp自定义参数

					leGameSDK.doPay(appActivity, payInfo, new PayCallback() {

						@Override
						public void onPayResult(String status,
								String errorMessage) {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("==onPayResult is called==");

							/*
							 * Toast.makeText(getApplicationContext(), "Status:"
							 * + status + ", message" + errorMessage,
							 * Toast.LENGTH_LONG) .show();
							 */
							TypeSDKLogger.i("pay_" + status + ":" + errorMessage);
							PayResultData payResult = new PayResultData();
							
							if (status.equals("SUCCESS")) {
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
							} else if (status.equals("FAILT")) {
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							} else if (status.equals("PAYED")) {
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAYED");
							} else if (status.equals("WAITTING")) {
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "WAITTING");
							} else if (status.equals("NONETWORK")) {
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "NONETWORK");
							} else if (status.equals("NONE")) {
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "NONE");
							} else if (status.equals("CANCEL")) {
								payResult.SetData(AttName.PAY_RESULT, "2");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
							} else if (status.equals("COINLOCKUSER")) {
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "COINLOCKUSER");
							} else {
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							}
							TypeSDKNotify_letvsdk notify = new TypeSDKNotify_letvsdk();
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
