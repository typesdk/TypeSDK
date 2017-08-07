package com.type.sdk.android.vivo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventListener;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.utils.*;
import com.type.sdk.android.BaseMainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;

import com.type.sdk.android.vivo.TypeSDKBonjour;
import com.type.sdk.android.vivo.TypeSDKNotify_vivo;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;


public class MainActivity extends BaseMainActivity {
	
//	public static final int REQUEST_CODE_LOGIN = 0;
//	public static final int REQUEST_CODE_PAY = 1;
//	public final static String KEY_LOGIN_RESULT = "LoginResult";
//	public final static String KEY_NAME = "name";
//	public final static String KEY_OPENID = "openid";
//	public final static String KEY_AUTHTOKEN = "authtoken";
//	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	
    	String result="";
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_SHARE);
        
    	
    	TypeSDKLogger.i("result "+result);
    	TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
    			TypeSDKBonjour.Instance().platform.GetData(AttName.CHANNEL_ID), TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
        update.checkUpdateInfo();
    	super.onCreate(savedInstanceState);
    	TypeSDKLogger.i("android on create finish");
//        CallInitSDK();
      
  }
    
//    @Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
////		super.onActivityResult(requestCode, resultCode, data);
//    	TypeSDKLogger.e("onActivityResult");
//    	TypeSDKLogger.e("resultCode:" + resultCode);
//		if(requestCode == REQUEST_CODE_LOGIN){
//			if(resultCode == Activity.RESULT_OK){
//				String loginResult = data.getStringExtra(KEY_LOGIN_RESULT);
//				JSONObject loginResultObj;
//				try {
//					loginResultObj = new JSONObject(loginResult);
//					String name = loginResultObj.getString(KEY_NAME);
//					String openid = loginResultObj.getString(KEY_OPENID);
//					String authtoken = loginResultObj.getString(KEY_AUTHTOKEN);
//					TypeSDKLogger.v("LOGIN_SUCCESS");
//					TypeSDKLogger.v("authtoken:" + authtoken);
//					TypeSDKLogger.v("openid:" + openid);
//					TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
//					notify.sendToken(authtoken, openid);
//					 TypeSDKBonjour.Instance().platform.GetData(AttName.APP_ID);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//				TypeSDKLogger.d("loginResult="+loginResult);
//			}else {
////				MyThread myThread = new MyThread();
////				myThread.start();
////				CallLogin("");
//			}
//		}else if(requestCode == REQUEST_CODE_PAY){
//			Bundle extras = data.getBundleExtra("pay_info");
//			String trans_no = extras.getString("transNo");
//	        boolean pay_result = extras.getBoolean("pay_result");
//			String res_code = extras.getString("result_code");
//			String pay_msg = extras.getString("pay_msg");
//			TypeSDKLogger.e(trans_no);
//			TypeSDKLogger.e("res_code:" + res_code);
//			TypeSDKLogger.e(pay_msg);
//			if(pay_result){
//			PayResultData payResult = new PayResultData();
//			payResult.SetData(AttName.PAY_RESULT, "1");
//			payResult.SetData(AttName.PAY_RESULT_REASON, "SUCCESS");
//			TypeSDKLogger.e("PAY_SUCCESS");
//			TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
//			notify.Pay(payResult.DataToString());
//			}
//		}
//	}
    
