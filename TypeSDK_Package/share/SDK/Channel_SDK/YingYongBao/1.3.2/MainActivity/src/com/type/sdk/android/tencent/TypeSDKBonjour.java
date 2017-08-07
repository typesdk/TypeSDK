package com.type.sdk.android.tencent;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.utils.*;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKDefine.ReceiveFunction;
import com.type.sdk.android.TypeSDKEventManager;
import com.type.sdk.android.TypeSDKLogger;

import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.eFlag;
import com.tencent.ysdk.framework.common.ePlatform;
import com.tencent.ysdk.module.pay.PayListener;
import com.tencent.ysdk.module.pay.PayRet;
import com.tencent.ysdk.module.user.UserLoginRet;


public class TypeSDKBonjour extends TypeSDKBaseBonjour {

	public Context appContext;
	public Activity appActivity;
	public static int logintype = 0;//1锟斤拷qq锟斤拷录锟斤拷2锟斤拷微锟脚碉拷录
	public String zoneId = "1";
	public static boolean isLoginFinish = true;
	public static boolean isEnterLogin = false;
	public static int curLoginType = 0;
	public static boolean isCallLogin = false;
	
	private String playerid;
	private String realprice;
	private String cporder;
	private String subject;
	private String zoneid;
	private String amount;
	private String serverid;
	private String saveValue;
	private byte[] appResData;
	private String ysdkExt;
	private Handler handler = new Handler(Looper.getMainLooper());
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}
	@Override
	public void initSDK(Context _in_context, String _in_data) {
		TypeSDKLogger.e("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (true) {
			TypeSDKLogger.e("sdk is already init");
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_INITFINISH, platform.DataToString());
			TypeSDKEventManager.Instance().SendUintyEvent(
					ReceiveFunction.MSG_UPDATEFINISH, platform.DataToString());
			return;
		}

	}

	private void tencentInit() {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("init begin");
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");
	}	
	@Override
	public void ShowLogin(final Context _in_context, String _in_data) {
		super.ShowLogin(_in_context, _in_data);
		isCallLogin = true;
		
		if(YSDKCallback.accessToken.isEmpty()){
			/*Intent intent = new Intent();
			intent.setClass(_in_context, YSDKLoginActivity.class);
			_in_context.startActivity(intent);*/
			
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						LoginDialog dialog = new LoginDialog(_in_context);
						dialog.show();
					} catch (Exception e) {
						TypeSDKLogger.e(e.toString());
					}
				}
			});
			
			
		}else{
			TypeSDKNotify_Tencent notify = new TypeSDKNotify_Tencent();
			notify.sendToken(YSDKCallback.openId, YSDKCallback.accessToken);
		}		
	}

	public void tencentLogin(final int type) {
		// TODO Auto-generated method stub
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					ePlatform platform = getPlatform();
					TypeSDKLogger.e("platform:"+platform.toString());
					if (type == 1) {
						if (platform == ePlatform.QQ) {

							TypeSDKLogger.e("WGLoginWithLocalInfo()ePlatform_QQ");
							YSDKApi.login(ePlatform.QQ);
							
						} else{
							TypeSDKLogger.e("login qq start");
							YSDKApi.login(ePlatform.QQ);
						} 
					} else if (type == 2) {
						if (platform== ePlatform.WX) {	
							
							TypeSDKLogger.e("WGLoginWithLocalInfo()ePlatform_Weixin");
							YSDKApi.login(ePlatform.WX);
						} else {
							TypeSDKLogger.e("login wechat start");
							YSDKApi.login(ePlatform.WX);
						}
					}else{
						TypeSDKLogger.e("unknown login type");
					}

				} catch (Exception e) {
					TypeSDKLogger.e("登录异常");
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// 执锟斤拷 锟角筹拷锟斤拷锟斤拷
		ShowLog("do sdk logout");
		YSDKApi.logout();
		YSDKCallback.accessToken = "";
		TypeSDKNotify_Tencent notify = new TypeSDKNotify_Tencent();
		notify.Logout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// 锟斤拷示锟矫伙拷锟斤拷锟斤拷
		ShowLog("do sdk show person center");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// 锟斤拷锟斤拷锟矫伙拷锟斤拷锟斤拷
		ShowLog("do sdk hide person center");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// 锟斤拷示锟斤拷锟斤拷锟斤拷锟斤拷
		ShowLog("do sdk show tool bar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// 锟斤拷锟截癸拷锟斤拷锟斤拷锟斤拷
		ShowLog("do sdk hide tool bar");
	}

	@Override
	public String PayItem(Context _in_context, String _in_data) {
		
		TypeSDKData.PayInfoData _in_pay = new TypeSDKData.PayInfoData();
		_in_pay.StringToData(_in_data);
		this.tencentPay(_in_pay);		
		return _in_data;
	}

	private void tencentPay(final PayInfoData _in_pay) {
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub
		TypeSDKLogger.e("pay begin:" + _in_pay.DataToString());
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					TypeSDKLogger.i("userid:"
							+ userInfo.GetData(AttName.USER_ID));
					int price = 0;
					try {
						price = Integer.parseInt(_in_pay
								.GetData(AttName.REAL_PRICE));
					} catch (Exception e) {
						TypeSDKLogger.e("Invalid price:" + e.toString());
					}					
					saveValue = String.valueOf(price*0.1);
					Bitmap bmp = BitmapFactory.decodeResource(
							appContext.getResources(),
							GetResId.getId(appContext, "drawable", "sample_yuanbao"));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
					appResData = baos.toByteArray();
					ysdkExt = _in_pay.GetData(AttName.BILL_NUMBER);
					playerid = _in_pay.GetData(AttName.USER_ID);
					realprice = String.valueOf((float)(((float)price)/100));
					TypeSDKLogger.i("realprice:"+realprice);
					cporder = ysdkExt;
					subject = _in_pay.GetData(AttName.ITEM_NAME);
					serverid = _in_pay.GetData(AttName.SERVER_ID);
					amount = saveValue;
					new DialogHelper().execute(2);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		TypeSDKLogger.e("pay done");		
	}
	@Override
	public String ExchangeItem(Context _in_context, String _in_data) {
		// 锟斤拷锟矫癸拷锟斤拷指锟斤拷锟斤拷品协锟斤拷
		ShowLog("do sdk ExchangeItem");
		PayItem(_in_context, _in_data);
		return null;
	}

	@Override
	public int LoginState(Context _in_context) {
		// 锟斤拷锟截碉拷陆状态
		ShowLog("get login state ");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// 锟斤拷锟矫凤拷锟斤拷涌锟�
		ShowLog("do sdk show share data:" + _in_data);

	}
	@Override
	public void ExitGame(Context _in_context) {
		// 锟剿筹拷锟斤拷戏
		ShowLog("do sdk exit game");
		((Activity) (_in_context)).finish();

	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// 锟较达拷
		ShowLog("do sdk SendInfo data:" + _in_data);
		SetPlayerInfo(_in_context, _in_data);
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// 锟较达拷锟斤拷锟斤拷锟斤拷锟�
		ShowLog("do sdk SetPlayerInfo data:" + _in_data);
		userInfo.StringToData(_in_data);
	}

	public void OnDestroy(){
		
	}
	private void ShowLog(String _info) {
		TypeSDKLogger.i( _info);
	}

	public ePlatform getPlatform() {
        UserLoginRet ret = new UserLoginRet();
        YSDKApi.getLoginRecord(ret);
        if (ret.flag == eFlag.Succ) {
            return ePlatform.getEnum(ret.platform);
        }
        return ePlatform.None;
    }
	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return null;
	}
	String zoneIdResult,payResult,saveOrderResult,sign,channel;
	Map<String, Object> map;
	private class DialogHelper extends AsyncTask<Integer, Void, Integer> {
		

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			switch (params[0]) {
			case 1:
				try {
					sign = MD5Util.md5(playerid + "|" + realprice + "|"
							+ cporder + "|" + subject + "|clientKey");
					map = new HashMap<String, Object>();
					map.put("playerid", playerid);
					map.put("price", realprice);
					map.put("cporder", cporder);
					map.put("subject", subject);
					map.put("sign", sign);
					TypeSDKLogger.i("openid:" + YSDKCallback.openId
							+ "openkey:" + YSDKCallback.accessToken + "pf:"
							+ YSDKCallback.pf + "pfkey:" + YSDKCallback.pfKey
							+ "payToken:" + YSDKCallback.payToken);
					map.put("openid", YSDKCallback.openId);
					map.put("openkey", YSDKCallback.accessToken);
					map.put("pf", YSDKCallback.pf);
					map.put("pfkey", YSDKCallback.pfKey);
					map.put("payToken", YSDKCallback.payToken);
					map.put("zoneid", zoneid);
					map.put("amt", amount);
					map.put("billno", cporder);
					channel = "";
					if (logintype == 1) {
						channel = "QQ";
					} else {
						if (logintype == 2) {
							channel = "WX";
						}
					}
					map.put("paytype", channel);
					String postUrl = platform.GetData("url");
					TypeSDKLogger.i("postUrl:" + postUrl);
					payResult = HttpUtil._post(postUrl, map);
					return 1;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("serverid", serverid);
					String postUrl = platform.GetData("zoneid_url");
					zoneIdResult = HttpUtil._post(postUrl, map);
					return 2;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				//锟酵伙拷锟剿讹拷锟斤拷锟斤拷锟斤拷时锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟较拷锟斤拷锟絊DK锟斤拷锟斤拷锟�
				try{
					if (logintype == 1) {
						channel = "QQ";
					} else {
						if (logintype == 2) {
							channel = "WX";
						}
					}
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("cporder", cporder);
					map.put("openid", YSDKCallback.openId);
					map.put("openkey", YSDKCallback.accessToken);
					map.put("pf", YSDKCallback.pf);
					map.put("pfkey", YSDKCallback.pfKey);
					map.put("payToken", YSDKCallback.payToken);
					map.put("price", realprice);
					map.put("amt", amount);
					map.put("subject", subject);
					map.put("zoneid", zoneid);
					map.put("paytype", channel);
					String beforeSignString = "amt=" + amount + "&cporder=" + cporder
						+ "&openid=" + YSDKCallback.openId + "&openkey=" + YSDKCallback.accessToken
						+ "&payToken=" + YSDKCallback.payToken + "&paytype=" + channel + "&pf=" + YSDKCallback.pf
						+ "&pfkey=" + YSDKCallback.pfKey + "&price=" + realprice + "&zoneid=" + zoneid
						+ "clientKey";
					TypeSDKLogger.e("pay beforeSignString:" + beforeSignString);
					String sign = md5(beforeSignString);
					TypeSDKLogger.e("sign:" + sign);
					map.put("sign", sign);
//					saveOrderResult = HttpUtil._post(platform.GetData("ClientPayUrl"), map, null, true);
					TypeSDKLogger.i("ClientPayUrl reuslt:" + saveOrderResult);
					return 3;
				}catch(Exception e){
					
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
			TypeSDKLogger.i("onPostExecute result:"+result);
			switch (result) {
			case 1:				
				try {
					TypeSDKLogger.i("createChannelOrderResult:" + payResult);
					JSONObject orderJson = new JSONObject();
					if(!isEmptyOrNull(payResult)){
						orderJson = new JSONObject(payResult);
					}
					
					if(isEmptyOrNull(payResult)||!orderJson.getString("code").equals("0")){
						//锟斤拷锟斤拷通锟斤拷状态锟诫不锟斤拷锟斤拷锟斤拷锟斤拷锟芥订锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟截凤拷锟斤拷锟斤拷(只锟斤拷锟斤拷锟斤拷锟斤拷锟�,锟斤拷锟斤拷锟截凤拷锟斤拷锟狡凤拷锟节成癸拷锟斤拷录锟斤拷)
						boolean bl = true;
						int key = 0;
						JSONObject js = new JSONObject(map);
						js.put("time", "1");
						SharedPreferencesUtil sh = new SharedPreferencesUtil(appContext, "payData" + YSDKCallback.openId);
						while(bl){
							if(sh.read("" + key) == null || sh.read("" + key).isEmpty()){
								bl = false;
								sh.save("" + key, js.toString());
								TypeSDKLogger.w("save error post key:" + key + ":" + js.toString() + "openId:" + map.get("openid"));
							}
							if(bl){
								key++;
							}
							
						}
						start(js, "" + key);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				break;
			case 2:
				try {
					TypeSDKLogger.i("getServerMapResult:"+zoneIdResult);
					JSONObject retJson = new JSONObject(zoneIdResult);
//					JSONObject idJson = new JSONObject(
//							retJson.getString("data"));
//					zoneid = idJson.getString("zoneId");
					
					zoneid = "1";
					
					new DialogHelper().execute(3);
					YSDKApi.recharge(zoneid, saveValue, false, appResData,
							ysdkExt, new PayListener() {
								@Override
								public void OnPayNotify(PayRet ret) {
									if (PayRet.RET_SUCC == ret.ret) {
										PayResultData payResult = new PayResultData();
										// 支锟斤拷锟斤拷锟教成癸拷
										switch (ret.payState) {
										// 支锟斤拷锟缴癸拷
										case PayRet.PAYSTATE_PAYSUCC:
											payResult.SetData(
													AttName.PAY_RESULT, "1");
											payResult.SetData(
													AttName.PAY_RESULT_DATA,
													"" + "锟矫伙拷支锟斤拷锟缴癸拷锟斤拷支锟斤拷锟斤拷锟�"
															+ ret.realSaveNum
															+ ";" + "使锟斤拷锟斤拷锟斤拷锟斤拷"
															+ ret.payChannel
															+ ";" + "锟斤拷锟斤拷状态锟斤拷"
															+ ret.provideState
															+ ";" + "业锟斤拷锟斤拷锟酵ｏ拷"
															+ ret.extendInfo
															+ ";锟斤拷锟斤拷锟窖拷锟筋："
															+ ret.toString());
											TypeSDKEventManager
													.Instance()
													.SendUintyEvent(
															TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT,
															payResult
																	.DataToString());
											new DialogHelper().execute(1);
											break;
										// 取锟斤拷支锟斤拷
										case PayRet.PAYSTATE_PAYCANCEL:
											payResult.SetData(
													AttName.PAY_RESULT, "2");
											payResult.SetData(
													AttName.PAY_RESULT_DATA,
													"锟矫伙拷取锟斤拷支锟斤拷锟斤拷" + ret.toString());
											TypeSDKEventManager
													.Instance()
													.SendUintyEvent(
															TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT,
															payResult
																	.DataToString());
											break;
										// 支锟斤拷锟斤拷锟轿粗�
										case PayRet.PAYSTATE_PAYUNKOWN:
											payResult.SetData(
													AttName.PAY_RESULT, "0");
											payResult.SetData(
													AttName.PAY_RESULT_DATA,
													"锟矫伙拷支锟斤拷锟斤拷锟轿粗拷锟斤拷锟斤拷锟斤拷询锟斤拷睿�"
															+ ret.toString());
											TypeSDKEventManager
													.Instance()
													.SendUintyEvent(
															TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT,
															payResult
																	.DataToString());
											break;
										// 支锟斤拷失锟斤拷
										case PayRet.PAYSTATE_PAYERROR:
											payResult.SetData(
													AttName.PAY_RESULT, "0");
											payResult.SetData(
													AttName.PAY_RESULT_DATA,
													"支锟斤拷锟届常" + ret.toString());
											TypeSDKEventManager
													.Instance()
													.SendUintyEvent(
															TypeSDKDefine.ReceiveFunction.MSG_PAYRESULT,
															payResult
																	.DataToString());
											break;
										}
									} else {
										switch (ret.flag) {
										case eFlag.Login_TokenInvalid:
											// 锟斤拷陆态锟斤拷锟斤拷
											TypeSDKLogger.e("锟斤拷陆态锟斤拷锟节ｏ拷锟斤拷锟斤拷锟铰碉拷陆锟斤拷"
													+ ret.toString());
											ShowLogout(appContext);
											break;
										case eFlag.Pay_User_Cancle:
											// 锟矫伙拷取锟斤拷支锟斤拷
											TypeSDKLogger.e("锟矫伙拷取锟斤拷支锟斤拷锟斤拷"
													+ ret.toString());
											break;
										case eFlag.Pay_Param_Error:
											TypeSDKLogger.e("支锟斤拷失锟杰ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷"
													+ ret.toString());
											break;
										case eFlag.Error:
										default:
											TypeSDKLogger.e("支锟斤拷锟届常"
													+ ret.toString());
											break;
										}
									}
								}
							});
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case 3:
				TypeSDKLogger.i("saveOrderResult:" + saveOrderResult);
				break;

			}

		}

	}
	//锟斤拷锟斤拷址锟斤拷锟斤拷欠锟轿拷栈锟斤拷锟絥ull
    public boolean isEmptyOrNull(String checkStr) {
        if (checkStr == null) {
            return true;
        } else if (checkStr.isEmpty()) {
            return true;
        }
        return false;
    }
	
	public static String md5(String string) {

	    byte[] hash;

	    try {

	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

	    } catch (NoSuchAlgorithmException e) {

	        throw new RuntimeException("Huh, MD5 should be supported?", e);

	    } catch (UnsupportedEncodingException e) {

	        throw new RuntimeException("Huh, UTF-8 should be supported?", e);

	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);

	    for (byte b : hash) {

	        if ((b & 0xFF) < 0x10) hex.append("0");

	        hex.append(Integer.toHexString(b & 0xFF));

	    }

	    return hex.toString();

	}
	
	public void start(final JSONObject data, final String key) {
		try {
			JSONObject js = data;
			final int period = Integer.parseInt("" + js.get("time"));
			switch (period) {
			case 0:
				js.put("time", "1");
			case 1:
				js.put("time", "2");
				break;
			case 2:
				js.put("time", "4");
				break;
			case 4:
				js.put("time", "8");
				break;
			case 8:
				js.put("time", "16");
				break;
			case 16:
				js.put("time", "32");
				break;
			case 32:
				js.put("time", "-1");
				break;
			default:
				break;
			}
			final JSONObject jsA = js;
			TypeSDKLogger.i("period:" + period);
			TypeSDKLogger.i("jsA:" + jsA.toString());
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if (period != -1) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("playerid", jsA.get("playerid"));
							map.put("price", jsA.get("price"));
							map.put("cporder", jsA.get("cporder"));
							map.put("subject", jsA.get("subject"));
							map.put("sign", jsA.get("sign"));
							map.put("openid", jsA.get("openid"));
							map.put("openkey", jsA.get("openkey"));
							map.put("pf", jsA.get("pf"));
							map.put("pfkey", jsA.get("pfkey"));
							map.put("payToken", jsA.get("payToken"));
							map.put("zoneid", jsA.get("zoneid"));
							map.put("amt", jsA.get("amt"));
							map.put("billno", jsA.get("billno"));
							map.put("paytype", jsA.get("paytype"));
							String postUrl = TypeSDKBonjour
									.Instance().platform.GetData("url");
							TypeSDKLogger.i("postUrl:" + postUrl);
							String payResult = HttpUtil._post(postUrl, map);
							TypeSDKLogger.i("payResult:" + payResult);
							JSONObject orderJson = new JSONObject();
							if(!TypeSDKBonjour.Instance()
									.isEmptyOrNull(payResult)){
								orderJson = new JSONObject(payResult);		
							}
							
							if (!TypeSDKBonjour.Instance()
									.isEmptyOrNull(payResult)
									&& orderJson.getString("code").equals("0")) {
								TypeSDKLogger.i("ag success openId:" + YSDKCallback.openId + "key:" + key);
								SharedPreferencesUtil sh = new SharedPreferencesUtil(
										appContext, "payData" + YSDKCallback.openId);
								sh.remove(key);
								cancel();
							} else {
								TypeSDKLogger.w("ag error:" + jsA.toString());
								start(jsA, key);
								cancel();
							}

						} else {
							cancel();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, (long) period * (60 * 1000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
