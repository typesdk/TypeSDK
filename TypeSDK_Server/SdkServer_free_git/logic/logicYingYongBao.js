/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');

var YSDK_QQ = 'QQ';
var YSDK_QQ_LOGIN_URL = 'http://ysdk.qq.com/auth/qq_check_token';
//var YSDK_QQ_LOGIN_URL = 'http://ysdktest.qq.com/auth/qq_check_token';
var YSDK_WX = 'WX';
var YSDK_WX_LOGIN_URL = 'http://ysdk.qq.com/auth/wx_check_token';
//var YSDK_WX_LOGIN_URL = 'http://ysdktest.qq.com/auth/wx_check_token';

/**
 * 转换登录参数
 *
 * @param query
 * @param ret
 * @returns {boolean}
 */
function convertParamLogin(query, ret) {
    var org =
    {
        "id": "0"
        , "token": ""
        , "data": ""
        , "sign": ""
    };

    var cloned = merge(true, org);
    merge(cloned, query);

    for (var i in cloned) {
        //判断参数中是否该有的字段齐全
        if (org[i] == cloned[i] && i != "data") {
            return false;
        }

        //判断参数中是否有为空的字段
        if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data") {
            return false;
        }
    }
    var userIdArr = cloned.id.toString().split("|");
    var tokenArr = cloned.token.toString().split("|");

    ret.openid = userIdArr[0];
    //  QQ的是pay_token
    ret.openkey = userIdArr[1];
    //  （可选）用户的外网IP
    ret.userip = '';
    //  类型
    ret.loginType = tokenArr[tokenArr.length - 1];
    return true;
}

/**
 * 渠道登录
 *
 * @param attrs
 * @param params
 * @param query
 * @param ret
 * @param retf
 */
function callChannelLogin(attrs, params, query, ret, retf,gattrs) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);

    var requestData = {
        appid: '',
        openid: cloned.openid,
        openkey: cloned.openkey,
        timestamp: Math.floor(Date.now() / 1000)
    };

    var appKey = '';
    var outURL = '';
    var requestMethod = 'GET';

    if (query.loginType == YSDK_QQ) {
        requestData.appid = attrs.app_id;
        appKey = attrs.app_key;
        outURL = YSDK_QQ_LOGIN_URL;
    } else {
        //   todo 登录中appid与appkey是不统一的
        requestData.appid = attrs.product_id;
        appKey = attrs.product_key;
        outURL = YSDK_WX_LOGIN_URL;
    }

    console.log("游戏对应的appkey");
    console.log(appKey);
    if (typeof cloned.userip != 'undefined' && cloned.userip != null && cloned.userip != '') {
        requestData.userip = cloned.userip;
    }
    requestData.sig = crypto.createHash('md5').update(appKey + requestData.timestamp).digest('hex');

    var options = {
        url: outURL,
        method: requestMethod,
        qs: requestData
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            console.log('******** 应用宝返回值 ********');
            console.log(retOut);

            if (retOut.ret == '0') {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = requestData.openid;
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
            }
            else {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code = 1;
                ret.msg = "LOGIN User ERROR";
                ret.id = "";
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }
        }
        else {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
            ret.code = 2;
            ret.msg = "OUT URL ERROR";
            ret.value = "";

        }
        retf(ret);
    });
}

/**
 * 根据serverId取出应用宝相应的zoneId
 *
 * @param attrs
 * @param requestQuery
 * @param retf
 */
function getServerMapById(attrs, requestQuery, retf) {
    var serverid = requestQuery.serverid;
    var retData = {
        code: "0",
        msg: '',
        data: ''
    };

    var serverData = {
        serverid: '',
        zoneId: '',
        name: ''
    };

    console.log("******** serverList ********");
    console.log(JSON.stringify(attrs.serverList));

    if (attrs.serverList) {
        for (var index = 0, length = attrs.serverList.length; index < length; index++) {
            if (attrs.serverList[index].serverId == serverid) {
                serverData.serverid = attrs.serverList[index].serverId;
                serverData.zoneId = attrs.serverList[index].zoneId;
                serverData.name = attrs.serverList[index].name;

                retData.data = serverData;
                retData.msg = "NOMARL";
                break;
            }
        }
    } else {
        retData.code = "1";
        retData.msg = "error"
    }

    console.log(retData);
    retf(retData);

}
/*
 * 客户端支付完成回调接口
 * 接口接收到回调之后请求应用包扣款
 * */
