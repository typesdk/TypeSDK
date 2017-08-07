package com.type.sdk.android.gfan;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.mappn.sdk.Gfan;
import com.mappn.sdk.gfanpay.GfanPay;
import com.mappn.sdk.gfanpay.GfanPayResult;
import com.mappn.sdk.init.InitControl;
import com.mappn.sdk.uc.LoginControl;
import com.mappn.sdk.uc.LoginResult;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	
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
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_gfan notify = new TypeSDKNotify_gfan();
			notify.Init();
			return;
		}
		this.gfanInit(_in_context);
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.gfanLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.gfanLogout();
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
		this.gfanPay(_in_pay);
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
		TypeSDKLogger.e("执行ExitGame方法");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if(exitGameListenser()){
					System.exit(0);	
				}
			}
		});
	}

	public void onResume() {
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
		
	}

	private void gfanInit(Context _in_context) {

		TypeSDKLogger.d("init begin");
		Gfan.init(_in_context, new InitControl.Listener() {
            @Override
            public void onComplete() {
            	TypeSDKNotify_gfan notify = new TypeSDKNotify_gfan();
    			notify.Init();
                gfanLogin();
            }
        });
		TypeSDKLogger.d("init done");

	}

	private void gfanLogin() {
		Gfan.login(appActivity,new LoginControl.Listener(){

		    @Override
		    public void onComplete(LoginResult result) {
		        switch (result.getLoginType()) {
		            case Common:
		            	gfanSendToken(result);
		                break;
		            case Quick:
		            	gfanSendToken(result);
		                break;
		        }
		    }
		});
	}
	
	private void gfanSendToken(LoginResult result) {
		TypeSDKNotify_gfan notify = new TypeSDKNotify_gfan();
		notify.sendToken(result.getToken(), result.getUserId());
	}

	private void gfanLogout() {
	}
	
	private void gfanPay(final PayInfoData _in_pay) {

		int price = 0;
		if(platform.GetData("mode").equals("debug")){
			price = 1;
			TypeSDKLogger.e("price=" + price);
		}else{
			price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)*0.01f);
			TypeSDKLogger.e("price=" + price);
		}
		Looper.prepare();
		Gfan.pay(appActivity,
				_in_pay.GetData(AttName.BILL_NUMBER),
				price, 
				_in_pay.GetData(AttName.ITEM_NAME),
				_in_pay.GetData(AttName.ITEM_DESC),
				_in_pay.GetData(AttName.BILL_NUMBER),
				new GfanPay.Listener(){
		    @Override
		    public void onComplete(GfanPayResult result) {
		    	PayResultData payResult = new PayResultData();
				TypeSDKNotify_gfan notify = new TypeSDKNotify_gfan();
		        switch (result.getStatusCode()) {
		            case Success:
		            	payResult.SetData(AttName.PAY_RESULT, "1");
						payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
						notify.Pay(payResult.DataToString());
		                break;
		            case UserBreak:
		            	payResult.SetData(AttName.PAY_RESULT, "0");
						payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
						notify.Pay(payResult.DataToString());
		                break;
		            case Fail:
		            	payResult.SetData(AttName.PAY_RESULT, "0");
						payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
						notify.Pay(payResult.DataToString());
		                break;
		        }
		    }
		});
		Looper.loop();
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
