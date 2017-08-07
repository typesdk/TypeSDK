/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.type.sdk.android.TypeSDKData.BaseData;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.utils.HttpUtil;
import com.type.utils.SharedPreferencesUtil;

/**
 * sdk 基础类库
 * 
 * @author TypeSDK
 * 
 */
public abstract class TypeSDKBaseBonjour {

	protected static Activity baseAppActivity;
	protected static Context baseAppContext;

	/***
	 * platform渠道信息
	 */
	public TypeSDKData.PlatformData platform = new TypeSDKData.PlatformData();
	/***
	 * userInfo用户信息
	 */
	public TypeSDKData.UserInfoData userInfo = new TypeSDKData.UserInfoData();

	public static TypeSDKData.ItemListData itemListData = new TypeSDKData.ItemListData();
	/**
	 * 是否已经执行过 init function
	 */
	public static boolean isInit = false;
	public static int initState = 0;

	public void TypeSDKInit(Activity activity) {
		baseAppActivity = activity;
		baseAppContext = activity;

		TypeSDKLogger.d("platform为空，准备加载CPSettings.txt");
		String buffStr = TypeSDKTool.getFromAssets(activity, "CPSettings.txt");
		if (buffStr.length() > 0) {
			platform.StringToData(buffStr);
			TypeSDKLogger.i(platform.DataToString());
		} else {
			TypeSDKLogger.e("Read CPSettings.txt error!");
		}
		final String switchConfigUrl = platform
				.GetData(AttName.SWITCHCONFIG_URL);
		final String cp_id = platform.GetData(AttName.CP_ID);
		final String channel_id = platform.GetData(AttName.CHANNEL_ID);
		new Thread() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.d("StartGetConfig");
					String controllerMessage = HttpUtil.mHttpGet(
							switchConfigUrl, 5);
					TypeSDKTool.ctrlMessage(controllerMessage, baseAppContext);
					getItemList(controllerMessage, cp_id, channel_id);
					TypeSDKLogger.i("Finsh run");
				} catch (Exception e) {
					TypeSDKLogger.e("logcollector controller exception:"
							+ e.toString());
					e.printStackTrace();
				}
			}
		}.start();

	}

	/***
	 * 初始化TypeSDK
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public abstract void initSDK(Context _in_context, String _in_data);

	/**
	 * 调用登录界面
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void ShowLogin(final Context _in_context, String _in_data) {
		if (!TypeSDKTool.showLogin) {
			TypeSDKLogger.d("当前渠道被禁止登录");
			Handler dialogHandler = new Handler(Looper.getMainLooper());
			dialogHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (TypeSDKTool.msg != null && !TypeSDKTool.msg.isEmpty()) {
						TypeSDKTool.showDialog(TypeSDKTool.msg, _in_context);
						return;
					} else {
						TypeSDKTool.showDialog("维护中！！！", _in_context);
						return;
					}
				}
			});
		} else {
			TypeSDKLogger.d("当前渠道可以登录");
			SdkLogin(_in_context);
		}
	}

	public void SdkLogin(Context _in_context) {

	};

	/**
	 * 调用登出界面
	 * 
	 * @param _in_context
	 */
	public abstract void ShowLogout(Context _in_context);

	/**
	 * 调用渠道用户中心界面
	 * 
	 * @param _in_context
	 */
	public abstract void ShowPersonCenter(Context _in_context);

	/**
	 * 隐藏渠道用户中心界面
	 * 
	 * @param _in_context
	 */
	public abstract void HidePersonCenter(Context _in_context);

	/**
	 * 显示浮标
	 * 
	 * @param _in_context
	 */
	public abstract void ShowToolBar(Context _in_context);

	/**
	 * 隐藏浮标
	 * 
	 * @param _in_context
	 */
	public abstract void HideToolBar(Context _in_context);

	public String PayItem(Context _in_context, String _in_data) {
		return "";
	};

	/**
	 * 支付
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public String ShowPay(final Context _in_context, final String _in_string) {

		if (!TypeSDKTool.isOpenPay) {
			Handler dialogHandler = new Handler(Looper.getMainLooper());
			dialogHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					TypeSDKTool.showDialog("充值功能暂未开启", _in_context);
					SdkPayCancel();
				}
			});

		} else {
			TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
			_in_pay.StringToData(_in_string);
			SdkPay(_in_context, _in_pay);
		}
		return "";
	}

	protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay) {

	};

	protected void SdkPaySuccess() {

	};

	protected void SdkPayFail() {

	};

	protected void SdkPayCancel() {

	};

	/**
	 * 兑换道具
	 * 
	 * @param _in_context
	 * @param _in_data
	 * @return
	 */
	public abstract String ExchangeItem(Context _in_context, String _in_data);

	/**
	 * 查询登录状态״̬
	 * 
	 * @param _in_context
	 * @return
	 */
	public abstract int LoginState(Context _in_context);

	/**
	 * 调用分享
	 * 
	 * @param _in_context
	 * @param _in_data
	 */
	public void ShowShare(Context _in_context, String _in_data) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		try {
			intent.setClass(_in_context,
					Class.forName("user.package.name.WXEntryActivity"));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("shareInfo", _in_data);
		_in_context.startActivity(intent);
	};

	public abstract void ShowInvite(Context _in_context, String _in_data);

	public abstract String getUserFriends(Context _in_context, String _in_data);

	/**
	 * 调用退出游戏
	 * 
	 * @param _in_context
	 */
	public void ExitGame(Context _in_context) {
		SdkExit();
	}

	protected void SdkExit() {

	};

	public abstract void SendInfo(Context _in_context, String _in_data);

	public abstract void SetPlayerInfo(Context _in_context, String _in_data);

	public void AddLocalPush(final Context _in_context, String _in_data) {
		TypeSDKLogger.i("AddLocalPush");
		final TypeSDKData.BaseData baseData = new BaseData();
		baseData.StringToData(_in_data);
		addNotification(_in_context, baseData.GetData(AttName.PUSH_ID),
				baseData.GetData(AttName.PUSH_REPEAT_INTERVAL),
				baseData.GetData(AttName.PUSH_ALERT_DATE),
				baseData.GetData(AttName.PUSH_TYPE_DATA),
				baseData.GetData(AttName.PUSH_TITLE),
				baseData.GetData(AttName.PUSH_INFO),
				baseData.GetData(AttName.PUSH_NEED_NOTIFY),
				baseData.GetData(AttName.PUSH_RECEIVE_TYPE),
				baseData.GetData(AttName.PUSH_RECEIVE_INFO));
		Handler dialogHandler = new Handler(Looper.getMainLooper()) {
		};
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				// Toast.makeText(_in_context,
				// "添加推送"+baseData.GetData(AttName.PUSH_ID),
				// Toast.LENGTH_LONG).show();
			}
		});
	};

	public void RemoveLocalPush(final Context _in_context, String _in_data) {
		TypeSDKLogger.i("RemoveLocalPush");
		TypeSDKData.BaseData baseData = new BaseData();
		baseData.StringToData(_in_data);
		removeNotification(_in_context, baseData.GetData(AttName.PUSH_ID));
		// Intent intent = new Intent(_in_context, PushService.class);
		// _in_context.stopService(intent);
		// intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		// _in_context.startService(intent);
		Handler dialogHandler = new Handler(Looper.getMainLooper()) {
		};
		dialogHandler.post(new Runnable() {
			@Override
			public void run() {
				// Toast.makeText(_in_context, "移除一个推送",
				// Toast.LENGTH_LONG).show();
			}
		});
	};

	public void RemoveAllLocalPush(final Context _in_context) {
		TypeSDKLogger.i("RemoveAllLocalPush");

		NotificationManager mn = (NotificationManager) _in_context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mn.cancelAll();
		// Intent intent = new Intent(_in_context, PushService.class);
		// _in_context.stopService(intent);
		SharedPreferencesUtil util = new SharedPreferencesUtil(_in_context);
		if (util.read("id") != null) {
			String[] strings = util.read("id").split(";");
			for (int i = 0; i < strings.length; i++) {
				util.remove(strings[i]);
			}
			util.remove("id");
			Handler dialogHandler = new Handler(Looper.getMainLooper()) {
			};
			dialogHandler.post(new Runnable() {
				@Override
				public void run() {
					// Toast.makeText(_in_context, "移除所有推送",
					// Toast.LENGTH_LONG).show();
				}
			});
		}
	};

	public String GetUserData() {
		return userInfo.DataToString();
	}

	public String GetPlatformData() {
		return platform.DataToString();
	}

	/** 是否有指定的特别需求 */
	public boolean isHasRequest(String _request) {
		String requestStr = platform.GetData(AttName.SDK_REQUEST_AND_SUPPORT);
		if (null == requestStr || "".equals(requestStr))
			return false;

		String valueArr[] = requestStr.split(",");
		for (int i = 0; i < valueArr.length; ++i) {
			if (valueArr[i].equals(_request))
				return true;
		}

		return false;
	}

	public String DoAnyFunction(Context _in_context, String FuncName,
			String _in_data) {
		Method[] me = this.getClass().getMethods();
		for (int i = 0; i < me.length; ++i) {
			if (me[i].getName().equals(FuncName)) {
				try {
					return (String) me[i].invoke(this, _in_context, _in_data);
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

	/***
	 * 调用安卓底层的剪贴板
	 * 
	 * @param _in_context
	 * @param _in_text
	 *            其中表示具体内容的字段 extra
	 */
	public void OnCopyClipboard(Context _in_context, String _in_data) {
		BaseData data = new BaseData();
		data.StringToData(_in_data);
		Handler mainHandler = new Handler(Looper.getMainLooper());
		final Context runContext = _in_context;
		final String runText = data.GetData(AttName.EXTRA);
		Runnable runAb = new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				((ClipboardManager) runContext.getSystemService("clipboard"))
						.setText(runText);
			}
		};
		mainHandler.post(runAb);
	}

	public void addNotification(Context _in_context, String id, String period,
			String time, String ticker, String title, String content,
			String extra, String receiveType, String receiveInfo) {

		SharedPreferencesUtil util = new SharedPreferencesUtil(_in_context);
		if (util.read("id") != null) {
			TypeSDKLogger.i(util.read("id"));
			if (!util.read("id").contains(id)) {
				util.save("id", util.read("id") + id + ";");
			}
			TypeSDKLogger.i(util.read("id"));
			util.save(id, period + ";" + time + ";" + ticker + ";" + title
					+ ";" + content + ";" + extra + ";" + receiveType + ";"
					+ receiveInfo);
			TypeSDKLogger.i(util.read(id));
		} else {
			TypeSDKLogger.i("Not had id");
			util.save("id", id + ";");
			util.save(id, period + ";" + time + ";" + ticker + ";" + title
					+ ";" + content + ";" + extra + ";" + receiveType + ";"
					+ receiveInfo);
			TypeSDKLogger.i(util.read("id"));
		}
	}

	public void removeNotification(Context _in_context, String id) {
		SharedPreferencesUtil util = new SharedPreferencesUtil(_in_context);
		TypeSDKLogger.i(util.read("id"));
		TypeSDKLogger.i(util.read(id));
		if (util.read("id") != null && util.read(id) != null) {
			TypeSDKLogger.i(util.read("id"));
			util.save("id", util.read("id").replace(id + ";", ""));
			TypeSDKLogger.i(util.read("id"));
			util.remove(id);

		} else {

		}
	}

	private static long lastClickTime;// 记录调用ExitGame的时间

	protected boolean exitGameListenser() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		TypeSDKLogger.i("time:" + time);
		TypeSDKLogger.i("timeD:" + timeD);
		boolean bl = true;
		if (0 < timeD && timeD < 100) {
			bl = false;
		}
		lastClickTime = time;
		return bl;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	protected boolean exitBy2Click(Context _in_context) {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			TypeSDKLogger.i("准备退出");
			Toast.makeText(_in_context, "再按一次退出游戏", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
					TypeSDKLogger.i("取消退出");
				}
			}, 1500);
			return false;
		} else {
			return true;
		}
	}

	// 用户行为统计 定时
	// public void clickButtonTimes(Context _in_context, String _in_data) {
	// }

	// 用户行为统计 实时
	// public void clickButtonNow(final Context _in_context, final String
	// _in_data) {
	//
	// }

	// 用户行为统计参数
	// public static Map<String, Object> getMap(Context _in_context) {
	//
	// return null;
	// }

	// 跳转到指定url的网页
	public void openURL(Context _in_context, String _in_data) {
		TypeSDKData.BaseData urlData = new TypeSDKData.BaseData();
		urlData.StringToData(_in_data);
		TypeSDKLogger.i("_in_data:" + _in_data);
		Uri uri = Uri.parse(urlData.GetData(AttName.EXTRA));
		TypeSDKLogger.i("url:" + uri.toString());
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		it.setClassName("com.android.browser",
				"com.android.browser.BrowserActivity");
		_in_context.startActivity(it);
	}

	// 重启Activity
	public void restartActivity(Context _in_context) {
		Activity activity = (Activity) _in_context;
		Intent intent = activity.getIntent();
		activity.finish();
		_in_context.startActivity(intent);
	}

	protected void getItemList(String str_itemListConfigJson, String cp_id,
			String channel_id) {
		// itemList
		TypeSDKData.BaseData configData = new TypeSDKData.BaseData();
		if (str_itemListConfigJson.length() > 0) {
			configData.StringToData(str_itemListConfigJson);
			String itemListUrl = configData.GetData("itemListUrl");
			TypeSDKLogger.i("itemistUrl=" + itemListUrl);
			if (itemListUrl != null && !itemListUrl.isEmpty()) {
				// TypeSDKLogger.i("getItemList itemListUrl:" + itemListUrl);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("gameId", cp_id);
				map.put("channelId", channel_id);
				HttpUtil.isPostok = true;
				while (HttpUtil.isPostok) {
					// TypeSDKLogger.i("itemList itemListUrl:" + itemListUrl);
					String result = HttpUtil.mHttpPost(itemListUrl, map);
					TypeSDKLogger.i("itemList result:" + result);
					if (result == null || result.isEmpty()) {
						return;
					}
					TypeSDKData.ItemListData tempdata = new TypeSDKData.ItemListData();
					tempdata.StringToData(result);
					if (tempdata.GetData("code").equals("0")) {
						tempdata.StringToData(tempdata.GetData("itemList"));
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

}
