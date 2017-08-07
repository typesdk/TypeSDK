package com.type.sdk.android.coolpad;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.coolcloud.uac.android.api.Coolcloud;
import com.coolcloud.uac.android.api.ErrInfo;
import com.coolcloud.uac.android.api.OnResultListener;
import com.coolcloud.uac.android.common.Constants;
import com.coolcloud.uac.android.common.Params;
import com.coolcloud.uac.android.gameassistplug.GameAssistApi;
import com.coolcloud.uac.android.gameassistplug.GameAssistApi.SwitchingAccount;
import com.coolcloud.uac.android.gameassistplug.GameAssistConfig;
import android.content.pm.ActivityInfo;
import com.yulong.paysdk.beens.CoolPayResult;
import com.yulong.paysdk.beens.CoolYunAccessInfo;
import com.yulong.paysdk.coolpayapi.CoolpayApi;
import com.yulong.paysdk.payinterface.IPayResult;
import com.yulong.paysdk.beens.PayInfo;
import com.dataeye.DCAccount;
import com.dataeye.DCAgent;
import com.dataeye.DCReportMode;
import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKData.PayInfoData;
import com.type.sdk.android.TypeSDKData.PayResultData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;
import com.type.utils.HttpUtil;

public class TypeSDKBonjour extends TypeSDKBaseBonjour {
	
	public Context appContext;
	public Activity appActivity;
	
	private Coolcloud coolcloud = null;
	private GameAssistApi mGameAssistApi;
	private GameAssistConfig mGameAssistConfig;
	
