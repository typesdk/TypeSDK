package com.type.sdk.android.jolo;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.jolo.sdk.JoloSDK;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	private RequestQueue mQueue;
	public Context appContext;
	public Activity appActivity;
	private boolean isDebug;
	private static boolean isCallRelogin=false;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		mQueue = Volley.newRequestQueue(appContext);
		if (isInit) {
			TypeSDKNotify_jolo notify = new TypeSDKNotify_jolo();
			notify.Init();
			return;
		}		
		this.joLoInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.joLoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		TypeSDKLogger.d( "ShowLogout");
		this.joLoLogout();
	}



	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		TypeSDKLogger.d(_in_OrderID);
		this.joLoPay(_in_pay);
		return _in_OrderID;
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);
		return PayItemByData(_in_context, _in_pay);
	}

	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return PayItem(_in_context, _in_data);
	}

	@Override
	public int LoginState(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d( "SendInfo");
		try {

			userInfo.StringToData(_in_data);

			if (userInfo.GetData("role_type").equals("enterGame")) {
				// 进入游戏时的角色信息
				TypeSDKLogger.d( "进入游戏时的角色信息");
			} else if (userInfo.GetData("role_type").equals("createRole")) {
				// 创建角色时的角色信息
				TypeSDKLogger.d( "创建角色时的角色信息");
			} else if(userInfo.GetData("role_type").equals("levelUp")){
				// 角色升级时角色信息
				TypeSDKLogger.d( "角色升级时角色信息");				
			}else{
				TypeSDKLogger.d( "datatype error:"+"提交的数据不合法");
			}

			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName", userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.d( "上传用户信息:string=" + userJsonExData);
			//			this.userInfo.CopyAttByData(userData);
		} catch (Exception e) {
			TypeSDKLogger.d( "上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.d( "执行ExitGame方法");
		System.exit(0);
	}

	public void onResume() {
		TypeSDKLogger.d( "onResume");
	}

	public void onPause() {
		TypeSDKLogger.d( "onPause");
	}

	public void onStop() {
		TypeSDKLogger.d( "onStop");
	}

	public void onDestroy() {
		JoloSDK.releaseJoloSDK();
		TypeSDKLogger.d( "onDestroy");
	}


	private void joLoInit() {

		TypeSDKLogger.d( "init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				JoloSDK.initJoloSDK(appContext, platform.GetData(AttName.APP_ID));
				TypeSDKNotify_jolo notify = new TypeSDKNotify_jolo();
				notify.Init();
				isInit=true;
				TypeSDKLogger.i("jolo inint success");
			}
		});
		TypeSDKLogger.v( "init done");
	}

	private void joLoLogin() {
		TypeSDKLogger.d( "login begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				JoloSDK.login(appActivity);
			}
		});
	}

	private void joLoLogout() {
		TypeSDKLogger.d( "logout begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				JoloSDK.logoff(appActivity);
				isCallRelogin=true;
				TypeSDKLogger.d( "logout success");
			}
		});
	}
	private String order; // 支付申请订单
	private String sign; // 支付订单签名(CP私钥签名)
	private String resultOrder;// 支付回执订单
	private String resultSign;// 支付回执订单签名(聚乐公钥验签)
	private void joLoPay(final PayInfoData _in_pay) {
		TypeSDKLogger.d( "pay begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());

		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				int price;
				if(isDebug){
					price = 1;
				}else{
					price = _in_pay.GetInt(AttName.REAL_PRICE);
				}
				Order or = new Order();
				//注意：参数里，不要出现类似“1元=10000个金币”的字段，因为“=”原因，会导致微信支付校验失败
				or.setAmount(price+""); // 设置支付金额，单位分
				or.setGameCode(platform.GetData(AttName.APP_ID)); // 设置游戏唯一ID,由Jolo提供
				or.setGameName(platform.GetData(AttName.APP_NAME)); // 设置游戏名称
				or.setGameOrderid(_in_pay.GetData(AttName.BILL_NUMBER)); // 设置游戏订单号
				or.setNotifyUrl(platform.GetData(AttName.PAY_CALL_BACK_URL)); // 设置支付通知
				or.setProductDes(_in_pay.GetData(AttName.ITEM_DESC)); // 设置产品描述
				or.setProductID(_in_pay.GetData(AttName.ITEM_SERVER_ID)); // 设置产品ID
				or.setProductName(_in_pay.GetData(AttName.ITEM_NAME)); // 设置产品名称
				or.setSession(session); // 设置用户session
				or.setUsercode(userId); // 设置用户ID
				order = or.toJsonOrder(); // 生成Json字符串订单
				sign = RsaSign.sign(order, platform.GetData(AttName.SECRET_KEY)); // 签名
				TypeSDKLogger.i("order:"+order.toString()+"||sign:" +sign);
				JoloSDK.startPay(appActivity, order, sign); // 启动支付
			}
		});
	}

	// 用户登录信息
	 	private String userName; // 用户名
	 	private String userId; // 用户ID
	 	private String session; // 用户登录session
	 	private String account; // 用户帐号信息
	 	private String accountSign; // 用户帐号信息签名(聚乐公钥验签)
	 	// 用户登录信息显示的VIEW
	    protected void onActivityResult(int requestCode, int resultCode, Intent data,int result_ok) {
	    	if (resultCode !=result_ok || data == null){
	    		TypeSDKLogger.i("login fail");
				return;
			}

			switch (requestCode) {
			case JoloSDK.ACCOUNT_REQUESTCODE: {
					TypeSDKLogger.i("login success");
				TypeSDKNotify_jolo notify=new TypeSDKNotify_jolo();
				
				// 用户账号名
				userName = data.getStringExtra(JoloSDK.USER_NAME);
				// 用户账号ID
				userId = data.getStringExtra(JoloSDK.USER_ID);
				// 账号的session，支付时使用
				session = data.getStringExtra(JoloSDK.USER_SESSION);
				// 用户帐号信息签名(聚乐公钥验签)，密文，CP对该密文用公钥进行校验
				accountSign = data.getStringExtra(JoloSDK.ACCOUNT_SIGN);
				// 用户帐号信息，明文，用户加密的字符串
				account = data.getStringExtra(JoloSDK.ACCOUNT);
				TypeSDKLogger.i("userId="+userId+"||account_sign = " + accountSign);
				if(isCallRelogin){
					isCallRelogin=false;
					TypeSDKData.UserInfoData userData= TypeSDKBonjour.Instance().userInfo;
					userData.SetData(TypeSDKDefine.AttName.USER_ID, userId);	
					userData.SetData(TypeSDKDefine.AttName.USER_TOKEN, accountSign);	
					userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.CP_ID);
					userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.SDK_NAME);
					userData.CopyAtt(TypeSDKBonjour.Instance().platform, AttName.PLATFORM);
					notify.reLogin(userData);
				}else{
					notify.sendToken(userId,accountSign);
				}
			}
				break;
				
			case JoloSDK.PAY_REQUESTCODE: {
				TypeSDKNotify_jolo notify=new TypeSDKNotify_jolo();
				PayResultData payResult = new PayResultData();
				resultOrder = data.getStringExtra(JoloSDK.PAY_RESP_ORDER);
				resultSign = data.getStringExtra(JoloSDK.PAY_RESP_SIGN);
				TypeSDKLogger.i("resultOrder : " + resultOrder+"resultSign :"+resultSign);
				if (RsaSign.doCheck(resultOrder, resultSign,
						platform.GetData(AttName.APP_KEY))) {
					// 校验支付订单后，解析订单内容
					ResultOrder or = new ResultOrder(resultOrder);
					String joloorderid = or.getJoloOrderID(); // jolo唯一订单号
					String amount = or.getRealAmount(); // 用户实际支付的金额
					int resultcode = or.getResultCode(); // 返回码, == 200为支付成功
					String resultmsg = or.getResultMsg(); // 返回提示信息
					TypeSDKLogger.i("joloorderid:" + joloorderid+"||amount:" + amount+"||resultcode:" + resultcode+"||resultmsg:" + resultmsg);
					TypeSDKLogger.i("pay success");
					payResult.SetData(AttName.PAY_RESULT, "1");
					payResult.SetData(AttName.PAY_RESULT_REASON, "Success");
					notify.Pay(payResult.DataToString());
				} else {
					TypeSDKLogger.i("pay fail");
					payResult.SetData(AttName.PAY_RESULT, "0");
					payResult.SetData(AttName.PAY_RESULT_REASON, "Fail");
					notify.Pay(payResult.DataToString());
				}
			}
				break;
			default:
				break;
			}
			}
	
	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}


}
