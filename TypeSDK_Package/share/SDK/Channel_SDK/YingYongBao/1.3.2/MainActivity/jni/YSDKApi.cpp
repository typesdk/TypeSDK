#include "CommonFiles/YSDKApi.h"
#include "CommonFiles/YSDKUserListener.h"
#include "CommonFiles/YSDKBuglyListener.h"
#include "CommonFiles/YSDKDefine.h"

#include <string>
#include <android/log.h>

static jclass s_YSDKApiClass;

YSDKApi::YSDKApi():mBuglyListener(NULL),mUserListener(NULL),mPayListener(NULL),needDelayLoginNotify(false), needDelayWakeupNotify(false){
	mPVM = NULL;
}
YSDKApi::~YSDKApi() {
}
//-----------------------------------------------------------------------------
void YSDKApi::init(JavaVM* pVM) {
	mPVM = pVM;
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jclass cls = env->FindClass("com/tencent/ysdk/api/YSDKApi");
	s_YSDKApiClass = (jclass) env->NewGlobalRef(cls);
	env->DeleteLocalRef(cls);
}

void YSDKApi::setVM(JavaVM* pVM) {
	mPVM = pVM;
}

JavaVM* YSDKApi::getVm() {
	return mPVM;
}

void YSDKApi::setLoginRet(UserLoginRet& lr) {
	this->mLastLoginRet = lr;
	needDelayLoginNotify = true;
	LOGD("YSDKApi needDelayLoginNotify %d", needDelayLoginNotify);
}

UserLoginRet& YSDKApi::getLoginRet() {
	return this->mLastLoginRet;
}


void YSDKApi::setWakeup(WakeupRet& wakeup) {

	this->mLastWakeup = wakeup;
	needDelayWakeupNotify = true;
	LOGD("YSDKApi needDelayWakeupNotify %d", needDelayWakeupNotify);
}

WakeupRet& YSDKApi::getWakeup() {
	return mLastWakeup;
}

YSDKApi * YSDKApi::mYSDKApiInstance;
YSDKApi* YSDKApi::getInstance() {
	if (mYSDKApiInstance == NULL) {
		mYSDKApiInstance = new YSDKApi();
	}
	return mYSDKApiInstance;
}


void YSDKApi::setBuglyListener(YSDKBuglyListener* pListener){
	if (pListener == NULL) {
		LOGI("pListener is NULL%s", "");
		return;
	}
	mBuglyListener = pListener;
}


YSDKBuglyListener * YSDKApi::getBuglyListener() const {
	return mBuglyListener;
}

void YSDKApi::setUserListener(YSDKUserListener* pListener){

	LOGD("YSDKApi::setUserListener needDelayWakeupNotify %d", needDelayWakeupNotify);
	LOGD("YSDKApi::setUserListener needDelayLoginNotify %d", needDelayLoginNotify);

	if (pListener == NULL) {
		LOGI("pListener is NULL%s", "");
		return;
	}
	mUserListener = pListener;

	if (needDelayWakeupNotify) {
		LOGD(" wakeup delay notify openid:%s", mLastWakeup.open_id.c_str());
		mUserListener->OnWakeupNotify(mLastWakeup);
		needDelayWakeupNotify = false;
	} else if (needDelayLoginNotify) {
		for (int i = 0; i < mLastLoginRet.token.size(); i++) {
			LOGD("login delay notify type:%d; value:%s",mLastLoginRet.token.at(i).type, mLastLoginRet.token.at(i).value.c_str());
		}
		mUserListener->OnLoginNotify(mLastLoginRet);
		needDelayLoginNotify = false;
	}
}

YSDKUserListener * YSDKApi::getUserListener() const {
	return mUserListener;
}

void YSDKApi::clearPayListener(){
	mPayListener = NULL;
}
YSDKPayListener * YSDKApi::getPayListener() const {
	return mPayListener;
}

const std::string YSDKApi::getVersion() {
	LOGD(" YSDKApi::getVersion() start %s","");
	JNIEnv *env;

	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID jGetVersionMethod = env->GetStaticMethodID(s_YSDKApiClass,
			"getVersion", "()Ljava/lang/String;");
	jstring jVersion = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass,
			jGetVersionMethod);
	jboolean isCopy;
	const char* cVersion = env->GetStringUTFChars(jVersion, &isCopy);
	std::string cVersionStr = cVersion;
	env->ReleaseStringUTFChars(jVersion, cVersion);
	env->DeleteLocalRef(jVersion);
	return cVersionStr;
}