	private CoolpayApi api;
	private CoolYunAccessInfo accessInfo;
	
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}
	
	public static TypeSDKBonjour Instance(){
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
			TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
			notify.Init();
			return;
		}
		
		this.coolpadInit();
	}

	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogin");
		super.ShowLogin(_in_context, _in_data);
		if(coolcloud != null){
			this.coolpadLogin();
		} else {
			TypeSDKLogger.e("coolcloud is null");
		}
		
	}

	@Override
	public void ShowLogout(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("ShowLogout");
		this.coolpadLogout();
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
	}

	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
		TypeSDKLogger.e("HideToolBar");
	}

	public String PayItemByData(Context _in_context,
			TypeSDKData.PayInfoData _in_pay) {
		TypeSDKLogger.e("pay begin");
		String _in_OrderID = _in_pay
				.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.coolpadPay(_in_pay);

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
		return "";//PayItem(_in_context, _in_data)
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
			TypeSDKLogger.e("userInfo:" + userInfo.DataToString());
			TypeSDKLogger.e("USER_TOKEN:" + userInfo.GetData(AttName.USER_TOKEN));
			TypeSDKLogger.e("USER_ID:" + userInfo.GetData(AttName.USER_ID));
			DCAccount.login(userInfo.GetData(AttName.USER_ID));
		} catch (Exception e) {
			TypeSDKLogger.e("上传用户信息:" + e.getMessage());
		}

	}

	@Override
	public void ExitGame(Context _in_context) {
		TypeSDKLogger.e("执行ExitGame方法");
			Handler mainHandler = new Handler(Looper.getMainLooper());
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					if(exitGameListenser()){
						DCAgent.onKillProcessOrExit();
						System.exit(0);
					}
				}
			});
	}

	public void onResume() {
		TypeSDKLogger.e("onResume");
		DCAgent.onResume(appContext);
		if (mGameAssistApi != null) {
            mGameAssistApi.onResume();
        }

	}
	

	public void onPause() {
		TypeSDKLogger.e("onPause");
		DCAgent.onPause(appContext);
		if (mGameAssistApi != null) {
			mGameAssistApi.onPause();
	    }


	}

	public void onStop() {
		TypeSDKLogger.e("onStop");
	}
	
	public void onDestroy() {
		TypeSDKLogger.e("onDestroy");
	}

	private void coolpadInit() {

		TypeSDKLogger.e("init begin");

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {

			@Override
			public void run() {
				try {
					TypeSDKLogger.e("initSDK_start");
					TypeSDKLogger.e("APP_KEY:" + platform.GetData(AttName.APP_KEY));
					TypeSDKLogger.e("sdkName:" + platform.GetData(AttName.SDK_NAME));
					
					DCAgent.setReportMode(DCReportMode.DC_AFTER_LOGIN);
					if(platform.GetData("mode").equals("debug")){
						DCAgent.setDebugMode(true);
					}
					
					coolcloud = Coolcloud.get(appContext, platform.GetData(AttName.APP_ID));
					mGameAssistConfig = new GameAssistConfig();
					mGameAssistConfig.setHideGift(true);
					if (mGameAssistApi == null) {
						
						if (coolcloud != null) {
							mGameAssistApi = (GameAssistApi) coolcloud.getGameAssistApi(
									appActivity, mGameAssistConfig);
							mGameAssistApi
									.addOnSwitchingAccountListen(new GameAssistApi.SwitchingAccount() {

								@Override
								public void onSwitchingAccounts() { // 重要
									// 切换账号
									coolcloud.logout(appActivity);
									TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
									notify.Logout();
								}
							});
						}
						
			        }
					
					api = CoolpayApi.createCoolpayApi(appActivity, platform.GetData(AttName.APP_ID));
			        isInit = true;
			        
			        TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
					notify.Init();
												
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		TypeSDKLogger.e("init done");

	}
	
	private void coolpadLogin() {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				Bundle input = new Bundle();
				/// 设置横屏显示
				input.putInt(Constants.KEY_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				// 设置申请的接口列表
				input.putString(Constants.KEY_SCOPE, "get_basic_userinfo");
				//获取类型为AuthCode
				input.putString(Constants.KEY_RESPONSE_TYPE, Constants.RESPONSE_TYPE_CODE);
//				input.putString(Constants.KEY_RESPONSE_TYPE, Constants.RESPONSE_TYPE_TOKEN);
				// 调用登录并授权接口，这里使用回调接口的方式
				coolcloud.login(appActivity, input, new Handler(Looper.getMainLooper()), new OnResultListener() {
				        @Override
				        public void onResult(Bundle result) {
				            // 返回成功，获取授权码
				            String code = result.getString(Params.RESPONSE_TYPE_CODE);
				            TypeSDKLogger.e("login_success_code:" + code);				         
				            DCAccount.logout();
				            TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
				            notify.sendToken(code);
				            
				        }

				        @Override
				        public void onError(ErrInfo error) {
				            // 出现错误，通过error.getError()和error.getMessage()获取错误信息
				        	TypeSDKLogger.e("login_error: " + error.toString());
				        }

				        @Override
				        public void onCancel() {
				            // 操作被取消
				        	TypeSDKLogger.e("login_cancel");
				        }
				});
			}
		});

	}

	private void coolpadLogout() {
		coolcloud.logout(appActivity);
		DCAccount.logout();
		TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
		notify.Logout();
	}
    
    @Override
    protected void SdkPay(Context _in_context, TypeSDKData.PayInfoData _in_pay)
    {
        coolpadPay(_in_pay);
    };
	private void coolpadPay(final PayInfoData _in_pay) {

		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {

				try {
					
					TypeSDKLogger.e("pay_start");
					TypeSDKLogger.e("_in_pay:" + _in_pay.DataToString());
					TypeSDKLogger.e("userInfoPAY_USER_TOKEN:" + userInfo.GetData(AttName.USER_TOKEN));
					TypeSDKLogger.e("userInfoPAY_USER_ID:" + userInfo.GetData(AttName.USER_ID));
					
					accessInfo = new CoolYunAccessInfo();
					accessInfo.setAccessToken(userInfo.GetData(AttName.USER_TOKEN));
					accessInfo.setOpenId(userInfo.GetData(AttName.USER_ID));
					
					int price;
					if(TypeSDKTool.isPayDebug){
						price = 1;
					} else {
						price = _in_pay.GetInt(AttName.REAL_PRICE);
					}
					if(itemListData == null){
						TypeSDKLogger.e("itemListData is null");
					}
					String item_id = itemListData.GetData(_in_pay.GetData(AttName.ITEM_SERVER_ID));
					int item_server_id = 0; //_in_pay.GetInt(AttName.ITEM_SERVER_ID);
					if(item_id != null && !item_id.isEmpty()){
						item_server_id = Integer.parseInt(item_id);
						TypeSDKLogger.i("item_server_id:" + item_server_id);
					} else {
						TypeSDKLogger.e("item_id is null replace with 1");
                        item_server_id = 1;
					}
					
					PayInfo payInfo = new PayInfo();
					payInfo.setAppId(platform.GetData(AttName.APP_ID));
					payInfo.setPayKey(platform.GetData(AttName.SECRET_KEY));
					// 设置CP透传信息，如果没有可以不设置
					payInfo.setCpPrivate(_in_pay.GetData(AttName.USER_ID));
					// 商品名称
					payInfo.setName(_in_pay.GetData(AttName.ITEM_NAME));
					// 支付价格,单位为分
					payInfo.setPrice(price);
					// 设置商品编号
					payInfo.setPoint(item_server_id);
					// 商品数量，目前不支持多数量支付，设置为定值1
					payInfo.setQuantity(1);
					// 如果没有订单号（不可重复），可不设置
					payInfo.setCpOrder(_in_pay.GetData(AttName.BILL_NUMBER));
					/*
					 * 如果不使用酷云账号，accessInfo 设置为null即可
					 */
					api.startPay(appActivity, payInfo, accessInfo, payResult,
								CoolpayApi.PAY_STYLE_ACTIVITY, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					/* CoolPadPay.startPay(appActivity, genUrl, accountBean, new IPayResultCallback() {
								
								@Override
								public void onPayResult(int resultCode, String signvalue, String resultInfo) {
									// TODO Auto-generated method stub
									switch (resultCode) {
									case CoolPadPay.PAY_SUCCESS:
										dealPaySuccess(signvalue);
										break;
									default:
										dealPayError(resultCode, resultInfo);
										break;
									}
									TypeSDKLogger.d("requestCode:" + resultCode + ",signvalue:" + signvalue + ",resultInfo:" + resultInfo);
								}
							}); */
										
				} catch (NumberFormatException exception) {
					TypeSDKLogger.e("Price input parse error: " + exception.toString());
				}
			}
		});
	}

	// 支付结果回调示例
	private IPayResult payResult = new IPayResult() {
		@Override
		public void onResult(CoolPayResult result) {
			if (null != result) {
				String resultStr = result.getResult();
				int resultStatus = result.getResultStatus();
				TypeSDKLogger.d("resultStr:" + resultStr);
				TypeSDKLogger.d("ResultStatus:" + result.getResultStatus());
				PayResultData payResult = new PayResultData();
				TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
				if(resultStatus == 0){
					TypeSDKLogger.e("pay_success");
					payResult.SetData(AttName.PAY_RESULT, "1");
					payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
							"SUCCESS");
					notify.Pay(payResult.DataToString());
				} else {
					TypeSDKLogger.e("pay success, sign error");
					TypeSDKLogger.e("pay_fail");
					payResult.SetData(AttName.PAY_RESULT, "0");
					payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
					notify.Pay(payResult.DataToString());
				}
			}
		}
	};	
	
	/*4.支付成功。
	 *  需要对应答返回的签名做验证，只有在签名验证通过的情况下，才是真正的支付成功
	 * 
	 * */
	/* private void dealPaySuccess(String signValue) {
		TypeSDKLogger.i("sign = " + signValue);
		if (TextUtils.isEmpty(signValue)) { */
			/**
			 *  没有签名值
			 */
			/* TypeSDKLogger.e("pay success,but it's signValue is null");
			TypeSDKLogger.e("pay success, but sign value is null");
			return;
		}

		boolean isvalid = false;
		try {
			isvalid = signCpPaySuccessInfo(signValue);
		} catch (Exception e) {
			isvalid = false;
		}
		
		PayResultData payResult = new PayResultData();
		TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
		
		if (isvalid) {
			TypeSDKLogger.e("pay_success");
			payResult.SetData(AttName.PAY_RESULT, "1");
			payResult.SetData(TypeSDKDefine.AttName.PAY_RESULT_REASON,
					"SUCCESS");
			notify.Pay(payResult.DataToString());
		} else {
			TypeSDKLogger.e("pay success, sign error");
			TypeSDKLogger.e("pay_fail");
			payResult.SetData(AttName.PAY_RESULT, "0");
			payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
			notify.Pay(payResult.DataToString());
		}

	} */

	/**
	 * valid cp callback sign
	 * @param signValue
	 * @return
	 * @throws Exception
	 * 
	 * transdata={"cporderid":"1","transid":"2","appid":"3","waresid":31,
	 * "feetype":4,"money":5,"count":6,"result":0,"transtype":0,
	 * "transtime":"2012-12-12 12:11:10","cpprivate":"7",
	 * "paytype":1}&sign=xxxxxx&signtype=RSA
	 */
	/* private boolean signCpPaySuccessInfo(String signValue) throws Exception {
		int transdataLast = signValue.indexOf("&sign=");
		String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
		
		int signLast = signValue.indexOf("&signtype=");
		String sign = URLDecoder.decode(signValue.substring(transdataLast+"&sign=".length(),signLast));
		
		String signtype = signValue.substring(signLast+"&signtype=".length());
		
		boolean isSign = RSAHelper.verify(transdata, platform.GetData(AttName.PRODUCT_ID), sign);
		if (signtype.equals("RSA") && isSign) {
		
			return true;
		}else{
			TypeSDKLogger.e("wrong type ");
		}
		return false;
	} */

	/* private void dealPayError(int resultCode, String resultInfo) {
		TypeSDKLogger.e("failure pay, callback cp errorinfo : " + resultCode + "," + resultInfo);
		TypeSDKLogger.e("payfail:["+ "resultCode:" + resultCode + "," + (TextUtils.isEmpty(resultInfo) ? "unkown error" : resultInfo) + "]");
		PayResultData payResult = new PayResultData();
		TypeSDKNotify_coolpad notify = new TypeSDKNotify_coolpad();
		payResult.SetData(AttName.PAY_RESULT, "0");
		payResult.SetData(AttName.PAY_RESULT_REASON, "PAY_FAIL");
		notify.Pay(payResult.DataToString());
	} */
	
	
	
	/*客户端下单模式
	 * 
	 * 生成数据后需要对数据做签名，签名的算法是使用应用的私钥做RSA签名。
	 * 应用的私钥可以在商户自服务获取
	 *   
	 *   */
	/* private String genUrl( String appid, String appuserid, String cpprivateinfo, String appKey, int waresid, double price, String cporderid) {
		String json = "";

		JSONObject obj = new JSONObject();
		try {
			obj.put("appid", appid);
			obj.put("waresid", waresid);
			obj.put("cporderid", cporderid);
			obj.put("price", price);
			obj.put("appuserid", appuserid);

			 */
			/*CP私有信息，选填*/
			/* String cpprivateinfo0 = cpprivateinfo;
			if(!TextUtils.isEmpty(cpprivateinfo0)){
				obj.put("cpprivateinfo", cpprivateinfo0);
			}	 */
			
			/*支付成功的通知地址。选填。如果客户端不设置本参数，则使用服务端配置的地址。*/
//			String notifyurl0 = platform.GetData(AttName.PAY_CALL_BACK_URL);
//			if(!TextUtils.isEmpty(notifyurl0)){
//				obj.put("notifyurl", notifyurl0);
//			}			
			/* json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sign = "";
		try {
			String cppk = appKey;
			sign = RSAHelper.signForPKCS1(json, cppk);
			
		} catch (Exception e) {
		}
		
		return "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
	} */
	
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
	
	@Override
	public String getUserFriends(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		return"";
	}

	@Override
	public void ShowInvite(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
		
	}
}
