/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var rsa = require('node-rsa');
var urldecode = require("urldecode");
var buildBuffer = require("./buildRequestBody").buildBuffer;

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

    ret.id = query.id;
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
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    ret.code = 0;
    ret.msg = "";
    ret.id = query.id;
    ret.nick = "";
    ret.token = "";
    ret.value = "";
    callBack(ret);
}

/**
 * 验证参数签名
 *
 * @param attrs
 * @param query
 * @returns {boolean}
 */
function checkSignPay(attrs, query) {
    var orderToJSON = "";
    try{
        orderToJSON = JSON.parse(urldecode(query.order));
    }catch(e){
        console.log("SDKError: 聚乐支付解析串为非标准JSON");
        return false;
    }

    for(key in orderToJSON){
        if(key == 'gmt_create' || key == 'gmt_payment'){
            orderToJSON[key] = orderToJSON[key].replace("+"," ");
        }
    }

    var queryData = JSON.stringify(orderToJSON);
    var querySign = urldecode(query.sign);

    //  获取signKey
    var tempPublicKey = attrs.secret_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");

    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\r\n";
    }
    var publickey  = beginArray.join("");

    var v = crypto.createVerify('SHA1');
    v.update(queryData, 'utf8');
    var flag = v.verify(publickey, querySign, 'base64');

    if(flag){
        console.log('Sign Success');
    }else{
        console.log('Sign Error');
    }

    return flag;
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
    var orderToJSON = "";
    try{
        orderToJSON = JSON.parse(urldecode(query.order));
    }catch(e){
        console.log("SDKError: 聚乐支付解析串为非标准JSON");
        retf("success");
        return;
    }

    for(key in orderToJSON){
        if(key == 'gmt_create' || key == 'gmt_payment'){
            orderToJSON[key] = orderToJSON[key].replace("+"," ");
        }
    }

    var orderData = orderToJSON;

    retValue.code = orderData.result_code == 1 ? 0 : 1;
    retValue.id = "";
    retValue.order = orderData.jolo_order_id;
    retValue.cporder = orderData.game_order_id;
    retValue.info = orderData.result_msg;

    //  聚乐渠道的订单状态：1（订单成功）其他的为订单失败
    if (retValue.code != 0) {
        retf("success");
        return;
    }

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf("success");
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
            console.log("通知游戏服检查订单：");
            console.log(options);
            request(options, function (error, response, body) {
                console.log("CB-Error: " + JSON.stringify(error));
                console.log("CB-Body: " + JSON.stringify(body));

                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retf("success");
                        return;
                    }
                    if (retOut.code == '0') {
                        /*&&query.data.amount*100<=retOut.amount
                         &&query.data.amount*100>=retOut.amount*0.9*/
                        if (retOut.Itemid) {
                            logicCommon.mapItemLists(attrs, retOut);
                        }
                        if (orderData.real_amount  <= retOut.amount
                                && orderData.real_amount  >= retOut.amount * 0.9) {
                            if (retOut.status == '2') {
                                retf("success");
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,orderData.real_amount);
                                retf("success");
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
                            retf("success");
                            return;
                        }
                    } else {
                        retf("success");
                        return;
                    }
                } else {
                    retf("success");
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
    var orderToJSON = "";
    try{
        orderToJSON = JSON.parse(urldecode(query.order));
    }catch(e){
        console.log("SDKError: 聚乐支付解析串为非标准JSON");
        retf("success");
        return;
    }

    for(key in orderToJSON){
        if(key == 'gmt_create' || key == 'gmt_payment'){
            orderToJSON[key] = orderToJSON[key].replace("+"," ");
        }
    }

    var orderData = orderToJSON;

    retValue.code = orderData.result_code == 1 ? 0 : 1;
    retValue.id = "";
    retValue.order = orderData.jolo_order_id;
    retValue.cporder = orderData.game_order_id;
    retValue.info = orderData.result_msg;

    if (retValue.code != 0) {
        retf("success");
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf("success");
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = orderData.real_amount + '';
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
                console.log("CB-Error: " + JSON.stringify(error));
                console.log("CB-Body: " + JSON.stringify(body));
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf("success");
                    }

                    if (retOut.code == 0) {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,orderData.real_amount);
                        retf("success");
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf("success");
                    }
                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf("success");
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

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var orderData = {};
    if (query.order != null && typeof query.order == 'string') {
        orderData = JSON.parse(query.order);
    } else if (query.order != null && typeof query.order == 'object') {
        orderData = query.order;
    }

    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,orderData.jolo_order_id,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, orderData.game_order_id, orderData.jolo_order_id,function(res){
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

/**
 * 解析并格式化渠道请求
 * */
function parseBody(req, retf){
    buildBuffer(req, function(bufferStr){
        if(typeof bufferStr == "string" && bufferStr.length > 0){
            var requestBody = {};
            var reqArr = bufferStr.split("&");
            for(var key in reqArr){
                var key1 = reqArr[key].split("=")[0];
                var val1 = reqArr[key].split("=")[1];
                requestBody[key1] = val1.substr(1,val1.length - 2);
            }
            retf({code:1, data: requestBody});
        }else{
            //空请求
            retf({code:0, data: {}});
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
exports.parseBody = parseBody;