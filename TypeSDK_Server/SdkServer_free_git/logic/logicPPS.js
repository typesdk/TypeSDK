/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');

function convertParamLogin(query,ret)
{

    ret.id = query.id;
    ret.data = query.token.split("|")[1];
    ret.token = query.token.split("|")[2];

    return true;
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var Strsign = query.id + '&' + query.data + '&' + attrs.app_key;
    var osign = crypto.createHash('md5').update(Strsign).digest('hex');
    if (osign != query.token)
    {
        ret.code =  1;
        ret.msg = "LOGIN User ERROR";
        ret.id = "";
        ret.nick = "";
        ret.token = "";
    }
    else
    {
        //打点：验证成功
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
        ret.code = 0;
        ret.msg = "NORMAL";
        ret.id = query.id;
        ret.nick = "";
        ret.token = query.token;
        ret.value = "";
        retf(ret);
    }
}


function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code = 0;
    retValue.id = query.user_id;
    retValue.order = query.order_id;
    retValue.cporder = query.userData;
    retValue.info = '';
    if(retValue.code!='0'){
        retf('FAILURE');return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('{FAILURE}');
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
                        retf('{FAILURE}');
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.amount*100>=retOut.amount*0.9
                            &&query.amount*100<=retOut.amount){
                            if(retOut.status=='2'){
                                retf('FAILURE');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount*100);
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
    retValue.id = query.user_id;
    retValue.order = query.order_id;
    retValue.cporder = query.userData;
    retValue.info = query.amount*100;

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {

            retf('FAILURE');   //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);

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
            console.log(options);
            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    //打点：服务器正确处理支付成功回调
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                    var retOut = body;
                    var data = {};
                    data.result = "0";
                    data.message = "success";
                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount * 100);
                    retf(data);
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    var data = {};
                    data.result = "-6";
                    data.message = "Other errors";
                    retf(data);
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var osign = crypto.createHash('md5').update(
        query.user_id +
        query.role_id +
        query.order_id +
        query.money +
        query.time +
        attrs.secret_key
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
            logicCommon.saveCHOrderInRedis(game, channel, query.userData, query.order_id,function(res){
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
exports.compareOrder = compareOrder;
exports.checkOrder = checkOrder;
exports.checkChOrder = checkChOrder;