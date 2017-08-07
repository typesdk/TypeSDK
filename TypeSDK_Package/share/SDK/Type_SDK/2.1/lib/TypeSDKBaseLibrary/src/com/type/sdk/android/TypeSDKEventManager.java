/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.unity3d.player.UnityPlayer;

public class TypeSDKEventManager {
	private Map<String, ArrayList<TypeSDKEventListener>> listenerMap;

	private TypeSDKEventManager() {
		listenerMap = new HashMap<String, ArrayList<TypeSDKEventListener>>();
	}

	private static class SingletonHandler {
		static final TypeSDKEventManager instance = new TypeSDKEventManager();
	}

	public static TypeSDKEventManager Instance() {
		return SingletonHandler.instance;
	}

	/***
	 * send event to unity
	 * 
	 * @param recevierFuncName
	 * @param sendData
	 */
	public void SendUintyEvent(String recevierFuncName, String sendData) {
		TypeSDKLogger.i("event manager send unity event " + recevierFuncName);
		UnityPlayer.UnitySendMessage(TypeSDKDefine.UNITY_RECIVER,
				recevierFuncName, sendData);
	}

	public void SendAndroidEvent(String _in_eventType, TypeSDKEvent _in_event) {
		TypeSDKLogger.i("event manager send android event " + _in_eventType);
		ArrayList<TypeSDKEventListener> list = listenerMap.get(_in_eventType);
		if (null == list) {
			// did not find target listener
			return;
		}
		Iterator<TypeSDKEventListener> it = list.iterator();

		while (it.hasNext()) {
			TypeSDKEventListener cacheData = it.next();
			if (null != cacheData) {
				cacheData.NotifySDKEvent(_in_event);
			}
		}
	}

	public void SendEvent(String _in_eventType, String recevierFuncName,
			String sendData, String platformName) {
		if (platformName.equals("unity")) {
			this.SendUintyEvent(recevierFuncName, sendData);
		} else if (platformName.equals("android")) {
			TypeSDKEvent Typeevent = new TypeSDKEvent(this);
			Typeevent.type = _in_eventType;
			Typeevent.data = sendData;
			this.SendAndroidEvent(_in_eventType, Typeevent);
		} else {
			TypeSDKLogger.e("平台配置不正确");
		}
	}

	public void AddEventListener(String _in_eventType,
			TypeSDKEventListener _in_listener) {
		ArrayList<TypeSDKEventListener> list = listenerMap.get(_in_eventType);
		if (null == list) {
			list = new ArrayList<TypeSDKEventListener>();
			listenerMap.put(_in_eventType, list);
		}
		list.add(_in_listener);
	}

	public void RemoveListener(String EventType, TypeSDKEventListener listener) {
		ArrayList<TypeSDKEventListener> list = listenerMap.get(EventType);
		if (null == list) {
			// did not find target listener
			return;
		}

		list.remove(listener);
	}

}
