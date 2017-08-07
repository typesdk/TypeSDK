/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.utils;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	private SharedPreferences sp;
	private Editor editor;
	private final static String SP_NAME = "mydata";
	private final static int MODE = Context.MODE_MULTI_PROCESS; //Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE + 

	public SharedPreferencesUtil(Context context) {
		sp = context.getSharedPreferences(SP_NAME, MODE);
		editor = sp.edit();
	}
	
	public SharedPreferencesUtil(Context context, String sp_name) {
		sp = context.getSharedPreferences(sp_name, MODE);
		editor = sp.edit();
	}

	public boolean save(String key, String value) {
		synchronized (SP_NAME) {
			editor.putString(key, value);
		}
		return editor.commit();
	}
	
	public boolean save(String key, String value, String sp_name) {
		synchronized (sp_name) {
			editor.putString(key, value);
		}
		return editor.commit();
	}
	
	public boolean remove(String key) {
		editor.remove(key);
		return editor.commit();
	}

	public String read(String key) {
		String str = "";
		synchronized (SP_NAME) {
			str = sp.getString(key, null);
		}
		return str;
	}
	
	public String read(String key, String sp_name) {
		String str = "";
		synchronized (sp_name) {
			str = sp.getString(key, null);
		}
		return str;
	}
	
	public boolean cleanData() {
		editor.clear();
		return editor.commit();
	}
	
	public Map<String, Object> getAll(String sp_name) {
		Map<String, Object> map = null;
		synchronized (sp_name) {
			map = (Map<String, Object>) sp.getAll();
		}
		return map;
	}
}
