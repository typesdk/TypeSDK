/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var randomArray = ['18257284-7F5D-348D-AB09-299E5B7DD997','655A957D-157D-7C21-E3A7-9CAAFA835318','F467CA93-D550-346D-6BCB-173995F7C83A','BD32817A-99F9-2E26-5B33-15208F7B360A'];

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
    var arr = cloned.id.split('|');
    ret.openid = arr[0];
    ret.imei = arr[1];
    ret.devicetype =arr[2];
    ret.channelkey='kaopu';
    ret.r =arr[3];
    ret.url = arr[4].substring(0,(arr[4].indexOf('?')));
    ret.token = cloned.token;


    return true;
}

function createSignLogin(query)
{
    var cloned = merge(true, data);
    merge(cloned,query);
    var r = query.r;

    var key = Object.keys(cloned);

    console.log(key);
    var data = key.sort();
    console.log(data);
    var str = '';
    for(var i=0;i<data.length;i++){
        str+=cloned[data[i]];
    }
    str = str+randomArray[r];
    console.log(str);
    var sign =crypto.createHash('md5').update(str).digest('hex');

    return sign;
}


function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var openid = query.openid;
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.tagid = attrs.secret_key;
    cloned.tag = attrs.app_key;
    cloned.appid = attrs.app_id;
    delete cloned.url;
    cloned.sign = createSignLogin(cloned);
    var options = {
        url:query.url,
        method:params.method,
        qs: cloned
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            console.log(retOut);
            if(retOut.code=='1'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = query.openid;
                ret.nick = "";
                ret.token = query.token;
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
            }else{
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
            console.log(error);
            console.log(response.statusCode);
            ret.code = 2;
            ret.msg = "OUT URL ERROR";
            ret.value = "";
        }
        retf(ret);

    });

}
function createSign(obj,attrs){
    return crypto.createHash('md5').update(obj.code+'|'+attrs.secret_key).digest('hex');
}
function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    var arr = query.ywordernum.split('|');
    retValue.code = query.status  == '1' ? 0 : 1;
    retValue.id = arr[1];
    retValue.order = query.kpordernum;
    retValue.cporder = arr[0];
    retValue.info = '';
    var retDate = {};
    retDate.code = '00000';
    retDate.msg = 'System Exceptions';
    if(retValue.code!='0'){
        retDate.sign = createSign(retDate,attrs);
        retf(retDate);
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf(retDate);
            return;
        } else  if (query.app_order_id == params.orderdata && query.product_id == params.goodsid && query.amount >= params.goodsprice*0.9&&query.amount <= params.goodsprice)
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
                        retDate.sign = createSign(retDate,attrs);
                        retf(retDate);
                        return;
                    }
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(retValue.cporder==retOut.cporder
                            &&query.amount>=retOut.amount*0.9
                            &&query.amount<=retOut.amount){
                                if(retOut.status=='2'){

                                    retf(retDate);
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);

                                    retDate.code = 1000;
                                    retDate.msg='success';
                                    retDate.sign = createSign(retDate,attrs);
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
                            retDate.sign = createSign(retDate,attrs);
                            retf(retDate);
                            return;
                        }
                    }else{
                        retDate.sign = createSign(retDate,attrs);
                        retDate.msg = 'Param Exceptions';
                        retf(retDate);
                        return;
                    }
                }else{
                    retDate.sign = createSign(retDate,attrs);
                    retf(retDate);
                    return;
                }
            });
        }
    });
}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    var arr = query.ywordernum.split('|');
    retValue.code = query.status  == '1' ? 0 : 1;
    retValue.id = arr[1];
    retValue.order = query.kpordernum;
    retValue.cporder = arr[0];
    retValue.info = '';
    var retDate = {};
    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retDate.code = '0000';
        retDate.msg = 'System Exceptions';
        retf(retDate);
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retDate.sign = createSign(retDate,attrs);
            retf(retDate);
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
                        retDate.code= '1005';
                        retDate.msg = 'System Exceptions';

                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount);
                        retDate.code = 1000;
                        retDate.msg='success';
                    }else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retDate.code = 1004;
                        retDate.msg = 'Param Exceptions';
                    }
                }else{
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retDate.code = '1005';
                    retDate.msg = 'System Exceptions';
                }
                retDate.sign = crypto.createHash('md5').update(retDate.code+attrs.secret_key).digest('hex');
                console.log(retDate);
                retDate.sign = createSign(retDate,attrs);
                retf(retDate);
            });
        }
    });
}
function checkSignPay(attrs,query)
{
    var str=query.username+'|'+query.kpordernum+'|'+query.ywordernum+'|'+query.status
        +'|'+query.paytype+'|'+query.amount+'|'+query.gameserver+'|'+query.errdesc
        +'|'+query.paytime+'|'+query.gamename+'|'+attrs.secret_key;
    //var osign = crypto.createHash('md5').update(logicCommon.utf16to8(str)).digest('hex');
    var osign = crypto.createHash('md5').update(str).digest('hex');
    console.log(str);

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
    var arr = query.ywordernum.split('|');

    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.kpordernum,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, arr[0], query.kpordernum,function(res){
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