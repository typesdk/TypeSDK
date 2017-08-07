/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

public final class TypeSDKDefine {
	public static final String UNITY_RECIVER = "TypeSDK";
	public static final String UNITY_START_ACTIVI_ATT = "real_start_activi";

	public final static class ReceiveFunction {
		public static final String MSG_LOGIN = "NotifyLogin";// 登录响应
		public static final String MSG_LOGOUT = "NotifyLogout";// 登出响应
		public static final String MSG_PAYRESULT = "NotifyPayResult";// 支付结果响应
		public static final String MSG_UPDATEFINISH = "NotifyUpdateFinish";// 更新完毕响应
		public static final String MSG_INITFINISH = "NotifyInitFinish";// 初始化完毕响应
		public static final String MSG_RELGOIN = "NotifyRelogin";// 重新登录响应
		public static final String MSG_EXITGAMECANCEL = "NotifyCancelExitGame";// 取消退出游戏的响应
		public static final String MSG_SETPHONEINFO = "NotifyGetPhoneInfo";// 获取手机信息的响应
		public static final String MSG_RECEIVE_LOCAL_PUSH = "NotifyReceiveLocalPush";// 通知响应
		public static final String MSG_USER_FRIENDS = "NotifyUserFriends";// 获取好友列表
		public static final String MSG_SHARERESULT = "NotifyShareResult";// 分享结果响应
		public static final String MSG_EXTRA_FUNCTION = "NotifyExtraFunction";// 响应额外的功能函数
		public static final String MSG_INVITERESULT = "NotifyInvitedResult";//邀请响应通知
	}

	public final static class AttName {
		// 应用的标识基本属性
		public static final String APP_NAME = "app_name"; // 应用安装后显示的名称
		public static final String APP_KEY = "app_key"; // 应用的app key
		public static final String APP_ID = "app_id"; // 应用的 app id
		public static final String REDIRECT_URI = "redirect_uri"; // 陌陌的
																	// redirect
																	// uri
		public static final String SECRET_KEY = "secret_key"; // 应用的 secret key
		public static final String CHANNEL_ID = "channel_id"; // 应用的 渠道id
		public static final String PAY_KEYSTORE = "pay_keystore"; // 支付用的
																	// keystore密码
		public static final String PAY_PASSWORD = "pay_password"; // 支付用的 密钥
		public static final String CP_ID = "cp_id"; // 应用开发者自己的id
		public static final String SDK_CP_ID = "sdk_cp_id"; // 渠道分配给cp的id
		public static final String SDK_NAME = "sdk_name"; // 应用所接渠道的sdk标识
		public static final String PLATFORM = "platform"; // 应用的平台（ANDROID/IOS）
		public static final String VERSION = "version"; // 应用的版本号
		public static final String BUNDLE_INDENTIFLER = "bundle_indentifler"; // 应用的包名
		public static final String BUNDLE_NAME = "bundle_name"; // ios中的应用（bundle
																// name）
		public static final String PRODUCT_PACKAGE_NAME = "product_package_name"; // 安卓中应用的包名
		public static final String PRODUCE_KEY = "product_key"; // 应用的product
																// key
		public static final String PRODUCT_ID = "product_id"; // 应用的 product id
		public static final String BUNDLE_DISPLAY_NAME = "bundle_display_name"; // iso中应用的
																				// bundle
																				// display
																				// name
		public static final String PAY_CALL_BACK_URL = "pay_call_back_url"; // 支付回调的url地址
		public static final String PAY_BASE_RATE = "pay_base_rate"; // 支付兑换的比例
		public static final String PAY_BASE_VALUE = "pay_base_value"; // 支付兑换的默认价格（分）
		public static final String EXTRA = "extra"; // 通用的额外数据