function createChannelOrder(gameName, channel, channelId, gameAttrs, attrs, params, query, retf) {
    var retData = {
        code: '0',
        msg: 'NORMAL'
    };
    var appkey = null;
    var appid = null;
    var payType = null;
    var openkey = null;

    console.log("请求参数");
    console.log(query);

    //   todo 支付中appid与appkey是统一的，统一使用qq的appid与appkey
    if (query.paytype == YSDK_QQ) {
        appid = attrs.app_id;
        appkey = attrs.app_key;
        payType = YSDK_QQ;
        openkey = query.payToken;
    } else {
        appid = attrs.product_id;
        appkey = attrs.product_key;
        payType = YSDK_WX;
        openkey = query.openkey;
    }



    var queueData = {
        gameName: gameName,
        channel: channel,
        channelId: channelId,
        appid: appid,
        appkey: appkey,
        gamekey: gameAttrs.gkey,

        cporderId: query.cporder,       //   内部订单号
        accountId: query.playerid,    //   账号id

        openid: query.openid,
        openkey: openkey,
        pf: query.pf,
        pfkey: query.pfkey,
        zoneid: query.zoneid,
        amt: parseInt(query.amt) + '', //   游戏币的个数
        price: query.price,
        billno: query.billno,        //   外部订单号（应用宝订单号）
        payitem: query.payitem == void 0 ? '' : query.payitem,
        appremark: (query.appremark == void 0) ? '' : query.appremark,
        payType: payType,
        requestTimes: 0,
        itemLists: attrs.itemLists == void 0 ? [] : attrs.itemLists
    };

    console.log("****** 应用宝支付消息内容 ******");
    console.log(queueData);

    compareOrder(queueData, function (compareObj) {
        if (compareObj.code == 0) {
            doPayAction(queueData, retf);
        } else {
            retf(compareObj);
        }
    });

    /*rsmqAction.sendMessage(queueData, function (err, resp) {
        if (err == null) {
            console.log("消息入队成功");
        } else {
            // 入队服务处理出错\
            console.log(err);
        }
    });*/

}

/**
 * 获取消息队列中的消息
 *
 */
/*function getQueueMessage(message) {
    var payMessage = message;
    compareOrder(message, function (compareObj) {
        if (compareObj.code == 0) {
            doPayAction(payMessage);
        }
    });
}

/**
 * 校验订单过后的扣款过程
 *
 * @param payMessage
 */
function doPayAction(payMessage, retf) {
    var retdata = {};
    tcGetBalance(payMessage, function (balanceObj) {
        if (balanceObj.code == 0) {
            //  订单真实扣款0
            var amount = payMessage.amt;
            //  balance:游戏币个数（包含了赠送游戏币
            if (balanceObj.balance >= amount) {
                //  扣除所有货币
                //payMessage.amt = balanceObj.balance;

                tcPay(payMessage, function (payObj) {
                    payMessage.amt = amount;
                    if (payObj.code == 0) {
                        notifyServer(payMessage, payObj, function (notifyObj) {
                            retf(notifyObj);
                        });
                    } else {
                        retf(payObj);
                    }
                });
            } else {
                retdata.code = 1;
                retdata.msg = "正常返回，结果失败";
                retf(retdata);
            }

        } else {
            console.log("****** 支付处理异常 ****");
            retdata.code = -99;
            retdata.msg = "未知错误";
            console.log(balanceObj);
            retf(retdata);
        }
    });
}


//  查询订单信息
function compareOrder(message, callBack) {
    var retValue = {};
    retValue.code = '0';
    retValue.id = message.accountId;
    retValue.order = message.billno;
    retValue.cporder = message.cporderId || '';
    retValue.info = '';

    var params = {
        out_url: '',
        verifyurl: ''
    };

    var gameName = message.gameName;
    var channelId = message.channelId;

    var retData = {
        code: 0,
        msg: 'NORMAL'
    };
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retData.code = -99;
            retData.msg = '订单不存在';
            console.log("****** 订单不存在 ******");
            console.log(retValue);
            callBack(retData);
            return;
        }

        else {
            retValue.sign = logicCommon.createSignPay(retValue, message.gamekey);
            logicCommon.UpdateOrderStatus(gameName, channelId, retValue.cporder, retValue.order, 1,0, message);

            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };

            console.log(options);
            request(options, function (error, response, body) {

                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    console.log('******** 比对订单信息 ********');

                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        retData.code = 99;
                        retData.msg = '服务端返回状态异常';

                    }
                    if (retOut.code == '0') {
                        if (retOut.Itemid) {
                            logicCommon.mapItemLists(message, retOut);
                        }
                        /*&&query.data.amount*100<=retOut.amount
                         &&query.data.amount*100>=retOut.amount*0.9*/
                        //   todo 正式环境记得更改条件
                        if ((message.price * 100) <= retOut.amount
                                && (message.amt * 100) >= retOut.amount * 0.9) {
                            if (retOut.status == '2') {
                                retData.code = 2;
                                console.log("服务端返回订单状态为2");
                            } else if (retOut.status == '4') {
                                retData.code = 4;
                                retData.msg = '服务端返回订单状态为4';
                                logicCommon.UpdateOrderStatus(gameName, channelId, retValue.cporder, retValue.order, 4,parseInt(message.price * 100));
                            } else if (retOut.status == '3') {
                                retData.code = 3;
                                retData.msg = '服务端返回订单状态为3';
                                logicCommon.UpdateOrderStatus(gameName, channelId, retValue.cporder, retValue.order, 4,parseInt(message.price * 100));
                            } else {
                                retData.code = 0;
                                retData.msg = 'NORMAL';
                                logicCommon.UpdateOrderStatus(gameName, channelId, retValue.cporder, retValue.order, 2,0);
                            }
                        } else {
                            retData.code = 99;
                            retData.msg = '服务端返回游戏金额异常';
                            console.log("服务端返回游戏金额异常");
                            logicCommon.UpdateOrderStatus(gameName, channelId, retValue.cporder, retValue.order, 3,0);
                        }
                    } else {
                        retData.code = -99;
                        retData.msg = '游戏服返回订单状态异常';
                        console.log("游戏服返回订单状态异常");
                    }
                } else {
                    retData.code = -99;
                    retData.msg = '游戏服返回异常';
                    console.log("游戏服返回异常");
                }

                callBack(retData);
            });
        }
    });

}

