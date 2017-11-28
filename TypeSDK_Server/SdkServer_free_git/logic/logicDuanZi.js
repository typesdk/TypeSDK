/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');

function convertParamLogin(query,ret)
{
    var org =
    {
        "id" : "0"
        ,"token": ""
        ,"data":""
        ,"sign":""
    };

    var cloned = merge(true, org);
    merge(cloned,query);

    for(var i in cloned)
    {
        //判断参数中是否该有的字段齐全
        if(org[i] == cloned[i] && i != "data" && i != "id")
        {
            return false;
        }

        //判断参数中是否有为空的字段
        if(0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data" && i != "id")
        {
            return false;
        }
    }

    ret.access_token = cloned.token;
    ret.uid = cloned.id;
    return true;
}

function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.client_key = attrs.app_id;
    cloned.check_safe = '0';

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
            var retOut =JSON.parse(body);
            if(retOut.message == 'success'){
                if(retOut.data.verify_result=='1'){
                    //打点：验证成功
                    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                    ret.code = 0;
                    ret.msg = "NORMAL";
                    ret.id = cloned.uid;
                    ret.nick = '';
                    ret.token = "";
                    ret.value = retOut;
                    logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
                }else{
                    //打点：验证失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                    ret.code = 1;
                    ret.msg = "LOGIN User ERROR";
                    ret.id = cloned.uid;
                    ret.nick = '';
                    ret.token = "";
                    ret.value = retOut;
                }
            }
            else
            {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);

                ret.code =  1;
                ret.msg = "LOGIN User ERROR";
                ret.id = "";
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }
        }
        else
        {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);

            ret.code = 2;
            ret.msg = "OUT URL ERROR";
            ret.value = "";
        }
        retf(ret);
    });
}
function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code = query.trade_status  == ('0' || '3') ? 0 : 1;
    retValue.id = query.client_id;
    retValue.order = query.trade_no;
    retValue.cporder = query.out_trade_no;
    retValue.info = '';
    if(retValue.code!='0'){
        retf('FAILURE');return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('FAILURE');
            return;
        }  else  if (query.app_order_id == params.orderdata && query.product_id == params.goodsid && query.amount >= params.goodsprice*0.9&&query.amount <= params.goodsprice)
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
                        retf('FAILURE');
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.total_fee>=retOut.amount*0.9
                            &&query.total_fee<=retOut.amount){
                                if(retOut.status=='2'){
                                    retf('FAILURE');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.total_fee);
                                    retf('ok');
                                    return;
                                }else{
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,0);
                                    var data  = {};
                                    data.code = '0000';
                                    data.msg = 'NORMAL';
                                    retf(data);
                                    return;
                                }
                        }else{
                            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,3,0);
                            retf('FAILURE');
                            return;
                        }
                    }else{
                        retf('FAILURE');
                        return;
                    }
                }else{
                    retf('FAILURE');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{

    var retValue = {};
    retValue.code = query.trade_status  == ('0' || '3') ? 0 : 1;
    retValue.id = query.client_id;
    retValue.order = query.trade_no;
    retValue.cporder = query.out_trade_no;
    retValue.info = '';
    if(retValue.code!='0'){
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('FAILURE');return;
    }

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf('FAILURE');
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.total_fee + '';
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
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');return;
                    }
                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.total_fee);
                        retf('ok');
                    }else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');
                    }

                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('FAILURE');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var signStr = getSignStr(query);
    console.log("SignStr: " + signStr);
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
    console.log(publickey);
    var v = crypto.createVerify('sha1WithRSAEncryption');
    //v.update(logicCommon.utf16to8(signStr));
    v.update(signStr);
    var isChecked = v.verify(publickey, query.tt_sign, 'base64');
    return isChecked;
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
        if (key != 'tt_sign' && key != 'tt_sign_type')
            str += key + '=' + arr2[key] + '&';
    }
    var signStr = str.substr(0, str.length - 1);
    return signStr;
}

function checkOrder()
{
   return false;
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
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;