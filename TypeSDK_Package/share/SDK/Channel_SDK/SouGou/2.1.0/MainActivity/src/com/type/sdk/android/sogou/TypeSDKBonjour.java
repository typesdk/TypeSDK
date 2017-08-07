package com.type.sdk.android.sogou;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.UserInfo;
import com.sogou.gamecenter.sdk.listener.InitCallbackListener;
import com.sogou.gamecenter.sdk.listener.LoginCallbackListener;
import com.sogou.gamecenter.sdk.listener.OnExitListener;
import com.sogou.gamecenter.sdk.listener.PayCallbackListener;
import com.sogou.gamecenter.sdk.listener.SwitchUserListener;
import com.sogou.gamecenter.sdk.views.FloatMenu;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private SogouGamePlatform mSogouGamePlatform = SogouGamePlatform.getInstance();
//	private FloatMenu mFloatSwitchUser;
	/**
	 *  浮动菜单接入说明：浮动菜单在当前界面创建后
	 *  注意：
	 *  1.当前界面切换到前台，调用浮动菜单show方法
	 *  2.当前界面切换到后台，调用浮动菜单hide方法
	 *  3.浮层有切换帐号功能，请在show方法后设置帐号变化监听器或者设置切换帐号监听器
	 */	
	private FloatMenu mFloatMenu;
	
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
			TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
			notify.Init();
			return;
		}
		
		this.sogouInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.sogouLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.sogouLogout();
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
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.sogouPay(_in_pay);
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
			
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e("创建角色时的角色信息");
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e("角色升级时角色信息");				
			}else{
				TypeSDKLogger.e("datatype error:"+"提交的数据不合法");
			}

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mSogouGamePlatform.exit(new OnExitListener(appContext){

					@Override
					public void onCompleted() {
						// TODO Auto-generated method stub
						System.exit(0);
					}
					
				});
			}
			
		});					
		}
		
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if(mFloatMenu!=null){
			// 默认浮在右上角位置，距左边为10，距下边为10位置，单位为像素
			mFloatMenu.setParamsXY(10, 100);
			mFloatMenu.show();
					
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		// 隐藏切换帐号浮层
		if(mFloatMenu!=null){
			TypeSDKLogger.e("mFloatSwitchUser is not null");
			mFloatMenu.hide();
		}else{
			TypeSDKLogger.e("mFloatSwitchUser is null");
		}
		
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void sogouInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// 接入时调用该接口，方便查看log日志，发布正式版本，请注释掉该接口
			        mSogouGamePlatform.openLogInfo();
			        mSogouGamePlatform.init(appContext, new InitCallbackListener() {
						
						@Override
						public void initSuccess() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("initSuccess");
							isInit = true;
							// SDK初始化
							TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
							notify.Init();
						}
						
						@Override
						public void initFail(int arg0, String arg1) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("init fail");
						}
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void sogouLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				mSogouGamePlatform.login(appContext, new LoginCallbackListener() {
					
					@Override
					public void loginSuccess(int code, UserInfo userInfo) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login success");
						TypeSDKLogger.e("getSessionKey:" + userInfo.getSessionKey());
						TypeSDKLogger.e("getRefreshToken:" + userInfo.getRefreshToken());
						TypeSDKLogger.e("getUserId:" + userInfo.getUserId());
						TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
//						notify.sendToken(URLEncoder.encode(userInfo.getSessionKey()), userInfo.getUserId()+"");
						notify.sendToken(userInfo.getSessionKey(), userInfo.getUserId()+"");
						// 当前是全屏模式，isFullscreen为true
						if(mFloatMenu == null){
							mFloatMenu = mSogouGamePlatform.createFloatMenu(appContext, true);
							// 默认浮在右上角位置，距左边为10，距下边为10位置，单位为像素
							mFloatMenu.setParamsXY(10, 100);
							mFloatMenu.show();
						}
						
						// 浮动菜单设置切换帐号监听器或者设置帐号变化监听器（选一种接入即可）
						// 演示设置切换帐号监听器
						mFloatMenu.setSwitchUserListener(new SwitchUserListener(){
							@Override
							public void switchSuccess(int code, UserInfo userInfo) {
								TypeSDKLogger.d("FloatMenu witchSuccess code:"+code+" userInfo:"+userInfo);
								TypeSDKLogger.e("getSessionKey:" + userInfo.getSessionKey());
								TypeSDKLogger.e("getRefreshToken:" + userInfo.getRefreshToken());
								TypeSDKLogger.e("getUserId:" + userInfo.getUserId());
								TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
//								notify.reLogin(URLEncoder.encode(userInfo.getSessionKey()), userInfo.getUserId()+"");
								notify.sendToken(userInfo.getSessionKey(), userInfo.getUserId()+"");
							}
							
							@Override
							public void switchFail(int code, String msg) {
								TypeSDKLogger.e("FloatMenus switchFail code:"+code+" msg:"+msg);				
							}
						});																		
						/*
						 * 演示切换帐号浮层使用 创建切换帐号浮层对象靠右边 实现切换帐号回调对象SwitchUserListener
						 * 不影响游戏视觉区域选择停靠y轴坐标（单位像素），demo设置为200 为了适配不同机型，游戏需要获取屏幕高度来计算y轴坐标值
						 */
//						mFloatSwitchUser = mSogouGamePlatform.createFloatSwitchUser(appContext, new SwitchUserListener() {
//
//							@Override
//							public void switchSuccess(int code, UserInfo userInfo) {
//								TypeSDKLogger.d("FloatSwitchUser switchSuccess:" + userInfo);
//								TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
//								notify.Logout();
//							}
//
//							@Override
//							public void switchFail(int code, String msg) {
//								TypeSDKLogger.d("FloatSwitchUser switchFail:" + code + " msg:" + msg);
//							}
//
//						}, 200);
						
					}
					
					@Override
					public void loginFail(int arg0, String arg1) {
						// TODO Auto-generated method stub
						TypeSDKLogger.e("login fail");
					}
				});
				
			}
		});

	}

	private void sogouLogout() {
		TypeSDKLogger.e("logout_success");
		TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
		notify.Logout();
	}

	private void sogouPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SERVER_ID:" + _in_pay.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("SERVER_NAME:" + _in_pay.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.e("ROLE_ID:" + _in_pay.GetData(AttName.ROLE_ID));
					TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
					TypeSDKLogger.e("ROLE_NAME:" + _in_pay.GetData(AttName.ROLE_NAME));
					TypeSDKLogger.e("ITEM_SERVER_ID:" + _in_pay.GetData(AttName.ITEM_SERVER_ID));
					TypeSDKLogger.e("callbackinfo:" + _in_pay.GetData(AttName.EXTRA) + "|" + _in_pay.GetData(AttName.BILL_NUMBER));
					
					int price = 0;
					if(platform.GetData("mode").equals("debug")){
						price = 1;
					}else{
						price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)*0.01f);
					}
					
					Map<String, Object> data = new HashMap<String, Object>();
					// 游戏货币名字（必传）
					data.put("currency", platform.GetData("currency"));
					// 人民币兑换比例（必传）
					data.put("rate", platform.GetData("rate"));
					// 购买商品名字（必传）
					data.put("product_name", _in_pay.GetData(AttName.ITEM_NAME));
					// 充值金额，单位是元，在手游中数据类型为整型（必传）
					data.put("amount", price);
					
					// 透传参数,游戏方自行定义（可选） _in_pay.GetData(AttName.EXTRA) + "|" + 
					data.put("app_data", _in_pay.GetData(AttName.BILL_NUMBER));
					// 是否可以编辑支付金额（可选）
					data.put("appmodes", false);
					mSogouGamePlatform.pay(appContext, data, new PayCallbackListener() {

						PayResultData payResult = new PayResultData();
						TypeSDKNotify_sogou notify = new TypeSDKNotify_sogou();
						// 支付成功回调,游戏方可以做后续逻辑处理
						// 收到该回调说明提交订单成功，但成功与否要以服务器回调通知为准
						@Override
						public void paySuccess(String orderId, String appData) {
							// orderId是订单号，appData是游戏方自己传的透传消息
							TypeSDKLogger.d("paySuccess orderId:" + orderId + " appData:" + appData);
							// 支付成功
							TypeSDKLogger.e("pay_success");
							
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}

						@Override
						public void payFail(int code, String orderId, String appData) {
							// 支付失败情况下,orderId可能为空
							if (orderId != null) {
								TypeSDKLogger.e("payFail code:" + code + "orderId:" + orderId + " appData:" + appData);
							} else {
								TypeSDKLogger.e("payFail code:" + code + " appData:" + appData);
							}
							// 支付失败
							TypeSDKLogger.e("return Error");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
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
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
