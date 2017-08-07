package com.type.sdk.application;

import android.app.Application;
import android.content.Context;

import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.type.sdk.android.wdj.TypeSDKBonjour;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;

public class TypeApplication extends Application {

	private static WandouGamesApi wandouGamesApi;
	
	public static WandouGamesApi getWandouGamesApi() {
	    return wandouGamesApi;
	  }
	
	@Override
	  protected void attachBaseContext(Context base) {
	    WandouGamesApi.initPlugin(base, TypeSDKBonjour.Instance().platform.GetInt(AttName.APP_KEY), 
	    		TypeSDKBonjour.Instance().platform.GetData(AttName.SECRET_KEY));
	    super.attachBaseContext(base);
	  }
	
	public TypeApplication() {
		
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TypeSDKLogger.e("TypeApplication onCreate begin");
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
        TypeSDKLogger.i(buffStr);
    	if(buffStr.length()>0)
    	{
    		TypeSDKBonjour.Instance().platform.StringToData(buffStr);
    		//TypeSDKLogger.i(TypeSDKBonjour.Instance().platform.DataToString());
    	}
    	TypeSDKLogger.i("Push Service Start");
		PushService.channelName = TypeSDKBonjour.Instance().platform.GetData("channelName");
		TypeSDKLogger.i("wandouGamesApi.Builder");
		wandouGamesApi = new WandouGamesApi.Builder(this, TypeSDKBonjour.Instance().platform.GetInt(AttName.APP_KEY), 
				TypeSDKBonjour.Instance().platform.GetData(AttName.SECRET_KEY)).create();
	    wandouGamesApi.setLogEnabled(true);
	    TypeSDKLogger.e("TypeApplication onCreate finish");
	}
	
}
