/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright漏 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

import com.type.sdk.android.TypeSDKData.BaseData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.utils.HttpUtil;
import com.type.utils.SystemUtils;

public class BaseMainActivity extends @gameMainActivity@ {
	public static Context _in_context = null;
	String openSettings = "1";
	private static final int FULL_SREEN_MSG = 10;
	private static final long TIME_BEFORE_HIDE_UI = 2 * DateUtils.SECOND_IN_MILLIS;
	public static String sdkName = "Default";
    public boolean openPay = true;
	public static String userId = "";

	
	public Handler handler = new Handler();
	TypeSDKData.BaseData platformBaseData;
	
	public BaseMainActivity()
	{
	
		super();
		TypeSDKLogger.i("do super function BaseMainActivity");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_in_context = this;
		String buffStr = TypeSDKTool.getFromAssets(this, "CPSettings.txt");
        //TypeSDKLogger.i(buffStr);
        TypeSDKData.BaseData platformBaseData = new BaseData();
    	if(buffStr.length()>0){
    		platformBaseData.StringToData(buffStr);
    		//TypeSDKLogger.i(platformBaseData.DataToString());
    	}
    	sdkName =  platformBaseData.GetData(AttName.SDK_NAME);    	
    	
    	TypeSDKLogger.i("sdkName:"+sdkName);
		final String switchConfigUrl = platformBaseData
				.GetData(AttName.SWITCHCONFIG_URL);
		final String cp_id = platformBaseData.GetData(AttName.CP_ID);
		final String channel_id = platformBaseData.GetData(AttName.CHANNEL_ID);
		//final String channel_name = platformBaseData.GetData("channelName");
		//PushService.channelName = channel_name;
    	TypeSDKLogger.d("switchConfigUrl:"+switchConfigUrl);
		new Thread(){
			@Override
			public void run(){
				try {
					TypeSDKLogger.d("StartGetConfig");
					String controllerMessage = HttpUtil.mHttpGet(switchConfigUrl,5);
					//TypeSDKLogger.d("config=" + controllerMessage);
					//openPay = TypeSDKTool.openPay(sdkName, controllerMessage);
					
					TypeSDKTool.ctrlMessage(controllerMessage,_in_context);
					getItemList(controllerMessage, cp_id, channel_id);			
					TypeSDKLogger.i("Finsh run");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					TypeSDKLogger.e("logcollector controller exception:"+e.toString());
					e.printStackTrace();
				}
			}			
		}.start();
//		registerReceiver(); //register network receiver
//		TypeSDKLogger.i("Set Fullscreen");
		setFullscreen();
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
				new OnSystemUiVisibilityChangeListener() {

					@Override
					public void onSystemUiVisibilityChange(int visibility) {
						// TODO Auto-generated method stub
						//TypeSDKLogger.i("visibility:" + visibility
						//		+ "Build.VERSION.SDK_INT:"
						//		+ Build.VERSION.SDK_INT);
						if ((((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) && (Build.VERSION.SDK_INT >= 18))
								|| ((Build.VERSION.SDK_INT < 18) && (visibility == 4))) {
							try {
								TypeSDKLogger.i("Enter correct ui mode");
								mHandler.sendEmptyMessageDelayed(
										FULL_SREEN_MSG, TIME_BEFORE_HIDE_UI);
							} catch (Exception e) {
								TypeSDKLogger.e(e.toString());
							}
						}

					}

				});
		
		TypeSDKLogger.i("Set openSettions");
		SharedPreferences preferences = getSharedPreferences("user",
				Context.MODE_PRIVATE);
		openSettings = preferences.getString("opensettings", "1");
		if (openSettings == "1") {
			Editor editor = preferences.edit();
			editor.putString("opensettings", "0");
			editor.commit();
			//showSettingTips(_in_context);
		}

		TypeSDKLogger.i("AND_EVENT_INIT_FINISH");
		TypeSDKEventManager.Instance().AddEventListener(
				TypeSDKEvent.EventType.AND_EVENT_INIT_FINISH,
				new TypeSDKEventListener() {

					@Override
					public Boolean NotifySDKEvent(TypeSDKEvent event) {
						// TODO Auto-generated method stub
						TypeSDKLogger.i("初始化完成 " + event.type + " "
								+ event.data);
						return true;
					}
				});		
		//_in_context.stopService(intent);
	}
	
	protected void getItemList(String str_itemListConfigJson, String cp_id, String channel_id) {
		// itemList
		TypeSDKData.BaseData configData = new TypeSDKData.BaseData();
		if (str_itemListConfigJson.length() > 0) 
		{
			configData.StringToData(str_itemListConfigJson);
			String itemListUrl = configData.GetData("itemListUrl");
			TypeSDKLogger.i("itemistUrl=" + itemListUrl);
			if (itemListUrl != null && !itemListUrl.isEmpty()) {
				//TypeSDKLogger.i("getItemList itemListUrl:" + itemListUrl);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("gameId", cp_id);
				map.put("channelId", channel_id);
				HttpUtil.isPostok = true;
				while (HttpUtil.isPostok) 
				{
					//TypeSDKLogger.i("itemList itemListUrl:" + itemListUrl);
					String result = HttpUtil.mHttpPost(itemListUrl, map);
					TypeSDKLogger.i("itemList result:" + result);
					if (result == null || result.isEmpty()) {
						return;
					}
					TypeSDKData.ItemListData tempdata = new TypeSDKData.ItemListData();
					tempdata.StringToData(result);					
					if (tempdata.GetData("code").equals("0")) {
						tempdata.StringToData(tempdata
								.GetData("itemList"));
						HttpUtil.isPostok = false;
					} else if (tempdata.GetData("code").equals("1")) {
						TypeSDKLogger.e("getitemList error:"
								+ tempdata.GetData("msg"));
						HttpUtil.isPostok = false;
					} else {
						TypeSDKLogger.e("code is -99 msg:"
								+ tempdata.GetData("msg"));
						HttpUtil.isPostok = false;
						
					}
					TypeSDKBaseBonjour.itemListData = tempdata;
					TypeSDKLogger.i("itemListBaseData:"
						+ tempdata.DataToString());
				}
				
			}
		}
	}
	