		// 设备设置相关属性
		public static final String IS_LANDSPACE_GAME = "is_landspace_game"; // 是否是横屏游戏（1/0）
		public static final String IS_SUPPORT_ROATED = "is_support_roated"; // 是否支持旋转（1/0）
		public static final String IS_SHOW_LOG = "is_show_log"; // 是否输出debug信息（1/0）
		public static final String IS_LONG_COMET = "is_long_comet"; // 应用是否和渠道为长链接
		public static final String IS_OPEN_RECHARGE = "is_open_recharge"; // 应用是否开放内部支付
		public static final String IS_LOGOUT_AUTO_LOGIN = "is_logout_auto_login"; // 应用是否登出后自动显示登录界面
		public static final String IS_DEBUG_MODEL = "is_debug_model"; // 应用是否处于debug模式下（1/0）
		public static final String CLOSE_RECHARGE_MSG = "close_recharge_msg"; // 当支付未开启时显示的提示信息

		// 用户的相关属性
		public static final String USER_NAME = "user_name"; // 用户名
		public static final String USER_PASS_WORD = "user_pass_word"; // 用户密码
		public static final String USER_TOKEN = "user_token"; // 用户验证用token
		public static final String USER_SESSION_ID = "user_session_id"; // 用户验证用sessionID
		public static final String USER_ID = "user_id"; // 用户id
		public static final String USER_HEAD_ID = "user_head_id"; // 用户头像id
		public static final String USER_HEAD_URL = "user_head_url"; // 用户头像url
		public static final String TIMESTAMP = "timestamp"; // 时间戳

		// 用户创建的角色相关属性
		public static final String ROLE_ID = "role_id"; // 角色id
		public static final String ROLE_NAME = "role_name"; // 角色名字
		public static final String ROLE_LEVEL = "role_level"; // 角色等级
		public static final String ROLE_CREATE_TIME = "role_create_time";// 角色创建时间
		public static final String ROLE_LEVELUP_TIME = "role_levelup_time";// 角色升级时间
		public static final String ZONE_ID = "zone_id"; // 所在大区id
		public static final String ZONE_NAME = "zone_name"; //
		public static final String SERVER_ID = "server_id"; // 所在服务器id
		public static final String SERVER_NAME = "server_name"; // 所在服务器名字
		public static final String SAVED_BALANCE = "saved_balance";// 当前角色余额（RMB购买的游戏币），默认为0
		public static final String ROLE_TYPE = "role_type";// 角色统计信息类型即调用时机
		public static final String CREATE_ROLE = "create_role";// 创建角色
		public static final String LEVE_UP = "leveUp";// 角色升级
		public static final String ENTER_GAME = "enterGame";// 进入游戏
		public static final String VIP_LEVEL = "vip_level";// vip等级

		// 订单的相关属性
		public static final String REAL_PRICE = "real_price"; // 实际支付价格
		public static final String ORGIN_PRICE = "orgin_price"; // 原始价格
		public static final String DISCOUNT = "discount"; // 折扣比例（n%）
		public static final String ITEM_COUNT = "item_count"; // 商品数量
		public static final String ITEM_LOCAL_ID = "item_local_id"; // 商品在应用本地的id
		public static final String ITEM_SERVER_ID = "item_server_id"; // 商品在渠道的id
		public static final String ITEM_NAME = "item_name"; // 商品名字
		public static final String ITEM_DESC = "item_desc"; // 商品描述
		public static final String BILL_NUMBER = "bill_number"; // 订单号

