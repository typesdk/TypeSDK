/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.shoumeng;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.sdklm.entity.CallbackInfo;
import com.sdklm.entity.SDKInitInfo;
import com.sdklm.entity.SDKLoginInfo;
import com.sdklm.entity.SDKPaymentInfo;
import com.sdklm.listener.OnSDKListener;
import com.sdklm.shoumeng.sdk.game.ExitCallback;
import com.sdklm.shoumeng.sdk.game.ShouMengSDKManager;
import com.sdklm.shoumeng.sdk.game.StateCodes;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKBonjour extends TypeSDKBaseBonjour 
{

	public Context appContext;
	public Activity appActivity;
	private ShouMengSDKManager shoumengSDK;
	
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
		shoumengSDK = ShouMengSDKManager.getInstance(appContext);
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
			notify.Init();
			return;
		}
		
		this.shoumengInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.shoumengLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		shoumengSDK.sdkLogout();
		TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
		notify.Logout();
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
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.shoumengPay(_in_pay);
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
			
			String extendInfo = new StringBuilder()
			.append("gameId=").append(platform.GetData(AttName.APP_ID))
			.append("&service=").append(userInfo.GetData(AttName.SERVER_NAME))
			.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
			.append("&grade=").append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);
			//文档未要求接入此接口（连提都没提到）暂时注销
//			int level = Integer.parseInt(getRoleLevel(userInfo.GetData(AttName.ROLE_LEVEL)));
//			GameRoleInfo roleInfo = new GameRoleInfo();
//			roleInfo.setRoleId(userInfo.GetData(AttName.ROLE_ID));
//			roleInfo.setRoleLevel(level);
//			roleInfo.setRoleName(userInfo.GetData(AttName.ROLE_NAME));
//			roleInfo.setServerId(userInfo.GetData(AttName.SERVER_ID));
//			roleInfo.setServerName(userInfo.GetData(AttName.SERVER_NAME));
//			shoumengSDK.sdkRoleInfo(roleInfo);
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}
	
