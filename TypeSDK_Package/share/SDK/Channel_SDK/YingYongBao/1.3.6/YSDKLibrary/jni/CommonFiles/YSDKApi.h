/**
 * hardyshi 2015-12-24
 */
#ifndef __YSDKAPI_H__
#define __YSDKAPI_H__

#include <string>
#include "YSDKCommon.h"
#include "YSDKBuglyListener.h"
#include "YSDKUserListener.h"
#include "YSDKDefine.h"
#include "YSDKPayListener.h"

#include <jni.h>

/**  
 * YSDK接口函数
 *
 * 该类封装了YSDK的外部接口
 */
class YSDKApi {

private:
	static YSDKApi * mYSDKApiInstance;
	YSDKBuglyListener* mBuglyListener;
	YSDKUserListener* mUserListener;
	YSDKPayListener* mPayListener;

	JavaVM* mPVM;

	WakeupRet mLastWakeup;
	UserLoginRet mLastLoginRet;
	bool needDelayWakeupNotify;
	bool needDelayLoginNotify;

private:
	YSDKApi();
	virtual ~YSDKApi();

public:

	void init(JavaVM* pVM);
	void setVM(JavaVM* pVM);
	JavaVM* getVm();

	static YSDKApi* getInstance();

	void setWakeup(WakeupRet& wakeup);
	WakeupRet& getWakeup();

	void setLoginRet(UserLoginRet& lr);
	UserLoginRet& getLoginRet();

	/**
 	 * 设置bugly异常上报相关回调
  	 */
	void setBuglyListener(YSDKBuglyListener* pListener);
	YSDKBuglyListener* getBuglyListener() const;

	/**
     * 设置用户登录相关回调
     */
	void setUserListener(YSDKUserListener* pListener);
	YSDKUserListener* getUserListener() const;

	void clearPayListener();
	YSDKPayListener* getPayListener() const;

    /**
	 * YSDK的getVersion方法，调用该方法获取SDK的当前版本
	 */
	const std::string getVersion();

	/**
	 * YSDK的getChannelID方法，调用该方法获取SDK的当前版本
	 */
	const std::string getChannelId();

	/**
	 * 获取注册渠道
	 */
	const std::string getRegisterChannelId();

	/**
     * YSDK获取平台软件（手Q，微信）是否安装的方法
     */
	bool isPlatformInstalled(ePlatform platform);

	/**
     * YSDK获取平台软件（手Q，微信）版本号方法
     */
	const std::string getPlatformAppVersion(ePlatform platform);

	/**
     * 用户登录
     */
	void login(ePlatform platform);

	/**
     * 用户登出
     */
	void logout();

	/**
     * 获取用户登录态数据
     */
	int getLoginRecord(UserLoginRet& userLoginRet);

	const std::string getPf();

	const std::string getPfKey();

	/**
     * 切换到拉起票据登录
     */
	bool switchUser(bool switchToLaunchUser);

	/**
     * 查询个人信息
     */
	void queryUserInfo(ePlatform platform);

	/**
     * 灯塔事件上报
     */
	void reportEvent(unsigned char* name, std::vector<KVPair>& eventList, bool isRealTime);

	/**
	 * 充值游戏币
	 * @param zoneId 大区id
	 * @param saveValue 充值数额
	 * @param isCanChange 设置的充值数额是否可改
	 * @param resData 代币图标的二进制数据
     * @param listener 充值回调
     */
	void recharge(unsigned char* zoneId, unsigned char* saveValue, bool isCanChange,
                  unsigned char* resData,int cReslength,unsigned char* ysdkExtInfo,YSDKPayListener* pListener);

	/**
	 * 充值游戏币
	 * @param zoneId 大区id
	 * @param goodsTokenUrl 服务器下订单返回的url_params
	 * @param resData 代币图标的二进制数据
     * @param listener 充值回调
     */
	void buyGoods(unsigned char* zoneId, unsigned char* goodsTokenUrl,
				  unsigned char* resData,int cReslength,unsigned char* ysdkExtInfo,YSDKPayListener* pListener);
};

#endif

