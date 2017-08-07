/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.uc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.AttName;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.type.sdk.android.BaseMainActivity;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.HttpUtil;

import android.os.Handler;
import android.os.Looper;

public class MainActivity extends BaseMainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			TypeSDKLogger.i("oncreate restore:"
					+ savedInstanceState.getString("reason"));
		}
		TypeSDKLogger.i("android on create finish");
		TypeSDKBonjour.Instance().onCreate((Activity) _in_context);
		CallInitSDK();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		TypeSDKBonjour.Instance().onNewIntent();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		TypeSDKBonjour.Instance().onRestart();
	}
	
	@Override
	protected void onStart(){
		// TODO Auto-generated method stub
		super.onStart();
		TypeSDKBonjour.Instance().onStart();
	}

	@Override
	protected void onDestroy() {
		TypeSDKLogger.e("sdk do destory");
		TypeSDKBonjour.Instance().onDestroy();
		super.onDestroy();
		
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
		TypeSDKBonjour.Instance().onPause(_in_context);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TypeSDKLogger.i("kill:" + "it has been killed!");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		TypeSDKLogger.i("onSaveInstanceState");
		outState.putString("reason", "it has been removed by system");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			TypeSDKLogger.i("restore:"
					+ savedInstanceState.getString("reason"));
		}
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
		TypeSDKLogger.i("call login:" + _in_data);
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

	/***
	 * 
	 * payData.SetData(U3DSharkAttName.REAL_PRICE,inputStr);
	 * payData.SetData(U3DSharkAttName.ITEM_NAME,"sk bi");
	 * payData.SetData(U3DSharkAttName.ITEM_DESC,"desc");
	 * payData.SetData(U3DSharkAttName.ITEM_COUNT,"1");
	 * payData.SetData(U3DSharkAttName.ITEM_SEVER_ID,"id");
	 * payData.SetData(U3DSharkAttName.SEVER_ID,"1");
	 * payData.SetData(U3DSharkAttName.EXTRA,"extra
	 * 
	 * 
	 * ���ⲿcall�Ķ���֧������(rmb�һ� ��Ϸ��)
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public String CallPayItem(final String _in_data) {
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
						TypeSDKNotify_UC notify = new TypeSDKNotify_UC();
						TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
						payResult.SetData(AttName.PAY_RESULT, "0");
						notify.Pay(payResult.DataToString());
						Handler dialogHandler = new Handler(
								Looper.getMainLooper());
						dialogHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								TypeSDKTool.showDialog("暂未开放充值！！！",
										_in_context);
							}
						});
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
		return TypeSDKBonjour.Instance().ExchangeItem(_in_context,
				_in_data);
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
					return (String) me[i].invoke(
							TypeSDKBonjour.Instance(), _in_context,
							_in_data);
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

	public void AddLocalPush(String _in_data) {
		TypeSDKLogger.i(_in_data);
		TypeSDKBonjour.Instance().AddLocalPush(_in_context, _in_data);
	}

	public void RemoveLocalPush(String _in_data) {
		TypeSDKLogger.i(_in_data);
		TypeSDKBonjour.Instance().RemoveLocalPush(_in_context, _in_data);
	}

	public void RemoveAllLocalPush() {
		TypeSDKBonjour.Instance().RemoveAllLocalPush(_in_context);
	}

}
