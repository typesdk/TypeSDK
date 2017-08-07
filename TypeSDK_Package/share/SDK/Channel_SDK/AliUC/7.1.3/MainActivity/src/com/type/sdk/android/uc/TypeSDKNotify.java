/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android.uc;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventManager;

public class TypeSDKNotify {
	public void sendToken(String _in_string)
	{
		// TODO Auto-generated method stub
		String userToken = _in_string;
		if(userToken == null || userToken.isEmpty()){
			return;
		}
		TypeSDKData.UserInfoData userData= TypeSDKBonjour.Instance().userInfo;
		userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, userToken);
		userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.CP_ID);
		userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.SDK_NAME);
		userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.PLATFORM);
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGIN, ReceiveFunction.MSG_LOGIN, 
				userData.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
	}
	
	public void Init()
	{
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH, ReceiveFunction.MSG_INITFINISH, 
				TypeSDKBonjour.Instance().platform.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
		
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_UPDATE_FINISH, ReceiveFunction.MSG_UPDATEFINISH, 
				TypeSDKBonjour.Instance().platform.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
	}
	
	public void Pay(String string)
	{
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_PAY_RESULT, ReceiveFunction.MSG_PAYRESULT, 
				string, TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
	}
	
	public void Logout()
	{
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGOUT, ReceiveFunction.MSG_LOGOUT, 
				TypeSDKBonjour.Instance().userInfo.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
	}

	public void reLogin()
	{
		TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_RELOGIN, ReceiveFunction.MSG_RELGOIN, 
				TypeSDKBonjour.Instance().userInfo.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
	}

}
