package com.type.sdk.android.shunwang;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.shunwang.sdk.game.SWGameSDK;
import com.shunwang.sdk.game.SWOrientation;
import com.shunwang.sdk.game.entity.PayData;
import com.shunwang.sdk.game.listener.ILoaderListener;
import com.shunwang.sdk.game.listener.OnLoginResponseListener;
import com.shunwang.sdk.game.listener.OnPayResponseListener;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public SWGameSDK swGameSdk;
	private boolean isLogin = false;

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
			TypeSDKNotify_shunwang notify = new TypeSDKNotify_shunwang();
			notify.Init();
			return;
		}
		this.shunwangInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.shunwangLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.shunwangLogout();
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
		this.shunwangPay(_in_pay);

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
		super.ShowShare(_in_context, _in_data);
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
				TypeSDKLogger.e( "进入游戏时的角色信息");
				
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e( "创建角色时的角色信息");
				
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e( "角色升级时角色信息");
			}else{
				TypeSDKLogger.e( "datatype error:"+"提交的数据不合法");
			}

		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			if (isInit) {
				isInit = false;
			}
			System.exit(0);
		}
		
	}
	
	public void onCreate(Context _in_context) {
		TypeSDKLogger.e("onCreate");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		swGameSdk = SWGameSDK .getInstance();
		this.initStart();
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			if(isLogin){
				SWGameSDK.getInstance().showFloatingView(appActivity);
			}
			
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (isInit) {
			SWGameSDK.getInstance().hideFloatingView(appActivity);
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		if (isInit) {
			
		}
	}

	private void shunwangInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					// SDK初始化
					initStart();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}
	
	private void initStart(){
		SWGameSDK.getInstance().init(appActivity, SWOrientation.LANDSCAPE, new ILoaderListener() {
						
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				TypeSDKLogger.i("init success");
				swGameSdk.setAutologon(true);
				isInit = true;
				TypeSDKNotify_shunwang notify = new TypeSDKNotify_shunwang();
				notify.Init();
			}
						
			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("init onStart");
			}
						
			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
				TypeSDKLogger.e("init onFailed");
			}
		});
	}

	private void shunwangLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				swGameSdk.login(appActivity, 
						platform.GetData(AttName.SDK_CP_ID), 
						platform.GetData(AttName.APP_ID),
						platform.GetData("md5Key"),
						platform.GetData("rsaKey"),
						new OnLoginResponseListener() {
							
							@Override
							public void onSdkNoInit() {
								// TODO Auto-generated method stub
								TypeSDKLogger.w("no init");
								shunwangInit();
							}
							
							@Override
							public void onLoginWindowClose() {
								// TODO Auto-generated method stub
								TypeSDKLogger.w("onLoginWindowClose");
							}
							
							@Override
							public void onLoginSucceed(String guid, String accessToken, String memberId) {
								// TODO Auto-generated method stub
								TypeSDKLogger.i("login succeed");
								isLogin = true;
								TypeSDKNotify_shunwang notify = new TypeSDKNotify_shunwang();
								notify.sendToken(accessToken, guid);
							}
							
							@Override
							public void onLoginFailed() {
								// TODO Auto-generated method stub
								TypeSDKLogger.e("onLoginFailed");
							}
							
							@Override
							public void onLogOutSucceed() {
								// TODO Auto-generated method stub
								TypeSDKLogger.i("onLogOutSucceed");
								isLogin = false;
								TypeSDKNotify_shunwang notify = new TypeSDKNotify_shunwang();
								notify.Logout();
							}
						});
						
			}
		});

	}

	private void shunwangLogout() {
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				isLogin = false;
				swGameSdk.logout(appActivity);
			}
		});
			
		
	}

	private void shunwangPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {
					int price = 0;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					}else{
						price = (_in_pay.GetInt(AttName.REAL_PRICE)/100);
					}
					
					PayData payData = new PayData();
					payData.setSiteId(platform.GetData(AttName.SDK_CP_ID));
					payData.setGuid(userInfo.GetData(AttName.USER_ID));
					payData.setGameId(platform.GetData(AttName.APP_ID));
					payData.setRsaKey(platform.GetData("rsaKey"));
					payData.setRegion(null);// 目前必须传null
					/**
					 * 如果贵方有自定义参数的话，可通过附加参数的形式添加到gameCallback后面，我们会透传过去。
					 * 如：http://test.shunwang.com/buy.htm？customerParams=gameParams
					 */
					payData.setGameCallback(platform.GetData(AttName.PAY_CALL_BACK_URL) + "?billNumber=" + _in_pay.GetData(AttName.BILL_NUMBER));
					/**
					 * price、gameCoin、gameCoinMes为组合字段，其关系如下： 
					 * 1、price 必传参数，人民币总金额，单元：元。
					 * 2、gameCoin 购买的游戏币总数量，常规场景下，使用price和gameCoin组合。gameCoin需要和price字段对应。
					 *   如：商务后台配置游戏币和人民币比率为10。则如果用户该次充值50元，则price为50，gameCoin为500.
					 * 3、gameCoinMes 游戏币特殊说明信息。该字段一般在有折扣的场景下使用。即用户该次购买不符合后台充值的比率，
					 *   如：购买50元“春节优惠大礼包”，则price为50，gameCoin不传递，gameCoinMes为“春节优惠大礼包”。
					 * 
					 * 备注：
					 * 1、gameCoin和gameCoinMes二者为互斥字段，一种场景下有且只能有一个。
					 * 2、price和GameCoin的比例需要和后台商务配置一致，若不清楚比率，可联系贵方商务咨询。
					 * 3、部分游戏price最小为1，所以测试时建议price>=1.具体情况以商务后台配置为准
					 */
					payData.setPrice(price);
					payData.setGameCoin(price * 10);
					
					swGameSdk.pay(appActivity, payData, new OnPayResponseListener() {
						
						TypeSDKNotify_shunwang notify = new TypeSDKNotify_shunwang();
						PayResultData payResult = new PayResultData();
						@Override
						public void onSdkNoInit() {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("pay no init");
						}
						
						@Override
						public void onPayWindowClose() {
							// TODO Auto-generated method stub
							TypeSDKLogger.w("onPayWindowClose");
							payResult.SetData(AttName.PAY_RESULT, "2");
							payResult.SetData(AttName.PAY_RESULT_REASON, "CANCEL");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onPaySucceed() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("onPaySucceed");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}
						
						@Override
						public void onPayFailed(String errMsg) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("onPayFailed:" + errMsg);
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
							notify.Pay(payResult.DataToString());
						}
					});
					
					
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: "
							+ exception.toString());
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
