package com.type.sdk.android.yyh;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.appchina.usersdk.Account;
import com.appchina.usersdk.ErrorMsg;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.yyh.sdk.AccountCallback;
import com.yyh.sdk.CPInfo;
import com.yyh.sdk.LoginCallback;
import com.yyh.sdk.PayParams;
import com.yyh.sdk.PayResultCallback;
import com.yyh.sdk.YYHSDKAPI;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	// TypeSDKEventManager evtMg = TypeSDKEventManager.Instance();
	public Context appContext;
	public Activity appActivity;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		YYHSDKAPI.setDebugModel(false);
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();
			notify.Init();
			return;
		}

		this.yyhInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// 执行login的动作
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		appActivity = (Activity) _in_context;
		this.yyhLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// 执行 登出函数
		YYHSDKAPI.logout();
		//TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();
		//notify.Logout();

	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// 显示用户中心
		ShowLog("do sdk show person center");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// 隐藏用户中心
		ShowLog("do sdk hide person center");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// 显示工具气泡
		ShowLog("do sdk show tool bar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// 隐藏工具气泡
		ShowLog("do sdk hide tool bar");
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// 调用支付协议
		TypeSDKData.PayInfoData payData = new TypeSDKData.PayInfoData();

		payData.StringToData(_in_data);
		ShowLog("do sdk pay Item data: " + payData.DataToString());
		this.yyhPay(payData);
		return payData.DataToString();
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		// 调用购买指定商品协议
		ShowLog("do sdk ExchangeItem");
		PayItem(_in_context, _in_data);
		return null;
	}

	@Override
	public int LoginState(Context _in_context) {
		// 返回登陆状态
		ShowLog("get login state ");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// 调用分享接口
		ShowLog("do sdk show share data:" + _in_data);

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			// 退出游戏
			ShowLog("do sdk exit game");
			System.exit(0);			
		}
		

	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// 上传
		ShowLog("do sdk SendInfo data:" + _in_data);
		SetPlayerInfo(_in_context, _in_data);
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// 上传玩家数据
		ShowLog("do sdk SetPlayerInfo data:" + _in_data);
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("onDestroy");

	}

	public void onPause() {
		TypeSDKLogger.e("onPause");

	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
	}

	private void ShowLog(String _info) {
		TypeSDKLogger.i(_info);
	}

	private void yyhInit() {
		TypeSDKLogger.e("init begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					CPInfo cpinfo = getCpinfo();
					//开启启动页，闪屏页需要安装要求在最外层调用，最少3秒中
					YYHSDKAPI.startSplash(appActivity, cpinfo.orientation, 3000);
					YYHSDKAPI.init(appActivity, cpinfo, new AccountCallback() {
						TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();

						@Override
						public void onSwitchAccount(Account preAccount,
								Account crtAccount) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("SDK_SwitchAccount");
							// 切换账号回调
							TypeSDKLogger.e("onSwitchAccount");
							notify.reLogin(crtAccount.ticket, crtAccount.userId
									+ "");
						}

						@Override
						public void onLogout() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("SDK_Logout");
							notify.Logout();
						}
					});
					TypeSDKLogger.e("init_SUCEESS");
					isInit = true;
					TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();
					notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");
	}

	private void yyhLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			public void run() {
				if (isInit) {
					YYHSDKAPI.login(appActivity, new LoginCallback() {

						@Override
						public void onLoginCancel() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("onLoginCancel");
						}

						@Override
						public void onLoginSuccess(Activity arg0,
								Account account) {
							TypeSDKLogger.i(account.ticket+account.userId+"");
							// 登录成功
							TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();
							notify.sendToken(account.ticket, account.userId
									+ "");
							// 展示悬浮框
							YYHSDKAPI.showToolbar(true);
						}

						@Override
						public void onLoginError(Activity arg0, ErrorMsg arg1) {
							// TODO Auto-generated method stub
							// 登录失败
							TypeSDKLogger.e("onLoginError");
						}
					});
				} else {
					yyhInit();
				}
			}
		});
	}

	private void yyhPay(final TypeSDKData.PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {

					int price = 0;
					if (TypeSDKTool.isPayDebug) {
						price = 1;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
					}
					
					if(itemListData == null){
						TypeSDKLogger.e("itemListData is null");
					}
					String item_id = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));
					int item_server_id = 0; //_in_pay.GetInt(AttName.ITEM_SERVER_ID);
					if(item_id != null && !item_id.isEmpty()){
						item_server_id = Integer.parseInt(item_id);
						TypeSDKLogger.i("item_server_id:" + item_server_id);
					} else {
						TypeSDKLogger.e("item_id is null");
					}

					PayParams payParam = new PayParams();
					payParam.buildWaresid(item_server_id)
					.buildWaresName(_in_pay.GetData(AttName.ITEM_NAME))
					.buildCporderid(_in_pay.GetData(AttName.BILL_NUMBER))
					.buildPrice(price)
					.buildCpprivateinfo(_in_pay.GetData(AttName.EXTRA));
					YYHSDKAPI.startPay(appActivity, payParam, new PayResultCallback() {
						PayResultData resultData = new PayResultData();
						TypeSDKNotify_yyh notify = new TypeSDKNotify_yyh();
						@Override
						public void onPaySuccess(int arg0, String arg1) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("pay_Success");
							resultData.SetData(AttName.PAY_RESULT, "1");
							resultData.SetData(
									AttName.PAY_RESULT_REASON,
									"SUCCESS");
							notify.Pay(resultData.DataToString());
						}
						
						@Override
						public void onPayFaild(int arg0, String arg1) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("pay_FAIL:errorCode:"
									+ arg1 + "errorMsg:"
									+ arg0);
							resultData.SetData(AttName.PAY_RESULT, "0");
							resultData.SetData(
									AttName.PAY_RESULT_REASON, "FAIL");
							notify.Pay(resultData.DataToString());
						}
					});

				} catch (NumberFormatException exception) {
					TypeSDKLogger.e(exception.toString());
				}

			}
		});
	}

	CPInfo getCpinfo() {
		CPInfo cpinfo = new CPInfo();
		cpinfo.needAccount = true;
		cpinfo.loginId = Integer.parseInt(platform.GetData("product_id"));
		cpinfo.loginKey = platform.GetData(AttName.APP_KEY);
		cpinfo.appid = platform.GetData(AttName.APP_ID);
		cpinfo.publicKey = platform.GetData("public_key");
		cpinfo.privateKey = platform.GetData("private_key");
		cpinfo.orientation = CPInfo.LANDSCAPE; // 横竖屏设置
		return cpinfo;
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

}
