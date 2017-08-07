/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.xyy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

public class MainActivity extends BaseMainActivity {

//	private Context _in_context = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TypeSDKLogger.i("android on create finish");
        _in_context = this;
        super.onCreate(savedInstanceState);
        
        TypeSDKBonjour.Instance().initSDK(_in_context, "");
		
		
//		TypeSDKBonjour.Instance();
//		TypeSDKBonjour.Instance().initSDK(this, this, "");

		//TypeSDKBonjour.Instance().addNotification(_in_context, "1", "0", "10:00", "123", "ABC", "abc", "0", "1", "2");
	}

	@Override
	protected void onDestroy() {
		TypeSDKLogger.e("sdk do destory");
		super.onDestroy();
		TypeSDKBonjour.Instance().onDestroy();

	}

	public void CallInitSDK()
    {
        TypeSDKLogger.e("sdk do TypeSDKBonjour CallInitSDK");
		TypeSDKBonjour.Instance().initSDK(_in_context, "");
		
//		TypeSDKBonjour.Instance().setIsAllowSendInitNotify(true);
//		TypeSDKBonjour.Instance().initTypeSDK(this, this, "");
	}

	public void CallLogin(String _in_data) {
		TypeSDKLogger.i("call login:" + _in_data);
		TypeSDKBonjour.Instance().ShowLogin(_in_context, _in_data);
	}

	public void CallLogout() {
		TypeSDKBonjour.Instance().ShowLogout(_in_context);
	}

	public String CallPayItem(String _in_data) {
		TypeSDKLogger.i("Call pay:" + _in_data);
		TypeSDKBonjour.Instance().PayItem(this, _in_data);
		return "client pay function finished";
	}

	public String CallExchangeItem(String _in_data) {
		return TypeSDKBonjour.Instance().ExchangeItem(_in_context, _in_data);
	}

	public void CallToolBar() {
		TypeSDKBonjour.Instance().ShowToolBar(_in_context);
	}

	public void CallHideToolBar() {
		TypeSDKBonjour.Instance().HideToolBar(_in_context);
	}

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
		TypeSDKLogger.e("=============== CallSetPlayerInfo ================");
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