/**
 * 查询余额
 *
 * @param message
 * @param callBack
 */
function tcGetBalance(message, callBack) {
    var tcurl = 'https://ysdk.qq.com';//正式
    //var tcurl = 'https://ysdktest.qq.com';//沙箱
    var org_loc = '/mpay/get_balance_m';
    var url = tcurl + org_loc;

    var data = {
        openid: message.openid
        , openkey: message.openkey
        , appid: message.appid
        , ts: Math.floor(Date.now() / 1000)
        , pf: message.pf
        , pfkey: message.pfkey
        , zoneid: message.zoneid
    };

    //data.sort(function (a, b) { return a[name] > b[name] ? 1 : -1 });
    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', message.appkey + '&').update('GET&' + urlencode('/v3/r' + org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    var j = request.jar();
    var cookie1 = null;
    var cookie2 = null;

    if (message.payType == YSDK_QQ) {
        cookie1 = request.cookie('session_id=openid');
        cookie2 = request.cookie('session_type=kp_actoken');
    } else {
        cookie1 = request.cookie('session_id=hy_gameid');
        cookie2 = request.cookie('session_type=wc_actoken');
    }
    var cookie3 = request.cookie('org_loc=' + org_loc);
    j.setCookie(cookie1, url);
    j.setCookie(cookie2, url);
    j.setCookie(cookie3, url);

    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log("****** 查询应用宝余额 ******");
    console.log(options);

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.balance = retOut.balance;
                retdata.gen_balance = retOut.gen_balance;
                retdata.first_save = retOut.first_save;
                retdata.save_amt = retOut.save_amt;
                retdata.tss_list = retOut.tss_list;
                retdata.data = retOut;
            } else if (retOut.ret == "1001") {
                retdata.code = 1;
                retdata.msg = '参数错误';
                retdata.data = retOut;
            } else if (retOut.ret == "1018") {
                retdata.code = 2;
                retdata.msg = '登陆校验失败';
                retdata.data = retOut;
            } else {
                retdata.code = 3;
                retdata.msg = 'ERR';
                retdata.data = retOut;
            }
        } else {
            retdata.code = -99;
            retdata.msg = 'NET ERROR';
        }
        callBack(retdata);

    });
}

/**
 * 查询余额
 *
 * @param message
 * @param callBack
 */
function tcGetBalanceForAct(attrs, params, oquery, ret, retf) {
    var tcurl = 'https://ysdk.qq.com';//正式
    //var tcurl = 'https://ysdktest.qq.com';//沙箱
    var org_loc = '/mpay/get_balance_m';
    var url = tcurl + org_loc;

    var appkey = null;
    var appid = null;
    var payType = null;
    var openkey = null;

    if (oquery.type == YSDK_QQ) {
        appid = attrs.app_id;
        appkey = attrs.app_key;
        openkey = oquery.pay_token;
        payType = YSDK_QQ;
    } else {
        appid = attrs.product_id;
        appkey = attrs.product_key;
        openkey = oquery.openkey;
        payType = YSDK_WX;
    }

    var data = {
        openid: oquery.openid
        , openkey: openkey
        , appid: appid
        , ts: Math.floor(Date.now() / 1000)
        , pf: oquery.pf
        , pfkey: oquery.pfkey
        , zoneid: oquery.zoneid
    };

    //data.sort(function (a, b) { return a[name] > b[name] ? 1 : -1 });
    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', appkey + '&').update('GET&' + urlencode('/v3/r' + org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    var j = request.jar();
    var cookie1 = null;
    var cookie2 = null;

    if (payType == YSDK_QQ) {
        cookie1 = request.cookie('session_id=openid');
        cookie2 = request.cookie('session_type=kp_actoken');
    } else {
        cookie1 = request.cookie('session_id=hy_gameid');
        cookie2 = request.cookie('session_type=wc_actoken');
    }

    var cookie3 = request.cookie('org_loc=' + org_loc);
    j.setCookie(cookie1, url);
    j.setCookie(cookie2, url);
    j.setCookie(cookie3, url);

    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log("****** 查询应用宝余额 ******");
    console.log(options);

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.balance = retOut.balance;
                retdata.gen_balance = retOut.gen_balance;
                retdata.first_save = retOut.first_save;
                retdata.save_amt = retOut.save_amt;
                retdata.tss_list = retOut.tss_list;
                retdata.data = retOut;
            } else if (retOut.ret == "1001") {
                retdata.code = 1;
                retdata.msg = '参数错误';
                retdata.data = retOut;
            } else if (retOut.ret == "1018") {
                retdata.code = 2;
                retdata.msg = '登陆校验失败';
                retdata.data = retOut;
            } else {
                retdata.code = 3;
                retdata.msg = 'ERR';
                retdata.data = retOut;
            }
        } else {
            retdata.code = -99;
            retdata.msg = 'NET ERROR';
        }
        retf(retdata);

    });
}

