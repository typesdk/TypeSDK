package com.type.sdk.android.tencent;


import java.util.Timer;
import java.util.TimerTask;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.unity3d.player.UnityPlayer;

public class TypeSDKNotify_Tencent {
	public void sendToken(String openid,String openkey) {
		// TODO Auto-generated method stub
		TypeSDKData.UserInfoData userData = TypeSDKBonjour.Instance().userInfo;
		String ysdkType = "";
		if(TypeSDKBonjour.logintype==1){
			ysdkType = "QQ";			
		}
		if(TypeSDKBonjour.logintype==2){
			ysdkType = "WX";
		}
		String userId = openid+"|"+openkey;
		String userToken = YSDKCallback.pf+"|"+YSDKCallback.pfKey+"|"+YSDKCallback.payToken+"|"+ysdkType;
		userData.SetData(AttName.USER_ID, userId);
		userData.SetData(AttName.USER_TOKEN, userToken);
		TypeSDKLogger.i("login msg:"+userData.DataToString());
		TypeSDKEventManager.Instance().SendUintyEvent(
				ReceiveFunction.MSG_LOGIN, userData.DataToString());
	}

	public void sendFailure(String failReason){
		TypeSDKData.BaseData failData = new TypeSDKData.BaseData();
		failData.SetData("fail_reason", failReason);
		TypeSDKEventManager.Instance().SendUintyEvent(
				ReceiveFunction.MSG_LOGIN, failData.DataToString());
		
	}
	public void Logout() {
		TypeSDKLogger.i( "user sdk logout");
		TypeSDKData.UserInfoData userData = TypeSDKBonjour.Instance().userInfo;
		UnityPlayer.UnitySendMessage(TypeSDKDefine.UNITY_RECIVER,
				TypeSDKDefine.ReceiveFunction.MSG_LOGOUT,
				userData.DataToString());
	}

}
