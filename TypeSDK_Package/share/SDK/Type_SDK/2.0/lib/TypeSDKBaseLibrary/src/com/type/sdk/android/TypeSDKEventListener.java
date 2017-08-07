/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

import java.util.EventListener;

public interface TypeSDKEventListener extends EventListener
{
	public Boolean NotifySDKEvent(TypeSDKEvent event);
}