const std::string YSDKApi::getChannelId() {
	LOGD(" YSDKApi::getChannelId() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID jGetChannelIdmethod = env->GetStaticMethodID(s_YSDKApiClass,
			"getChannelId", "()Ljava/lang/String;");

	jstring jChannelId = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass,
			jGetChannelIdmethod);

	jboolean isCopy;
	const char* cChannel = env->GetStringUTFChars(jChannelId, &isCopy);
	std::string cChannelStr = cChannel;
	env->ReleaseStringUTFChars(jChannelId, cChannel);
	env->DeleteLocalRef(jChannelId);
	return cChannelStr;
}

const std::string YSDKApi::getRegisterChannelId() {
	LOGD(" YSDKApi::getRegisterChannelId() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID jGetChannelIdmethod = env->GetStaticMethodID(s_YSDKApiClass,"getRegisterChannelId", "()Ljava/lang/String;");

	jstring jChannelId = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass,jGetChannelIdmethod);
	jboolean isCopy;
	const char* cChannel = env->GetStringUTFChars(jChannelId, &isCopy);
	std::string cChannelStr = cChannel;
	env->ReleaseStringUTFChars(jChannelId, cChannel);
	env->DeleteLocalRef(jChannelId);
	return cChannelStr;
}


bool YSDKApi::isPlatformInstalled(ePlatform platform){
	LOGD(" YSDKApi::isPlatformInstalled() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jclass jCommonClass = env->FindClass("com/tencent/ysdk/framework/common/ePlatform");
	jmethodID jGetEnumMethod = env->GetStaticMethodID(jCommonClass, "getEnum","(I)Lcom/tencent/ysdk/framework/common/ePlatform;");
	jobject jEnumObj = env->CallStaticObjectMethod(jCommonClass, jGetEnumMethod,(int) platform);

	jboolean result;
	jmethodID isInstalled;
	isInstalled = env->GetStaticMethodID(s_YSDKApiClass,"isPlatformInstalled", "(Lcom/tencent/ysdk/framework/common/ePlatform;)Z");
	result = env->CallStaticBooleanMethod(s_YSDKApiClass, isInstalled,jEnumObj);

	env->DeleteLocalRef(jCommonClass);
	env->DeleteLocalRef(jEnumObj);
	return result;
}

const std::string  YSDKApi::getPlatformAppVersion(ePlatform platform){
	LOGD(" YSDKApi::getPlatformAppVersion() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);

	jclass jCommonClass = env->FindClass("com/tencent/ysdk/framework/common/ePlatform");
	jmethodID jGetEnumMethod = env->GetStaticMethodID(jCommonClass, "getEnum","(I)Lcom/tencent/ysdk/framework/common/ePlatform;");
	jobject jEnumObj = env->CallStaticObjectMethod(jCommonClass, jGetEnumMethod,(int) platform);
	jmethodID method = env->GetStaticMethodID(s_YSDKApiClass, "getPlatformAppVersion", "(Lcom/tencent/ysdk/framework/common/ePlatform;)Ljava/lang/String;");
	jstring jAPPVersion = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass, method,jEnumObj);

	jboolean isCopy;
	const char* cAPPVersion = env->GetStringUTFChars(jAPPVersion, &isCopy);
	std::string cAPPVersionStr = cAPPVersion;
	env->ReleaseStringUTFChars(jAPPVersion, cAPPVersion);
	env->DeleteLocalRef(jAPPVersion);
	env->DeleteLocalRef(jCommonClass);
	env->DeleteLocalRef(jEnumObj);
	return cAPPVersionStr;
}


void YSDKApi::login(ePlatform platform){

	LOGD("YSDKApi::login platform:%d", (int)platform);
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);

	jclass jCommonClass = env->FindClass("com/tencent/ysdk/framework/common/ePlatform");
	jmethodID jGetEnumMethod = env->GetStaticMethodID(jCommonClass, "getEnum","(I)Lcom/tencent/ysdk/framework/common/ePlatform;");
	jobject jEnumObj = env->CallStaticObjectMethod(jCommonClass, jGetEnumMethod,(int) platform);
	jmethodID jlogin = env->GetStaticMethodID(s_YSDKApiClass, "login", "(Lcom/tencent/ysdk/framework/common/ePlatform;)V");
	env->CallStaticVoidMethod(s_YSDKApiClass, jlogin, jEnumObj);
	env->DeleteLocalRef(jCommonClass);
	env->DeleteLocalRef(jEnumObj);
}


