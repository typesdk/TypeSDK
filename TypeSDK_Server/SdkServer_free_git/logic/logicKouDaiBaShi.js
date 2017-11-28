/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var rsa = require('node-rsa');


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


function callChannelLogin(attrs, params, query, ret, retf,gattrs) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);
    cloned.access_token = query.token;
    var options = {
        url: params.out_url,
        method: params.method,
        qs: cloned
    };

    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            if (retOut.code == '0') {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.id;
                ret.nick = "";
                ret.token = query.token;
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

function compareOrder(attrs,gattrs,params,query,ret,game,channel,retfa){
    var retValue = {};
    retValue.code = 0;
    retValue.id = query.userId;
    retValue.order = query.pOrder|| "";
    retValue.cporder =  query.appOrder || "";
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retfa('fail');
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
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,0,query);
            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };
            request(options, function (error, response, body) {
                if(!error && response.statusCode == 200){
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        retfa('FAILURE');
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code=='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(retValue.cporder==retOut.cporder
                            &&query.amount * 100>=retOut.amount*0.9
                            &&query.amount * 100<=retOut.amount){
                            if(retOut.status=='2'){
                                var ret = {};
                                ret.code = '1';
                                ret.msg = 'error';
                                retfa(ret);
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount * 100);
                                var ret = {};
                                ret.code = '0';
                                ret.msg = 'success';
                                retfa(ret);
                                return;
                            }else{
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,0);

                                ret.code = '0000';
                                ret.msg = 'NORMAL';
                                retfa(ret);
                                return;
                            }
                        }else{
                            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,3,0);
                            var ret = {};
                            ret.code = '1';
                            ret.msg = 'error';
                            retfa(ret);
                            return;
                        }
                    }else{
                        var ret = {};
                        ret.code = '1';
                        ret.msg = 'error';
                        retfa(ret);
                        return;
                    }
                }else{
                    var ret = {};
                    ret.code = '1';
                    ret.msg = 'error';
                    retfa(ret);
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
    var tempPublicKey = attrs.public_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");

    beginArray = beginArray.concat(strArray);
    for (var i = 0; i < beginArray.length; i++) {
        if (i != beginArray.length - 1)
            beginArray[i] = beginArray[i] + "\r\n";
    }
    var publickey = beginArray.join("");

    var key = new rsa(publickey);
    var strSign = key.decryptPublic(query.req_data, 'utf8');
    var arrSign = strSign.split('&');
    var jsonSign = '';
    for (var i = 0; i < arrSign.length; i++) {
        jsonSign += '"' + arrSign[i].split('=')[0] + '":"' + arrSign[i].split('=')[1] + '",';
    }
    jsonSign = "{" + jsonSign.substr(0, jsonSign.length - 1) + "}";
    //var data = encodeURIComponent.parse(strSign);
    var data = JSON.parse(jsonSign);
    if (data.payStatus == "1") {
        var retValue = {};
        retValue.code = 0;
        retValue.id = data.userId;
        retValue.order = data.pOrder;
        retValue.cporder = data.appOrder;
        retValue.info = "";

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
                retValue.amount = '' + query.amount + '';

                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1,0, query);

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
                            var ret = {};
                            ret.code = '1';
                            ret.msg = 'error';
                            retf(ret);
                        }

                        if (retOut.code == 0) {
                            //打点：服务器正确处理支付成功回调
                            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.amount*100);
                            var ret = {};
                            ret.code = '0';
                            ret.msg = 'success';
                            retf(ret);
                            //retf('FAILURE');
                        }
                        else {
                            //打点：其他支付失败
                            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                            var ret = {};
                            ret.code = '1';
                            ret.msg = 'error';
                            retf(ret);
                        }
                    } else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        var ret = {};
                        ret.code = '1';
                        ret.msg = 'error';
                        retf(ret);
                    }
                });
            }
        });
    }
    else {
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('error');
    }
}


function checkSignPay(attrs, query) {

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
function checkChOrder(game, channel,attrs, query, retf){
    var tempPublicKey = attrs.public_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");

    beginArray = beginArray.concat(strArray);
    for (var i = 0; i < beginArray.length; i++) {
        if (i != beginArray.length - 1)
            beginArray[i] = beginArray[i] + "\r\n";
    }
    var publickey = beginArray.join("");

    var key = new rsa(publickey);
    var strSign = key.decryptPublic(query.req_data, 'utf8');
    var arrSign = strSign.split('&');
    var jsonSign = '';
    for (var i = 0; i < arrSign.length; i++) {
        jsonSign += '"' + arrSign[i].split('=')[0] + '":"' + arrSign[i].split('=')[1] + '",';
    }
    jsonSign = "{" + jsonSign.substr(0, jsonSign.length - 1) + "}";
    //var data = encodeURIComponent.parse(strSign);
    var data = JSON.parse(jsonSign);
    if (data.payStatus != "1") {
        retf(false);
        return;
    }

    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,data.pOrder,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, data.appOrder, data.pOrder,function(res){
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
exports.checkChOrder = checkChOrder;
exports.compareOrder = compareOrder;