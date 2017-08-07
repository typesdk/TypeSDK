package com.type.sdk.application;

import android.app.Application;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.type.sdk.android.wogame.TypeSDKBonjour;
import com.unicom.dcLoader.Utils;
import com.unicom.dcLoader.Utils.UnipayPayResultListener;

public class TypeApplication extends Application{

	public TypeApplication() {
		
	}
	
	@Override
	public void onCreate() { 
		super.onCreate();
		TypeSDKLogger.e("TypeApplication");
		
		// 支付安全模块初始化
		Utils.getInstances().initSDK(this,
				new UnipayPayResultListener() {

					@Override
					public void PayResult(String arg0, int arg1,
							int arg2, String arg3) {
						// TODO Auto-generated method stub

					}
				});
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