/**
 * 扣除游戏币接口
 *
 * @param message
 * @param callBack
 */
function tcPay(message, callBack) {
    var tcurl = 'https://ysdk.qq.com';//正式环境
    //var tcurl = 'https://ysdktest.qq.com';//沙箱环境
    var org_loc = '/mpay/pay_m';
    var url = tcurl + org_loc;

    var data = {
        openid: message.openid
        , openkey: message.openkey
        , appid: message.appid
        , ts: Math.floor(Date.now() / 1000)
        //        平台来源，$平台-$渠道-$版本-$业务标识。
        //例如： openmobile_android-2001-android-xxxx
        , pf: message.pf
        , pfkey: message.pfkey
        , zoneid: message.zoneid
        , amt: message.amt
        , billno: message.billno
    };

    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', message.appkey + '&').update('GET&' + urlencode('/v3/r' + org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    //    Cookie里面需要包含的参数：
    //    session_id    用户账户类型，（手Q）session_id ="openid"；（微信）session_id = "hy_gameid"
    //    session_type  session类型，（手Q）session_type = "kp_actoken"；（微信）session_type = "wc_actoken"
    //    org_loc     	需要填写: /mpay/get_balance_m
    var j = request.jar();
    var cookie1 = null;
    var cookie2 = null;
    if (message.payType == YSDK_QQ) {
        cookie1 = request.cookie('session_id=openid');
        cookie2 = request.cookie('session_type=kp_actoken');
    } else {
        cookie1 = request.cookie('session_id=hy_gameid');
        cookie2 = request.cookie('session_type=wc_actoken');
    }
    var cookie3 = request.cookie('org_loc=' + org_loc);

    j.setCookie(cookie1, url);
    j.setCookie(cookie2, url);
    j.setCookie(cookie3, url);


    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log("****** 通知应用宝支付 ******");
    console.log(options);

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        //console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            //日志记录CP端返回
            console.log(retOut);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.balance = retOut.balance;
                retdata.billno = retOut.billno;
            }
            else if (retOut.ret == "1004") {
                //打点：其他支付失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                retdata.code = 1;
                retdata.msg = '余额不足';
                retdata.data = retOut;
            }
            else if (retOut.ret == "1018") {
                //打点：其他支付失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                retdata.code = retOut.ret;
                retdata.msg = '登陆校验失败';
                retdata.data = retOut;
            }
            else {
                //打点：其他支付失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                retdata.code = '99';
                retdata.msg = '失败';
                retdata.data = retOut;
            }
        } else {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retdata.code = -1;
            retdata.msg = 'NET ERROR　' +　response.statusCode;
        }
        callBack(retdata);
    });
}

/**
 * 通知游戏服发货
 *
 * @param message
 * @param payObj
 * @param callback
 */
function notifyServer(message, payObj, callback) {
    var serverObj = {};
    serverObj.code = payObj.code;
    //  todo   something
    serverObj.id = message.accountId == void 0 ? '' : message.accountId;
    serverObj.order = message.billno;
    serverObj.cporder = message.cporderId;
    serverObj.info = '';

    var params = {
        out_url: '',
        verifyurl: ''
    };
    console.log("通知游戏服");
    console.log(serverObj);
    var retdata = {};

    logicCommon.getNotifyUrl(serverObj.cporder, params, function (hasData) {
        if (!hasData) {
            console.log("****** 订单不存在 ******");
            console.log(serverObj);
            retdata.code = "-99";
            retdata.msg = "订单不存在";
            callback(retdata);
            return;
        } else {

            serverObj.sign = logicCommon.createSignPay(serverObj, message.gamekey);
            serverObj.gamename = message.gameName;
            serverObj.sdkname = message.channel;
            serverObj.channel_id = message.channelId;
            serverObj.amount = '' + parseInt(message.price * 100) + '';

            var options = {
                url: params.out_url,
                method: "POST",
                body: serverObj,
                json: true
            };
            console.log("****** 通知游戏服发货 ******");
            console.log(options);
            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                console.log("游戏服返回数据");
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    console.log(retOut);
                    if (retOut.code == 0) {
                        //  服务器发货成功
                        console.log("服务端发货成功");

                        retdata.code = 0;
                        retdata.msg = "游戏服发货成功";
                        logicCommon.UpdateOrderStatus(message.gameName, message.channelId, message.cporderId, message.billno, 4,parseInt(message.price * 100));
                    } else {
                        // 游戏服处理异常也要进行退单
                        retdata.code = 1;
                        retdata.msg = "服务端发货异常，进行退单";
                        console.log("服务端发货异常，进行退单");
                        tcCancelPay(message);
                    }
                } else {
                    // 游戏服处理异常也要进行退单
                    retdata.code = 2;
                    retdata.msg = "服务端发货异常，进行退单";

                    console.log("服务端发货异常，进行退单");
                    tcCancelPay(message);
                }
                callback(retdata);
            });
        }
    });
}

