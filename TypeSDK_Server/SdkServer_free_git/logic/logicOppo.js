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
        if (org[i] == cloned[i] && i != "id" && i != "data") {
            return false;
        }

        //判断参数中是否有为空的字段
        if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "id" && i != "data") {
            return false;
        }
    }

    var token = cloned.token;
    if(token.indexOf('%')>0){
        token = decodeURIComponent(token);
    }
    ret.token = token;
    ret.ssoid = cloned.id;
    return true;
}

function createSignLogin(basestring, key) {

    var url = "http://thapi.nearme.com.cn/account/GetUserInfoByGame";
    return encodeURIComponent(crypto.createHmac('sha1', key).update(basestring).digest('base64'));

}

function callChannelLogin(attrs, params, query, ret, retf) {
    var cloned = merge(true, params.out_params);
    merge(cloned, query);
    var token_key = query.token;
    var Signkey = encodeURIComponent(attrs.secret_key) + "&";
    var nonce = Math.floor((Math.random() * 100000000) + 99999999);
    var auth = "oauthConsumerKey=" + encodeURIComponent(attrs.app_key) + '&' +
        "oauthToken=" + encodeURIComponent(token_key) + '&' +
        "oauthSignatureMethod=" + encodeURIComponent("HMAC-SHA1") + "&" +
        "oauthTimestamp=" + encodeURIComponent(Date.now()) + "&" +
        "oauthNonce=" + encodeURIComponent(nonce) + "&" +
        "oauthVersion=" + encodeURIComponent("1.0") + "&";

    var Sign = createSignLogin(auth, Signkey);

    var options = {
        url: params.out_url,
        method: params.method,

        qs: {
            'token': cloned.token,
            'fileId': cloned.ssoid
        },

        headers: {
            'param': auth,
            'oauthSignature': Sign
        }
    };
    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        console.log(error);
        console.log(body);
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            if (retOut.resultMsg == "正常") {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = cloned.ssoid;
                ret.nick = retOut.userName;
                ret.token = "";
                ret.value = retOut;
            }else{
                ret.code = -3;
                ret.msg = "验证失败";
                ret.id = '';
                ret.nick = '';
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
    retValue.code = 0;
    retValue.id = query.attach;
    retValue.order = query.notifyId;
    retValue.cporder = query.partnerOrder;
    retValue.info = query.productName;
    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf('FAILURE');
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
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined') {
                        retf('FAILURE');
                        return;
                    }
                    if (retOut.code == '0') {
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (query.price >= retOut.amount * 0.9
                            && query.price <= retOut.amount) {
                            if (retOut.status == '2') {
                                retf('FAILURE');
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.price);
                                retf('ok');
                                return;
                            } else {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2,0);
                                var data = {};
                                data.code = '0000';
                                data.msg = 'NORMal';
                                retf(data);
                                return;
                            }
                        } else {
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,0);
                            retf('result=FAIL&Msg=订单信息不匹配');
                            return;
                        }
                    } else {
                        retf('result=FAIL&Msg=游戏服务器错误');
                        return;
                    }

                } else {
                    retf('result=FAIL&Msg=游戏服务器错误');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {

    var retValue = {};
    retValue.code = 0;
    retValue.id = query.attach;
    retValue.order = query.notifyId;
    retValue.cporder = query.partnerOrder;
    retValue.info = query.productName;
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
            retValue.amount = '' + query.price + '';


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
                    if (typeof retOut.code == 'undefined') {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('result=FAIL&Msg=游戏服务器错误');
                    }

                    if (retOut.code == '0') {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.price);
                        retf('result=OK&resultMsg=成功');
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('result=FAIL&Msg=游戏服务器错误');
                    }
                } else {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('result=FAIL&Msg=游戏服务器错误');
                }
            });
        }
    });
}

function checkSignPay(attrs, query) {

    var tempPublicKey = attrs.product_key;
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
    var str =
        "notifyId=" + query.notifyId + "&" +
        "partnerOrder=" + query.partnerOrder + "&" +
        "productName=" + query.productName + "&" +
        "productDesc=" + query.productDesc + "&" +
        "price=" + query.price + "&" +
        "count=" + query.count + "&" +
        "attach=" + query.attach;

    var v = crypto.createVerify('sha1WithRSAEncryption');
    v.update(str);
    return v.verify(publickey, query.sign, 'base64');

}

function checkOrder(attrs, params, query, ret, retf) {
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
    logicCommon.selCHOrderInRedis(channel,query.notifyId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.partnerOrder, query.notifyId,function(res){
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
