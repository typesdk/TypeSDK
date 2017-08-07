package com.type.sdk.android.paojiao;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.paojiao.sdk.PJSDK;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.bean.UserBean;
import com.paojiao.sdk.listener.LoginListener;
import com.paojiao.sdk.listener.LogoutListener;
import com.paojiao.sdk.listener.PayListener;
import com.paojiao.sdk.listener.UploadPlayInfoListener;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKBonjour extends TypeSDKBaseBonjour 
{

	public Context appContext;
	public Activity appActivity;
	private boolean isDebug;
	
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
		isDebug = TypeSDKTool.isPayDebug;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
			notify.Init();
			return;
		}
		this.paojiaoInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.paojiaoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.paojiaoLogout();
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
		this.paojiaoPay(_in_pay);
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
			
			/*
			 * roleName 玩家角色名称 roleLevel 玩家等级 roleServer 玩家所在区服 roleMoney
			 * 玩家在游戏里面的帐户余额
			 */
			// 构造玩家信息
			RoleInfo roleInfo = new RoleInfo(userInfo.GetData(AttName.ROLE_NAME), 
					Integer.parseInt(userInfo.GetData(AttName.ROLE_LEVEL)) , 
					userInfo.GetData(AttName.SERVER_NAME), 
					0);
			// 调用API发送数据
			PJSDK.uploadPlayInfo(roleInfo, new UploadPlayInfoListener() {

				@Override
				public void onSuccess(String msg) {
					super.onSuccess(msg);
					TypeSDKLogger.e("upladPlayInfo_success" + msg);
				}
				@Override
				public void onFailure(String msg) {
					super.onFailure(msg);
					TypeSDKLogger.e("upladPlayInfo_Fail" + msg);
				}
			});
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			System.exit(0);			
		}
		
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			PJSDK.showFloatingView();
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			PJSDK.hideFloatingView();
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		PJSDK.destroy();
	}
	
	private void paojiaoInit() {

		TypeSDKLogger.e("init begin");
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PJSDK.initialize(appContext, Integer.parseInt(platform.GetData(AttName.APP_ID)), platform.GetData(AttName.APP_KEY), true);
				PJSDK.setLogoutListener(new LogoutListener() {
					
					@Override
					public void onLogout() {
						TypeSDKLogger.e("logout_SUCCESS");
						TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
						notify.Logout();
					}
				});
				isInit = true;
				TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
				notify.Init();
				TypeSDKLogger.e("init_SUCCESS");
				
			}
			
		});
		
		TypeSDKLogger.e("init done");

	}
	
	private void paojiaoLogin() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("login begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PJSDK.doLogin(new LoginListener() {

					public void onSuccess (UserBean user) {
						TypeSDKLogger.e("login_SUCCESS");
						TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
						notify.sendToken(user.getUid() ,user.getToken());
					}

					public void onFailure() {
						TypeSDKLogger.e("onFailure");
						
					}
				});
			}
		});
	}
	
	private void paojiaoLogout(){
		TypeSDKLogger.e("logout begin");
		
		TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
		notify.Logout();

	}
	
	private void paojiaoPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				float price;
				if(isDebug){
					price = 0.1f;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE)/100;
				}
				
				PJSDK.doPay(_in_pay.GetData(AttName.ITEM_NAME), 
						price, _in_pay.GetData(AttName.ITEM_DESC), 
						_in_pay.GetData(AttName.BILL_NUMBER), 
						new PayListener() {
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_paojiao notify = new TypeSDKNotify_paojiao();
							@Override
							public void onPaySuccess() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("pay_Success");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
								notify.Pay(payResult.DataToString());
							}
							
							@Override
							public void onPayFailure() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("onPayFailure");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "Fail");
								notify.Pay(payResult.DataToString());
							}
							
							@Override
							public void onPayCancel() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("pay_Cancel");
								payResult.SetData(AttName.PAY_RESULT, "2");
								payResult.SetData(AttName.PAY_RESULT_REASON, "Cancel");
								notify.Pay(payResult.DataToString());
							}
						});
				
			}
		});
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
