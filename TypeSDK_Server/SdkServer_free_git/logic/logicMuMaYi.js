/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');

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

    ret.uid = cloned.id;
    ret.token = cloned.token;

    return true;
}

function callChannelLogin(attrs, params, query, ret, retf,gattrs) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);

    var options = {
        url: params.out_url,
        formData: cloned
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request.post(options, function optionalCallback(error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;

            if (retOut == "success") {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = cloned.uid;
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
            } else {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code = 1;
                ret.msg = "LOGIN ERROR";
                ret.id = "";
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }

        } else {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
            ret.code = 2;
            ret.msg = "OUT URL ERROR";
            ret.value = "";
        }

        retf(ret);
    });
}

function checkSignPay(attrs, query) {
    var sign = query.tradeSign;

    if (sign.length < 14) {
        return false;
    }
    var verityStr = sign.substring(0, 8);
    sign = sign.substring(8);
    var temp = crypto.createHash('md5').update(sign).digest('hex');
    if (verityStr != temp.substring(0, 8)) {
        return false;
    }
    var keyB = sign.substring(0, 6);

    var randKey = keyB + attrs.app_key;

    randKey = crypto.createHash('md5').update(randKey).digest('hex');
    var signStr = sign.substring(6);

    var signB = new Buffer(signStr, 'base64').toString();
    console.log(signB);
    var signLength = signB.length;

    var verfic = "";
    for (var i = 0; i < signLength; i++) {
        verfic += String.fromCharCode(signB[i].charCodeAt() ^ randKey[i % 32].charCodeAt());
    }
    return verfic == query.orderID;
};
function compareOrder(attrs, gattrs, params, query, ret, game, channel, retf) {
    var retValue = {};
    retValue.code = query.tradeState == 'success' ? 0 : 1;
    retValue.id = query.uid;
    retValue.order = query.orderID;
    retValue.cporder = query.productDesc || '';
    retValue.info = query.productName;

    if (retValue.code != '0') {
        retf('fail');
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf('fail');
            return;
        }
        else  if (query.app_order_id == params.orderdata && query.product_id == params.goodsid && query.amount >= params.goodsprice*0.9&&query.amount <= params.goodsprice)
        {
            var data  = {};
            data.code = '0000';
            data.msg = 'NORMAL';
            retf(data);
            return;
        }
        else {
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
                        retf('fail');
                        return;
                    }
                    if (retOut.code == '0') {
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (query.uid == retOut.id
                            && query.productDesc == retOut.cporder
                            && query.productPrice * 100 >= retOut.amount * 0.9
                            && query.productPrice * 100 <= retOut.amount) {
                            if (retOut.status == '2') {
                                retf('FAILURE');
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.productPrice * 100);
                                retf('success');
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
                            retf('fail');
                            return;
                        }
                    } else {
                        retf('fail');
                        return;
                    }
                } else {
                    retf('fail');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
    var retValue = {};
    retValue.code = query.tradeState == 'success' ? 0 : 1;
    retValue.id = query.uid;
    retValue.order = query.orderID;
    retValue.cporder = query.productDesc || '';
    retValue.info = query.productName;

    if (retValue.code != '0') {
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('fail');
        return;
    }
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
            retValue.amount = '' + query.productPrice * 100 + '';
            var options = {
                url: params.out_url,
                method: 'POST',
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
                        retf('fail');
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.productPrice * 100);
                        retf('success');
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('fail');
                    }
                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('fail');
                }
            });
        }
    });
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
function checkChOrder(game, channel,attrs, query, retf){
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.orderID,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.productDesc || '', query.orderID,function(res){
                if(res && typeof res != "undefined"){
                    isIllegal = true;
                    retf(isIllegal);
                }else{
                    retf(isIllegal);
                }
            });
        }else{
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