package com.type.sdk.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;

import com.type.sdk.android.@channelName@.*;
import com.type.sdk.android.TypeSDKLogger;

public class TypeSDKHelper {

	public static void onCreate(Activity activity) {
		//TypeSDKBonjour.Instance().TypeSDKInit(activity);
		TypeSDKLogger.i("android on create finish");
	}

	public static void onDestroy(Activity activity) {
		//TypeSDKBonjour.Instance().onDestroy();
	}

	public static void onResume(Activity activity) {
		//TypeSDKBonjour.Instance().onResume(activity);
	}

	public static void onPause(Activity activity) {
		//TypeSDKBonjour.Instance().onPause(activity);
	}
	
	public static void onStart(Activity activity) {
	}
	
	public static void onRestart(Activity activity) {
	}	
	
	public static void onStop(Activity activity) {
		//TypeSDKBonjour.Instance().onStop();		
	}
	
	public static void onNewIntent(Activity _in_activity, Intent intent) {
	}
	
	public static void onActivityResult(Activity _in_activity, int requestCode, int resultCode, Intent data) {
	}

	public static void CallInitSDK(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().initSDK(activity, _in_data);
	}

	public static void CallLogin(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		
		TypeSDKBonjour.Instance().ShowLogin(activity, _in_data);
	}

	public static void CallLogout(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().ShowLogout(activity);
	}

	public static String CallPayItem(Activity activity, String _in_data) {
		TypeSDKLogger.i("参数:" + _in_data);
		return TypeSDKBonjour.Instance().ShowPay(activity,
				_in_data);
	}

	public static void CallShare(Activity activity, String _in_data) {
		TypeSDKLogger.i("参数:" + _in_data);
		TypeSDKBonjour.Instance().ShowShare(activity, _in_data);
	}

	public static void CallSetPlayerInfo(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance()
		.SetPlayerInfo(activity, _in_data);
	}

	public static void CallExitGame(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().ExitGame(activity);
	}

	public static String CallUserData(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		return TypeSDKBonjour.Instance().GetUserData();
	}

	public static String CallPlatformData(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		return TypeSDKBonjour.Instance().GetPlatformData();
	}

	public static boolean CallIsHasRequest(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		return TypeSDKBonjour.Instance().isHasRequest(_in_data);
	}

	public static String CallAnyFunction(Activity activity, String FuncName,
			String _in_data) {
		Method[] me = TypeSDKBonjour.Instance().getClass()
				.getMethods();
		for (int i = 0; i < me.length; ++i) {
			if (me[i].getName().equals(FuncName)) {
				try {
					return (String) me[i].invoke(
							TypeSDKBonjour.Instance(), activity,
							_in_data);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "error";
	}

	public static void AddLocalPush(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().AddLocalPush(activity, _in_data);
	}

	public static void RemoveLocalPush(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().RemoveLocalPush(activity, _in_data);
	}

	public static void RemoveAllLocalPush(Activity activity, String _in_data) {
		TypeSDKLogger.d("参数:" + _in_data);
		TypeSDKBonjour.Instance().RemoveAllLocalPush(activity);
	}
}
