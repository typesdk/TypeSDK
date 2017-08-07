package com.type.sdk.android.wogame;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.SystemUtils;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;
import com.unipay.account.AccountAPI;
import com.unipay.account.AccountAPI.BusyException;
import com.unipay.account.AccountAPI.OnInitResultListener;
import com.unipay.account.AccountAPI.OnLoginResultListener;
import com.unipay.account.AccountAPI.OnLogoutResultListener;
import com.unipay.account.UnipayAccountPlatform;
import com.unipay.account.UserInfo;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private String token;
	public boolean isNoInit = false;
	//AccountAPI mUniPay;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		TypeSDKLogger.i("isInit=" + isInit);
		if (isInit) {
			TypeSDKLogger.e("error init do again");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}
		woGameSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.woGameSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.woGameSdkLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.woGameSdkPay(_in_pay);
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
	}

	@Override
	public void ExitGame(Context _in_context) {
		woGameSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		//if(mUniPay != null){
		//	mUniPay.showFloatView(appActivity);
		//}
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
		//if(mUniPay != null){
		//	mUniPay.closeFloatView(appActivity);
		//}		
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
	}

	private void woGameSdkInit() {
		TypeSDKLogger.i("woGameSdkInit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {

					// 账户模块初始化
					UnipayAccountPlatform.init(appActivity,
							platform.GetData(AttName.APP_ID),
							platform.GetData(AttName.APP_KEY),
							new OnInitResultListener() {

								@Override
								public void onResult(int code, String message) {
									// TODO Auto-generated method stub
									if (code == AccountAPI.CODE_SUCCESS) {
										// mTvOutput.setText("初始化成功");
										TypeSDKLogger.e("初始化成功: " + message);

										TypeSDKNotify_WoGame notify = new TypeSDKNotify_WoGame();
										notify.Init();
										UnipayAccountPlatform
												.getInstance()
												.setAccountStatusChangedListener(
														mOnAccountStatusChangedListener);

									} else {
										TypeSDKLogger.e("初始化失败: " + message);
									}
								}
							});

				} catch (BusyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private AccountAPI.OnAccountStatusChangedListener mOnAccountStatusChangedListener = new AccountAPI.OnAccountStatusChangedListener() {

		@Override
		public void onLogout() {
			TypeSDKLogger.e("用户已退出登录，请重新登录 ");
		}

		@Override
		public void onAccountSwitched() {

			UserInfo uinfo = UnipayAccountPlatform.getInstance()
					.getCurrentUserInfo();
			if (uinfo == null) {
				TypeSDKLogger.e("can NOT get current user info!! ");
				TypeSDKLogger.e("错误.1：未能获取到用户信息!");

			} else {
				TypeSDKLogger.e("已切换到新用户");
				TypeSDKLogger.e(uinfo.toString());
			}
		}
	};

	private void woGameSdkLogin() {
		TypeSDKLogger.i("woGameSdkLogin start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					UnipayAccountPlatform.getInstance().login(appContext,
							new OnLoginResultListener() {

								@Override
								public void onLoginResult(int code) {
									// TODO Auto-generated method stub
									if (code == AccountAPI.CODE_SUCCESS) {
										TypeSDKLogger.e("登录成功 ");
										UserInfo uinfo = UnipayAccountPlatform
												.getInstance()
												.getCurrentUserInfo();
										TypeSDKLogger.e("uinfo="
												+ uinfo.toString());
										//mUniPay = UnipayAccountPlatform.getInstance();
										//mUniPay.showFloatView(appActivity);
										TypeSDKNotify_WoGame notify = new TypeSDKNotify_WoGame();
										token = uinfo.getAccessToken()
												+ "|"
												+ SystemUtils
														.getIMEI(appContext)
												+ "|"
												+ SystemUtils
														.getWifiMac(appContext)
												+ "|"
												+ SystemUtils.getPhoneIp();

										TypeSDKLogger.e("token:" + token);
										TypeSDKLogger.e("uinfo.getUserId():"
												+ uinfo.getUserId());
										notify.sendToken(token,
												"" + uinfo.getUserId());
									} else {
										TypeSDKLogger.e("登录失败：" + code);
									}
								}
							});
				} catch (BusyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void woGameSdkLogout() {
		TypeSDKLogger.i("woGameSdkLogout start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				UnipayAccountPlatform.getInstance().logout(appActivity,
						new OnLogoutResultListener() {

							@Override
							public void onLogoutResult(int code, String message) {
								// TODO Auto-generated method stub
								if (code == AccountAPI.CODE_SUCCESS) {
									// showToast("注销成功");
									TypeSDKNotify_WoGame notify = new TypeSDKNotify_WoGame();
									notify.Logout();
									TypeSDKLogger.e("LOGOUT SUCCESS");
								}
							}
						});

			}
		});
	}

	private void woGameSdkExit() {
		TypeSDKLogger.i("woGameSdkExit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (UnipayAccountPlatform.getInstance() != null) {
					UnipayAccountPlatform.getInstance().exitSDK();
					System.exit(0);
				}

			}
		});
	}
	
	/**
	  * 在进制表示中的字符集合。
	  */
	 final static char[] digits = {
	  '0' , '1' , '2' , '3' , '4' , '5' ,
	  '6' , '7' , '8' , '9' , 'a' , 'b' ,
	  'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
	  'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
	  'o' , 'p' , 'q' , 'r' , 's' , 't' ,
	  'u' , 'v' , 'w' , 'x' , 'y' , 'z'
	 };
	 /**
	  * 将十进制的数字转换为指定进制的字符串。
	  * @param i 十进制的数字。
	  * @param system 指定的进制，常见的2/8/16。
	  * @return 转换后的字符串。
	  */
	 public static String toCustomNumericString(long i, int system) {
	  long num = 0;
	  if (i < 0) {
	   num = ((long)2 * 0x7fffffff) + i + 2;
	  } else {
	   num = i;
	  }
	  char[] buf = new char[32];
	  int charPos = 32;
	  while ((num / system) > 0) {
	   buf[--charPos] = digits[(int)(num % system)];
	   num /= system;
	  }
	  buf[--charPos] = digits[(int)(num % system)];
	  return new String(buf, charPos, (32 - charPos));
	 }

	private void woGameSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		Handler mHandler = new Handler(Looper.getMainLooper());
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				String price = "0";
				String ss = "";
				String uids="";
				if (TypeSDKTool.isPayDebug) {
					price = "1";
				} else {
					price = _in_pay.GetData(AttName.REAL_PRICE);
				}
				TypeSDKLogger.e("ITEM_SERVER_ID:"+_in_pay.GetData(AttName.ITEM_SERVER_ID));
				if(itemListData == null){
					TypeSDKLogger.e("itemListData is null");
				}
				TypeSDKLogger.i("itemListData:" + itemListData.DataToString());
				String item_id = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));
				String item_uid = userInfo.GetData(AttName.USER_ID);
				String it_id = "0";
				String it_uid = "0";
				if(item_id != null && !item_id.isEmpty()){
					it_id = item_id;
				} else {
					TypeSDKLogger.e("item_id is null");
				}
				if(item_uid != null && !item_uid.isEmpty()){
					it_uid = item_uid;
				} else {
					TypeSDKLogger.e("(用户ID)item_uid is null");
				}
				uids = toCustomNumericString(Long.parseLong(it_uid), 32);
				ss = toCustomNumericString(Long.parseLong(it_id), 32);
				TypeSDKLogger.i("it_id:" + it_id);
				TypeSDKLogger.i("it_uid:" + it_uid);
				TypeSDKLogger.i("bill number s:"
						+ _in_pay.GetData(AttName.BILL_NUMBER) + "|"
								+ uids + "|" + ss);
				Utils.getInstances().payOnlineAnyMoney(
						appContext,
						_in_pay.GetData(AttName.ITEM_NAME),
						price,
						_in_pay.GetData(AttName.BILL_NUMBER) + "|"
								+ uids + "|" + ss,
						new paylistener());

			}
		});
	}

	private class paylistener implements UnipayPayResultListener {
		TypeSDKNotify_WoGame notify_WoGame = new TypeSDKNotify_WoGame();
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();

		@Override
		public void PayResult(final String arg0, final int arg1,
				final int arg2, final String error) {
			TypeSDKLogger.e("arg0:" + arg0);
			TypeSDKLogger.e("arg1:" + arg1);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					switch (arg1) {
					case 1:// success
							// 此处放置支付请求已提交的相关处理代码
						payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT,
								"1");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"支付成功");
						notify_WoGame.Pay(payResult.DataToString());
						TypeSDKLogger.i("支付成功");
						break;

					case 2:// fail
							// 此处放置支付请求失败的相关处理代码
						payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT,
								"0");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"支付失败");
						notify_WoGame.Pay(payResult.DataToString());
						TypeSDKLogger.i("支付失败:" + error);
						break;

					case 3:// cancel
							// 此处放置支付请求被取消的相关处理代码
						payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT,
								"2");
						payResult.SetData(
								TypeSDKDefine.AttName.PAY_RESULT_REASON,
								"支付取消");
						notify_WoGame.Pay(payResult.DataToString());
						TypeSDKLogger.i("支付取消:" + error);
						break;

					default:
						break;
					}
				}
			});
		}
	}

	// private void paPaSdkSendInfo(final JSONObject _jsonExData) {}

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

}
