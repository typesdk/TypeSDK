package com.type.sdk.android.jolo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 支付订单编码，采用标准json格式
 * {"session_id":"11123443212","product_id":"pd001","amount":"300", "game_code"
 * :"game10001","product_name":"元宝30个","user_code":"100000009","notify_url"
 * :"http://www.g.cn/pay_notify"
 * ,"product_desc":"","game_order_id":"order1234567890"}
 * 
 * @author Administrator
 * 
 */
public class Order {

	// 注意：下面所有字段都不能为空，没有请填充""字符串

	// game_name 游戏名称，由游戏方填写
	private static final String GAME_NAME = "game_name";
	// game_code 游戏编号，由jolo平台统一分配
	private static final String GAME_CODE = "game_code";
	// game_order_id 游戏订单，由游戏应用负责保证唯一，用于标识此订单
	private static final String GAME_ORDER_ID = "game_order_id";
	// product_id 产品编号，游戏应用生成
	private static final String PRODUCT_ID = "product_id";
	// product_name 产品名称，游戏应用生成
	private static final String PRODUCT_NAME = "product_name";
	// product_des 产品描述，游戏应用生成
	private static final String PRODUCT_DES = "product_des";
	// amount 金额，游戏应用生成，单位分
	private static final String AMOUNT = "amount";
	// notify_url 支付结果回调url
	private static final String NOTIFY_URL = "notify_url";
	// user_code jolo游戏平台用户，由启动网游时平台传入
	private static final String USER_CODE = "user_code";
	// session_id 标识此次用户会话，由启动网游时平台传入
	private static final String SESSION_ID = "session_id";

	private String m_game_name;
	private String m_game_code;
	private String m_game_order_id;
	private String m_product_id;
	private String m_product_name;
	private String m_product_des;
	private String m_amount;
	private String m_notify_url;
	private String m_user_code;
	private String m_session_id;

	public Order() {
		m_game_name = "";
		m_game_code = "";
		m_game_order_id = "";
		m_product_id = "";
		m_product_name = "";
		m_product_des = "";
		m_amount = "";
		m_notify_url = "";
		m_user_code = "";
		m_session_id = "";
	}

	public Order(String game_name, String game_code, String game_order_id,
			String product_id, String product_name, String product_des,
			String amount, String notify_url, String user_code,
			String session_id) {
		m_game_name = game_name;
		m_game_code = game_code;
		m_game_order_id = game_order_id;
		m_product_id = product_id;
		m_product_name = product_name;
		m_product_des = product_des;
		m_amount = amount;
		m_notify_url = notify_url;
		m_user_code = user_code;
		m_session_id = session_id;
	}
	
	public void setGameName(String game_name){
		m_game_name = game_name;
	}
	
	public void setGameCode(String game_code){
		m_game_code = game_code;
	}
	
	public void setGameOrderid(String game_order_id){
		m_game_order_id = game_order_id;
	}
	
	
	public void setProductID(String product_id){
		m_product_id = product_id;
	}
	
	public void setProductName(String product_name){
		m_product_name = product_name;
	}
	
	public void setProductDes(String product_des){
		m_product_des = product_des;
	}
	
	public void setAmount(String amount){
		m_amount = amount;
	}
	
	public void setNotifyUrl(String notify_url){
		m_notify_url = notify_url;
	}
	
	public void setUsercode(String user_code){
		m_user_code = user_code;
	}
	
	public void setSession(String session_id){
		m_session_id = session_id;
	}

	public String toJsonOrder() {
		JSONObject jsonorder = new JSONObject();
		try {
			jsonorder.put(GAME_NAME, m_game_name);
			jsonorder.put(GAME_CODE, m_game_code);
			jsonorder.put(GAME_ORDER_ID, m_game_order_id);
			jsonorder.put(PRODUCT_ID, m_product_id);
			jsonorder.put(PRODUCT_NAME, m_product_name);
			jsonorder.put(PRODUCT_DES, m_product_des);
			jsonorder.put(AMOUNT, m_amount);
			jsonorder.put(NOTIFY_URL, m_notify_url);
			jsonorder.put(USER_CODE, m_user_code);
			jsonorder.put(SESSION_ID, m_session_id);
			return jsonorder.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
