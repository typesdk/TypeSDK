package com.type.sdk.application;

import android.app.Application;

import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.pyw.TypeSDKBonjour;
import com.type.sdk.android.pyw.SDKEventListener;
import com.type.sdk.notification.PushService;
import com.pengyouwan.sdk.api.PYWPlatform;
import com.pengyouwan.sdk.api.SDKConfig;

public class TypeApplication extends Application{

	public TypeApplication() {
		
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
        TypeSDKLogger.i(buffStr);
    	if(buffStr.length()>0)
    	{
    		TypeSDKBonjour.Instance().platform.StringToData(buffStr);
    		TypeSDKLogger.i(TypeSDKBonjour.Instance().platform.DataToString());
    	}
    	
    	SDKConfig sdkconfig = new SDKConfig();
    	sdkconfig.setGameKey(TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY));
    	TypeSDKLogger.i(TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY));
    	PYWPlatform.initSDK(this,sdkconfig,new SDKEventListener(this));
    	TypeSDKLogger.i("init success");
    	
		PushService.channelName = TypeSDKBonjour.Instance().platform.GetData("channelName");
		
//		SDKConfig sdkconfig = new SDKConfig();
//		/** 必填**，请替换成朋友玩提供的GameKey,此处为测试值 **/
//		sdkconfig.setGameKey(TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY));
//		TypeSDKLogger
//				.e("TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY):"
//						+ TypeSDKBonjour.Instance().platform
//								.GetData(AttName.APP_KEY));
//
//		TypeSDKLogger.e("TypeApplication init start");
//		PYWPlatform.initSDK(this, sdkconfig,
//				new SDKEventListener(this));
		
	}
	
}
