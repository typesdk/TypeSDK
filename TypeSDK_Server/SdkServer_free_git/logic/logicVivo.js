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
        if (org[i] == cloned[i] && i != "sign" && i != "data") {
            return false;
        }
        //判断参数中是否有为空的字段
        if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "sign" && i != "data") {
            return false;
        }
    }
    ret.authtoken = cloned.token;
    return true;
}

function callChannelLogin(attrs, params, query, ret, retf,gattrs) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);
    var options = {
        url: params.out_url,
        method: params.method,
        form: cloned,
        json: true
    };
    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;
            console.log(retOut);

            if (retOut.retcode == '0') {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.openid;
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

function compareOrder(attrs, gattrs, params, query, ret, game, channel, retf) {
    var retValue = {};
    retValue.code = query.tradeStatus == '0000' ? 0 : 1;
    retValue.id = query.uid;
    retValue.order = query.orderNumber;
    retValue.cporder = query.cpOrderNumber;
    retValue.info = query.extInfo;
    if (retValue.code != '0') {
        retf('FAILURE');
        return;
    }
    var retData = query;
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf('FAILURE');
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
            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1,0, retData);

            console.log(query);
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
                        retf('FAILURE1');
                        return;
                    }
                    console.log(retOut);
                    if (retOut.code == '0') {
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (parseInt(query.orderAmount)>=retOut.amount/100*0.9&&parseInt(query.orderAmount)<=retOut.amount/100) {
                            if (retOut.status == '2') {
                                retf('FAILURE2');
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,parseInt(query.orderAmount)*100);
                                retf('SUCCESS');
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
                            retf('FAILURE3');
                            return;
                        }
                    } else {
                        retf('FAILURE4');
                        return;
                    }
                } else {
                    retf('FAILURE5');
                    return;
                }
            });
        }
    });
}


function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
    var retValue = {};
    retValue.code = query.tradeStatus == '0000' ? 0 : 1;
    retValue.id = query.uid;
    retValue.order = query.orderNumber;
    retValue.cporder = query.cpOrderNumber;
    retValue.info = query.extInfo;

    if (retValue.code != '0') {
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('FAILURE');
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
            retValue.amount = '' + query.orderAmount + '';

            var options = {
                url: params.out_url,
                method: params.method,
                body: retValue,
                json: true
            };

            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    //日志记录CP端返回
                    if (typeof retOut.code == 'undefined') {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,parseInt(query.orderAmount)*100);
                        retf('SUCCESS');
                    }
                    else {
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
    var keymd5 = crypto.createHash('md5').update(attrs.app_key).digest('hex').toLowerCase();
    var strdata = getSignStr(query);
    var osign = crypto.createHash('md5').update(strdata + '&' + keymd5).digest('hex').toLowerCase();
    console.log('checkSignPay:' + osign + '::' + query.signature);
    if (query.signature != osign) {
        return false;
    }
    return true;
}

function checkOrder(attrs, params, query, ret, retf) {
    return false;
}

function CreateChannelOrderSign(query, attrs) {

    var keymd5 = crypto.createHash('md5').update(attrs.app_key).digest('hex').toLowerCase();
    var strdata = getSignStr(query);
    return crypto.createHash('md5').update(strdata + '&' + keymd5).digest('hex').toLowerCase();
}

function GetNowStr() {
    var util = require('util');
    var now = new Date();

    pad = function (tbl) {
        return function (num, n) {
            return (0 >= (n = n - num.toString().length)) ? num : (tbl[n] || (tbl[n] = Array(n + 1).join(0))) + num;
        }
    }([]);
    var result = '' + now.getFullYear() +
            pad(now.getMonth() + 1, 2) +
            pad(now.getDate(), 2) +
            pad(now.getHours(), 2) +
            pad(now.getMinutes(), 2) +
            pad(now.getSeconds(), 2);

    return result;
}

function CheckChannelOrderReturnSign(query, attrs) {
    var keymd5 = crypto.createHash('md5').update(attrs.app_key).digest('hex').toLowerCase();
    var strdata = getSignStr(query);
    var osign = crypto.createHash('md5').update(strdata + '&' + keymd5).digest('hex').toLowerCase();
    console.log('CheckChannelOrderReturnSign:' + osign + '::' + query.signature);
    return (query.signature == crypto.createHash('md5').update(strdata + '&' + keymd5).digest('hex').toLowerCase());
}


function getSignStr(query) {

    var arrKey = Object.keys(query).map(function (k) {
        return k;
    });
    var arr = new Array();
    for (var i in query) {
        arr[i] = query[i];
    }

    arrKey.sort();

    var arr2 = new Array();
    var key = "";

    var k = "";
    for (var j in arrKey) {
        k = arrKey[j];
        arr2[k] = arr[k];
    }

    var str = "";
    for (var key in arr2) {
        if (key != 'signMethod' && key != 'signature')
            str += key + '=' + arr2[key] + '&';
    }
    console.log(str);
    var signStr = str.substr(0, str.length - 1);
    return signStr;
}

function CreateChannelOrder(attrs, params, query, ret, retf) {
    var data = {
        "version": "1.0.0"
        , "signMethod": "MD5"
        , "signature": ""
        , "cpId": attrs.sdk_cp_id
        , "appId": attrs.app_id
        , "cpOrderNumber": query.cporder
        , "notifyUrl": attrs.pay_call_back_url
        , "orderTime": GetNowStr()
        , "orderAmount": query.price
        , "orderTitle": query.subject
        , "orderDesc": query.subject
        , "extInfo": query.data || "aa"
    };

    data.signature = CreateChannelOrderSign(data, attrs);

    var options = {
        url: 'https://pay.vivo.com.cn/vcoin/trade',
        method: 'POST',
        form: data,
        json: true
    };
    console.log(options);

    var retdata = {
        "code": -99
        , "playerid": ""
        , "order": ""
        , "cporder": ""
        , "submit_time": data.orderTime
        , "msg": "UNKNOWN ERROR"
        , "data": {}
    };

    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;

            //日志记录CP端返回
            console.log(retOut);
            if (typeof retOut.respCode == 'undefined') {
                retdata.code = -2;
                retdata.msg = 'FORMAT ERROR';
                retf(retdata);
            }

            if (retOut.respCode == "200" && CheckChannelOrderReturnSign(retOut, attrs)) {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.playerid = query.playerid;
                retdata.order = retOut.orderNumber;
                retdata.cporder = query.cporder;
                retdata.data = retOut;
                retf(retdata);
            }
            else {
                retdata.code = 1;
                retdata.msg = 'RETURN ERROR';
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
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.orderNumber,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.cpOrderNumber, query.orderNumber,function(res){
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
exports.CreateChannelOrder = CreateChannelOrder;
exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;