#include "com_tencent_ysdk_module_user_UserNativeListener.h"
#include "com_tencent_tmgp_yybtestsdk_PlatformTest.h"
#include "CommonFiles/YSDKApi.h"
#include "CommonFiles/YSDKDefine.h"
#include <string>
#include <vector>
#include <sstream>

#include <android/log.h>


static JNIEnv *sGlobalJNIEnv;
static jobject sGlobalActivity;
/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    setActivity
 * Signature: (Landroid/app/Activity;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_setActivity(
    JNIEnv * env, jclass, jobject activity) {
    LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_setActivity call %s", "");
    sGlobalJNIEnv = env;
    sGlobalActivity = env->NewGlobalRef(activity);
}

static void sendResult (const std::string& result) {
    jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
    jmethodID method = sGlobalJNIEnv->GetMethodID(cls, "sendResult", "(Ljava/lang/String;)V");
    jstring jResult = sGlobalJNIEnv->NewStringUTF(result.c_str());
    sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, method, jResult);
    sGlobalJNIEnv->DeleteLocalRef(jResult);
}

static void showToastTips (const std::string& result) {
	jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
	jmethodID method = sGlobalJNIEnv->GetMethodID(cls, "showToastTips", "(Ljava/lang/String;)V");
	jstring jResult = sGlobalJNIEnv->NewStringUTF(result.c_str());
	sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, method, jResult);
	sGlobalJNIEnv->DeleteLocalRef(jResult);
}

static void letUserLogout () {
	jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
	jmethodID method = sGlobalJNIEnv->GetMethodID(cls, "letUserLogout", "()V");
	sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, method);
	LOGD("OnLoginNotify call letUserLogout end%s", "");
}

class GlobalBuglyListener: public YSDKBuglyListener {
public:
	virtual std::string OnCrashExtMessageNotify(){
		// 此处游戏补充crash时上报的额外信息
		std::string str = "new jni update extra jni crash log now!";
		LOGD("OnCrashExtMessageNotify %s", str.c_str());
		return str;
	}
	virtual unsigned char* OnCrashExtDataNotify(){
		// 此处游戏补充crash时上报的额外信息
		LOGD("OnCrashExtDataNotify %s", "");
		std::string str="OnCrashExtDataNotify CPP";
		unsigned char *extData = (unsigned char *)str.c_str();
		return extData;
	}

	virtual ~GlobalBuglyListener() {

	}
};

GlobalBuglyListener gTestBuglyListener;


