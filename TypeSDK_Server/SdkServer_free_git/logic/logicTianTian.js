/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var URL = require('url');

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
        if (org[i] == cloned[i] && i != "data" && i != "id") {
            return false;
        }

        //判断参数中是否有为空的字段
        if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data" && i != "id") {
            return false;
        }
    }
    ret.token = cloned.token;
    return true;
}
function createSign(attrs, signUrl, data) {
    var p = URL.parse(signUrl);
    var str = p.pathname + JSON.stringify(data) + attrs.app_key;
    console.log(str);
    var osign = crypto.createHash('md5').update(str).digest('hex');
    return osign;

}

function callChannelLogin(attrs, params, query, ret, retf) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);
    var retData = {};
    var data = {
        "cpId": attrs.sdk_cp_id,
        "token": cloned.token
    };
    retData.data = data;
    retData.sign = createSign(attrs, params.out_url, data);

    var options = {
        url: params.out_url,
        method: params.method,
        qs: retData
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        console.log('channelLoginError>>>>'+JSON.stringify(error));
        console.log('channelLoginBody>>>>'+JSON.stringify(body));
        console.log('channelLogincallbackResponse>>>>'+response.statusCode);
        if (!error && response.statusCode == 200) {
            var retOut = body;
            console.log(retOut);
            if (retOut.status == '1') {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.id;
                ret.nick = retOut.data.nickname;
                ret.token = "";
                ret.value = retOut;
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

function compareOrder(attrs, gattrs, params, query, ret, game, channel, retf) {
    var retValue = {};
    retValue.id = query.cpId;
    retValue.order = query.orderId;
    //  todo private字段
    retValue.cporder = query.private;
    retValue.info = '';

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf('{FAILURE}');
            return;
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1, 0,query);
            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };

            request(options, function (error, response, body) {

                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retf('{FAILURE}');
                        return;
                    }
                    console.log(retOut);
                    if (retOut.code == '0') {
                        if (retOut.Itemid) {
                            logicCommon.mapItemLists(attrs, retOut);
                        }
                        if (query.money * 100 >= retOut.amount * 0.9
                                && query.money * 100 <= retOut.amount) {
                            if (retOut.status == '2') {
                                retf('FAILURE');
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.money * 100);
                                retf('ok');
                                return;
                            } else {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2,0);
                                var data = {};
                                data.code = '0000';
                                data.msg = 'NORMAL';
                                retf(data);
                                return;
                            }
                        } else {
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,0);
                            retf('FAILURE');
                            return;
                        }
                    } else {
                        retf('FAILURE');
                        return;
                    }
                } else {
                    retf('FAILURE');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {

    var retValue = {};

    retValue.id = query.cpId;
    retValue.order = query.orderId;
    retValue.cporder = query.private;
    retValue.info = '';

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf('FAILURE');
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.money * 100 + '';
            var options = {
                url: params.out_url,
                method: params.method,
                body: retValue,
                json: true
            };
            console.log(options);
            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');
                        return;
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.money * 100);
                        retf('ok');
                    } else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');
                    }

                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('FAILURE');
                }
            });
        }
    });
}

function checkSignPay(attrs, query) {
    //参数中应该要配上 回调地址

    var osign = createSign(attrs, attrs.pay_call_back_url, query.data);
    console.log(query.sign + " :: " + osign);

    if (query.sign != osign) {
        return false;
    }

    return true;
}

function checkOrder() {
    return false;
}

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel, attrs, query, retf) {
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel, query.orderId, function (res) {
        if (!res || typeof res == "undefined") {
            logicCommon.saveCHOrderInRedis(game, channel, query.private, query.orderId, function (res) {
                if (res && typeof res != "undefined") {
                    isIllegal = true;
                    retf(isIllegal);
                } else {
                    retf(isIllegal);
                }
            });
        } else {
            retf(isIllegal);
        }
    });
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;