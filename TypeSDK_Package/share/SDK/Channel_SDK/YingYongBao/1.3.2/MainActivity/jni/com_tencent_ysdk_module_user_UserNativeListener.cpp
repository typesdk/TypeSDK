#include "CommonFiles/YSDKCommon.h"
#include "CommonFiles/YSDKApi.h"
#include "com_tencent_ysdk_module_user_UserNativeListener.h"
#include "CommonFiles/YSDKDefine.h"
#include <android/log.h>


/*
 * Class:     com_tencent_ysdk_module_user_UserNativeListener
 * Method:    OnLoginNotify
 * Signature: (Lcom/tencent/ysdk/module/user/UserLoginRet;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify
(JNIEnv * env, jclass jc, jobject jLoginRet) {
     LOGD("Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify start%s", "");
     UserLoginRet lr;
     jclass jLoginRetClass = env->GetObjectClass(jLoginRet);
     jboolean isCopy;
     JniGetAndSetIntField(ret, "ret", jLoginRetClass, jLoginRet, lr);
     JniGetAndSetIntField(flag, "flag", jLoginRetClass, jLoginRet, lr);
     JniGetAndSetStringField(msg, "msg", jLoginRetClass, jLoginRet, lr);
     JniGetAndSetIntField(platform, "platform", jLoginRetClass, jLoginRet, lr);
     JniGetAndSetStringField(open_id, "open_id", jLoginRetClass, jLoginRet, lr);

     jmethodID jGetUserTypeMethod = env->GetMethodID(jLoginRetClass,"getUserType","()I");
     jint userType = env->CallIntMethod(jLoginRet,jGetUserTypeMethod);
     lr.user_type = (int) userType;

     jfieldID vctId = env->GetFieldID(jLoginRetClass, "token", "Ljava/util/Vector;");
     jobject tokenList = env->GetObjectField(jLoginRet, vctId);
     jclass tokenRetVectorClass = env->GetObjectClass(tokenList);

     jmethodID vectorSizeM = env->GetMethodID(tokenRetVectorClass, "size", "()I");
     jmethodID vectorGetM = env->GetMethodID(tokenRetVectorClass, "get", "(I)Ljava/lang/Object;");
     jint len = env->CallIntMethod(tokenList, vectorSizeM);

     LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify: tokenListSize: %d", len);
     for (int i = 0; i < len; i++) {
          UserToken cToken;
          jobject jTokenRetObject = env->CallObjectMethod(tokenList, vectorGetM, i);
          jclass jTokenRetClass = env->GetObjectClass(jTokenRetObject);

          JniGetAndSetIntField(type, "type", jTokenRetClass, jTokenRetObject, cToken);
          JniGetAndSetStringField(value, "value", jTokenRetClass, jTokenRetObject, cToken);
          JniGetAndSetLongField(expiration, "expiration", jTokenRetClass, jTokenRetObject, cToken);

          LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify: type: %d", cToken.type);
          LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify: value: %s", cToken.value.c_str());
          LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnLoginNotify: expiration: %lld", cToken.expiration);

          lr.token.push_back(cToken);

          env->DeleteLocalRef(jTokenRetObject);
          env->DeleteLocalRef(jTokenRetClass);
     }

     JniGetAndSetStringField(pf, "pf", jLoginRetClass, jLoginRet, lr);
     JniGetAndSetStringField(pfKey, "pf_key", jLoginRetClass, jLoginRet, lr);

     if (YSDKApi::getInstance()->getUserListener() != NULL) {
          LOGD("OnLoginNotify getUserListener()->OnLoginNotify start%s", "");
          YSDKApi::getInstance()->getUserListener()->OnLoginNotify(lr);
          LOGD("OnLoginNotify getUserListener()->OnLoginNotify end%s", "");
     } else {
          LOGD("OnLoginNotify getInstance()->setLoginRet start%s", "");
          YSDKApi::getInstance()->setLoginRet(lr);
          LOGD("OnLoginNotify getInstance()->setLoginRet end%s", "");
     }

     env->DeleteLocalRef(jLoginRetClass);
     env->DeleteLocalRef(jLoginRet);
     LOGD("OnLoginNotify end%s", "");
}




/*
 * Class:     com_tencent_ysdk_module_user_UserNativeListener
 * Method:    OnWakeupNotify
 * Signature: (Lcom/tencent/ysdk/module/user/WakeupRet;)V
 */