class GlobalUserListener: public YSDKUserListener {
public:
	virtual void OnLoginNotify(UserLoginRet& loginRet){
		LOGD("OnLoginNotify:ret:%d  flag:%d user_type:%d  platform:%d OpenId:%s, Token Size: %d",loginRet.ret, loginRet.flag, loginRet.user_type, loginRet.platform, loginRet.open_id.c_str(), loginRet.token.size());

        jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
        jmethodID method = sGlobalJNIEnv->GetMethodID(cls, "stopWaiting", "()V");
        sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, method);
        if(loginRet.ret == RET_SUCC){
            // 下面是MSDKSample使用的逻辑, 游戏忽略此部分内容
			method = sGlobalJNIEnv->GetMethodID(cls, "letUserLogin", "()V");
            sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, method);
            LOGD("OnLoginNotify call letUserLogin end%s", "");
        }else{
            if (loginRet.platform == ePlatform_QQ) {
                // 读取QQ的登陆票据
                switch (loginRet.flag) {
					case eFlag_LocalTokenInvalid:
						showToastTips("您尚未登录或者之前的登录已过期，请重试");
						letUserLogout();
						break;
                    case eFlag_QQ_NotInstall:
						showToastTips("手机未安装手Q，请安装后重试");
						letUserLogout();
						break;
                    case eFlag_QQ_NetworkErr:
						showToastTips("QQ登录异常，请重试");
						letUserLogout();
						break;
					case eFlag_QQ_NotSupportApi:
						showToastTips("手机手Q版本太低，请升级后重试");
						letUserLogout();
						break;
                    case eFlag_QQ_UserCancel:
						showToastTips("用户取消授权，请重试");
						letUserLogout();
						break;
                    case eFlag_QQ_LoginFail:
						showToastTips("QQ登录失败，请重试");
						letUserLogout();
						break;
					case eFlag_Login_NotRegisterRealName:
						showToastTips("您的账号没有进行实名认证，请实名认证后重试");
						letUserLogout();
						break;
					case eFlag_Error:
						showToastTips("QQ登录异常，请重试");
						letUserLogout();
						break;
                }
            } else if (loginRet.platform == ePlatform_Weixin) {
                switch (loginRet.flag) {
					case eFlag_LocalTokenInvalid:
						showToastTips("您尚未登录或者之前的登录已过期，请重试");
						letUserLogout();
						break;
					case eFlag_WX_NotInstall:
						showToastTips("手机未安装微信，请安装后重试");
						letUserLogout();
						break;
					case eFlag_WX_NotSupportApi:
						showToastTips("手机微信版本太低，请升级后重试");
						letUserLogout();
						break;
					case eFlag_WX_UserCancel:
						showToastTips("用户取消授权，请重试");
						letUserLogout();
						break;
					case eFlag_WX_UserDeny:
						showToastTips("用户拒绝了授权，请重试");
						letUserLogout();
						break;
					case eFlag_WX_LoginFail:
						showToastTips("微信登录失败，请重试");
						letUserLogout();
						break;
					case eFlag_Login_NotRegisterRealName:
						showToastTips("您的账号没有进行实名认证，请实名认证后重试");
						letUserLogout();
						break;
					case eFlag_Error:
						showToastTips("微信登录异常，请重试");
						letUserLogout();
                        break;
                }
            }
        }
		LOGD("OnLoginNotify finished in test%s", "");
	}

	virtual void OnWakeupNotify(WakeupRet& wakeupRet){
		LOGD("OnWakeupNotify: platform:%d flag:%d openid:%s", wakeupRet.platform, wakeupRet.flag, wakeupRet.open_id.c_str());
        switch(wakeupRet.flag){
            case eFlag_Wakeup_YSDKLogining:{
                // 下面是MSDKSample使用的逻辑, 游戏忽略此部分内容
                LOGD("OnWakeupNotify: platform:%s","");
                break;
            }
            case eFlag_Wakeup_NeedUserSelectAccount:{
                LOGD("diff account%s", "");
                jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
                jmethodID methodshowDiffLogin = sGlobalJNIEnv->GetMethodID(cls, "showDiffLogin", "()V");
                sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, methodshowDiffLogin);
                LOGD("OnWakeupNotify call showDiffLogin end%s", "");
                break;
            }
            case eFlag_Wakeup_NeedUserLogin:{
                LOGD("login%s", "");
                jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
                jmethodID methodletUserLogout = sGlobalJNIEnv->GetMethodID(cls, "letUserLogout", "()V");
                sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, methodletUserLogout);
                LOGD("OnWakeupNotify call letUserLogout end%s", "");
                break;
            }
            default :{
                LOGD("login%s", "");
                jclass cls = sGlobalJNIEnv->GetObjectClass(sGlobalActivity);
                jmethodID methodletUserLogout = sGlobalJNIEnv->GetMethodID(cls, "letUserLogout", "()V");
                sGlobalJNIEnv->CallVoidMethod(sGlobalActivity, methodletUserLogout);
                LOGD("OnWakeupNotify call letUserLogout end%s", "");
                break;
            }
        }
        LOGD("OnWakeupNotify: platform:%d flag:%d openid:%s", wakeupRet.platform, wakeupRet.flag, wakeupRet.open_id.c_str());
    }

	virtual void OnRelationNotify(UserRelationRet& relationRet){
		LOGD("OnRelationCallBack flag:%d ", relationRet.flag);

		std::ostringstream result;
		result << "ret:" << relationRet.ret << "\n";
		result << "flag:" << relationRet.flag << "\n";
		result << "msg:" << relationRet.msg.c_str() << "\n";

        if(relationRet.ret == RET_SUCC){
            for (int i = 0; i < relationRet.persons.size(); i++) {
				result << "nickName:" <<relationRet.persons.at(i).nickName.c_str() << "\n";
				result << "openId:" <<relationRet.persons.at(i).openId.c_str() << "\n";
				result << "userId:" <<relationRet.persons.at(i).userId.c_str() << "\n";
				result << "gender:" <<relationRet.persons.at(i).gender.c_str() << "\n";
				result << "pictureSmall:" <<relationRet.persons.at(i).pictureSmall.c_str() << "\n";
				result << "pictureMiddle:" <<relationRet.persons.at(i).pictureMiddle.c_str() << "\n";
				result << "pictureLarge:" <<relationRet.persons.at(i).pictureLarge.c_str() << "\n";
				result << "province:" <<relationRet.persons.at(i).province.c_str() << "\n";
				result << "city:" <<relationRet.persons.at(i).city.c_str() << "\n";
				result << "country:" <<relationRet.persons.at(i).country.c_str() << "\n";
            }
        }
        sendResult(result.str());
	}

	virtual ~GlobalUserListener() {

	}
};

