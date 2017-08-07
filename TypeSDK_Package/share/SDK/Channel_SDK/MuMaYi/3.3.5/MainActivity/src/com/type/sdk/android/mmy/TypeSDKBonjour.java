package com.type.sdk.android.mmy;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.mumayi.paymentmain.business.FindUserDataListener;
import com.mumayi.paymentmain.business.ResponseCallBack;
import com.mumayi.paymentmain.business.onLoginListener;
import com.mumayi.paymentmain.business.onTradeListener;
import com.mumayi.paymentmain.ui.PaymentCenterInstance;
import com.mumayi.paymentmain.ui.PaymentUsercenterContro;
import com.mumayi.paymentmain.ui.pay.MMYInstance;
import com.mumayi.paymentmain.ui.usercenter.PaymentFloatInteface;
import com.mumayi.paymentmain.util.PaymentConstants;
import com.mumayi.paymentmain.util.PaymentLog;
import com.mumayi.paymentmain.vo.UserBean;
import com.unity3d.player.UnityPlayer;


public class TypeSDKBonjour extends TypeSDKBaseBonjour{

	public Context appContext;
	public Activity appActivity;
	private PaymentCenterInstance instance = null;
	private PaymentUsercenterContro userCenter = null;
	private PaymentFloatInteface floatInteface;
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}
	
	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity)appContext;
		if(isInit){
			TypeSDKLogger.i( "error init do again");
			TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
			notify.Init();
			return;
		}
		mmySdkInit();
	}
	