//	public static String getRoleLevel(String RoleLevel){
//		String countString = "";
//		for (int i = 0; i < RoleLevel.length(); i++) {
//			if ('0' <= RoleLevel.charAt(i) && RoleLevel.charAt(i) <= '9') {
//				countString += RoleLevel.charAt(i);
//			}else {
//				
//			}
//		}
//		TypeSDKLogger.e("getItemName:" + countString);
//		return countString;
//	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			Handler mHandler = new Handler(Looper.getMainLooper());
			mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ShouMengSDKManager.getInstance(appContext).exit(appActivity, new ExitCallback() {
		            @Override
		            public void onCancel() {
		                //  退出弹出框取消
		            	TypeSDKLogger.e("exit_cancel");
		            }

		            @Override
		            public void onExit() {
		                //游戏只须实现资源回收，杀死进程等退出逻辑
		            	shoumengSDK.sdkFloat(false);
		        		System.exit(0);
		            }
		        });
			}
		});			
		}
		
		
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		if(shoumengSDK != null){
			shoumengSDK.onSdkResume(appActivity);
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if(shoumengSDK != null){
			shoumengSDK.onSdkPause(appActivity);
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		shoumengSDK.sdkDestroy();	
	}
	
	OnSDKListener sdkListener = new OnSDKListener() {
		@Override
		public void onPay(int stateCode) {
			// TODO Auto-generated method stub
			PayResultData payResult = new PayResultData();
			TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
			if(StateCodes.PAYMENT_SUCCESS == stateCode){
				TypeSDKLogger.e("pay_PAYMENT_SUCCESS");
				payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
			} else if(StateCodes.PAYMENT_CONFIRMATION == stateCode){
				TypeSDKLogger.e("pay_PAYMENT_CONFIRMATION");
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "CONFIRMATION");
			} else if(StateCodes.PAYMENT_FAIL == stateCode){
				TypeSDKLogger.e("pay_PAYMENT_FAIL");
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "FAIL");
			} else if(StateCodes.PARAMETER_ERROR == stateCode){
				TypeSDKLogger.e("pay_PAYMENT_ERROR");
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "ERROR");
			} else {
				TypeSDKLogger.e("PAY_FAIL");
				payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "FAIL");
			}
			notify.Pay(payResult.DataToString());
		}
		
		@Override
		public void onLogout(int stateCode) {
			// TODO Auto-generated method stub
			if(StateCodes.LOGOUT_SUCCESS == stateCode){
				TypeSDKLogger.e("LOGOUT_SUCCESS");
				TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
				notify.Logout();
			} else {
				TypeSDKLogger.e("LOGOUT_FAIL");
			}
		}
		
		@Override
		public void onLogin(CallbackInfo info, int stateCode) {
			// TODO Auto-generated method stub
			if(StateCodes.LOGIN_SUCCESS == stateCode){
				TypeSDKLogger.e("LOGIN_SUCCESS");
				TypeSDKLogger.e("userId:" + info.getUserId() + "|" + info.getUserAccount());
				TypeSDKLogger.e("token" + info.getTokenInfo());
				shoumengSDK.sdkFloat(true);
				TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
				notify.sendToken(info.getUserId() + "|" + info.getUserAccount() , info.getTokenInfo());
			} else if(StateCodes.LOGIN_FAIL == stateCode){
				TypeSDKLogger.e("LOGIN_FAIL");
			} else if(StateCodes.LOGIN_CANCEL == stateCode){
				TypeSDKLogger.e("LOGIN_CANCEL");
			} else {
				TypeSDKLogger.e("FAIL");
			}
		}
		
		@Override
		public void onInit(int stateCode) {
			// TODO Auto-generated method stub
			if(StateCodes.INIT_SUCCESS == stateCode){
				TypeSDKLogger.e("INIT_SUCCESS");
				isInit = true;
				TypeSDKNotify_shoumeng notify = new TypeSDKNotify_shoumeng();
				notify.Init();
			} else {
				TypeSDKLogger.e("INIT_FAIL");
			}
		}
	};
	
	private void shoumengInit() {

		TypeSDKLogger.e("init begin");
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SDKInitInfo initInfo = new SDKInitInfo();
				initInfo.setContext(appContext);
				initInfo.setDebug(TypeSDKTool.isPayDebug);
				initInfo.setSdkListener(sdkListener);
				JSONObject json = new JSONObject();
				try {
					boolean isScreen = true;
					if(TypeSDKTool.isScreenOriatationPortrait(appContext)){
						isScreen = false;
					}
					json.put("sdkIslandspace", isScreen);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				initInfo.setExtStr(json.toString());
				
				shoumengSDK.sdkInit(initInfo);
			}
		});

		TypeSDKLogger.e("init done");

	}
	
	private void shoumengLogin() {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SDKLoginInfo loginInfo = new SDKLoginInfo();
				loginInfo.setActivity(appActivity);
				shoumengSDK.sdkLogin(loginInfo);
			}
		});
		
	}
	
	private void shoumengPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int price;
				if(TypeSDKTool.isPayDebug){
					price = 1;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
				}
				
				//配置支付参数
				SDKPaymentInfo info = new SDKPaymentInfo();
				info.setCpOrderId(_in_pay.GetData(AttName.BILL_NUMBER));
				//cp订单号长度限制为50
				info.setGameGold(getGameGold(_in_pay.GetData(AttName.ITEM_NAME)));
				//游戏虚拟币名称
				info.setMoney(price);
				//充值金额（单位:元）
				info.setRate(10);
				//游戏币兑换比例SDK
				info.setPayType(1);
				//支付类型1-定额支付0-不定额支付
				info.setProductName(_in_pay.GetData(AttName.ITEM_NAME));
				//商品名称
				info.setRoleId(_in_pay.GetData(AttName.ROLE_ID));
				//角色ID
				info.setRoleName(_in_pay.GetData(AttName.ROLE_NAME));
				//角色名
				//以下参数暂时设置无效
				//info.setCallBackInfo("callbackinfo");//
				//合作商自定义参数，平台收到第三方充值结果之后回传给CP服务器的内容
				//info.setCallBackUrl("http://www.game.com/callback/pay/notify");
				//info.setMoreCharge(1);
				//0-是，其它-否，是否可以连续充值
				JSONObject json = new JSONObject();
				try{
					json.put("serverId" ,_in_pay.GetData(AttName.SERVER_ID));
					//服务器ID必须为数字
				} catch (JSONException e){
					
				}
				info.setExtStr(json.toString());
				shoumengSDK.sdkPay(info);
				
			}
		});
	}
	
	//游戏虚拟币名称
	public static String getGameGold(String itemName){
		String countString = "";
		for (int i = 0; i < itemName.length(); i++) {
			if ('0' <= itemName.charAt(i) && itemName.charAt(i) <= '9') {
				
			} else {
				countString += itemName.charAt(i);
			}
		}
		return countString;
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
