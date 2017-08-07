LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := YSDKDemo
LOCAL_SRC_FILES += com_tencent_tmgp_yybtestsdk_PlatformTest.cpp
LOCAL_SRC_FILES += com_tencent_ysdk_module_bugly_BuglyNativeListener.cpp
LOCAL_SRC_FILES += com_tencent_ysdk_module_pay_PayNativeListener.cpp
LOCAL_SRC_FILES += com_tencent_ysdk_module_user_UserNativeListener.cpp
LOCAL_SRC_FILES += YSDKApi.cpp

LOCAL_LDLIBS := -llog 
include $(BUILD_SHARED_LIBRARY)
