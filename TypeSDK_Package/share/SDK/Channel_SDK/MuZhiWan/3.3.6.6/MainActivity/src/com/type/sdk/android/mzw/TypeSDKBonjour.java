package com.type.sdk.android.mzw;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.muzhiwan.sdk.core.MzwSdkController;
import com.muzhiwan.sdk.core.callback.MzwInitCallback;
import com.muzhiwan.sdk.core.callback.MzwLoignCallback;
import com.muzhiwan.sdk.core.callback.MzwPayCallback;
import com.muzhiwan.sdk.service.MzwOrder;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKBonjour extends TypeSDKBaseBonjour 
{

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
			TypeSDKNotify_MZW notify = new TypeSDKNotify_MZW();
			notify.Init();
			return;
		}
		this.MZWInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");		
		super.ShowLogin(_in_context, _in_data);
		this.muzhiwanLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		MzwSdkController.getInstance().doLogout();
		TypeSDKNotify_MZW notify = new TypeSDKNotify_MZW();			
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
		this.muzhiwanPay(_in_pay);
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

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		MzwSdkController.getInstance().destory();
	}

	/**
	 * 3.0.9 init xnz
	 * 2015.11.30
	 */
	private void MZWInit() {

		TypeSDKLogger.e("init begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				boolean isProt = TypeSDKTool.isScreenOriatationPortrait(appContext);
				int protvalue ;
				if(isProt){
					protvalue = MzwSdkController.ORIENTATION_VERTICAL;
				} else {
					protvalue = MzwSdkController. ORIENTATION_HORIZONTAL;
				}
		
				MzwSdkController.getInstance().init(appActivity,
					protvalue, new MzwInitCallback() {
					
					@Override
					public void onResult(int code, String msg) {
						// TODO Auto-generated method stub
						if (code == 1){
							TypeSDKLogger.e("initSDK_success:msg" + msg);
							TypeSDKNotify_MZW notify = new TypeSDKNotify_MZW();
							notify.Init();
							isInit = true;
						} else {
							TypeSDKLogger.e("initSDK_fail:msg:" + msg);
						}
					}
				});
			}
		});
		
		

		TypeSDKLogger.e("init done");

	}
	
	/**
	 * 3.0.9 login xnz
	 * 2015.11.30
	 */
	private void muzhiwanLogin() {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				MzwSdkController.getInstance().doLogin(new MzwLoignCallback() {
			
					@Override
					public void onResult(int code, String msg) {
						// TODO Auto-generated method stub
						if(code == 1){
							TypeSDKLogger.e("Login_success:msg:token:" + msg);
							TypeSDKNotify_MZW notify = new TypeSDKNotify_MZW();
							notify.sendToken(msg);
							
						} else {
							TypeSDKLogger.e("Login_fail:msg:" + msg);
						}
					}
				});
			}
		});
	}
	
	/**
	 * 3.0.9 pay xnz
	 * 2015.11.30
	 * @param _in_pay
	 */
	private void muzhiwanPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
		TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
		TypeSDKLogger.e("ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
		TypeSDKLogger.e("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
		TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int price;
				if(TypeSDKTool.isPayDebug){
					price = 1;
				}else{
					price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)/100);
				}
				MzwOrder order = new MzwOrder();
				order.setExtern(_in_pay.GetData(AttName.BILL_NUMBER));
				order.setMoney(price);//
				order.setProductdesc(_in_pay.GetData(AttName.ITEM_NAME));//渠道无法解析ITEM_DESC
				order.setProductid(_in_pay.GetData(AttName.ITEM_SERVER_ID));
				order.setProductname(_in_pay.GetData(AttName.ITEM_NAME));
				TypeSDKLogger.e("order" + order);
				MzwSdkController.getInstance().doPay(order, new MzwPayCallback() {
					
					@Override
					public void onResult(int code, MzwOrder order) {
						// TODO Auto-generated method stub
						
						TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
						TypeSDKNotify_MZW notify = new TypeSDKNotify_MZW();
						
						if(code == 1){
							TypeSDKLogger.e("Pay_success code:" + code);
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						} else if(code == 5) {
							TypeSDKLogger.e("Pay_server_end code:" + code);
						} else if(code == -1){
							TypeSDKLogger.e("Pay_server_start code:" + code);
						} else if(code == 0){
							TypeSDKLogger.e("Pay_fail code:" + code);
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}
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