void YSDKApi::logout(){
	LOGD("YSDKApi::logout:%s", "");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID WGLogout = env->GetStaticMethodID(s_YSDKApiClass, "logout","()V");
	env->CallStaticVoidMethod(s_YSDKApiClass, WGLogout);
}

int YSDKApi::getLoginRecord(UserLoginRet& userLoginRet){
	LOGD(" YSDKApi::getLoginRecord() start %s","");
	JNIEnv* env;
	mPVM->AttachCurrentThread(&env, NULL);

	jboolean isCopy;
	jmethodID getLoginRecord = env->GetStaticMethodID(s_YSDKApiClass,
														"getLoginRecord", "(Lcom/tencent/ysdk/module/user/UserLoginRet;)I");
	jclass cls =  env->FindClass("com/tencent/ysdk/module/user/UserLoginRet");
	jclass s_LoginRetClass = (jclass) env->NewGlobalRef(cls);
	env->DeleteLocalRef(cls);

	jmethodID lrConstruct = env->GetMethodID(s_LoginRetClass, "<init>", "()V");
	jobject jLoginRet = env->NewObject(s_LoginRetClass, lrConstruct);
	env->CallStaticIntMethod(s_YSDKApiClass, getLoginRecord, jLoginRet);

	JniGetAndSetIntField(ret, "ret", s_LoginRetClass, jLoginRet, userLoginRet);
	JniGetAndSetIntField(flag, "flag", s_LoginRetClass, jLoginRet, userLoginRet);
	JniGetAndSetStringField(msg, "msg", s_LoginRetClass, jLoginRet, userLoginRet);
	JniGetAndSetIntField(platform, "platform", s_LoginRetClass, jLoginRet, userLoginRet);
	JniGetAndSetStringField(open_id, "open_id", s_LoginRetClass, jLoginRet, userLoginRet);

	jmethodID jGetUserTypeMethod = env->GetMethodID(s_LoginRetClass,"getUserType","()I");
	jint userType = env->CallIntMethod(jLoginRet,jGetUserTypeMethod);
	userLoginRet.user_type = (int) userType;

	JniGetAndSetStringField(pf, "pf", s_LoginRetClass, jLoginRet, userLoginRet);
	JniGetAndSetStringField(pfKey, "pf_key", s_LoginRetClass, jLoginRet, userLoginRet);

	// Vector
	jfieldID jVectorFieldId = env->GetFieldID(s_LoginRetClass, "token","Ljava/util/Vector;");
	jobject jTokenVectorObject = env->GetObjectField(jLoginRet, jVectorFieldId);
	jclass jVectorClass = env->GetObjectClass(jTokenVectorObject);

	jmethodID jVectorSizeMethod = env->GetMethodID(jVectorClass, "size", "()I");
	jmethodID jVectorGetMethod = env->GetMethodID(jVectorClass, "get", "(I)Ljava/lang/Object;");
	jint jLength = env->CallIntMethod(jTokenVectorObject, jVectorSizeMethod);

	for (int i = 0; i < jLength; i++) {
		UserToken cTokenRet;
		jobject jTokenRetObject = env->CallObjectMethod(jTokenVectorObject,
														jVectorGetMethod, i);
		jclass jTokenRetClass = env->GetObjectClass(jTokenRetObject);

		JniGetAndSetIntField(type, "type", jTokenRetClass, jTokenRetObject,cTokenRet);
		JniGetAndSetStringField(value, "value", jTokenRetClass, jTokenRetObject,cTokenRet);
		JniGetAndSetLongField(expiration, "expiration", jTokenRetClass, jTokenRetObject, cTokenRet)
		userLoginRet.token.push_back(cTokenRet);
		env->DeleteLocalRef(jTokenRetObject);
		env->DeleteLocalRef(jTokenRetClass);
	}
	env->DeleteLocalRef(jLoginRet);
	env->DeleteLocalRef(jTokenVectorObject);
	env->DeleteLocalRef(jVectorClass);
	return userLoginRet.platform;
}

