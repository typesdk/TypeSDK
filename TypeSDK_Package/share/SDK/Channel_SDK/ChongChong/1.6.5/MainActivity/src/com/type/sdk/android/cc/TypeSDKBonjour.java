package com.type.sdk.android.cc;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.lion.ccpay.sdk.CCPaySdk;
import com.lion.ccsdk.SdkUser;
import com.lion.ccsdk.SdkLoginListener;
import com.lion.ccsdk.SdkLogoutListener;
import com.lion.ccsdk.SdkPayListener;
import com.lion.ccpay.sdk.CCApplicationUtils;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean isDebug = false;
	private static final String PAY_RESULT_CANCEL = "0003";
	private static final String PAY_RESULT_SUCCESS = "0000";

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
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
			TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
			notify.Init();
			return;
		}
		this.ccInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.ccLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.ccLogout();
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
			TypeSDKData.PayInfoData _in_pay)
    {

		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.ccPay(_in_pay);
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

			String extendInfo = new StringBuilder().append("gameId=")
					.append(platform.GetData(AttName.APP_ID))
					.append("&service=")
					.append(userInfo.GetData(AttName.SERVER_NAME))
					.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
					.append("&grade=")
					.append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.i("执行exitGame方法");
		CCPaySdk.getInstance().killApp(appActivity);
			System.exit(0);			

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		TypeSDKLogger.e("appActivity" + appActivity);
		CCPaySdk.getInstance().onResume(appActivity);
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		TypeSDKLogger.e("appActivity" + appActivity);
		CCPaySdk.getInstance().onPause(appActivity);
	}
	
	public void onStart() {
		TypeSDKLogger.e("onStart");
		TypeSDKLogger.e("appActivity" + appActivity);
	   CCPaySdk.getInstance().onStart(appActivity);
    }

	public void onStop() {
		TypeSDKLogger.e("onStop");
		TypeSDKLogger.e("appActivity" + appActivity);
		CCPaySdk.getInstance().onStop(appActivity);
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		TypeSDKLogger.e("appActivity" + appActivity);
		CCPaySdk.getInstance().onDestroy(appActivity);
	}
	
	public void onRestart() {
	   TypeSDKLogger.e("onRestart");
	   TypeSDKLogger.e("appActivity" + appActivity);
	   CCPaySdk.getInstance().onRestart(appActivity);
    }
	
	public void onNewIntent(Intent intent) {
	   TypeSDKLogger.e("onNewIntent");
	   TypeSDKLogger.e("appActivity" + appActivity);
	   CCPaySdk.getInstance().handleIntent(appActivity, intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	   TypeSDKLogger.e("onActivityResult");
	   TypeSDKLogger.e("appActivity" + appActivity);
	   CCPaySdk.getInstance().onActivityResult(appActivity, requestCode, resultCode, data);
    }

	private void ccInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				
				TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
				notify.Init();
				isInit = true;
				
				CCPaySdk.getInstance().setOnLoginOutListener(new SdkLogoutListener() {

					@Override
					public void onLoginOut() {
						TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
						notify.Logout();
				        TypeSDKLogger.e("logout_SUCCESS");
					}
				});

				TypeSDKLogger.e("init_SUCCESS");

			}

		});

		TypeSDKLogger.e("init done");

	}

	private void ccLogin() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("login begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				TypeSDKLogger.e("appActivity" + appActivity);
				// TODO Auto-generated method stub
				CCPaySdk.getInstance().login(appActivity, true, new SdkLoginListener() {

					@Override
					public void onLoginSuccess(SdkUser user) {
						//ToastUtils.showLongToast(activity, "登录成功\n" + "uid:" + user.uid + "\ntoken:" + user.token + "\nuserName:" + user.userName);
						TypeSDKLogger.e("login_SUCCESS");
						TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
						notify.sendToken(user.uid, user.token);
					}

					@Override
					public void onLoginFail(String message) {
						//ToastUtils.showLongToast(activity, "登录失败~");
					}

					@Override
					public void onLoginCancel() {
						//ToastUtils.showLongToast(activity, "登录取消~");
					}

				});
			}
		});
	}

	private void ccLogout() {
		TypeSDKLogger.e("logout_start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				CCPaySdk.getInstance().logout(appActivity);
					TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
					notify.Logout();
				   TypeSDKLogger.e("logout_SUCCESS");
			}
		});

	}
    
    @Override
    protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay)
    {
        ccPay(_in_pay);
    };

    private void ccPay(final TypeSDKData.PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String price;
				if (isDebug) {
					price = "0.01";
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE) * 0.01 + "";
				}
				if(itemListData == null){
						TypeSDKLogger.e("itemListData is null");
				}
				String item_id = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));
				String item_server_id = "0"; //_in_pay.GetInt(AttName.ITEM_SERVER_ID);
				if(item_id != null && !item_id.isEmpty()){
					item_server_id = item_id;
					TypeSDKLogger.i("item_server_id:" + item_server_id);
				} else {
					TypeSDKLogger.e("item_id is null  replace with 1");
                    item_server_id = "1";
				}
				TypeSDKLogger.e("price" + price);
				TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
				
				CCPaySdk.getInstance().pay(appActivity, _in_pay.GetData(AttName.BILL_NUMBER),
						_in_pay.GetData(AttName.ITEM_NAME), 
						price, userInfo.GetData(AttName.USER_ID), new SdkPayListener() {
					@Override
					public void onPayResult(int status, String tn, String money) {
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_cc notify = new TypeSDKNotify_cc();
						String text = "";
						switch (status) {
						case SdkPayListener.CODE_SUCCESS://支付成功
							text = "支付成功\n";
							TypeSDKLogger.e("pay_SUCCESS");
									payResult.SetData(AttName.PAY_RESULT, "1");
									payResult.SetData(
											AttName.PAY_RESULT_REASON,
											"SUCCESS");
									notify.Pay(payResult.DataToString());
							break;
						case SdkPayListener.CODE_FAIL://支付失败
							text = "支付失败\n";
							TypeSDKLogger.e("pay_FAIL");
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(
											AttName.PAY_RESULT_REASON, "FAIL");
									notify.Pay(payResult.DataToString());
							break;
						case SdkPayListener.CODE_CANCEL://支付取消
							text = "支付取消\n";
							TypeSDKLogger.e("pay_CANCEL");
									payResult.SetData(AttName.PAY_RESULT, "2");
									payResult
											.SetData(AttName.PAY_RESULT_REASON,
													"CANCEL");
									notify.Pay(payResult.DataToString());
							break;
						case SdkPayListener.CODE_UNKNOWN://支付结果未知
							text = "支付结果未知\n";
							TypeSDKLogger.e("pay_FAIL");
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult
											.SetData(AttName.PAY_RESULT_REASON,
													"UNKNOW");
									notify.Pay(payResult.DataToString());
							break;
						}
						//ToastUtils.showLongToast(activity, text + "status:" + status + "\ntn:" + tn + "\nmoney:" + money);
					}
				});

			}
		});
	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}
}
