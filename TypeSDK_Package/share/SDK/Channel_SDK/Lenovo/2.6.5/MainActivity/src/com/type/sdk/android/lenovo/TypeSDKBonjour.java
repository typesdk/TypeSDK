package com.type.sdk.android.lenovo;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.lenovo.lsf.gamesdk.IAuthResult;
import com.lenovo.lsf.gamesdk.IPayResult;
import com.lenovo.lsf.gamesdk.LenovoGameApi;
import com.lenovo.lsf.gamesdk.GamePayRequest;
import com.unity3d.player.UnityPlayer;

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
			TypeSDKNotify_lenovo notify = new TypeSDKNotify_lenovo();
			notify.Init();
			return;
		}
		this.lenovoInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.lenovoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.lenovoLogout();
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
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.lenovoPay(_in_pay);

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
		return "";//PayItem(_in_context, _in_data, itemListBaseData)
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
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			LenovoGameApi.doQuit(appActivity, new IAuthResult() {
				@Override
				public void onFinished(boolean ret, String data) {
					if(ret){
						TypeSDKLogger.i("exit");
						appActivity.finish();
						System.exit(0);
					}else{
						TypeSDKLogger.i("close");
					}

				}
			});
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
	}

	private void lenovoInit() {

		TypeSDKLogger.e("init begin");

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
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					LenovoGameApi.doInit(appActivity,
							platform.GetData(AttName.APP_ID));
					TypeSDKNotify_lenovo notify = new TypeSDKNotify_lenovo();
					notify.Init();
					isInit = true;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void lenovoLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				getTokenByQuickLogin();
			}
		});

	}

	/**
	 * 后台快捷登录
	 */
	private void getTokenByQuickLogin() {

		// 请不要在回调函数里进行UI操作，如需进行UI操作请使用handler将UI操作抛到主线程
		LenovoGameApi.doAutoLogin(UnityPlayer.currentActivity, new IAuthResult() {
			@Override
			public void onFinished(boolean ret, String data) {
				if (ret) {
					TypeSDKLogger.e("login success");
					TypeSDKLogger.e("login result data:" + data);
					TypeSDKNotify_lenovo notify = new TypeSDKNotify_lenovo();
					notify.sendToken(data);
				} else {
					// 后台快速登录失败(失败原因开启飞行模式、 网络不通等)
					TypeSDKLogger.e("login fail");
				}
			}
		});

	}

	private void lenovoLogout() {
		TypeSDKLogger.i("logout success");
		TypeSDKNotify_lenovo notify = new TypeSDKNotify_lenovo();
		notify.Logout();
	}

	private void lenovoPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("ITEM_LOCAL_ID:" + _in_pay.GetData(AttName.ITEM_LOCAL_ID));
					TypeSDKLogger.e("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
					TypeSDKLogger.e("itemListData_ITEM_SERVER_ID:" + itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)));
					TypeSDKLogger.e("EXTRA:" + _in_pay.GetData(AttName.EXTRA));
					TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetInt(AttName.REAL_PRICE));
					TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					TypeSDKLogger.e("PRODUCT_ID:" + _in_pay.GetData(AttName.PRODUCT_ID));
					/***********
					 * 支付LenovoGameApi.doPay（） 接口 调用
					 */
					GamePayRequest payRequest = new GamePayRequest();
					// 请填写商品自己的参数
					payRequest.addParam("notifyurl", "");// 当前版本暂时不用，传空String
					payRequest.addParam("appid",
							platform.GetData(AttName.APP_ID));
					if(itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)) != null && !itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)).isEmpty()){
						payRequest.addParam("waresid", 
								itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)));
						TypeSDKLogger.i("item_list_id:" + itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID)));
					} else {
						TypeSDKLogger.e("item_id is null");
						payRequest.addParam("waresid", 
								_in_pay.GetData(AttName.ITEM_SERVER_ID));
					}
					payRequest.addParam("exorderno",
							_in_pay.GetData(AttName.BILL_NUMBER));

					int price;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}
					payRequest.addParam("price", price);

					payRequest.addParam("cpprivateinfo",
							userInfo.GetData(AttName.USER_ID));

					LenovoGameApi.doPay(appActivity,
							platform.GetData(AttName.APP_KEY), payRequest,
							new IPayResult() {
						@Override
						public void onPayResult(int resultCode,
								String signValue, String resultInfo) {// resultInfo
							// =
							// 应用编号&商品编号&外部订单号
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_lenovo notify = new TypeSDKNotify_lenovo();
							if (LenovoGameApi.PAY_SUCCESS == resultCode) {
								// 支付成功
								TypeSDKLogger.e("pay_success");
								payResult.SetData(AttName.PAY_RESULT,
										"1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
							} else if (LenovoGameApi.PAY_CANCEL == resultCode) {
								// 取消支付处理，默认采用finish()，请根据需要修改
								TypeSDKLogger.e("return cancel");
								payResult.SetData(AttName.PAY_RESULT,
										"2");
								payResult.SetData(
										AttName.PAY_RESULT_REASON,
										"PAY_CANCEL");
								notify.Pay(payResult.DataToString());
							} else {
								// 计费失败处理，默认采用finish()，请根据需要修改
								TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT,
										"0");
								payResult.SetData(
										AttName.PAY_RESULT_REASON,
										"PAY_FAIL");
								notify.Pay(payResult.DataToString());
							}

						}
					});

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
