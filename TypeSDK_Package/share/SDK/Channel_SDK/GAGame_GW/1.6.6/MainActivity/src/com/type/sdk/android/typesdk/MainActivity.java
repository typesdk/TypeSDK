/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.typesdk;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;

import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import android.content.Intent;

public class MainActivity extends BaseMainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TypeSDKLogger.i("android on create begin");
		
		super.onCreate(savedInstanceState);
		TypeSDKBonjour.Instance().onCreate(_in_context);
		TypeSDKLogger.i("android on create finish");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		TypeSDKBonjour.Instance().onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		TypeSDKLogger.e("sdk do destory");
		super.onDestroy();
		TypeSDKBonjour.Instance().onDestroy();
	}
	
	public void CallInitSDK() {
		TypeSDKLogger.w("CallInitSDK: begin");
		
		String _in_data = "";
		TypeSDKBonjour.Instance().initSDK(_in_context, _in_data);
		TypeSDKLogger.d("CallInitSDK: end");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TypeSDKBonjour.Instance().onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TypeSDKBonjour.Instance().onPause();
	}

	public void CallLogin(String _in_data) {
		TypeSDKLogger.i("call login:" + _in_data);
		TypeSDKBonjour.Instance().ShowLogin(this, _in_data);
	}

	public void CallLogout() {
		TypeSDKBonjour.Instance().ShowLogout(_in_context);
	}

	public String CallPayItem(String _in_data) {
		TypeSDKLogger.i("Call pay:" + _in_data);
		TypeSDKBonjour.Instance().ShowPay(this, _in_data);
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
		TypeSDKLogger.d("CallShare");
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
	
	public void CallInvitedFriend(String json_str){
		TypeSDKLogger.d("CallInvitedFriend");
		TypeSDKBonjour.Instance().InvitedFriend(json_str);
	}

}