//	public void finish() {
//		// TODO Auto-generated method stub
//		userCenter.finish();
//	}
	
	public void onResume(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("onResume");
		
    	if (userCenter != null) {
//    		userCenter.checkLogin();
		}
		if(floatInteface!=null){
			floatInteface.show();
		}
	}
	
	public void onPause() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("onPause");
		if(floatInteface!=null){
			floatInteface.close();
		}
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("onDestroy");
		//
        floatInteface.close();
        userCenter.finish();
        //
        instance.exit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.mmySdkLogin();
	}
	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLog");
		this.mmySdkLogout();
	}
	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		userCenter.go2Ucenter();  //进入用户中心
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
	public String PayItemByData(Context _in_context, TypeSDKData.PayInfoData _in_pay)
	{

		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.mmySdkPay(_in_pay);
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
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		try{
			userInfo.StringToData(_in_data);
			
		TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
		userData.StringToData(_in_data);
		JSONObject userJsonExData = new JSONObject();
		userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
		userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
		userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
		userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
		userJsonExData.put("zoneName",userData.GetData(AttName.SERVER_NAME));
		
		TypeSDKLogger.e("上传用户信息:string="+userJsonExData);

		}
		catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:"+e.getMessage());
		}
		
	}
	@Override
	public void ExitGame(Context _in_context) 
	{
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			this.mmySdkExit();			
		}
		
	}
	
	private void mmySdkInit() {
		TypeSDKLogger.e("initSDK_begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
	    mainHandler.post(new Runnable() {
	       @Override
	       public void run() {
	        	try{
	        		TypeSDKLogger.e("initSDK_success");
	        		Handler mainHandler = new Handler(Looper.getMainLooper());
	        	    mainHandler.post(new Runnable() 
	        	    {
	        	       @Override
	        	       public void run() 
	        	       {
	        		// 初始化支付SDK用户中心
	        		instance = PaymentCenterInstance.getInstance(appContext);
	        		instance.initial(TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY), TypeSDKBonjour.Instance().platform.GetData("app_name"));
	        		// 
	        		instance.setTestMode(platform.GetData("mode").equals("debug"));
	        		instance.setTradeListener(new onTradeListener() {
						
						@Override
						public void onTradeFinish(String tradeType, int tradeCode, Intent intent) {
							// TODO Auto-generated method stub
					    	// 可在此处获取到提交的商品信息
					        Bundle bundle = intent.getExtras();
					        String orderId = bundle.getString("orderId");
					        String productName = bundle.getString("productName");
					        String productPrice = bundle.getString("productPrice");
					        String productDesc = bundle.getString("productDesc");
					        TypeSDKData.PayResultData payResult  = new TypeSDKData.PayResultData();
					        TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
					        if (tradeCode == MMYInstance.PAY_RESULT_SUCCESS)
					        {
					            // 在每次支付回调结束时候，调用此接口检查用户是否完善了资料
					            userCenter.checkUserState(appContext);
					            
					            payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
					            
					            TypeSDKLogger.e(productName + "支付成功 支付金额:" + productPrice);
					        }
					        else if (tradeCode == MMYInstance.PAY_RESULT_FAILED)
					        {
					        	TypeSDKLogger.e(productName + "支付失败 支付金额:" + productPrice);
					        	payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "0");
								payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "PAY_FAILED");
								notify.Pay(payResult.DataToString());
					        }
						}
					});
	        		instance.setListeners(new onLoginListener() {
						
						@Override
						public void onLoginOut(String loginOutCallBackStr) {
							// TODO Auto-generated method stub
							try {
								JSONObject json = new JSONObject(loginOutCallBackStr);
								String code = json.getString("loginOutCode");
								if (code.equals("success"))
								{
									String uid = json.getString("uid");
									String name = json.getString("uname");
									TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
					                notify.Logout();
					                TypeSDKLogger.e( "loginout_success");
									// 注销成功
									PaymentLog.getInstance().d("name:" + name + "loginOutCallBackStr>>" + loginOutCallBackStr + " uid:" + uid);
								}
								else
								{
									// 注销失败
									PaymentLog.getInstance().d("loginOutCallBackStr>>" + loginOutCallBackStr);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								PaymentLog.getInstance().E(UnityPlayer.currentActivity.toString(), e);
							}
						}
						
						@Override
						public void onLoginFinish(String loginResult) {
							// TODO Auto-generated method stub
							try {
								if (loginResult != null) {
									JSONObject loginJson = new JSONObject(loginResult);
									String loginState = loginJson
											.getString(PaymentConstants.LOGIN_STATE);
									// 
									if (loginState != null
											&& loginState.equals(PaymentConstants.STATE_SUCCESS)) {
										String uname = loginJson.getString("uname");
										String uid = loginJson.getString("uid");
										String token = loginJson.getString("token");
										String session = loginJson.getString("session");
										PaymentLog.getInstance().d(
												"token>>" + token + "\n session>>" + session);
										TypeSDKLogger.e("uname:" + uname);
										TypeSDKLogger.e("uid:" + uid);
										TypeSDKLogger.e("token:" + token + ";session:" + session);
										TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
										notify.sendToken(token, uid);
									} else {
										// 
										String error = loginJson.getString("error");
										if (error != null && error.trim().length() > 0
												&& error.equals("cancel_login")) {
											//
											PaymentLog.getInstance().d("login_failed:" + error);
											TypeSDKLogger.e("login_failed:" + error);
//											instance.go2Login(_in_context);
										} else if (error != null && error.trim().length() > 0) {
											//
										}
									}

								}
							} catch (JSONException e) {
								// TODO: handle exception
							}
						}
					});
	        		instance.setChangeAccountAutoToLogin(false);
	                userCenter = instance.getUsercenterApi(appContext);
	                
	                instance.findUserData(new FindUserDataListener() {

	                    @Override
	                    public void findUserDataComplete()
	                    {
	                        
	                    }
	                });
	                
	                floatInteface = instance.createFloat();
	                floatInteface.show();
	        	       }
	        	    });
	        		TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
	    			notify.Init();
					isInit= true;
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        		TypeSDKLogger.e("initSDK_Exception");
	        	}
	        }
	    });
	    TypeSDKLogger.e("initSDK_end");
	}
	
	private void mmySdkLogin() {

//        instance.findUserData(new FindUserDataListener() {

//            @Override
//            public void findUserDataComplete()
//            {
            	instance.go2Login(appContext);
//            }
//        });
	}
	
	private void mmySdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
    	mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("CallLogout");
		    	//获取当前用户信息
		    	UserBean user = PaymentConstants.NOW_LOGIN_USER;
		    	
		    	TypeSDKLogger.e("CallLogout:user:" + user);
		    	userCenter.loginOut(appContext,user.getName(),new ResponseCallBack() {
					@Override
					public void onSuccess(Object obj) {
						try
						{
							JSONObject loginoutJson = (JSONObject) obj;
							String loginoutCode = loginoutJson.getString("loginOutCode");
							if (loginoutCode.equals("success"))
							{
								// 注销成功之后回到登录界面
								TypeSDKLogger.e( "logout_success");
								floatInteface.close();
								TypeSDKNotify_MMY notify = new TypeSDKNotify_MMY();
				                notify.Logout();
//				                userCenter.go2Login();
							}
							else
							{
								// 注销失败
								TypeSDKLogger.e("注销失败");
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					
					@Override
					public void onFail(Object obj) {
						//注销失败
					}
				});
			}
		});

	}
	
	private void mmySdkExit() {
		TypeSDKLogger.e("exit_start");
				try {
					TypeSDKLogger.e("exit_success");
					System.exit(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	public String priceTransfer(String string) {
		String s = "";
		if(string.length() > 2){
			s = string.replace(string.substring(string.length()-2), "."+string.substring(string.length()-2));
		}else if(string.length() == 2){
			s = string.replace(string.subSequence(0,1), "0." + string.subSequence(0, 1));
		}else if(string.length() == 1){
			s = "0.0" + string;
		}
		return s;
	}
	
	
	private void mmySdkPay(final TypeSDKData.PayInfoData _in_pay) 
	{
		Handler mainHandler = new Handler(Looper.getMainLooper());
	    mainHandler.post(new Runnable() 
	    {
	       @Override
	       public void run() 
	       {
	    	   
	    	   String price;
	       	if (TypeSDKTool.isPayDebug) {
	   			price = "0.01";
	   		}else {
	   			price = _in_pay.GetInt(AttName.REAL_PRICE) * 0.01 + "";
	   		}
	       	instance.setUserArea(_in_pay.GetData(AttName.SERVER_NAME));
	       	instance.setUserName(_in_pay.GetData(AttName.ROLE_NAME));
	       	instance.setUserLevel(_in_pay.GetInt(AttName.ROLE_LEVEL));
	       	userCenter.pay(appContext, _in_pay.GetData(AttName.ITEM_NAME), 
	    			price, _in_pay.GetData(AttName.BILL_NUMBER));
	       	
	       }
		});

	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		SetPlayerInfo(_in_context, _in_data);
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
