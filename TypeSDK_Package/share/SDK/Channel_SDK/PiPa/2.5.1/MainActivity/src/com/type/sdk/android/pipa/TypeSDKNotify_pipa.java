package com.type.sdk.android.pipa;

import android.util.Log;

import com.type.sdk.android.pipa.*;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.unity3d.player.UnityPlayer;

public class TypeSDKNotify_pipa
{
	public void initFinish()
	{
		TypeSDKEventManager.Instance().SendUintyEvent(ReceiveFunction.MSG_INITFINISH, "");
		TypeSDKEventManager.Instance().SendUintyEvent(ReceiveFunction.MSG_UPDATEFINISH, "");
	}
	
	public void payOK()
	{
		TypeSDKData.BaseData payResult  = new TypeSDKData.BaseData();
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
		payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "NO_INIT");
		TypeSDKEventManager.Instance().SendUintyEvent(TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT, payResult.DataToString());
	}
	public void PayFail()
	{
		TypeSDKData.PayInfoData payResult = new TypeSDKData.PayInfoData();
		payResult.SetData(AttName.PAY_RESULT, "0");
		TypeSDKEventManager.Instance().SendUintyEvent(TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT, payResult.DataToString());
	}
		public void sendToken(String username, String token)
		{
			// TODO Auto-generated method stub
			android.util.Log.i("login info", "login success: token=" + token +"username="+username);
		
			TypeSDKData.UserInfoData userData= TypeSDKBonjour.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, token);
			userData.SetData(AttName.USER_ID, username);
			TypeSDKData.PlatformData platform = TypeSDKBonjour.Instance().platform;
			userData.CopyAtt(platform, TypeSDKDefine.AttName.CP_ID);
			userData.CopyAtt(platform, TypeSDKDefine.AttName.SDK_NAME);
			userData.CopyAtt(platform, TypeSDKDefine.AttName.PLATFORM);
			
			TypeSDKEventManager.Instance().
			SendUintyEvent(ReceiveFunction.MSG_LOGIN, userData.DataToString());
		}
		
		
		public void Logout()
		{
			Log.i("logout info","user sdk logout");
			TypeSDKData.UserInfoData userData = TypeSDKBonjour.Instance().userInfo;
			
			UnityPlayer.UnitySendMessage(TypeSDKDefine.UNITY_RECIVER,TypeSDKDefine.ReceiveFunction.MSG_LOGOUT, userData.DataToString());
		}


}
