/**
 * Created by TypeSDK on 2017/2/9.
 */

var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var rsa = require('node-rsa');

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
        if (org[i] == cloned[i] && i != "data" && i != "id") {
            return false;
        }

        //判断参数中是否有为空的字段
        if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data" && i != "id") {
            return false;
        }
    }

    ret.id = Date.now();
    ret.tokenid = cloned.token;
    //ret.sign = cloned.sign;

    return true;
}


/**
 * 获取用户系统验证信息
 *
 * @param attrs
 * @param query
 * @param matchAction\
 * @param ret
 * @param callBack
 * @returns {{url: *, method: *, body: {}, json: boolean}}
 */
function callChannelLogin(attrs, matchAction, query, ret, callBack) {
    var keyNum = Object.keys(matchAction);
    var cloned = {};
    if (keyNum > 0) {
        cloned = merge(true, matchAction.out_params);
    } else {
        cloned = merge(true, {});
    }
    cloned.appid = attrs.app_id;
    cloned.tokenid = query.tokenid;

    var options = {
        url: matchAction.out_url + "?" + "appid=" + cloned.appid + "&" + "tokenid=" + cloned.tokenid
    };
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    console.log(options);
    request(options, function (error, response, body) {

        if (error == null && response.statusCode == 200 && body != undefined) {
            var bodyData = JSON.parse(body);
            console.log(bodyData);
            if (bodyData.status == 0) {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = bodyData.status;
                ret.msg = bodyData.message;
                ret.id = bodyData.data.account_id;
                ret.nick = bodyData.data.nickname;
                ret.token = cloned.tokenid;
                ret.value = bodyData;
            } else {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code = 1;
                ret.msg = "渠道正常返回，结果失败";
                ret.value = bodyData;
            }
        } else {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
            ret.code = 2;
            ret.msg = "OUT URL ERROR";
            ret.value = '';
        }

        callBack(ret);
    });

}

function checkOrder() {
    return false;
}

/**
 * 检查支付签名
 *
 * @param attrs
 * @param query
 * @returns {boolean}
 */
function checkSignPay(attrs, query) {
    var strCpOrderId = '';
    var queryData = null;
    var querySign = null;

    if (query.data != null && typeof query.data == 'string'){
        queryData = JSON.parse(query.data);
        querySign = query.sign;
    } else if (query.data != null && typeof query.data == 'object'){
        queryData = query.data;
        querySign = query.sign;
    }
    if (typeof queryData.cpOrderId != 'undefined' && queryData.cpOrderId != 0) {
        strCpOrderId = 'cpOrderId=' + queryData.cpOrderId;
    }
    var signStr = 'accountId=' + queryData.accountId +
            'amount=' + queryData.amount +
            'appId=' + queryData.appId +
            strCpOrderId +
            'orderId=' + queryData.orderId +
            'orderStatus=' + queryData.orderStatus +
            attrs.secret_key;
    console.log(signStr);

    var osign = crypto.createHash('md5').update(signStr).digest('hex');

    console.log(querySign + " :: " + osign);

    if (querySign != osign) {
        return false;
    }

    return true;
}
/**
 * 订单信息对比
 * @param attrs
 * @param gattrs
 * @param params
 * @param query
 * @param ret
 * @param game
 * @param channel
 * @param retf
 */
