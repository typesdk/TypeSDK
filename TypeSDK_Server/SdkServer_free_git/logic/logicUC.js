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

    ret.id = Date.now();
    ret.data = {};
    ret.data.sid = cloned.token;

    return true;
}

function createSignLogin(query,key)
{
    return crypto.createHash('md5').update('sid=' + query.data.sid + key).digest('hex').toLowerCase();
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);

    cloned.service = "channelLogin";
    cloned.game = {  "gameId" : attrs.app_id};
    cloned.sign = createSignLogin(cloned,attrs.app_key);

    var options = {
        url: params.out_url,
        method:params.method,
        body: cloned,
        json: true
    };

    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;
            if ( retOut.state.code == 1)
            {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code =  0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.accountId;
                ret.nick = retOut.data.nickName;
                ret.token = "";
                ret.value = retOut;
            }else{
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code =  1;
                ret.msg = "LOGIN ERROR";
                ret.id = "";
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
            }

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
    retValue.code = query.data.orderStatus == 'S' ? 0 : 1;
    retValue.id = query.data.accountId;
    retValue.order = query.data.orderId;
    retValue.cporder = query.data.cpOrderId || '';
    retValue.info = query.data.callbackInfo;

    if(retValue.code!='0'){

        retf('SUCCESS');
        return;
    }
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

                if(!error && response.statusCode == 200){
                    var retOut = body;
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        retf('SUCCESS555');
                        return;
                    }
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        console.log("query.data.amount:" + query.data.amount);
                        console.log("retOut.amount:" + retOut.amount);
                        if(query.data.amount*100<=retOut.amount
                            &&query.data.amount*100>=retOut.amount*0.9){
                                console.log('1');
                                if(retOut.status=='2'){
                                    retf('SUCCESS');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.data.amount*100);
                                    retf('SUCCESS');
                                    return;
                                }else{
                                    console.log('3');
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,0);
                                    var data  = {};
                                    data.code = '0000';
                                    data.msg = 'NORMAL';
                                    retf(data);
                                    return;
                                }
                        }else{
                            console.log('2');
                            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,3,0);
                            retf('SUCCESS222');
                            return;
                        }
                    }else{
                        retf('SUCCESS333');
                        return;
                    }
                }else{
                    retf('SUCCESS444');
                    return;
                }
            });
        }
    });

}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code = query.data.orderStatus == 'S' ? 0 : 1;
    retValue.id = query.data.accountId;
    retValue.order = query.data.orderId;
    retValue.cporder = query.data.cpOrderId || '';
    retValue.info = query.data.callbackInfo;

    if(retValue.code!='0'){
        retf('SUCCESS');
        return;
    }
    //保存外部订单号
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf('FAILURE');
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.amount = '' + query.data.amount * 100 + '';


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
                        retf('SUCCESS');
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.data.amount*100);
                        retf('SUCCESS1111');
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('SUCCESS');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('SUCCESS');
                }
            });
        }
    });
}

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.data.orderId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.data.cpOrderId || '', query.data.orderId,function(res){
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

function checkSignPay(attrs,query)
{
    var strCpOrderId = '';
    if (typeof query.data.cpOrderId != 'undefined' && query.data.cpOrderId != 0)
    {
        strCpOrderId = 'cpOrderId=' + query.data.cpOrderId;
    }
    var osign = crypto.createHash('md5').update(
        'accountId=' + query.data.accountId +
        'amount=' + query.data.amount +
        'callbackInfo=' + query.data.callbackInfo +
        strCpOrderId +
        'creator=' + query.data.creator +
        'failedDesc=' + query.data.failedDesc +
        'gameId=' + query.data.gameId +
        'orderId=' + query.data.orderId +
        'orderStatus=' + query.data.orderStatus +
        'payWay=' + query.data.payWay +
        attrs.app_key
    ).digest('hex');

    console.log(query.sign + " :: " + osign);

    if (query.sign != osign)
    {
        return false;
    }

    return true;
}

function checkOrder()
{
    return false;
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;
exports.compareOrder = compareOrder;