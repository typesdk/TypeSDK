package com.type.sdk.android.linyou;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.sdk.android.TypeSDKDefine.AttName;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.*;
import com.sandglass.game.SGGameProxy;

import android.os.Handler;
import android.os.Looper;
public class MainActivity extends BaseMainActivity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);	
    	
    	String result="";
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_SHARE);
        CallInitSDK();
    	TypeSDKLogger.i("result "+result);   	
        TypeSDKLogger.e("android on create finish");
        TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
        		TypeSDKBonjour.Instance().platform.GetData(AttName.CHANNEL_ID), 
        		TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
        update.checkUpdateInfo();
      
  }
  
    
    @Override
    protected void onStart() {
        
        TypeSDKLogger.i("onStart");
        super.onStart();
        TypeSDKBonjour.Instance().onStart(_in_context);
    }

    @Override
    protected void onDestroy(){
        
        TypeSDKLogger.e("sdk do destory");
//      SGGameProxy.instance().onDestroy((Activity) _in_context);
        super.onDestroy();
        TypeSDKBonjour.Instance().onDestroy(_in_context);
        
    }
    
    @Override
    protected void onResume() 
    {
    
        super.onResume();
        TypeSDKBonjour.Instance().onResume(_in_context);
    }
    
    @Override
    protected void onStop() {
        
        super.onStop();
        TypeSDKBonjour.Instance().onStop(_in_context);
    }

    @Override
    protected void onPause() {
        
        super.onPause();
        TypeSDKBonjour.Instance().onPause(_in_context);
    }

    @Override
    protected void onRestart() {
        
        TypeSDKLogger.i("onRestart");
        super.onRestart();
        TypeSDKBonjour.Instance().onRestart(_in_context);
        
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        
        TypeSDKLogger.i("onNewIntent");
        super.onNewIntent(intent);
        TypeSDKBonjour.Instance().onNewIntent(intent);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TypeSDKBonjour.Instance().onActivityResult(_in_context,requestCode,resultCode,data);
    }
    
    public  void CallInitSDK()
    {
      String _in_data = "";     
        TypeSDKBonjour.Instance().initSDK(_in_context,_in_data);  
    }
 
   
    public  void CallLogin(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour.Instance().ShowLogin(_in_context,_in_data);
    }
 
    public  void CallLogout()
    {
    	TypeSDKBonjour.Instance().ShowLogout(_in_context);
    }
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
                        TypeSDKBonjour.Instance().PayItem(_in_context,
                                _in_data);
                    } else {
                        TypeSDKNotify_linyou notify = new TypeSDKNotify_linyou();
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

    public  String CallExchangeItem(String _in_data)
    {
    	return TypeSDKBonjour.Instance().ExchangeItem(_in_context, _in_data);
    }
 
    public  void CallToolBar()
    {
    	TypeSDKBonjour.Instance().ShowToolBar(_in_context);
    }
    public void CallHideToolBar()
    {
    	TypeSDKBonjour.Instance().HideToolBar(_in_context);
    }
    /***
     * ���ⲿ���õ���ʵ�û����ĺ���
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
    	TypeSDKBonjour.Instance().onDestroy(_in_context);
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
    	if (_in_data.equals("support_exit_window")) {          
            return SGGameProxy.instance().has3rdExitView((Activity) _in_context);
        }
        return TypeSDKBonjour.Instance().isHasRequest(_in_data);
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
}
