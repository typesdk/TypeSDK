package com.type.sdk.android.huawei;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.huawei.gameservice.sdk.GameServiceSDK;
import com.huawei.gameservice.sdk.control.GameEventHandler;
import com.huawei.gameservice.sdk.model.Result;
import com.huawei.gameservice.sdk.model.UserResult;
import com.huawei.gameservice.sdk.model.RoleInfo;
import com.huawei.gameservice.sdk.model.PayResult;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.MD5Util;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	private RequestQueue mQueue;
	public Context appContext;
	public Activity appActivity;
	public String jsonResult;
	public String sign;
	public Map<String, String> params;
	public Map<String, Object> payInfo;
	
	private final String HUAWEI_API_PROVIDE  = "com.huawei.gb.huawei.installnewtype.provider";
		
	private boolean isLogin = false;
	/**
     * 浮标密钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    public static String BUO_SECRET = "";
    /**
     * 支付私钥，CP必须存储在服务端，然后通过安全网络（如https）获取下来，存储到内存中，否则存在私钥泄露风险
     */
    public static String PAY_RSA_PRIVATE = "";
    
    
    /**
     * 登录签名公钥
     */
	
	public static final String LOGIN_RSA_PUBLIC = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmKLBMs2vXosqSR2rojMzioTRVt8oc1ox2uKjyZt6bHUK0u+OpantyFYwF3w1d0U3mCF6rGUnEADzXiX/2/RgLQDEXRD22er31ep3yevtL/r0qcO8GMDzy3RJexdLB6z20voNM551yhKhB18qyFesiPhcPKBQM5dnAOdZLSaLYHzQkQKANy9fYFJlLDo11I3AxefCBuoG+g7ilti5qgpbkm6rK2lLGWOeJMrF+Hu+cxd9H2y3cXWXxkwWM1OZZTgTq3Frlsv1fgkrByJotDpRe8SwkiVuRycR0AHsFfIsuZCFwZML16EGnHqm2jLJXMKIBgkZTzL8Z+201RmOheV4AQIDAQAB";
	
	
    /**
     * 支付公钥
     * 已保存在CPSettings中
     * XNZ 2015.12.2
     */
//    public static final String PAY_RSA_PUBLIC = "";
	/**
     * 保存浮标信息
     */