GlobalUserListener gTestUserListener;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	//TODO GAME C++层初始化, 必须在游戏主Activity的onCreate之前被调用
	YSDKApi::getInstance()->init(vm);
	YSDKApi::getInstance()->setBuglyListener(&gTestBuglyListener);
	YSDKApi::getInstance()->setUserListener(&gTestUserListener);

	return JNI_VERSION_1_4;
}
static std::string j2c(JNIEnv* env, jstring str) {

	jclass clsstring = env->FindClass("java/lang/String");
	jmethodID length = env->GetMethodID(clsstring, "length", "()I");
	if (str == NULL) {
		std::string stemp("");
		return stemp;
	}
	jint len = (jint) env->CallIntMethod(str, length);

	if (len > 0) {
		jstring strencode = env->NewStringUTF("utf-8");
		jmethodID mid = env->GetMethodID(clsstring, "getBytes","(Ljava/lang/String;)[B");
		jbyteArray barr = (jbyteArray) env->CallObjectMethod(str, mid,strencode);
		jsize alen = env->GetArrayLength(barr);
		jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);

		char* rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
		env->ReleaseByteArrayElements(barr, ba, 0);

		std::string stemp(rtn);
		free(rtn);
		return stemp;
	} else {
		std::string stemp("");
		return stemp;
	}
}

