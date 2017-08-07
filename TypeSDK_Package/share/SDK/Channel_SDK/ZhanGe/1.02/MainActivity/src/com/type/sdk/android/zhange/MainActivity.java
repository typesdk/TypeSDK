package com.type.sdk.android.zhange;


import com.type.sdk.android.BaseMainActivity;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.TypeSDKUpdateManager;
import com.type.utils.*;
import com.game.sdk.YTSDKManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
public class MainActivity extends BaseMainActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{   	
		super.onCreate(savedInstanceState);
		TypeSDKLogger.i("android on create finish");
		CallInitSDK();
		TypeSDKUpdateManager update = new TypeSDKUpdateManager(this, 
				TypeSDKBonjour.Instance().platform.GetData(AttName.CHANNEL_ID), TypeSDKBonjour.Instance().platform.GetData("check_update_url"));
		update.checkUpdateInfo();

	}

	@Override
	protected void onDestroy()
	{
		TypeSDKLogger.e("sdk do destory");
		TypeSDKBonjour.Instance().onDestroy(_in_context);
		super.onDestroy();
	}

	@Override
	protected void onResume() 
	{
		TypeSDKBonjour.Instance().onResume(_in_context);
		super.onResume();
	}

	@Override
	protected void onStop() {
		
		TypeSDKBonjour.Instance().onStop(_in_context);
		super.onStop();
	}
	@Override
	protected void onPause() {
		TypeSDKBonjour.Instance().onPause(_in_context);
		super.onPause();
	}

	public  void CallInitSDK()
	{
		String _in_data = "";
		TypeSDKBonjour.Instance().initSDK(_in_context,_in_data);
	}
	public  void CallLogin(String _in_data)
	{
		TypeSDKLogger.i("call login:" + _in_data);
		TypeSDKBonjour.Instance().ShowLogin(_in_context,_in_data);
	}
	public  void CallLogout()
	{
		TypeSDKBonjour.Instance().ShowLogout(_in_context);
	}


	public String CallPayItem(final String _in_data) {
		TypeSDKLogger.i("CallPayItem:" + _in_data);
		TypeSDKBonjour.Instance().ShowPay(_in_context, _in_data);
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
		return TypeSDKBonjour.Instance().isHasRequest(_in_data);
	}
	public String CallAnyFunction(String FuncName,String _in_data)
	{

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
