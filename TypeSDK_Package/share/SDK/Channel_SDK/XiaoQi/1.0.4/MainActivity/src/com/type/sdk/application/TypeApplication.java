package com.type.sdk.application;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.smwl.smsdk.app.BaseApplicationSDK;
import com.type.sdk.android.x7sy.TypeSDKBonjour;

public class TypeApplication extends BaseApplicationSDK{

	public TypeApplication() {
		
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		TypeSDKLogger.e("TypeApplication");
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
        TypeSDKLogger.i(buffStr);
    	if(buffStr.length()>0)
    	{
    		TypeSDKBonjour.Instance().platform.StringToData(buffStr);
    		TypeSDKLogger.i(TypeSDKBonjour.Instance().platform.DataToString());
    	}
		
		PushService.channelName = TypeSDKBonjour.Instance().platform.GetData("channelName");
	}
	
}
