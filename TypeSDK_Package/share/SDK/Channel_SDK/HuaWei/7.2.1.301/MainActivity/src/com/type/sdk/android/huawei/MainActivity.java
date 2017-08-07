package com.type.sdk.android.huawei;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;

import com.type.utils.*;
import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;

import android.app.Activity;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.huawei.TypeSDKNotify_huawei;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
public class MainActivity extends BaseMainActivity{

	/**
     * �ṩ���ⲿ call�ĺ���
     */
    // public static boolean changeUserReceiver = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	
    	String result="";
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.REQUEST_INIT_WITH_SEVER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_PERSON_CENTER);
    	result +="~"+ TypeSDKBonjour.Instance().isHasRequest(TypeSDKDefine.AttName.SUPPORT_SHARE);
        
    	TypeSDKLogger.i("result "+result);
    	super.onCreate(savedInstanceState);
        TypeSDKLogger.i( "android on create finish");
//        TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
//        		TypeSDKBonjour.Instance().platform.GetData(AttName.CHANNEL_ID), TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
//        update.checkUpdateInfo();
      
	}
  
    
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	TypeSDKLogger.e( "sdk do destory");
    	TypeSDKBonjour.Instance().onDestroy();
    }

    
    @Override
    protected void onResume() 
    {
    	super.onResume();
    	TypeSDKBonjour.Instance().onResume(_in_context);
    	// if (TypeSDKBaseBonjour.isInit && changeUserReceiver) {//this.getIntent().getStringExtra("from") != null
     //  	  TypeSDKLogger.i("changeuserinfo");
     //  	changeUserReceiver = false;
     //  	  TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
  			// notify.Logout();
     //    }
    }
    
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour.Instance().onPause();
	}
	
    /**
     *  ���ⲿ call �� init����
     * @param _in_context
     * @param _in_data
     */
    public  void CallInitSDK()
    {
    	String _in_data = "";
    	
    	TypeSDKBonjour.Instance().initSDK(_in_context,_in_data);
    }
    /**
     * ���ⲿ call�� login����
     * @param _in_context
     * @param _in_data
     */
    public  void CallLogin(String _in_data)
    {
    	TypeSDKLogger.i("call login" +  _in_data);
    	TypeSDKBonjour.Instance().ShowLogin(_in_context,_in_data);
    }
    /**
     * ���ⲿ call ��logout����
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
			
			
     * ���ⲿcall�Ķ���֧������(rmb�һ� ��Ϸ��)
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallPayItem(final String _in_data)
    {
    	TypeSDKLogger.i("CallPayItem" +  _in_data);
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
						TypeSDKNotify_huawei notify = new TypeSDKNotify_huawei();
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
     * ���ⲿcall �� �Ƕ���֧������һ��ƶ�����Ʒ��
     * @param _in_context
     * @param _in_data
     * @return
     */
    public  String CallExchangeItem(String _in_data)
    {
    	return TypeSDKBonjour.Instance().ExchangeItem(_in_context, _in_data);
    }
    /***
     * ���ⲿ���õ� ��ʵ����������
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

///**
// * 华为防成迷验证处理
// * @author Administrator
// *
// */
//    public class GameAuthListener implements AuthListener
//    {
//        @Override
//        public void onCheckResult(int result)
//        {
//            TypeSDKLogger.i("onCheckResult:" + result);
//            switch (result)
//            {
//                // 玩家实名认证信息成功，已满18岁
//                // Players real name auth success, over 18 years of age
//                case AuthListener.AUTH_RESULT_SUCCESFULL:
//                    break;
//                // 玩家实名认证信息成功，未满18岁，建议cp做防沉迷处理，详见开发指导书
//                // Players real name auth success, not over 18 years of age, suggest cp do prevent indulge processing, see development guide book
//                case AuthListener.AUTH_RESULT_SUCCESFULL_UNDER_AGE:
//                    MainActivity.this.runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                        }
//                    });
//                    break;
//                // 玩家实名认证信息不通过，需要更新实名信息
//                // Players real name is not passed,have to update real name
//                case AuthListener.AUTH_RESULT_FAILED:
//                    MainActivity.this.runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            OpenSDK.updateAccount(MainActivity.this, GameAuthListener.this);
//                        }
//                    });
//
//                    break;
//                // 玩家实名认证检测未成功，不需处理
//                // Players real name auth failed, do not need to deal
//                case AuthListener.AUTH_RESULT_UNKNOWN:
//                    break;
//                default:
//                    break;
//            }
//        }
//        
//        @Override
//        public void onUpdateResult(int result)
//        {
//           TypeSDKLogger.i("onUpdateResult:" + result);
//            switch (result)
//            {
//                // 玩家实名认证信息提交成功
//                // Players real name information submitted successfully
//                case AuthListener.UPDATE_RESULT_SUBMIT_SUCCESFULL:
//                    break;
//                // 玩家取消提交实名认证信息
//                // Players cancel the submission
//                case AuthListener.UPDATE_RESULT_CANCEL:
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
}