JNIEXPORT void JNICALL Java_com_tencent_ysdk_module_user_UserNativeListener_OnWakeupNotify(JNIEnv *env, jclass jc,
        jobject jWakeupRetObject) {
     LOGD("OnWakeupNotify start%s", "");
     jclass jWakeupRetClass = env->GetObjectClass(jWakeupRetObject);
     WakeupRet wr;
     jboolean isCopy;

     JniGetAndSetIntField(flag, "flag", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetStringField(msg, "msg", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetIntField(platform, "platform", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetStringField(open_id, "open_id", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetStringField(media_tag_name, "media_tag_name", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetStringField(messageExt, "message_ext", jWakeupRetClass, jWakeupRetObject, wr);
     JniGetAndSetStringField(country, "country", jWakeupRetClass, jWakeupRetObject, wr);

     jfieldID jVectorMethodId = env->GetFieldID(jWakeupRetClass, "ext_info", "Ljava/util/Vector;");
     jobject extInfoVector = env->GetObjectField(jWakeupRetObject, jVectorMethodId);
     jclass extInfoVectorClass = env->GetObjectClass(extInfoVector);

     jmethodID vectorSizeM = env->GetMethodID(extInfoVectorClass, "size", "()I");
     jmethodID vectorGetM = env->GetMethodID(extInfoVectorClass, "get", "(I)Ljava/lang/Object;");
     jint len = env->CallIntMethod(extInfoVector, vectorSizeM);


     LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnWakeupNotify: extInfoSize: %s", "");
     for (int i = 0; i < len; i++) {
          KVPair cKVPair;
          jobject jKVPair = env->CallObjectMethod(extInfoVector, vectorGetM, i);
          jclass jKVPairClass = env->GetObjectClass(jKVPair);

          JniGetAndSetStringField(key, "key", jKVPairClass, jKVPair, cKVPair);
          JniGetAndSetStringField(value, "value", jKVPairClass, jKVPair, cKVPair);

          LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnWakeupNotify: key: %s", cKVPair.key.c_str());
          LOGD( "Java_com_tencent_ysdk_module_user_UserNativeListener_OnWakeupNotify: value: %s", cKVPair.value.c_str());

          wr.extInfo.push_back(cKVPair);

          env->DeleteLocalRef(jKVPair);
          env->DeleteLocalRef(jKVPairClass);
     }
     env->DeleteLocalRef(extInfoVector);
     env->DeleteLocalRef(extInfoVectorClass);


     if (YSDKApi::getInstance()->getUserListener() != NULL) {
          LOGD("OnWakeupNotify getUserListener()->OnWakeupNotify start%s", "");
          YSDKApi::getInstance()->getUserListener()->OnWakeupNotify(wr);
          LOGD("OnWakeupNotify getUserListener()->OnWakeupNotify start%s", "");
     } else {
          LOGD("OnWakeupNotify getInstance()->setWakeup start%s", "");
          YSDKApi::getInstance()->setWakeup(wr);
          LOGD("OnWakeupNotify getInstance()->setWakeup start%s", "");
     }
     env->DeleteLocalRef(jWakeupRetObject);
     env->DeleteLocalRef(jWakeupRetClass);

     LOGD("OnWakeupNotify end%s", "");
}



/*
 * Class:     com_tencent_ysdk_module_user_UserNativeListener
 * Method:    OnRelationNotify
 * Signature: (Lcom/tencent/ysdk/module/user/UserRelationRet;)V
 */
JNIEXPORT void JNICALL  Java_com_tencent_ysdk_module_user_UserNativeListener_OnRelationNotify
(JNIEnv* env, jclass,jobject jRelationRet) {
     jclass jRelationRetClz = env->GetObjectClass(jRelationRet);
     UserRelationRet cRelactionRet;
     jboolean iscopy;

     JniGetAndSetIntField(ret, "ret", jRelationRetClz, jRelationRet, cRelactionRet);
     JniGetAndSetIntField(flag, "flag", jRelationRetClz, jRelationRet, cRelactionRet);
     JniGetAndSetStringField(msg, "msg", jRelationRetClz, jRelationRet, cRelactionRet);
     //
     jfieldID jPersonsField = env->GetFieldID(jRelationRetClz, "persons", "Ljava/util/Vector;");
     jobject jPersonList = env->GetObjectField(jRelationRet, jPersonsField);
     jclass jArrayListClz = env->GetObjectClass(jPersonList);

     jmethodID jArrayListSizeMethod = env->GetMethodID(jArrayListClz, "size", "()I");
     jmethodID jArrayListGetMethod = env->GetMethodID(jArrayListClz, "get", "(I)Ljava/lang/Object;");
     jint jLength = env->CallIntMethod(jPersonList, jArrayListSizeMethod);

     LOGD("Java_com_tencent_ysdk_module_user_UserNativeListener_OnRelationNotify: tokenListSize: %d", jLength);
     for (int i = 0; i < (int) jLength; i++) {
          PersonInfo person;
          jobject jPerson = env->CallObjectMethod(jPersonList, jArrayListGetMethod, i);
          jclass jPersonInfoClass = env->GetObjectClass(jPerson);
          LOGD("push_back: tokenListSize: %d", jLength);
          JniGetAndSetStringField(nickName, "nickName", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(openId, "openId", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(userId, "userId", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(gender, "gender", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(pictureSmall, "pictureSmall", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(pictureMiddle, "pictureMiddle", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(pictureLarge, "pictureLarge", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(country, "country", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(province, "province", jPersonInfoClass, jPerson, person);
          JniGetAndSetStringField(city, "city", jPersonInfoClass, jPerson, person);

          cRelactionRet.persons.push_back(person);
          env->DeleteLocalRef(jPerson);
          env->DeleteLocalRef(jPersonInfoClass);
     }

     if(YSDKApi::getInstance()->getUserListener()!= NULL) {
          YSDKApi::getInstance()->getUserListener()->OnRelationNotify(cRelactionRet);
     }

     env->DeleteLocalRef(jRelationRetClz);
     env->DeleteLocalRef(jPersonList);
     env->DeleteLocalRef(jArrayListClz);
}
