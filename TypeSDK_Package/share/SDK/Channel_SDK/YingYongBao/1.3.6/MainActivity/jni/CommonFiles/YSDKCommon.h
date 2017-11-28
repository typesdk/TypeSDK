
#ifndef YSDK_COMMON_H
#define YSDK_COMMON_H
#include <string>
#include <vector>
#include "YSDKDefine.h"

#include <android/log.h>
#include <jni.h>

#define LOGI(fmt, ...)   __android_log_print(ANDROID_LOG_INFO, "YSDK  cpp", fmt, __VA_ARGS__)
#define LOGD(fmt, ...)   __android_log_print(ANDROID_LOG_DEBUG, "YSDK  cpp", fmt, __VA_ARGS__)
#define LOGW(fmt, ...)   __android_log_print(ANDROID_LOG_WARN, "YSDK  cpp", fmt, __VA_ARGS__)
#define LOGE(fmt, ...)   __android_log_print(ANDROID_LOG_ERROR, "YSDK  cpp", fmt, __VA_ARGS__)

// 获取某个java对象的值(String), 再赋值给本地对象
#define JniGetAndSetStringField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "Ljava/lang/String;"); \
jstring j##fieldName##FieldValue = (jstring) (env->GetObjectField(jOriginObj, j##fieldName##FieldId)); \
if (j##fieldName##FieldValue == NULL) {\
    targetObj.fieldName = ""; \
} else { \
    char const * c##fieldName##FieldValue = env->GetStringUTFChars(j##fieldName##FieldValue, NULL); \
    targetObj.fieldName = c##fieldName##FieldValue; \
    LOGD("cField:%s,Value: %s", fieldNameStr, c##fieldName##FieldValue); \
    env->ReleaseStringUTFChars(j##fieldName##FieldValue, c##fieldName##FieldValue); \
} \
env->DeleteLocalRef(j##fieldName##FieldValue);

#define JniGetAndSetIntField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "I"); \
targetObj.fieldName = (int) (env->GetIntField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(long), 再赋值给本地对象
#define JniGetAndSetLongField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "J"); \
targetObj.fieldName = (int) (env->GetLongField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(float), 再赋值给本地对象
#define JniGetAndSetFloatField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "F"); \
targetObj.fieldName = (int) (env->GetFloatField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(boolean), 再赋值给本地对象
#define JniGetAndSetBooleanField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "Z"); \
targetObj.fieldName = (int) (env->GetBooleanField(jOriginObj, j##fieldName##FieldId));

// 获取某个java对象的值(double), 再赋值给本地对象
#define JniGetAndSetDoubleField(fieldName, fieldNameStr, jOriginClass, jOriginObj, targetObj) \
jfieldID j##fieldName##FieldId = env->GetFieldID(jOriginClass, fieldNameStr, "D"); \
targetObj.fieldName = (double) (env->GetDoubleField(jOriginObj, j##fieldName##FieldId));
#endif
