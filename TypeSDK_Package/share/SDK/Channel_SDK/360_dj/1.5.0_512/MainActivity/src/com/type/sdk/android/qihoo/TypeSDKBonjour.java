package com.type.sdk.android.qihoo;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.CPCallBackMgr.MatrixCallBack;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.unity3d.player.UnityPlayer;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private int i = 0;
	public boolean isLoginFinished = false;
	public boolean isInitFinished = false;
	private boolean mIsInOffline = false;
	private boolean isLandScape = true;
	private boolean isShowClose = false; // 是否显示关闭按钮
	private boolean isSupportOffline = true; // 可选参数，是否支持离线模式，默认值为false
	private boolean isShowSwitchButton = true; // 可选参数，是否在自动登录的过程中显示切换账号按钮
	private boolean isHideWellcome = false; // 可选参数，是否隐藏欢迎界面
	private boolean isShowDlgOnFailedAutoLogin = true; // 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
	private boolean isAutoLoginHideUI = false; // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
	private boolean isDebugSocialShare = true; // 测试参数，发布时要去掉
	protected QihooUserInfo mQihooUserInfo;
	protected String mAccessToken = null;
//	private ProgressDialog mProgress;
	// AccessToken是否有效
	protected static boolean isAccessTokenValid = true;
	// QT是否有效
	protected static boolean isQTValid = true;
	
	private static class SingletonHandler 
	{
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance()
	{
		return SingletonHandler.instance;
	}


	@Override
	public void initSDK(Context _in_context, String _in_data) {
		if (isInit) {
			TypeSDKLogger.i( "error init do again");
			TypeSDKNotify_Qihoo notify = new TypeSDKNotify_Qihoo();
			notify.Init();
			return;
		}

		TypeSDKLogger.d( "initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		isInitFinished = true;
		qihooSdkInit();
	}

	public void onResume() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "onResume");
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "onPause");
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "destory");
		Matrix.destroy(UnityPlayer.currentActivity);
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.d( "ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.qihooSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.d( "ShowLogout");
		this.qihooSdkLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
	}

	@Override
	public void ShowToolBar(Context _in_context) {

	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.qihooSdkPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);

		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
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
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		TypeSDKLogger.d( "SetPlayerInfo");
		try {

			userInfo.StringToData(_in_data);
			TypeSDKLogger.d( "_in_data:" + _in_data);
			TypeSDKLogger.d( "userInfo:" + userInfo.DataToString());
			// JSONObject userJsonExData = new JSONObject();

			// userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			// userJsonExData.put("roleName",
			// userData.GetData(AttName.ROLE_NAME));
			// userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
			// userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			// userJsonExData.put("zoneName",userData.GetData(AttName.SERVER_NAME));
			//
			// TypeSDKLogger.e("上传用户信息:string="+userJsonExData);
		} catch (Exception e) {
			TypeSDKLogger.e( "上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.d( "执行ExitGame方法");
		if(exitGameListenser()){
			this.qihooSdkExit();			
		}
		
	}

	protected boolean getLandscape(Context context) {
    	if (context == null) {
    		return false;
    	}
    	boolean landscape = (context.getResources().getConfiguration().orientation
    			== Configuration.ORIENTATION_LANDSCAPE);
    	return landscape;
    }

	private void qihooSdkInit() {

		final MatrixCallBack mSDKCallback = new MatrixCallBack() {

			@Override
			public void execute(Context context, int functionCode, String functionParams) {
				// TODO Auto-generated method stub
				if (functionCode == ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT) {
					doSdkSwitchAccount(getLandscape(context));
				}else if (functionCode == ProtocolConfigs.FUNC_CODE_INITSUCCESS) {
					TypeSDKNotify_Qihoo notify = new TypeSDKNotify_Qihoo();
					notify.Init();
					
					TypeSDKLogger.d( "initSDK success");
					isInit = true;
				}
			}
			
		};
		
//		final Activity runActivi;
//		if (UnityPlayer.currentActivity != null)
//			runActivi = UnityPlayer.currentActivity;
//		else
//			runActivi = appActivity;

		//TypeSDKLogger.e( "initSDK_begin" + appActivity.getResources().getString(R.string.app_name));
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.d( "initSDK start");
					// 调用其他SDK接口之前必须先调用init
					Matrix.init(appActivity,mSDKCallback);
				} catch (Exception e) {
					e.printStackTrace();
					TypeSDKLogger.d( "initSDK Exception");
				}
			}
		});
		TypeSDKLogger.d( "initSDK_end");
	}

	private void qihooSdkLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.d( "i:" + ++i);
					doSdkLogin(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void qihooSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				doSdkLogout(mQihooUserInfo);
				TypeSDKLogger.d( "sdk_logout");
			}
		});

	}

	private void qihooSdkExit() {
		TypeSDKLogger.d( "exit_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					doSdkQuit(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void qihooSdkPay (final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.d( "receive pay data: " + _in_pay.DataToString());

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				if (!checkLoginInfo(mQihooUserInfo)) {
					return;
				}

				if (!isAccessTokenValid) {
					Toast.makeText(appActivity, "AccessToken已失效，请重新登录",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!isQTValid) {
					Toast.makeText(appActivity, "QT已失效，请重新登录",
							Toast.LENGTH_SHORT).show();
					return;
				}

				TypeSDKLogger.d(
						"_in_pay_USER_ID:" + _in_pay.GetData(AttName.USER_ID));
				
				String price;
				if(TypeSDKTool.isPayDebug){
					price = "100";
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE)+"";
				}

				// 支付基础参数  
				QihooPayInfo payInfo = getQihooPay(
						price,//_in_pay.GetData(AttName.REAL_PRICE)
						_in_pay.GetData(AttName.ITEM_NAME).isEmpty()?"商品":_in_pay.GetData(AttName.ITEM_NAME),
						userInfo.GetData(AttName.ROLE_NAME).isEmpty() ? "玩家" : userInfo.GetData(AttName.ROLE_NAME),
						userInfo.GetData(AttName.ROLE_ID).isEmpty()?"1":userInfo.GetData(AttName.ROLE_ID),
						_in_pay.GetData(AttName.ITEM_SERVER_ID).isEmpty()?"1":_in_pay.GetData(AttName.ITEM_SERVER_ID),
						_in_pay.GetData(AttName.BILL_NUMBER),
						platform.GetData(AttName.PAY_CALL_BACK_URL),
						userInfo.GetData(AttName.USER_ID));
				Intent intent = getPayIntent(isLandScape, payInfo);

				// 必需参数，使用360SDK的支付模块。
				intent.putExtra(ProtocolKeys.FUNCTION_CODE,
						ProtocolConfigs.FUNC_CODE_PAY);

				// 启动接口
				final Activity runActivi;
//				if (UnityPlayer.currentActivity != null)
				runActivi = UnityPlayer.currentActivity;
//				else
//					runActivi = appActivity;
				// 启动接口
				Matrix.invokeActivity(runActivi, intent, mPayCallback);

//				TypeSDKLogger.e( "pay_start" + ";price:" + _in_pay.GetData(AttName.REAL_PRICE));
//				TypeSDKLogger.e( "USER_ID:" + userInfo.GetData(AttName.USER_ID));
//				TypeSDKLogger.e( "USER_NAME:" + userInfo.GetData(AttName.USER_NAME));
//				TypeSDKLogger.e( "ROLE_NAME_in_pay:" + _in_pay.GetData(AttName.ROLE_NAME));
//				TypeSDKLogger.e( "ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
//				TypeSDKLogger.e( "ROLE_NAME:" + (userInfo.GetData(AttName.ROLE_NAME).isEmpty() ? "玩家" : userInfo.GetData(AttName.ROLE_NAME)));
//				TypeSDKLogger.e( "PAY_CALL_BACK_URL:"+ platform.GetData(AttName.PAY_CALL_BACK_URL));

			}
		});

	}

	/**
	 * 使用360SDK的登录接口
	 *
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 */
	protected void doSdkLogin(boolean isLandScape) {
		mIsInOffline = false;
		Intent intent = getLoginIntent(isLandScape);
		IDispatcherCallback callback = mLoginCallback;
		if (isSupportOffline) {
			callback = mLoginCallbackSupportOffline;
		}
		Matrix.execute(appActivity, intent, callback);
	}

	/**
	 * 生成调用360SDK登录接口的Intent
	 * 
	 * @param isLandScape
	 *            是否横屏
	 * @return intent
	 */
	private Intent getLoginIntent(boolean isLandScape) {

		Intent intent = new Intent(appActivity, ContainerActivity.class);

		// 界面相关参数，360SDK界面是否以横屏显示。
		intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 必需参数，使用360SDK的登录模块。
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_LOGIN);

		// 是否显示关闭按钮
		intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, isShowClose);

		// 可选参数，是否支持离线模式，默认值为false
		intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, isSupportOffline);

		// 可选参数，是否在自动登录的过程中显示切换账号按钮
		intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH,
				isShowSwitchButton);

		// 可选参数，是否隐藏欢迎界面
		intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, isHideWellcome);

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE,
		// getUiBackgroundPicPath());
		// 可选参数，指定assets中的图片路径，作为背景图
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS,
		// getUiBackgroundPathInAssets());

		// -- 以下参数仅仅针对自动登录过程的控制
		// 可选参数，自动登录过程中是否不展示任何UI，默认展示。
		intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, isAutoLoginHideUI);

		// 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
		intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN,
				isShowDlgOnFailedAutoLogin);
		// 测试参数，发布时要去掉
		intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, isDebugSocialShare);

		return intent;
	}

	// 登录、注册的回调
	private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			// press back
			if (isCancelLogin(data)) {
//				doSdkLogin(true);
				return;
			}

			// 显示一下登录结果
			TypeSDKLogger.d( "login callback data:" + data);
			mIsInOffline = false;
			mQihooUserInfo = null;
			// TypeSDKLogger.d( "mLoginCallback, data is " + data);
			// 解析User info，这里获取到的user info 没有qid，需要单独从服务器获取
			QihooUserInfo info = parseUserInfoFromLoginResult(data);
			// 解析access_token
			mAccessToken = parseAccessTokenFromLoginResult(data);
			TypeSDKNotify_Qihoo notify = new TypeSDKNotify_Qihoo();
			notify.sendToken(mAccessToken);
			// 保存QihooUserInfo
			mQihooUserInfo = info;
			//TypeSDKLogger.d( "token:" + mAccessToken);
			//TypeSDKLogger.e( "userName:" + info.getName());.
			//TypeSDKLogger.e( data);
			
		}
	};

	private IDispatcherCallback mLoginCallbackSupportOffline = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			if (isCancelLogin(data)) {
				return;
			}

			TypeSDKLogger.d( "mLoginCallbackSupportOffline, data is " + data);
			try {
				JSONObject joRes = new JSONObject(data);
				JSONObject joData = joRes.getJSONObject("data");
				String mode = joData.optString("mode", "");
				if (!TextUtils.isEmpty(mode) && mode.equals("offline")) {
					// Toast.makeText(appActivity,
					// "login success in offline mode",
					// Toast.LENGTH_SHORT).show();
					mIsInOffline = true;
					mQihooUserInfo = parseUserInfoFromLoginResult(data);
					// 显示一下登录结果
					// Toast.makeText(appActivity, data,
					// Toast.LENGTH_LONG).show();
					//TypeSDKLogger.e( "token:" + mAccessToken);
					// 登录结果直接返回的userinfo中没有qid，需要去应用的服务器获取用access_token获取一下带qid的用户信息
					//getUserInfo(mQihooUserInfo);
				} else {
					mLoginCallback.onFinished(data);
				}
			} catch (Exception e) {
				TypeSDKLogger.e( "mLoginCallbackSupportOffline exception", e);
			}

		}
	};

	// ------------------注销登录----------------
	protected void doSdkLogout(QihooUserInfo usrInfo) {
		if (!checkLoginInfo(usrInfo)) {
			return;
		}
		Intent intent = getLogoutIntent();
		Matrix.execute(appActivity, intent, new IDispatcherCallback() {
			@Override
			public void onFinished(String data) {
				mQihooUserInfo = null;
				TypeSDKLogger.d( "logout_success");
				TypeSDKNotify_Qihoo notify = new TypeSDKNotify_Qihoo();
				notify.Logout();
				// Toast.makeText(appActivity, data, Toast.LENGTH_SHORT).show();
				// System.out.println(data);
			}
		});
	}

	private Intent getLogoutIntent() {
		/*
		 * 必须参数： function_code : 必须参数，表示调用SDK接口执行的功能
		 */
		Intent intent = new Intent();
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_LOGOUT);
		return intent;
	}

	private boolean checkLoginInfo(QihooUserInfo info) {
		if (mIsInOffline) {
			return true;
		}
		// if(null == info || !info.isValid()){
		// Toast.makeText(appActivity, "需要登录才能执行此操作",
		// Toast.LENGTH_SHORT).show();
		// return false;
		// }
		return true;
	}

	private boolean isCancelLogin(String data) {
		try {
			JSONObject joData = new JSONObject(data);
			int errno = joData.optInt("errno", -1);
			if (-1 == errno) {
				// Toast.makeText(appActivity, data, Toast.LENGTH_LONG).show();
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	private QihooUserInfo parseUserInfoFromLoginResult(String loginRes) {
		try {
			JSONObject joRes = new JSONObject(loginRes);
			JSONObject joData = joRes.getJSONObject("data");
			JSONObject joUserLogin = joData.getJSONObject("user_login_res");
			JSONObject joUserLoginData = joUserLogin.getJSONObject("data");
			JSONObject joAccessInfo = joUserLoginData
					.getJSONObject("accessinfo");
			JSONObject joUserMe = joAccessInfo.getJSONObject("user_me");
			return QihooUserInfo.parseUserInfo(joUserMe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String parseAccessTokenFromLoginResult(String loginRes) {
		try {

			JSONObject joRes = new JSONObject(loginRes);
			JSONObject joData = joRes.getJSONObject("data");
			return joData.getString("access_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void onGotUserInfo2(QihooUserInfo userInfo) {

//		ProgressUtil.dismiss(mProgress);

		if (userInfo != null && userInfo.isValid()) {
			// 保存QihooUserInfo
			mQihooUserInfo = userInfo;
			TypeSDKLogger.d( "get_user_success");
			// SharkSDKNotify_Qihoo notify = new SharkSDKNotify_Qihoo();
			// notify.sendToken(mAccessToken, mQihooUserInfo.getId(),
			// mQihooUserInfo.getName());
		} else {
			// Toast.makeText(appActivity, R.string.get_user_fail,
			// Toast.LENGTH_SHORT).show();
			clearLoginResult();
		}
	}

	private void clearLoginResult() {
		mQihooUserInfo = null;
	}

	/*
	 * protected void doSdkPay(QihooUserInfo usrinfo, boolean isLandScape) {
	 * 
	 * if (!checkLoginInfo(usrinfo)) { return; }
	 * 
	 * if(!isAccessTokenValid) { Toast.makeText(appActivity,
	 * R.string.access_token_invalid, Toast.LENGTH_SHORT).show(); return; }
	 * if(!isQTValid) { Toast.makeText(appActivity, R.string.qt_invalid,
	 * Toast.LENGTH_SHORT).show(); return; }
	 * 
	 * // 支付基础参数 QihooPayInfo payInfo = getQihooPayInfo(true); Intent intent =
	 * getPayIntent(isLandScape, payInfo);
	 * 
	 * // 必需参数，使用360SDK的支付模块。 intent.putExtra(ProtocolKeys.FUNCTION_CODE,
	 * ProtocolConfigs.FUNC_CODE_PAY);
	 * 
	 * // 启动接口 Matrix.invokeActivity(appActivity, intent, mPayCallback); }
	 */

	// 支付的回调
	protected IDispatcherCallback mPayCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			TypeSDKLogger.d( "mPayCallback, data is " + data);
			if (TextUtils.isEmpty(data)) {
				return;
			}

			boolean isCallbackParseOk = false;
			JSONObject jsonRes;
			try {
				jsonRes = new JSONObject(data);
				// error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中,
				// 4010201和4009911 登录状态已失效，引导用户重新登录
				// error_msg 状态描述
				int errorCode = jsonRes.optInt("error_code");
				isCallbackParseOk = true;
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				switch (errorCode) {
				case 0:
					TypeSDKLogger.d( "支付结果回调成功");
					payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
					payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
							"SUCCESS");
					TypeSDKEventManager.Instance().SendUintyEvent(
							ReceiveFunction.MSG_PAYRESULT,
							payResult.DataToString());
					break;
				case 1:
					TypeSDKLogger.d( "支付失败");
					payResult.SetData(AttName.PAY_RESULT, "0");
					payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
					TypeSDKEventManager.Instance().SendUintyEvent(
							ReceiveFunction.MSG_PAYRESULT,
							payResult.DataToString());
					break;
				case -1:
					TypeSDKLogger.d( "支付取消");
					payResult.SetData(AttName.PAY_RESULT, "2");
					payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
					TypeSDKEventManager.Instance().SendUintyEvent(
							ReceiveFunction.MSG_PAYRESULT,
							payResult.DataToString());
					break;
				case -2:
					isAccessTokenValid = true;
					isQTValid = true;
					// String errorMsg = jsonRes.optString("error_msg");
					// String text =
					// appActivity.getString(R.string.pay_callback_toast,
					// errorCode, errorMsg);
					// Toast.makeText(appActivity, text,
					// Toast.LENGTH_SHORT).show();
					break;
				case 4010201:
					// acess_token失效
					isAccessTokenValid = false;
					// Toast.makeText(appActivity,
					// R.string.access_token_invalid,
					// Toast.LENGTH_SHORT).show();
					break;
				case 4009911:
					// QT失效
					isQTValid = false;
					// Toast.makeText(appActivity, R.string.qt_invalid,
					// Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// 用于测试数据格式是否异常。
			if (!isCallbackParseOk) {
				// Toast.makeText(appActivity,
				// appActivity.getResources().getString(R.string.data_format_error),
				// Toast.LENGTH_LONG).show();
			}
		}
	};

	/*
	 * protected QihooPayInfo getQihooPayInfo(boolean isFixed) { QihooPayInfo
	 * payInfo = null; if(isFixed) { payInfo =
	 * getQihooPay(Constants.DEMO_FIXED_PAY_MONEY_AMOUNT); //定额支付 } else {
	 * payInfo = getQihooPay(Constants.DEMO_NOT_FIXED_PAY_MONEY_AMOUNT); }
	 * return payInfo; }
	 */

	/***
	 * 生成调用360SDK支付接口的Intent
	 *
	 * @param isLandScape
	 * @param pay
	 * @return Intent
	 */
	protected Intent getPayIntent(boolean isLandScape, QihooPayInfo pay) {
		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// *** 以下非界面相关参数 ***

		// 设置QihooPay中的参数。

		// 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
		bundle.putString(ProtocolKeys.ACCESS_TOKEN, pay.getAccessToken());

		// 必需参数，360账号id，整数。
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

		// 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
		bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

		// 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
		bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

		// 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
		bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

		// 必需参数，购买商品的商品id，应用指定，最大16字符。
		bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

		// 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
		bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

		// 必需参数，游戏或应用名称，最大16中文字。
		bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

		// 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
		// 充到统一的用户账户，各区服角色均可使用）。
		bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

		// 必需参数，应用内的用户id。
		// 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
		bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

		// 可选参数，应用扩展信息1，原样返回，最大255字符。
		bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

		// 可选参数，应用扩展信息2，原样返回，最大255字符。
		bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

		// 可选参数，应用订单号，应用内必须唯一，最大32字符。
		bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

		// 必需参数，使用360SDK的支付模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

		Intent intent = new Intent(appActivity, ContainerActivity.class);
		intent.putExtras(bundle);

		return intent;
	}

	// payment begin
	/***
	 * @param moneyAmount
	 *            金额数，使用者可以自由设定数额。金额数为100的整数倍，360SDK运行定额支付流程；
	 *            金额数为0，360SDK运行不定额支付流程。
	 * @return QihooPay
	 */
	private QihooPayInfo getQihooPay(String moneyAmount, String itemName,
			String userName, String roleId, String productId,
			String billNumber, String pauBackUrl, String userId) {

		// String qihooUserId = (mQihooUserInfo != null) ?
		// mQihooUserInfo.getId() : null;

		// 创建QihooPay
		QihooPayInfo qihooPay = new QihooPayInfo();
		qihooPay.setQihooUserId(userId);
		qihooPay.setMoneyAmount(moneyAmount);
		qihooPay.setAccessToken(mAccessToken);
		qihooPay.setExchangeRate("1");

		qihooPay.setProductName(itemName);
		qihooPay.setProductId(productId);

		qihooPay.setNotifyUri(pauBackUrl);

		qihooPay.setAppName(platform.GetData(AttName.CP_ID));
		qihooPay.setAppUserName(userName);
		qihooPay.setAppUserId(roleId);

		// 可选参数
		qihooPay.setAppExt1("ext1");
		qihooPay.setAppExt2("ext2");
		qihooPay.setAppOrderId(billNumber);

		return qihooPay;
	}

	/**
	 * 使用360SDK的切换账号接口
	 *
	 * @param isLandScape
	 *            是否横屏显示登录界面
	 */
	protected void doSdkSwitchAccount(boolean isLandScape) {
		Intent intent = getSwitchAccountIntent(isLandScape);
		IDispatcherCallback callback = mAccountSwitchCallback;
		if (isSupportOffline) {
			callback = mAccountSwitchSupportOfflineCB;
		}
		Matrix.invokeActivity(appActivity, intent, callback);
	}

	/***
	 * 生成调用360SDK切换账号接口的Intent
	 *
	 * @param isLandScape
	 *            是否横屏
	 * @param isBgTransparent
	 *            是否背景透明
	 * @param clientId
	 *            即AppKey
	 * @return Intent
	 */
	private Intent getSwitchAccountIntent(boolean isLandScape) {
		Intent intent = new Intent(appActivity, ContainerActivity.class);

		// 界面相关参数，360SDK界面是否以横屏显示。
		intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 必需参数，使用360SDK的切换账号模块。
		intent.putExtra(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

		// 是否显示关闭按钮
		intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, isShowClose);

		// 可选参数，是否支持离线模式，默认值为false
		intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, isSupportOffline);

		// 可选参数，是否在自动登录的过程中显示切换账号按钮
		intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH,
				isShowSwitchButton);

		// 可选参数，是否隐藏欢迎界面
		intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, isHideWellcome);

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE,
		// getUiBackgroundPicPath());
		// 可选参数，指定assets中的图片路径，作为背景图
		// intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS,
		// getUiBackgroundPathInAssets());

		// -- 以下参数仅仅针对自动登录过程的控制
		// 可选参数，自动登录过程中是否不展示任何UI，默认展示。
		intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, isAutoLoginHideUI);

		// 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
		intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN,
				isShowDlgOnFailedAutoLogin);
		// 测试参数，发布时要去掉
		intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, isDebugSocialShare);

		return intent;
	}

	// 切换账号的回调
	private IDispatcherCallback mAccountSwitchCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			// press back
			if (isCancelLogin(data)) {
				return;
			}

			// 显示一下登录结果
			// Toast.makeText(appActivity, data, Toast.LENGTH_LONG).show();

			// TypeSDKLogger.d( "mAccountSwitchCallback, data is " + data);
			// 解析User info
			QihooUserInfo info = parseUserInfoFromLoginResult(data);
			// 解析access_token
			mAccessToken = parseAccessTokenFromLoginResult(data);
		}
	};

	// 支持离线模式的切换账号的回调
	private IDispatcherCallback mAccountSwitchSupportOfflineCB = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			// press back
			if (isCancelLogin(data)) {
				return;
			}
			// 显示一下登录结果
			// Toast.makeText(appActivity, data, Toast.LENGTH_LONG).show();
			// TypeSDKLogger.d( "mAccountSwitchSupportOfflineCB, data is " + data);
			// 解析User info
			QihooUserInfo info = parseUserInfoFromLoginResult(data);
			// 解析access_token
			mAccessToken = parseAccessTokenFromLoginResult(data);
		}
	};

	/**
	 * 使用360SDK的退出接口
	 *
	 * @param isLandScape
	 *            是否横屏显示支付界面
	 */
	protected void doSdkQuit(boolean isLandScape) {

		Bundle bundle = new Bundle();

		// 界面相关参数，360SDK界面是否以横屏显示。
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE,
				isLandScape);

		// 必需参数，使用360SDK的退出模块。
		bundle.putInt(ProtocolKeys.FUNCTION_CODE,
				ProtocolConfigs.FUNC_CODE_QUIT);

		// 可选参数，登录界面的背景图片路径，必须是本地图片路径
		bundle.putString(ProtocolKeys.UI_BACKGROUND_PICTRUE, "");

		Intent intent = new Intent(appActivity, ContainerActivity.class);
		intent.putExtras(bundle);

		Matrix.invokeActivity(appActivity, intent, mQuitCallback);
	}

	// 退出的回调
	private IDispatcherCallback mQuitCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String data) {
			// TypeSDKLogger.d( "mQuitCallback, data is " + data);
			JSONObject json;
			try {
				json = new JSONObject(data);
				int which = json.optInt("which", -1);
				// String label = json.optString("label");
				// Toast.makeText(appActivity,
				// "按钮标识：" + which + "，按钮描述:" + label, Toast.LENGTH_LONG)
				// .show();
				switch (which) {
				case 0: // 用户关闭退出界面
					TypeSDKLogger.d( "用户关闭退出界面");
					return;
				case 1: //进入论坛
					TypeSDKLogger.d( "exit_success");
					UnityPlayer.currentActivity.finish();
					System.exit(0);
					break;
				case 2://退出游戏
					TypeSDKLogger.d( "exit_success");
					UnityPlayer.currentActivity.finish();
					System.exit(0);
					break;
				default:
					TypeSDKLogger.i("exit which:" + which);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	};
//	protected boolean mGetUserInfoFlag = false;
	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		SetPlayerInfo(_in_context, _in_data);
		TypeSDKLogger.d("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			
			String id = userInfo.GetData(AttName.USER_ID);
			String roleId = userInfo.GetData(AttName.ROLE_ID);
			String roleName = userInfo.GetData(AttName.ROLE_NAME);
			String roleLevel = userInfo.GetData(AttName.ROLE_LEVEL);
			String zoneId = userInfo.GetData(AttName.SERVER_ID);
			String zoneName = userInfo.GetData(AttName.SERVER_NAME);
			String balance = userInfo.GetData(AttName.SAVED_BALANCE);
			String vip = userInfo.GetData(AttName.VIP_LEVEL);
			String partyName = userInfo.GetData("party_name").isEmpty()?"无帮派":userInfo.GetData("party_name");
//			HashMap<String, String> mapParams = new HashMap<String, String>();
//	    	mapParams.put("key1", "value1");
//	    	mapParams.put("key2", "value2");
//	    	HashMap<String, String> map = mGetUserInfoFlag ? mapParams : null;
//	    	mGetUserInfoFlag = !mGetUserInfoFlag;
			//TypeSDKLogger.d( "给360传递的partyName值："+partyName);
			HashMap<String, String> map = null;
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				
				Matrix.statEvent(id, roleId, roleName, roleLevel, zoneId, zoneName, balance, vip, partyName, map);
				TypeSDKLogger.d( "进入游戏时的角色信息");
				
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				Matrix.statEvent(id, roleId, roleName, roleLevel, zoneId, zoneName, balance, vip, partyName, map);
				TypeSDKLogger.d( "创建角色时的角色信息");
				
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				Matrix.statEvent(id, roleId, roleName, roleLevel, zoneId, zoneName, balance, vip, partyName, map);
				TypeSDKLogger.d( "角色升级时角色信息");
			}else{
				TypeSDKLogger.d( "datatype error:"+"提交的数据不合法");
			}
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
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
