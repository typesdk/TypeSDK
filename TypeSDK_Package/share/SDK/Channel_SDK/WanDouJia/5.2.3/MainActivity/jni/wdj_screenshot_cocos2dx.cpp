#include <string.h>
#include <stdio.h>
#include <jni.h>
#include "wdj_screenshot.h"
#include "cocos2d.h"

USING_NS_CC;

JNIEXPORT jboolean JNICALL Java_com_wandoujia_mariosdk_plugin_apk_utils_ScreenShotUtils_nativeScreenshotSupported(
        JNIEnv* , jclass) {
#ifdef COCOS2D_VERSION
    return JNI_TRUE;
#else
    return JNI_FALSE;
#endif
}

#if defined(COCOS2D_VERSION) && COCOS2D_VERSION < 0x00030000
class Screenshooter : public CCObject {
public:
    void screenshot(float dt) {
        std::string fullPath = CCFileUtils::sharedFileUtils()->getWritablePath() + "screenshot.png";
        std::string tmpFile = "screenshot_tmp.png";
        std::string tmpFullPath = CCFileUtils::sharedFileUtils()->getWritablePath() + tmpFile;
        CCSize size = CCDirector::sharedDirector()->getWinSize();
        CCRenderTexture* screenTexture = CCRenderTexture::create(size.width,size.height, kCCTexture2DPixelFormat_RGBA8888);
	    screenTexture->begin();
        CCDirector::sharedDirector()->getRunningScene()->visit();
        screenTexture->end();
        screenTexture->saveToFile(tmpFile.c_str(), kCCImageFormatPNG);
        CC_SAFE_DELETE(screenTexture);
        rename(tmpFullPath.c_str(), fullPath.c_str());
    }
};
#endif

JNIEXPORT jstring JNICALL Java_com_wandoujia_mariosdk_plugin_apk_utils_ScreenShotUtils_nativeTakeScreenshot(
        JNIEnv* env, jclass clazz) {
#ifdef COCOS2D_VERSION
#if COCOS2D_VERSION >= 0x00030000
    std::string fullPath = FileUtils::getInstance()->getWritablePath() + "screenshot.png";
    std::string tmpFile = "screenshot_tmp.png";
    std::string tmpFullPath = FileUtils::getInstance()->getWritablePath() + tmpFile;
    remove(fullPath.c_str());
    Image::Format format = Image::Format::PNG;
    Size size = Director::getInstance()->getWinSize();
    auto renderTexture = RenderTexture::create(size.width, size.height, Texture2D::PixelFormat::RGBA8888);
    renderTexture->begin();
    Director::getInstance()->getRunningScene()->visit();
    renderTexture->end();
    renderTexture->saveToFile(tmpFile, format);
    auto scheduleCallback = [&,tmpFullPath, fullPath](float dt) {
        rename(tmpFullPath.c_str(), fullPath.c_str());
    };
    auto scheduler = Director::getInstance()->getRunningScene()->getScheduler();
    scheduler->schedule(scheduleCallback, FileUtils::getInstance(), 0.0f, 0, 0.0f, false, "screenshot");
    return env->NewStringUTF(fullPath.c_str());
#else
    std::string fullPath = CCFileUtils::sharedFileUtils()->getWritablePath() + "screenshot.png";
    remove(fullPath.c_str());
    CCDirector::sharedDirector()->getRunningScene()->scheduleOnce(
            schedule_selector(Screenshooter::screenshot),0);
    return env->NewStringUTF(fullPath.c_str());
#endif
#else
    return env->NewStringUTF("");
#endif
}