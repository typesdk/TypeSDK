package com.type.sdk.android.pocketbus;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.stargame.pay.bean.StarGameOrder;
import com.stargame.sdk.ResponseCode;
import com.stargame.sdk.StarGamePlatform;
import com.stargame.sdk.StarGamePlatform.OnLoginListener;
import com.stargame.sdk.StarGamePlatform.OnPayProcessListener;
import com.stargame.sdk.bean.Account;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	StarGameOrder order;
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	private OnLoginListener _sdkLoginCBK = new OnLoginListener()
	{
		
		@Override
		public void finishLoginProgress(int code) 
		{
			// TODO Auto-generated method stub
			switch (code) 
			{
			case ResponseCode.PLATFORM_LOGIN_CANCEL:
				TypeSDKLogger.d("PLATFORM_LOGIN_CANCEL");
				ShowLogin(appContext, null);
				break;
			case ResponseCode.PLATFORM_LOGIN_SUCCESS:
			{
				TypeSDKLogger.d("PLATFORM_LOGIN_SUCCESS");
				Account SDKAccount = StarGamePlatform.getInstance().getCurrentAccount(appContext);
//				userInfo.SetData(AttName.USER_ID, String.valueOf(SDKAccount.getUserId()));
				
				TypeSDKNotify_PocketBus notify = new TypeSDKNotify_PocketBus();
				notify.sendToken(SDKAccount.getToken(), "123", SDKAccount.getUserName());
			}
				break;
				
			case ResponseCode.PLATFORM_LOGIN_FAIL:
				TypeSDKLogger.d("PLATFORM_LOGIN_FAIL");
				ShowLogin(appContext ,null);
				break;
			case ResponseCode.PLATFORM_HIDDEN:
				TypeSDKLogger.d("PLATFORM_HIDDEN");
				{
//				switch (StarGamePlatform.getInstance().getLoginStatus(MainActivity.this)) {
//					case Account.NOT_LOGIN:
//						TypeSDKLogger.d("PLATFORM_HIDDEN.NOT_LOGIN");
//						break;
//					case Account.ACCOUNT_LOGIN:
//						TypeSDKLogger.d("PLATFORM_HIDDEN.ACCOUNT_LOGIN");
//						break;
//					case Account.GUEST_LOGIN:
//						TypeSDKLogger.d("PLATFORM_HIDDEN.GUEST_LOGIN");
//						break;
//					default:
//						TypeSDKLogger.d("PLATFORM_HIDDEN.DEFAULT");
//						break;
				}
				break;
			case ResponseCode.PLATFORM_LOGOUT:
				TypeSDKLogger.d("PLATFORM_LOGOUT");
				break;
			default:
				TypeSDKLogger.d("PLATFORM_LOGIN_ERROR_CODE");
				ShowLogin(appContext, null);
				break;
			}
		}
	
	};
	
	private OnPayProcessListener _sdkPayCBK = new OnPayProcessListener()
	{
		
		@Override
		public void finishPayProcess(int code) 
		{
			TypeSDKLogger.e("code:" + code);
			PayResultData payResult = new PayResultData();
			TypeSDKNotify_PocketBus notify = new TypeSDKNotify_PocketBus();
			payResult.SetData(AttName.PAY_RESULT, "0");
			payResult.SetData(AttName.PAY_RESULT_REASON, "code "+ code);
			TypeSDKLogger.e("sdkResult_data:" + payResult.DataToString());
			switch (code) 
			{
			case ResponseCode.PLATFORM_ORDER_SUCCESS:
				
				TypeSDKLogger.d("下单成功");
				
				break;
			case ResponseCode.PLATFORM_ORDER_FAIL:
				
				TypeSDKLogger.d("下单失败");
				break;
			case ResponseCode.PLATFORM_ORDER_PAY_SUCCESS:
				
				TypeSDKLogger.d("支付成功");
				
				payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
				break;
			case ResponseCode.PLATFORM_ORDER_PAY_FAIL:
			
				TypeSDKLogger.d("支付失败");
				break;
			default:
				TypeSDKLogger.d("error code: " + code);
				break;
			}
			
			notify.Pay(payResult.DataToString());
		}
	};
	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_PocketBus notify = new TypeSDKNotify_PocketBus();
			notify.Init();
			return;
		}
		
		isInit =  true;
		
		StarGamePlatform.getInstance().initAppInfo(_in_context, 
									platform.GetData(AttName.APP_ID),  
									platform.GetData(AttName.SECRET_KEY),  
									"1");
		
		TypeSDKLogger.e("sdk is init complete");
		TypeSDKNotify_PocketBus notify = new TypeSDKNotify_PocketBus();
		notify.Init();
		TypeSDKLogger.e("sdk is init success");
	}

	@Override
	public void SdkLogin(Context _in_context) {
		appContext = _in_context;
		Handler dialogHandler = new Handler(Looper.getMainLooper());
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				StarGamePlatform.getInstance().loginForGame(appContext,
						_sdkLoginCBK, false);
			}
		});
	};

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		StarGamePlatform.getInstance().logoutAccount(_in_context);
		TypeSDKNotify_PocketBus notify = new TypeSDKNotify_PocketBus();
		notify.Logout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
		StarGamePlatform.getInstance().startAccountHome(_in_context);
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
		
		int price;
		if(TypeSDKTool.isPayDebug){
			price = 1;
		}else{
			price = _in_pay.GetInt(AttName.REAL_PRICE);
		}
		order = new StarGameOrder();
		order.setOrderNo(_in_pay.GetData(AttName.BILL_NUMBER));
		order.setPrice(price);
		order.setDescription(_in_pay.GetData(AttName.ITEM_DESC));
		order.setServerId(_in_pay.GetInt(AttName.SERVER_ID));
		order.setExt(_in_pay.GetData(AttName.EXTRA));
		
//		StarGameOrder order = new StarGameOrder();
//		order.setOrderNo("0123456789");
//		order.setPrice(1);
//		order.setDescription("itemdesc");
//		order.setServerId(1);
//		order.setExt("extradata");
		
		TypeSDKLogger.e("start call sdk pay function");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("order:NO:" + order.getOrderNo() );
				TypeSDKLogger.e("Price:" + order.getPrice());
				TypeSDKLogger.e("Description:" + order.getDescription());
				TypeSDKLogger.e("ServerID:" + order.getServerId());
				TypeSDKLogger.e("Ext:" + order.getExt());
				
				StarGamePlatform.getInstance().payment(appContext, order, _sdkPayCBK);
			}
		});
		
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		TypeSDKLogger.i("payitem data: "+ _in_data);
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

	public void onResume() {
		TypeSDKLogger.e("onResume");
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() 
	{
		TypeSDKLogger.e("onDestroy");
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
