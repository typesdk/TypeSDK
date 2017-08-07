package com.type.sdk.android.ouwan;

import net.ouwan.umipay.android.api.AccountCallbackListener;
import net.ouwan.umipay.android.api.ExitDialogCallbackListener;
import net.ouwan.umipay.android.api.GameParamInfo;
import net.ouwan.umipay.android.api.GameRolerInfo;
import net.ouwan.umipay.android.api.GameUserInfo;
import net.ouwan.umipay.android.api.InitCallbackListener;
import net.ouwan.umipay.android.api.PayCallbackListener;
import net.ouwan.umipay.android.api.UmipayFloatMenu;
import net.ouwan.umipay.android.api.UmipaySDKManager;
import net.ouwan.umipay.android.api.UmipaySDKStatusCode;
import net.ouwan.umipay.android.api.UmipaymentInfo;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	UmipaymentInfo paymentInfo;

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
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
			notify.Init();
			return;
		}

		this.OuWaninit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub		
		super.ShowLogin(_in_context, _in_data);
		TypeSDKLogger.e("ShowLogin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				UmipaySDKManager.showLoginView(appContext);
			}
		});

	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				UmipaySDKManager.logoutAccount(appContext, "logoutAccount");// 登出账户接口//登出账户接口
			}
		});
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
		UmipaySDKManager.showAccountManagerView(_in_context);// 调用账号中心接口
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
		if(UmipayFloatMenu.getInstance().create((Activity) _in_context)){
			UmipayFloatMenu.getInstance().show((Activity) _in_context);
		}
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
		UmipayFloatMenu.getInstance().hide((Activity) _in_context);
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("pay begin:" + _in_pay.DataToString());
		String _in_OrderID = _in_pay.GetData(AttName.BILL_NUMBER);
		int price = 0;
		if (TypeSDKTool.isPayDebug) {
			price = 1;
		} else {
			price = (int) (_in_pay.GetInt(AttName.REAL_PRICE) * 0.01f);
		}
		paymentInfo = new UmipaymentInfo();
		// 业务类型，SERVICE_TYPE_QUOTA(固定额度模式，充值金额在支付页面不可修改)，SERVICE_TYPE_RATE(汇率模式，充值金额在支付页面可修改）
		paymentInfo.setServiceType(UmipaymentInfo.SERVICE_TYPE_QUOTA);
		// 定额支付金额，单位“元”
		paymentInfo.setPayMoney(price);
		// 订单描述
		paymentInfo.setDesc(_in_pay.GetData(AttName.ITEM_NAME));
		// 【可选】外部订单号
		paymentInfo.setTradeno(_in_pay.GetData(AttName.BILL_NUMBER));
		paymentInfo.setRoleGrade(userInfo.GetData(AttName.ROLE_LEVEL)); // 【必填】设置用户的游戏角色等级
		paymentInfo.setRoleId(userInfo.GetData(AttName.ROLE_ID));// 【必填】设置用户的游戏角色的ID
		paymentInfo.setRoleName(userInfo.GetData(AttName.ROLE_NAME));// 【必填】设置用户的游戏角色名字
		paymentInfo.setServerId(_in_pay.GetData(AttName.SERVER_ID));// 【必填】设置用户所在的服务器ID
		paymentInfo.setCustomInfo(_in_pay.GetData(AttName.BILL_NUMBER)+"|"+userInfo.GetData(AttName.USER_ID));// 【可选】游戏开发商自定义数据。该值将在用户充值成功后，在服务端充值回调接口通知给游戏开发商时携带该数据.透传参数默认开启唯一性检查,必须确认传入参数是否唯一，如不唯一，请联系偶玩技术申请关闭唯一性检查，否则会导致传入重复参数的订单支付失败。

		Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				UmipaySDKManager.showPayView(appContext, paymentInfo,
						new PayCallbackListener() {
							@Override
							public void onPay(int code) {
								TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
								TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
								// 该接口只表示充值流程完成与否，充值结果需以服务器回调为准
								if (code == UmipaySDKStatusCode.PAY_FINISH) {
									// 订单完成
									TypeSDKLogger.i("client pay successful !!!");
									payResult.SetData(AttName.PAY_RESULT, "1");
									notify.Pay(payResult.DataToString());

								} else {
									// 订单未完成
									TypeSDKLogger.e("client pay failed !!!");
									payResult.SetData(AttName.PAY_RESULT, "0");
									notify.Pay(payResult.DataToString());
								}
							}
						});// 调用充值接口
			}
		});
		
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
			// 初始化游戏角色信息
			GameRolerInfo rolerInfo = new GameRolerInfo();
			rolerInfo.setBalance(userInfo.GetInt("saved_banlance"));// 游戏币当前余额
			rolerInfo.setRoleId(userInfo.GetData(AttName.ROLE_ID));// 游戏角色的id
			rolerInfo.setRoleLevel(userInfo.GetData(AttName.ROLE_LEVEL));// 游戏角色等级
			rolerInfo.setRoleName(userInfo.GetData(AttName.ROLE_NAME));// 游戏角色名字
			rolerInfo.setServerId(userInfo.GetData(AttName.SERVER_ID));// 用户所在服务器ID
			rolerInfo.setServerName(userInfo.GetData(AttName.SERVER_NAME));// 用户所在服务器名称
			rolerInfo.setVip(userInfo.GetData("vip_level"));// 用户vip等级
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
				UmipaySDKManager.setGameRolerInfo(appContext,
						GameRolerInfo.AT_LOGIN_GAME, rolerInfo);
				return;
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
				UmipaySDKManager.setGameRolerInfo(appContext,
						GameRolerInfo.AT_CREATE_ROLE, rolerInfo);
				return;
			} else if (userInfo.GetData("role_type").equals("levelUp")) {
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");
				UmipaySDKManager.setGameRolerInfo(appContext,
						GameRolerInfo.AT_LEVEL_UP, rolerInfo);
				return;
			} else {
				TypeSDKLogger.e("datatype error:" + "提交的数据类型不合法");
			}
		} catch (Exception e) {
			TypeSDKLogger.e("SendInfo Exception:" + e.getMessage());
		}
	}

	@Override
	public void ExitGame(Context _in_context) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// 必须在UI线程中调用
				UmipaySDKManager.exitSDK(appContext,
						new ExitDialogCallbackListener() {
							@Override
							public void onExit(int code) {
								if (code == UmipaySDKStatusCode.EXIT_FINISH) {
									// 用户成功退出，可执行退出游戏逻辑
									TypeSDKLogger.e("ExitGame");
									System.exit(0);
								} else {
									// 用户取消退出，可执行继续游戏逻辑
									TypeSDKLogger.e("Cancel ExitGame");
								}
							}
						});
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

	public void onDestroy(Context _in_context) {
		TypeSDKLogger.e("onDestroy");
		// 使用浮动菜单的activity销毁时调用
		UmipayFloatMenu.getInstance().cancel((Activity) _in_context);

	}

	private void OuWaninit() {
		GameParamInfo gameParamInfo = new GameParamInfo();
		gameParamInfo.setAppId(platform.GetData(AttName.APP_ID));// 设置AppID
		gameParamInfo.setAppSecret(platform.GetData(AttName.APP_KEY));// 设置AppSecret
		// 调用初始化接口
		UmipaySDKManager.initSDK(appContext, gameParamInfo,
				new InitCallbackListener() {
					@Override
					public void onSdkInitFinished(int code, String message) {
						if (code == UmipaySDKStatusCode.SUCCESS) {
							isInit = true;
							// 初始化成功,可以执行后续的登录充值操作
							TypeSDKLogger.i("init ouwan sdk successful");
							//UmipayFloatMenu.getInstance().create(appActivity);							
							TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
							notify.Init();

						} else {
							// 初始化失败,不能进行后续操作
							TypeSDKLogger.e("init ouwan sdk failed");
						}
					}
				}, new AccountCallbackListener() {
					@Override
					public void onLogout(int code, Object object) {
						if (code == UmipaySDKStatusCode.SUCCESS) {
							// 客户端成功退出
							// 通过自定义数据object判断onLogout回调来自主动调用还是SDK调用
							if (object != null) {
								// object非空时可以用传回的object判断是否属于用户的主动调用
								TypeSDKLogger.i("SDK登出偶玩账户,用户的主动调用");
								TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
								notify.Logout();
							} else {
								// object为空时为SDK登出偶玩账户调用的回调
								TypeSDKLogger.i("SDK登出偶玩账户调用的回调");
								TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
								notify.Logout();
							}
						}
					}

					@Override
					public void onLogin(int code, GameUserInfo info) {
						// TODO Auto-generated method stub
						if (code == UmipaySDKStatusCode.SUCCESS
								&& userInfo != null) {
							// 登录成功后,sdk返回一个GameUserInfo结构,里面包含平台用户唯一标志OpenId,用户签名Sign及utc秒数。							
							TypeSDKLogger.i("login successful:"
									+ info.toString());
							ShowToolBar(appContext);
							TypeSDKNotify_ouwan notify = new TypeSDKNotify_ouwan();
							notify.sendToken(
									info.getSign() + "|"
											+ info.getTimestamp_s(),
									info.getOpenId(), info.getTimestamp_s()
											+ "");
						} else {
							// 用户按返回键,退出了sdk登录界面,需要进行相关的操作,比如弹出对话框提醒用户重新登录。
							TypeSDKLogger.e("login cancel");
						}
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
