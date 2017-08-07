package com.type.sdk.application;

import android.app.Application;

import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.sdk.notification.PushService;
import com.sogou.gamecenter.sdk.SogouGamePlatform;
import com.sogou.gamecenter.sdk.bean.SogouGameConfig;
import com.type.sdk.android.sogou.TypeSDKBonjour;

public class TypeApplication extends Application{

	// 防止内存临界时，垃圾回收了SogouGamePlatform对象		
	private SogouGamePlatform mSogouGamePlatform = SogouGamePlatform.getInstance();
		
	public TypeApplication() {
		
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		// 防止内存泄露，清理相关数据务必调用SDK结束接口
		mSogouGamePlatform.onTerminate();
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
		
		// 配置游戏信息（gid、appKey由搜狗游戏平台统一分配）
		SogouGameConfig config = new SogouGameConfig();		
		// 开发模式为true，false是正式环境
	    // 请注意，提交版本设置正式环境 
		config.devMode = false;
		config.gid = TypeSDKBonjour.Instance().platform.GetInt(AttName.APP_ID);
		config.appKey = TypeSDKBonjour.Instance().platform.GetData(AttName.APP_KEY);
		config.gameName = TypeSDKBonjour.Instance().platform.GetData(AttName.APP_NAME);
		
		// SDK准备初始化
		mSogouGamePlatform.prepare(this, config);
	}
	
}
