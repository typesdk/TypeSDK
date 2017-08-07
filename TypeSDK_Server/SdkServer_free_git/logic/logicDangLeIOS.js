/**
 * Created by Wans on 2014/12/31.
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
        if(org[i] == cloned[i] && i != "data")
        {
            return false;
        }

        //判断参数中是否有为空的字段
        if(0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data")
        {
            return false;
        }
    }

    ret.umid = cloned.id;
    ret.token = cloned.token;
    return true;
}

function createSignLogin(query,key)
{
    var str = query.appid+'|'+key+'|'+query.token+'|'+query.umid;
    return crypto.createHash('md5').update(str).digest('hex');
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.appid = attrs.app_id;
    cloned.sig = createSignLogin(cloned,attrs.app_key);
    console.log(createSignLogin(cloned,attrs.app_key));

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
            //打点：验证成功
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
            var retOut = JSON.parse(body);
            console.log(retOut);
            ret.code = retOut.valid=='1' ? 0 : 1;
            ret.msg = "NORMAL";
            ret.id = cloned.umid;
            ret.nick ='';
            ret.token = cloned.token;
            ret.value = retOut;
        }else
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
    retValue.code = query.result == '1' ? '0' : '1';
    retValue.id = query.mid;
    retValue.order = query.order;
    retValue.cporder = query.ext;
    retValue.info = query.ext;
    if(retValue.code!='0'){
        retf('failure');
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('failure');
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
                        retf('failure');
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code=='0'){
                        if(retOut.Itemid){
                                logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.ext==retOut.cporder
                            &&query.money * 100>=retOut.amount*0.9
                            &&query.money * 100<=retOut.amount){
                                if(retOut.status=='2'){
                                    retf('failure');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.money * 100);
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
                            retf('failure');
                            return;
                        }
                    }else{
                        retf('failure');
                        return;
                    }
                }else{
                    retf('failure');
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel)
{
    var retValue = {};
    retValue.code = query.result == '1' ? '0' : '1';
    retValue.id = query.mid;
    retValue.order = query.order;
    retValue.cporder = query.ext;
    retValue.info = query.ext;
    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('failure');
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf('failure');
        }else{
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.amount = '' + query.money * 100 + '';
            var options = {
                url: params.out_url,
                method: 'POST',
                body: retValue,
                json: true
            };
            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    //日志记录CP端返回
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('failure');
                    }
                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.money * 100);
                        retf('success');
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('failure');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('failure');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var osign = crypto.createHash('md5').update(
        'order=' + query.order + '&' +
        'money=' + query.money + '&' +
        'mid=' + query.mid + '&' +
        'time=' + query.time + '&' +
        'result=' + query.result + '&' +
        'ext=' + query.ext + '&' +
        'key=' + attrs.secret_key
    ).digest('hex').toLowerCase();

    console.log(query.signature + " :: " + osign);

    if (query.signature != osign)
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
    logicCommon.selCHOrderInRedis(channel,query.order,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.ext, query.order,function(res){
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