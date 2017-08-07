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
        if(0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data"  && i != "id")
        {
            return false;
        }
    }
    ret.cpServiceId = cloned.token.split("|",2)[0];
    ret.Ua = cloned.token.split("|",2)[1];
    ret.userId = cloned.id;


    return true;
}
function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);

    cloned.cpServiceId = query.kay;
    cloned.Ua = query.Ua;
    cloned.userId = query.userId;
    cloned.p = query.p;
    cloned.region = query.region;
    cloned.key = attrs.app_key;
    cloned.cpId = attrs.sdk_cp_id;
    cloned.channelId = attrs.app_id;

    var options = {
        url: params.out_url,
        method:params.method,
        qs: cloned
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;
            if ( retOut== "0")
            {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code =  0;
                ret.msg = "NORMAL";
                ret.id = cloned.userId;
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }else {
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code =  1;
                ret.msg = "LOGIN ERROR";
                ret.id = "";
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }
        }else {
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
    retValue.code = query.hRet == 0 ? 0 : 1;
    retValue.id = query.userId;
    retValue.order = '';
    retValue.cporder = query.cpparam;
    retValue.info = '';

    var  retData = {
        'hRet':-1,
        'message':'未知错误'
    };
    if(retValue.code!=0){
        retData.hRet =1;
        retData.message = '参数hRet不为0';
        retf(retData);
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf(retData);
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
                console.log('cp verifycallback error'+JSON.stringify(error));
                console.log('cp verifycallback response.statusCode'+response.statusCode);
                console.log('cp verifycallback body'+JSON.stringify(body));

                if(!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retf(retData);
                        return;
                    }
                    if(retOut.code =='0'){
                        var consumeCode = query.consumeCode;
                        // 具体格式还要看计费代码的表示怎么对应的
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.consumeCode>=retOut.amount*0.9
                            &&query.consumeCode<=retOut.amount){
                            if(retOut.status=='2'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2);
                                retData.hRet =-3;
                                retData.message = '订单已取消';
                                retf(retData);
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.consumeCode);
                                retData.hRet =0;
                                retData.message = 'NORMAL';
                                retf(retData);
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
                            retData.hRet = -2;
                            retData.message = '金额验证错误';
                            retf(retData);
                            return;
                        }
                    }else{
                        retf(retData);
                        return;
                    }
                }else{
                    retf(retData);
                    return;
                }
            });
        }
    });
}


function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code = query.hRet == 0 ? 0 : 1;
    retValue.id = query.userId;
    retValue.order = '';
    retValue.cporder = query.cpparam;
    retValue.info = '';

    var  retData = {
        'hRet':-1,
        'message':'未知错误'
    };
    if(retValue.code!=0){
        retData.hRet =1;
        retData.message = '参数hRet不为0';
        retf(retData);
        return;
    }

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf(retData);
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);

            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.consumeCode+ '';

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
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retData);
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.consumeCode);
                        retData.hRet =0;
                        retData.message = 'NORMAL';
                        retf(retData);
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retData);
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf(retData);
                }
            });
        }
    });
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
    retf(true);
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;