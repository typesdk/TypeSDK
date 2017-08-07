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

    ret.token = cloned.token.split("|",2)[0];
    ret.deviceid = cloned.token.split("|",2)[1];
    ret.suid = cloned.id;


    return true;
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.suid  = query.suid;
    cloned.appkey  = attrs.app_key;
    cloned.deviceid = query.deviceid;
    cloned.token = query.token;

    var str = "appkey=" + cloned.appkey + "&" +
             "deviceid=" + cloned.deviceid + "&" +
             "suid=" + cloned.suid + "&" +
             "token=" + cloned.token + "|" + attrs.product_key;

    cloned.signature = crypto.createHash('md5').update(str).digest('hex');
    var options = {
        url: params.out_url,
        method:params.method,
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
            if(typeof  retOut.usertype != 'undefined'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = query.suid;
                ret.nick = "";
                ret.token = "";
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

function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code =  0;
    retValue.id = query.order_uid;
    retValue.order = query.order_id;
    retValue.cporder = query.pt;
    retValue.info = "";

    var retDate = {};
    retDate.status = 'failed';
    retDate.desc = '';

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('FAILURE');
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
            console.log(options);

            request(options, function (error, response, body) {
                console.log("CB-Body: " + JSON.stringify(body));

                if(!error && response.statusCode == 200){
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        retf('FAILURE');
                        return;
                    }
                    if(retOut.code=='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        retOut.status = 0;

                        if(query.order_uid==retOut.id
                            &&query.pt==retOut.cporder
                            &&query.amount>=retOut.amount*0.9
                            &&query.amount<=retOut.amount){
                            if(retOut.status=='2'){
                                retf('FAILURE');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);
                                retDate.status = 'success';
                                retDate.desc = '通知成功';
                                retf(retDate);
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
    retValue.code =  0;
    retValue.id = query.order_uid;
    retValue.order = query.order_id;
    retValue.cporder = query.pt;
    retValue.info = "";
    console.log("retValue:"+retValue);
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
            retValue.amount = '' + query.amount + '';
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
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');
                        return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);
                        retf( 'OK');
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
    var str =  'actual_amount|' + query.actual_amount + '|' +
        'amount|' + query.amount + '|' +
        'order_id|' + query.order_id + '|' +
        'order_uid|' + query.order_uid + '|' +
        'pt|' + query.pt + '|' +
        'source|' + query.source + '|' + attrs.secret_key;

    var osign = crypto.createHash('sha1').update(str).digest('hex');

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
    logicCommon.selCHOrderInRedis(channel,query.order_id,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.pt, query.order_id,function(res){
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
exports.compareOrder = compareOrder;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;
