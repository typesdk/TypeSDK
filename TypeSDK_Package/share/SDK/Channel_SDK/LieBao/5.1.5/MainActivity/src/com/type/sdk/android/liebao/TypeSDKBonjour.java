package com.type.sdk.android.liebao;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.lb.sdk.Constants;
import com.lb.sdk.LBSDK;
import com.lb.sdk.bean.LoginResult;
import com.lb.sdk.bean.PayParams;
import com.lb.sdk.bean.Response;
import com.lb.sdk.bean.UserExtraData;
import com.lb.sdk.listener.ILBSDKListener;
import com.lb.sdk.plugin.LBData;
import com.lb.sdk.plugin.LBPay;
import com.lb.sdk.plugin.LBUser;
import com.lb.sdk.plugin.LBVersion;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	private boolean isDebug;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		if (isInit) {
			TypeSDKLogger.i("already init"+isInit);
			TypeSDKNotify_LieBao notify = new TypeSDKNotify_LieBao();
			notify.Init();
			return;
		}
		isDebug = TypeSDKTool.isPayDebug;
		TypeSDKLogger.i("initSDK begin");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		this.lieBaoInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.lieBaoSdkLogIn();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.lieBaoSdkLogout();
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
		this.lieBaoSdkPay(_in_pay);
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
	public void SetPlayerInfo(Context _in_context, final String _in_data) {
		TypeSDKLogger.e("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);
			
			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.e("进入游戏时的角色信息");
				submitUserData(userInfo,1);
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.e( "创建角色时的角色信息");
				submitUserData(userInfo,4);
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.e( "角色升级时角色信息");	
				submitUserData(userInfo,5);
			}else{
				TypeSDKLogger.e("datatype error:"+"提交的数据不合法");

			}
			
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}
	}
	private void submitUserData(final TypeSDKData.UserInfoData userInfo,final int infoType){
		LBSDK.getInstance().runOnMainThread(new Runnable() {

			@Override
			public void run() {
				UserExtraData userData = new UserExtraData();
				userData.setDataType(infoType);//数据统计类型 1：登录 2：注册 3：登出 4：创建角色 5：角色升级
				userData.setMoneyNum(10);//玩家剩余金币
				userData.setRoleID(userInfo.GetData(AttName.ROLE_ID));//角色编号
				userData.setRoleLevel(userInfo.GetData(AttName.ROLE_LEVEL));//角色等级
				userData.setRoleName(userInfo.GetData(AttName.ROLE_NAME));//角色名称
				userData.setServerID(userInfo.GetData(AttName.SERVER_ID));//区服编号
				userData.setServerName(userInfo.GetData(AttName.SERVER_NAME));//区服名称
				userData.setUid(userInfo.GetData(AttName.USER_ID));//玩家编号
				userData.setAttach("0");//扩展字段 
				TypeSDKLogger.i(userData.toString()+22222);
				LBData.getInstance().submitUserData(userData);
			}
		});
	}

	@Override
	public void ExitGame(Context _in_context) {
		if(exitGameListenser()){
			TypeSDKLogger.e("执行ExitGame方法");
			LBSDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				LBUser.getInstance().exit();
			}
		});			
		}
		

	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause(Context _in_context) {
		
		TypeSDKLogger.e( "onPause");
	}

	public void onStop() {
		TypeSDKLogger.e( "onStop");
	}

	public void onDestroy() {
	
		TypeSDKLogger.e( "onDestroy");
	}

	private void lieBaoInit() {
		LBSDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				if(isInit){
				}else{
					
					LBSDK.getInstance().init(appActivity);
					LBVersion.getInstance().checkVersion();
					TypeSDKNotify_LieBao notify = new TypeSDKNotify_LieBao();
					notify.Init();
					isInit=true;
					TypeSDKLogger.i( "liebaoInit_SUCCESS");
					//调出sdk升级功能
					LBVersion.getInstance().checkVersion();
					//监听各种事件的回调
					LBSDK.getInstance().setSDKListener(new ILBSDKListener() {
						
						@Override
						public void onResult(Response arg0) {
							//=======普通回调=========
						}
				
						@Override
						public void onPayResult(Response pay) {
							//=======支付回调=========
							PayResultData payResult = new PayResultData();
							TypeSDKNotify_LieBao notify=new TypeSDKNotify_LieBao();
							if(pay.getCode()==Constants.RESULT_CODE_SUCCESS){
								TypeSDKLogger.i("pay_SUCCESS");
								payResult.SetData(AttName.PAY_RESULT, "1");
								payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
								notify.Pay(payResult.DataToString());
							}else {
								TypeSDKLogger.e("pay_FAIL");
								payResult.SetData(AttName.PAY_RESULT, "0");
								payResult.SetData(AttName.PAY_RESULT_REASON, "Fail");
								notify.Pay(payResult.DataToString());
							}
						}
				
						@Override
						public void onLogout(Response logout) {
							//=======注销回调=========
							//游戏账号退出
							if(logout.getCode()==Constants.RESULT_CODE_SUCCESS){
								TypeSDKLogger.i("logout_SUCCESS");
								TypeSDKNotify_LieBao notify=new TypeSDKNotify_LieBao();
								notify.Logout();
								//提交游戏数据
								submitUserData(userInfo, 3);
							}else{
								TypeSDKLogger.i("logut_FAIL");
							}
						}
				
						@Override
						public void onLoginResult(LoginResult login) {
							//=======登录回调=========
							if(login.getCode()==Constants.RESULT_CODE_SUCCESS){
								TypeSDKLogger.i("login_SUCCESS"+login.getCode()+"||"+login.getMsg());
								TypeSDKNotify_LieBao notify=new TypeSDKNotify_LieBao();
								notify.sendToken(login.getUsername()+"|"+login.getLogintime(),login.getSign());
							}else{
								TypeSDKLogger.e("login_FAIl"+login.getMsg());
							}
						}
				
						@Override
						public void onExit(Response exit) {
							TypeSDKLogger.e("onExit()");
							if (exit.getCode() == Constants.RESULT_CODE_SUCCESS){
								TypeSDKLogger.i("exit_SUCCESS");
								TypeSDKNotify_LieBao notify=new TypeSDKNotify_LieBao();
								notify.Logout();
								appActivity.finish();
								System.exit(0);
							}else{
								TypeSDKLogger.e("exit_FAIL:"+exit.getMsg());
							}
						}
					});
					
				}
			}
		});
	}

	private void lieBaoSdkLogIn() {
		LBSDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.i("login"+1111);
				LBSDK.getInstance().runOnMainThread(new Runnable() {
					@Override
					public void run() {
						LBUser.getInstance().login();
					}
				});
			}
		});
	}
	
	private void lieBaoSdkLogout() {
		LBSDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				TypeSDKLogger.i("logout");
				submitUserData(userInfo, 3);
				LBUser.getInstance().logout();
			}
		});
	}

	private void lieBaoSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.i("receive pay data: " + _in_pay.DataToString());
		LBSDK.getInstance().runOnMainThread(new Runnable() {
			@Override
			public void run() {
				int price;
				if(isDebug){
					price = 1;
				}else{
					price = (int)(_in_pay.GetInt(AttName.REAL_PRICE)/100);
				}
				PayParams params = new PayParams();
				params.setRoleId(_in_pay.GetData(AttName.ROLE_ID));  //角色编号
				params.setRoleName(_in_pay.GetData(AttName.ROLE_NAME));
				params.setRoleName(_in_pay.GetData(AttName.ROLE_LEVEL));
				params.setRoleName(_in_pay.GetData(AttName.SERVER_NAME));
				params.setPrice(String.valueOf(price));		//充值金额（整型）
				params.setAttach(_in_pay.GetData(AttName.BILL_NUMBER));//扩展字段（订单号）
				params.setProductDesc(_in_pay.GetData(AttName.ITEM_DESC));//商品描述
				params.setProductName(_in_pay.GetData(AttName.ITEM_NAME));//商品名称
				params.setServerId(_in_pay.GetData(AttName.SERVER_ID));//区服编号
				TypeSDKLogger.i(_in_pay.GetData(AttName.ROLE_ID)+"|"+price+"|"+_in_pay.GetData(AttName.BILL_NUMBER)+"|"+_in_pay.GetData(AttName.ITEM_DESC)+
						"|"+_in_pay.GetData(AttName.ITEM_NAME)+"|"+_in_pay.GetData(AttName.SERVER_ID));
				LBPay.getInstance().pay(params);
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
	public void onStart(Context _in_context) {
		
		TypeSDKLogger.e("onStart");
	}
	public void onStop(Context _in_context) {
		
		TypeSDKLogger.e("onStop");
	}

	public void onRestart(Context _in_context) {
		
		TypeSDKLogger.e("onRestart");
	}

	public void onBackPressed(Context _in_context) {
		
		TypeSDKLogger.e("onBackPressed");

	}

	public void onNewIntent(Intent intent) {
		TypeSDKLogger.e("onNewIntent");
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}

}
