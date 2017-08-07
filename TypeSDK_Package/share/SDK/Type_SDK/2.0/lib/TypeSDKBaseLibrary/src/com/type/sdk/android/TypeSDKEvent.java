/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

import java.util.EventObject;

public class TypeSDKEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	public String type;
	public String data;
	public String extra;

	public TypeSDKEvent(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	/**
	 * android event type
	 */
	public static class EventType {
		public static String AND_EVENT_INIT_FINISH = "init finish";
		public static String AND_EVENT_UPDATE_FINISH = "update finish";
		public static String AND_EVENT_LOGIN = "login event";
		public static String AND_EVENT_RELOGIN = "relogin event";
		public static String AND_EVENT_LOGOUT = "logout event";
		public static String AND_EVENT_PAY_RESULT = "pay result event";
		public static String AND_EVENT_LOCAL_PUSH = "local push event";
		public static String AND_EVENT_USER_FRIENDS = "local user friends";
		public static String AND_EVENT_SHARE_RESULT = "share result event";

	}

}
