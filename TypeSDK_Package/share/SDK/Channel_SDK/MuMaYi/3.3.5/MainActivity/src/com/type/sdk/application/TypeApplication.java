package com.type.sdk.application;

import java.lang.reflect.Method;

import android.app.Application;
import android.content.Context;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.mumayi.paymentmain.ui.MMYApplication;
import com.type.sdk.android.mmy.TypeSDKBonjour;

public class TypeApplication extends MMYApplication{

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
    	
    	executeMMYApplication(this);
	}
	
	private void executeMMYApplication(Context context){
		try{
			Class clazz = Class.forName("com.mumayi.paymentmain.ui.MMYApplication");
			Method method = clazz.getDeclaredMethod("init", Context.class);
			method.setAccessible(true);
			method.invoke(clazz.newInstance(), context);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
