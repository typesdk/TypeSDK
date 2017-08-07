package com.type.sdk.android.pipa;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.type.sdk.android.TypeSDKBaseBonjour;
import com.type.sdk.android.TypeSDKData;
import com.type.sdk.android.TypeSDKDefine;
import com.type.sdk.android.TypeSDKDefine.AttName;
import com.pipaw.pipawpay.PipawExitListener;
import com.pipaw.pipawpay.PipawGameUrlListener;
import com.pipaw.pipawpay.PipawLoginListener;
import com.pipaw.pipawpay.PipawPayListener;
import com.pipaw.pipawpay.PipawPayRequest;
import com.pipaw.pipawpay.PipawSDK;
import com.unity3d.player.UnityPlayer;
import com.type.utils.*;

public class TypeSDKBonjour extends TypeSDKBaseBonjour
{

	private Context appContext;
	private Activity appActivity;
	private static class SingletonHandler {
		static final TypeSDKBonjour instance = new TypeSDKBonjour();
	}

	public static TypeSDKBonjour Instance(){
		return SingletonHandler.instance;
	}
	
	
	public void onResume(Context _in_context) {
		Log.e("Type","Call onResume");
		PipawSDK.getInstance().onResume(appActivity);
	}
	
	public void onPause(Context _in_context) {
		Log.e("Type","Call onPause");
		PipawSDK.getInstance().onPause(appActivity);
	}
	
	public void onDestroy(Context _in_context) {
		Log.e("Type","Call onDestroy");
		PipawSDK.getInstance().onDestroy(appActivity);
	}
	
	@Override
	public void initSDK(Context _in_context, String _in_data) {
		Log.e("Type","Call initSDK");
		if(isInit) 
		{
			Log.i("Type", "error init do again");
			return;
		}
		appContext = _in_context;
		appActivity = (Activity)appContext;
		isInit= true;
	}
	
	public void CallInitSDK() {
		Log.e("Type","Call CallInitSDK");
		this.pipaSdkInit();
	}
	
	@Override
	public void ShowLogin(Context _in_context, String _in_data) {
		Log.e("Type","Call ShowLogin");
		this.pipaSdkLogin();
	}
	
	@Override
	public void ShowLogout(Context _in_context) {
		Log.e("Type","Call ShowLogin");
	}
	
	@Override
	public void ShowPersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void HidePersonCenter(Context _in_context) {
		// TODO Auto-generated method stub
	}
	@Override
	public void ShowToolBar(Context _in_context) {
		//this.ucSdkShowFloatButton();
	}
	@Override
	public void HideToolBar(Context _in_context) {
		// TODO Auto-generated method stub
	}
	public String PayItemByData(Context _in_context, TypeSDKData.PayInfoData _in_pay)
	{
		Log.e("Type","Call PayItemByData");
		String _in_OrderID = _in_pay.GetData(TypeSDKDefine.AttName.BILL_NUMBER);
		this.pipaSdkPay(_in_pay);
		return _in_OrderID;
	}
	@Override
	public String PayItem(Context _in_context, String _in_data) {
		Log.e("Type","Call PayItem");
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
		return 0;
	}
	@Override
	public void ShowShare(Context _in_context, String _in_data) {
		// TODO Auto-generated method stub
	}
	@Override
	public void SetPlayerInfo(Context _in_context, String _in_data) {
		
	}
	@Override
	public void ExitGame(Context _in_context) 
	{
		Log.e("Type","Call ExitGame");
		this.pipaSdkExit();
	}
	

