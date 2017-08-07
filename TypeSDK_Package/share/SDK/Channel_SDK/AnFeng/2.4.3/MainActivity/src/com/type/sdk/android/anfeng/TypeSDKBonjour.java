package com.type.sdk.android.anfeng;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.anfeng.pay.AnFengPaySDK;
import com.anfeng.pay.entity.CPInfo;
import com.anfeng.pay.entity.OrderInfo;
import com.anfeng.pay.inter.LoginCallback;
import com.anfeng.pay.inter.LogoutCallback;
import com.anfeng.pay.inter.PayCallback;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public boolean isNoInit = false;
	AnFengPaySDK sdk = AnFengPaySDK.getInstance();

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
		anFengSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.anFengSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.anFengSdkLogout();
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
		this.anFengSdkPay(_in_pay);
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
		this.SendInfo(_in_context,_in_data);
	}

	@Override
	public void ExitGame(Context _in_context) {
		anFengSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
	}

	private void anFengSdkInit() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				CPInfo info = new CPInfo(platform.GetData(AttName.APP_KEY));
				TypeSDKLogger.e("platform.GetData(AttName.APP_KEY):"+platform.GetData(AttName.APP_KEY));
				sdk.init(appActivity, info);
				TypeSDKNotify_AnFeng notify = new TypeSDKNotify_AnFeng();
				notify.Init();
				TypeSDKLogger.e("INIT SUCCESS");
			}
		});

	}

	private void anFengSdkLogin() {
		TypeSDKLogger.i("anFengSdkLogin start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				sdk.anfanLogin(appActivity, new LoginCallback() {
					/**
					 * @param uid
					 *            用户登录名
					 * @param uuid
					 *            用户登陆后，产生的token
					 * @param ucid
					 *            用户的数字ID,做用户唯一标识符
					 */
					@Override
					public void onLoginSuccess(String uid, String uuid,
							String ucid) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("登录成功 ");
						// 添加悬浮球 在登录后调用
						sdk.addFloatBall(appActivity);
						/**
						 * 
						 * @param act
						 *            游戏activty
						 * @param roleId
						 *            角色唯一标志
						 * @param roleName
						 *            角色名
						 * @param roleLevel
						 *            角色等级
						 * @param zoneId
						 *            服务器id
						 * @param zoneName
						 *            服务器名称
						 */
						// 登录成功获取账号角色信息后可上传角色信息给sdk服务器
						sdk.setRoleData(appActivity,
								userInfo.GetData(AttName.ROLE_ID),
								userInfo.GetData(AttName.ROLE_NAME),
								userInfo.GetData(AttName.ROLE_LEVEL),
								userInfo.GetData(AttName.SERVER_ID),
								userInfo.GetData(AttName.SERVER_NAME));
						TypeSDKLogger.e("uid:" + uid);
						TypeSDKLogger.e("uuid:" + uuid);
						TypeSDKLogger.e("ucid:" + ucid);
						TypeSDKNotify_AnFeng notify = new TypeSDKNotify_AnFeng();
						notify.sendToken(uuid, ucid + "|" + uid);
					}

					@Override
					public void onLoginFailure() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("登录失败：");
					}

					@Override
					public void onLoginCancel() {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("退出失败");
					}
				});

			}
		});
	}

	private void anFengSdkLogout() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKNotify_AnFeng notify = new TypeSDKNotify_AnFeng();
				notify.Logout();
				TypeSDKLogger.e("LOGOUT SUCCESS");
			}
		});
	}

	private void anFengSdkExit() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				sdk.anfanLogout(appActivity, new LogoutCallback() {

					@Override
					public void onLogout() {
						// TODO Auto-generated method stub
						appActivity.finish();
					}
				});
				TypeSDKLogger.e("EXIT SUCCESS");
			}
		});

	}

	private void anFengSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("anFeng start!");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				DecimalFormat df = new DecimalFormat("0.00");

				int price = 0;
				if (TypeSDKTool.isPayDebug) {
					price = 1;
				} else {
					price = _in_pay.GetInt(AttName.REAL_PRICE);
				}
				double payAmount = price * 0.01f;
				// dia.show();
				// Random random = new Random();
				// 商品名和商品描述不能为空
				String orderId = String.valueOf(System.currentTimeMillis());
				TypeSDKLogger.e("orderId:"+orderId);
				TypeSDKLogger.e("_in_pay.GetData(AttName.BILL_NUMBER):"+_in_pay.GetData(AttName.BILL_NUMBER));
				TypeSDKLogger.e("df.format(payAmount):"+df.format(payAmount));
				TypeSDKLogger.e("_in_pay.GetData(AttName.ITEM_NAME):"+_in_pay.GetData(AttName.ITEM_NAME));
				TypeSDKLogger.e("_in_pay.GetData(AttName.ITEM_DESC):"+_in_pay.GetData(AttName.ITEM_DESC));
				OrderInfo info = new OrderInfo(_in_pay.GetData(AttName.BILL_NUMBER), df.format(payAmount),
						_in_pay.GetData(AttName.ITEM_NAME), _in_pay
								.GetData(AttName.ITEM_DESC));
				sdk.anFanPay(appActivity, info,
						platform.GetData(AttName.PAY_CALL_BACK_URL),
						new PayCallback() {
							// result 订单号
							TypeSDKNotify_AnFeng notify_AnFeng = new TypeSDKNotify_AnFeng();
							TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();

							@Override
							public void onPaySuccess(String result) {
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT,
												"1");
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT_REASON,
												"支付成功");
								notify_AnFeng.Pay(payResult.DataToString());
								TypeSDKLogger.i("支付成功");
								TypeSDKLogger.e("支付提示：" + "订单  " + result
										+ "  已支付成功");
							}

							@Override
							public void onPayFailure(String orderId) {
								TypeSDKLogger.e("支付失败了");
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT,
												"0");
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT_REASON,
												"支付失败");
								notify_AnFeng.Pay(payResult.DataToString());
								TypeSDKLogger.i("支付失败:");
							}

							@Override
							public void onPayCancel() {
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT,
												"2");
								payResult
										.SetData(
												TypeSDKDefine.AttName.PAY_RESULT_REASON,
												"支付取消");
								notify_AnFeng.Pay(payResult.DataToString());
								TypeSDKLogger.e("放弃支付");

							}

						});
			}
		});

	}

	// private void anFengSdkSendInfo(final JSONObject _jsonExData) {}

	
	public void SendInfo(Context _in_context, String _in_data) {
		TypeSDKLogger.e( "SendInfo"+_in_data);
		try {
			userInfo.StringToData(_in_data);

			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				String roleId=userInfo.GetData(AttName.ROLE_ID);
				String roleNmae=userInfo.GetData(AttName.ROLE_NAME);
				String roleLevel=userInfo.GetData(AttName.ROLE_LEVEL);
				String severId=userInfo.GetData(AttName.SERVER_ID);
				String severName=userInfo.GetData(AttName.SERVER_NAME);
				sdk.setRoleData(appActivity, roleId, roleNmae, roleLevel, severId, severName);
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
