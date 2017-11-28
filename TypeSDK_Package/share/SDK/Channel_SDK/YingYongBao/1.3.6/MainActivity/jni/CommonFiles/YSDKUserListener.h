#ifndef YSDK_USER_LISTENER_H
#define YSDK_USER_LISTENER_H

#include <string>
#include "YSDKCommon.h"
#include "YSDKDefine.h"

class YSDKUserListener
{
public:
    virtual void OnLoginNotify(UserLoginRet& loginRet) = 0;

    virtual void OnWakeupNotify(WakeupRet& wakeupRet) = 0;

    virtual void OnRelationNotify(UserRelationRet& relationRet) = 0;

    virtual ~YSDKUserListener() {};
};

#endif
