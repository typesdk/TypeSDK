/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.android.youlong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.text.TextUtils;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.yx19196.bean.ExtendDataInfo;
import com.yx19196.bean.OrderInfoVo;
import com.yx19196.callback.IAccountSwitchCallback;
import com.yx19196.callback.ILoginDispatcherCallback;
import com.yx19196.callback.IPaymentCallback;
import com.yx19196.callback.IRegisterDispatcherCallback;
import com.yx19196.callback.IExitDispatcherCallback;
import com.yx19196.pay.ClosePaymentCallBack;
import com.yx19196.pay.PayCallBackhandler;
import com.yx19196.utils.Utils;
import com.yx19196.utils.YLGameSDK;

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
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_youlong notify = new TypeSDKNotify_youlong();
			notify.Init();
			return;
		}
		appContext = _in_context;
		appActivity = (Activity) appContext;
		final Activity passContext = (Activity) _in_context;
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() 
		{

			@Override
			public void run() 
			{
				YLGameSDK.initYLsdk(passContext);
				TypeSDKNotify_youlong notify = new TypeSDKNotify_youlong();
				notify.Init();
			}
		});
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() 
		{
			@Override
			public void run() 
			{
				youlongLogin(appActivity);
			}
		});
		
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		TypeSDKLogger.d("ShowPersonCenter");
			
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("ShowToolBar");
		YLGameSDK.bindYxFloat(_in_context); // 调用浮窗
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.d("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.youlongPay(_in_pay);

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
			TypeSDKLogger.i("进入游戏时的角色信息");
			
			ExtendDataInfo data = new ExtendDataInfo();
			data.setUsername(userInfo.GetData(AttName.USER_ID));// 登陆的账号名
			data.setRoleId(userInfo.GetData(AttName.ROLE_ID));// 角色ID
			data.setRoleName(userInfo.GetData(AttName.ROLE_NAME));// 角色名称
			data.setServerNum(userInfo.GetData(AttName.SERVER_ID)); // 区服
			data.setRoleBuildTime(userInfo.GetData(AttName.ROLE_CREATE_TIME));// 时间戳
			YLGameSDK.submitExtendData(appContext, data);
		
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.d("执行ExitGame方法");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				final Activity passContext = (Activity)appContext;
				YLGameSDK.exit(passContext, new IExitDispatcherCallback()
				{
					@Override
					public void onExit(Context context, Intent intent) 
					{
					}
				});
				
				
			}
			
		});
		
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
	}
	
	/**
	 * 关闭支付页面的回调
	 */
	private ClosePaymentCallBack exitPaymentCallBack = new ClosePaymentCallBack() {
		@Override
		public void onClosePay(Context context, String closePay) {
			// 若无需特殊处理，请去掉该Toast提示
			if (closePay.equals("EXIT_PAY")) {
				
//				PayResultData data = new PayResultData();
//				data.SetData(AttName.PAY_RESULT, "0");
//				data.SetData(AttName.PAY_RESULT_REASON, "exit_pay");
//				Toast.makeText(context, "您已退出支付", Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * 支付回调接口对象 支付操作完成后回调 onPaymentFinished(String paramString)
	 * 
	 * @param paramString
	 *            支付结果json {"err_code":"1","err_msg":"支付成功","content":""}
	 *            {"err_code":"0","err_msg":"支付失败","content":""}
	 *            {"err_code":"-1","err_msg":"取消支付","content":""}
	 *            {"err_code":"2","err_msg":"支付结果确认中","content":""}
	 * */
	private IPaymentCallback paymentCallback = new IPaymentCallback()
	{
		@Override
		public void onPaymentFinished(String paramString) {
//			Toast.makeText(TestSDKActivity.this, "支付结果：　" + paramString, Toast.LENGTH_LONG).show();
			PayResultData data = new PayResultData();
			
			TypeSDKData.BaseData resultData = new TypeSDKData.BaseData();
			resultData.StringToData(paramString);
			int resultCode = resultData.GetInt("err_code");
			if(resultCode == 1)
			{
				data.SetData(AttName.PAY_RESULT, "1");
			}
			else 
			{
				data.SetData(AttName.PAY_RESULT, "0");
				data.SetData(AttName.PAY_RESULT_REASON, resultData.GetData("err_msg"));
			}
			
			TypeSDKNotify_youlong notify  = new TypeSDKNotify_youlong();
			notify.Pay(data.DataToString());
		}
	};
	
	/**
	 * 银联支付结果回调，必须重写onActivity
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		PayCallBackhandler.PayResult(requestCode, resultCode, data, appContext);

	}
	
	private void youlongLogin(final Activity _in_context)
	{
		YLGameSDK.login(_in_context, new ILoginDispatcherCallback() 
		{
			@Override
			public void onFinished(Context context, Intent intent) 
			{
				// 登录状态：成功 :Utils.LOGIN_SUCCESS，失败：Utils.LOGIN_FAIL， 取消：Utils.LOGIN_CANCEL
				String state = intent.getStringExtra("state");
				if (!TextUtils.isEmpty(state))
				{
					if (state.equals(Utils.LOGIN_SUCCESS)) { // 登陆成功操作
						TypeSDKLogger.i("Login success");
						TypeSDKNotify_youlong notify = new TypeSDKNotify_youlong();
						notify.sendToken(intent.getStringExtra("token"), intent.getStringExtra("userName"));
						ShowToolBar(_in_context);
					} else if (state.equals(Utils.LOGIN_FAIL)) { // 登陆失败操作
						TypeSDKLogger.e("Login fail");
					} else if (state.equals(Utils.LOGIN_CANCEL)) { // 取消登录操作
						TypeSDKLogger.e("Login cancel");
					}
				}
			}
		}, new IRegisterDispatcherCallback() 
		{
			@Override
			public void onFinished(Context context, Intent intent) 
			{
				// 注册状态：成功 :Utils.REGISTER_SUCCESS，失败：Utils.REGISTER_FAIL
				String state = intent.getStringExtra("state");
					if (state.equals(Utils.REGISTER_SUCCESS)) { // 注册成功操作
						TypeSDKLogger.i("Register success");
						TypeSDKNotify_youlong notify = new TypeSDKNotify_youlong();
						notify.sendToken(intent.getStringExtra("token"), intent.getStringExtra("userName"));
						ShowToolBar(_in_context);					
					} else if (state.equals(Utils.REGISTER_FAIL)) { // 注册失败操作
						TypeSDKLogger.e("Register fail");
					}
//				}
			}
		}, new IAccountSwitchCallback() 
		{
			@Override
			public void onSwitch(Context context, Intent intent) 
			{
				String state = intent.getStringExtra("state");
				if (state.equals(Utils.LOGIN_SWITCH)) { // 用户进行切换操作
					TypeSDKLogger.i("Login switch");
//					Toast.makeText(TestSDKActivity.this, "用户进行账号切换操作", Toast.LENGTH_SHORT).show();
//					Log.i("***IAccountSwitchCallback***", "IAccountSwitchCallback");
					//TypeSDKNotify_youlong notify = new TypeSDKNotify_youlong();
					//notify.reLogin(intent.getStringExtra("token"), intent.getStringExtra("userName"));

					
				} else if (state.equals(Utils.LOGIN_SWITCH_NONE)) { // 用户未进行切换
					TypeSDKLogger.i("Login switch none");
				}
				
			}
		});
	}
	

	private void youlongPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					
					int realPrice = _in_pay.GetInt(AttName.REAL_PRICE)/100;
					// 提交的订单数据
					OrderInfoVo orderInfo = new OrderInfoVo();

					orderInfo.setUserName(userInfo.GetData(AttName.USER_ID));
					orderInfo.setOrder(_in_pay.GetData(AttName.BILL_NUMBER));
					orderInfo.setServerNum(_in_pay.GetData(AttName.SERVER_ID));
					orderInfo.setPlayerName(userInfo.GetData(AttName.ROLE_NAME));
					orderInfo.setAmount(""+realPrice); // 金额为不能小于1的整数！
					orderInfo.setExtra(_in_pay.GetData(AttName.EXTRA));
					orderInfo.setProductName(_in_pay.GetData(AttName.ITEM_NAME));

					final Activity passActivity = (Activity) appContext;
					// 调用支付接口，打开支付页面
					YLGameSDK.performPay(passActivity, orderInfo, paymentCallback, exitPaymentCallBack);

				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
				}

			}
		});

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
