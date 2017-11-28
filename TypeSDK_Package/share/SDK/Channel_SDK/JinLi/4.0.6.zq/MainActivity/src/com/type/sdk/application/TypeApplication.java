package com.type.sdk.application;

import android.app.Application;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.type.sdk.android.amigo.TypeSDKBonjour;
import com.gionee.gamesdk.floatwindow.GamePlatform;
import com.type.sdk.android.TypeSDKDefine.AttName;

public class TypeApplication extends Application{

	public TypeApplication() {
		
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		TypeSDKLogger.i("start Application");
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
        TypeSDKLogger.i(buffStr);
    	if(buffStr.length()>0)
    	{
    		TypeSDKBonjour.Instance().platform.StringToData(buffStr);
    		TypeSDKLogger.i(TypeSDKBonjour.Instance().platform.DataToString());
    	}
		TypeSDKLogger.d("APP_KEY:" + TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY));
		GamePlatform.init(this, TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY));
		PushService.channelName = TypeSDKBonjour.Instance().platform.GetData("channelName");
	}
	
}