const std::string YSDKApi::getPf(){
	LOGD(" YSDKApi::getPf() start %s","");
    JNIEnv *env;
    mPVM->AttachCurrentThread(&env, NULL);
    jmethodID method= env->GetStaticMethodID(s_YSDKApiClass, "getPf",
                                             "()Ljava/lang/String;");
    jstring jPf = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass,
                                                        method);

    jboolean isCopy;
    const char* cPf = env->GetStringUTFChars(jPf, &isCopy);
    std::string cPfStr = cPf;
    env->ReleaseStringUTFChars(jPf, cPf);
    env->DeleteLocalRef(jPf);
    LOGD(" YSDKApi::getPf() end %s","");
    return cPfStr;
}

const std::string YSDKApi::getPfKey(){
	LOGD(" YSDKApi::getPfKey() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID method;
	method = env->GetStaticMethodID(s_YSDKApiClass, "getPfKey","()Ljava/lang/String;");
	jstring jPfKey = (jstring) env->CallStaticObjectMethod(s_YSDKApiClass,method);

	jboolean isCopy;
	const char* cPfKey = env->GetStringUTFChars(jPfKey, &isCopy);
	std::string cPfKeyStr = cPfKey;
	env->ReleaseStringUTFChars(jPfKey, cPfKey);
	env->DeleteLocalRef(jPfKey);
	return cPfKeyStr;
}

bool YSDKApi::switchUser(bool switchToLaunchUser){
	LOGD(" YSDKApi::switchUser() start %s","");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);
	jmethodID method = env->GetStaticMethodID(s_YSDKApiClass,"switchUser", "(Z)Z");
	return env->CallStaticBooleanMethod(s_YSDKApiClass, method, switchToLaunchUser);
}

void YSDKApi::queryUserInfo(ePlatform platform){
	LOGD("YSDKApi::queryUserInfo %s", "");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);

	jclass jCommonClass = env->FindClass("com/tencent/ysdk/framework/common/ePlatform");
	jmethodID jGetEnumMethod = env->GetStaticMethodID(jCommonClass, "getEnum","(I)Lcom/tencent/ysdk/framework/common/ePlatform;");
	jobject jEnumObj = env->CallStaticObjectMethod(jCommonClass, jGetEnumMethod,(int) platform);

	jmethodID WGQueryWXMyInfo = env->GetStaticMethodID(s_YSDKApiClass,"queryUserInfo", "(Lcom/tencent/ysdk/framework/common/ePlatform;)V");
	env->CallStaticVoidMethod(s_YSDKApiClass,WGQueryWXMyInfo,jEnumObj);

	env->DeleteLocalRef(jCommonClass);
	env->DeleteLocalRef(jEnumObj);

}

