package com.type.sdk.android.x7sy;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;

public class TypeSDKNotify_x7sy
{
		public void sendToken(String _in_string)
		{
			// TODO Auto-generated method stub
			String userToken = _in_string;
			TypeSDKLogger.i("login info:" + "login success intent extra:" + userToken);
		
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
			TypeSDKLogger.i("pay:" + string);
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_PAY_RESULT, ReceiveFunction.MSG_PAYRESULT, 
					string, TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			
		}
		
		public void Logout()
		{
			TypeSDKLogger.i("user sdk logout");
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGOUT, ReceiveFunction.MSG_LOGOUT, 
					TypeSDKBonjour.Instance().userInfo.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			
		}

		public void reLogin(TypeSDKData.UserInfoData data)
		{
			TypeSDKLogger.i("user sdk reLogin");
			
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_RELOGIN, ReceiveFunction.MSG_RELGOIN, 
					data.DataToString(), TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			
			TypeSDKLogger.i("user sdk reLogin2");
		}

}
