/**
 * Created by TYPESDK on 2017/1/19.
 */

var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var urlencode = require('urlencode');

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

    ret.mem_id = cloned.id;
    ret.user_token = cloned.token;

    return true;
}


function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);

    cloned.app_id = attrs.app_id;
    cloned.mem_id = query.mem_id;
    cloned.user_token = query.user_token;
    cloned.sign = crypto.createHash('md5').update('app_id=' + attrs.app_id + '&' +  'mem_id=' + query.mem_id + '&' +  'user_token=' + query.user_token + '&' +  'app_key=' + attrs.app_key).digest('hex').toLowerCase();;

    var options = {
        url: params.out_url,
        method:params.method,
        form: cloned,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    };

    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            if ( retOut.status == 1)
            {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code =  0;
                ret.msg = "NORMAL";
                ret.id = query.mem_id;
                ret.token = query.user_token;
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
            }else{
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code =  1;
                ret.msg = "LOGIN ERROR";
                ret.id = "";
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
    retValue.code = query.data.orderStatus == '3' ? 0 : 1;
    retValue.id = query.mem_id;
    retValue.order = query.order_id;
    retValue.cporder = query.data.cp_order_id || '';
    retValue.info = '';

    if(retValue.code!='0'){

        retf('SUCCESS');
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('FAILURE');
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
                        retf('FAILURE');
                        return;
                    }
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }

                        if(query.product_price*100<=retOut.amount
                            &&query.product_price*100>=retOut.amount*0.9){
                            console.log('1');
                            if(retOut.status=='2'){
                                retf('FAILURE');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,parseInt(query.data.amount*100));
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
                            retf('SUCCESS');
                            return;
                        }
                    }else{
                        retf('SUCCESS');
                        return;
                    }
                }else{
                    retf('SUCCESS');
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
    retValue.id = query.mem_id;
    retValue.order = query.order_id;
    retValue.cporder = query.cp_order_id || '';
    retValue.info = '';
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
            retValue.amount = '' + query.product_price * 100 + '';

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
                        retf('FAILURE');
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,parseInt(query.data.amount*100));
                        retf('SUCCESS');
                    }
                    else {
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
    var osign = crypto.createHash('md5').update(
        'app_id=' + attrs.app_id + '&' +
        'cp_order_id=' + query.cp_order_id + '&' +
        'mem_id=' + query.mem_id + '&' +
        'order_id=' + query.order_id + '&' +
        'order_status=' + query.order_status + '&' +
        'pay_time=' + query.pay_time + '&' +
        'product_id=' + query.product_id + '&' +
        'product_name=' + urlencode(query.product_name) + '&' +
        'product_price=' + urlencode(query.product_price) + '&' +
        'app_key=' + attrs.app_key
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

function CreateChannelOrder(attrs,params,query,ret,retf)
{
    var retErrorData = {};
    var retData = {};
    if(!query.playerid  && !query.cporder && !query.price)
    {
        retErrorData.code = -1;
        retErrorData.msg = 'ERROR';
        retf(retErrorData);
    }
    else if(query.playerid == "253" && query.cporder && query.price)
    {
        var subjectDECODE =  urldecode(query.subject);
        console.log(subjectDECODE);
        var osign = crypto.createHash('md5').update(subjectDECODE + attrs.app_key).digest('hex');
        var retStr = {};
        retStr.code = 0;
        retStr.msg = 'NORMAL';
        retStr.playerid = 0;
        retStr.order = 0;
        retStr.cporder = 0;
        retStr.data = osign;
        retf(JSON.stringify(retStr));
    }
    else
    {
        retErrorData.code = -2;
        retErrorData.msg = 'ERROR';
        retf(retErrorData);
    }
}


exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;
exports.compareOrder = compareOrder;
exports.CreateChannelOrder = CreateChannelOrder;