function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    var bodyData = {};
    if (query.data != null && typeof query.data == 'string' ){
        bodyData = JSON.parse(query.data);
    } else if (query.data != null && typeof query.data == 'object'){
        bodyData = query.data;
    }
    retValue.code = bodyData.orderStatus==3?0:1;
    retValue.id = '' + bodyData.accountId;
    retValue.order = bodyData.orderId;
    retValue.cporder = bodyData.cpOrderId;
    retValue.info = '';
    var retData = null;
    if(retValue.code!=0){
        retData = getRetData('ORDER_ERROR');
        retf(JSON.stringify(retData));
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retData = getRetData('NO_ORDER');  //  11
            retf(JSON.stringify(retData));
            return;
        } else {

            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1,parseInt( bodyData.amount), query);

            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };
            console.log("通知游戏服检查订单：");
            console.log(options);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    console.log("compareOrder中verifyURL游戏服返回数据");
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        retData = getRetData('VERIFY_ORDER_ERROR');
                        retf(JSON.stringify(retData));
                        return;
                    }
                    if (retOut.code == '0') {
                        /*&&query.data.amount*100<=retOut.amount
                         &&query.data.amount*100>=retOut.amount*0.9*/
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (bodyData.amount  <= retOut.amount
                            && bodyData.amount >= retOut.amount * 0.9) {
                            console.log('1');
                            if (retOut.status == '2') {
                                retData = getRetData('VERIFY_ORDER_ERROR');
                                retf(JSON.stringify(retData));
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,parseInt( bodyData.amount));
                                retData = getRetData('VERIFY_ORDER_ERROR');
                                retf(JSON.stringify(retData));
                                return;
                            } else {
                                console.log('3');
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2,parseInt( bodyData.amount));
                                var data = {};
                                data.code = '0000';
                                data.msg = 'NORMAL';
                                retf(data);
                                return;
                            }
                        } else {
                            console.log('2');
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,parseInt( bodyData.amount));
                            retData = getRetData('VERIFY_ORDER_ERROR');
                            retf(JSON.stringify(retData));
                            return;
                        }
                    } else {
                        retData = getRetData('VERIFY_ORDER_ERROR');
                        retf(JSON.stringify(retData));
                        return;
                    }
                } else {
                    retData = getRetData('VERIFY_ORDER_ERROR');
                    retf(JSON.stringify(retData));
                    return;
                }
            });
        }
    });
}

/**
 * 通知支付结果
 *
 * @param attrs
 * @param gattrs
 * @param params
 * @param query
 * @param ret
 * @param retf
 * @param game
 * @param channel
 * @param channelId
 */
function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
    var retValue = {};
    var bodyData = {};
    if (query.data != null && typeof query.data == 'string' ){
        bodyData = JSON.parse(query.data);
    } else if (query.data != null && typeof query.data == 'object'){
        bodyData = query.data;
    }

    retValue.code = bodyData.orderStatus==3?0:1;
    retValue.id = '' + bodyData.accountId;
    retValue.order = bodyData.orderId;
    retValue.cporder = bodyData.cpOrderId;
    retValue.info = '';
    if(retValue.code!=0){
        retData = getRetData('ORDER_ERROR');
        retf(JSON.stringify(retData));
        return;
    }

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        var retData = null;
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retData = getRetData('NO_ORDER');
            retf(retData);
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);

            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + bodyData.amount + '';


            var options = {
                url: params.out_url,
                method: 'POST',
                body: retValue,
                json: true
            };
            console.log("请求appStore");
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
                        retData = getRetData('NOTIFY_ORDER_ERROR');
                        retf(retData);
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4);
                        retData = getRetData('SUCCESS');
                        retf(retData);
                        //retf('FAILURE');
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retData = getRetData('NOTIFY_ORDER_ERROR');
                        retf(retData);
                    }
                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retData = getRetData('NOTIFY_ORDER_ERROR');
                    retf(retData);
                }
            });
        }
    });
}

function getRetData (msg){
    var temp = {
        code:0,
        msg: ''
    };
    switch (msg){
        case 'SUCCESS':
            temp.code = 0;
            temp.msg = 'SUCCESS';
            break;
        case 'NO_ORDER':
            temp.code = 1;
            temp.msg = '订单不存在';
            break;
        case 'VERIFY_ORDER_ERROR':
            temp.code = 1;
            temp.msg = '订单校验异常';
            break;
        case 'ORDER_ERROR':
            temp.code = 1;
            temp.msg = '支付中心订单异常';
            break;
        case 'NOTIFY_ORDER_ERROR':
            temp.code = 1;
            temp.msg = '订单通知游戏服异常';
            break;
        case 'FAILURE':
            temp.code = 1;
            temp.msg = '未处理异常';
            break;
        default :
            break
    }
    return temp;
}

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var bodyData = {};
    if (query.data != null && typeof query.data == 'string' ){
        bodyData = JSON.parse(query.data);
    } else if (query.data != null && typeof query.data == 'object'){
        bodyData = query.data;
    }
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,bodyData.orderId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, bodyData.cpOrderId, bodyData.orderId,function(res){
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

exports.checkOrder = checkOrder;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.compareOrder =compareOrder;
exports.checkChOrder = checkChOrder;