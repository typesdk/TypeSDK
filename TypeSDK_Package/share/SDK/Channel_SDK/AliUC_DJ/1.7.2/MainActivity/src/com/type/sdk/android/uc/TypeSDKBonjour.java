/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.uc;

import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import cn.uc.gamesdk.sa.UCGameSdk;
import cn.uc.gamesdk.sa.iface.open.ActivityLifeCycle;
import cn.uc.gamesdk.sa.iface.open.SDKConst;
import cn.uc.gamesdk.sa.iface.open.UCCallbackListener;
import cn.uc.gamesdk.sa.iface.open.UCGameSDKStatusCode;
import cn.uc.paysdk.SDKCore;
import cn.uc.paysdk.common.utils.PhoneInfoUtil;
import cn.uc.paysdk.face.commons.SDKProtocolKeys;

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

	private Handler mhandler; // 处理JY支付接口回调

	public final static int HANDLER_SHOW_ERROR_DIALOG = -1;// 显示错误提示
	public final static int HANDLER_INIT_SUCC = 1; // 支付SDK初始化成功
	public final static int HANDLER_PAY_CALLBACK = 2; // 支付SDK支付回调

	private boolean sdkInit = false;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		if (isInit) {
			TypeSDKLogger.e("error init do again");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		checkNetwork();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.ucSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.ucSdkLogout();
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
		TypeSDKLogger.e("ShowToolBar");
		this.ucSdkShowFloatButton();
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.ucSdkPay(_in_pay);
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
		TypeSDKLogger.e("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		try {
			TypeSDKLogger.e("_in_data:" + _in_data);
			userInfo.StringToData(_in_data);

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",
					userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName",
					userData.GetData(AttName.SERVER_NAME));
			userJsonExData.put("roleCTime",
					userData.GetData(AttName.ROLE_CREATE_TIME));
			TypeSDKLogger.e("上传用户信息:string=" + userJsonExData);
			this.ucSdkSendInfo(userJsonExData);
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
			} else if (userInfo.GetData("role_type").equals("levelUp")) {
				// 角色升级时角色信息

			} else {
				TypeSDKLogger.e("datatype error:" + "提交的数据不合法");
			}
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}

	@Override
	public void ExitGame(Context _in_context) {
		if (exitGameListenser()) {
			TypeSDKLogger.e("执行ExitGame方法");
			this.ucSdkExit();
		}

	}

	public void onCreate(Activity activity) {
		UCGameSdk.defaultSdk().lifeCycle(activity,
				ActivityLifeCycle.LIFE_ON_CREATE);
		getHandler();
	}

	public void onStart() {
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_START);
	}

	public void onRestart() {
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_RESTART);
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_RESUME);
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_PAUSE);
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_STOP);
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_DESTROY);
	}

	public void onNewIntent() {
		TypeSDKLogger.e("onNewIntent");
		UCGameSdk.defaultSdk().lifeCycle(appActivity,
				ActivityLifeCycle.LIFE_ON_NEW_INTENT);
	}

	public void checkNetwork() {
		// !!!在调用SDK初始化前进行网络检查
		// 当前没有拥有网络
		final Activity runActivi;
		if (UnityPlayer.currentActivity != null)
			runActivi = UnityPlayer.currentActivity;
		else
			runActivi = appActivity;
		ucSdkInit();
	}

	private void ucSdkInit() {
		TypeSDKLogger.e("initSDK_begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					UCGameSdk.defaultSdk().setCallback(
							SDKConst.SDK_INIT_LISTENER, mUCcallBackListener);
					UCGameSdk.defaultSdk().setCallback(
							SDKConst.PAY_INIT_LISTENER,
							new TypePaySDKListener(mhandler));

					try {
						Bundle payInitData = new Bundle();
						payInitData.putString(SDKProtocolKeys.APP_ID,
								platform.GetData(AttName.APP_ID));
						payInitData.putString(SDKProtocolKeys.APP_KEY,
								platform.GetData(AttName.APP_KEY));
						UCGameSdk.defaultSdk().init(appActivity, payInitData);
					} catch (Exception e) {

					}

				} catch (Exception e) {
					e.printStackTrace();
					TypeSDKLogger.e("initSDK_Exception");
				}
			}
		});
		TypeSDKLogger.e("initSDK_end");
	}

	UCCallbackListener<String> mUCcallBackListener = new UCCallbackListener<String>() {

		@Override
		public void callback(int statuscode, String msg) {

			if (statuscode == UCGameSDKStatusCode.SUCCESS) {
				TypeSDKLogger.d("sdk init success");
				sdkInit = true;
			} else {
				TypeSDKLogger.e("sdk init fail msg :" + msg);
				sdkInit = false;
			}

		}
	};

	private void ucSdkLogin() {
		// final Activity runActivi;
		// if (UnityPlayer.currentActivity != null)
		// runActivi = UnityPlayer.currentActivity;
		// else
		// runActivi = appActivity;

		// runActivi.runOnUiThread(new Runnable() {
		// public void run() {
		TypeSDKLogger.i("login start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	private void ucSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.e("ucSdkLogout");
			}
		});
	}

	private void ucSdkExit() {
		// Activity runActivi = null;
		// if(UnityPlayer.currentActivity!=null)
		// runActivi = UnityPlayer.currentActivity;
		// else
		// runActivi = appActivity;

		// runActivi.runOnUiThread(new Runnable() {
		// public void run() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("执行ucSdkExit方法");
					UCGameSdk.defaultSdk().exit(appActivity, new UCCallbackListener<String>() {
			            @Override
			            public void callback(int statuscode, String data) {
			                if (UCGameSDKStatusCode.SDK_EXIT == statuscode) {
			                	TypeSDKLogger.d("退出接口调用");
			                	appActivity.finish();
			                	System.exit(0);
			                } else {
			                	//取消回到游戏
			                	TypeSDKLogger.d("exit error");
			                }
			            }
			        });
				} catch (Exception e) {
					e.printStackTrace();
					TypeSDKLogger.e("Exception:" + e.toString());
				}

			}
		});
	}

	private void ucSdkCreateFloatButton() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	private void ucSdkShowFloatButton() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	private void ucSdkDestoryFloatButton() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	private void ucSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());

		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				String price = "0";
				if (TypeSDKTool.isPayDebug) {
					price = "1";
				} else {
					price = (_in_pay.GetInt(AttName.REAL_PRICE) / 100) + "";
				}
				TypeSDKLogger.e("" + price);
				TypeSDKLogger.i("REAL_PRICE:"
						+ _in_pay.GetFloat(AttName.REAL_PRICE));

				final Activity runActivi = UnityPlayer.currentActivity;
				Intent data = new Intent();
				// 具体请参考SDKProtocolKeys
				data.putExtra(SDKProtocolKeys.APP_NAME,
						platform.GetData(AttName.APP_NAME));
				data.putExtra(SDKProtocolKeys.AMOUNT, price);
				data.putExtra(SDKProtocolKeys.PRODUCT_NAME,
						_in_pay.GetData(AttName.ITEM_NAME));
				data.putExtra(SDKProtocolKeys.CP_ORDER_ID,
						_in_pay.GetData(AttName.BILL_NUMBER));
				data.putExtra(SDKProtocolKeys.ATTACH_INFO,
						_in_pay.GetData(AttName.BILL_NUMBER));// 值为[a-z]字符集
				// GAME_ID可选参数(如果是多业务接入，必填)，由支付中心分配
				data.putExtra(SDKProtocolKeys.GAME_ID,
						platform.GetData(AttName.APP_ID));
				data.putExtra(SDKProtocolKeys.NOTIFY_URL, platform.GetData("url"));

				// payCode 可选参数(运营商短代支付必填)，由运营商派生平台分配
				String payCode = getPaycode(appContext.getApplicationContext());
				if (!TextUtils.isEmpty(payCode)) {
					/*
					 * 需要根据sim卡号传入不同运营商平台提供的paycode， 判断运营商的方法见下getOperatorByMnc
					 * 如果是移动的卡，因为移动有移动MM和南京基地两个支付sdk，所以： 如果 payCode.lennth == 3;
					 * 走南京基地支付; 否则走移动MM支付
					 */
					data.putExtra(SDKProtocolKeys.PAY_CODE, payCode);
				}
				try {
					SDKCore.pay(appActivity, data, new TypePaySDKListener(mhandler));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	// ////////////////////////以下是对sim卡做区分的功能函数/////////////////////////////

	/**
	 * 获取测试运营商计费代码：运营商短代支付必传 注意：
	 * 1、该paycode是本demo测试专用，游戏接入的时候需要换成自己在各运营商平台申请的paycode
	 * 2、移动运营商单机短代支付有两种支付方式（移动MM和移动咪咕） 本示例采取随机办法，选取不同平台的paycode以演示支付能力
	 * 
	 * @param ctx
	 * @return paycode
	 */
	private String getPaycode(Context ctx) {
		int operatorCode = getOperatorByMnc(getOperator(ctx));
		String paycode = "";
		switch (operatorCode) {
		case PhoneInfoUtil.CHINA_MOBILE:// 移动
			int i = new Random().nextInt(2);// 随机产生移动mm或移动咪咕计费代码
			if (i > 0) {
				paycode = platform.GetData("mm_paycode"); // MM支付测试paycode
			} else {
				paycode = platform.GetData("mg_paycode");// 咪咕支付测试paycode;
			}
			break;

		case PhoneInfoUtil.CHINA_UNICOM:// 联通
			paycode = platform.GetData("lt_paycode"); // 联通支付测试paycode
			break;

		case PhoneInfoUtil.CHINA_TELECOM:// 电信
			paycode = platform.GetData("dx_paycode"); // 电信支付测试paycode
			break;
		default:
			break;
		}

		return paycode;
	}

	/**
	 * 根据mccmnc获取运营商类型 https://en.wikipedia.org/wiki/Mobile_country_code
	 * 
	 * @param mccmnc
	 * @return 手机卡运营商类型
	 */
	public static int getOperatorByMnc(String mccmnc) {
		int ret = -1;
		if (!TextUtils.isEmpty(mccmnc)) {
			int code = 0;
			try {
				code = Integer.parseInt(mccmnc);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			switch (code) {
			case 46000:
			case 46002:
			case 46007:
			case 46020:
				ret = PhoneInfoUtil.CHINA_MOBILE;// 移动卡
				break;

			case 46001:
			case 46006:
				ret = PhoneInfoUtil.CHINA_UNICOM;// 联通卡
				break;

			case 46003:
			case 46005:
			case 46011:
				ret = PhoneInfoUtil.CHINA_TELECOM;// 电信卡
				break;
			default:
				break;
			}
		}

		return ret;
	}

	/**
	 * 获取sim卡Operator
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getOperator(Context ctx) {
		String type = "";
		if (getSimState(ctx)) {
			TelephonyManager telephonyManager = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			String operator = telephonyManager.getSimOperator();
			if (operator != null) {
				type = operator;
			}
		}

		return type;
	}

	/**
	 * 获取sim卡的状态
	 * 
	 * @param ctx
	 * @return true，SIM卡良好可以正常methodName使用；false 其它状态
	 */
	public static boolean getSimState(Context ctx) {
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telephonyManager.getSimState();
		return (simState == TelephonyManager.SIM_STATE_READY) ? true : false;
	}

	// ////////////////////////以上是对sim卡做区分的功能函数/////////////////////////////

	private void ucSdkSendInfo(final JSONObject _jsonExData) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("提交游戏扩展数据功能调用成功");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		SetPlayerInfo(_in_context, _in_data);
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

	private void getHandler() {
		mhandler = new Handler() {
			public void handleMessage(Message msg) {
				TypeSDKNotify_UC notify = new TypeSDKNotify_UC();
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				switch (msg.what) {
				case HANDLER_INIT_SUCC:
					// 完成了初始化，显示右上金币按钮，点击可进行支付
					isInit = true;
					notify.Init();
					
					break;
				case HANDLER_SHOW_ERROR_DIALOG:
					TypeSDKLogger.e("error:" + msg.obj);
					payResult.SetData(AttName.PAY_RESULT, "0");
					payResult.SetData(AttName.PAY_RESULT_REASON, "FAIL");
					notify.Pay(payResult.DataToString());
					
					break;
				case HANDLER_PAY_CALLBACK:
					if (appActivity.isFinishing()) {
						return;
					}

					/*
					 * 处理初始化、支付回调信息，这里仅dialog展示， 游戏接入需要根据业务修改：比如支付成功发放道具等
					 */
					String message = (String) msg.obj;
					if (TextUtils.isEmpty(message))
						message = "unknown";

					TypeSDKLogger.d("message:" + message);

					payResult.SetData(AttName.PAY_RESULT, "1");
					payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
					notify.Pay(payResult.DataToString());

					break;
				}
			}
		};
	}
}
