/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.ch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEvent.EventType;
import com.type.sdk.android.TypeSDKEventListener;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
//import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.utils.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;



import com.type.sdk.android.TypeSDKData.BaseData;

import android.app.NotificationManager;
import android.os.Looper;
import com.type.utils.*;
//import com.type.sdk.notification.PushService;
import com.type.sdk.android.BaseMainActivity;

public class MainActivity extends BaseMainActivity{

	@Override	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TypeSDKLogger.e("android init");
//		TypeSDKUpdateManager update = new TypeSDKUpdateManager(this,
//				TypeSDKBonjour.Instance().platform.GetData(AttName.SDK_NAME), TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
//		update.checkUpdateInfo();		

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TypeSDKLogger.e("sdk do destory");
		TypeSDKBonjour.Instance().onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		TypeSDKBonjour.Instance().onResume(_in_context);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour.Instance().onPause();
	}

	/**
	 * ���ⲿ call �� init����
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void CallInitSDK() {
		String _in_data = "";

		TypeSDKBonjour.Instance().initSDK(_in_context, _in_data);
	}

	/**
	 * ���ⲿ call�� login����
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void CallLogin(String _in_data) {
		TypeSDKLogger.i(_in_data);
		TypeSDKBonjour.Instance().ShowLogin(_in_context, _in_data);
	}

	/**
	 * ���ⲿ call ��logout����
	 * 
	 * @param _in_context
	 */
	public void CallLogout() {
		TypeSDKBonjour.Instance().ShowLogout(_in_context);
	}

	public String CallPayItem(final String _in_data) {
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
						TypeSDKBonjour.Instance().PayItem(_in_context, _in_data);
					} else {
						TypeSDKNotify_ch notify = new TypeSDKNotify_ch();
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
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public String CallExchangeItem(String _in_data) {
		return TypeSDKBonjour.Instance().ExchangeItem(_in_context, _in_data);
	}

	/***
	 * ���ⲿ���õ� ��ʵ����������
	 * 
	 * @param _in_context
	 */
	public void CallToolBar() {
		TypeSDKBonjour.Instance().ShowToolBar(_in_context);
	}

	public void CallHideToolBar() {
		TypeSDKBonjour.Instance().HideToolBar(_in_context);
	}

	/***
	 * ���ⲿ���õ���ʵ�û����ĺ���
	 * 
	 * @param _in_context
	 */
	public void CallPersonCenter() {
		TypeSDKBonjour.Instance().ShowPersonCenter(_in_context);
	}

	public void CallHidePersonCenter() {
		TypeSDKBonjour.Instance().HidePersonCenter(_in_context);
	}

	public void CallShare(String _in_data) {
		TypeSDKBonjour.Instance().ShowShare(_in_context, _in_data);
	}

	public void CallSetPlayerInfo(String _in_data) {
		TypeSDKBonjour.Instance().SetPlayerInfo(_in_context, _in_data);
	}

	public void CallExitGame() {
		TypeSDKBonjour.Instance().ExitGame(_in_context);
	}

	public void CallDestory() {
		TypeSDKBonjour.Instance().onDestroy();
	}

	public int CallLoginState() {
		return TypeSDKBonjour.Instance().LoginState(_in_context);
	}

	public String CallUserData() {
		return TypeSDKBonjour.Instance().GetUserData();
	}

	public String CallPlatformData() {
		return TypeSDKBonjour.Instance().GetPlatformData();
	}

	public boolean CallIsHasRequest(String _in_data) {
		return TypeSDKBonjour.Instance().isHasRequest(_in_data);
	}
	public String CallAnyFunction(String FuncName, String _in_data) {
		Method[] me = TypeSDKBonjour.Instance().getClass().getMethods();
		for (int i = 0; i < me.length; ++i) {
			if (me[i].getName().equals(FuncName)) {
				try {
					return (String) me[i].invoke(TypeSDKBonjour.Instance(),
							_in_context, _in_data);
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
