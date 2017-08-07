package com.type.sdk.android.qtld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.ledi.floatwindow.util.FloatView.KeyBackListener;
import com.ledi.util.CallbackListener;
import com.ledi.util.LoadPayCallBackLinstener;
import com.ledi.util.Operate;
import com.ledi.util.Operate.QuitListener;
import com.ledi.util.PayCallBack;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public static String UId = "";
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// 执行init的动作
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
			notify.Init();
			return;
		}
		this.qtldInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// 执行login的动作
		ShowLog("do sdk show login");
		super.ShowLogin(_in_context, _in_data);
		this.qtldLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// 执行 登出函数
		ShowLog("do sdk logout");
		// 登出行为成功后 发送通知
		TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
		notify.Logout();

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
		Operate.showFloatView((Activity) (_in_context), 0, 10, true,
				new KeyBackListener() {

					@Override
					public void onClickBack(boolean arg0) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("点击返回游戏按钮");
					}

				});
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// 隐藏工具气泡
		ShowLog("do sdk hide tool bar");
		Operate.destoryFloatView(_in_context);
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// 调用支付协议
		TypeSDKData.PayInfoData payData = new TypeSDKData.PayInfoData();

		payData.StringToData(_in_data);
		ShowLog("do sdk pay Item data: " + payData.DataToString());
		// do pay ........
		this.qtldPay(payData);
		// 反馈订单号
		return payData.GetData(AttName.BILL_NUMBER);
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
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Operate.finishGame(appContext, new QuitListener() {

						@Override
						public void isComeForum(boolean arg0) {
							// TODO Auto-generated method stub
							if (arg0) {
								// 进入论坛
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("http://bbs.44755.com"));
								appContext.startActivity(intent);
							} else {
								TypeSDKData.PayResultData exitResult = new TypeSDKData.PayResultData();
								exitResult.SetData("exitreason", "exit game");
								TypeSDKEventManager
										.Instance()
										.SendUintyEvent(
												ReceiveFunction.MSG_EXITGAMECANCEL,
												exitResult.DataToString());
								appActivity.finish();
								System.exit(0);
							}
						}

					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});			
		}
		

	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// 上传
		try {
			userInfo.StringToData(_in_data);

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// 上传玩家数据
		ShowLog("do sdk SetPlayerInfo data:" + _in_data);
	}

	private void ShowLog(String _info) {
		TypeSDKLogger.i(_info);
	}
	
	public void onDestroy() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("onDestroy");
	}

	// qtld init
	private void qtldInit() {

		TypeSDKLogger.e("init begin");
		TypeSDKLogger.e("getPackageName:" + appActivity.getPackageName());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					Operate.init(appActivity, platform.GetData(AttName.APP_ID),
							appActivity.getPackageName(),
							"com.type.sdk.android.qtld.MainActivity", callback,
							mPayCallBackLinstener);
					isInit = true;
					TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
					notify.Init();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void qtldLogin() {

		TypeSDKLogger.e("show login view begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKBonjour bonj = new TypeSDKBonjour();
					if (isInit) {
						com.ledi.util.Operate.startMain(appActivity);

					} else {
						bonj.qtldInit();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("Login done");

	}

	LoadPayCallBackLinstener mPayCallBackLinstener = new com.ledi.util.LoadPayCallBackLinstener() {

		@Override
		public void isPayBack(boolean arg0) {
			// TODO Auto-generated method stub
			TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
			payResult.SetData(AttName.PAY_RESULT, "0");
			payResult.SetData(AttName.PAY_RESULT_DATA, "充值返回");
			TypeSDKEventManager.Instance().SendUintyEvent(
					TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT,
					payResult.DataToString());
			Toast.makeText(appContext, "充值返回", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void isloadBack(boolean arg0) {
			// TODO Auto-generated method stub
			TypeSDKBonjour bonj = new TypeSDKBonjour();
			bonj.qtldLogin();
		}

	};
	CallbackListener callback = new com.ledi.util.CallbackListener() {

		@Override
		public boolean init(boolean arg0) {
			// TODO Auto-generated method stub
			isInit = arg0;
			TypeSDKLogger.e("isLoad-------" + arg0);
			return arg0;
		}

		@Override
		public void loginBackKey(boolean arg0) {
			// TODO Auto-generated method stub
//			WujianyuSDKBonjour_QTLD bonj = new WujianyuSDKBonjour_QTLD();
//			bonj.ExitGame(appContext);
			TypeSDKLogger.e("点击返回按键");
		}

		@Override
		public void loginReback(String arg0, String arg1) {
			// TODO Auto-generated method stub
			TypeSDKBonjour bonj = new TypeSDKBonjour();
			bonj.ShowToolBar(appContext);
			String ssionid = arg0;
			UId = arg0;
			String uid = arg1;
			TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
			notify.sendToken(ssionid, uid);
		}

	};

	private void qtldPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					// _in_pay.GetData(AttName.REAL_PRICE),
					// Integer.parseInt(_in_pay.GetData(AttName.SERVER_ID)),
					TypeSDKLogger.e("Extra:" + _in_pay.GetData(AttName.EXTRA) + "|" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SERVER_ID:" + _in_pay.GetInt(AttName.SERVER_ID));
					
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)*0.01f);
					}
					
					Operate.payMoney(
							_in_pay.GetData(AttName.SERVER_NAME),
							appActivity,
							_in_pay.GetInt(AttName.SERVER_ID),
							price,
							_in_pay.GetData(AttName.BILL_NUMBER),
							new PayCallBack() {
								
								PayResultData payResult = new PayResultData();
								TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
								@Override
								public void payFail(String arg0) {
									// TODO Auto-generated method stub
									
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(AttName.PAY_RESULT_DATA,
											arg0);
									notify.Pay(payResult.DataToString());
								}

								@Override
								public void paySuccess(String arg0, int arg1) {
									// TODO Auto-generated method stub
									payResult.SetData(AttName.PAY_RESULT, ""
											+ arg1);
									payResult.SetData(AttName.PAY_RESULT_DATA,
											arg0);
									notify.Pay(payResult.DataToString());
								}

							});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("pay done");
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
