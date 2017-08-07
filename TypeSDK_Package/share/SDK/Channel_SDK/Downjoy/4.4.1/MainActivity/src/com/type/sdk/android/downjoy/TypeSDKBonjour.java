/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved.
 */

package com.type.sdk.android.downjoy;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.downjoy.CallbackListener;
import com.downjoy.CallbackStatus;
import com.downjoy.Downjoy;
import com.downjoy.InitListener;
import com.downjoy.LoginInfo;
import com.downjoy.LogoutListener;
import com.downjoy.ResultListener;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.unity3d.player.UnityPlayer;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	/** 当乐游戏中心实例 */
    private Downjoy downjoy;
	
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
			TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
			notify.Init();
			return;
		}
		isInit = true;
		this.downjoyInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context,_in_data);
		this.downjoyLogin();
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.downjoyLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
		downjoy.openMemberCenterDialog((Activity) _in_context);
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
		
		downjoy.showDownjoyIconAfterLogined(true);
		//设置悬浮窗显示位置 
		downjoy.setInitLocation(Downjoy.LOCATION_RIGHT_CENTER_VERTICAL);
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return PayItem(_in_context, _in_data);
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
			TypeSDKLogger.e("_in_data:" + _in_data);
			
			if( "" == userInfo.GetData(AttName.SERVER_ID))
			{
				userInfo.SetData(AttName.SERVER_ID,"1");
			}
			if( "" == userInfo.GetData(AttName.SERVER_NAME))
			{
				userInfo.SetData(AttName.SERVER_NAME,"001");
			}
			if( "" == userInfo.GetData(AttName.ROLE_ID))
			{
				userInfo.SetData(AttName.ROLE_ID,"1");
			}
			if( "" == userInfo.GetData(AttName.ROLE_NAME))
			{
				userInfo.SetData(AttName.ROLE_NAME,"001");
			}
			
			downjoy.submitGameRoleData(
					userInfo.GetData(AttName.SERVER_ID), 
					userInfo.GetData(AttName.SERVER_NAME),
					userInfo.GetData(AttName.ROLE_ID), 
					userInfo.GetData(AttName.ROLE_NAME),
					userInfo.GetData(AttName.ROLE_CREATE_TIME), 
					userInfo.GetData(AttName.ROLE_LEVELUP_TIME), 
					userInfo.GetData(AttName.ROLE_LEVEL), 
					new ResultListener() 
					{ @Override
						public void onResult(Object result) 
						{
							Boolean isSuccess = (Boolean) result; 
							TypeSDKLogger.e("submitResult-------->"+ isSuccess.toString());
						} 
					}
					);
			
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
		if(exitGameListenser()){
			final Activity runActivi;
			runActivi = UnityPlayer.currentActivity;
			downjoy.openExitDialog(runActivi, new CallbackListener<String>() {

            @Override
            public void callback(int status, String data) {
                if (CallbackStatus.SUCCESS == status) {
					appActivity.finish();
                	System.exit(0);
                } else if (CallbackStatus.CANCEL == status) {
                	TypeSDKLogger.e("cancel exit " + data);
                }
            }
        });			
		}
		

	}
	
	public void onResume(Context context) {
		TypeSDKLogger.e("onResume");
		if (downjoy != null) {
			downjoy.resume((Activity)context);
		}
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		if (downjoy != null) {
			downjoy.pause();
		}
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		if (downjoy != null) {
			downjoy.destroy();
			downjoy = null;
		}
	}
	
	private void downjoyInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		
	    mainHandler.post(new Runnable() 
	    {
	       @Override
	       public void run() 
	       {
				try {
					TypeSDKLogger.e("initSDK_start");
					// 初始化当乐游戏中心
			        
			        // 获取当乐游戏中心的实例
			        downjoy = Downjoy.getInstance();
					if(downjoy == null){
						TypeSDKLogger.e("downjoy is null");
					}
					
			        // 设置登录成功后属否显示当乐游戏中心的悬浮按钮
			        // 注意：
			        // 此处应在调用登录接口之前设置，默认值是true，即登录成功后自动显示当乐游戏中心的悬浮按钮。
			        // 如果此处设置为false，登录成功后，不显示当乐游戏中心的悬浮按钮。
			        // 正常使用悬浮按钮还需要实现两个函数onResume、onPause
					ShowToolBar(appContext);
			        
			        downjoy.setLogoutListener(new LogoutListener() {
			            
			            @Override
			            public void onLogoutSuccess() {
			            	TypeSDKLogger.e("注销成功");
			            	TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
							notify.Logout();
			            }
			            
			            @Override
			            public void onLogoutError(String msg) {
			            	TypeSDKLogger.e("注销失败："+msg);
			            }
			        });
					
					TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
					notify.Init();
					isInit = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}

	private void downjoyLogin() {
		
		TypeSDKLogger.e("downjoyLogin start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		
	    mainHandler.post(new Runnable() 
	    {
	        	
	       @Override
	       public void run() 
	       {
	    	
	           downjoy.openLoginDialog(appActivity, new CallbackListener<LoginInfo>() {
	        	   @Override
                   public void callback(int status, LoginInfo data) {
					   TypeSDKLogger.i("login:status:" + status + ";dataMsg:" + data.getMsg());
                       if (status == CallbackStatus.SUCCESS && data != null) {
                           String memberId = data.getUmid();
                           String username = data.getUserName();
                           String nickname = data.getNickName();
                           String token = data.getToken();
                           TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
                           notify.sendToken(token, memberId);
                           TypeSDKLogger.e("umid:" + memberId + "\nusername:" + username + "\nnickname:" + nickname + "\ntoken:" + token);
                       } else if (status == CallbackStatus.FAIL && data != null) {
                    	   TypeSDKLogger.e("onError:" + data.getMsg());
                       } else if (status == CallbackStatus.CANCEL && data != null) {
                    	   TypeSDKLogger.e(data.getMsg());
//                    	   downjoyLogin();
                       }
                   }
	        	   
               });
		
	       }
		});
	    
	}

	private void downjoyLogout(){
		
		Handler mainHandler = new Handler(Looper.getMainLooper());
	    mainHandler.post(new Runnable(){
	       @Override
	       public void run(){
		
	    	   downjoy.logout(appContext);
		
	       }
		});
	}
	
	@Override
	protected void SdkPay(Context _in_context, final TypeSDKData.PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
	    mainHandler.post(new Runnable(){
	       @Override
	       public void run() {
	    	   try {
			
			TypeSDKLogger.e("pay_start");
			TypeSDKLogger.e("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
			TypeSDKLogger.e("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
			TypeSDKLogger.e("REAL_PRICE:" + _in_pay.GetData(AttName.REAL_PRICE));
		
			
			float price;
			if(TypeSDKTool.isPayDebug){
				price = 0.01f;
			}else{
				price = _in_pay.GetInt(AttName.REAL_PRICE) * 0.01f;
			}
			
			// 打开支付界面,获得订单号
            downjoy.openPaymentDialog(appActivity,
            		price, 
            		_in_pay.GetData(AttName.ITEM_NAME), 
            		_in_pay.GetData(AttName.ITEM_DESC), 
            		_in_pay.GetData(AttName.BILL_NUMBER), 
            		_in_pay.GetData(AttName.EXTRA),
            		userInfo.GetData(AttName.SERVER_ID),
            		userInfo.GetData(AttName.SERVER_NAME),
            		userInfo.GetData(AttName.ROLE_ID),
            		userInfo.GetData(AttName.ROLE_NAME), new CallbackListener<String>() {
            	PayResultData payResult = new PayResultData();
                @Override
                public void callback(int status, String data) {
                    if (status == CallbackStatus.SUCCESS) {
                    	TypeSDKLogger.e( "payment success! \n data:" + data);
                    	payResult.SetData(AttName.PAY_RESULT, "1");
                    	payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_SUCCESS");
                    } else if (status == CallbackStatus.FAIL) {
                    	TypeSDKLogger.e( "onError:" + data);
                    	payResult.SetData(AttName.PAY_RESULT, "0");
    					payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
                    } else if (status == CallbackStatus.CANCEL){
						TypeSDKLogger.e( "onError:" + data);
                    	payResult.SetData(AttName.PAY_RESULT, "2");
    					payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
					}
                    TypeSDKNotify_downjoy notify = new TypeSDKNotify_downjoy();
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
