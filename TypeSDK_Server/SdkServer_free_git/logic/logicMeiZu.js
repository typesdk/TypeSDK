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

    ret.session_id = cloned.token;
    ret.uid = cloned.id;
    return true;
}



function callChannelLogin(attrs,params,query,ret,retf)
{

    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.app_id  = attrs.app_id;
    cloned.session_id = query.session_id;
    cloned.uid = query.uid;
    cloned.ts = Date.now();
    cloned.sign_type = "md5";
    var str = 'app_id=' + cloned.app_id + '&' +
        'session_id=' + cloned.session_id + '&' +
        'ts=' + cloned.ts + '&' +
        'uid=' + cloned.uid + ':' + attrs.secret_key;

    cloned.sign = crypto.createHash('md5').update(str).digest('hex');
    console.log(str);
    var options = {
        url: params.out_url,
        method:params.method,
        form: cloned,
        rejectUnauthorized: false,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    };

    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        console.log(error);
        console.log('request body= '+JSON.stringify(body));
        if (!error && response.statusCode == 200) {
            var retOut = JSON.parse(body);
            if(retOut.code == '200'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = query.uid;
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
    retValue.code = (query.trade_status==3)?0:1;
    retValue.id = query.uid;
    retValue.order = query.order_id;
    retValue.cporder = query.cp_order_id;
    retValue.info = query.buy_amount;

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
            var retDate = {};
            retDate.code = '900000';
            request(options, function (error, response, body) {
                if(!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retf(retDate);
                        return;
                    }
                    console.log(retOut);
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.total_price*100>=retOut.amount*0.9
                            &&query.total_price*100<=retOut.amount){
                            if(retOut.status=='2'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,0);
                                retf(retDate);
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.total_price*100);
                                retDate.code = '200';
                                retDate.message = 'Success';
                                retDate.value = 'Success';
                                retDate.redirect = "";
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
                        }
                    }else{
                        retf(retDate);
                        return;
                    }
                }else{
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
    retValue.code = (query.trade_status==3)?0:1;
    retValue.id = query.uid;
    retValue.order = query.order_id;
    retValue.cporder = query.cp_order_id;
    retValue.info = query.buy_amount;
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
            retValue.amount = '' + query.total_price * 100 + '';
            var options = {
                url: params.out_url,
                method: params.method,
                body: retValue,
                json: true
            };
            console.log(options);
            var retDate = {};
            retDate.code = '900000';
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);
            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retDate);
                        return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.total_price*100);
                        retDate.code = '200';
                        retDate.message = 'Success';
                        retDate.value = 'Success';
                        retDate.redirect = "";
                        retf(retDate);
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retDate);
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf(retDate);
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var str ='app_id=' + attrs.app_id + '&' +
        'buy_amount=' + query.buy_amount + '&' +
        'cp_order_id=' + query.cp_order_id + '&' +
        'create_time=' + query.create_time + '&' +
        'notify_id=' + query.notify_id + '&' +
        'notify_time=' + query.notify_time + '&' +
        'order_id=' + query.order_id + '&' +
        'partner_id=' + query.partner_id + '&' +
        'pay_time=' + query.pay_time + '&' +
        'pay_type=' + query.pay_type + '&' +
        'product_id=' + query.product_id + '&' +
        'product_per_price=' + query.product_per_price + '&' +
        'product_unit=' + query.product_unit + '&' +
        'total_price=' + query.total_price + '&' +
        'trade_status=' + query.trade_status + '&' +
        'uid=' + query.uid + '&' +
        'user_info=' + query.user_info + ':' + attrs.secret_key;

    var osign = crypto.createHash('md5').update(str).digest('hex');

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
    if(typeof (query.playerid  || query.cporder || query.price) == 'undefined')
    {
        retErrorData.code = -1;
        retErrorData.msg = 'ERROR';
        retf(retErrorData);
    }
    else
    {
        var dhdkjhf ={
            "playerid": "0",
            "price": "0",
            "cporder": "0",
            "subject": ""
        };
        retData.app_id = attrs.app_id;
        retData.cp_order_id = query.cporder;
        retData.uid = query.playerid;
        retData.product_id = query.product_id ? query.product_id : "0";
        retData.product_subject = query.subject;
        retData.product_body = query.product_body ? query.product_body : "";
        retData.product_unit = query.product_unit ? query.product_unit : "";
        retData.buy_amount = query.buy_amount ? query.buy_amount  : "1";
        retData.product_per_price = query.product_per_price;
        retData.total_price = query.price;
        retData.create_time = Math.floor(Date.now()/1000);
        retData.pay_type = query.pay_type;
        retData.user_info =query.user_info;
        retData.sign_type = "md5";

        var str = 'app_id=' + retData.app_id + '&' +
            'buy_amount=' + retData.buy_amount + '&' +
            'cp_order_id=' + retData.cp_order_id + '&' +
            'create_time=' + retData.create_time + '&' +
            'pay_type=' + retData.pay_type + '&' +
            'product_body=' + retData.product_body  + '&' +
            'product_id=' + retData.product_id + '&' +
            'product_per_price=' + retData.total_price + '&' +
            'product_subject=' + retData.product_subject + '&' +
            'product_unit=' + retData.product_unit + '&' +
            'total_price=' +  retData.total_price + '&' +
            'uid=' + retData.uid + '&' +
            'user_info=' + retData.user_info +  ':' + attrs.secret_key;

        var osign = crypto.createHash('md5').update(str).digest('hex');
        console.log(str);
        var retStr = {};
        retStr.sign = osign;
        retStr.code = 0;
        retStr.msg = 'NORMAL';
        retStr.create_time = retData.create_time;
        console.log(retStr);
        retf(retStr);
    }
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
            logicCommon.saveCHOrderInRedis(game, channel, query.cp_order_id, query.order_id,function(res){
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
exports.CreateChannelOrder = CreateChannelOrder;
exports.compareOrder =compareOrder;
exports.checkChOrder = checkChOrder;