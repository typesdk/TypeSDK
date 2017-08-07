package com.type.sdk.android.zhuoyi;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.zhuoyou.pay.sdk.ZYGameManager;
import com.zhuoyou.pay.sdk.account.UserInfo;
import com.zhuoyou.pay.sdk.entity.PayParams;
import com.zhuoyou.pay.sdk.listener.IZYLoginCheckListener;
import com.zhuoyou.pay.sdk.listener.ZYInitListener;
import com.zhuoyou.pay.sdk.listener.ZYLoginListener;
import com.zhuoyou.pay.sdk.listener.ZYRechargeListener;

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
			TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
			notify.Init();
			return;
		}
		isInit = true;
		this.zhuoyiInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.zhuoyiLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
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
		this.zhuoyiPay(_in_pay);
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
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		ZYGameManager.onDestroy(appContext);
	}
	
	private void zhuoyiInit() {

		TypeSDKLogger.e("init begin");
		
		TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
		notify.Init();
		TypeSDKLogger.e("init_SUCCESS");
		
		TypeSDKLogger.e("init done");

	}
	
	private void zhuoyiLogin() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("login begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				
				ZYGameManager.login(appContext,new ZYLoginListener() {
					
					@Override
					public void logout() {
						TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
						notify.Logout();
						TypeSDKLogger.e("logout success");
						
					}
					@Override
					public void login() {
						TypeSDKLogger.i("login to init begin");
						zhuoyInit();
					}
				}, ZYGameManager.LOIGN_THEME_LANDSCAPE);
				
				
			}
		});
	}
	
	private void zhuoyInit(){
		// TODO Auto-generated method stub
		ZYGameManager.init(appContext, new ZYInitListener() {
			
			@Override
			public void iniSuccess(UserInfo userInfo) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("init_login_SUCCESS");
				TypeSDKLogger.e("userInfo:AccessToken:" + userInfo.getAccessToken());
				TypeSDKLogger.e("userInfo:OpenId:" + userInfo.getOpenId());
				TypeSDKLogger.e("userInfo:NickName" + userInfo.getNickName());
				
				if (null != userInfo) {
					final int userId = userInfo.getOpenId();
					final String token = userInfo.getAccessToken();
					ZYGameManager.loginCheck(appActivity, userInfo.getOpenId(), userInfo.getAccessToken(),
                        new IZYLoginCheckListener() {

						@Override
						public void checkResult(String code, String message) {
							TypeSDKLogger.i("LoginCheck code ： " + code + " ,\n Message : " + message);
							if (!TextUtils.isEmpty(code) && code.equals("0")) {
								// 登录验证通过
								TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
								notify.sendToken(userId, token);
							}
						}
					});
				}
			}
			
			@Override
			public void iniFail(String erroMsg) {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("Login_Fail:errorMsg:" + erroMsg);
			}
			
		});
	}
	
	private void zhuoyiPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int price;
				if(isDebug){
					price = 1;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE)/100;
				}
				
				PayParams parmas = new PayParams();
				parmas.setAmount(price);
				parmas.setExtraParam(_in_pay.GetData(AttName.EXTRA));
				parmas.setOrderId(_in_pay.GetData(AttName.BILL_NUMBER));
				parmas.setPropsName(_in_pay.GetData(AttName.ITEM_NAME));
				
				ZYGameManager.pay(parmas, appContext, new ZYRechargeListener() {
					PayResultData payResutlt = new PayResultData();
					TypeSDKNotify_zhuoyi notify = new TypeSDKNotify_zhuoyi();
					@Override
					public void success(PayParams params, String zyOrderId) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("pay_SUCCESS");
						TypeSDKLogger.e("params:" + params.toString());
						
						payResutlt.SetData(AttName.PAY_RESULT, "1");
						payResutlt.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
						notify.Pay(payResutlt.DataToString());
					}
					
					@Override
					public void fail(PayParams param, String erroMsg) {
						// TODO Auto-generated method stub
						payResutlt.SetData(AttName.PAY_RESULT, "0");
						if(erroMsg != null){
							payResutlt.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, erroMsg);
						} else {
							payResutlt.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "FAIL");
						}
						notify.Pay(payResutlt.DataToString());
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
