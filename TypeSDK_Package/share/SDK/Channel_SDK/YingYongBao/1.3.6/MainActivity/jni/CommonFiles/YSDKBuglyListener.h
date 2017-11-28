
#ifndef YSDK_BUGLY_LISTENER_H
#define YSDK_BUGLY_LISTENER_H

#include <string>
#include "YSDKCommon.h"

class YSDKBuglyListener
{
public:
    virtual std::string OnCrashExtMessageNotify() = 0;
    virtual unsigned char* OnCrashExtDataNotify() = 0;

    virtual ~YSDKBuglyListener() {};
};

#endif
