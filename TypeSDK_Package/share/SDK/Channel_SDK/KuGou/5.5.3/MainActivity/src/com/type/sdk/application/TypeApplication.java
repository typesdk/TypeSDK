package com.type.sdk.application;

import android.app.Application;
import android.content.Context;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.type.sdk.android.kugou.TypeSDKBonjour;
public class TypeApplication extends Application{

	static public Context context;
	
	public TypeApplication() {
		context = this;
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
