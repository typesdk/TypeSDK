package com.type.sdk.android.xiaomi;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.xiaomi.gamecenter.sdk.GameInfoField;
import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public static MiAppInfo appInfo;
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e( "sdk already init");
			TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
			notify.Init();
			return;
		}
		
		this.xiaomiInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		XiaomiUpdateAgent.update(appContext);
		this.xiaomiLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogout");
		TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
		notify.Logout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.xiaomiPay(_in_pay);
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
		TypeSDKLogger.e( "LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "SendInfo");
		try {
			TypeSDKLogger.e( "_in_data:" + _in_data);
			userInfo.StringToData(_in_data);

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel", userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName", userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.e( "上传用户信息:string=" + userJsonExData);
			// this.userInfo.CopyAttByData(userData);
		} catch (Exception e) {
			TypeSDKLogger.e( "上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e( "执行ExitGame方法");
			System.exit(0);			
		}
		
	}

	public void onResume() {
		TypeSDKLogger.e( "onResume");
	}

	public void onPause() {
		TypeSDKLogger.e( "onPause");
	}

	public void onStop() {
		TypeSDKLogger.e( "onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e( "onDestroy");
	}

	private void xiaomiInit() {
		TypeSDKLogger.e( "init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				/** SDK初始化 */
				appInfo = new MiAppInfo();
				appInfo.setAppId(platform.GetData(AttName.APP_ID));
				appInfo.setAppKey(platform.GetData(AttName.APP_KEY)); 
				appInfo.setOrientation(ScreenOrientation.horizontal); // 横竖屏
//				appInfo.setAppType(MiAppType.online);
				MiCommplatform.Init(appActivity, appInfo);
				TypeSDKLogger.e( "APP_ID:" + appInfo.getAppId());
				TypeSDKLogger.e( "APP_KEY:" + appInfo.getAppKey());
				TypeSDKLogger.e( "init success");
				isInit = true;
				
				TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
				notify.Init();
			}
		});

		TypeSDKLogger.e( "init done");

	}

	private void xiaomiLogin() {
		TypeSDKLogger.e( "login start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.e( "login begin");
				// 调用SDK执行登陆操作
				MiCommplatform.getInstance().miLogin(
						appActivity,

						new OnLoginProcessListener() {
							@Override
							public void finishLoginProcess(int code,
									MiAccountInfo arg1) {
								switch (code) {
								case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
									TypeSDKLogger.e( "MI_XIAOMI_PAYMENT_SUCCESS");
									// 登陆成功
									// 获取用户的登陆后的UID（即用户唯一标识）
									long uid = arg1.getUid();
									/**
									 * 以下为获取session并校验流程，如果是网络游戏必须校验，
									 * 如果是单机游戏或应用可选
									 **/
									// 获取用户的登陆的Session（请参考5.3.3流程校验Session有效性）
									String session = arg1.getSessionId();
									TypeSDKLogger.e( "getUid():" + uid);
									TypeSDKLogger.e("session:" + session);
									// 请开发者完成将uid和session提交给开发者自己服务器进行session验证
									TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
									notify.sendToken(uid + "", session);
									break;
								case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
									// 登陆失败
									TypeSDKLogger.e( "login fail");
//									xiaomiLogin();
									break;
								case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_HAS_NOT_LOGIN:
									// 未登录
									TypeSDKLogger.e( "MI_XIAOMI_GAMECENTER_ERROR_HAS_NOT_LOGIN");
									break;
								case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL:
									// 取消登录
									TypeSDKLogger.e( "login cancel");
//									xiaomiLogin();
//									System.exit(0);
									break;
								case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
									// 登录操作正在进行中
									TypeSDKLogger.e( "login executed");
									break;
								default:
									// 登录失败
									break;
								}
							}
						});
			}
		});
	}

	private void xiaomiPay(PayInfoData _in_pay) {
		MiBuyInfo miBuyInfo = new MiBuyInfo();
		miBuyInfo.setCpOrderId(_in_pay.GetData(AttName.BILL_NUMBER));// 订单号唯一（不为空）UUID.randomUUID().toString()
		miBuyInfo.setCpUserInfo(_in_pay.GetData(AttName.EXTRA)); // 此参数在用户支付成功后会透传给CP的服务器
		
		int price = 0;
		if(TypeSDKTool.isPayDebug){
			price = 1;
		}else{
			price = (_in_pay.GetInt(AttName.REAL_PRICE))/100;
		}
		miBuyInfo.setAmount(price);// 必须是大于1的整数，10代表10米币，即10元人民币（不为空）
		//_in_pay.GetInt(AttName.REAL_PRICE) / 100
		// 用户信息，网游必须设置、单机游戏或应用可选
		Bundle mBundle = new Bundle();
		// mBundle.putString(GameInfoField.GAME_USER_BALANCE, "0"); // 用户余额
		// mBundle.putString(GameInfoField.GAME_USER_GAMER_VIP, "0"); // vip等级
		mBundle.putString(GameInfoField.GAME_USER_LV,
				userInfo.GetData(AttName.ROLE_LEVEL)); // 角色等级
		// mBundle.putString(GameInfoField.GAME_USER_PARTY_NAME, "猎人"); // 工会，帮派
		mBundle.putString(GameInfoField.GAME_USER_ROLE_NAME,
				userInfo.GetData(AttName.ROLE_NAME).length() == 0 ? userInfo.GetData(AttName.USER_ID) : 
					userInfo.GetData(AttName.ROLE_NAME)); // 角色名称
		mBundle.putString(GameInfoField.GAME_USER_ROLEID,
				userInfo.GetData(AttName.ROLE_ID)); // 角色id
		mBundle.putString(GameInfoField.GAME_USER_SERVER_NAME,
				userInfo.GetData(AttName.SERVER_NAME).length() == 0 ? userInfo.GetData(AttName.SERVER_ID) : 
					userInfo.GetData(AttName.SERVER_NAME)); // 所在服务器
		TypeSDKLogger.e("GAME_USER_LV:" + userInfo.GetData(AttName.ROLE_LEVEL));
		TypeSDKLogger.e("GAME_USER_ROLE_NAME:" + ((userInfo.GetData(AttName.ROLE_NAME).length() == 0) ? userInfo.GetData(AttName.USER_ID) : 
					userInfo.GetData(AttName.ROLE_NAME)));
		TypeSDKLogger.e("GAME_USER_ROLEID:" + userInfo.GetData(AttName.ROLE_ID));
		TypeSDKLogger.e("GAME_USER_SERVER_NAME:" + ((userInfo.GetData(AttName.SERVER_NAME).length() == 0) ? userInfo.GetData(AttName.SERVER_ID) : 
					userInfo.GetData(AttName.SERVER_NAME)));
		miBuyInfo.setExtraInfo(mBundle); // 设置用户信息
		TypeSDKLogger.e( "BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
		TypeSDKLogger.e( "ROLE_LEVEL:" + userInfo.GetData(AttName.ROLE_LEVEL));
		TypeSDKLogger.e( "ROLE_NAME:" + userInfo.GetData(AttName.ROLE_NAME));
		TypeSDKLogger.e( "USER_NAME:" + userInfo.GetData(AttName.USER_NAME));
		TypeSDKLogger.e( "USER_ID:" + userInfo.GetData(AttName.USER_ID));
		TypeSDKLogger.e( "ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
		TypeSDKLogger.e( "SERVER_NAME:" + userInfo.GetData(AttName.SERVER_NAME));
		TypeSDKLogger.e( "SERVER_ID:" + userInfo.GetData(AttName.SERVER_ID));
		TypeSDKLogger.e( "S_ID:" + (userInfo.GetData(AttName.SERVER_NAME).length() == 0 ? userInfo.GetData(AttName.SERVER_ID) : 
			userInfo.GetData(AttName.SERVER_NAME)));
		MiCommplatform.getInstance().miUniPay(appActivity,
				miBuyInfo, new OnPayProcessListener() {
					@Override
					public void finishPayProcess(int code) {

						PayResultData payResult = new PayResultData();

						switch (code) {
						case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
							// 购买成功
							TypeSDKLogger.e( "MI_XIAOMI_PAYMENT_SUCCESS");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
							// 取消购买
							TypeSDKLogger.e( "MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL");
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
							// 购买失败
							TypeSDKLogger.e( "MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							break;
						case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
							// 操作正在进行中
							TypeSDKLogger.e("MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAYMENT_ERROR_ACTION_EXECUTED");
							break;
						default:
							// 购买失败
							TypeSDKLogger.e("default");
							break;
						}
						TypeSDKNotify_xiaomi notify = new TypeSDKNotify_xiaomi();
						notify.Pay(payResult.DataToString());
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
