package com.type.sdk.android.tencent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.*;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.module.bugly.BuglyListener;
import com.tencent.ysdk.module.user.PersonInfo;
import com.tencent.ysdk.module.user.UserListener;
import com.tencent.ysdk.module.user.UserLoginRet;
import com.tencent.ysdk.module.user.UserRelationRet;
import com.tencent.ysdk.module.user.WakeupRet;

public class YSDKCallback implements UserListener, BuglyListener {

	public static String openId = "";
	public static String pf = "";
	public static String pfKey = "";
	public static String accessToken = "";
	public static String payToken = "";
	public static Context localContext = null;
	public boolean isLogin = true;

	@Override
	public byte[] OnCrashExtDataNotify() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String OnCrashExtMessageNotify() {
		// TODO Auto-generated method stub
		// 此处游戏补充crash时上报的额外信息
		TypeSDKLogger.d("OnCrashExtMessageNotify called");
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return "new Upload extra crashing message for bugly on "
				+ time.format(nowTime);
	}

	@Override
	public void OnLoginNotify(UserLoginRet ret) {
		// TODO Auto-generated method stub
		TypeSDKNotify_Tencent notify = new TypeSDKNotify_Tencent();
		switch (ret.flag) {
		case eFlag.Succ:
			TypeSDKLogger.e("Login success:" + ret.toString());
			openId = ret.open_id;
			payToken = ret.getPayToken();
			pf = ret.pf;
			pfKey = ret.pf_key;
			accessToken = ret.getAccessToken();
			TypeSDKLogger.i("platform:" + ret.platform);
			TypeSDKBonjour.logintype = ret.platform;
			notify.sendToken(YSDKCallback.openId, YSDKCallback.accessToken);
			if(isLogin){
				new PostHelper(localContext, YSDKCallback.openId).execute();
				isLogin = false;
			}
			
			new NetHelper().execute();
			
			break;
		// 游戏逻辑，对登录失败情况分别进行处理
		case eFlag.QQ_UserCancel:
			TypeSDKLogger.e("用户取消授权，请重试");
			// showTips("用户取消授权，请重试",localContext);
			break;
		case eFlag.QQ_LoginFail:
			TypeSDKLogger.e("QQ登录失败，请重试");
			// showTips("QQ登录失败，请重试",localContext);
			break;
		case eFlag.QQ_NetworkErr:
			TypeSDKLogger.e("QQ登录失败，请重试");
			// showTips("QQ登录失败，请重试",localContext);
			break;
		case eFlag.QQ_NotInstall:
			TypeSDKLogger.e("手机未安装手Q，请安装后重试");
			showTips("手机未安装手Q，请安装后重试", localContext);
			break;
		case eFlag.QQ_NotSupportApi:
			TypeSDKLogger.e("手机手Q版本太低，请升级后重试");
			showTips("手机手Q版本太低，请升级后重试", localContext);
			break;
		case eFlag.WX_NotInstall:
			TypeSDKLogger.e("手机未安装微信，请安装后重试");
			showTips("手机未安装微信，请安装后重试", localContext);
			break;
		case eFlag.WX_NotSupportApi:
			TypeSDKLogger.e("手机微信版本太低，请升级后重试");
			showTips("手机微信版本太低，请升级后重试", localContext);
			break;
		case eFlag.WX_UserCancel:
			TypeSDKLogger.e("用户取消授权，请重试");
			// showTips("用户取消授权，请重试",localContext);
			break;
		case eFlag.WX_UserDeny:
			TypeSDKLogger.e("用户拒绝了授权，请重试");
			// showTips("用户拒绝了授权，请重试",localContext);
			break;
		case eFlag.WX_LoginFail:
			TypeSDKLogger.e("微信登录失败，请重试");
			// showTips("微信登录失败，请重试",localContext);
			break;
		case eFlag.Login_TokenInvalid:
			TypeSDKLogger.e("您尚未登录或者之前的登录已过期，请重试");
			// showTips("您尚未登录或者之前的登录已过期，请重试",localContext);
			break;
		case eFlag.Login_NotRegisterRealName:
			// 显示登录界面
			TypeSDKLogger.e("您的账号没有进行实名认证，请实名认证后重试");
			showTips("您的账号没有进行实名认证，请实名认证后重试", localContext);
			break;
		default:
			// 显示登录界面
			break;
		}
	}

