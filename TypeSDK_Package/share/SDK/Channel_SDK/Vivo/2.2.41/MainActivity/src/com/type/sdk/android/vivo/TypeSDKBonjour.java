package com.type.sdk.android.vivo;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

import com.vivo.unionsdk.open.VivoAccountCallback;
import com.vivo.unionsdk.open.VivoExitCallback;
import com.vivo.unionsdk.open.VivoPayCallback;
import com.vivo.unionsdk.open.VivoPayInfo;
import com.vivo.unionsdk.open.VivoRoleInfo;
import com.vivo.unionsdk.open.VivoUnionSDK;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	private RequestQueue mQueue;
	public Context appContext;
	public Activity appActivity;
	//String KEY_SWITCH_ACCOUNT = "switchAccount";
	private boolean isLogin = false;

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
		mQueue = Volley.newRequestQueue(appContext);
		if (isInit) {
			TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
			notify.Init();
			return;
		}
		this.vivoInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.vivoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.vivoLogout();		
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
		//获取订单号
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.vivoPay(_in_context, _in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKLogger.i(_in_data);
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		//将传进来的支付字符串转换为JSON数据map集合里
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
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SendInfo");
		try {

			userInfo.StringToData(_in_data);

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName", userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.e("上传用户信息:string=" + userJsonExData);
			this.userInfo.CopyAttByData(userData);

			//记录用户角色信息
			VivoUnionSDK.reportRoleInfo(new VivoRoleInfo(userData.GetData(AttName.ROLE_ID), userData.GetData(AttName.ROLE_LEVEL), 
					userData.GetData(AttName.ROLE_NAME), userData.GetData(AttName.SERVER_ID), userData.GetData(AttName.SERVER_NAME)));


		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			VivoUnionSDK.exit(appActivity, new VivoExitCallback() {
			
			@Override
			public void onExitConfirm() {
				// TODO Auto-generated method stub
				appActivity.finish();
			}
			
			@Override
			public void onExitCancel() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("onExitCancel");
			}
		});
					
		}
		
	}

	public void onResume(Context context) {
		TypeSDKLogger.e("onResume");
		//调用悬浮窗显示接口
		if(isLogin){
		}
		

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



	private void vivoInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("APP_ID:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					TypeSDKLogger.e("SDK_CP_ID:" + platform.GetData(AttName.SDK_CP_ID));
					
					VivoUnionSDK.registerAccountCallback(appActivity, new VivoAccountCallback() {
			
			@Override
			public void onVivoAccountLogout(int code) {
				// TODO Auto-generated method stub
				isLogin = false;
				TypeSDKNotify_vivo notify_vivo = new TypeSDKNotify_vivo();
				notify_vivo.Logout();
			}
			
			@Override
			public void onVivoAccountLoginCancel() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("onVivoAccountLoginCancel");
			}
			
			@Override
			public void onVivoAccountLogin(String userName, String openId, String authtoken) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("Login success");
			TypeSDKLogger.e("name="+userName+", openid="+openId+", authtoken="+authtoken);
			isLogin = true;
			TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
			notify.sendToken(authtoken , openId);
			}
		});
					TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
					notify.Init();
					//					VivoAccountManager.vivoAccountStartAssistView(appContext);
					TypeSDKLogger.e("init Success");
					isInit = true;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		TypeSDKLogger.e("init done");

	}

	private void vivoLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				//调用登录接口
				VivoUnionSDK.login(appActivity);

				//				Intent loginIntent = new Intent(appContext, LoginActivity.class);
				//				//				loginIntent.putExtra(KEY_SHOW_TEMPLOGIN, false);
				//				appActivity.startActivityForResult(loginIntent, MainActivity.REQUEST_CODE_LOGIN);
			}
		});
	}

	//	protected void ShowToolBar(int requestCode, int resultCode, Intent data) {
	//		Bundle extras = data.getBundleExtra("pay_info");
	//		String trans_no = extras.getString("transNo");
	//		boolean pay_result = extras.getBoolean("pay_result");
	//		String res_code = extras.getString("result_code");
	//		String pay_msg = extras.getString("pay_msg");
	//		PayResultData payResult = new PayResultData();
	//		TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
	//		if (res_code.equals("9000")){
	//			payResult.SetData(AttName.PAY_RESULT, "1");
	//			payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
	//					"SUCCESS");
	//			notify.Pay(payResult.DataToString());
	//		} else {
	//			payResult.SetData(AttName.PAY_RESULT, "0");
	//			payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
	//			notify.Pay(payResult.DataToString());
	//		}
	//	}


	private void vivoLogout(){
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				//切换帐号 xnz 2015.11.30
				//Intent swithIntent = new Intent(appContext, LoginActivity.class);
				//swithIntent.putExtra(KEY_SWITCH_ACCOUNT, true);
				//appActivity.startActivityForResult(swithIntent, MainActivity.REQUEST_CODE_LOGIN);
				isLogin = false;
				TypeSDKNotify_vivo notify_vivo = new TypeSDKNotify_vivo();
				notify_vivo.Logout();
				//				mVivoAccountManager.removeAccount();
			}
		});
	}

	public static String md5(String string) {

		byte[] hash;

		try {

			hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {

			throw new RuntimeException("Huh, MD5 should be supported?", e);

		} catch (UnsupportedEncodingException e) {

			throw new RuntimeException("Huh, UTF-8 should be supported?", e);

		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {

			if ((b & 0xFF) < 0x10) hex.append("0");

			hex.append(Integer.toHexString(b & 0xFF));

		}

		return hex.toString();

	}


	String productName;
	String orderNo;
	int realPrice;
	String jsonResult;
	String billNo;
	String beforeSignString;
	String sign;
	String accessKey;
	String productDes;
	String extra;
	String userId;
	String submit_time;

	//支付
	private void vivoPay(Context _in_context, PayInfoData _in_pay) {
		appContext = _in_context;
		appActivity = (Activity) appContext;
		TypeSDKLogger.v("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
		TypeSDKLogger.v("EXTRA:" + _in_pay.GetData(AttName.EXTRA));
		TypeSDKLogger.v("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));

		extra = _in_pay.GetData(AttName.EXTRA);//
		userId = userInfo.GetData(AttName.USER_ID);//用户ID
		productName = _in_pay.GetData(AttName.ITEM_NAME);//商品名称

		if(TypeSDKTool.isPayDebug){
			realPrice = 1;
		}else{
			realPrice = (int)(_in_pay.GetInt(AttName.REAL_PRICE));
		}
		//realPrice = _in_pay.GetInt(AttName.REAL_PRICE)*100;//价格
		productDes = _in_pay.GetData(AttName.ITEM_DESC);//商品描述
		orderNo = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);//SharkSDK.Instance().ExchangeItem(_in_context, _in_data);//商品订单
		beforeSignString = userId + "|" + realPrice + "|" + orderNo + "|" + productName + "|clientKey";
		sign = md5(beforeSignString);
		TypeSDKLogger.e("beforeSignString:" + beforeSignString);
		TypeSDKLogger.e("orderNo:" + orderNo);
		TypeSDKLogger.e("sign:" + sign);
		TypeSDKLogger.e("realPrice:" + realPrice);
		TypeSDKLogger.e("productDes:" + productDes);
		TypeSDKLogger.e("extra:" + extra);

		//new DialogHelper().execute(1);

		Map<String, String> map = new HashMap<String, String>();
		map.put("playerid", userId);
		map.put("price", realPrice+"");
		map.put("cporder", orderNo);
		map.put("subject", productName);
		//加密订单号全转换为小写
		map.put("sign", sign.toLowerCase());
		TypeSDKLogger.e("map:" + map);
		TypeSDKLogger.e("url" + platform.GetData("url"));
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, 
				platform.GetData("url"), 
				new JSONObject(map), 
				new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("onResponse:" + jsonObject.toString());
				try {
					billNo = jsonObject.getString("order");
					submit_time = jsonObject.getString("submit_time");
					JSONObject data = jsonObject.getJSONObject("data");
					accessKey = data.getString("accessKey");
					//							TypeSDKLogger.e("signature:" + data.getString("signature"));
					//TypeSDKLogger.e("billNo:" + billNo);
					//TypeSDKLogger.e("accessKey:" + accessKey);

					//String packageName = appActivity.getPackageName();//获取应用的包名
					//TypeSDKLogger.e("packageName:" + packageName);

					TypeSDKLogger.e("----------------------");
					TypeSDKLogger.e("transNo:" + billNo);
					TypeSDKLogger.e("accessKey:" + accessKey);
					TypeSDKLogger.e("appId:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.e("productName:" + productName);
					TypeSDKLogger.e("productDes:" + productDes);
					TypeSDKLogger.e("price:" + realPrice);
//					TypeSDKLogger.e("SERVER_NAME:" + userInfo.GetData(AttName.SERVER_NAME));
//					TypeSDKLogger.e("ROLE_NAME:" + userInfo.GetData(AttName.ROLE_NAME));
//					TypeSDKLogger.e("ROLE_LEVEL:" + userInfo.GetData(AttName.ROLE_LEVEL));
//					TypeSDKLogger.e("ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
//					
					//Intent target = new Intent(appContext, PaymentActivity.class);
					//调用支付接口进行支付
					VivoPayInfo info = new VivoPayInfo(productName, productDes, "" + realPrice, accessKey, platform.GetData(AttName.APP_ID), billNo, userInfo.GetData(AttName.USER_ID));
					VivoUnionSDK.pay(appActivity, info, new VivoPayCallback() {
			
			@Override
			public void onVivoPayResult(String transNo, boolean isSucc, String errorCode) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("transNo:" + transNo);
				TypeSDKLogger.e("isSucc:" + isSucc);
				TypeSDKLogger.e("errorCode:" + errorCode);
				PayResultData payResult = new PayResultData();
			TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
				if(errorCode.equals("0")){
					payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
						"SUCCESS");
				notify.Pay(payResult.DataToString());
				} else {
					payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				notify.Pay(payResult.DataToString());
				}
			}
		});
					//					appActivity.startActivityForResult(target, MainActivity.REQUEST_CODE_PAY);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("onErrorResponse:" + volleyError.getMessage());
			}
		});
		mQueue.add(jsonObjectRequest);

	}


	//	private class DialogHelper extends AsyncTask<Integer, Void, Integer> {
	//
	//		@Override
	//		protected Integer doInBackground(Integer... params) {
	//			// TODO Auto-generated method stub
	//			switch (params[0]) {
	//			case 1:
	//				try {
	//					jsonResult = ApiClient.getBillNo(userId, realPrice+"", orderNo, productName, sign.toLowerCase(), platform.GetData("url"));
	//				} catch (CrashHandler e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//				break;
	//
	//			default:
	//				break;
	//			}
	//			return null;
	//		}

	//		@Override
	//		protected void onPostExecute(Integer result) {
	//			// TODO Auto-generated method stub
	//			super.onPostExecute(result);
	//			JSONObject jsonObject;
	//			try {
	//
	//				jsonObject = new JSONObject(jsonResult);
	//				billNo = jsonObject.getString("order");
	//				submit_time = jsonObject.getString("submit_time");
	//				JSONObject data = jsonObject.getJSONObject("data");
	//				accessKey = data.getString("accessKey");
	//				//				TypeSDKLogger.e("signature:" + data.getString("signature"));
	//				TypeSDKLogger.e("billNo:" + billNo);
	//				TypeSDKLogger.e("accessKey:" + accessKey);
	//				String packageName = appActivity.getPackageName();//获取应用的包名
	//				TypeSDKLogger.e("packageName:" + packageName);
	//				 platform.GetData(AttName.APP_ID);
	//				Bundle localBundle = new Bundle();
	//				localBundle.putBoolean("logOnOff", true);
	//				localBundle.putString("transNo", billNo);// 交易流水号，由订单推送接口返回
	//				localBundle.putString("accessKey", accessKey);// 签名信息，由订单推送接口返回
	//				localBundle.putString("package", packageName); //在开发者平台创建应用时填写的包名，务必一致，否则SDK界面不会被唤起
	//				//			localBundle.putString("submit_time", submit_time); //
	//				//			localBundle.putString("signature", data.getString("signature")); //
	//				localBundle.putString("userId", userId);//vivo账户id，不允许为空
	//				localBundle.putString("uid", userId);//vivo账户id，不允许为空
	//				localBundle.putString("useMode", "00");//固定值
	//				localBundle.putString("productName", productName);//商品名称
	//				localBundle.putString("productDes", productDes);//商品描述
	//				localBundle.putLong("price", realPrice);//价格
	//				localBundle.putString("appId", platform.GetData(AttName.APP_ID));//appid为vivo开发者平台中生成的App ID
	//				localBundle.putInt("level", userInfo.GetInt(AttName.ROLE_LEVEL));//角色等级 可选参数
	//				localBundle.putString("roleId", userInfo.GetData(AttName.ROLE_ID));//角色id 可选参数
	//				//			localBundle.putString("roleName", userInfo.GetData(AttName.ROLE_NAME));//角色名称 可选参数
	//				localBundle.putString("serverName", userInfo.GetData(AttName.SERVER_ID));//区服信息 可选参数
	//				localBundle.putString("extInfo", extra);//扩展参数 可选参数
	//				TypeSDKLogger.e("----------------------");
	//				TypeSDKLogger.e("userId:" + userId);
	//				TypeSDKLogger.e("appId:" + platform.GetData(AttName.APP_ID));
	//				TypeSDKLogger.e("roleId:" + userInfo.GetData(AttName.ROLE_ID));
	//				TypeSDKLogger.e("level:" + userInfo.GetInt(AttName.ROLE_LEVEL));
	//				TypeSDKLogger.e("roleName:" + userInfo.GetData(AttName.ROLE_NAME));
	//				TypeSDKLogger.e("serverName:" + userInfo.GetData(AttName.SERVER_ID));
	//				Intent target = new Intent(appContext, PaymentActivity.class);
	//				target.putExtra("payment_params", localBundle);
	//				appActivity.startActivityForResult(target, MainActivity.REQUEST_CODE_PAY);
	//
	//			} catch (JSONException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//
	//	}

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
