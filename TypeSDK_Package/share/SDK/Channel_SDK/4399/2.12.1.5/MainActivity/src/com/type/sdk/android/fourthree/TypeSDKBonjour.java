package com.type.sdk.android.fourthree;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import cn.m4399.operate.OperateCenter;
import cn.m4399.operate.OperateCenter.OnLoginFinishedListener;
import cn.m4399.operate.OperateCenter.OnQuitGameListener;
import cn.m4399.operate.OperateCenter.OnRechargeFinishedListener;
import cn.m4399.operate.OperateCenterConfig;
import cn.m4399.operate.OperateCenterConfig.PopLogoStyle;
import cn.m4399.operate.User;
import cn.m4399.operate.OperateCenterConfig.PopWinPosition;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKData;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private OperateCenter mOpeCenter;
	private boolean isDebug = false;

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
			TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
			notify.Init();
			return;
		}
		this.fourthreeInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		//		super.ShowLogin(_in_context, _in_data);
		super.ShowLogin(_in_context, _in_data);
		this.fourthreeLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.fourthreeLogout();
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

	public String PayItemByData(Context _in_context, PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay.GetData(AttName.BILL_NUMBER);
		this.fourthreePay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		PayInfoData _in_pay = new PayInfoData();
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
				mOpeCenter.shouldQuitGame(appContext, new OnQuitGameListener() {
					@Override
					public void onQuitGame(boolean shouldQuit) {
						if (shouldQuit) {
							TypeSDKLogger.e("onQuitGame");
							appActivity.finish();
							System.exit(0);
						}else {
							TypeSDKLogger.e("notExit");
						}
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
		if(mOpeCenter != null){
			mOpeCenter.destroy();
			mOpeCenter = null;
		}


	}

	private void fourthreeInit() {

		TypeSDKLogger.e("init begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// runActivi.runOnUiThread(new Runnable() {
				// public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// 游戏接入SDK
					mOpeCenter = OperateCenter.getInstance();

					if("debug".equals(platform.GetData("mode"))){
						isDebug = true;
					} else {
						isDebug = false;
					}

					// 配置sdk属性,比如可扩展横竖屏配置
					OperateCenterConfig opeConfig = new OperateCenterConfig.Builder(appContext)
					.setDebugEnabled(isDebug)//设置DEBUG 模式,用于接入过程中开关日志输出，发布前必须设置为false 或删除该行。默认为false。4399有测试白名单
					.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
					.setPopLogoStyle(PopLogoStyle.POPLOGOSTYLE_ONE)//设置悬浮窗样式，现有四种可选
					.setPopWinPosition(PopWinPosition.POS_LEFT)//设置悬浮窗默认显示位置，现有四种可选
					.setSupportExcess(false) //设置服务端是否支持处理超出部分金额，默认为false
					.setGameKey(platform.GetData(AttName.APP_KEY)) //设置AppKey
					.build();
					mOpeCenter.setConfig(opeConfig);

					//初始化SDK，在这个过程中会读取各种配置和检查当前帐号是否在登录中
					//只有在init之后， isLogin()返回的状态才可靠
					mOpeCenter.init(appContext, new OperateCenter.OnInitGloabListener() {

						// 初始化结束执行后回调
						@Override
						public void onInitFinished(boolean isLogin, User userInfo) {
							TypeSDKLogger.e("initSDK_success");

							TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
							notify.Init();

							assert(isLogin == mOpeCenter.isLogin());
							TypeSDKLogger.d("userInfo:" + userInfo);
						}

						// 注销帐号的回调， 包括个人中心里的注销和logout()注销方式
						@Override
						public void onUserAccountLogout(boolean fromUserCenter, int resultCode) {
							String tail = fromUserCenter ? "从用户中心退出" : "不是从用户中心退出";
							TypeSDKLogger.e("已退出账户: " + tail);
							TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
							notify.Logout();
						}

						// 个人中心里切换帐号的回调
						@Override
						public void onSwitchUserAccountFinished(User userInfo) {
							TypeSDKLogger.d("Switch Account: " + userInfo.toString());
							TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
							notify.Logout();
						}
					});

					isInit = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.d("init done");

	}

	private void fourthreeLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				//2015.12.14 xnz 登录前先判断是否有账号登录
				if(mOpeCenter.isLogin()){
					TypeSDKLogger.d("isLogin is true!");
					fourthreeLogout();
				} else {
					mOpeCenter.login(appContext, new OnLoginFinishedListener() {

						@Override
						public void onLoginFinished(boolean success, int resultCode, User userInfo) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("uid:" + userInfo.getUid());
							TypeSDKLogger.e("uerState:" + userInfo.getState());
							TypeSDKLogger.e("resultCode:" + resultCode);
							if (success) {
								TypeSDKLogger.e("login success");
								//								mOpeCenter.setServer("0");
								TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
								notify.sendToken(userInfo.getState(), userInfo.getUid());
							}else {
								//								fourthreeLogin();
								TypeSDKLogger.e("login FAIL");						}

						}
					});
				}
			}
		});

	}


	private void fourthreeLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				mOpeCenter.logout();
				//				TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
				//				notify.Logout();
			}
		});
	}


	private void fourthreePay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("USER_ID" + _in_pay.GetData(AttName.USER_ID));
					TypeSDKLogger.e("SERVER_ID" + _in_pay.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("ITEM_DESC:" + _in_pay.GetData(AttName.ITEM_DESC));
					TypeSDKLogger.e("ITEM_COUNT:" + _in_pay.GetInt(AttName.ITEM_COUNT));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
					}
					mOpeCenter.recharge(appContext, price, _in_pay.GetData(AttName.BILL_NUMBER), 
							_in_pay.GetData(AttName.ITEM_NAME), new OnRechargeFinishedListener() {

						@Override
						public void onRechargeFinished(boolean success, int resultCode, String msg) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("resultCode:" + resultCode);
							TypeSDKLogger.e("msg:" + msg);
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_fourthree notify = new TypeSDKNotify_fourthree();
							if (success) {
								// 支付成功
								TypeSDKLogger.e("pay_success");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							}else {
								TypeSDKLogger.e("return Error");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");								
							}
							notify.Pay(payResult.DataToString());
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
		return"";
	}

}
