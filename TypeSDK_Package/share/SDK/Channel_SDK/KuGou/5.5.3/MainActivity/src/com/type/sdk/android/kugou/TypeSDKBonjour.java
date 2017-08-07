package com.type.sdk.android.kugou;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

import com.kugou.game.sdk.api.common.ActivityOrientation;
import com.kugou.game.sdk.api.common.OnPlatformEventListener;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.api.online.OnlineConfig;
import com.kugou.game.sdk.api.common.IEventCode;
import com.kugou.game.sdk.api.common.IEventDataField;
import com.kugou.game.sdk.api.common.User;
import com.kugou.game.sdk.api.common.KGPayInfo;
import com.kugou.game.sdk.api.common.OnExitListener;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean haslogin = false;
	private boolean isShowToolBar;
	
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
			TypeSDKNotify_kugou notify = new TypeSDKNotify_kugou();
			notify.Init();
			return;
		}
		this.kugouInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.kugouLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.kugouLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
//		KGPlatform.enterUserCenter(appActivity);
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
		this.kugouPay(_in_pay);
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
			
			String extendInfo = new StringBuilder()
			.append("gameId=").append(platform.GetData(AttName.APP_ID))
			.append("&service=").append(userInfo.GetData(AttName.SERVER_NAME))
			.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
			.append("&grade=").append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			
			KGPlatform.sendEnterGameStatics(userInfo.GetData(AttName.ROLE_NAME), 
					userInfo.GetInt(AttName.ROLE_LEVEL), 
					Integer.parseInt(userInfo.GetData(AttName.SERVER_ID)));
			TypeSDKLogger.e("extendInfo:" + extendInfo);
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
			@Override
			public void run() {
				KGPlatform.exitGame(appActivity, userInfo.GetData(AttName.ROLE_NAME), userInfo.GetInt(AttName.ROLE_LEVEL), new OnExitListener() {
					
					@Override
					public void exitGame(int state) {
						// TODO Auto-generated method stub
						// state代表是否会弹出礼包领取对话框
		                if (state == OnExitListener.STATE_GIFTBAG_NO) {
		                    // 做退出时没有礼包领取对话框的逻辑，在这里游戏调用退出逻辑，Demo用finish模拟
		                    appActivity.finish();
		                    // 记得调用SDK的退出函数，释放资源
		                    KGPlatform.release(true);
		                    System.exit(0);
		                }
						
					}

					@Override
					public void onDialogDismiss() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});			
		}
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		if (isInit) {
			// 显示悬浮窗
			if(!isShowToolBar){
				KGPlatform.showToolBar(appActivity);
				isShowToolBar = true;
			}
			
		}
		
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if(isInit){
			if(isShowToolBar){
				KGPlatform.hideToolBar(appActivity);
				isShowToolBar = false;
			}
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		//if (toolBar != null) {
			//toolBar.hide();
        //}
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");		
		// 销毁悬浮窗。注意：在界面被销毁前，必须执行该方法，建议在onDestroy()里执行
        if (isInit) {
        	KGPlatform.recycleToolBar(appActivity);
			//toolBar.recycle();
        	//toolBar = null;
        }
	}

	private void kugouInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.d("SDK_CP_ID:" + platform.GetInt(AttName.SDK_CP_ID));
					TypeSDKLogger.d("APP_ID:" + platform.GetInt(AttName.APP_ID));
					TypeSDKLogger.d("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					TypeSDKLogger.d("PRODUCT_ID:" + platform.GetInt(AttName.PRODUCT_ID));
					OnlineConfig sdkConfig = new OnlineConfig();
					/** --------填写SDK的必选配置项，参数来自酷狗提供的配置文档------------- */
			        // 对应配置文档参数--MerchantId
			        sdkConfig.setMerchantId(platform.GetInt(AttName.SDK_CP_ID));
			        // 对应配置文档参数--AppId
			        sdkConfig.setAppId(platform.GetInt(AttName.APP_ID));
			        // 对应配置文档参数--AppKey
			        sdkConfig.setAppKey(platform.GetData(AttName.APP_KEY));
			        // 对应配置文档参数--GameId
			        sdkConfig.setGameId(platform.GetInt(AttName.PRODUCT_ID));
			        // 对应配置文档参数--code ( 注意！！code内容里不要有换行)
			        sdkConfig.setCode(platform.GetData("code"));

					/** --------填写SDK的必选配置项，参数来自酷狗提供的配置文档------------- */
					// 对应配置文档参数--MerchantId
//					sdkConfig.setMerchantId(1);
					// 对应配置文档参数--AppId
//					sdkConfig.setAppId(1138);
					// 对应配置文档参数--AppKey
//					sdkConfig.setAppKey("KU5DJ8fDjeHGRYui0G2khkIUrcJJ2Ii2");
					// 对应配置文档参数--GameId
//					sdkConfig.setGameId(10396);
					// 对应配置文档参数--code ( 注意！！code内容里不要有换行)
//					sdkConfig
//							.setCode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSOEB36K6OmrgvCFocDsyd8eStPQfWrdpqLt5CNmrklXMg/DUkbu3GuiXW52oHAJw65u7LJzKqJSbhFp7e7X4ib3qlq8O5D3lF4yEjyzwgVTQkpluqmKfErkSu7ruMUi0Y++ApeC3YREy8bpWdhJIf308FIDw67qbSa32kTfIU7wIDAQAB");

					/** --------设置SDK的可选配置项，具体可选项定义参看使用文档------------- */
					// 设置SDK界面的横竖屏
 //     			sdkConfig.setActivityOrientation(ActivityOrientation.LANDSCAPE);
			        /** --------设置SDK的可选配置项，具体可选项定义参看使用文档------------- */
				        TypeSDKLogger.e("sdkConfig.getAppId():" + sdkConfig.getAppId());
				        TypeSDKLogger.e("sdkConfig.getMerchantId():" + sdkConfig.getMerchantId());
				        TypeSDKLogger.e("sdkConfig.getAppKey()" + sdkConfig.getAppKey());
				        TypeSDKLogger.e("sdkConfig.getGameId()" + sdkConfig.getGameId());
				        TypeSDKLogger.e("sdkConfig.getCode()" + sdkConfig.getCode());

			        /** --------初始化SDK------------- */
			        // SDK事件回调接口
			        // 初始化SDK(--必须先初始化SDK后，才能使用SDK的功能---)
			        
			        KGPlatform.init(appContext, sdkConfig, new OnPlatformEventListener() {
						
						@Override
						public void onEventOccur(int eventCode, Bundle data) {
							// TODO Auto-generated method stub
							TypeSDKNotify_kugou notify = new TypeSDKNotify_kugou();
							switch (eventCode) {
				            case IEventCode.ENTER_GAME_SUCCESS:
				                // 获取登录成功后的用户信息
				            	haslogin = true;
				                User user = (User) data.getSerializable(IEventDataField.EXTRA_USER);
				                TypeSDKLogger.d("UserName:" + user.getOpenId());// 用户唯一标识
				                TypeSDKLogger.d("getToken:" + user.getToken());// 登录token，可用于服务端登录验证
				                TypeSDKLogger.d("getUnixTime:" + user.getUnixTime());// SDK时间戳
				                TypeSDKLogger.d("getKugouid:" + user.getKugouid());// 用户id
				                // 发送登录SDK成功广播通知界面
		    					notify.sendToken(user.getToken(), user.getOpenId());
		    					KGPlatform.showWelcomeDialog(appActivity);
								// 显示悬浮窗
								KGPlatform.showToolBar(appActivity);
								isShowToolBar = true;
		    					// 创建浮动工具栏
		    					// 注意：1、悬浮窗必须在**登录成功后**进行创建；2、ToolBar不是单例模式，不要重复创建，不然会生成多个悬浮窗。
		    					/* if(toolBar == null){
		    						toolBar = new ToolBar(appContext, ToolBar.LEFT_MID);
		    						toolBar.show();
		    				        // 设置悬浮窗收拢
		    				        toolBar.setCustomViewVisibility(View.GONE);
		    					} */
				                break;
				            case IEventCode.ENTER_GAME_FAILED:
				                String errorMsg = data.getString(IEventDataField.EXTRA_ERROR_MESSAGE);
				                TypeSDKLogger.e("登录SDK失败：" + errorMsg);
				                haslogin = false;
				                break;
				            case IEventCode.ACCOUNT_CHANGED_SUCCESS:
				                TypeSDKLogger.d("切换账号成功");
				                haslogin = false;
				        		notify.Logout();
				                break;
				            case IEventCode.REGISTER_SUCCESS:
				            	TypeSDKLogger.e("注册成功");
				                break;
				            case IEventCode.RECHARGE_SUCCESS:
				            	TypeSDKLogger.e("充值成功");
								TypeSDKLogger.e("pay_success");
				                PayResultData payResult = new PayResultData();
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
								notify.Pay(payResult.DataToString());
				                break;
				            case IEventCode.INTENT_TO_REBOOT_APP:
				                TypeSDKLogger.d("即将重启游戏");
				                break;
				            case IEventCode.GO_BACK_TO_GAME:
				                User info = KGPlatform.getCurrentUser();
				                if (info != null) {// 已登录SDK
				                	TypeSDKLogger.e("退出sdk回到游戏");
//				                    SharkSDKNotify_kugou notify2 = new SharkSDKNotify_kugou();
//				            		notify2.Logout();
				                	if (!haslogin) {
										kugouLogin();
									}
				                } else {
				                	TypeSDKLogger.e("退出sdk回到游戏(还未登录)");
				                    kugouLogin();
				                }
				                break;
							}
						}
					});
					// 设置开发模式：true为开发调试包；false为正式上线包
					// 初始化sdk后调用！！
					KGPlatform.setDebugMode(TypeSDKTool.isPayDebug);
			        TypeSDKLogger.e("initSDK_success");
			        isInit = true;
					TypeSDKNotify_kugou notify = new TypeSDKNotify_kugou();
					notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void kugouLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				KGPlatform.enterGame(appActivity);
			}
		});

	}


	private void kugouLogout() {
		TypeSDKNotify_kugou notify = new TypeSDKNotify_kugou();
		notify.Logout();
	}
	

	private void kugouPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("pay_start");
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
					KGPayInfo payInfo = new KGPayInfo();
		            // 订单号，非空唯一且不能重复
		            payInfo.setOrderId(_in_pay.GetData(AttName.BILL_NUMBER));
		            // 区服id，如果没有则填默认值1
		            payInfo.setServerId(getServerId(_in_pay.GetData(AttName.SERVER_ID)));
		            // 游戏角色名
		            payInfo.setRoleName(_in_pay.GetData(AttName.ROLE_NAME));
		            // 扩展参数1
		            payInfo.setExtension1(_in_pay.GetData(AttName.EXTRA));
		            // 扩展参数2
//		            payInfo.setExtension2("ext2");
		            // 进入【定额充值】中心(登录SDK后再调用)
					KGPlatform.enterRechargeCenter(appActivity, payInfo, price);
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
				}

			}
		});

	}
	
	public static int getServerId(String itemName){
		int count = 0;
		String countString = "";
		for (int i = 0; i < itemName.length(); i++) {
			if ('0' <= itemName.charAt(i) && itemName.charAt(i) <= '9') {
				countString += itemName.charAt(i);
			}
		}
		if (!countString.isEmpty()) {
			count = Integer.parseInt(countString);
		}
		TypeSDKLogger.e("getItemCount:" + count);
		return count;
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