//    public static IBuoyOpenSDK hwBuoy = null;
    
	private class PayParameters
	{
		static final String returnCode= "returnCode";
		static final String errMsg = "errMsg";
		static final String sign = "sign";
	}
    private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		mQueue = Volley.newRequestQueue(appContext);
		if (isInit) {
			TypeSDKLogger.e( "sdk is already init");
			TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
			notify.Init();
			return;
		}
		
		this.huaweiGetInitKey();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.huaweiLogin(1);
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogout");
		this.huaweiLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowToolBar");
		
		GameServiceSDK.showFloatWindow(appActivity);
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "HideToolBar");
		GameServiceSDK.hideFloatWindow(appActivity);
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e( "pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.huaweiPay(_in_pay);

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
		TypeSDKLogger.e( "ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			
	        HashMap<String, String> role = new HashMap<String, String>();
	        
	        /**
	         * 将用户的等级等信息保存起来，必须的参数为RoleInfo.GAME_RANK(等级)/RoleInfo.GAME_ROLE(角色名称)/RoleInfo.GAME_AREA(角色所属区)
	         * 全部使用String类型存放
	         */
	        role.put(RoleInfo.GAME_RANK, userInfo.GetData(AttName.ROLE_LEVEL));
	        role.put(RoleInfo.GAME_ROLE, userInfo.GetData(AttName.ROLE_NAME));
	        role.put(RoleInfo.GAME_AREA, userInfo.GetData(AttName.SERVER_NAME));
	        
	        // 获取用户之前已存储的角色信息
//	        hwBuoy.getRoleInfo(appContext, userInfo.GetData(AttName.USER_ID));
	        // 存储用户当前的角色信息
	        GameServiceSDK.addPlayerInfo(appActivity, role,
					new GameEventHandler(){

						@Override
						public void onResult(Result result) {
							if(result.rtnCode != Result.RESULT_OK){
								TypeSDKLogger.e("add player info failed:" + result.rtnCode);
							}
							
						}
						@Override
						public String getGameSign(String appId, String cpId,
								String ts) {
							return null;
						}
	        			}
						);

		} catch (Exception e) {
			TypeSDKLogger.e( "上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e( "执行ExitGame方法");
		if(exitGameListenser()){
			//清空帐号资源
//			OpenHwID.releaseResouce();
			// 在退出的时候销毁浮标
			GameServiceSDK.destroy(appActivity);
			System.exit(0);	
		}
		
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e( "onResume");
		// 在界面恢复的时候又显示浮标，和onPause配合使用
//        if (isLogin){
        	TypeSDKLogger.e( "showToolBar");
        	this.ShowToolBar(_in_context);
//        }
	}

	public void onPause() {
		TypeSDKLogger.e( "onPause");
		// 在界面暂停的时候，隐藏浮标，和onResume配合使用
//		if (isLogin) {
			TypeSDKLogger.e( "hideToolBar");
			this.HideToolBar(appActivity);
			
//		}
	}

	public void onStop() {
		TypeSDKLogger.e( "onStop");
		this.HideToolBar(appActivity);
	}

	public void onDestroy() {
		TypeSDKLogger.e( "onDestroy");
		//清空帐号资源
//		OpenHwID.releaseResouce();
		// 在退出的时候销毁浮标
		GameServiceSDK.destroy(appActivity);
	}

	private void huaweiInit() 
	{

		GameServiceSDK.init
		(appActivity, 
		platform.GetData(AttName.APP_ID), 
		platform.GetData(AttName.SDK_CP_ID), 
		HUAWEI_API_PROVIDE, 
		new GameEventHandler() 
		{
			@Override
			public void onResult(Result result) 
			{
				if(result.rtnCode != Result.RESULT_OK)
				{
//				handleError("init the game service SDK failed:" + result.rtnCode);
					TypeSDKLogger.e("华为初始化失败 errorcode ：" + result.rtnCode);
				}
				else
				{
					TypeSDKLogger.i( "initSDK_success do huawei check update");
					 huaweiCheckUpdate();
				}
			}
											
			@Override
			public String getGameSign(String appId, String cpId, String ts)
			{
				return createGameSign(appId+cpId+ts);
			}
		}
		);
		TypeSDKLogger.e( "init done");
	}
	
	private void huaweiGetInitKey()
	{
		TypeSDKLogger.e( "init begin");
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
					TypeSDKLogger.e( "initSDK_start");
//					hwBuoy = BuoyOpenSDK.getIntance();
					
					String beforeSignString = "n" + "|" + "n" + "|" + "233" + "|" + "n" + "|clientKey";
					sign = md5(beforeSignString);
					
                    //new DialogHelper().execute(1);
                     
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("playerid", "n");
                	map.put("price", "n");
                    map.put("cporder", "233");
                    map.put("subject", "n");
                    map.put("sign", sign);
            	
                    String huawei_key_url = platform.GetData("huawei_key_url");
                    TypeSDKLogger.i("read huawei key url :"+ huawei_key_url);
            		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, 
            				huawei_key_url, 
            				new JSONObject(map), 
            				new Listener<JSONObject>() {
            					@Override
            					public void onResponse(JSONObject jsonObject) {
            						// TODO Auto-generated method stub
            						TypeSDKLogger.e("onResponse:" + jsonObject.toString());
									try {
										BUO_SECRET = jsonObject.getString("data");
										
										huaweiInit();
										
										TypeSDKLogger.e( "BUO_SECRET:" +BUO_SECRET);
										// 初始化成功
										
										
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
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e( "init done");
	}
	
	private void huaweiCheckUpdate()
	{
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(
				new Runnable() 
		{

			@Override
			public void run() 
			{
				GameServiceSDK.checkUpdate( appActivity, new GameEventHandler()
				{
					@Override
					public void onResult(Result result) 
					{ //返回检测结果，如果用户取消更新，则rtnCode返回RESULT_ERR_CANCLE
						if(Result.RESULT_OK ==result.rtnCode
						||Result.RESULT_ERR_CANCEL == result.rtnCode)
						{
							TypeSDKLogger.i( "huawei check update success code:"+ result.rtnCode);
							isInit = true;
		                     TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
		                     notify.Init();
						}
						else
						{
							TypeSDKLogger.e( "huawei check update fail code:"+ result.rtnCode);
						}
						
					}
				
					@Override
					public String getGameSign(String appId, String cpId, String ts)
					{ 
						//返回游戏签名，签名算法见4.1章节
						return createGameSign(appId+cpId+ts);
					}
				} );
			}
		});
	}
	
	private void huaweiLogin(final int authType ) 
	{

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() 
			{
				
				if (isInit) 
				{
					GameServiceSDK.login(appActivity, new GameEventHandler()
					{
						@Override
						public void onResult(Result result) 
						{
							UserResult userResult = (UserResult) result;
							TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
			               
			                if (userResult.rtnCode == Result.RESULT_ERR_NOT_INIT){ 
								huaweiInit();
								return; 
							}
			                else  if (result.rtnCode != Result.RESULT_OK)
			                {
			                    TypeSDKLogger.e("login failed:" + result.toString());
			                    return; 
							}
			                else 
			                {
										  
			                    if(userResult.isAuth != null && userResult.isAuth == 1)
			                    {
			                    	//SDK通知游戏需要对鉴权签名进行校验时，此 参数返回1
			                    	TypeSDKLogger.i("huawei userResult :"+userResult.toString());
			                    	userInfo.SetData(AttName.USER_NAME, userResult.displayName);	
			                    	notify.sendToken(userResult.gameAuthSign, userResult.playerId,userResult.ts);
			                    	ShowToolBar(appActivity);
			                    	
			                    }else if(userResult.isChange != null && userResult.isChange == 1){
			                    	// 收到SDK的帐号变更通知，退出游戏重新登录
			                    	userInfo.SetData(AttName.USER_NAME, userResult.displayName);
			                    	userInfo.SetData(AttName.USER_ID, userResult.playerId);
			                    	userInfo.SetData(AttName.USER_TOKEN,userResult.gameAuthSign+userResult.ts);
			                    	HideToolBar(appContext);
			                    	notify.Logout();;
			                    }else
			                    {
			                        TypeSDKLogger.e("login success:" + userResult.toString());
			                        
			                    }
			                   
			                    
			                }
						}

						@Override
						public String getGameSign(String appId, String cpId, String ts){
							return createGameSign(appId+cpId+ts);
						}
					},
					authType
					);
				}
				else 
				{
//					Toast.makeText(appContext, "未初始化", Toast.LENGTH_LONG).show();
					TypeSDKLogger.e("未初始化");
					initSDK(appActivity, null);
				}
				
			}
		});

	}
		
	private void huaweiLogout() {
		HideToolBar(appContext);
		TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
		notify.Logout();
	}

	
	
	private void huaweiPay(final PayInfoData _in_pay) {

		final Handler mainHandler = new Handler(Looper.getMainLooper());

				try {
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)*0.01f);
					}
					
					TypeSDKLogger.e( "amount:" + new DecimalFormat("0.00").format((long)price));
					TypeSDKLogger.e( "pay_start");
					TypeSDKLogger.e("SDK_CP_ID" + platform.GetData(AttName.SDK_CP_ID));
					TypeSDKLogger.e("APP_ID" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.e( "ITEM_NAME:" + MD5Util.replaceBlank( _in_pay.GetData(AttName.ITEM_NAME)));
					TypeSDKLogger.e( "ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e( "BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e( "REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
					TypeSDKLogger.e( "PAY_CALL_BACK_URL:" + platform.GetData(AttName.PAY_CALL_BACK_URL));					
					
					params = new HashMap<String, String>();
			        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
			        params.put("userID", platform.GetData(AttName.SDK_CP_ID));  //PAY_ID
			        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
			        params.put("applicationID", platform.GetData(AttName.APP_ID));
			        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
					
			        params.put("amount", new DecimalFormat("0.00").format((long)price));
			        // 必填字段，不能为null或者""，道具名称
			        params.put("productName", MD5Util.replaceBlank( _in_pay.GetData(AttName.ITEM_NAME)));
			        // 必填字段，不能为null或者""，道具描述
			        params.put("productDesc", _in_pay.GetData(AttName.ITEM_DESC));
			        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
			        params.put("requestId", _in_pay.GetData(AttName.BILL_NUMBER));
			        
			        
			        
			        payInfo = new HashMap<String, Object>();
			        // 必填字段，不能为null或者""
			        payInfo.put("amount", new DecimalFormat("0.00").format((long)price));
			        // 必填字段，不能为null或者""
			        payInfo.put("productName", MD5Util.replaceBlank( _in_pay.GetData(AttName.ITEM_NAME)));
			        // 必填字段，不能为null或者""
			        payInfo.put("requestId", _in_pay.GetData(AttName.BILL_NUMBER));
			        // 必填字段，不能为null或者""
			        payInfo.put("productDesc", _in_pay.GetData(AttName.ITEM_DESC));
			        // 必填字段，不能为null或者""，请填写自己的公司名称
			        payInfo.put("userName", platform.GetData("company"));
			        // 必填字段，不能为null或者""
			        payInfo.put("applicationID", platform.GetData(AttName.APP_ID));
			        // 必填字段，不能为null或者""
			        payInfo.put("userID", platform.GetData(AttName.SDK_CP_ID));  //PAY_ID
			        
			        payInfo.put("notifyUrl", platform.GetData(AttName.PAY_CALL_BACK_URL));
			        
			        // 必填字段，不能为null或者""，此处写死X6
			        payInfo.put("serviceCatalog", "X6");
			        
			        // 调试期可打开日志，发布时注释掉
//			        payInfo.put("showLog", true);
			        
			        // 设置支付界面横竖屏，默认竖屏
			        payInfo.put("screentOrient", 2);  //支付页面横竖屏参数：1表示竖屏，2表示横屏
			        
			        payInfo.put("extReserved", userInfo.GetData(AttName.USER_ID));
			        
			        TypeSDKLogger.d("支付请求参数 : " + payInfo.toString());
			        
			        String beforeSignString = "253" + "|" + "n" + "|" + "n" + "|" + "n" + "|clientKey";
					sign = md5(beforeSignString);
			        
//			        new DialogHelper().execute(2);
					Map<String, String> map = new HashMap<String, String>();
                    map.put("playerid", "253");
                	map.put("price", "n");
                    map.put("cporder", "n");
                    map.put("subject", "n");
                    map.put("sign", sign);	  
					TypeSDKLogger.e("map:" + map);
            		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, 
            				platform.GetData("huawei_key_url"), 
            				new JSONObject(map), 
            				new Listener<JSONObject>() {
            					@Override
            					public void onResponse(JSONObject jsonObject) {
            						// TODO Auto-generated method stub
            						TypeSDKLogger.e("onResponse:" + jsonObject.toString());
									try {
										PAY_RSA_PRIVATE = jsonObject.getString("data");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
            						TypeSDKLogger.e( "PAY_RSA_PRIVATE: " + PAY_RSA_PRIVATE);
            						
            						String noSign = getSignData(params);
            				        TypeSDKLogger.e("noSign" + noSign);
//            				        TypeSDKLogger.e("sing()" + Rsa.sign(noSign, PAY_RSA_PRIVATE));
            				        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
            				        String sign = RSAUtil.sign(noSign, PAY_RSA_PRIVATE);
            				        TypeSDKLogger.d("签名： " + sign);
            				        payInfo.put("sign", sign);
            				        TypeSDKLogger.e("payInfo:sign" + payInfo.get("sign"));
            				        
            				        mainHandler.post(new Runnable() {
										
										@Override
										public void run() {
											// TODO Auto-generated method stub
		            				        /**
		            				         * 开始支付
		            				         */
											TypeSDKLogger.e("payInfo:" + payInfo.toString());
		            				        GameServiceSDK.startPay(appActivity, payInfo, new GameEventHandler() {

												@Override
												public String getGameSign(
														String appId,
														String cpId, String ts) {
													// TODO Auto-generated method stub
													return createGameSign(appId+cpId+ts);
												}

												@Override
												public void onResult(Result result) {
													// TODO Auto-generated method stub
													// TODO Auto-generated method stub
		            								PayResultData payResult = new PayResultData();
		            								Map<String, String> payResp = ((PayResult)result).getResultMap();
		            					            // 处理支付结果
		            					            String pay = "支付未成功！";
		            					            TypeSDKLogger.e( "支付结束，返回码： returnCode=" + payResp.get(PayParameters.returnCode));
		            					            // 支付成功，进行验签
		            					            if ("0".equals(payResp.get(PayParameters.returnCode)))
		            					            {
		            					                if ("success".equals(payResp.get(PayParameters.errMsg)))
		            					                {
		            					                    // 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
		            					                    if (payResp.containsKey("isCheckReturnCode") && "yes".equals(payResp.get("isCheckReturnCode")))
		            					                    {
		            					                        payResp.remove("isCheckReturnCode");
		            					                        
		            					                    }
		            					                    else
		            					                    {// 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
		            					                        payResp.remove("isCheckReturnCode");
		            					                        payResp.remove(PayParameters.returnCode);
		            					                    }
		            					                    // 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
		            					                    String sign = payResp.remove(PayParameters.sign);
		            					                    
		            					                    String noSigna = getSignData(payResp);
		            					                    
		            					                    // 使用公钥进行验签
		            					                    boolean s = true;//Rsa.doCheck(noSigna, sign, platform.GetData("pay_rsa_public"));
		            					                    
		            					                    if (s)
		            					                    {
		            					                        pay = "支付成功！";
		            					                        payResult.SetData(AttName.PAY_RESULT, "1");
		            					                        payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
		            					                    }
		            					                    else
		            					                    {
		            					                        pay = "支付成功，但验签失败！";
		            					                        payResult.SetData(AttName.PAY_RESULT, "0");
		            											payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		            					                    }
		            					                    TypeSDKLogger.d( "支付结束：sign= " + sign + "，待验证字段：" + noSigna + "，Rsa.doChec = " + s);
		            					                }
		            					                else
		            					                {
		            					                	TypeSDKLogger.d( "支付失败 errMsg= " + payResp.get(PayParameters.errMsg));
		            					                	payResult.SetData(AttName.PAY_RESULT, "0");
		            										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		            					                }
		            					            }
		            					            else if ("30002".equals(payResp.get(PayParameters.returnCode)))
		            					            {
		            					                pay = "支付结果查询超时！";
		            					                payResult.SetData(AttName.PAY_RESULT, "0");
		            									payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		            					            }
		            					            TypeSDKLogger.e( " 支付结果 result = " + pay);
		            					            TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
		            					            notify.Pay(payResult.DataToString());
												}
		            						});
										}
									});
            					}
            				}, new Response.ErrorListener() {
            					@Override
            					public void onErrorResponse(VolleyError volleyError) {
            						// TODO Auto-generated method stub
            						TypeSDKLogger.e("onErrorResponse:" + volleyError.getMessage());
            					}
            		});
            		mQueue.add(jsonObjectRequest);
					
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e( "Price input parse error: " + exception.toString());
				}

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

	/**
	 * 生成游戏签名
	 * generate the game sign
	 */
	private String createGameSign(String data){
		
		// 为了安全把浮标密钥放到服务端，并使用https的方式获取下来存储到内存中，CP可以使用自己的安全方式处理
        // For safety, buoy key put into the server and use the https way to get down into the client's memory. 
        // By the way CP can also use their safe approach.
       
		String str = data;
		try {
			String result = RSAUtil.sha256WithRsa(str.getBytes("UTF-8"), BUO_SECRET);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 校验签名
	 * check the
	 */
    protected boolean checkSign(String data, String gameAuthSign)
    {

    	/*
         * 建议CP获取签名后去游戏自己的服务器校验签名
         */
    	/*
         * The CP need to deployed a server for checking the sign.
         */
        try
        {
            return RSAUtil.verify(data.getBytes("UTF-8"), LOGIN_RSA_PUBLIC, gameAuthSign);
        }
        catch (Exception e)
        {
            return false;
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
	
	public static String getSignData(Map<String, String> params)
    {
      StringBuffer content = new StringBuffer();
      
      List keys = new ArrayList(params.keySet());
      Collections.sort(keys);
      for (int i = 0; i < keys.size(); i++)
      {
        String key = (String)keys.get(i);
        if (!"sign".equals(key))
        {
          String value = (String)params.get(key);
          if (value != null) {
            content.append((i == 0 ? "" : "&") + key + "=" + value);
          }
        }
      }
      return content.toString();
    }
	
	/**
	 * 
	 * @author sunzhijun
	 *
	 */
	
//	private class DialogHelper extends AsyncTask<Integer, Void, Integer> 
//	{
//
//		@Override
//		protected Integer doInBackground(Integer... params) {
//			// TODO Auto-generated method stub
//			switch (params[0]) {
//			case 1://初始化
//				try {
//					jsonResult = ApiClient.getBillNo("n", "n", "233", "n", sign, platform.GetData("url"));
//				} catch (CrashHandler e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return 1;
//			case 2://支付
//				try {
//					jsonResult = ApiClient.getBillNo("253", "n", "n", "n", sign, platform.GetData("url"));
//				} catch (CrashHandler e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return 2;
//			default:
//				break;
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//			try {
//				JSONObject jsonObject;
//				jsonObject = new JSONObject(jsonResult);
////				JSONObject data = jsonObject.getJSONObject("data");
//				switch (result) {
//				case 1:
//					BUO_SECRET = jsonObject.getString("data");
//					TypeSDKLogger.e( "BUO_SECRET: " + BUO_SECRET);
//					//浮标初始化
//					int retCode = OpenSDK.init(appActivity, platform.GetData(AttName.APP_ID), platform.GetData(AttName.SDK_CP_ID), BUO_SECRET, new UserInfo() {
//						
//						@Override
//						public void dealUserInfo(HashMap<String, String> userInfo) {
//							// TODO Auto-generated method stub
//							// 用户信息为空，登录失败
//		                    if (null == userInfo)
//		                    {
//		                        TypeSDKLogger.e( "用户信息为null");
//		                    }
//		                    // 使用华为账号登录且成功，进行accessToken验证
//		                    else if ("1".equals((String)userInfo.get("loginStatus")))
//		                    {
//		                        TypeSDKLogger.e( "使用华为账号登录，进行accessToken校验");
//		                        // 保存userID，供用户登录信息显示
////		                        userInfo.get("userID");
//		                        
//		                        BuoyOpenSDK.getIntance().showSmallWindow(appContext);
//		                        TypeSDKLogger.e( "login success");
//		    					TypeSDKLogger.e( "accessToken:" + userInfo.get("accesstoken"));
//		    					TypeSDKLogger.e( "userID:" + userInfo.get("userID"));
//		    					TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
//		    					notify.sendToken(userInfo.get("accesstoken"), userInfo.get("userID"));
//		                        
//		                    }
//						}
//					});
//					
//					// 初始化成功
//                    if(RetCode.SUCCESS == retCode)
//                    {
//                        isInit = true;
//                        TypeSDKLogger.e( "initSDK_success");
//                        TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
//                        notify.Init();
//                    }else {
//                    	Toast.makeText(appContext, "初始化失败", Toast.LENGTH_LONG).show();
//					}
//                    
//    				break;
//				case 2:
//					PAY_RSA_PRIVATE = jsonObject.getString("data");
//					TypeSDKLogger.e( "PAY_RSA_PRIVATE: " + PAY_RSA_PRIVATE);
//					
//					String noSign = HuaweiPayUtil.getSignData(params);
//			        TypeSDKLogger.d("签名参数noSign：" + noSign);
//			        
//			        // CP必须把参数传递到服务端，在服务端进行签名，然后把sign传递下来使用；服务端签名的代码和客户端一致
//			        String sign = Rsa.sign(noSign, PAY_RSA_PRIVATE);
//			        TypeSDKLogger.d("签名： " + sign);
//			        payInfo.put("sign", sign);
//					
//					IHuaweiPay payHelper = new MobileSecurePayHelper();
//			        /**
//			         * 开始支付
//			         */
//			        payHelper.startPay(appActivity, payInfo, new IPayHandler() {
//						
//						@Override
//						public void onFinish(Map<String, String> payResp) {
//							// TODO Auto-generated method stub
//							PayResultData payResult = new PayResultData();
//							TypeSDKLogger.d( "支付结束：payResp= " + payResp);
//				            // 处理支付结果
//				            String pay = "支付未成功！";
//				            TypeSDKLogger.d( "支付结束，返回码： returnCode=" + payResp.get(PayParameters.returnCode));
//				            // 支付成功，进行验签
//				            if ("0".equals(payResp.get(PayParameters.returnCode)))
//				            {
//				                if ("success".equals(payResp.get(PayParameters.errMsg)))
//				                {
//				                    // 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
//				                    if (payResp.containsKey("isCheckReturnCode") && "yes".equals(payResp.get("isCheckReturnCode")))
//				                    {
//				                        payResp.remove("isCheckReturnCode");
//				                        
//				                    }
//				                    else
//				                    {// 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
//				                        payResp.remove("isCheckReturnCode");
//				                        payResp.remove(PayParameters.returnCode);
//				                    }
//				                    // 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
//				                    String sign = payResp.remove(PayParameters.sign);
//				                    
//				                    String noSigna = HuaweiPayUtil.getSignData(payResp);
//				                    
//				                    // 使用公钥进行验签
//				                    boolean s = true;//Rsa.doCheck(noSigna, sign, platform.GetData("pay_rsa_public"));
//				                    
//				                    if (s)
//				                    {
//				                        pay = "支付成功！";
//				                        payResult.SetData(AttName.PAY_RESULT, "1");
//				                    }
//				                    else
//				                    {
//				                        pay = "支付成功，但验签失败！";
//				                        payResult.SetData(AttName.PAY_RESULT, "0");
//										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
//				                    }
//				                    TypeSDKLogger.d( "支付结束：sign= " + sign + "，待验证字段：" + noSigna + "，Rsa.doChec = " + s);
//				                }
//				                else
//				                {
//				                	TypeSDKLogger.d( "支付失败 errMsg= " + payResp.get(PayParameters.errMsg));
//				                	payResult.SetData(AttName.PAY_RESULT, "0");
//									payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
//				                }
//				            }
//				            else if ("30002".equals(payResp.get(PayParameters.returnCode)))
//				            {
//				                pay = "支付结果查询超时！";
//				                payResult.SetData(AttName.PAY_RESULT, "0");
//								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
//				            }
//				            TypeSDKLogger.e( " 支付结果 result = " + pay);
//				            TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
//				            notify.Pay(payResult.DataToString());
//						}
//					});
//					
//					break;
//				}
//			
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//	}

	
}
