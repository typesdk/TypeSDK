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

    var token = decodeURIComponent(cloned.token);
    ret.userid = cloned.id;
    ret.sign = token.split("|",2)[0];
    ret.t = token.split("|",2)[1];

    return true;
}



function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    if(query.sign.indexOf(" ")>=0)
    {
        var Usign = query.sign.replace(/\s/g,"+");
    }
    else
    {
        var Usign =  query.sign;
    }
    var osign = crypto.createHash('md5').update(query.userid + "&" + query.t + "&" + attrs.app_key).digest('base64');

    if (Usign != osign)
    {
        //打点：验证失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
        ret.code = 2;
        ret.msg = "SIGN ERROR";
        ret.value = {};
        retf(ret);
    }
    else
    {
        //打点：验证成功
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
        ret.code = 0;
        ret.msg = "NORMAL";
        ret.id = query.userid;
        ret.nick = "";
        ret.token = Usign;
        ret.value = {};
        retf(ret);
        logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);

    }

}


function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code = query.orderStatus  == 'SUCCESS' ? 0 : 1;
    retValue.id = query.openId;
    retValue.order = query.orderId;
    retValue.cporder = query.query.callBackInfo || '';
    retValue.info = '';
    if(retValue.code!='0'){
        retf('FAILURE');return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('{FAILURE}');
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
                        retf('{FAILURE}');
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.amount>=retOut.amount*0.9
                            &&query.amount<=retOut.amount){
                            if(retOut.status=='2'){
                                retf('FAILURE');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);
                                retf('ok');
                                return;
                            }else{
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,0);
                                retf("success");
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
    retValue.code = 0;
    retValue.id = query.openId;
    retValue.order = query.orderId;
    retValue.cporder =  query.callBackInfo;
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
            retValue.amount = '' + query.amount + '';

            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,0,query);

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
                        retf('FAILURE1');
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);
                        retf("success");
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE2');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('FAILURE3');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{

    var str = "amount=" + query.amount + "&" +
        "callBackInfo=" + query.callBackInfo + "&" +
        "currency=" + query.currency + "&" +
        "openId=" + query.openId + "&" +
        "orderId=" + query.orderId + "&" +
        "orderStatus=" + query.orderStatus + "&" +
        "payId=" + query.payId + "&" +
        "payName=" + query.payName + "&" +
        "paySUTime=" + query.paySUTime + "&" +
        "payTime=" + query.payTime + "&" +
        "payType=" + query.payType + "&" +
        "roleId=" + query.roleId + "&" +
        "serverId=" + query.serverId + "&" +
        "serverName=" + query.serverName + "&" +
        "key=" + attrs.app_key;
    var osign = crypto.createHash('md5').update(str).digest('hex').toLowerCase();


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

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.orderId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.callBackInfo, query.orderId,function(res){
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