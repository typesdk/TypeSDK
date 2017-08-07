package com.type.sdk.android.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.unity3d.player.UnityPlayer;
import com.weibo.game.sdk.WeiboGameSDKAPI;
import com.weibo.game.sdk.callback.SinaGameCallBack;
import com.weibo.game.sdk.callback.SinaUser;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	
	public static final int START = 0;
    public static final int SUCESS = 1;
    public static final int ERROR = 2;
    public static final int CANCEL = 3;
    public static final int TIMEOUT = 4;
    
    private int exit = 0;
    
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
			TypeSDKNotify_sina notify = new TypeSDKNotify_sina();
			notify.Init();
			return;
		}
		
		this.sinaInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.sinaLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.sinaLogout();
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
		this.sinaPay(_in_pay);

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
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
			Handler mainHandler = new Handler(Looper.getMainLooper());
				mainHandler.post(new Runnable() {
				@Override
				public void run() {
					WeiboGameSDKAPI.exit(appActivity, new SinaGameCallBack() {
						
						@Override
						public void onResult(int code, Object obj) {
							// TODO Auto-generated method stub
							switch (code) {
			                case SUCESS:
			                	TypeSDKLogger.d("exit success");
			                	appActivity.finish();
			                	System.exit(0);
			                    break;
			                case CANCEL:
			                    TypeSDKLogger.d("exit cancel");
			                    exit = 0;
			                    break;
			                default:
			                    break;
			                }
						}
					});
					
				}
			});
		
	}

	public void onResume(Context context) {
		TypeSDKLogger.e("onResume");
	}
	
	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
	}
	
	public void onRestart() {
		TypeSDKLogger.e("onRestart");
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}
	
	public void onStart(Context context) {
		TypeSDKLogger.e("onStart");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void sinaInit() {

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
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					// SDK初始化
					/** 第一步：初始化 */
			        WeiboGameSDKAPI.init(appActivity);
			        TypeSDKLogger.e("init success");
			        
			        isInit = true;
			        
			        TypeSDKNotify_sina notify = new TypeSDKNotify_sina();
					notify.Init();
												
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void sinaLogin() {
		  TypeSDKLogger.e("start login");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				
				WeiboGameSDKAPI.login(appActivity, new SinaGameCallBack() {
					
					@Override
					public void onResult(int code, Object obj) {
						// TODO Auto-generated method stub
						switch (code) {
		                case SUCESS: //成功
		                	TypeSDKLogger.e("login success");
		                    SinaUser user = (SinaUser) obj;
		                    TypeSDKLogger.d(user.toString());
		                    TypeSDKNotify_sina notify = new TypeSDKNotify_sina();
							notify.sendToken(user.getToken() + "|" + user.getDeviceId(), user.getSuid());
							TypeSDKLogger.i("token:"+user.getToken() + "|" + user.getDeviceId()+"  userid:"+user.getSuid());
		                    break;
		                case CANCEL: //cancel
		                	TypeSDKLogger.e("login cancel");
		                	// sinaLogin();
		                    break;
		                case START: //start
		                	TypeSDKLogger.e("login start");
		                	 //如果需要可以加入相关统计信息,通常不处理
		                case ERROR: //error
		                	TypeSDKLogger.e("login error:" + obj.toString());
		                default:
		                TypeSDKLogger.e("default");
		                    break;
						}
					}
				});
				
			}
		});

	}

	private void sinaLogout() {
		WeiboGameSDKAPI.logout(appActivity);
		TypeSDKNotify_sina notify = new TypeSDKNotify_sina();
		notify.Logout();
	}

	private void sinaPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {

				try {

					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
					TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
					TypeSDKLogger.e("SERVER_ID:" + userInfo.GetData(AttName.SERVER_ID));
					TypeSDKLogger.e("SERVER_NAME:" + userInfo.GetData(AttName.SERVER_NAME));
					TypeSDKLogger.e("ROLE_ID:" + userInfo.GetData(AttName.ROLE_ID));
					TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
					TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
					int price = 0;
		            if(TypeSDKTool.isPayDebug){
			           price = 1;
		            }else{
			           price = _in_pay.GetInt(AttName.REAL_PRICE);
		            }
					WeiboGameSDKAPI.pay(appActivity, price,//_in_pay.GetInt(AttName.REAL_PRICE)
							_in_pay.GetData(AttName.ITEM_NAME), _in_pay.GetData(AttName.ITEM_DESC), 
							_in_pay.GetData(AttName.BILL_NUMBER), new SinaGameCallBack() {
								
								@Override
								public void onResult(int code, Object obj) {
									// TODO Auto-generated method stub
									PayResultData payResult = new PayResultData();
									TypeSDKNotify_sina notify = new TypeSDKNotify_sina();
									switch (code) {
					                case SUCESS:
					                	TypeSDKLogger.e("pay_success");
										payResult.SetData(AttName.PAY_RESULT, "1");
										payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
										notify.Pay(payResult.DataToString());
										break;
					                case CANCEL:
					                	TypeSDKLogger.e("pay_cancel");
										payResult.SetData(AttName.PAY_RESULT, "2");
										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
										notify.Pay(payResult.DataToString());
					                    break;
					                case TIMEOUT:
					                	TypeSDKLogger.e("pay_timeout");
										payResult.SetData(AttName.PAY_RESULT, "0");
										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
										notify.Pay(payResult.DataToString());
					                    break;
					                case START:
									
										break;
					                case ERROR:
					                	TypeSDKLogger.e("pay_fail");
										payResult.SetData(AttName.PAY_RESULT, "0");
										payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
										notify.Pay(payResult.DataToString());
					                    break;
					                default:
					                    break;
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
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
