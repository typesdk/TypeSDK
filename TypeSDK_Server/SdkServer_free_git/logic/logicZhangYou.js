/**
 * Created by TypeSDK 2016/10/10.
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
    ret.uid = cloned.id.split('|',2)[1];
    ret.ucid = cloned.id.split('|',2)[0];
    ret.uuid = cloned.token;
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
function callChannelLogin(attrs, matchAction, query, ret, callBack,gattrs) {
    var keyNum = Object.keys(matchAction);
    var cloned = {};
    if (keyNum > 0) {
        cloned = merge(true, matchAction.out_params);
    } else {
        cloned = merge(true, {});
    }

    cloned.uid = query.uid;
    cloned.ucid = query.ucid;
    cloned.uuid = query.uuid;
    cloned.appId = attrs.app_id;
    var obj = sortOrderObject(cloned);
    //console.log(logicCommon.utf16to8(obj)+'signKey='+attrs.app_key);
    //cloned.sign = crypto.createHash('md5').update(logicCommon.utf16to8(obj)+'signKey='+attrs.app_key).digest('hex');
    console.log(obj +'signKey='+attrs.app_key);
    cloned.sign = crypto.createHash('md5').update(obj +'signKey='+attrs.app_key).digest('hex');

    var options = {
        url: matchAction.out_url,
        method: matchAction.method,
        body: cloned,
        json: true
    };

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    console.log(options);

    request(options, function (error, response, body) {

        if (error == null && response.statusCode == 200 && body != undefined) {
            var bodyData = JSON.parse(body);
            console.log(bodyData);

            if (bodyData.returnCode == 0) {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                var returnMsg = bodyData.msg;
                ret.code = bodyData.returnCode;
                ret.msg = "NORMAL";
                ret.id = query.ucid;
                ret.value = bodyData;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
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

/**
 * 验证参数签名
 *
 * @param attrs
 * @param query
 * @returns {boolean}
 */
function checkSignPay(attrs, query) {

    var queryData = null;
    var querySign = null;

    if (query != null && typeof query == 'string'){
        queryData = JSON.parse(query);
        querySign = query.sign;
    } else if (query != null && typeof query == 'object'){
        queryData = query;
        querySign = query.sign;
    }
    delete queryData.sign;
    var signStr = sortOrderObject(queryData);
    console.log(signStr);
    console.log("掌游验签字符串！！！！");
    signStr +='signKey='+ attrs.app_key;

    console.log(signStr);
    var osign = crypto.createHash('md5').update(logicCommon.utf16to8(signStr)).digest('hex');
    var osign = crypto.createHash('md5').update(signStr).digest('hex');

    console.log(querySign + " :: " + osign);

    if (querySign != osign) {
        return false;
    }

    return true;
}

function sortOrderObject(object) {
    var sortedObj = {},
            keys = Object.keys(object);

    keys.sort(function (key1, key2) {
        key1 = key1.toLowerCase(), key2 = key2.toLowerCase();
        if (key1 < key2) return -1;
        if (key1 > key2) return 1;
        return 0;
    });

    for (var index in keys) {
        var key = keys[index];
        if (typeof object[key] == 'object' && !(object[key] instanceof Array)) {
            sortedObj[key] = sortOrderObject(object[key]);
        } else {
            if (object[key] != '' && object[key] != null) {
                sortedObj[key] = object[key];
            }
        }
    }

    var signStr = '';
    for (var key in sortedObj) {
        signStr += key + "=" + sortedObj[key] + "&";
    }
    return signStr;
}

/**
 * 比较订单
 *
 * @param attrs
 * @param gattrs
 * @param params
 * @param query
 * @param ret
 * @param game
 * @param channel
 * @param retf
 */
function compareOrder(attrs, gattrs, params, query, ret, game, channel, retf) {
    var retValue = {};
    var bodyData = {};
    if (query != null && typeof query == 'string' ){
        bodyData = JSON.parse(query);
    } else if (query != null && typeof query == 'object'){
        bodyData = query;
    }

    retValue.code = 0;
    retValue.id = bodyData.uid;
    retValue.order = bodyData.sn;
    retValue.cporder = bodyData.vorderid;
    retValue.info = '';

    var retData = null;
    //  横向渠道的订单状态：3（订单成功）其他的为订单失败
    if (retValue.code !=0) {
        retf(JSON.stringify(retData));
        return;
    }

    console.log("支付中心结果!!!!");
    console.log(bodyData);
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retData = getRetData('FAIL');  //  11
            retf(JSON.stringify(retData));
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
            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1,0, query);

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

                    if (retOut.code == '0') {
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (bodyData.fee * 100 <= retOut.amount
                                && bodyData.fee * 100 >= retOut.amount * 0.9) {
                            console.log('1');
                            if (retOut.status == '2') {
                                retData = getRetData('FAIL');
                                retf(JSON.stringify(retData));
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,bodyData.fee * 100);
                                retData = getRetData('FAIL');
                                retf(JSON.stringify(retData));
                                return;
                            } else {
                                console.log('3');
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2,0);
                                var data = {};
                                data.code = '0000';
                                data.msg = 'NORMAL';
                                retf(data);
                                return;
                            }
                        } else {
                            console.log('2');
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,0);
                            retData = getRetData('FAIL');
                            retf(JSON.stringify(retData));
                            return;
                        }
                    } else {
                        retData = getRetData('FAIL');
                        retf(JSON.stringify(retData));
                        return;
                    }
                } else {
                    retData = getRetData('FAIL');
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
    if (query != null && typeof query == 'string' ){
        bodyData = JSON.parse(query);
    } else if (query != null && typeof query == 'object'){
        bodyData = query;
    }

    retValue.code = 0;
    retValue.id = bodyData.uid;
    retValue.order = bodyData.sn;
    retValue.cporder = bodyData.vorderid;
    retValue.info = '';

    var retData = null;
    console.log("callGamePay!!!!!!!!!!!!!");
    console.log(retValue);
    if(retValue.code!=0){
        retf('FAIL');
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf("FAIL");
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + bodyData.fee * 100 + '';


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
                console.log("callGamePay中outURL游戏服返回数据");
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAIL');
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,bodyData.fee * 100);
                        retf("SUCCESS");
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf("FAIL");
                    }
                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf("FAIL");
                }
            });
        }
    });
}

/**
 * 检查订单
 *
 * @returns {boolean}
 */
function checkOrder() {
    return false;
}

function getRetData (msg){
    var temp = {
        status:0,
        message: ''
    };
    switch (msg){
        case 'SUCCESS':
            temp.status = "0000";
            temp.message = 'SUCCESS';
            break;
        case 'FAIL':
            temp.status = "0001";
            temp.message = '处理异常';
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
    if (query != null && typeof query == 'string' ){
        bodyData = JSON.parse(query);
    } else if (query != null && typeof query == 'object'){
        bodyData = query;
    }

    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,bodyData.sn,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, bodyData.vorderid, bodyData.sn,function(res){
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
exports.checkOrder = checkOrder;
exports.compareOrder = compareOrder;
exports.callGamePay = callGamePay;
exports.checkChOrder = checkChOrder;
