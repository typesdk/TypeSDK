package com.type.sdk.android.linyou;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.sandglass.game.SGGameProxy;
import com.sandglass.game.interf.SGCommonResult;
import com.sandglass.game.interf.SGExitCallbackInf;
import com.sandglass.game.interf.SGPayCallBackInf;
import com.sandglass.game.interf.SGReportDataBackInf;
import com.sandglass.game.interf.SGRoleOptCallBackInf;
import com.sandglass.game.interf.SGUserListenerInf;
import com.sandglass.game.model.SGConst;
import com.sandglass.game.model.SGGameConfig;
import com.sandglass.game.model.SGResult;
public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	
	public Context appContext;
	public Activity appActivity;
	boolean bl = true;
	
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
			TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
			notify.Init();
			return;
		}		
		this.linYouInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.linyouLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e( "ShowLogout");
		this.linyouLogout();
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
		TypeSDKLogger.e( "ShowToolBar");
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				SGGameProxy.instance().showFloatMenu(appActivity);
			}

		});
	}

	@Override
	public void HideToolBar(Context _in_context) {
		TypeSDKLogger.e( "HideToolBar");
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				SGGameProxy.instance().hideFloatMenu(appActivity);
			}

		});
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		TypeSDKLogger.e(_in_OrderID);
		this.linyouPay(_in_pay);
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
		TypeSDKLogger.e( "SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		TypeSDKLogger.e( "SendInfo:"+_in_data);
		try {
			userInfo.StringToData(_in_data);

            SGGameProxy.instance().setUid(userInfo.GetData(AttName.USER_ID));
            TypeSDKLogger.i("USER_ID:"+userInfo.GetData(AttName.USER_ID));

            //初始化数据
            HashMap<String,String> map=new HashMap<String,String>();
            TypeSDKLogger.i("userInfo"+userInfo.DataToString());
            map.put("roleId",userInfo.GetData(AttName.ROLE_ID));
            map.put("roleName",userInfo.GetData(AttName.ROLE_NAME));
            map.put("roleLevel",userInfo.GetData(AttName.ROLE_LEVEL));
            
            String tmpZoneID =userInfo.GetData(AttName.SERVER_ID);
            
            if(-1 == tmpZoneID.indexOf("s"))
            {
                tmpZoneID = "s1";
                userInfo.SetData(AttName.SERVER_ID ,tmpZoneID);
            }
            
            map.put("zoneId",tmpZoneID);
            
            map.put("zoneName",userInfo.GetData(AttName.SERVER_NAME));
            TypeSDKLogger.i(map.toString());
            SGGameProxy.instance().submitExtendData(appActivity, map);
            
            if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e( "进入游戏时的角色信息");
				if(bl){
					
				} else {
					bl = true;
				}
				
        

				statisticsData("1");
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				bl = false;
				TypeSDKLogger.e( "创建角色时的角色信息");
				SGGameProxy.instance().setUid(userInfo.GetData(AttName.USER_ID));	 
				TypeSDKLogger.i("USER_ID:"+userInfo.GetData(AttName.USER_ID));
				creatRole();
				statisticsData("4");
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e( "角色升级时角色信息");
				levelUp();
				statisticsData("3");
			}else{
				TypeSDKLogger.e( "datatype error:"+"提交的数据不合法");
			}
			// statisticsData("1");
		} catch (Exception e) {
			TypeSDKLogger.e( "上传用户信息:" + e.getMessage());
		}
	}

	/**
	 * 数据统计
	 */
	public void statisticsData(String type){
		String roleName=userInfo.GetData(AttName.ROLE_NAME);
		String roleId=userInfo.GetData(AttName.ROLE_ID);
		String roleLevel=userInfo.GetData(AttName.ROLE_LEVEL);
		SGGameProxy.instance().reportOptData(appActivity, roleName, roleId, roleLevel, type,"","",new SGReportDataBackInf() {
			
			@Override
			public void onCallBack(SGResult result) {
				if(result.isOK()){
					TypeSDKLogger.i("数据上传成功");
				}
			}
		});
	}
	
	/**
	 * 创建角色数据
	 */
	public void creatRole(){
		String roleName=userInfo.GetData(AttName.ROLE_NAME);
		SGGameProxy.instance().createRole(appActivity,roleName,"","", new SGRoleOptCallBackInf() {
			
			@Override
			public void onUpgrade(SGResult result) {
				
			}
			
			@Override
			public void onCreate(SGResult result) {
				if(result.isOK()){
					TypeSDKLogger.i("reateRole success");
				}
			}
		});
	}
	
	/**
	 * 角色升级
	 */
	public void levelUp(){
		String roleName=userInfo.GetData(AttName.ROLE_NAME);
		String roleLevel=userInfo.GetData(AttName.ROLE_LEVEL);	
		SGGameProxy.instance().upgradeRole(appActivity, roleName, roleLevel,"","",new SGRoleOptCallBackInf() {
			
			@Override
			public void onUpgrade(SGResult result) {
				if(result.isOK()){
					TypeSDKLogger.i("levelUp success");
				}
			}
			
			@Override
			public void onCreate(SGResult result) {
				
			}
		});
	}
	
	
	@Override
	public void ExitGame(final Context _in_context) {
		TypeSDKLogger.e( "执行ExitGame方法");
		SGGameProxy.instance().exit(appActivity, new SGExitCallbackInf() {

			@Override
			public void onNo3rdExiterProvide() {
			TypeSDKLogger.i("exit success");
			SGGameProxy.instance().hideFloatMenu(appActivity);
			SGGameProxy.instance().onDestroy((Activity) _in_context);
			appActivity.finish();
			System.exit(0);
			}
			@Override
			public void onExit() {
				// 添加无弹框退出逻辑
				// ..........
			TypeSDKLogger.i("exit success no tuichukuang");
			SGGameProxy.instance().hideFloatMenu(appActivity);
			SGGameProxy.instance().onDestroy((Activity) _in_context);
			appActivity.finish();
			System.exit(0);
			}
		});
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e( "onResume");
		SGGameProxy.instance().onResume((Activity) _in_context);
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e( "onPause");
		SGGameProxy.instance().onPause((Activity) _in_context);
	}

	public void onStop(Context _in_context) {
		TypeSDKLogger.e( "onStop");
		SGGameProxy.instance().onStop((Activity) _in_context);
	}

	public void onDestroy(Context _in_context) {
		TypeSDKLogger.e( "onDestroy");
		SGGameProxy.instance().hideFloatMenu(appActivity);
		SGGameProxy.instance().onDestroy((Activity) _in_context);
	}


	private void linYouInit() {

			TypeSDKLogger.e( "init begin");

		//初始化平台
		SGGameConfig config=new SGGameConfig();
		config.setDebugState(false);
		config.setLocation("cn");
		config.setorientation(SGConst.ORIENTATION_LANDSCAPE);
		config.setProductId(platform.GetData(AttName.PRODUCT_ID));
		config.setSignKey(platform.GetData(AttName.APP_KEY));
		TypeSDKLogger.i("product id:"+platform.GetData(AttName.PRODUCT_ID)+"||appkey:"+platform.GetData(AttName.APP_KEY));
		SGGameProxy.instance().initWithConfig(appActivity, config, new SGCommonResult() {
			@Override
			public void onComplete(SGResult arg0, Bundle arg1) {
				//SGToast.showMessage(appContext,"初始化成功");
				TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
				notify.Init();
				isInit=true;
				TypeSDKLogger.i("init success");
			}
		});

		SGGameProxy.instance().setUserListener(appActivity, userListener);
		SGGameProxy.instance().applicationDestroy(appActivity);
		SGGameProxy.instance().onCreate(appActivity);

		TypeSDKLogger.v( "init done");
	}

	/**
	 * 切换账号
	 */
	private void switchAccount(){
			if (SGGameProxy.instance().isSupportChangeAccount(appActivity, null)) {
				SGGameProxy.instance().changeAccount(appActivity, null);
			} else {
				TypeSDKLogger.i("暂不支持切换账号功能，请调logout接口");
			}
	}
	
	private SGUserListenerInf userListener=new SGUserListenerInf() {

		@Override
		public void onLogout(SGResult ret) {
			if(ret.isOK()){
				TypeSDKLogger.i("LOGOUT SUCCESS");
				TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
				notify.Logout();	
			}else{
				TypeSDKLogger.i("LOGOUT fail");
			}

		}
		@Override
		public void onLogin(SGResult ret) {
			if(ret.isOK()){
				TypeSDKLogger.i("login success");
				String data=ret.getMsg();
				TypeSDKLogger.i(data);
				String encodeData = null ;
				try {
					 encodeData = URLEncoder.encode(data,"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
				notify.sendToken(encodeData);
				SGGameProxy.instance().showFloatMenu(appActivity);
			}
		}
	};
	private void linyouLogin() {

		TypeSDKLogger.i("login start");
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				SGGameProxy.instance().login(appActivity, null);
			}

		});

	}

	private void linyouLogout() {
		TypeSDKLogger.i("logout ");
		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				SGGameProxy.instance().logout(appActivity, null);
				TypeSDKLogger.i("logout success");
			}

		});
	}
	
	private void linyouPay(final PayInfoData _in_pay) {
		TypeSDKLogger.i("pay start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.i("_in_pay:"+_in_pay.DataToString());
				String itemName=_in_pay.GetData(AttName.ITEM_NAME);
				String itemId=_in_pay.GetData(AttName.ITEM_SERVER_ID);
				int price = 0;
				if(TypeSDKTool.isPayDebug){
					price = 1;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE);
				}
				int count=Integer.parseInt(_in_pay.GetData(AttName.ITEM_COUNT));
                if(count <=0){count =1;}
				TypeSDKLogger.i("counta:"+count);
				TypeSDKLogger.i("itemId:"+itemId);
				String callBackInfo=_in_pay.GetData(AttName.BILL_NUMBER);

                SGGameProxy.instance().payFixed(appActivity,itemName, itemId,price,count,callBackInfo,new SGPayCallBackInf() {
					@Override
					public void onPay(SGResult result) {
						PayResultData payResult = new PayResultData();
						TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
						if(result.isOK()){
							TypeSDKLogger.i("pay_success");
							payResult.SetData(AttName.PAY_RESULT, "1");
							payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
							notify.Pay(payResult.DataToString());
						}else{
							TypeSDKLogger.e("pay_fail");
							payResult.SetData(AttName.PAY_RESULT, "0");
							payResult.SetData(AttName.PAY_RESULT_REASON, "pay fail");
							notify.Pay(payResult.DataToString());
						}
					}
				});

			}

		});
	}
	@Override
	public void ShowInvite(Context _in_context, String _in_data) {

	}

@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		return"";
	}

	public void onStart(Context context) {
		TypeSDKLogger.e("onStart");
		SGGameProxy.instance().onStart((Activity) context);
	}

	public void onRestart(Context _in_context) {
		TypeSDKLogger.e("onRestart");
		SGGameProxy.instance().onRestart((Activity) _in_context);
	}

	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
		SGGameProxy.instance().onNewIntent(intent);
	}

	public void onActivityResult(Context _in_context, int requestCode, int resultCode, Intent data) {
		TypeSDKLogger.i("onActivityResult");
		SGGameProxy.instance().onActivityResult((Activity) _in_context, requestCode, resultCode, data);

	}


}
