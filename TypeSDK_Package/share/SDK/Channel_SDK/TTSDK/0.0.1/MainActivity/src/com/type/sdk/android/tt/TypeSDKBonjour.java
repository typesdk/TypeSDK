package com.type.sdk.android.tt;

import java.util.Date;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.wett.cooperation.container.SdkCallback;
import com.wett.cooperation.container.TTSDKV2;
import com.wett.cooperation.container.bean.GameInfo;
import com.wett.cooperation.container.bean.PayInfo;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public boolean isNoInit = false;

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
		TypeSDKLogger.i("isInit=" + isInit);
		if (isInit) {
			TypeSDKLogger.e("error init do again");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}
		TTSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.TTSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.TTSdkLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.TTSdkPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);

		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
	}

	@Override
	public void ExitGame(Context _in_context) {
		TTSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		TTSDKV2.getInstance().onResume((Activity)_in_context);
		if (TTSDKV2.getInstance().isLogin()) {
			TTSDKV2.getInstance().showFloatView((Activity)_in_context);
		}
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
		TTSDKV2.getInstance().onPause((Activity)_in_context);
		TTSDKV2.getInstance().hideFloatView((Activity)_in_context);
	}

	public void onStop(Context _in_context) {
		TypeSDKLogger.e("onStop");
		TTSDKV2.getInstance().onStop((Activity)_in_context);
	}

	public void onDestroy(Context _in_context) {
		TypeSDKLogger.e("onDestroy");
		TTSDKV2.getInstance().onDestroy((Activity)_in_context);
	}

	public void onRestart(Context _in_context) {
		TypeSDKLogger.e("onRestart");
		TTSDKV2.getInstance().onRestart((Activity)_in_context);
	}

	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
		TTSDKV2.getInstance().onNewIntent(intent);
	}

	public void onActivityResult(Context context,int requestCode, int resultCode, Intent data) {
		TypeSDKLogger.e("onActivityResult");
		TTSDKV2.getInstance().onActivityResult((Activity)context,requestCode,resultCode,data);
	}

	private void TTSdkInit() {
		/*
		 * TTSDKV2.getInstance().init(@NonNull Activity context, GameInfo info,
		 * boolean isDebug, int orientation, SdkCallback<String> callback）参数说明
		 * context: Activity对象info：初始化游戏信息使用的实体类isDebug：是否打开调试模式
		 * orientation：默认屏幕方向callback：初始化步骤执行完后回调处理的操作
		 */
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				GameInfo gameInfo = new GameInfo();
				TTSDKV2.getInstance().init(appActivity, gameInfo,false,
						Configuration.ORIENTATION_LANDSCAPE,
						new SdkCallback<String>() {
					@Override
					protected boolean onResult(int i, String s) {
						if (i == 0) {
							TTSDKV2.getInstance().onCreate(appActivity);
							TypeSDKNotify_TT notify = new TypeSDKNotify_TT();
							notify.Init();
							TypeSDKLogger.e("init success!");

							TTSDKV2.getInstance().setLogoutListener(
									new SdkCallback<String>() {
										@Override
										protected boolean onResult(int i, String s) {
											if (i == 0) {
												TypeSDKNotify_TT notify = new TypeSDKNotify_TT();
												notify.Logout();
												TypeSDKLogger.e("LOGOUT SUCCESS");
											} else {
												TypeSDKLogger.e("LOGOUT FAILED");
											}
											return false;
										}
									});


						} else {
							TypeSDKLogger.e("init failed!");
						}
						return false;
					}
				});
			}
		});

	}

	private void TTSdkLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TTSDKV2.getInstance().login(appActivity,
						new SdkCallback<String>() {
					@Override
					protected boolean onResult(int i, String s) {
						if (i == 0) {
							TypeSDKLogger.i("login success");
							// 登录成功后还需要做登录校验,由游戏自己实现
							String gameid = TTSDKV2.getInstance()
									.getGameId();
							String uid = TTSDKV2.getInstance().getUid();
							String session = TTSDKV2.getInstance()
									.getSession();
							TypeSDKNotify_TT notify = new TypeSDKNotify_TT();
							notify.sendToken(session, uid);
							TypeSDKLogger.e("gameid:" + gameid);
							TypeSDKLogger.e("uid:" + uid);
							TypeSDKLogger.e("session:" + session);

							TTSDKV2.getInstance().showFloatView(appActivity);
						} else {
							TypeSDKLogger.e("LOGIN FAILED");
						}
						return false;
					}
				});
			}
		});
	}

	private void TTSdkLogout() {
		TypeSDKLogger.i("logout start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.i("logout");
				TTSDKV2.getInstance().logout(appActivity);
			}
		});
	}

	private void TTSdkExit() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TTSDKV2.getInstance().uninit(appActivity,
						new SdkCallback<String>() {
					@Override
					protected boolean onResult(int i, String s) {
						if (i == 0) {
							TypeSDKLogger.e("EXIT SUCCESS");
							System.exit(0);
						} else {
							TypeSDKLogger.e("EXIT FAILED");
						}
						return false;
					}
				});
			}
		});

	}

	private void TTSdkPay(TypeSDKData.PayInfoData _in_pay) {

		int price = 0;
		if (TypeSDKTool.isPayDebug) {
			price = 1;
		} else {
			price = (_in_pay.GetInt(AttName.REAL_PRICE))/100;
		}
		TypeSDKLogger.i("price:"+price);
		PayInfo payInfo = new PayInfo();
		payInfo.setBody(_in_pay.GetData(AttName.ITEM_DESC)); //商品描述
		payInfo.setCpFee(price);//CP订单金额
		payInfo.setCpTradeNo(_in_pay.GetData(AttName.BILL_NUMBER));//CP订单号
		payInfo.setServerName(_in_pay.GetData(AttName.SERVER_ID));//游戏服务器id
		payInfo.setExInfo(_in_pay.GetData(AttName.PAY_RESULT_DATA));//CP扩展信息，该字段将会在支付成功后原样返回给CP
		payInfo.setSubject(_in_pay.GetData(AttName.ITEM_NAME));//订单商品名称
		payInfo.setPayMethod(payInfo.PAY_METHOD_ALL);//支付方式
		payInfo.setCpCallbackUrl(platform.GetData(AttName.PAY_CALL_BACK_URL));//游戏方支付回调
		payInfo.setChargeDate(new Date().getTime());//cp充值时间
		TTSDKV2.getInstance().pay(appActivity, payInfo, new SdkCallback<String>() {
			@Override
			protected boolean onResult(int i, String payResponse) {
				TypeSDKNotify_TT notify_TT = new TypeSDKNotify_TT();
				TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
				if (i == 0) {
					payResult.SetData(
							TypeSDKDefine.AttName.PAY_RESULT, "1");
					payResult.SetData(
							TypeSDKDefine.AttName.PAY_RESULT_REASON,
							"支付成功");
					notify_TT.Pay(payResult.DataToString());
					TypeSDKLogger.e("PAY SUCCESS");
				} else {
					payResult.SetData(
							TypeSDKDefine.AttName.PAY_RESULT, "0");
					payResult.SetData(
							TypeSDKDefine.AttName.PAY_RESULT_REASON,
							"支付失败");
					notify_TT.Pay(payResult.DataToString());
					TypeSDKLogger.e("PAY FAILED");
				}
				return true;
			}
		});
	}


	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		TypeSDKLogger.e( "SendInfo"+_in_data);
		try {
			userInfo.StringToData(_in_data);

			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				Map<String,String> exMapParams = new ArrayMap<String, String>();
				exMapParams.put("scene_Id","");

				exMapParams.put("zoneId","");

				exMapParams.put("balance","");

				exMapParams.put("Vip","");

				exMapParams.put("partyName","");

				JSONObject jsonObject = new JSONObject(exMapParams);

				String exInfo = jsonObject.toString();
				int roleLevel=Integer.valueOf(userInfo.GetData(AttName.ROLE_LEVEL)).intValue();
				TypeSDKLogger.i("roleLevel:"+roleLevel);
				TTSDKV2.getInstance().submitGameRoleInfo((Activity)_in_context,userInfo.GetData(AttName.SERVER_ID),
						userInfo.GetData(AttName.ROLE_ID),userInfo.GetData(AttName.ROLE_NAME),roleLevel,exInfo);
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e( "创建角色时的角色信息");
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e( "角色升级时角色信息");
			}else{
				TypeSDKLogger.e( "datatype error:"+"提交的数据不合法");
			}
			//			statisticsData("1");
		} catch (Exception e) {
			TypeSDKLogger.e( "上传用户信息:" + e.getMessage());
		}
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