/*
 * 取消支付
 * */
function tcCancelPay(message) {
    var tcurl = 'https://ysdk.qq.com';//正式环境
    //var tcurl = 'https://ysdktest.qq.com';//沙箱环境
    var org_loc = '/mpay/cancel_pay_m';
    var url = tcurl + org_loc;

    var data = {
        openid: message.openid
        , openkey: message.openkey
        , appid: message.appid
        , ts: Math.floor(Date.now() / 1000)
        , pf: message.pf
        , pfkey: message.pfkey
        , zoneid: message.zoneid
        , amt: message.amt
        , billno: message.billno
    };

    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', message.appkey + '&').update('GET&' + urlencode('/v3/r' + org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    var j = request.jar();

    var cookie1 = null;
    var cookie2 = null;

    if (message.payType == YSDK_QQ) {
        cookie1 = request.cookie('session_id=openid');
        cookie2 = request.cookie('session_type=kp_actoken');
    } else {
        cookie1 = request.cookie('session_id=hy_gameid');
        cookie2 = request.cookie('session_type=wc_actoken');
    }
    var cookie3 = request.cookie('org_loc=' + org_loc);

    j.setCookie(cookie1, url);
    j.setCookie(cookie2, url);
    j.setCookie(cookie3, url);

    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log("****** 取消订单Action ******");
    console.log(options);

    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                console.log("取消订单成功");
            } else {
                console.log("取消订单异常");
            }
        } else {
            console.log("取消订单异常");
        }
    });
}

/*
 * 支付回调发货
 * */
function TCChargeCallBack(game, channel, action, attrs, params, query, ret, retf) {

    //第1步：将请求的URI路径进行URL编码（URI不含host，URI示例：/v3/user/get_info）
    var url = encodeURIComponent('/' + game + '/' + channel + '/' + action);

    //第2步：将除“sig”外的所有参数按key进行字典升序排列。
    var arrKey = Object.keys(query).map(function (k) {
        return k
    });
    var arr = new Array();
    for (var i in query) {
        arr[i] = query[i];
    }

    arrKey.sort();
    var arr2 = new Array();

    var k = "";
    for (var j in arrKey) {
        k = arrKey[j];
        arr2[k] = arr[k].replace(/\-/g, '%2D').replace(/\_/g, '%5F').replace(/\./g, '%2E').replace(/\%/g, '%25');
    }

    //第3步：将第2步中排序后的参数(key=value)用&拼接起来，并进行URL编码。
    var str = "";
    for (var key in arr2) {
        if (key != 'sig' && key != 'cee_extend')
            str += key + '=' + arr2[key] + '&';
    }

    var signStr = str.substr(0, str.length - 1).replace(/\=/g, '%3D').replace(/\*/g, '%2A').replace(/\&/g, '%26');

    //第4步：将HTTP请求方式（GET或者POST）以及第1步和第3步中的字符串用&拼接起来。
    var basestring = 'POST&' + url + '&' + signStr;

    var appKey = attrs.app_key + '&';

    var osign = crypto.createHmac('sha1', appKey).update(basestring).digest('base64');

    console.log('Sign: ' + query.sig + " :: " + osign);

    var retDate = {};
    if (query.sign == osign) {

        var postdata = {};
        postdata.appid = '1';
        postdata.sdkid = '1';
        postdata.channel = '48';
        postdata.serverid = query.zoneid;

        postdata.data = {};
        postdata.data.itemid = retValue.billno.split('*')[0];
        postdata.data.orderid = retValue.billno;
        postdata.data.paytype = '1';
        postdata.data.amt = '' + query.amt + '';
        postdata.data.sign = 'sign';
        var t = logicCommon.GetNowStrForWJY();
        postdata.data.paytime = t;
        postdata.data.placeordertime = t;

        var options = {
            url: params.out_url,
            method: params.method,
            body: postdata,
            json: true
        };
        console.log('Options: ' + JSON.stringify(options));
        console.log(options);

        request(options, function (error, response, body) {
            console.log('CB-Unsuccess: ' + JSON.stringify(error));
            console.log('CP-Body: ' + JSON.stringify(body));

            if (!error && response.statusCode == 200) {
                var retOut = body;

                //日志记录CP端返回
                if (typeof retOut.RetCode == 'undefined') {
                    retDate.ret = 4;
                    retDate.msg = "ERROR";

                    retf(retDate);
                    return;
                }

                if (retOut.RetCode == '1') {
                    logicCommon.UpdateOrderStatusForWJY(game, channel, '', retValue.order, 2);
                    retDate.ret = 0;
                    retDate.msg = "OK";

                    retf(retDate);
                }
                else {
                    retDate.ret = 4;
                    retDate.msg = "ERROR";

                    retf(retDate);
                }

            } else {
                retDate.ret = 4;
                retDate.msg = "ERROR";

                retf(retDate);
            }
        });

    } else {
        retDate.ret = 4;
        retDate.msg = "SIGN ERROR";

        retf(retDate);
    }
}

