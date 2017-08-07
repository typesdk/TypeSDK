package com.type.sdk.android.meizu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.sqwan.msdk.SQwanCore;
import com.sqwan.msdk.api.SQResultListener;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;

	// pay info
	String jsonResult;
	String order_amount;
	String order_id;
	String item_name;
	String product_subject;
	String product_unit;
	String product_id;
	String sign;
	String sign_type;
	String user_info;
	String user_id;
	String beforeSignString;
	long create_time;
	int buy_count;
	int buy_amount;
	// pay info end

	// login info
	String userToken;

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
			TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
			notify.Init();
			return;
		}
		this.meizuInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.meizuLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.meizuLogout();
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
		this.meizuPay(_in_pay);
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

			String extendInfo = new StringBuilder().append("gameId=")
					.append(platform.GetData(AttName.APP_ID))
					.append("&service=")
					.append(userInfo.GetData(AttName.SERVER_NAME))
					.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
					.append("&grade=")
					.append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if(exitGameListenser()){
					SQwanCore.getInstance().logout(appContext,
						new SQResultListener() {

							@Override
							public void onSuccess(Bundle arg0) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("exit success");
								// TypeSDKData.BaseData exitResult = new
								// TypeSDKData.BaseData();
								// exitResult.SetData("exitreason",
								// "quit game");
								// TypeSDKEventManager.Instance().SendUintyEvent(ReceiveFunction.MSG_EXITGAMECANCEL,
								// exitResult.DataToString());
								System.exit(0);
							}

							@Override
							public void onFailture(int arg0, String arg1) {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("exit fail");
							}
						});
				}
			}
		});
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		/**
		 * ！！！必须接入！！！
		 */
		if (isInit) {
			SQwanCore.getInstance().onResume();
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		/**
		 * ！！！必须接入！！！
		 */
		SQwanCore.getInstance().onPause();
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		/**
		 * ！！！必须接入！！！
		 */
		SQwanCore.getInstance().onStop();
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		/**
		 * ！！！必须接入！！！
		 */
		SQwanCore.getInstance().onDestroy();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		/**
		 * ！！！必须接入！！！
		 */
		SQwanCore.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	protected void onNewIntent(Intent intent) {
		/**
		 * ！！！必须接入！！！
		 */
		SQwanCore.getInstance().onNewIntent(intent);
	}

	private void meizuInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.d("APP_ID:"
							+ platform.GetData(AttName.APP_ID));
					TypeSDKLogger.d("APP_KEY:"
							+ platform.GetData(AttName.SECRET_KEY));
                    
					SQwanCore.getInstance().init(appContext,
							platform.GetData(AttName.SECRET_KEY),
							new SQResultListener() {

								@Override
								public void onSuccess(Bundle arg0) {
									// TODO Auto-generated method stub
									TypeSDKLogger.e("initSDK_success");
									TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
									notify.Init();
									isInit = true;
								}

								@Override
								public void onFailture(int code, String msg) {
									// TODO Auto-generated method stub
									TypeSDKLogger.e("initSDK_fail:" + msg);
									TypeSDKLogger.e("fail msg:" + msg);
									TypeSDKLogger.e("fail code:" + code);
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

	private void meizuLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				SQwanCore.getInstance().setSwitchAccountListener(
						new SQResultListener() {

							@Override
							public void onSuccess(Bundle bundle) {
								// TODO Auto-generated method stub
								TypeSDKLogger.d("switchAccount success");
								TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
								notify.Logout();
							}

							@Override
							public void onFailture(int code, String msg) {
								// TODO Auto-generated method stub
								TypeSDKLogger.d("switchAccount fail");
							}
						});

				SQwanCore.getInstance().login(appContext,
						new SQResultListener() {

							@Override
							public void onSuccess(Bundle bundle) {
								// TODO Auto-generated method stub
								TypeSDKLogger.d("LOGIN_SUCCESS");
								TypeSDKLogger.d("登陆成功:\n token:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_TOKEN)
										+ "\n userid:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_USERID)
										+ "\n username:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_USERNAME)
										+ "\n gid:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_GID)
										+ "\n pid:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_PID));

								try {
									userToken = URLEncoder.encode(
											bundle.getString(SQwanCore.LOGIN_KEY_TOKEN),
											"UTF-8");
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								TypeSDKLogger.d("token:"
										+ URLEncoder.encode(bundle
												.getString(SQwanCore.LOGIN_KEY_TOKEN)));
								TypeSDKLogger.d("token:" + userToken);// 登录token，可用于服务端登录验证
								TypeSDKLogger.d("userid:"
										+ bundle.getString(SQwanCore.LOGIN_KEY_USERID));// 用户id
								// 发送登录SDK成功广播通知
								TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
								notify.sendToken(userToken, bundle
										.getString(SQwanCore.LOGIN_KEY_USERID));
								// URLEncoder.encode(bundle.getString(SQwanCore.LOGIN_KEY_TOKEN))
							}

							@Override
							public void onFailture(int code, String msg) {
								// TODO Auto-generated method stub
								TypeSDKLogger.d("LOGIN_FAIL");
								TypeSDKLogger.d("fail code:" + code);
								TypeSDKLogger.d("fail msg:" + msg);
							}
						});

			}
		});

	}

	private void meizuLogout() {
		TypeSDKLogger.d("meizuLogout");
		// xnz 2015.12.11 SQwanCore.getInstance().changeAccount()切换账号
		SQwanCore.getInstance().changeAccount(appContext,
				new SQResultListener() {

					@Override
					public void onSuccess(Bundle arg0) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("meizuLogout_SUCCESS");
						TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
						notify.Logout();
					}

					@Override
					public void onFailture(int arg0, String arg1) {
						// TODO Auto-generated method stub
						TypeSDKLogger.d("meizuLogout_Fail");
					}
				});

	}

	private void meizuPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("product_body:"
							+ _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("product_id:"
							+ _in_pay.GetData(AttName.ITEM_LOCAL_ID));
					TypeSDKLogger.e("buy_count:"
							+ _in_pay.GetInt(AttName.ITEM_COUNT));
					TypeSDKLogger.e("order_id:"
							+ _in_pay.GetData(AttName.EXTRA) + "|"
							+ _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("PAY_BASE_RATE:"
							+ _in_pay.GetData(AttName.PAY_BASE_RATE));
					TypeSDKLogger.e("PAY_BASE_VALUE:"
							+ _in_pay.GetData(AttName.PAY_BASE_VALUE));
					buy_count = _in_pay.GetInt(AttName.ITEM_COUNT);
					order_amount = "0.01";
					order_id = _in_pay.GetData(AttName.BILL_NUMBER);// _in_pay.GetData(AttName.EXTRA)+"|"+
					item_name = _in_pay.GetData(AttName.ITEM_NAME);
					user_id = _in_pay.GetData(AttName.USER_ID);
					// product_id = _in_pay.GetData(AttName.ITEM_LOCAL_ID);
					beforeSignString = user_id + "|" + order_amount + "|"
							+ order_id + "|" + item_name + "|clientKey";
					sign = md5(beforeSignString);
					// new DialogHelper().execute(1);
					TypeSDKLogger.e("order_id:" + order_id);

					float price;
					if (TypeSDKTool.isPayDebug) {
						price = 1.0f;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
					}
					TypeSDKLogger.e("price:" + price);
					/**
					 * 支付接口参数说明 !!! 注意必传参数,不能为空，推荐所有参数都传值 !!!
					 * 
					 * @param context
					 *            上下文 (*必传)
					 * @param doid
					 *            CP订单ID (*必传)
					 * @param dpt
					 *            CP商品名
					 * @param dcn
					 *            CP货币名称
					 * @param dsid
					 *            CP游戏服ID (*必传)
					 * @param dsname
					 *            CP游戏服名称
					 * @param dext
					 *            CP扩展回调参数 (*必传)
					 * @param drid
					 *            CP角色ID
					 * @param drname
					 *            CP角色名
					 * @param drlevel
					 *            CP角色等级
					 * @param dmoney
					 *            CP金额(定额) (*必传)
					 * @param dradio
					 *            CP兑换比率(1元兑换率默认1:10)
					 * @param payListener
					 *            充值回调 (*必传)
					 */
					int dradio = 10;
					String dcn = "钻石";
					SQwanCore.getInstance().pay(appContext, order_id,
							item_name, dcn, _in_pay.GetData(AttName.SERVER_ID),
							_in_pay.GetData(AttName.SERVER_NAME), order_id,
							_in_pay.GetData(AttName.ROLE_ID),
							_in_pay.GetData(AttName.ROLE_NAME),
							_in_pay.GetInt(AttName.ROLE_LEVEL), price, dradio,
							new SQResultListener() {

								@Override
								public void onSuccess(Bundle bundle) {
									// 支付成功
									TypeSDKLogger.e("pay_success");
									PayResultData payResult = new PayResultData();
									payResult.SetData(AttName.PAY_RESULT, "1");
									payResult
											.SetData(
													TypeSDKDefine.AttName.PAY_RESULT_REASON,
													"SUCCESS");
									TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
									notify.Pay(payResult.DataToString());
								}

								@Override
								public void onFailture(int code, String msg) {
									TypeSDKLogger.e("return Error");
									TypeSDKLogger.e("code:" + code);
									TypeSDKLogger.e("msg:" + msg);
									PayResultData payResult = new PayResultData();
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(
											AttName.PAY_RESULT_REASON,
											"PAY_FAIL");
									TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
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

	public static String md5(String string) {

		byte[] hash;

		try {

			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException("Huh, MD5 should be supported?", e);

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("Huh, UTF-8 should be supported?", e);

		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {

			if ((b & 0xFF) < 0x10)
				hex.append("0");

			hex.append(Integer.toHexString(b & 0xFF));

		}

		return hex.toString();

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
