package com.type.sdk.android.pyw;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.pengyouwan.sdk.api.PYWPlatform;
import com.pengyouwan.sdk.api.PayConstant;
import com.pengyouwan.sdk.utils.FloatViewTool;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public static final String ACTION_LOGIN_SDK_SUCCESS = "com.type.sdk.pyw.ACTION_LOGIN_SDK_SUCCESS";
	public static final String ACTION_TO_START_LOGIN = "com.type.sdk.pyw.ACTION_TO_START_LOGIN";
    public static final String ACTION_TO_EXIT_GAME = "com.type.sdk.pyw.ACTION_TO_EXIT";
	private LoginResultReceiver mReceiver;

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		TypeSDKLogger.i("isInit=" + isInit);
		if (isInit) {
			TypeSDKLogger.e("error init do again");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}
		PYWSdkInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.PYWSdkLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.e("ShowLogout");
		this.PYWSdkLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		TypeSDKLogger.e("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {

		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.PYWSdkPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);

		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
	}

	@Override
	public void ExitGame(Context _in_context) {
		PYWSdkExit();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
	}

	public void onPause(Context _in_context) {
		TypeSDKLogger.e("onPause");
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
		hideFloatView();
	}

	public void onDestroy() {
		if (mReceiver != null) {
			appContext.unregisterReceiver(mReceiver);
		}
        FloatViewTool.instance(appActivity).destroyFloatView();
	}

	/**
	 * 注册登录SDK成功广播接收器
	 * 注意！！1、该广播不是SDK内部发出给游戏接收的！而是游戏收到SDK登录回调事件ENTER_GAME_SUCCESS发出通知界面的！
	 * 2、登录成功后通过广播通知界面，这是demo采用的一种实现方式，不是SDK要求的固定实现方案，具体实现方案由游戏自行决定选择！
	 */
	private void initLoginSDKSuccessReceiver() {
		mReceiver = new LoginResultReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGIN_SDK_SUCCESS);
		filter.addAction(ACTION_TO_START_LOGIN);
        filter.addAction(ACTION_TO_EXIT_GAME);
		appContext.registerReceiver(mReceiver, filter);
	}

	/**
	 * 描述:登录SDK成功广播接收者
	 */
	class LoginResultReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TypeSDKBonjour.ACTION_LOGIN_SDK_SUCCESS.equals(action)) {
				showFloatView();
			}else if (TypeSDKBonjour.ACTION_TO_START_LOGIN.equals(action)) {
				PYWSdkLogout();
//				hideFloatView();
                // 此处可知是经过切换账号的逻辑到此处切换场景，故可以主动调起一次sdk登陆
//                PYWPlatform.openLogin(appActivity);
            } else if (TypeSDKBonjour.ACTION_TO_EXIT_GAME.equals(action)) {
                exit();//退出
            }
		}
	}

	/**
	 * 显示浮点
	 */
	private void showFloatView() {
		if (null != PYWPlatform.getCurrentUser()) { // 表示当前登录状态
			FloatViewTool.instance(appActivity).showFloatView();
		}
	}

	/**
	 * 隐藏浮点
	 */
	private void hideFloatView() {
		FloatViewTool.instance(appActivity).hideFloatView();
	}
	/**
     * 游戏退出
     */
    private void exit() {
//        finish();
    	System.exit(0);
        Process.killProcess(Process.myPid());
    }

	private void PYWSdkInit() {
		TypeSDKLogger.e("PYWSdkInit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// PYWPlatform.initSDK(this, sdkconfig, new
				// SDKEventListener(this));
				TypeSDKNotify_PYW typeSDKNotify_PYW = new TypeSDKNotify_PYW();
				typeSDKNotify_PYW.Init();
				TypeSDKLogger.e("PYWINIT SUCCESS");
				// 注册登录SDK成功的广播
				initLoginSDKSuccessReceiver();
			}
		});
	}

	private void PYWSdkLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PYWPlatform.openLogin(appActivity);
				TypeSDKLogger.e("LOGIN SUCCESS");
			}
		});

	}

	private void PYWSdkLogout() {
		TypeSDKLogger.e("PYWSdkLogout start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				hideFloatView();
				TypeSDKNotify_PYW notify = new TypeSDKNotify_PYW();
				notify.Logout();
			}
		});
	}

	private void PYWSdkExit() {
		TypeSDKLogger.e("PYWSdkExit start");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				PYWPlatform.exit(appActivity);
//				Process.killProcess(Process.myPid());
//				System.exit(0);
				TypeSDKLogger.e("EXIT SUCCESS");
			}
		});

	}

	private void PYWSdkPay(final TypeSDKData.PayInfoData _in_pay) {
		int price = 0;
		if (TypeSDKTool.isPayDebug) {
			price = 1;
		} else {
			price = _in_pay.GetInt(AttName.REAL_PRICE);
		}
		final float priceValue = price * 0.01f;
		
		final Map<String, Object> paramsMap = new HashMap<String, Object>();
		// 产品名称，用于显示在朋友玩充值界面，请于游戏界面中的商品保持一致
        paramsMap.put(PayConstant.PAY_PRODUCE_NAME, _in_pay.GetData(AttName.ITEM_NAME));
        // 实际充值金额，单位元 可任意金额 不需要与产品id对应
        paramsMap.put(PayConstant.PAY_MONEY, (int)priceValue);
     // 订单id，此项必须要填写，并且参数名必须为"order_id"，否则会出错
        paramsMap.put(PayConstant.PAY_ORDER_ID, _in_pay.GetData(AttName.BILL_NUMBER));
     // 厂商需要朋友玩回调时回传的参数，届时会原样返回
        paramsMap.put(PayConstant.PAY_EXTRA, getOrderExtraParams(_in_pay));

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// 调用定额充值--第三个参数为false
		        PYWPlatform.openChargeCenter(appActivity, paramsMap, true);
				// TypeSDKLogger.e("PAY SUCCESS");
			}
		});

	}
	
	/**
     * 模拟发送订单额外数据（非必传）CP根据需要传
     * 
     * @return
     */
    private String getOrderExtraParams(final TypeSDKData.PayInfoData _in_pay) {
        JSONObject jobj = new JSONObject();
        try {
            // 以下为非必要参数，只供参考，厂商可根据自身需求决定传什么参数与值或者不传
            jobj.put("roles_nick", userInfo.GetData(AttName.ROLE_NAME));
            jobj.put("area_name", userInfo.GetData(AttName.SERVER_NAME));
            jobj.put("area_num", userInfo.GetData(AttName.SERVER_ID));
            jobj.put("channel", platform.GetData(AttName.CHANNEL_ID));
            jobj.put("product_desc", _in_pay.GetData(AttName.ITEM_DESC));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jobj.toString();
    }

	// private void paPaSdkSendInfo(final JSONObject _jsonExData) {}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		SetPlayerInfo(_in_context, _in_data);
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return "";
	}

}
