package com.type.sdk.application;

import android.content.Context;
import android.content.res.Configuration;

import com.kaopu.supersdk.api.KPSuperApp;
import com.kaopu.supersdk.api.KPSuperSDK;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.android.kp.TypeSDKBonjour;
import com.type.sdk.notification.PushService;

public class TypeApplication extends KPSuperApp {

	public TypeApplication() {
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TypeSDKLogger.e("TypeApplication");
		KPSuperSDK.onProxyCreate();
		
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
		TypeSDKLogger.i(buffStr);
		if (buffStr.length() > 0) {
			TypeSDKBonjour.Instance().platform.StringToData(buffStr);
			TypeSDKLogger.i(TypeSDKBonjour.Instance().platform
					.DataToString());
		}
		PushService.channelName = TypeSDKBonjour.Instance().platform
				.GetData("channelName");
	}

	@Override
	protected void attachBaseContext(Context base) {
		TypeSDKLogger.e("TypeApplication:attachBaseContext");
        super.attachBaseContext(base);
        KPSuperSDK.onProxyAttachBaseContext(base);
    }

	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		TypeSDKLogger.e("TypeApplication:onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        KPSuperSDK.onProxyConfigurationChanged(newConfig);
	}
}