	private void showTips(final String tips, final Context context) {
		if (context != null) {
			if (TypeSDKBonjour.isCallLogin) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						TypeSDKTool.showDialog(tips, context);
					}
				});
			}
		} else {
			TypeSDKLogger.e("showTips context is null");
		}

	}

	@Override
	public void OnRelationNotify(UserRelationRet ret) {
		// TODO Auto-generated method stub
		String result = "";
		result = result + "flag:" + ret.flag + "\n";
		result = result + "msg:" + ret.msg + "\n";
		result = result + "platform:" + ret.platform + "\n";
		if (ret.persons != null && ret.persons.size() > 0) {
			PersonInfo personInfo = (PersonInfo) ret.persons.firstElement();
			StringBuilder builder = new StringBuilder();
			builder.append("UserInfoResponse json: \n");
            builder.append("nick_name: " + personInfo.nickName + "\n");
            builder.append("open_id: " + personInfo.openId + "\n");
            builder.append("userId: " + personInfo.userId + "\n");
            builder.append("gender: " + personInfo.gender + "\n");
            builder.append("picture_small: " + personInfo.pictureSmall + "\n");
            builder.append("picture_middle: " + personInfo.pictureMiddle + "\n");
            builder.append("picture_large: " + personInfo.pictureLarge + "\n");
            builder.append("provice: " + personInfo.province + "\n");
            builder.append("city: " + personInfo.city + "\n");
            builder.append("country: " + personInfo.country + "\n");
			result = result + builder.toString();
		} else {
			result = result + "relationRet.persons is bad";
		}
		TypeSDKLogger.d("OnRelationNotify" + result);

		// 发送结果到结果展示界面
	}

	@Override
	public void OnWakeupNotify(WakeupRet ret) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("called");
		TypeSDKLogger.d("flag:" + ret.flag);
		TypeSDKLogger.d("msg:" + ret.msg);
		TypeSDKLogger.d("platform:" + ret.platform);
		int platform = ret.platform;
		// TODO GAME 游戏需要在这里增加处理异账号的逻辑
		if (eFlag.Wakeup_YSDKLogining == ret.flag) {
			// 用拉起的账号登录，登录结果在OnLoginNotify()中回调
		} else if (ret.flag == eFlag.Wakeup_NeedUserSelectAccount) {
			// 异账号时，游戏需要弹出提示框让用户选择需要登录的账号
			TypeSDKLogger.d("diff account");
		} else if (ret.flag == eFlag.Wakeup_NeedUserLogin) {
			// 没有有效的票据，登出游戏让用户重新登录
			TypeSDKLogger.d("need login");
		} else {
			TypeSDKLogger.d("logout");
		}

	}

	String refreshResult;

	// 客户端收到登录成功回调（正常登录，票据刷新），发送SDK服务端刷新票据
	private class NetHelper extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("openid", YSDKCallback.openId);
				map.put("openkey", YSDKCallback.accessToken);
				map.put("pf", YSDKCallback.pf);
				map.put("pfkey", YSDKCallback.pfKey);
				map.put("payToken", YSDKCallback.payToken);
				String beforeSignString = "openid=" + YSDKCallback.openId
					+ "&openkey=" + YSDKCallback.accessToken
					+ "&payToken=" + YSDKCallback.payToken
					+ "&pf=" + YSDKCallback.pf 
					+ "&pfkey=" + YSDKCallback.pfKey + "clientKey";
				TypeSDKLogger.e("login beforeSignString:" + beforeSignString);
				String sign = TypeSDKBonjour.Instance().md5(beforeSignString);
				map.put("sign", sign);
				refreshResult = HttpUtil._post(TypeSDKBonjour
						.Instance().platform.GetData("tokenUrl"), map);
				TypeSDKLogger.i("tokenUrl reuslt:" + refreshResult);
			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

}
