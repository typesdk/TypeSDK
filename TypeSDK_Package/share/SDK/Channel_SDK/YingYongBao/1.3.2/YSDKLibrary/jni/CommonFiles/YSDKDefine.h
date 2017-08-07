
#ifndef AGSDK_DEFINE_H
#define AGSDK_DEFINE_H


#define DEPRECATED(_version) __attribute__((deprecated))

typedef enum _ePlatform{
    ePlatform_None = 0,
    ePlatform_QQ = 1,
    ePlatform_Weixin =2,
    ePlatform_GUEST = 7
}ePlatform;

typedef struct
{
    std::string key;
    std::string value;

}KVPair;

typedef struct {
    int type;
    std::string value;
    long long expiration;
} UserToken;


typedef enum _eTokenType
{
    eToken_QQ_Access = 1, // 手Q accessToken
    eToken_QQ_Pay = 2,	// 手Q payToken
    eToken_WX_Access = 3, // 微信accessToken
    eToken_WX_Refresh = 4, // 微信refreshToken
    eToken_GUEST_PAY = 5, // 游客模式payToken
}eTokenType;

typedef struct {
    int ret;
    int flag;               //返回标记，标识成功和失败类型
    std::string msg;       //返回描述
    int platform;           //当前登录的平台
    std::string open_id;
    int user_type;
    std::vector<UserToken> token;
    std::string pf;
    std::string pfKey;
}UserLoginRet;


typedef struct
{
    int flag;               //返回标记，标识成功和失败类型
    std::string msg;       //返回描述
    int platform;           //当前登录的平台
    std::string media_tag_name; //wx回传得meidaTagName
    std::string open_id;        //qq传递的openid
    std::string desc;           //描述
    std::string lang;          //语言     目前只有微信5.1以上用，手Q不用
    std::string country;       //国家     目前只有微信5.1以上用，手Q不用
    std::string messageExt;    //游戏分享传入自定义字符串，平台拉起游戏不做任何处理返回         目前只有微信5.1以上用，手Q不用
    std::vector<KVPair> extInfo;  //游戏－平台携带的自定义参数手Q专用
}WakeupRet;


typedef struct {
    std::string nickName;         //昵称
    std::string openId;           //帐号唯一标示
    std::string userId;           //当应用有多个appid时，返回同一应用用户的标示
    std::string gender;           //性别
    std::string pictureSmall;     //小头像
    std::string pictureMiddle;    //中头像
    std::string pictureLarge;     //datouxiang
    std::string country;          //国家
    std::string province;          //省份(老版本属性，为了不让外部app改代码，没有放在AddressInfo)
    std::string city;             //城市(老版本属性，为了不让外部app改代码，没有放在AddressInfo)
}PersonInfo;

typedef struct {
    int ret;
    int flag;               //返回标记，标识成功和失败类型
    std::string msg;       //返回描述
    std::vector<PersonInfo> persons;//保存好友或个人信息
    std::string extInfo; //游戏查询是传入的自定义字段，用来标示一次查询
}UserRelationRet;

typedef struct {
    int ret;
    int flag;               //返回标记，标识成功和失败类型
    std::string msg;       //返回描述
    int platform;           //当前登录的平台

    int realSaveNum;
    int payChannel;
    int payState;
    int provideState;
    std::string extendInfo;
    std::string payReserve1;
    std::string payReserve2;
    std::string payReserve3;

    std::string ysdkExtInfo;

}PayRet;

typedef enum
{
    eFlag_Succ = 0, //成功
    eFlag_Error = -1, //通用失败

    eFlag_QQ_UserCancel = 1001,
    eFlag_QQ_LoginFail = 1002,
    eFlag_QQ_NetworkErr = 1003,
    eFlag_QQ_NotInstall = 1004,
    eFlag_QQ_NotSupportApi = 1005,

    eFlag_WX_NotInstall = 2000,
    eFlag_WX_NotSupportApi = 2001,
    eFlag_WX_UserCancel = 2002,
    eFlag_WX_UserDeny = 2003,
    eFlag_WX_LoginFail = 2004,

    eFlag_GUEST_LoginFail = 3000,

    eFlag_LocalTokenInvalid = 3100,
    eFlag_Login_NotRegisterRealName = 3101,
    eFlag_CheckingToken = 3102,

    eFlag_Relation_RelationNoPerson = 3201,

    eFlag_Wakeup_NeedUserLogin = 3301,
    eFlag_Wakeup_YSDKLogining = 3302,
    eFlag_Wakeup_NeedUserSelectAccount = 3303,


    eFlag_Pay_User_Cancle = 4001, //用户取消
    eFlag_Pay_Param_Error = 4002, //参数错误

}eFlag;

typedef enum
{
    PAYSTATE_PAYUNKOWN = -1,
    PAYSTATE_PAYSUCC = 0,
    PAYSTATE_PAYCANCEL = 1,
    PAYSTATE_PAYERROR = 2

}PayState;
typedef enum
{
    RET_SUCC = 0,
    RET_FAIL = 1

}BaseRet;
#endif
