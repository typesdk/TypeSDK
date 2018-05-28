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
    ret.uid = cloned.id;
    ret.token = cloned.token;

    return true;
}
function createSign(attrs,query){
   var obj= logicCommon.sortObject(query);
   var str = attrs.app_key+attrs.secret_key;
    for(var i in obj){
        str+=i+'='+obj[i]+'&'
    }
    str= str.substring(0,str.lastIndexOf('&'));
    console.log(str);
    var osign = crypto.createHash('md5').update(str).digest('hex');
    return osign
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.uid = query.uid;
    cloned.app_key = attrs.app_key;
    cloned.token = query.token;
    cloned.sign = createSign(attrs,cloned);
    var options = {
        url: params.out_url,
        method:params.method,
        formData: cloned,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            var data = retOut.data;
            if(data.is_success){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = query.uid;
                ret.nick = "";
                ret.token = query.token;
                ret.value = retOut;
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

function compareOrder(attrs,gattrs,params,query,ret,game,channel,retfa){
    var retValue = {};
    retValue.code = 0;
    var userId = query.app_user_id;
    retValue.id = userId.replace(/_/,':');
    retValue.order = query.pa_open_order_id;
    retValue.cporder =  query.app_order_id;
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retfa('fail');
            return;
        } else {
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
                    if(retOut.code=='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(retValue.cporder==retOut.cporder
                            &&query.money_amount * 100>=retOut.amount*0.9
                            &&query.money_amount * 100<=retOut.amount){
                                if(retOut.status=='2'){
                                    retfa('fail');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.money_amount * 100);
                                    retfa('success');
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
                            retfa('fail');
                            return;
                        }
                    }else{
                        retfa('fail');
                        return;
                    }
                }else{
                    retfa('fail');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code = 0;
    var userId = query.app_user_id;
    retValue.id = userId.replace(/_/,':');
    retValue.order = query.pa_open_order_id;
    retValue.cporder =  query.app_order_id;
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData){
        if(!hasData)
        {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf('FAILURE');
        }else{
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.money_amount * 100 + '';
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
                        retf('fail');
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.money_amount * 100);
                        retf("ok");
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('fail');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('fail');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var sign = query.sign;
    delete query.sign;
    var strArray = ['app_key','app_order_id','app_district','app_server'
        ,'app_user_id','app_user_name','product_id','product_name'
        ,'money_amount','pa_open_uid','app_extra1','app_extra2','pa_open_order_id'];
    strArray = strArray.sort();
    var str = attrs.app_key+attrs.secret_key;
    for(var i=0;i<strArray.length;i++){
        str+=strArray[i]+'='+query[strArray[i]]+'&'
    }
    str= str.substring(0,str.lastIndexOf('&'));
    console.log(str);
    //var osign = crypto.createHash('md5').update(logicCommon.utf16to8(str)).digest('hex');
    var osign = crypto.createHash('md5').update(str).digest('hex');
    console.log(sign + " :: " + osign);

    if (sign!= osign)
    {
        return false;
    }
    return true;
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
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.pa_open_order_id,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.app_order_id, query.pa_open_order_id,function(res){
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

exports.compareOrder =compareOrder;
exports.checkChOrder = checkChOrder;