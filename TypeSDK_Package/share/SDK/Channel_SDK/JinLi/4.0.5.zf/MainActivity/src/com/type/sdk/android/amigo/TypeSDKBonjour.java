package com.type.sdk.android.amigo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gionee.gamesdk.AccountInfo;
import com.gionee.gamesdk.GamePayer;
import com.gionee.gamesdk.GamePlatform;
import com.gionee.gamesdk.GamePlatform.LoginListener;
import com.gionee.gamesdk.OrderInfo;
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

	private RequestQueue mQueue;
	public Context appContext;
	public Activity appActivity;
	// �?始支�?
    private GamePayer mGamePayer;
    // 支付结果回调
    private GamePayer.GamePayCallback mGamePayCallback;
	String unserId = "";
    
    private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}

	@Override
	public void initSDK(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("initSDK");
		appContext = _in_context;
		appActivity = (Activity) appContext;
		if (isInit) {
			TypeSDKNotify_amigo notify = new TypeSDKNotify_amigo();
			notify.Init();
			return;
		}
		mQueue = Volley.newRequestQueue(appContext);
		this.amigoInit();
		
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.i("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		this.amigoLogin();
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("ShowLogout");
		amigoLogout();
	}

	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("ShowPersonCenter");
	}

	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("HidePersonCenter");
	}

	@Override
	public void ShowToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("ShowToolBar");
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.amigoPay(_in_pay);
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
		TypeSDKLogger.v("LoginState");
		return 0;
	}

	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("ShowShare");
	}

	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("SetPlayerInfo");
		SendInfo(_in_context, _in_data);
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.v("SendInfo");
		try {
			
			userInfo.StringToData(_in_data);
			
			TypeSDKData.UserInfoData userData = new TypeSDKData.UserInfoData();
			userData.StringToData(_in_data);
			JSONObject userJsonExData = new JSONObject();
			userJsonExData.put("roleId", userData.GetData(AttName.ROLE_ID));
			userJsonExData.put("roleName", userData.GetData(AttName.ROLE_NAME));
			userJsonExData.put("roleLevel",userData.GetData(AttName.ROLE_LEVEL));
			userJsonExData.put("zoneId", userData.GetInt(AttName.SERVER_ID));
			userJsonExData.put("zoneName", userData.GetData(AttName.SERVER_NAME));
			TypeSDKLogger.d("上传用户信息:string=" + userJsonExData);
			this.userInfo.CopyAttByData(userData);
			// this.ucSdkSendInfo(userJsonExData);
		} catch (Exception e) {
			TypeSDKLogger.d("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.d("执行ExitGame方法");
		System.exit(0);
	}
	
	public void onCreate(Context _in_context){
		TypeSDKLogger.d("onCreate");
		this.initSDK(_in_context, "");
	}

	public void onResume() {
		TypeSDKLogger.d("onResume");
		if (mGamePayer != null) {
			mGamePayer.onResume();
		}
	}

	public void onPause() {
		TypeSDKLogger.d("onPause");
	}

	public void onStop() {
		TypeSDKLogger.d("onStop");
	}
	
	public void onDestroy() {
		TypeSDKLogger.d("onDestroy");
		if (mGamePayer != null) {
			mGamePayer.onDestroy();
		}
	}
	
	private void amigoInit() {

		TypeSDKLogger.i("init begin");

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				try {
//					TypeSDKLogger.e("APP_ID:" + platform.GetData(AttName.APP_ID));
					TypeSDKLogger.d("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					GamePlatform.init(appContext, platform.GetData(AttName.APP_KEY));
					mGamePayer = new GamePayer(appActivity);
			        isInit = true;
			        TypeSDKNotify_amigo notify = new TypeSDKNotify_amigo();
			        notify.Init();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.v("init done");

//		setSuspendWindowChangeAccountListener();
	}

	private void amigoLogin() {
		
		TypeSDKLogger.d("login begin");

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				try {
					GamePlatform.loginAccount(appActivity, true, new LoginListener() {
						
						@Override
						public void onSuccess(AccountInfo arg0) {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("login_success");
							TypeSDKNotify_amigo notify = new TypeSDKNotify_amigo();
							unserId = arg0.mUserId;
							notify.sendToken(arg0.mToken, arg0.mPlayerId);
						}
						
						@Override
						public void onError(Exception arg0) {
							// TODO Auto-generated method stub
							TypeSDKLogger.e("login error:" + arg0.toString());
						}
						
						@Override
						public void onCancel() {
							// TODO Auto-generated method stub
							TypeSDKLogger.i("login cancel");
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.i("login done");
		
	}

	private void amigoLogout() {
		TypeSDKLogger.v("amigoLogout");
		TypeSDKNotify_amigo notify = new TypeSDKNotify_amigo();
		TypeSDKLogger.v("notify_logout");
		notify.Logout();
	}
	

	String productName;
	String orderNo;
	String realPrice;
	String jsonResult;
	String billNo;
	String beforeSignString;
	String sign;
	String submitTime;
	
	private void amigoPay(PayInfoData _in_pay) {
		TypeSDKLogger.v("ITEM_NAME:" + _in_pay.GetData(AttName.ITEM_NAME));
		TypeSDKLogger.v("EXTRA:" + _in_pay.GetData(AttName.EXTRA));
		TypeSDKLogger.v("BILL_NUMBER:" + _in_pay.GetData(AttName.BILL_NUMBER));
		TypeSDKLogger.v("USER_ID:" + userInfo.GetData(AttName.USER_ID));
		productName = _in_pay.GetData(AttName.ITEM_NAME);//商品名称
		
	    if (TypeSDKTool.isPayDebug) {
			realPrice = "0.01";
		} else {
			realPrice = _in_pay.GetInt(AttName.REAL_PRICE)*0.01 + "";
		}
		
		orderNo = _in_pay.GetData(AttName.BILL_NUMBER)+ "|" + unserId;
		beforeSignString = unserId + "|" + realPrice + "|" + orderNo + "|" + productName + "|clientKey";
		sign = MD5Util.md5(beforeSignString);
		mGamePayCallback = mGamePayer.new GamePayCallback() {
			PayResultData payResult = new PayResultData();
			TypeSDKNotify_amigo notify = new TypeSDKNotify_amigo();
            //支付成功
            @Override
            public void onPaySuccess() {
                // 可以在这里处理自己的业务
            	TypeSDKLogger.v("PAY_SUCCESS");
            	payResult.SetData(AttName.PAY_RESULT, "1");
				payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON, "SUCCESS");
				notify.Pay(payResult.DataToString());
            }
            
            //支付取消
            @Override
            public void onPayCancel() {
				//支付取消时会同时给出支付失败的回调，故此回调不做处理
    //             // 可以在这里处理自己的业务
    //         	payResult.SetData(AttName.PAY_RESULT, "0");
				// payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_CANCEL");
				// notify.Pay(payResult.DataToString());
            }

            //支付失败，stateCode为支付失败状态码，详见接入指�?
            @Override
            public void onPayFail(String stateCode) {
                // 可以在这里处理自己的业务
            	payResult.SetData(AttName.PAY_RESULT, "0");
				payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
				notify.Pay(payResult.DataToString());
            }
            
        };
//        new DialogHelper().execute(1);
        Map<String, String> map = new HashMap<String, String>();
        map.put("playerid", unserId);
        map.put("price", realPrice);
        map.put("cporder", orderNo);
        map.put("subject", productName);
        map.put("sign", sign.toLowerCase());
        		      
        JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Request.Method.POST, 
        		TypeSDKBonjour.Instance().platform.GetData("url"), 
        		new JSONObject(map), 
        		new Listener<JSONObject>() {
	            	@Override
	            	public void onResponse(JSONObject jsonObject) {
	            		// TODO Auto-generated method stub
	            		TypeSDKLogger.d("onResponse:" + jsonObject.toString());
	            		try {
	        				billNo = jsonObject.getString("order");
	        				submitTime = jsonObject.getString("submit_time");
//	        				JSONObject data = jsonObject.getJSONObject("data");
	        				//创建订单信息
	        		        final OrderInfo orderInfo = new OrderInfo();
	        		        //�?发�?�后台申请的Apikey
	        		        orderInfo.setApiKey(platform.GetData(AttName.APP_KEY)); 
	        		        //商户订单号，与创建支付订单中�?"out_order_no"值相�?
	        		        orderInfo.setOutOrderNo(orderNo);  // + "|" + userInfo.GetData(AttName.USER_ID)
	        		        //支付订单提交时间，与创建支付订单中的"submit_time"值相�?
	        		        orderInfo.setSubmitTime(submitTime);
//	        		        TypeSDKLogger.v("SubmitTime:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
	        		        TypeSDKLogger.d("jsonResult:" + jsonResult);
	        		        TypeSDKLogger.d("billNo:" + billNo);
	        		        TypeSDKLogger.d("sign:" + sign);
	        		        TypeSDKLogger.d("out_order_no:" + orderNo);
	        		      //调用启动收银台接�? 
	        		        new Handler(Looper.getMainLooper()).post(new Runnable() {
	        					@Override
	        					public void run() {
	        						try {
	        							mGamePayer.pay(orderInfo, mGamePayCallback);
	        						} catch (Exception e) {
	        							// TODO Auto-generated catch block
	        							e.printStackTrace();
	        						}

	        					}
	        				});
	        		        
	        			} catch (JSONException e1) {
	        				// TODO Auto-generated catch block
	        				e1.printStackTrace();
	        			}
	            	}
        		}, 
        		new Response.ErrorListener() {
        			@Override
        			public void onErrorResponse(VolleyError volleyError) {
        				// TODO Auto-generated method stub
        				TypeSDKLogger.e("onErrorResponse:" + volleyError.getMessage());
        			}
        		});
        
        mQueue.add(jsonObjectRequest);
		
	}
	
	private class DialogHelper extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			switch (params[0]) {
			case 1:
				try {
					jsonResult = getBillNo(userInfo.GetData(AttName.USER_ID), 
							realPrice, 
							orderNo, 
							productName, 
							sign.toLowerCase(), 
							platform.GetData("url"));
				} catch (AppException e) {
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
				billNo = jsonObject.getString("order");
				submitTime = jsonObject.getString("submit_time");
//				JSONObject data = jsonObject.getJSONObject("data");
				//创建订单信息
		        OrderInfo orderInfo = new OrderInfo();
		        //�?发�?�后台申请的Apikey
		        orderInfo.setApiKey(platform.GetData(AttName.APP_KEY)); 
		        //商户订单号，与创建支付订单中�?"out_order_no"值相�?
		        orderInfo.setOutOrderNo(orderNo);  // + "|" + userInfo.GetData(AttName.USER_ID)
		        //支付订单提交时间，与创建支付订单中的"submit_time"值相�?
		        orderInfo.setSubmitTime(submitTime);
//		        TypeSDKLogger.v("SubmitTime:" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		        TypeSDKLogger.d("jsonResult:" + jsonResult);
		        TypeSDKLogger.d("billNo:" + billNo);
		        TypeSDKLogger.d("sign:" + sign);
		        TypeSDKLogger.d("out_order_no:" + orderNo);
		      //调用启动收银台接�? 
//		        new Handler(Looper.getMainLooper()).post(new Runnable() {
//					@Override
//					public void run() {
						try {
							mGamePayer.pay(orderInfo, mGamePayCallback);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

//					}
//				});
		        
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	        
		}
		
	}
	
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}
	
    private static String getBillNo(final String playerid, final String price, final String cporder, final String subject, final String sign, final String url)
            throws AppException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("playerid", playerid);
    	map.put("price", price);
        map.put("cporder", cporder);
        map.put("subject", subject);
        map.put("sign", sign);
        String result = "";
        try {
            TypeSDKLogger.d(map.toString());
            result = HttpUtil._post(url, map);
        } catch (Exception e) {
            if (e instanceof AppException)
                throw (AppException) e;
            throw AppException.network(e);
        }
        return result;
    }

}
