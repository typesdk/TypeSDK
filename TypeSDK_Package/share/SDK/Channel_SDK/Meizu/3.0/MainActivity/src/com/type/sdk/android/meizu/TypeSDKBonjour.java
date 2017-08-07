package com.type.sdk.android.meizu;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.meizu.gamesdk.model.callback.MzLoginListener;
import com.meizu.gamesdk.model.callback.MzPayListener;
import com.meizu.gamesdk.model.model.LoginResultCode;
import com.meizu.gamesdk.model.model.MzAccountInfo;
import com.meizu.gamesdk.model.model.PayResultCode;
import com.meizu.gamesdk.online.core.MzGameBarPlatform;
import com.meizu.gamesdk.online.core.MzGameCenterPlatform;
import com.meizu.gamesdk.online.model.model.MzBuyInfo;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.HttpUtil;
import com.type.utils.MD5Util;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;

	MzGameBarPlatform mzGameBarPlatform;

	String orderId; // cp_order_id (娑撳秷鍏樻稉铏光敄)
	String sign; // sign (娑撳秷鍏樻稉铏光敄)
	String signType = "md5"; // sign_type (娑撳秷鍏樻稉铏光敄)
	int buyCount = 1; // buy_amount
	String cpUserInfo; // user_info
	String amount; // total_price
	String productId; // product_id
	String productSubject; // product_subject
	String productBody; // product_body
	String productUnit; // product_unit
	String appid; // app_id (娑撳秷鍏樻稉铏光敄)
	String uid; // uid (娑撳秷鍏樻稉铏光敄)
	String perPrice; // product_per_price
	long createTime; // create_time
	int payType = 0; // pay_type

	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance() {
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
			notify.Init();
			return;
		}
		this.meizuInit();

	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.meizuLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.meizuLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowToolBar");
		mzGameBarPlatform.showGameBar();
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
		mzGameBarPlatform.hideGameBar();
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.meizuPay(_in_pay);
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
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("SendInfo:" + _in_data);
		try {
			userInfo.StringToData(_in_data);

			String extendInfo = new StringBuilder().append("gameId=")
					.append(platform.GetData(AttName.APP_ID))
					.append("&service=")
					.append(userInfo.GetData(AttName.SERVER_NAME))
					.append("&role=").append(userInfo.GetData(AttName.ROLE_ID))
					.append("&grade=")
					.append(userInfo.GetData(AttName.ROLE_LEVEL)).toString();
			TypeSDKLogger.e("extendInfo:" + extendInfo);

		} catch (Exception e) {
			TypeSDKLogger.e("娑撳﹣绱堕悽銊﹀煕娣団剝浼�:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("閹笛嗩攽ExitGame閺傝纭�");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// 濞夈劍鍓伴敍锟�
				// 1.logout()閺傝纭堕崣顏堟付鐟曚礁婀〒鍛婂灆缂佹挻娼敍鍫㈡暏閹寸兘锟斤拷閸戠儤鐖堕幋蹇ョ礆閺冩儼鐨熼悽銊ュ祮閸欘垽绱濋悽銊﹀煕濞夈劑鏀㈢拹锔藉煕閺冩湹绗夐棁锟界憰浣界殶閻€劊锟斤拷
				// 2.婵″倹鐏夐悽銊﹀煕闁瀚ㄥ▔銊╂敘鐠愶附鍩涢敍灞剧埗閹村繐褰ч棁锟界憰浣圭闂勩倛鍤滈煬顐ヮ唶瑜版洜娈戦悽銊﹀煕娣団剝浼呴崡鍐插讲閵嗭拷
				// 3.婵″倿娓剁憰渚�鍣搁弬鎵瑜版洏锟戒礁鍨忛幑銏ｅ閹撮鐡戦幙宥勭稊閿涘本鐖堕幋蹇撳涧闂囷拷閸忓牊绔婚梽銈嗘拱闊偉顔囪ぐ鏇犳畱鐠愶附鍩涙穱鈩冧紖閿涘瞼鍔ч崥搴ょ殶閻拷 login()閹恒儱褰涢敍锟�
				// 閻ц缍嶅ù浣衡柤娑擃厾鏁ら幋宄板祮閸欘垰浠涢惄绋垮彠閻ㄥ嫬鍨忛幑銏℃惙娴ｏ拷
				MzGameCenterPlatform.logout(appContext);
				appActivity.finish();
				System.exit(0);
			}
		});
		
	}

	public void onCreate(Activity activity) {
		// TODO 閸掓繂顫愰崠鏍电礉 閸欘垯浜掗幐鍥х暰 Gamebar 缁楊兛绔村▎鈩冩▔缁�铏规畱娴ｅ秶鐤嗛敍灞芥躬濞撳憡鍨欓柅锟介崙鐑樻娴兼俺顔囨担蹇曟暏閹撮攱鎼锋担婊呮畱閺堬拷閸氬簼绔村▎鈥茬秴缂冾噯绱濋崘宥嗩偧閸氾拷
		// 閸斻劍妞傛担璺ㄦ暏娑撳﹣绔村▎锛勬畱娴ｅ秶鐤�
		// 缁楊兛绔村▎鈩冩▔缁�铏规畱娴ｅ秶鐤嗛崣顖欎簰閹稿洤鐣鹃崶娑楅嚋閺傜懓鎮滈敍灞戒箯娑撳绱濆锔跨瑓閿涘苯褰告稉濠忕礉閸欏厖绗�
		// public static final int GRAVITY_LEFT_TOP = 1;
		// public static final int GRAVITY_LEFT_BOTTOM = 2;
		// public static final int GRAVITY_RIGHT_TOP = 3;
		// public static final int GRAVITY_RIGHT_BOTTOM = 4;
		mzGameBarPlatform = new MzGameBarPlatform(activity,
				MzGameBarPlatform.GRAVITY_RIGHT_BOTTOM);
		mzGameBarPlatform.onActivityCreate();
	}

	public void onResume(Context _in_context) {
		TypeSDKLogger.e("onResume");
		mzGameBarPlatform.onActivityResume();
	}

	public void onPause() {
		TypeSDKLogger.e("onPause");
		mzGameBarPlatform.onActivityPause();
	}

	public void onStop() {
		TypeSDKLogger.e("onStop");

	}

	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
		mzGameBarPlatform.onActivityDestroy();
	}

	private void meizuInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.d("APP_ID:"
							+ platform.GetData(AttName.APP_ID));
					TypeSDKLogger.d("APP_KEY:"
							+ platform.GetData(AttName.APP_KEY));
					MzGameCenterPlatform.init(appContext,
							platform.GetData(AttName.APP_ID),
							platform.GetData(AttName.APP_KEY));
					productUnit = platform.GetData("product_unit");
					TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
					notify.Init();
					isInit = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		TypeSDKLogger.e("init done");

	}

	private void meizuLogin() {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO 鐠嬪啰鏁ら惂璇茬秿閹恒儱褰涢妴锟� 濞夈劍鍓伴敍宀冾嚉閺傝纭惰箛鍛淬�忛崷銊ョ安閻€劎娈戞稉鑽ゅ殠缁嬪鑵戠拫鍐暏閵嗭拷
				MzGameCenterPlatform.login(appActivity, new MzLoginListener() {
					@Override
					public void onLoginResult(int code,
							MzAccountInfo accountInfo, String errorMsg) {
						// TODO 閻ц缍嶇紒鎾寸亯閸ョ偠鐨熼妴锟� 濞夈劍鍓伴敍宀冾嚉閸ョ偠鐨熺捄鎴濇躬鎼存梻鏁ゆ稉鑽ゅ殠缁嬪绱濇稉宥堝厴閸︺劏绻栭柌灞戒粵閼版妞傞幙宥勭稊
						switch (code) {
						case LoginResultCode.LOGIN_SUCCESS:
							// TODO 閻ц缍嶉幋鎰閿涘本瀣侀崚鐨峣d 閸滐拷 session閸掓媽鍤滃杈╂畱閺堝秴濮熼崳銊ュ箵閺嶏繝鐛檚ession閸氬牊纭堕幀锟�
							String mUid = accountInfo.getUid();
							String mSession = accountInfo.getSession();
							TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
							notify.sendToken(mUid, mSession);
							break;
						case LoginResultCode.LOGIN_ERROR_CANCEL:
							// TODO 閻€劍鍩涢崣鏍ㄧХ閻у妾伴幙宥勭稊
							TypeSDKLogger.e("LoginResultCode:"
									+ "LOGIN_ERROR_CANCEL");
							break;
						default:
							// TODO 閻у妾版径杈Е閿涘苯瀵橀崥顐︽晩鐠囶垳鐖滈崪宀勬晩鐠囶垱绉烽幁顖橈拷锟�
							// TODO 濞夈劍鍓伴敍宀勬晩鐠囶垱绉烽幁锟�(errorMsg)闂囷拷鐟曚胶鏁卞〒鍛婂灆鐏炴洜銇氱紒娆戞暏閹村嚖绱濋幓鎰仛婢惰精瑙﹂崢鐔锋礈
							TypeSDKLogger.e("LoginResultCode:"
									+ "LOGIN_ERROR_EXTRA:code:" + code + "\nerrorMsg:" + errorMsg);
							break;
						}
					}
				});
			}
		});

	}

	private void meizuLogout() {
		TypeSDKLogger.d("meizuLogout");
		TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
		notify.Logout();

	}

	private void meizuPay(final PayInfoData _in_pay) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.d("pay_start");
					TypeSDKLogger.i("payInfo:" + _in_pay.DataToString());
					float price;
					if (TypeSDKTool.isPayDebug) {
						price = 1.0f;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE) / 100;
					}
					TypeSDKLogger.e("price:" + price);

					// TODO 娴犮儰绗呮穱鈩冧紖闂囷拷鐟曚礁顓归幋椋庮伂闁俺绻冪純鎴犵捕鐠佸潡妫堕懛顏勭箒閻ㄥ嫭婀囬崝鈥虫珤閼惧嘲褰囬敍灞筋吂閹撮顏稉宥夋付鐟曚椒绡冩稉宥堝厴閸︺劍婀伴崷鎵晸閹存劑锟斤拷
					// TODO 濮濄倕顦╅崥鍕摟濞堥潧锟界厧顕惔鎿烶閺堝秴濮熺粩顖滄晸閹存劘顓归崡鏇犳畱娣団剝浼�(閸氬酣娼伴惃鍕雹閼规彃鐡х�电懓绨查張宥呭缁旑垳娈戠�涙顔�)閿涘矁顕涢幆鍛邦嚞閸欏倽锟斤拷
					// 5.1.3 閸掓稑缂撻張宥呭缁旑垵顓归崡鏇橈拷锟� 閸忔湹鑵慶p_order_id閵嗭拷 uid閵嗭拷 sign閵嗭拷 sign_type閵嗭拷
					// uid娑撳秷鍏樻稉铏光敄
					orderId = _in_pay.GetData(AttName.BILL_NUMBER); // cp_order_id
																	// (娑撳秷鍏樻稉铏光敄)
					cpUserInfo = _in_pay.GetData(AttName.EXTRA); // user_info
					amount = String.valueOf(price); // total_price
					productId = _in_pay.GetData(AttName.ITEM_SERVER_ID); // product_id
					productSubject = _in_pay.GetData(AttName.ITEM_NAME); // product_subject
					productBody = _in_pay.GetData(AttName.ITEM_DESC); // product_body
					appid = platform.GetData(AttName.APP_ID); // app_id
																// (娑撳秷鍏樻稉铏光敄)
					uid = _in_pay.GetData(AttName.USER_ID); // uid (娑撳秷鍏樻稉铏光敄)
					perPrice = String.valueOf(price); // product_per_price

					new DialogHelper().execute(1);
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: "
							+ exception.toString());
				}

			}
		});

	}

	String jsonResult;

	private class DialogHelper extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			switch (params[0]) {
			case 1:
				try {
					jsonResult = getBillNo(uid, amount, orderId, productSubject,
							appid, platform.GetData("url"), "1", "0", productBody,
							productId, perPrice,productUnit,
							cpUserInfo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				JSONObject jsonObject = new JSONObject(jsonResult);
				sign = jsonObject.getString("sign");
				createTime = Long
						.parseLong(jsonObject.getString("create_time"));

				TypeSDKLogger.e("jsonResult:" + jsonResult);
				TypeSDKLogger.e("sign:" + sign);
				TypeSDKLogger.e("createTime:" + createTime);
				// 鐠嬪啰鏁ら弨顖欑帛閹恒儱褰�
				Bundle buyInfo = new MzBuyInfo().setBuyCount(buyCount)
						.setCpUserInfo(cpUserInfo).setOrderAmount(amount)
						.setOrderId(orderId).setPerPrice(perPrice)
						.setProductBody(productBody).setProductId(productId)
						.setProductSubject(productSubject)
						.setProductUnit(productUnit).setSign(sign)
						.setSignType(signType).setCreateTime(createTime)
						.setAppid(appid).setUserUid(uid).setPayType(payType).toBundle();
				
				TypeSDKLogger.i("buyInfo:"+buyInfo.toString());
				// TODO 鐠嬪啰鏁ら弨顖欑帛閹恒儱褰涢妴锟� 濞夈劍鍓伴敍宀冾嚉閺傝纭惰箛鍛淬�忛崷銊ョ安閻€劎娈戞稉鑽ゅ殠缁嬪鑵戠拫鍐暏閵嗭拷
				MzGameCenterPlatform.payOnline(appActivity, buyInfo,
						new MzPayListener() {

							@Override
							public void onPayResult(int code, Bundle info,
									String errorMsg) {
								// TODO 閺�顖欑帛缂佹挻鐏夐崶鐐剁殶閿涘矁顕氶崶鐐剁殶鐠烘垵婀惔鏃傛暏娑撹崵鍤庣粙瀣拷锟�
								// 濞夈劍鍓伴敍宀冾嚉閸ョ偠鐨熺捄鎴濇躬鎼存梻鏁ゆ稉鑽ゅ殠缁嬪绱濇稉宥堝厴閸︺劏绻栭柌灞戒粵閼版妞傞幙宥勭稊
								PayResultData payResult = new PayResultData();
								TypeSDKNotify_meizu notify = new TypeSDKNotify_meizu();
								switch (code) {
								case PayResultCode.PAY_SUCCESS:
									// TODO 婵″倹鐏夐幋鎰閿涘本甯存稉瀣箵闂囷拷鐟曚礁鍩岄懛顏勭箒閻ㄥ嫭婀囬崝鈥虫珤閺屻儴顕楃拋銏犲礋缂佹挻鐏�
									payResult.SetData(AttName.PAY_RESULT, "1");
									payResult.SetData(AttName.PAY_RESULT_DATA,
											"MzBuyInfo:"+info.toString()+"\n"+"errorMsg:"+errorMsg);									
									notify.Pay(payResult.DataToString());

									break;
								case PayResultCode.PAY_ERROR_CANCEL:
									// TODO 閻€劍鍩涙稉璇插З閸欐牗绉烽弨顖欑帛閹垮秳缍旈敍灞肩瑝闂囷拷鐟曚焦褰佺粈铏规暏閹村嘲銇戠拹锟�
									payResult.SetData(AttName.PAY_RESULT, "2");
									payResult.SetData(AttName.PAY_RESULT_DATA,
											"MzBuyInfo:"+info.toString()+"\n"+"errorMsg:"+errorMsg);									
									notify.Pay(payResult.DataToString());
									break;
								default:
									// TODO 閺�顖欑帛婢惰精瑙﹂敍灞藉瘶閸氼偊鏁婄拠顖滅垳閸滃矂鏁婄拠顖涚Х閹垬锟斤拷
									// TODO 濞夈劍鍓伴敍宀勬晩鐠囶垱绉烽幁锟�(errorMsg)闂囷拷鐟曚胶鏁卞〒鍛婂灆鐏炴洜銇氱紒娆戞暏閹村嚖绱濋幓鎰仛婢惰精瑙﹂崢鐔锋礈
									payResult.SetData(AttName.PAY_RESULT, "0");
									payResult.SetData(AttName.PAY_RESULT_DATA,
											"MzBuyInfo:"+info.toString()+"\n"+"errorMsg:"+errorMsg);									
									notify.Pay(payResult.DataToString());

									break;
								}
							}
						});				

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

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

    public static String getBillNo(final String playerid, final String price, final String cporder, final String subject, final String appid, 
   		 final String url,final String amount,final String paytype,
   		 final String productbody,final String productid,final String perprice,final String unit
   		 ,final String userinfo){
       String sign = MD5Util.md5(playerid+"|"+price+"|"+cporder+"|"+subject+"|clientKey");

   	Map<String, Object> map = new HashMap<String, Object>();
   	map.put("app_id", appid);
   	map.put("buy_amount", amount);
   	map.put("cporder", cporder);
   	map.put("pay_type", paytype);
   	map.put("product_body", productbody);
   	map.put("product_id", productid);
   	map.put("product_per_price", perprice);
   	map.put("subject", subject);
   	map.put("product_unit", unit);
   	map.put("price", price);
   	map.put("playerid", playerid);
   	map.put("user_info", userinfo);        
       map.put("sign", sign);
       String result = "";
       try {
           TypeSDKLogger.e(map.toString());
           result = HttpUtil._post(url, map);
           TypeSDKLogger.e( "result:" + result);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return result;
   }
	
}