		// 支付结果的相关属性
		public static final String PAY_RESULT = "pay_result"; // 支付结果（1/0）成功／失败
		public static final String PAY_RESULT_REASON = "pay_result_reason"; // 支付结果的原因（失败原因）
		public static final String PAY_RESULT_DATA = "pay_result_data"; // 支付结果的返回数据
		// 分享的相关属性
		public static final String SHARE_ID = "share_id";// //分享id，分享消息的唯一标识，每次分享时id不能相同
		public static final String SHARE_TARGET_URL = "share_target_url";// 点击跳转的地址
		public static final String SHARE_IMG_LOCAL_URL = "share_img_local_url";// 分享图片的本地url
		public static final String SHARE_VIDEO_URL = "share_video_url";// 分享视频的url
		public static final String SHARE_SENDER_ID = "share_sender_id"; // 分享的发起人id
		public static final String SHARE_SENDER_NAME = "share_sender_name"; // 分享的发起人名字
		public static final String SHARE_RECEIVER_ID = "share_receiver_id"; // 分享的接受人id
		public static final String SHARE_RECEIVER_NAME = "share_receiver_name"; // 分享的接受人名字
		public static final String SHARE_INFO_TITLE = "share_info_title"; // 分享的标题
		public static final String SHARE_INFO_CONTENT = "share_info_content"; // 分享的文字内容
		public static final String SHARE_IMG_URL = "share_img_url"; // 分享的图片url
		public static final String SHARE_TYPE = "share_type"; // 分享的类型
		public static final String RESULT = "result"; // 分享结果（1/0）成功／失败
		// 渠道需求相关
		public static final String SDK_REQUEST_AND_SUPPORT = "sdk_request_and_support"; // sdk的支持和需求
		public static final String REQUEST_INIT_WITH_SEVER = "request_init_with_sever"; // init的时候
		public static final String SUPPORT_SHARE = "support_share"; // 支持分享接口
		public static final String NOT_ALLOW_PUSH_NOTIFY = "not_allow_push_notify"; // 渠道不支持推送
		public static final String SUPPORT_PERSON_CENTER = "support_person_center"; // 支持显示个人中心接口
		// 手机信息相关
		public static final String APP_VERSION_NAME = "app_version_name";// 当前应用的版本号
		public static final String CURRENT_TIMEZONE = "current_timezone";// 设备的当前时区
		public static final String CURRENT_TIME = "current_time";// 设备的当前时间
		public static final String CURRENT_LANGUAGE = "current_language";// 设备当前语言环境
		public static final String SIM_OPERATOR_NAME = "sim_operator_name";// 运营商
		public static final String NETWORK_TYPE = "network_type";// 网络类型
		public static final String PHONE_IP = "phone_ip";// 设备当前的Ip地址
		public static final String PHONE_MODEL = "phone_model";// 设备型号
		public static final String PHONE_PRODUCTOR = "phone_productor";// 设备生产商
		public static final String CPU_TYPE = "cpu_type";// cpu型号
		public static final String SYSTEM_VERSION = "system_version";// 系统版本
		public static final String SCREEN_HEIGHT = "screen_height";// 屏高
		public static final String SCREEN_WIDTH = "screen_width";// 屏宽
		public static final String ROOT_AHTH = "root_ahth";// 是否获得Root权限
		public static final String MEMORY_TOTAL_MB = "memory_total_mb";// 设备运行内存
		public static final String MAC_ADDRESS = "mac_address";// 设备Mac地址
		public static final String IMEI = "imei";// 移动设备国际身份码
		public static final String IMSI = "imsi";// 国际移动用户识别码
		public static final String SIM_SERIAL_NUMBER = "sim_serial_number";// SIM卡序列号
		public static final String ANDROID_ID = "android_id";// 安卓设备唯一编号
		public static final String APP_CURRENT_USED_MEMORY = "app_current_used_memory";// 内存占用
		public static final String APP_CURRENT_USED_CPU = "app_current_used_cpu";// cpu使用率
		public static final String CURRENT_VOLUME = "current_volume";// 音量
		public static final String EXTRA_MSG_TYPE = "extra_msg_type";//
		public static final String ACTIVE_CALL = "active_call";
		public static final String PASSIVE_NOTIFY = "passive_notify";
		public static final String WHITE_ID = "white_id";// Login开关
		public static final String IP = "ip";
		public static final String GET_IP = "getip";

