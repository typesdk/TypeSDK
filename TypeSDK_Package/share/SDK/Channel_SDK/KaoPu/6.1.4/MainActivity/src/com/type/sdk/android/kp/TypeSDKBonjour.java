package com.type.sdk.android.kp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.cyjh.pay.util.MD5Util;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.kaopu.supersdk.api.KPSuperConstants;
import com.kaopu.supersdk.api.KPSuperSDK;
import com.kaopu.supersdk.callback.KPAuthCallBack;
import com.kaopu.supersdk.callback.KPGetCheckUrlCallBack;
import com.kaopu.supersdk.callback.KPLoginCallBack;
import com.kaopu.supersdk.callback.KPLogoutCallBack;
import com.kaopu.supersdk.callback.KPPayCallBack;
import com.kaopu.supersdk.model.UserInfo;
import com.kaopu.supersdk.model.params.PayParams;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private String mDevicetype;
	private String mImei;
	private String mOpenid;
	private String mToken;
	private int r;
	private boolean bl = true;
	private boolean isLogout = true;

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
			TypeSDKNotify_KP notify = new TypeSDKNotify_KP();
			notify.Init();
			return;
		}
		this.KPInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin:");
		super.ShowLogin(_in_context, _in_data);
		if (KPSuperSDK.isLogin()) {
			TypeSDKLogger.e("isLogin:" + KPSuperSDK.isLogin());
			KPSuperSDK.logoutAccount();
			isLogout = false;
		}
		if (bl) {
			if(isInit){
				this.KPLogin();
			}			
		} else {
			bl = true;
		}

	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		KPSuperSDK.logoutAccount();
		isLogout = false;
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
		this.KPPay(_in_pay);
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

			String extendInfo = new StringBuilder().append("gameId=")
					.append(platform.GetData(AttName.APP_ID))
					.append("&service=")
					.append(userInfo.GetData(AttName.SERVER_NAME))
					.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
					.append("&grade=")
					.append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);

			int level = Integer.parseInt(getRoleLevel(userInfo
					.GetData(AttName.ROLE_LEVEL)));

			KPSuperSDK.setUserGameRole(appContext,
					userInfo.GetData(AttName.SERVER_NAME),
					userInfo.GetData(AttName.ROLE_NAME),
					userInfo.GetData(AttName.ROLE_ID), level);

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	public static String getRoleLevel(String RoleLevel) {
		String countString = "";
		for (int i = 0; i < RoleLevel.length(); i++) {
			if ('0' <= RoleLevel.charAt(i) && RoleLevel.charAt(i) <= '9') {
				countString += RoleLevel.charAt(i);
			} else {

			}
		}
		TypeSDKLogger.e("getItemName:" + countString);
		return countString;
	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.e("执行ExitGame方法");
			KPSuperSDK.release();
			System.exit(0);
		}

	}

	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
		KPSuperSDK.onNewIntent(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		TypeSDKLogger.e("onActivityResult");
		KPSuperSDK.onActivityResult(requestCode, resultCode, data);
	}

	public void onCreate(Context _in_context) {
		TypeSDKLogger.e("onCreate");
		KPSuperSDK.onCreate(appActivity);
	}

	public void onStart(Context _in_context) {
		TypeSDKLogger.e("onStart");
		KPSuperSDK.onStart(appActivity);
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		KPSuperSDK.onResume(appActivity);
	}

	public void onRestart(Context _in_context) {
		TypeSDKLogger.e("onRestart");
		KPSuperSDK.onRestart(appActivity);
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		KPSuperSDK.onPause(appActivity);

	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		KPSuperSDK.onStop(appActivity);
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		KPSuperSDK.onDestroy(appActivity);

	}

	/**
	 * KP5.2 init xnz 2015.12.16
	 */
	private void KPInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// 初始化监�?
				KPSuperSDK.auth(appActivity, null, new KPAuthCallBack() {

					@Override
					public void onAuthSuccess() {
						TypeSDKLogger.e("KPInit_SUCCESS");
						// 此接口请于授权成功之后，登陆之前调用,用于当用户点�? SDK 切换账号按钮 �? 调用注销接口时回�?
						KPSuperSDK
								.registerLogoutCallBack(new KPLogoutCallBack() {

									@Override
									public void onSwitch() {
										TypeSDKLogger.e("onSwitch");
										bl = false;
										TypeSDKNotify_KP notify = new TypeSDKNotify_KP();
										notify.Logout();
									}

									@Override
									public void onLogout() {
										TypeSDKLogger.e("onLogout");
										TypeSDKNotify_KP notify = new TypeSDKNotify_KP();
										notify.Logout();
									}
								});
						isInit = true;
						TypeSDKNotify_KP notify = new TypeSDKNotify_KP();
						notify.Init();
					}

					@Override
					public void onAuthFailed() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("KPInit_onAuthFailed");
					}
				});
			}
		});

		TypeSDKLogger.e("init done");

	}

	/**
	 * KP5.2 Login xnz 2015.12.16
	 */
	private void KPLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// 此接口设置获取登录验证地�? �? 回调对象
				KPSuperSDK.setGetCheckUrlCallBack(new KPGetCheckUrlCallBack() {
					@Override
					public void onGetCheckUrlSuccess(String url) {
						TypeSDKLogger.d("onGetCheckUrlSuccess");
						TypeSDKLogger.e("URL:" + url);
						TypeSDKLogger.d("Openid2:" + mOpenid);
						TypeSDKNotify_KP notify = new TypeSDKNotify_KP();
						notify.sendToken(mToken, mOpenid + "|" + mImei + "|"
								+ mDevicetype + "|" + r + "|" + url);
						isLogout = true;
					}

					@Override
					public void onGetCheckUrlFailed() {
						TypeSDKLogger.e("onGetCheckUrlFailed");
					}
				});

				if(isLogout){
					KPSuperSDK.loginDefault(appActivity, kpLoginCallBack, "");
				} else {
					KPSuperSDK.login(appActivity, kpLoginCallBack, "");
				}
				
			}
		});
	}
	
	private KPLoginCallBack kpLoginCallBack = new KPLoginCallBack() {

					@Override
					public void onLoginSuccess(UserInfo userInfo) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("KPLogin_SUCCESS");
						TypeSDKLogger.e("username:" + userInfo.getUsername());
						TypeSDKLogger.e("userid:" + userInfo.getUserid());
						TypeSDKLogger.e("Openid1:" + userInfo.getOpenid());
						TypeSDKLogger.e("iconurl:" + userInfo.getIconurl());
						TypeSDKLogger.e("description  :"
								+ userInfo.getDescription());
						TypeSDKLogger.e("Token:" + userInfo.getToken());
						TypeSDKLogger.e("Tag:" + userInfo.getTag());
						TypeSDKLogger.e("Tagid:" + userInfo.getTagid());
						TypeSDKLogger.e("Appid:" + userInfo.getAppid());
						TypeSDKLogger.e("ChannelKey:"
								+ userInfo.getChannelKey());
						TypeSDKLogger.e("R:" + userInfo.getR());
						TypeSDKLogger.e("Devicetype:"
								+ userInfo.getDevicetype());
						TypeSDKLogger.e("Imei:" + userInfo.getImei());
						TypeSDKLogger.e("Sign:" + userInfo.getSign());
						TypeSDKLogger.e("Version:" + userInfo.getVersion());

						mOpenid = userInfo.getOpenid();
						mToken = userInfo.getToken();
						mImei = userInfo.getImei();
						mDevicetype = userInfo.getDevicetype();
						r = userInfo.getR();
						// 此接口用于获取登录验证地�?
						KPSuperSDK.getCheckUrl();

						String str = userInfo.getAppid()
								+ userInfo.getChannelKey()
								+ userInfo.getDevicetype() + userInfo.getImei()
								+ userInfo.getOpenid() + userInfo.getR()
								+ userInfo.getTag() + userInfo.getTagid()
								+ userInfo.getToken();

						String mSign = MD5Util.MD5(str
								+ KPSuperConstants.oauthkeys[userInfo.getR()]);
						TypeSDKLogger.e("mSign:" + mSign);
						TypeSDKLogger.e("str + oauthkeys:" + str
								+ KPSuperConstants.oauthkeys[userInfo.getR()]);
					}

					@Override
					public void onLoginFailed() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("KPLogin_Failed");
					}

					@Override
					public void onLoginCanceled() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("KPLogin_Canceled");
					}
				};

	/**
	 * KP5.2 Pay xnz 2015.12.16
	 * 
	 * @param _in_pay
	 */
	private void KPPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				double price;
				if (TypeSDKTool.isPayDebug) {
					price = 0.1;
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
				}

				TypeSDKLogger.e("setAmount:" + price);
				TypeSDKLogger.e("setGamename:"
						+ platform.GetData(AttName.APP_NAME));
				TypeSDKLogger.e("setGameserver:"
						+ _in_pay.GetData(AttName.SERVER_NAME));
				TypeSDKLogger.e("setRolename:"
						+ _in_pay.GetData(AttName.ROLE_NAME));
				TypeSDKLogger.e("setOrderid:"
						+ _in_pay.GetData(AttName.BILL_NUMBER) + "|"
						+ _in_pay.GetData(AttName.USER_ID));
				TypeSDKLogger.e("setCustomPrice:true");
				TypeSDKLogger.e("setCustomText:"
						+ _in_pay.GetData(AttName.ITEM_NAME));

				PayParams payParams = new PayParams();
				payParams.setAmount(price);
				payParams.setGamename(platform.GetData(AttName.APP_NAME));
				payParams.setGameserver(_in_pay.GetData(AttName.SERVER_NAME));
				payParams.setRolename(_in_pay.GetData(AttName.ROLE_NAME));
				payParams.setOrderid(_in_pay.GetData(AttName.BILL_NUMBER) + "|"
						+ _in_pay.GetData(AttName.USER_ID));
				payParams.setCurrencyname(_in_pay.GetData(AttName.ITEM_NAME
						.replaceAll("\\d+", "")));
				payParams.setProportion(10);
				payParams.setCustomPrice(true);
				payParams.setCustomText(_in_pay.GetData(AttName.ITEM_NAME));

				KPSuperSDK.pay(appActivity, payParams, "", new KPPayCallBack() {
					PayResultData payResult = new PayResultData();
					TypeSDKNotify_KP notify = new TypeSDKNotify_KP();

					@Override
					public void onPaySuccess() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("KPPay_SUCCESS");
						payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"SUCCESS");
						notify.Pay(payResult.DataToString());
					}

					@Override
					public void onPayFailed() {
						// TODO Auto-generated method stub
						payResult.SetData(AttName.PAY_RESULT, "0");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"Failed");
						notify.Pay(payResult.DataToString());
					}

					@Override
					public void onPayCancel() {
						// TODO Auto-generated method stub
						payResult.SetData(AttName.PAY_RESULT, "2");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"Cancle");
						notify.Pay(payResult.DataToString());
					}
				});

			}
		});
	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

}
