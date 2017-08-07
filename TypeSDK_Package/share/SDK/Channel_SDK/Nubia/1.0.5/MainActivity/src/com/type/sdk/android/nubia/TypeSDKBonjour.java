package com.type.sdk.android.nubia;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import cn.nubia.componentsdk.constant.ConstantProgram;
import cn.nubia.sdk.CallbackListener;
import cn.nubia.sdk.GameSdk;
import cn.nubia.sdk.constant.ErrorCode;
import cn.nubia.sdk.entry.AppInfo;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
	private RequestQueue mQueue;

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
		mQueue = Volley.newRequestQueue(appContext);
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
			notify.Init();
			return;
		}
		this.nubiaSDKInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.nubiaSDKLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.nubiaSDKLogout();
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
		this.nubiaSDKPay(_in_pay);

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

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if (exitGameListenser()) {
			System.exit(0);
		}

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {

		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {

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

	private void nubiaSDKInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					int appId = platform.GetInt(AttName.APP_ID); // 配置您自己的appid
					String appKey = platform.GetData(AttName.APP_KEY);// 配置您自己的appkey
					
					final AppInfo appInfo = new AppInfo();
					appInfo.setAppId(appId);
					appInfo.setAppKey(appKey);
					appInfo.setChannelId(1); // 配置渠道
					if (TypeSDKTool.isScreenOriatationPortrait(appContext)) {
						appInfo.setOrientation(1); // 0：横屏；1：竖屏
					} else {
						appInfo.setOrientation(0);
					}
					GameSdk.initSdk(appContext, appInfo,
							new CallbackListener<Void>() {

								@Override
								public void callback(int responseCode, Void t) {
									// TODO Auto-generated method stub
									if (responseCode == ErrorCode.SUCCESS) {
										TypeSDKLogger.i("init_success");
										TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
										notify.Init();
										isInit = true;
									} else {
										TypeSDKLogger.e("init_errorCode:"
												+ responseCode);
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

	}

	private void nubiaSDKLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (GameSdk.isLogined()) {
					TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
					notify.sendToken(GameSdk.getSessionId(),
							GameSdk.getLoginUid());
					return;
				}
				GameSdk.openLoginActivity(appContext,
						new CallbackListener<String>() {
							@Override
							public void callback(int responseCode,
									String account) {
								switch (responseCode) {
								case ErrorCode.SUCCESS:
									// TODO 登陆成功，拿uid和sessionId去CP服务器校验合法性
									TypeSDKLogger.i("login_success");
									TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
									notify.sendToken(GameSdk.getSessionId(),
											GameSdk.getLoginUid());
									break;
								case ErrorCode.USER_CANCLE:
									// TODO 用户主动取消登录操作
									TypeSDKLogger.w("login_cancle");
									break;
								default:
									// TODO 登录失败，包含错误码和错误消息
									TypeSDKLogger.e("login_errorCode:"
											+ responseCode);
									break;
								}
							}
						});
			}
		});

	}

	private void nubiaSDKLogout() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				if (!GameSdk.isLogined()) {
					TypeSDKLogger.w("no login");
					return;
				}
				GameSdk.logout(appContext, new CallbackListener<Void>() {
					public void callback(int responseCode, Void t) {
						TypeSDKLogger.i("Logout_code:" + responseCode);
						TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
						notify.Logout();
					}
				});
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

	private void nubiaSDKPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
	    String price = "0";
		if (TypeSDKTool.isPayDebug) {
			price = "0.01";
		} else {
			price = ""
					+ (_in_pay.GetInt(AttName.REAL_PRICE) * 0.01f);
		}
		final String amount = price;
		final String orderNo = _in_pay.GetData(AttName.BILL_NUMBER) + "|" + userInfo.GetData(AttName.USER_ID);
		String beforeSignString = GameSdk.getLoginUid() + "|" + price + "|" + _in_pay.GetData(AttName.BILL_NUMBER) + "|" + _in_pay.GetData(AttName.ITEM_NAME) + "|clientKey";
		String postSign = md5(beforeSignString);
		TypeSDKLogger.i("beforeSignString:" + beforeSignString);
		TypeSDKLogger.i("postSign:" + postSign);
		
		Map<String, String> postMap = new HashMap<String, String>();
		postMap.put("playerid", userInfo.GetData(AttName.USER_ID));
		postMap.put("price", price);
		postMap.put("cporder", _in_pay.GetData(AttName.BILL_NUMBER));
		postMap.put("subject", _in_pay.GetData(AttName.ITEM_NAME));
		postMap.put("sign", postSign.toLowerCase());
		
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
				platform.GetData("url"), new JSONObject(postMap),
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("onResponse:" + jsonObject.toString());
						try {
	        				final String sign = jsonObject.getString("sign");
	        				final String submitTime = jsonObject.getString("create_time");
	        				
	        				Handler mainHandler = new Handler(Looper.getMainLooper());
	        				mainHandler.post(new Runnable() {

	        					@Override
	        					public void run() {

	        						try {					
	        							HashMap<String, Object> map = new HashMap<String, Object>();
	        							map.put(ConstantProgram.TOKEN_ID, GameSdk.getSessionId());
	        							map.put(ConstantProgram.UID, GameSdk.getLoginUid());
	        							map.put(ConstantProgram.APP_ID,
	        									platform.GetData(AttName.APP_ID));
	        							map.put(ConstantProgram.APP_KEY,
	        									platform.GetData(AttName.APP_KEY));
	        							map.put(ConstantProgram.AMOUNT, amount);
	        							map.put(ConstantProgram.PRICE, amount);
	        							map.put(ConstantProgram.NUMBER, "1");
	        							map.put(ConstantProgram.PRODUCT_NAME,
	        									_in_pay.GetData(AttName.ITEM_NAME));
	        							map.put(ConstantProgram.PRODUCT_DES,
	        									_in_pay.GetData(AttName.ITEM_NAME));
	        							map.put(ConstantProgram.PRODUCT_ID,
	        									_in_pay.GetData(AttName.ITEM_SERVER_ID));
	        							map.put(ConstantProgram.PRODUCT_UNIT, "个");
	        							map.put(ConstantProgram.CP_ORDER_ID,
	        									orderNo);
	        							map.put(ConstantProgram.SIGN, sign);//MD5Signature.doSign(platform.GetData(AttName.APP_ID), 
											//_in_pay.GetData(AttName.ITEM_NAME),
											//submitTime,
											//orderNo, 
											//_in_pay.GetData(AttName.ITEM_NAME), 
											//"1", amount, GameSdk.getLoginUid())
//	        							map.put(ConstantProgram.CHANNEL_DIS, "1");
	        							map.put(ConstantProgram.DATA_TIMESTAMP, submitTime);
//	        							map.put(ConstantProgram.GAME_ID, "100");
	        							GameSdk.doPay(appActivity, map,
	        									new CallbackListener() {
	        										@Override
	        										public void callback(int responseCode, Object o) {
	        											// TODO Auto-generated method stub
	        											PayResultData payResult = new PayResultData();
	        											TypeSDKNotify_nubia notify = new TypeSDKNotify_nubia();
	        											switch (responseCode) {
	        											case 0:
	        												// TODO 支付完成
	        												TypeSDKLogger.i("pay_success:");
	        												payResult.SetData(AttName.PAY_RESULT, "1");
	        												payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
	        												notify.Pay(payResult.DataToString());
	        												break;
	        											case 10001:
	        												// TODO 用户取消了本次支付
	        												TypeSDKLogger.w("pay_Cancel");
	        												payResult.SetData(AttName.PAY_RESULT, "2");
	        												payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "PAY_CANCEL");
	        												notify.Pay(payResult.DataToString());
	        												break;
	        											
	        											default:
	        												// TODO 其他所有场景统一处理为支付失败
	        												TypeSDKLogger.e("pay_errorCode:" + responseCode);
	        												payResult.SetData(AttName.PAY_RESULT, "0");
	        												payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
	        												notify.Pay(payResult.DataToString());
	        												break;
	        											}
	        										}
	        									});

	        						} catch (NumberFormatException exception) {
	        							TypeSDKLogger.e("Price input parse error: "
	        									+ exception.toString());
	        						}

	        					}
	        				});
	        		        
	        			} catch (JSONException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError volleyError) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("onErrorResponse:" + volleyError.getMessage());
					}
				});
		mQueue.add(jsonObjectRequest);

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
