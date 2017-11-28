
#ifndef YSDK_PAY_LISTENER_H
#define YSDK_PAY_LISTENER_H

#include "YSDKDefine.h"

class YSDKPayListener
{
public:
    virtual void OnPayNotify(PayRet& ret) = 0;

    virtual ~YSDKPayListener() {};
};

#endif