//    class MyThread extends Thread{
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			super.run();
//			try {
//				Thread.currentThread();
//				Thread.sleep(3000);
//				CallLogin("");
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//    	
//    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	TypeSDKLogger.e("sdk do destory");
    	TypeSDKBonjour.Instance().onDestroy();
    					
    }

	@Override
    protected void onResume() 
    {
    	super.onResume();
    	TypeSDKBonjour.Instance().onResume(_in_context);
    }
    
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TypeSDKBonjour.Instance().onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour.Instance().onPause();
	}

	/**
     * 通知初始化SDK
     * @param _in_context
     * @param _in_data
     */
    public  void CallInitSDK()
    {
    	String _in_data = "";    	
    	TypeSDKBonjour.Instance().initSDK(_in_context,_in_data);
    }
    /**
     * 通知账号登录
     * @param _in_context
     * @param _in_data
     */
    public  void CallLogin(String _in_data)
    {
    	TypeSDKLogger.e("CallLogin:" + _in_data);
    	TypeSDKBonjour.Instance().ShowLogin(_in_context, _in_data);
    	
    }
    /**
     * 通知账号退出
     * @param _in_context
     */
    public  void CallLogout()
    {
    	TypeSDKBonjour.Instance().ShowLogout(_in_context);
    }
    /***
     * 
     * payData.SetData(U3DSharkAttName.REAL_PRICE,inputStr);
			payData.SetData(U3DSharkAttName.ITEM_NAME,"sk bi");
			payData.SetData(U3DSharkAttName.ITEM_DESC,"desc");
			payData.SetData(U3DSharkAttName.ITEM_COUNT,"1");
			payData.SetData(U3DSharkAttName.ITEM_SEVER_ID,"id");
			payData.SetData(U3DSharkAttName.SEVER_ID,"1");
			payData.SetData(U3DSharkAttName.EXTRA,"extra
			
			
     * 支付商品
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallPayItem(final String _in_data)
    {
    	TypeSDKLogger.i("CallPayItem:" + _in_data);
    	new Thread() {
			@Override
			public void run() {
				String payMessage;
				try {
					payMessage = HttpUtil.http_get(TypeSDKBonjour
							.Instance().platform
							.GetData(AttName.SWITCHCONFIG_URL));
					if (((payMessage.equals("") || payMessage.isEmpty()) && openPay)
							|| TypeSDKTool.openPay(TypeSDKBonjour
									.Instance().platform
									.GetData(AttName.SDK_NAME), payMessage)) {
						Handler mHandler = new Handler(Looper.getMainLooper());
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TypeSDKBonjour.Instance().PayItem(_in_context, _in_data);
							}
						});
					} else {
						TypeSDKNotify_vivo notify = new TypeSDKNotify_vivo();
						TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						notify.Pay(payResult.DataToString());
						Handler dialogHandler = new Handler(Looper.getMainLooper());
						dialogHandler.post(new Runnable(){
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TypeSDKTool.showDialog("暂未开放充值！！！", _in_context);
							}});							
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();
		return "client pay function finished";
    }
    /***
     * 
     * 锟斤拷锟解部call 锟斤拷 锟角讹拷锟斤拷支锟斤拷锟斤拷锟斤拷一锟斤拷贫锟斤拷锟斤拷锟狡凤拷锟�
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallExchangeItem(final String _in_data)
    {
    	TypeSDKLogger.e("CallExchangeItem:" + _in_data);
		return TypeSDKBonjour.Instance().ExchangeItem(_in_context, _in_data);
    }
    
    /***
     * 锟斤拷锟解部锟斤拷锟矫碉拷 锟斤拷实锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
     * @param _in_context
     */
    public  void CallToolBar()
    {
    	TypeSDKBonjour.Instance().ShowToolBar(_in_context);
    }
    public void CallHideToolBar()
    {
    	TypeSDKBonjour.Instance().HideToolBar(_in_context);
    }
    /***
     * 锟斤拷锟解部锟斤拷锟矫碉拷锟斤拷实锟矫伙拷锟斤拷锟侥猴拷锟斤拷
     * @param _in_context
     */
    public  void CallPersonCenter()
    {
    	TypeSDKBonjour.Instance().ShowPersonCenter(_in_context);
    }
    public void CallHidePersonCenter()
    {
    	TypeSDKBonjour.Instance().HidePersonCenter(_in_context);
    }
    public void CallShare(String _in_data)
    {
    	TypeSDKBonjour.Instance().ShowShare(_in_context, _in_data);
    }
    public void CallSetPlayerInfo(String _in_data)
    {
    	TypeSDKBonjour.Instance().SetPlayerInfo(_in_context, _in_data);
    }
    public void CallExitGame()
    {
    	TypeSDKBonjour.Instance().ExitGame(_in_context);
    }
    public void CallDestory()
    {
    	TypeSDKBonjour.Instance().onDestroy();
    }
    public int CallLoginState()
    {
    	return TypeSDKBonjour.Instance().LoginState(_in_context);
    }
    public String CallUserData()
    {
    	return TypeSDKBonjour.Instance().GetUserData();
    }
    public String CallPlatformData()
    {
    	return TypeSDKBonjour.Instance().GetPlatformData();
    }
    public boolean CallIsHasRequest(String _in_data)
    {
    	return TypeSDKBonjour.Instance().isHasRequest(_in_data);
    }
    
    public String CallAnyFunction(String FuncName,String _in_data)
    {
    	Method[] me = TypeSDKBonjour.Instance().getClass().getMethods();
    	for(int i = 0;i<me.length;++i)
    	{
    		if(me[i].getName().equals(FuncName))
    		{
    			try 
    			{
					return (String) me[i].invoke(TypeSDKBonjour.Instance(),_in_context ,_in_data);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    	return "error";
    }
    
	
	public void AddLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour.Instance().AddLocalPush(_in_context, _in_data);
    }
    
    public void RemoveLocalPush(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour.Instance().RemoveLocalPush(_in_context, _in_data);
    }
    
    public void RemoveAllLocalPush()
    {
    	
    	TypeSDKBonjour.Instance().RemoveAllLocalPush(_in_context);
    }
    
}
