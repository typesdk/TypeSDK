package com.type.sdk.android.jolo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 解析支付结果。
 * 支付结果字符串实例，采用标准的json格式：
 * {"result_code":"200","result_msg"
 * :"支付成功","game_code"
 * :"game10001","game_order_id":"order1234567890","jolo_order_id"
 * :"jolo1234567890","gmt_payment":"2013-02-28 14:35:10"}
 * 
 * @author Administrator
 * 
 */
public class ResultOrder {

	//game_code	游戏编号，由jolo平台统一分配
	private static final String GAME_CODE = "game_code";
	//game_order_id	游戏订单，由游戏应用负责保证唯一，确保游戏内唯一
	private static final String GAME_ORDER_ID = "game_order_id";
	//jolo_order_id	平台订单，由平台生成，确保平台唯一
	private static final String JOLO_ORDER_ID = "jolo_order_id";
	// 实际支付成功的金额
	private static final String REAL_AMOUNT="real_amount";
	//gmt_payment	订单支付时间
	private static final String GMT_PAYMENT = "gmt_payment";
	//result_code	错误编码
	private static final String RESULT_CODE = "result_code";
	//result_msg	错误信息
	private static final String RESULT_MSG = "result_msg";

	private String m_game_code;
	private String m_game_order_id;
	private String m_jolo_order_id;
	private String m_real_amount;
	private String m_gmt_payment;
	private int m_result_code;
	private String m_result_msg;

	public ResultOrder(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			m_game_code = jsonObject.getString(GAME_CODE);
			m_game_order_id = jsonObject.getString(GAME_ORDER_ID);
			m_jolo_order_id = jsonObject.getString(JOLO_ORDER_ID);
			m_real_amount = jsonObject.getString(REAL_AMOUNT);
			m_gmt_payment = jsonObject.getString(GMT_PAYMENT);
			m_result_code = jsonObject.getInt(RESULT_CODE);
			m_result_msg = jsonObject.getString(RESULT_MSG);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getGameCode() {
		return m_game_code;
	}

	public String getGameOrderID() {
		return m_game_order_id;
	}

	public String getJoloOrderID() {
		return m_jolo_order_id;
	}

	public String getRealAmount(){
		return m_real_amount;
	}

	public String getGmtPayMent() {
		return m_gmt_payment;
	}

	public int getResultCode() {
		return m_result_code;
	}

	public String getResultMsg() {
		return m_result_msg;
	}
}