void YSDKApi::reportEvent(unsigned char* cName, std::vector<KVPair>& cEventList, bool isRealTime){
    LOGD("YSDKApi::reportEvent %s", "");
    JNIEnv *env;
    mPVM->AttachCurrentThread(&env, NULL);
    jmethodID method = env->GetStaticMethodID(s_YSDKApiClass,
                                              "reportEvent", "(Ljava/lang/String;Ljava/util/HashMap;Z)V");
    jstring jName = env->NewStringUTF((char const *) cName);
    jclass jHashMapClass = env->FindClass("java/util/HashMap");
    jmethodID jInitMethod = env->GetMethodID(jHashMapClass, "<init>", "()V");
    jmethodID jPutMethod = env->GetMethodID(jHashMapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    jobject jparams = env->NewObject(jHashMapClass, jInitMethod);

    for (int i = 0; i < cEventList.size(); i++) {
        jstring jKey = env->NewStringUTF(
                (const char *) cEventList.at(i).key.c_str());
        jstring jValue = env->NewStringUTF(
                (const char *) cEventList.at(i).value.c_str());
        env->CallObjectMethod(jparams, jPutMethod, jKey,jValue);
        env->DeleteLocalRef(jKey);
        env->DeleteLocalRef(jValue);
    }
    env->CallStaticVoidMethod(s_YSDKApiClass, method, jName, jparams,isRealTime);
    env->DeleteLocalRef(jHashMapClass);
    env->DeleteLocalRef(jName);
    env->DeleteLocalRef(jparams);
}

void YSDKApi::recharge(unsigned char* cZoneId, unsigned char* cSaveValue, bool cIsCanChange,unsigned char* cResData,int cReslength, unsigned char* cYSDKInfo,YSDKPayListener* pListener){
	LOGD("YSDKApi::recharge %s", "");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);

	if (pListener == NULL) {
		LOGI("pListener is NULL%s", "");
		return;
	}
	mPayListener = pListener;

	jstring jZoneId = env->NewStringUTF((char const *) cZoneId);
	jstring jSaveValue = env->NewStringUTF((char const *) cSaveValue);
	jstring jYSDKInfo = env->NewStringUTF((char const *) cYSDKInfo);
	jbyteArray jImgData = env->NewByteArray(cReslength);
	env->SetByteArrayRegion(jImgData, 0, cReslength,(jbyte *) cResData);

    // 创建PayNativeListener的对象
    jclass jPayListenerCls =  env->FindClass("com/tencent/ysdk/module/pay/PayInnerNativeListener");
    jmethodID jPayListenerInit = env->GetMethodID(jPayListenerCls, "<init>", "()V");
    jobject jPayListener = env->NewObject(jPayListenerCls, jPayListenerInit);
	jmethodID jRecharge = env->GetStaticMethodID(s_YSDKApiClass,"recharge", "(Ljava/lang/String;Ljava/lang/String;Z[BLjava/lang/String;Lcom/tencent/ysdk/module/pay/PayListener;)V");
	env->CallStaticVoidMethod(s_YSDKApiClass, jRecharge, jZoneId, jSaveValue, cIsCanChange, jImgData, jYSDKInfo,jPayListener);

    //资源回收
	env->DeleteLocalRef(jZoneId);
	env->DeleteLocalRef(jSaveValue);
	env->DeleteLocalRef(jYSDKInfo);
	env->DeleteLocalRef(jImgData);
	env->DeleteLocalRef(jPayListenerCls);
	env->DeleteLocalRef(jPayListener);
}

void YSDKApi::buyGoods(unsigned char* cZoneId, unsigned char* cGoodsTokenUrl, unsigned char* cResData,int cReslength, unsigned char* cYSDKInfo,YSDKPayListener* pListener){
	LOGD("YSDKApi::recharge %s", "");
	JNIEnv *env;
	mPVM->AttachCurrentThread(&env, NULL);

	if (pListener == NULL) {
		LOGI("pListener is NULL%s", "");
		return;
	}
	mPayListener = pListener;

	jstring jZoneId = env->NewStringUTF((char const *) cZoneId);
	jstring jGoodsTokenUrl = env->NewStringUTF((char const *) cGoodsTokenUrl);
	jstring jYSDKInfo = env->NewStringUTF((char const *) cYSDKInfo);
	jbyteArray jImgData = env->NewByteArray(cReslength);
	env->SetByteArrayRegion(jImgData, 0, cReslength,(jbyte *) cResData);

	// 创建PayNativeListener的对象
	jclass jPayListenerCls =  env->FindClass("com/tencent/ysdk/module/pay/PayInnerNativeListener");
	jmethodID jPayListenerInit = env->GetMethodID(jPayListenerCls, "<init>", "()V");
	jobject jPayListener = env->NewObject(jPayListenerCls, jPayListenerInit);
	jmethodID jBuyGoods = env->GetStaticMethodID(s_YSDKApiClass, "buyGoods", "(Ljava/lang/String;Ljava/lang/String;[BLjava/lang/String;Lcom/tencent/ysdk/module/pay/PayListener;)V");
	env->CallStaticVoidMethod(s_YSDKApiClass, jBuyGoods, jZoneId, jGoodsTokenUrl, jImgData, jYSDKInfo, jPayListener);

	//资源回收
	env->DeleteLocalRef(jZoneId);
	env->DeleteLocalRef(jGoodsTokenUrl);
	env->DeleteLocalRef(jYSDKInfo);
	env->DeleteLocalRef(jImgData);
	env->DeleteLocalRef(jPayListenerCls);
	env->DeleteLocalRef(jPayListener);
}
