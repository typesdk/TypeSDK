#include "CommonFiles/YSDKCommon.h"
#include "CommonFiles/YSDKApi.h"
#include "com_tencent_ysdk_module_pay_PayNativeListener.h"
#include "CommonFiles/YSDKDefine.h"
#include <android/log.h>

/*
 * Class:     com_tencent_ysdk_module_pay_PayNativeListener
 * Method:    OnPayNotify
 * Signature: (Lcom/tencent/ysdk/module/pay/PayRet;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_ysdk_module_pay_PayNativeListener_OnPayNotify
		(JNIEnv * env, jclass, jobject jRet) {
	LOGD("Java_com_tencent_ysdk_module_pay_PayNativeListener_OnPayListener start%s", "");
	PayRet lr;
	jclass jPayRetClass = env->GetObjectClass(jRet);
	JniGetAndSetIntField(ret, "ret", jPayRetClass, jRet, lr);
	JniGetAndSetIntField(flag, "flag", jPayRetClass, jRet, lr);
	JniGetAndSetStringField(msg, "msg", jPayRetClass, jRet, lr);
	JniGetAndSetIntField(platform, "platform", jPayRetClass, jRet, lr);

	JniGetAndSetIntField(realSaveNum, "realSaveNum", jPayRetClass, jRet, lr);
	JniGetAndSetIntField(payChannel, "payChannel", jPayRetClass, jRet, lr);
	JniGetAndSetIntField(payState, "payState", jPayRetClass, jRet, lr);
	JniGetAndSetIntField(provideState, "provideState", jPayRetClass, jRet, lr);

	JniGetAndSetStringField(extendInfo, "extendInfo", jPayRetClass, jRet, lr);
	JniGetAndSetStringField(payReserve1, "payReserve1", jPayRetClass, jRet, lr);
	JniGetAndSetStringField(payReserve2, "payReserve2", jPayRetClass, jRet, lr);
	JniGetAndSetStringField(payReserve3, "payReserve3", jPayRetClass, jRet, lr);

	JniGetAndSetStringField(ysdkExtInfo, "ysdkExtInfo", jPayRetClass, jRet, lr);

	if (YSDKApi::getInstance()->getPayListener() != NULL) {
		LOGD("OnPayNotify getPayListener()->OnPayNotify start%s", "");
		YSDKApi::getInstance()->getPayListener()->OnPayNotify(lr);
		YSDKApi::getInstance()->clearPayListener();
		LOGD("OnPayNotify getPayListener()->OnPayNotify end%s", "");
	} else {
		LOGD("OnPayNotify getPayListener() is null%s", "");
	}
	env->DeleteLocalRef(jPayRetClass);
	LOGD("OnPayNotify end%s", "");
}
