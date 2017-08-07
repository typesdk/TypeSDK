package com.type.sdk.android.amigo;

//import java.util.EventListener;


import org.json.JSONException;
import org.json.JSONObject;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKEvent;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEvent.EventType;


public class TypeSDKNotify_amigo
{
		public void sendToken(String _utoken_string, String _uid_string)
		{
			// TODO Auto-generated method stub
			String userToken = _utoken_string;
			String encodeToken ="";
			try{
				JSONObject attJS = new JSONObject(userToken);
				encodeToken = attJS.getString("n") + "|" + attJS.getString("v") + "|" + attJS.getString("h") + "|" + attJS.getString("t");

			}catch(JSONException ex){
				TypeSDKLogger.e(ex.getMessage());
			}
			String uid = _uid_string;
			TypeSDKLogger.i("userToken extra:" + encodeToken);
			TypeSDKLogger.i("uid extra:" + uid);
		
			TypeSDKData.UserInfoData userData= TypeSDKBonjour.Instance().userInfo;
			userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, encodeToken);
			userData.SetData(TypeSDKDefine.AttName.USER_ID, uid);	
			userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.CP_ID);
			userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.SDK_NAME);
			userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.PLATFORM);
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_LOGIN, 
					ReceiveFunction.MSG_LOGIN, 
					userData.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
		}
		
		public void Init()
		{
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH, 
					ReceiveFunction.MSG_INITFINISH, 
					TypeSDKBonjour.Instance().platform.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));
			TypeSDKEventManager.Instance().SendEvent(TypeSDKEvent.EventType.AND_EVENT_UPDATE_FINISH, 
					ReceiveFunction.MSG_UPDATEFINISH, 
					TypeSDKBonjour.Instance().platform.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));		
		}
		
		public void Pay(String string)
		{
			TypeSDKLogger.i("pay");
			TypeSDKEventManager.Instance().SendEvent(EventType.AND_EVENT_PAY_RESULT,
					ReceiveFunction.MSG_PAYRESULT, 
					string, 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));			
		}
		
		public void Logout()
		{
			TypeSDKLogger.i("user sdk logout");			
			TypeSDKData.UserInfoData userData = TypeSDKBonjour.Instance().userInfo;
			TypeSDKEventManager.Instance().SendEvent(EventType.AND_EVENT_LOGOUT,
					ReceiveFunction.MSG_LOGOUT, 
					userData.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));	
		}

		public void reLogin()
		{
			TypeSDKLogger.i("user sdk reLogin");
			TypeSDKData.UserInfoData userData = TypeSDKBonjour.Instance().userInfo;
			TypeSDKLogger.i("logout info:" + userData.DataToString());
			TypeSDKEventManager.Instance().SendEvent(EventType.AND_EVENT_RELOGIN,
					ReceiveFunction.MSG_RELGOIN, 
					userData.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));		
			TypeSDKLogger.i("user sdk reLogin2");
		}
		
		public void localPush(TypeSDKData.BaseData baseData)
		{
			TypeSDKLogger.i("localPush send event start");
			TypeSDKLogger.i("localPush info:" + baseData.DataToString());
			TypeSDKEventManager.Instance().SendEvent(EventType.AND_EVENT_LOCAL_PUSH, ReceiveFunction.MSG_RECEIVE_LOCAL_PUSH, 
					baseData.DataToString(), 
					TypeSDKBonjour.Instance().platform.GetData(AttName.PLATFORM));		
			TypeSDKLogger.i("localPush send event end");
		}

}