	private void pipaSdkInit() {
		
		appActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					String merchantId;
					String merchantAppId;
					String appId;
					merchantId=platform.GetData(AttName.SDK_CP_ID);
					merchantAppId=platform.GetData(AttName.PRODUCT_ID);
					appId=platform.GetData(AttName.APP_ID);
					
					Log.e("Type","pipa getGameUrl: merchantId="+merchantId+",merchantAppId="+merchantAppId+",appId="+appId);
					PipawSDK.getInstance().getGameUrl(appActivity, merchantId, merchantAppId,
							appId, new PipawGameUrlListener() {

								@Override
								public void callback(int resultCode, String url, String data) {
									if (resultCode == PipawSDK.GET_GAME_URL_SUCCESS) {
										/**
										 * 获取游戏包下载地址成功 url为游戏包下载地址
										 */
										//url="http://dl.6dajie.com/down/360MobileSafe_101200025.apk";
										
										if (url.startsWith("http")) {
											Log.e("Type","pipa getGameUrl Success1: msg=had update,url="+url);
											Toast.makeText(appActivity, "游戏需要更新,后台下载中.请完安装最新游戏.",Toast.LENGTH_LONG).show();
											Log.e("Type","pipa getGameUrl Success2: msg=had update,url="+url);
											DownloadManager downloadManager =  (DownloadManager)appContext.getSystemService(Context.DOWNLOAD_SERVICE);
											Request request = new Request(Uri.parse(url));
											request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
											request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
											request.setAllowedOverRoaming(false);
								            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "yl_hero.apk");
								            request.setVisibleInDownloadsUi(true);
								            request.setTitle("游戏更新安装包");
								            Long mDownloadId = downloadManager.enqueue(request);
								            Log.e("Type","pipa getGameUrl Success3: msg=had update,url="+url+",downloadId="+mDownloadId);
										}else{
											TypeSDKNotify_pipa notify = new TypeSDKNotify_pipa();
											notify.initFinish();
										}
										Log.e("Type","pipa getGameUrl Success4: url="+url+",data="+data);
									}else if (resultCode == PipawSDK.GET_GAME_URL_FAIL) {
										Log.e("Type","pipa getGameUrl Fail: msg=had update,url="+url);
										Toast.makeText(appActivity, "无法更新游戏,请重试",Toast.LENGTH_LONG).show();
									}
								}
							});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void pipaSdkLogin() {
//		final Activity runActivi ;
//		if(UnityPlayer.currentActivity!=null)
//			runActivi = UnityPlayer.currentActivity;
//		else
//			runActivi = appActivity;
		appActivity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					String merchantId;
					String merchantAppId;
					String appId;
					
					merchantId=platform.GetData(AttName.SDK_CP_ID);
					merchantAppId=platform.GetData(AttName.PRODUCT_ID);
					appId=platform.GetData(AttName.APP_ID);
					
					PipawSDK.getInstance().login(appActivity, merchantId, merchantAppId, appId,
							new PipawLoginListener() {

								@Override
								public void callback(int resultCode, String data) {
									if (resultCode == PipawSDK.LOGIN_EXIT) {
										/**
										 * 退出登录
										 */
										
										Log.e("Type","login exit: string="+data);
										pipaSdkLogin();

									} else if (resultCode == PipawSDK.LOGIN_SUCCESS) {
										/**
										 * 登录成功
										 */

										/**
										 * 返回包含username，sid，time的json对象。
										 * 游戏服务端可通过merchantId，merchantAppId
										 * ，appId，username，sid，time向 支付SDK服务端请求验证sid。
										 * 注：sid的有效时间为1小时，游戏服务端须在1小时内完成sid验证。
										 */
										Log.e("Type","login success: string="+data);
										try{
										String username;
										String sid;
										String time;
										JSONTokener jsonParser = new JSONTokener(data);
										JSONObject person = (JSONObject)jsonParser.nextValue();
										username=person.getString("username");
										sid=person.getString("sid");
										time=person.getString("time");
										
										TypeSDKNotify_pipa notify = new TypeSDKNotify_pipa();
										notify.sendToken(username, sid+"|sy|"+time);
										
										}catch (JSONException ex) {  
										    // 异常处理代码  
										}  
										
										
										PipawSDK.getInstance().showPipawSDKIcon(true);

									} else if (resultCode == PipawSDK.LOGIN_FAIL) {
										/**
										 * 登录失败
										 */
										Log.e("Type","login fail: string="+data);
										pipaSdkLogin();
									}
								}
							});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private void pipaSdkExit() {
		
		Log.e("Type","pipaSdkExit: run Exit");
		Activity runActivi = null;
		if(UnityPlayer.currentActivity!=null)
			runActivi = UnityPlayer.currentActivity;
		else
			runActivi = appActivity;
		
		runActivi.runOnUiThread(new Runnable() {
			public void run() {
				try {
					PipawSDK.getInstance().exitSDK(appActivity, new Handler(),
							new PipawExitListener() {

								@Override
								public void callback(int resultCode, String data) {
									if (resultCode == PipawSDK.EXIT_OK) {
										/**
										 * 退出游戏
										 */
										Log.e("Type","pipaSdkExit: Exit_OK");
										appActivity.finish();
										Log.e("Type","Activity finish");
										//System.exit(0);
										//Log.e("Type","System.exit");
									} else if (resultCode == PipawSDK.EXIT_CANCEL) {
										/**
										 * 继续游戏
										 */
										Log.e("Type","pipaSdkExit: Exit_CANCEL");
									}
								}
							});
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}

//    * payData.SetData(U3DSharkAttName.REAL_PRICE,inputStr);
//			payData.SetData(U3DSharkAttName.ITEM_NAME,"sk bi");
//			payData.SetData(U3DSharkAttName.ITEM_DESC,"desc");
//			payData.SetData(U3DSharkAttName.ITEM_COUNT,"1");
//			payData.SetData(U3DSharkAttName.ITEM_SEVER_ID,"id");
//			payData.SetData(U3DSharkAttName.SEVER_ID,"1");
//			payData.SetData(U3DSharkAttName.EXTRA,"extra" +
//					"
	private void pipaSdkPay(TypeSDKData.PayInfoData _in_pay) 
	{
		android.util.Log.i("Type","-------:receive pay data: "+ _in_pay.DataToString());
		
		String merchantId;
		String merchantAppId;
		String appId;
		String privateKey;
		String payerId;
		String exOrderNo;
		String subject;
		String price;
		String extraParam;
		
		merchantId=platform.GetData(AttName.SDK_CP_ID);
		merchantAppId=platform.GetData(AttName.PRODUCT_ID);
		appId=platform.GetData(AttName.APP_ID);
		privateKey=platform.GetData(AttName.APP_KEY);
		payerId=_in_pay.GetData(AttName.USER_ID);
		exOrderNo=_in_pay.GetData(AttName.BILL_NUMBER);
		subject=_in_pay.GetData(AttName.ITEM_NAME);
		int intPrice = _in_pay.GetInt(AttName.REAL_PRICE);
		price=Integer.toString(intPrice/100);
		extraParam=exOrderNo;
		
		PipawPayRequest pipawPayRequest = new PipawPayRequest();
		pipawPayRequest.setMerchantId(merchantId);
		pipawPayRequest.setMerchantAppId(merchantAppId);
		pipawPayRequest.setAppId(appId);
		pipawPayRequest.setPayerId(payerId);
		pipawPayRequest.setExOrderNo(exOrderNo);
		pipawPayRequest.setSubject(subject);
		pipawPayRequest.setPrice(price);
		pipawPayRequest.setExtraParam(extraParam);
		
		StringBuilder content = new StringBuilder();
		content.append(merchantId).append(merchantAppId).append(appId)
				.append(payerId).append(exOrderNo).append(subject)
				.append(price).append(extraParam).append(privateKey);
		Log.d("Type", "content " + content);
		String merchantSign = MD5Util.MD5(content.toString());
		Log.d("Type", "merchantSign " + merchantSign);
		/**
		 * merchantSign 交易签名
		 */
		pipawPayRequest.setMerchantSign(merchantSign);
		PipawSDK.getInstance().pay(appActivity, pipawPayRequest,
				new PipawPayListener() {

					/**
					 * 客户端同步通知 可选操作，请以服务端异步通知为准。
					 */
					@Override
					public void callback(int resultCode, String data) {
						if (resultCode == PipawSDK.PAY_CANCEL) {
							/**
							 * 用户取消支付
							 */
							
							
						} else if (resultCode == PipawSDK.PAY_SUCCESS) {
							/**
							 * 支付成功
							 */
							TypeSDKNotify_pipa notify = new TypeSDKNotify_pipa();
							notify.payOK();
							
						} else if (resultCode == PipawSDK.PAY_FAIL) {
							/**
							 * 支付失败
							 */
						
						}
					}
				});
	}

	@Override
	public void SendInfo(Context _in_context, String _in_data) {
		this.SetPlayerInfo(_in_context, _in_data);
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