		public static final String PUSH_TYPE = "push_type"; // 推送的类型 int 0,1,2…
		public static final String PUSH_TYPE_DATA = "push_type_data"; // 推送类型的可自定义内容
																		// string
		public static final String PUSH_ID = "push_id"; // 推送的id int
														// 1,2,3...同一id下推送唯一
		public static final String PUSH_TITLE = "push_title"; // 推送的标题 string
																// xxxx
		public static final String PUSH_INFO = "push_info"; // 推送的内容 string xxxx
		public static final String PUSH_TICKER = "push_ticker"; // 推送在通知栏出现时显示的内容
																// String xxxx
		public static final String PUSH_REPEAT_INTERVAL = "push_repeat_interval"; // 重复的时间间隔
		public static final String PUSH_ALERT_DATE = "push_alert_date"; // 推送的出现时间
		public static final String PUSH_NEED_NOTIFY = "push_need_notify"; // 推送是否需要将收到的信息发送给客户端
		public static final String PUSH_RECEIVE_TYPE = "push_receive_type"; // 推送反馈客户端的信息类型
		public static final String PUSH_RECEIVE_INFO = "push_receive_info"; // 推送反馈客户端的信息附带数据

		public static final String PROFILE_VERSION = "profile_verison";// 配置表的版本
		public static final String CRASH_PROFILE = "crash_profile";// crash数据表在json中的key
		public static final String IS_OPEN = "is_open";// 是否开启crash收集
		public static final String CATCH_TAG = "catch_tag";// 额外捕捉的crash tag
		public static final String COLLECT_MODEL_REGULAR = "collect_model_regular";// crash手机的机型列表规则
		public static final String COLLECT_MODEL = "collect_model";// crash收集的机型列表
		public static final String COLLECT_SDK_REGULAR = "collect_sdk_regular";// 收集的渠道列表规则
		public static final String COLLECT_SDK = "collect_sdk";// 具体列表为
																// 约定的SDK_NAME
		public static final String OTHER = "other";// 其他的sdk服务器配置
		public static final String PUSHSERVICE = "pushservice";// 推送开关
		public static final String OPEN_LOG = "open_log";// LOG开关
		public static final String OPEN_PAY = "open_pay";// 支付开关
		public static final String PAY_MODE = "pay_mode";// 支付模式
		public static final String CRASH_URL = "crash_url";// 崩溃日志上传的ftp地址
		public static final String CRASH_PORT = "crash_port";
		public static final String SWITCHCONFIG_URL = "switchconfig_url";// 开关配置文件地址
		public static final String PAYMENT_PROFILE = "payment_profile";// 支付相关数据
		public static final String SDK_OPEN_REGULAR = "sdk_open_regular";// 支付相关渠道列表规则
		public static final String SDK_OPEN_LIST = "sdk_open_list";// 支付相关渠道列表

		public static final String EXTRA_FUNCTION_KEY = "extra_function_key";// 额外函数的参数key
		public static final String EXTRA_FUNTION_VALUE = "extra_function_value";// 额外函数参数的value
		public static final String EXTRA_FUNCTION_VALUE_2 = "extra_function_value_2";// 额外参数的备用value

		// 用户数据统计
		public static final String EVENT_ID = "event_id";
		public static final String USER_STATIC_TIMER = "user_static_timer";// 用户行为统计
																			// 定时
		public static final String USER_STATIC_CURRENT = "user_static_current";// 用户行为统计
																				// 实时
		public static final String USER_STATIC_KEY = "user_static_key";// 用户行为统计
																		// key
		public static final String ANDROID_GID = "android_gid";// adid统计ID

		// 应用宝专用字段
		public static final String SDK_NAME_QQ = "sdk_name_qq";// QQ标识
		public static final String SDK_NAME_WX = "sdk_name_wx";// 微信标识
		public static final String TECENT_TYPE = "tencent_type";// 用来标记登录时的channel_id
		public static final String OPENID = "openid";// 从手Q登录态或微信登录态中获取的openid的值
		public static final String OPENKEY = "openkey";// 从手Q登录态中获取的pay_token的值或微信登录态中获取的access_token
														// 的值
		public static final String PF = "pf";// 平台来源，平台-注册渠道-系统运行平台-安装渠道-业务自定义
		public static final String PFKEY = "pfkey";// // pf校验Key
		public static final String PAY_TOKEN = "pay_token";// 手Q登录时从手Q登录态中获取的pay_token的值,使用YSDK登录后获取到的eToken_QQ_Pay返回内容就是pay_token；
															// 微信登录时特别注意该参数传空。
	}

}