/*
 * 等级礼包
 * */
function TCGradePackage(game, channel, action, attrs, params, query, ret, retf) {

    //第1步：将请求的URI路径进行URL编码（URI不含host，URI示例：/v3/user/get_info）
    var url = encodeURIComponent('/' + game + '/' + channel + '/' + action);

    //第2步：将除“sig”外的所有参数按key进行字典升序排列。
    var arrKey = Object.keys(query).map(function (k) {
        return k
    });
    var arr = new Array();
    for (var i in query) {
        arr[i] = query[i];
    }

    arrKey.sort();
    var arr2 = new Array();

    var k = "";
    for (var j in arrKey) {
        k = arrKey[j];
        arr2[k] = arr[k].replace(/\-/g, '%2D').replace(/\_/g, '%5F').replace(/\./g, '%2E').replace(/\%/g, '%25');
    }

    //第3步：将第2步中排序后的参数(key=value)用&拼接起来，并进行URL编码。
    var str = "";
    for (var key in arr2) {
        if (key != 'sig' && key != 'cee_extend')
            str += key + '=' + arr2[key] + '&';
    }

    var signStr = str.substr(0, str.length - 1).replace(/\=/g, '%3D').replace(/\*/g, '%2A').replace(/\&/g, '%26');

    //第4步：将HTTP请求方式（GET或者POST）以及第1步和第3步中的字符串用&拼接起来。
    var basestring = 'GET&' + url + '&' + signStr;

    var appKey = attrs.app_key + '&';

    var osign = crypto.createHmac('sha1', appKey).update(basestring).digest('base64');

    console.log('Sign: ' + query.sig + " :: " + osign);

    var retDate = {};
    if (query.sign == osign) {

        var postdata = {};
        postdata.appid = '1';
        postdata.sdkid = '1';
        postdata.channel = '48';
        postdata.data = {};
        postdata.data.itemid = retValue.billno.split('*')[0];
        postdata.data.orderid = retValue.billno;
        postdata.data.paytype = '1';
        postdata.data.amt = '' + query.amt + '';
        postdata.data.sign = 'sign';
        var t = logicCommon.GetNowStrForWJY();
        postdata.data.paytime = t;
        postdata.data.placeordertime = t;

        var options = {
            url: params.out_url,
            method: params.method,
            body: postdata,
            json: true
        };

        console.log('Options: ' + JSON.stringify(options));
        console.log(options);

        request(options, function (error, response, body) {
            console.log('CB-Error: ' + JSON.stringify(error));
            console.log('CB-Response: ' + JSON.stringify(response));
            console.log('CP-Body: ' + JSON.stringify(body));

            if (!error && response.statusCode == 200) {

                //日志记录CP端返回
                if (typeof retOut.RetCode == 'undefined') {
                    retDate.ret = 4;
                    retDate.msg = "ERROR";
                    retDate.zoneid = "1";

                    retf(retDate);
                    return;
                }

                if (retOut.RetCode == '1') {
                    logicCommon.UpdateOrderStatusForWJY(game, channel, '', retValue.order, 2);
                    retDate.ret = 0;
                    retDate.msg = "OK";
                    retDate.zoneid = "1";

                    retf(retDate);
                }
                else {
                    retDate.ret = 4;
                    retDate.msg = "ERROR";
                    retDate.zoneid = "1";

                    retf(retDate);
                }

            } else {
                retDate.ret = 4;
                retDate.msg = "ERROR";
                retDate.zoneid = "1";

                retf(retDate);
            }
        });

    } else {
        retDate.ret = 4;
        retDate.msg = "SIGN ERROR";
        retDate.zoneid = "1";

        retf(retDate);
    }
}

/*
 * 直接赠送接口
 * */
