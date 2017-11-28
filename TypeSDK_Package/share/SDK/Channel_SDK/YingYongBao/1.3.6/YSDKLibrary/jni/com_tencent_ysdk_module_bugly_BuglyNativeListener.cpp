#include "CommonFiles/YSDKCommon.h"
#include "CommonFiles/YSDKApi.h"
#include "com_tencent_ysdk_module_bugly_BuglyNativeListener.h"
#include <android/log.h>


/*
 * Class:     com_tencent_ysdk_module_bugly_BuglyNativeListener
 * Method:    OnCrashExtMessageNotify
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtMessageNotify
  (JNIEnv * env , jclass jc) {
	 LOGD("Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtMessageNotify start%s", "");
	 std::string cMsg;
	 if (YSDKApi::getInstance()->getBuglyListener() != NULL) {
		 cMsg = YSDKApi::getInstance()->getBuglyListener()->OnCrashExtMessageNotify();
	 }
	 const char* c_s = cMsg.c_str();
	 LOGD("Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtMessageNotify end%s", "");
	 return env->NewStringUTF(c_s);
 }

/*
 * Class:     com_tencent_ysdk_module_bugly_BuglyNativeListener
 * Method:    OnCrashExtDataNotify
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtDataNotify
		(JNIEnv * env , jclass jc) {
	LOGD( "Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtDataNotify: start: %s","");
	unsigned char* extData = { 0 };
	if (YSDKApi::getInstance()->getBuglyListener() != NULL) {
		extData = YSDKApi::getInstance()->getBuglyListener()->OnCrashExtDataNotify();
	} else {
		LOGD( "Java_com_tencent_ysdk_module_bugly_BuglyNativeListener_OnCrashExtDataNotify: key: %s","");
	}

	int len = strlen( (char *)extData);
	jbyteArray jData = env->NewByteArray(len);
	env->SetByteArrayRegion(jData, 0, len, (jbyte *) extData);
	return jData;
}
