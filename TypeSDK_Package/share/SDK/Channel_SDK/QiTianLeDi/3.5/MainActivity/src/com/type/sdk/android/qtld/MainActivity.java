package com.type.sdk.android.qtld;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKUpdateManager;
import com.ledi.util.Operate;

import android.app.Activity;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.*;
import android.os.Handler;
import android.os.Looper;
public class MainActivity extends BaseMainActivity
{	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	String result="";
    	result += "~"+ TypeSDKBonjour.Instance().isHasRequest(
						TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
		result += "~"+ TypeSDKBonjour.Instance().isHasRequest(
						TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
		result += "~"+ TypeSDKBonjour.Instance().isHasRequest(
						TypeSDKDefine.AttName.SUPPORT_SHARE);       
    	TypeSDKLogger.i("result "+result);
    	//检查apk更新
		TypeSDKUpdateManager update = new TypeSDKUpdateManager(this,
				TypeSDKBonjour.Instance().platform.GetData(AttName.CHANNEL_ID), 
				TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
      update.checkUpdateInfo();         	
      TypeSDKLogger.i("android on create finish");
    }
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	TypeSDKLogger.e("sdk do destory");
    	TypeSDKBonjour.Instance().onDestroy();    	
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	String userId = TypeSDKBonjour.UId;
    	TypeSDKLogger.e("hasFocus:"+String.valueOf(hasFocus)+"uid:"+userId);
    	if(hasFocus){   		    		
    		TypeSDKLogger.e(userId);
    		if(userId!=null&&!userId.equals("")){
    			Operate.showFloatView((Activity)_in_context, 0, 10, true);
    		}    		
    	}
    	super.onWindowFocusChanged(hasFocus);
    }
    public  void CallInitSDK()
    {
    	String _in_data = "";
    	TypeSDKBonjour.Instance().initSDK(_in_context,_in_data);
    }
    /**
     * 锟斤拷锟解部 call锟斤拷 login锟斤拷锟斤拷
     * @param _in_context
     * @param _in_data
     */
    public  void CallLogin(String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
    	TypeSDKBonjour.Instance().ShowLogin(_in_context,_in_data);
    }
    /**
     * 锟斤拷锟解部 call 锟斤拷logout锟斤拷锟斤拷
     * @param _in_context
     */
    public  void CallLogout()
    {
    	TypeSDKBonjour.Instance().ShowLogout(_in_context);
    }
    public  String CallPayItem(final String _in_data)
    {
    	TypeSDKLogger.i(_in_data);
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
						TypeSDKNotify_QTLD notify = new TypeSDKNotify_QTLD();
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
     * 锟斤拷锟解部call 锟斤拷 锟角讹拷锟斤拷支锟斤拷锟斤拷锟斤拷一锟斤拷贫锟斤拷锟斤拷锟狡凤拷锟�
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallExchangeItem(String _in_data)
    {
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
    	TypeSDKBonjour.Instance().onDestroy(); ;
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
    public void CallCopyClipboard(String _in_data)
    {
    	TypeSDKBonjour.Instance().OnCopyClipboard(_in_context, _in_data);
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
