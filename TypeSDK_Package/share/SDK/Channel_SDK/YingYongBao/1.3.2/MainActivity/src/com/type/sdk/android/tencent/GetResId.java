package com.type.sdk.android.tencent;

import android.content.Context;

public class GetResId {

	public static int getId(Context paramContext, String paramString1,
			String paramString2) {
		return paramContext.getResources().getIdentifier(paramString2,
				paramString1, paramContext.getPackageName());
	}

}