	@Override
    protected void onDestroy(){
    	super.onDestroy();
    	//unregisterReceiver();//unregister network receiver
    }

    
    @Override
    protected void onResume() 
    {
    	super.onResume();
    }
    
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
    public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus){
			try {
				TypeSDKLogger.i("Enter correct ui mode");
				mHandler.sendEmptyMessageDelayed(
						FULL_SREEN_MSG, TIME_BEFORE_HIDE_UI);
			} catch (Exception e) {
				TypeSDKLogger.e(e.toString());
			}
		}
	}
	TypeSDKEventListener initListener = new TypeSDKEventListener() {
		@Override
		public Boolean NotifySDKEvent(TypeSDKEvent event) {
			RealOncreate();
			return null;
		}
	};

	private void RealOncreate() {

	}
	public String CallPhoneInfo(){
		TypeSDKLogger.i("CallPhoneInfo");
		return GetPhoneInfo(_in_context);
	}
	public String GetPhoneInfo(Context _in_context) {
		TypeSDKLogger.i("GetPhoneInfo");
		TypeSDKData.BaseData phoneInfo = new TypeSDKData.BaseData();
		phoneInfo.SetData(AttName.APP_VERSION_NAME,
				SystemUtils.getAppVersionName(_in_context));
		phoneInfo.SetData(AttName.CURRENT_TIMEZONE, SystemUtils.getTimeZone());
		phoneInfo.SetData(AttName.CURRENT_TIME, SystemUtils.getCurTime());
		phoneInfo.SetData(AttName.CURRENT_LANGUAGE,
				SystemUtils.getCurLanguage(_in_context));
		phoneInfo.SetData(AttName.SIM_OPERATOR_NAME,
				SystemUtils.getSimOperatorName((Activity) _in_context));
		phoneInfo.SetData(AttName.NETWORK_TYPE,
				SystemUtils.getNetworkType((Activity) _in_context));
		phoneInfo.SetData(AttName.PHONE_IP, SystemUtils.getPhoneIp());
		phoneInfo.SetData(AttName.PHONE_MODEL, SystemUtils.getModel());
		phoneInfo.SetData(AttName.PHONE_PRODUCTOR, SystemUtils.getProduct());
		phoneInfo.SetData(AttName.ANDROID_ID,
				SystemUtils.getAndroidID((Activity) _in_context));
		phoneInfo.SetData(AttName.CPU_TYPE, SystemUtils.getCPUType());
		phoneInfo.SetData(AttName.SYSTEM_VERSION,
				SystemUtils.getSystemVersion());
		phoneInfo.SetData(AttName.SCREEN_HEIGHT,
				String.valueOf(SystemUtils.getScreenHeightSize(_in_context)));
		phoneInfo.SetData(AttName.SCREEN_WIDTH,
				String.valueOf(SystemUtils.getScreenWidthSize(_in_context)));
		phoneInfo.SetData(AttName.ROOT_AHTH,
				String.valueOf(SystemUtils.getRootAhth()));
		phoneInfo.SetData(AttName.MEMORY_TOTAL_MB,
				String.valueOf(SystemUtils.getMemoryTotalMB(_in_context)));
		phoneInfo.SetData(AttName.MAC_ADDRESS,
				SystemUtils.getWifiMac(_in_context));
		phoneInfo.SetData(AttName.IMEI, SystemUtils.getIMEI(_in_context));
		phoneInfo.SetData(AttName.IMSI, SystemUtils.getIMSI(_in_context));
		phoneInfo.SetData(AttName.SIM_SERIAL_NUMBER,
				SystemUtils.getPhoneSN(_in_context));
		return phoneInfo.DataToString();
	}

	private void setFullscreen() {
		if ((Build.VERSION.SDK_INT>=14)&&(Build.VERSION.SDK_INT < 18)) {
			//TypeSDKLogger.i("Build.VERSION.SDK_INT<18");
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LOW_PROFILE
							| View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
		if (Build.VERSION.SDK_INT >= 18) {
			//TypeSDKLogger.i("Build.VERSION.SDK_INT>=18");
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav
																	// bar
							| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
							| View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == FULL_SREEN_MSG) {
				setFullscreen();
			}
		}
	};

	public void showSettingTips(Context _context) {
		AlertDialog.Builder builder = new Builder(_context);
		builder.setTitle("开启自动更新");
		builder.setMessage("建议你开启游戏自动更新暗转功能");
		builder.setPositiveButton("前往设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent settingIntent = new Intent(
						Settings.ACTION_ACCESSIBILITY_SETTINGS);
				startActivity(settingIntent);
			}
		});
		builder.setNegativeButton("暂不设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setCancelable(false);
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}

	static {
		try {
			TypeSDKLogger.i("loadLibrary start");
			System.loadLibrary("ApkPatchLibrary");
			TypeSDKLogger.i("loadLibrary end");
		} catch (Throwable ex) {
			ex.printStackTrace();
			TypeSDKLogger.e(ex.toString());
		}

	}
}
