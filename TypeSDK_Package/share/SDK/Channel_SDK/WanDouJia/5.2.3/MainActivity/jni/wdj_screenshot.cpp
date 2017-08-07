#include <jni.h>
#include "wdj_screenshot.h"

JNIEXPORT jboolean JNICALL Java_com_wandoujia_mariosdk_plugin_apk_utils_ScreenShotUtils_nativeScreenshotSupported(
        JNIEnv* , jclass) {
    return JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_wandoujia_mariosdk_plugin_apk_utils_ScreenShotUtils_nativeTakeScreenshot(
        JNIEnv* env, jclass clazz) {
    return env->NewStringUTF("");
}