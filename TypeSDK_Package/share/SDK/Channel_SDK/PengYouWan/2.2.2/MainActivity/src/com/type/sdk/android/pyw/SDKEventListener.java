package com.type.sdk.android.pyw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKLogger;
import com.pengyouwan.sdk.api.ISDKEventCode;
import com.pengyouwan.sdk.api.ISDKEventExtraKey;
import com.pengyouwan.sdk.api.OnSDKEventListener;
import com.pengyouwan.sdk.api.User;

public class SDKEventListener implements OnSDKEventListener {

	private Context mContext;

	public SDKEventListener(Context context) {
		mContext = context;
	}

	@Override
	public void onEvent(int eventCode, Bundle data) {
		TypeSDKNotify_PYW notify_PYW = new TypeSDKNotify_PYW();
		TypeSDKData.PayResultData payResult = new TypeSDKData.PayResultData();
		TypeSDKLogger.i("eventCode:" + eventCode);
		switch (eventCode) {
		case ISDKEventCode.CODE_LOGIN_SUCCESS:
			// TypeSDKNotify_PYW notify_PYW = new TypeSDKNotify_PYW();
			// 登录成功通知，bundle中会带有user信息
			TypeSDKLogger.e("success");
			if (data != null) {
				User user = (User) data
						.getSerializable(ISDKEventExtraKey.EXTRA_USER);
				if (user != null) {
					//String userName = user.getUserName();// 用户账号
					String userId = user.getUserId(); // 朋友玩为用户分配的唯一标识
					String tokenPYW = user.getToken();
					TypeSDKLogger.e("tokenPYW:" + user.getToken());
					notify_PYW.sendToken(tokenPYW, userId);
				}
			}
			// 发送登录SDK成功广播通知界面
			mContext.sendBroadcast(new Intent(
					TypeSDKBonjour.ACTION_LOGIN_SDK_SUCCESS));
			break;
		case ISDKEventCode.CODE_LOGIN_FAILD:
			String erroMsg = data.getString(ISDKEventExtraKey.EXTRA_ERRO);
			TypeSDKLogger.e("LOGIN FAILD:" + erroMsg);
			break;
		case ISDKEventCode.CODE_CHARGE_SUCCESS:
			// 充值成功
			payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "1");
			payResult
					.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "支付成功");
			notify_PYW.Pay(payResult.DataToString());
			TypeSDKLogger.i("支付成功");
			TypeSDKLogger.e("支付成功:"
					+ data.getString(ISDKEventExtraKey.EXTRA_ORDERID));
			TypeSDKLogger.e("PAY SUCCESS");
			break;
		case ISDKEventCode.CODE_CHARGE_FAIL:
			// 充值失败
			payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "0");
			payResult
					.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "支付失败");
			TypeSDKLogger.e("充值失败:"
					+ data.getString(ISDKEventExtraKey.EXTRA_ORDERID));
			break;
		case ISDKEventCode.CODE_CHARGE_CANCEL:
			// 取消支付
			payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT, "0");
			payResult
					.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "取消支付");
			TypeSDKLogger.e("取消支付:"
					+ data.getString(ISDKEventExtraKey.EXTRA_ORDERID));
			break;
		case ISDKEventCode.CODE_LOGOUT:
			// 注销
			// 发送通知，切换到开始登陆场景
			TypeSDKLogger.e("LOGOUT SUCCESS");
			mContext.sendBroadcast(new Intent(
					TypeSDKBonjour.ACTION_TO_START_LOGIN));
			break;
		case ISDKEventCode.CODE_EXIT:
			// 退出
			TypeSDKLogger.e("EXIT GAME");
			 mContext.sendBroadcast(new
			 Intent(TypeSDKBonjour.ACTION_TO_EXIT_GAME));
		}
	}
}
