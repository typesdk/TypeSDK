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
    ret.token = cloned.token;
    return true;
}



function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.token = query.token;

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
            var retOut = JSON.parse(body);
            if(retOut.resultCode == '1'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.uid;
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
    retValue.code = 0;
    retValue.id = query.order_id.split("|",2)[1];
    retValue.order = query.order_id.split("|",2)[0]|| "";
    retValue.cporder = query.order_id.split("|",2)[0]|| "";
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('fail');
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
                            &&query.cost * 10>=retOut.amount*0.9
                            &&query.cost * 10<=retOut.amount){
                            if(retOut.status=='2'){
                                retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.cost * 10);
                                retf('<response><ErrorCode>1</ErrorCode><ErrorDesc>Success</ErrorDesc></response>');
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
                            retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                            return;
                        }
                    }else{
                        retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                        return;
                    }
                }else{
                    retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
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
    retValue.id = query.order_id.split("|",2)[1];
    retValue.order = query.order_id.split("|",2)[0];
    retValue.cporder = query.order_id.split("|",2)[0];
    retValue.info = "";

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
            retValue.amount = '' + query.amount * 10 + '';
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
                    if (typeof retOut.code  == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                    }

                    if (retOut.code == '0')
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,4,query.cost * 10);
                        retf('<response><ErrorCode>1</ErrorCode><ErrorDesc>Success</ErrorDesc></response>');
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('<response><ErrorCode>0</ErrorCode><ErrorDesc>Error</ErrorDesc></response>');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{

    var Str = attrs.secret_key +  query.qs.time;

    var osign = crypto.createHash('md5').update(Str).digest('hex');

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
    retf(true);
}

/**
 * 解析并格式化渠道请求
 * */
function parseBody(bufferStr, retf){
    var xmlreader = require('xmlreader');
    var data = {code:1, data: {}};

    xmlreader.read(bufferStr, function(err, resp){
        if(err !== null){
            //打点： 其他错误
            logicCommon.sdkMonitorDot(logicCommon.dotType.OtherDot.Error);
            retf(data);
            return;
        }

        if(resp.response){
            channelParam = formatMessage(resp.response);
            data.code = 0;
            data.data = channelParam;
            retf(data);
        }else{
            retf(data);
            return;
        }
    });
}

function formatMessage(result) {
    var message = {};
    for (var key in result) {
        if (typeof(result[key]) == 'object') {
            var val = result[key].text();
            message[key] = (val ? val : '').trim();
        }
    }
    return message;
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;
exports.compareOrder = compareOrder;
//exports.parseBody = parseBody;