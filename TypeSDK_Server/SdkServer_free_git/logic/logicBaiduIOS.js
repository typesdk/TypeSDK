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
        if(org[i] == cloned[i]  && i != "data")
        {
            return false;
        }

        //判断参数中是否有为空的字段
        if(0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length  && i != "data")
        {
            return false;
        }
    }

    ret.Uin =  cloned.id;
    ret.SessionId = cloned.token;
    return true;
}

function createSignLogin(query,key)
{
    return crypto.createHash('md5').update( query.AppID + query.Act + query.Uin + query.SessionId + key).digest('hex').toLowerCase();
}

function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);

    cloned.AppID = attrs.app_id;
    cloned.Act = '4';
    cloned.Sign = createSignLogin(cloned,attrs.private_key);
    var options = {
        url: params.out_url,
        method:params.method,
        qs: cloned,
        json: true
    };
    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body;
            if(retOut.ErrorCode == 0)
            {
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = query.Uin;
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
            }else{
                //打点：验证失败
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
                ret.code = 1;
                ret.msg = "OUT ERROR";
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

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code =  0;
    retValue.id = Content.Uin;
    retValue.order = query.ConsumeStreamId ;
    retValue.cporder = query.CooOrderSerial;
    retValue.OrderType = 1;
    retValue.info = Content.Note;

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

            var retBaidu = {};
            retBaidu.ErrorCode = "0";
            retBaidu.ErrorDesc = "接收失败";
            retf(retBaidu);
        }else{
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.appid = attrs.app_id;
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.amount * 100 + '';

            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,0,query);

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
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                        var retBaidu = {};
                        retBaidu.ErrorCode = "0";
                        retBaidu.ErrorDesc = "接收失败";
                        retf(retBaidu);
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount * 100,query);
                        var retBaidu = {};
                        retBaidu.ErrorCode = "1";
                        retBaidu.ErrorDesc = "接收成功";
                        retf(retBaidu);
                    }
                    else
                    {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                        var retBaidu = {};
                        retBaidu.ErrorCode = "0";
                        retBaidu.ErrorDesc = "接收失败";
                        retf(retBaidu);
                    }

                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                    var retBaidu = {};
                    retBaidu.ErrorCode = "0";
                    retBaidu.ErrorDesc = "接收失败";
                    retf(retBaidu);
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var Buffer = require('buffer').Buffer;
    var buf = new Buffer(1024);
    var len = buf.write(query.AppId +
    query.Act  +
    query.ProductName +
    query.ConsumeStreamId +
    query.CooOrderSerial +
    query.Uin +
    query.GoodsId +
    query.GoodsInfo +
    query.GoodsCount +
    query.OriginalMoney +
    query.OrderMoney +
    query.Note +
    query.PayStatus +
    query.CreateTime +
    attrs.app_key,0);
    var result = buf.toString('binary',0,len);

    var osign = crypto.createHash('md5').update(result).digest('hex');

    console.log(query.Sign + " :: " + osign);

    if (query.Sign != osign)
    {
        return false;
    }

    return true;
}

function checkOrder(attrs,params,query,ret,retf)
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
    logicCommon.selCHOrderInRedis(channel,query.ConsumeStreamId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.CooOrderSerial, query.ConsumeStreamId,function(res){
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
exports.checkChOrder = checkChOrder;