function TCPresent(attrs, params, query, ret, retf) {

    var tcurl = 'https://ysdk.qq.com';
    //var tcurl = 'https://ysdktest.qq.com';
    var org_loc = '/mpay/present_m';
    var url = tcurl + org_loc;

    var data = {
        openid: query.openid
        , openkey: query.pay_token
        , appid: attrs.app_id
        , ts: Math.floor(Date.now() / 1000)
        , pf: query.pf
        , zoneid: query.zoneid
        , pfkey: query.pfkey
        , presenttimes: parseInt(query.presenttimes)
        , billno: query.billno
    };

    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', attrs.app_key + '&').update('GET&' + urlencode('/v3/r' + org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    var j = request.jar();

    var cookie1 = request.cookie('session_id=openid');
    var cookie2 = request.cookie('session_type=kp_actoken');

    var cookie3 = request.cookie('org_loc=' + org_loc);

    j.setCookie(cookie1, url);
    j.setCookie(cookie2, url);
    j.setCookie(cookie3, url);


    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log('Options: ' + JSON.stringify(options));

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        //console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                //retdata.data = retOut;
                retf(retdata);
            }
            else if (retOut.ret == "1018") {
                retdata.code = 2;
                retdata.msg = 'NOT LOGIN';
                retdata.data = retOut;
                retf(retdata);
            }
            else {
                retdata.code = 99;
                retdata.msg = '失败';
                retdata.data = retOut;
                retf(retdata);
            }
        } else {
            retdata.code = -1;
            retdata.msg = 'NET ERROR';
            retf(retdata);
        }
    });
}

function TCLogin(attrs, params, query, ret, retf) {
    var tcurl = 'http://ysdk.qq.com';//正式换进
    //var tcurl = "http://ysdktest.qq.com";//测试环境
    var org_loc = '/auth/qq_check_token';
    var url = tcurl + org_loc;

    var data = {};
    if (query.txType === 5) {
        org_loc = '/auth/guest_check_token';
        url = tcurl + org_loc;
        data = {
            guestid: query.openid,
            accessToken: query.openkey
        };
    } else {
        data = {
            openid: query.openid
            , openkey: query.openkey
            , appid: attrs.appId
        };
    }
    data.timestamp = Math.floor(Date.now() / 1000);
    data.sig = crypto.createHash('md5').update(attrs.appKey + data.timestamp).digest('hex');

    var options = {
        url: url,
        method: 'GET',
        qs: data
    };

    console.log('Options: ' + JSON.stringify(options));
    console.log(options);

    var retdata = {};
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        console.log('CB-Unsuccess: ' + JSON.stringify(error));
        //console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                retdata.code = '0';
                retdata.msg = 'success';
                retdata.uid = query.openid;
                retdata.channel = '76';
                retf(retdata);
            }
            else {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                retdata.code = '99';
                retdata.msg = 'error';
                retdata.uid = query.openid;
                retdata.channel = '76';

                retf(retdata);
            }
        } else {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
            retdata.code = '99';
            retdata.msg = 'error';
            retdata.uid = query.openid;
            retdata.channel = '76';

            retf(retdata);
        }
    });
}

/*
 * 检测登录态
 * */
function CheckTCLoginStatus(attrs, params, query, ret, retf) {
    var checkUrl = 'http://113.108.20.23/v3/user/is_login';
    var cloned = merge(true, params.out_params);
    merge(cloned, query);
    cloned.appid = attrs.appId;

    var qs = {};
    qs.timestamp = Date.now();
    qs.appid = attrs.appId;
    qs.openid = cloned.openid;
    qs.encode = '1';
    qs.sig = crypto.createHash('md5').update(attrs.appKey + qs.timestamp).digest('hex');

    var options = {
        url: checkUrl,
        method: params.method,
        qs: qs,
        json: true
    };

    var retDate = {};

    console.log('Options: ' + JSON.stringify(options));
    console.log(options);

    request(options, function (error, response, body) {
        console.log('CB-Error: ' + JSON.stringify(error));
        console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CB-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = body;
            if (retOut.ret == '0') {

                retDate.ret = '1';
                retDate.msg = 'success';
                retDate.uid = '';
                retDate.channel = '48';
            }
            else {
                retDate.ret = '0';
                retDate.msg = 'error';
                retDate.uid = '';
                retDate.channel = '48';
            }
        }
        else {
            retDate.ret = '0';
            retDate.msg = 'error';
            retDate.uid = '';
            retDate.channel = '48';
        }
        retf(retDate);
    });
}

/*
 * 订阅物品查询接口
 * */
