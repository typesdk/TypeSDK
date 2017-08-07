package com.type.sdk.android.uc;

import org.json.JSONException;
import org.json.JSONObject;

import com.type.sdk.android.TypeSDKLogger;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.uc.paysdk.face.commons.PayResponse;
import cn.uc.paysdk.face.commons.Response;
import cn.uc.paysdk.face.commons.SDKCallbackListener;
import cn.uc.paysdk.face.commons.SDKError;


/**
 * JY单机支付回调处理类
 * @author Administrator
 *
 */
public class TypePaySDKListener implements SDKCallbackListener {
	
	private Handler mHandler; //用于处理支付回调信息

	public TypePaySDKListener(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void onErrorResponse(SDKError error) {
		//失败, 该回调是在子线程中调用，通过mHandler转到UI线程显示
		//游戏接入需根据业务酌情修改
		handleErrResp(error);
	}

	@Override
	public void onSuccessful(int status, Response response) {
		
		if (response.getType() == Response.LISTENER_TYPE_INIT) {
		    //JY单机支付SDK初始化成功
            Message message = new Message();
            message.what = TypeSDKBonjour.Instance().HANDLER_INIT_SUCC;
            mHandler.sendMessage(message);
            TypeSDKLogger.d("支付初始化成功，可以调用支付接口了");
            
		} else if (response.getType() == Response.LISTENER_TYPE_PAY) {
			//JY单机支付SDK支付成功
			/**
			 * 当为支付回调时，必须响应消息设置setMessage 必须及时响应，不要进行耗时操作，否则会导致线程阻塞
			 * 相关异步操作可以在handler中进行，另起工作者线程 设置为
			 * Response.OPERATE_SUCCESS_MSG 代表CP成功进行相关动作时，响应SDK
			 * Response.OPERATE_FAIL_MSG 代表CP进行相关动作失败时，响应SDK
			 */
			//#########################[重要，设置确认收到，否则会不断的回调]###############
			response.setMessage(Response.OPERATE_SUCCESS_MSG);
			//#########################!!!!!!!!!!!!!!!!!!!!!!!!!#############
			
			
			handlePayResp(response); //解析订单信息
			
			handlePaySucc(status, response); //显示支付结果
		}
	}
	
	/**
	 * 解析支付成功，订单返回的参数
	 * @param response
	 */
	private void handlePayResp(Response response) {
		try {
		    if (!TextUtils.isEmpty(response.getData())) {
				JSONObject data = new JSONObject(response.getData());
				String orderId = data.optString(PayResponse.CP_ORDER_ID); // CP订单号
				String tradeId = data.optString(PayResponse.TRADE_ID); // 交易号
				String payMoney = data.optString(PayResponse.PAY_MONEY); // 支付金额
				String payType = data.optString(PayResponse.PAY_TYPE); // 支付类型
				String orderStatus = data.optString(PayResponse.ORDER_STATUS); // 订单状态
				String orderFinishTime = data.optString(PayResponse.ORDER_FINISH_TIME); // 订单完成时间
				String productName = data.optString(PayResponse.PRO_NAME);// 道具名称
				String extendInfo = data.optString(PayResponse.EXT_INFO); // 商品扩展信息
				String attachInfo = data.optString(PayResponse.ATTACH_INFO); // 附加透传信息
				
				///todo 游戏拿到订单结果，进行后续处理。。。
		    }
		} catch (JSONException ex) {
			ex.printStackTrace();
		}		
	}
	
	/**
	 * 展示支付成功订单结果信息
	 * @param status
	 * @param response
	 */
	private void handlePaySucc(int status, Response response) {
		Message msg = new Message();
		msg.what = TypeSDKBonjour.Instance().HANDLER_PAY_CALLBACK;
		if (!TextUtils.isEmpty(response.getData())) {
			msg.obj = response.getData();
		}
		
		mHandler.sendMessage(msg);
	}
	
	/**
	 * 回调结果失败处理
	 * @param error
	 */
	private void handleErrResp(SDKError error) {
		String msg = error.getMessage();
        TypeSDKLogger.e("handleErrResp : " + msg);
        if (TextUtils.isEmpty(msg)) {
			msg = "SDK occur error!";
		}
        
		Message message = new Message();
		message.what = TypeSDKBonjour.Instance().HANDLER_SHOW_ERROR_DIALOG;
		message.obj = msg;
		mHandler.sendMessage(message);
	}

}