static jstring c2j(JNIEnv *env, unsigned char* buf, unsigned int size) {
	jclass strClass = env->FindClass("java/lang/String");
	jmethodID ctorID = env->GetMethodID(strClass, "<init>","([BLjava/lang/String;)V");
	jbyteArray bytes = env->NewByteArray(size);
	env->SetByteArrayRegion(bytes, 0, size, (jbyte*) buf);
	jstring encoding = env->NewStringUTF("utf-8");
	return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getVersion
		(JNIEnv *env, jclass){
	const std::string version = YSDKApi::getInstance()->getVersion();
	return env->NewStringUTF(version.c_str());
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getChannelId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getChannelId
		(JNIEnv *env, jclass){
	const std::string version = YSDKApi::getInstance()->getChannelId();
	return env->NewStringUTF(version.c_str());
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getRegisterChannelId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getRegisterChannelId
		(JNIEnv *env, jclass){
	const std::string version = YSDKApi::getInstance()->getRegisterChannelId();
	return env->NewStringUTF(version.c_str());
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    isPlatformInstalled
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_isPlatformInstalled
		(JNIEnv *env, jclass, jint jPlatform){
	return YSDKApi::getInstance()->isPlatformInstalled((ePlatform) jPlatform);

}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getPlatformAppVersion
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getPlatformAppVersion
		(JNIEnv *env, jclass, jint jPlatform){
	const std::string version = YSDKApi::getInstance()->getPlatformAppVersion((ePlatform) jPlatform);
	return env->NewStringUTF(version.c_str());
}



/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    login
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_login
		(JNIEnv *env, jclass, jint jPlatform){
	YSDKApi::getInstance()->login((ePlatform) jPlatform);
}


/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    logout
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_logout
		(JNIEnv *env, jclass){
	YSDKApi::getInstance()->logout();
}


/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
* Method:    getLoginRecord
* Signature: (Lcom/tencent/ysdk/module/user/UserLoginRet;)I
*/
JNIEXPORT jint JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getLoginRecord(
	JNIEnv * env, jclass, jobject jLr) {
	UserLoginRet lr;
	LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getLoginRecord start: msg: %s", lr.msg.c_str());
	int platform = YSDKApi::getInstance()->getLoginRecord(lr);


	jclass cls = env->GetObjectClass(jLr);
	jfieldID retFieldId = env->GetFieldID(cls, "ret", "I");
	env->SetIntField(jLr, retFieldId, lr.flag);

	jfieldID flagFieldId = env->GetFieldID(cls, "flag", "I");
	env->SetIntField(jLr, flagFieldId, lr.flag);

	jfieldID descFieldId = env->GetFieldID(cls, "msg", "Ljava/lang/String;");
	env->SetObjectField(jLr, descFieldId,c2j(env, (unsigned char *) lr.msg.c_str(), strlen((char *) lr.msg.c_str())));

	jfieldID platformFieldId = env->GetFieldID(cls, "platform", "I");
	env->SetIntField(jLr, platformFieldId, (jint) lr.platform);

	jfieldID openIdFieldId = env->GetFieldID(cls, "open_id", "Ljava/lang/String;");
	env->SetObjectField(jLr, openIdFieldId,c2j(env, (unsigned char *) lr.open_id.c_str(), strlen((char *) lr.open_id.c_str())));

	jfieldID pfFieldId = env->GetFieldID(cls, "pf", "Ljava/lang/String;");
	env->SetObjectField(jLr, pfFieldId, c2j(env, (unsigned char *) lr.pf.c_str(), strlen((char *) lr.pf.c_str())));

	jfieldID pfKeyFieldId = env->GetFieldID(cls, "pf_key", "Ljava/lang/String;");
	env->SetObjectField(jLr, pfKeyFieldId, c2j(env, (unsigned char *) lr.pfKey.c_str(), strlen((char *) lr.pfKey.c_str())));

	// Vector<UserToken>
	jfieldID tokenFieldId = env->GetFieldID(cls, "token", "Ljava/util/Vector;");
	jobject tokenVector = env->GetObjectField(jLr, tokenFieldId);
	jclass tokenVctCls = env->GetObjectClass(tokenVector);
	jmethodID jmVectorAdd = env->GetMethodID(tokenVctCls, "add", "(Ljava/lang/Object;)Z");

	// UserToken
	jclass tokenRetCls = env->FindClass("com/tencent/ysdk/module/user/UserToken");
	jmethodID trConstructor = env->GetMethodID(tokenRetCls, "<init>", "()V");
	jfieldID trTypeFieldId = env->GetFieldID(tokenRetCls, "type", "I");
	jfieldID trValueFieldId = env->GetFieldID(tokenRetCls, "value", "Ljava/lang/String;");
	jfieldID trExpFieldId = env->GetFieldID(tokenRetCls, "expiration", "J");

	for (int i = 0; i < lr.token.size(); i++) {
		jobject loginRet = env->NewObject(tokenRetCls, trConstructor);
		env->SetIntField(loginRet, trTypeFieldId, lr.token.at(i).type);
		jstring jValue = c2j(env, (unsigned char*) lr.token.at(i).value.c_str(), strlen((char *) lr.token.at(i).value.c_str()));
		env->SetObjectField(loginRet, trValueFieldId, jValue);
		env->SetLongField(loginRet, trExpFieldId, lr.token.at(i).expiration);
		env->CallBooleanMethod(tokenVector, jmVectorAdd, loginRet);
		env->DeleteLocalRef(loginRet);
		env->DeleteLocalRef(jValue);
	}

	env->SetObjectField(jLr, tokenFieldId, tokenVector);

	env->DeleteLocalRef(cls);
	env->DeleteLocalRef(tokenVector);
	env->DeleteLocalRef(tokenVctCls);
	env->DeleteLocalRef(tokenRetCls);

	return platform;
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getPf
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getPf(
	JNIEnv * env, jclass) {
    jstring js;
    const std::string pf = YSDKApi::getInstance()->getPf();
    js = env->NewStringUTF(pf.c_str());
	return js;
}
/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    getPfKey
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_getPfKey
		(JNIEnv *env, jclass){
	const std::string PfKey = YSDKApi::getInstance()->getPfKey();
	return env->NewStringUTF(PfKey.c_str());
}


/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    switchUser
 * Signature: (Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_switchUser
		(JNIEnv *env, jclass, jboolean switchToLaunchUser){
	return YSDKApi::getInstance()->switchUser((bool)switchToLaunchUser);
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    queryUserInfo
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_queryUserInfo
		(JNIEnv *env, jclass, jint jPlatform){
    LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_queryUserInfo %d",jPlatform);
	YSDKApi::getInstance()->queryUserInfo((ePlatform) jPlatform);
}

/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    reportEvent
 * Signature: (Ljava/lang/String;Ljava/util/HashMap;Z)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_reportEvent
        (JNIEnv * env, jclass, jstring jName, jobject jparams, jboolean jIsRealTime){
	LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_reportEvent %s","");
    std::string sName = j2c(env, jName);
    jclass clsHashMap = env->FindClass("java/util/HashMap");

    jmethodID jEntrySetMethod = env->GetMethodID(clsHashMap, "entrySet", "()Ljava/util/Set;");
    jobject jEntrySet = env->CallObjectMethod(jparams, jEntrySetMethod);
    jclass jEntrySetClass = env->GetObjectClass(jEntrySet);
    jmethodID jIteratorMethod = env->GetMethodID(jEntrySetClass, "iterator", "()Ljava/util/Iterator;");
    jobject jIterator = env->CallObjectMethod(jEntrySet, jIteratorMethod);

    jclass jIteratorClass = env->GetObjectClass(jIterator);
    jmethodID jHasNextMethod = env->GetMethodID(jIteratorClass, "hasNext", "()Z");
    jmethodID jNextMethod = env->GetMethodID(jIteratorClass, "next", "()Ljava/lang/Object;");

    std::vector<KVPair> cEventList;

    while(env->CallBooleanMethod(jIterator, jHasNextMethod)){
        jobject jMap = env->CallObjectMethod(jIterator, jNextMethod);
        jclass jMapClass = env->GetObjectClass(jMap);
        jmethodID jGetKeyMethod = env->GetMethodID(jMapClass, "getKey", "()Ljava/lang/Object;");
        jmethodID jGetValueMethod = env->GetMethodID(jMapClass, "getValue", "()Ljava/lang/Object;");
        std::string cKeysInfo = j2c(env,(jstring) env->CallObjectMethod(jMap, jGetKeyMethod));
        std::string cValuesInfo = j2c(env,(jstring) env->CallObjectMethod(jMap, jGetValueMethod));

        KVPair cKVPair = {cKeysInfo,cValuesInfo};
        cEventList.push_back(cKVPair);
		env->DeleteLocalRef(jMap);
		env->DeleteLocalRef(jMapClass);
    }
    YSDKApi::getInstance()->reportEvent((unsigned char*) sName.c_str(), cEventList,(bool)jIsRealTime);

	env->DeleteLocalRef(clsHashMap);
	env->DeleteLocalRef(jEntrySet);
	env->DeleteLocalRef(jEntrySetClass);
	env->DeleteLocalRef(jIterator);
	env->DeleteLocalRef(jIteratorClass);
}


class GlobalPayListener: public YSDKPayListener {
public:
	virtual void OnPayNotify(PayRet& ret){
		LOGD("OnPayNotify:ret:%d\n flag:%d\n msg:%s\n platform:%d",ret.ret, ret.flag,ret.msg.c_str(),ret.platform);
		LOGD("OnPayNotify:realSaveNum:%d\n payChannel:%d\n payState:%d\n provideState:%d",ret.realSaveNum, ret.payChannel,ret.payState,ret.provideState);
		LOGD("OnPayNotify:extendInfo:%s\n payReserve1:%s\n payReserve2:%s\n payReserve3:%s",ret.extendInfo.c_str(), ret.payReserve1.c_str(),ret.payReserve2.c_str(),ret.payReserve3.c_str());
		LOGD("OnPayNotify:ysdkExtInfo:%s",ret.ysdkExtInfo.c_str());

		std::ostringstream result;
		std::string desc = "";
		result << "ret:" << ret.ret << "\n";
		result << "flag:" << ret.flag << "\n";
		result << "msg:" << ret.msg.c_str() << "\n";
		if(RET_SUCC == ret.ret){
			//支付流程成功
			switch (ret.payState){
				//支付成功
				case PAYSTATE_PAYSUCC:
					//建议查询余额
					result << "realSaveNum:" << ret.realSaveNum << "\n";
					result << "payChannel:" << ret.payChannel << "\n";
					result << "provideState:" << ret.provideState << "\n";
					result << "extendInfo:" << ret.extendInfo.c_str() << "\n";
					desc = "建议查询余额";
					result << desc.c_str() << "\n";
					break;
					//取消支付
				case PAYSTATE_PAYCANCEL:
					desc = "用户取消支付";
					result << desc.c_str() << "\n";
					break;
					//支付结果未知
				case PAYSTATE_PAYUNKOWN:
					desc = "用户支付结果未知，建议查询余额：";
					result << desc.c_str() << "\n";
					break;
					//支付失败
				case PAYSTATE_PAYERROR:
					desc = "支付异常";
					result << desc.c_str() << "\n";
					break;
			}
		}else{
			switch (ret.flag){
				case eFlag_LocalTokenInvalid:
					//用户取消支付
					desc =  "用户登录态失效，请重新登录";
					result << desc.c_str() << "\n";
					break;
				case eFlag_Pay_User_Cancle:
					//用户取消支付
					desc =  "用户取消支付";
					result << desc.c_str() << "\n";
					break;
				case eFlag_Pay_Param_Error:
					desc =  "支付失败,参数错误";
					result << desc.c_str() << "\n";
					break;
				case eFlag_Error:
				default:
					desc =  "支付异常";
					result << desc.c_str() << "\n";
					break;
			}
		}
		sendResult(result.str());
	}

	virtual ~GlobalPayListener() {

	}
};
GlobalPayListener gTestPayListener;
/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    recharge
 * Signature: (Ljava/lang/String;Ljava/lang/String;Z[BILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_recharge
		(JNIEnv * env, jclass, jstring jzoneId, jstring jsaveValue, jboolean jisCanChange, jbyteArray jImgData, jint jLength,jstring jysdkExtInfo){
	LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_recharge %s","");

	// 把参数中的jstring全部转为 unsigned char *
	std::string s_zoneId = j2c(env, jzoneId);
	unsigned char * cZoneId = (unsigned char *) s_zoneId.c_str();

	std::string s_saveValue = j2c(env, jsaveValue);
	unsigned char * cSaveValue = (unsigned char *) s_saveValue.c_str();

	std::string s_ysdkExtInfo = j2c(env, jysdkExtInfo);
	unsigned char * cYsdkExtInfo = (unsigned char *) s_ysdkExtInfo.c_str();

	// 把jByteArray中的数据转为unsigned char array
	jboolean isCopy;
	jbyte * imgDataJb = env->GetByteArrayElements(jImgData, &isCopy);
	int imgDataLen = (int) jLength;
	unsigned char * imgData = new unsigned char[imgDataLen];
	memcpy(imgData, (unsigned char *) imgDataJb, imgDataLen);

	YSDKApi::getInstance()->recharge(cZoneId,cSaveValue,jisCanChange,imgData,imgDataLen,cYsdkExtInfo,&gTestPayListener);
}


/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    buyGoods
 * Signature: (Ljava/lang/String;Ljava/lang/String;[BILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_buyGoods
(JNIEnv * env, jclass, jstring jzoneId, jstring jGoodTokenUrl, jbyteArray jImgData, jint jLength, jstring jysdkExtInfo){
	LOGD("Java_com_tencent_tmgp_yybtestsdk_PlatformTest_buyGoods %s","");

	// 把参数中的jstring全部转为 unsigned char *
	std::string s_zoneId = j2c(env, jzoneId);
	unsigned char * cZoneId = (unsigned char *) s_zoneId.c_str();

	std::string s_goodtokenurl = j2c(env, jGoodTokenUrl);
	unsigned char *cGoodTokenUrl = (unsigned char *) s_goodtokenurl.c_str();

	std::string s_ysdkExtInfo = j2c(env, jysdkExtInfo);
	unsigned char * cYsdkExtInfo = (unsigned char *) s_ysdkExtInfo.c_str();

	// 把jByteArray中的数据转为unsigned char array
	jboolean isCopy;
	jbyte * imgDataJb = env->GetByteArrayElements(jImgData, &isCopy);
	int imgDataLen = (int) jLength;
	unsigned char * imgData = new unsigned char[imgDataLen];
	memcpy(imgData, (unsigned char *) imgDataJb, imgDataLen);

	YSDKApi::getInstance()->buyGoods(cZoneId, cGoodTokenUrl, imgData, imgDataLen, cYsdkExtInfo, &gTestPayListener);
}
/*
 * Class:     com_tencent_tmgp_yybtestsdk_PlatformTest
 * Method:    testNativeCrash
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_tencent_tmgp_yybtestsdk_PlatformTest_testNativeCrash
		(JNIEnv *, jclass){

	int a = 0;
	int b = 100 / a;
}