function TCSubscribeQuery(attrs, params, query, ret, retf) {
    var tcurl = 'http://msdk.qq.com';
    var org_loc = '/mpay/subscribe_m';
    var url = tcurl + org_loc;

    //session_id 登陆态帐号类型，需和sessionType成对匹配，例如：（手Q）sessionId = "openid"；(微信)sessionId = "hy_gameid"
    //session_type 登陆态票据类型，需和sessionId成对匹配。例如：(手Q)sessionType = "kp_actoken"；（微信）sessionType = "wc_actoken" 如：手Q： sessionId =“openid” sessionType = ” kp_actoken”;微信：sessionId = "hy_gameid" sessionType = "wc_actoken"
    var data = {
        session_id: 'openid'
        , session_type: 'kp_actoken'
        , openid: query.openid
        , openkey: query.openkey
        , zoneid: query.zoneid
        , appid: attrs.appId
        , ts: Math.floor(Date.now() / 1000)
        //        平台来源，$平台-$渠道-$版本-$业务标识。
        //例如： openmobile_android-2001-android-xxxx
        , pf: query.pf
        , pfkey: query.pfkey
        , cmd: "QUERY"
    };
    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', attrs.appKey + '&').update('GET&' + urlencode(org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    //    Cookie里面需要包含的参数：
    //    session_id    用户账户类型，（手Q）session_id ="openid"；（微信）session_id = "hy_gameid"
    //    session_type  session类型，（手Q）session_type = "kp_actoken"；（微信）session_type = "wc_actoken"
    //    org_loc     	需要填写: /mpay/get_balance_m


    var j = request.jar();
    var cookie1 = request.cookie('org_loc=' + org_loc);

    j.setCookie(cookie1, url);


    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log('Options: ' + JSON.stringify(options));
    console.log(options);

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Error: ' + JSON.stringify(error));
        console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.list = retOut.list;
                retdata.data = retOut;
                retf(retdata);
            }
            else if (retOut.ret == "1001") {
                retdata.code = 1;
                retdata.msg = 'PARAM ERR';
                retdata.data = retOut;
                retf(retdata);
            }
            else if (retOut.ret == "1018") {
                retdata.code = 2;
                retdata.msg = 'NOT LOGIN';
                retdata.data = retOut;
                retf(retdata);
            }
            else {
                retdata.code = 99;
                retdata.msg = 'ERR';
                retdata.data = retOut;
                retf(retdata);
            }
        } else {
            retdata.code = -1;
            retdata.msg = 'NET ERROR';
            retf(retdata);
        }
    });
}

/*
 * 订阅物品赠送接口
 * */
function TCSubscribePresent(attrs, params, query, ret, retf) {
    var tcurl = 'http://msdk.qq.com';
    var org_loc = '/mpay/subscribe_m';
    var url = tcurl + org_loc;

    var data = {
        session_id: query.session_id
        , session_type: query.session_type
        , openid: query.openid
        , openkey: query.openkey
        , zoneid: query.zoneid
        , appid: attrs.appId
        , ts: Math.floor(Date.now() / 1000)
        //        平台来源，$平台-$渠道-$版本-$业务标识。
        //例如： openmobile_android-2001-android-xxxx
        , pf: query.pf
        , pfkey: query.pfkey
        , cmd: "PRESENT"
        , tss_inner_product_id: query.tss_inner_product_id
        , buy_quantity: query.buy_quantity
    };

    //data.sort(function (a, b) { return a[name] > b[name] ? 1 : -1 });
    data = logicCommon.sortObject(data);
    var querystring = require('querystring');
    var datastr = querystring.stringify(data);
    var urlencode = require('urlencode');
    data.sig = crypto.createHmac('sha1', attrs.appKey + '&').update('GET&' + urlencode(org_loc) + '&' + urlencode(datastr)).digest().toString('base64');

    //    Cookie里面需要包含的参数：
    //    session_id    用户账户类型，（手Q）session_id ="openid"；（微信）session_id = "hy_gameid"
    //    session_type  session类型，（手Q）session_type = "kp_actoken"；（微信）session_type = "wc_actoken"
    //    org_loc     	需要填写: /mpay/get_balance_m


    var j = request.jar();
    var cookie1 = request.cookie('org_loc=' + org_loc);

    j.setCookie(cookie1, url);


    var options = {
        url: url,
        jar: j,
        method: 'GET',
        qs: data
    };

    console.log('Options: ' + JSON.stringify(options));
    console.log(options);

    var retdata = {};

    request(options, function (error, response, body) {
        console.log('CB-Error: ' + JSON.stringify(error));
        console.log('CB-Response: ' + JSON.stringify(response));
        console.log('CP-Body: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);

            if (retOut.ret == "0") {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.inner_productid = retOut.inner_productid;
                retdata.begintime = retOut.begintime;
                retdata.data = retOut;
                retf(retdata);
            }
            else if (retOut.ret == "1001") {
                retdata.code = 1;
                retdata.msg = 'PARAM ERR';
                retdata.data = retOut;
                retf(retdata);
            }
            else if (retOut.ret == "1018") {
                retdata.code = 2;
                retdata.msg = 'NOT LOGIN';
                retdata.data = retOut;
                retf(retdata);
            }
            else {
                retdata.code = 99;
                retdata.msg = 'ERR';
                retdata.data = retOut;
                retf(retdata);
            }
        } else {
            retdata.code = -1;
            retdata.msg = 'NET ERROR';
            retf(retdata);
        }
    });
}

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    retf(true);
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.getServerMapById = getServerMapById;
exports.CreateChannelOrder = createChannelOrder;
exports.TCLogin = TCLogin;
exports.TCGetBalance = tcGetBalance;
exports.TCGetBalanceForAct = tcGetBalanceForAct;
exports.TCPay = tcPay;
exports.TCCancelPay = tcCancelPay;
exports.TCPresent = TCPresent;
exports.CheckTCLoginStatus = CheckTCLoginStatus;
exports.TCChargeCallBack = TCChargeCallBack;
exports.TCGradePackage = TCGradePackage;
exports.TCSubscribeQuery = TCSubscribeQuery;
exports.TCSubscribePresent = TCSubscribePresent;
exports.checkChOrder = checkChOrder;