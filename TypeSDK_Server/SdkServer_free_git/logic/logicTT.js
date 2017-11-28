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
    ret.sid = cloned.token;
    ret.uid = cloned.id;
    return true;
}

function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    var data = {
        gameId:attrs.app_id,
        uid:cloned.uid
    };

    var sign =crypto.createHash('md5').update(JSON.stringify(data)+attrs.app_key,'utf8').digest('base64');

    var options = {
        url: params.out_url+'/rest/user/loginstatus.view',//测试环境out_url_test 正式环境 out_url
        method:params.method,
        body: data,
        json:true,
        headers: {
            'Content-Type': 'application/json',
            'sign':sign,
            'sid':cloned.sid
        }
    };

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    console.log(options);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            var retOut = body.head;
            console.log(retOut);
            if(retOut.result == '0'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = cloned.uid;
                ret.nick = "";
                ret.token = "";
                ret.value = retOut;
                logicCommon.createLoginLog(gattrs.id,attrs.channel_id,attrs.sdk_name,ret.id);
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
    retValue.code =  query.payResult==1?0:1;
    retValue.id = query.uid;
    retValue.order = query.sdkOrderId;
    retValue.cporder = query.cpOrderId;
    retValue.info = query.exInfo;
    var retDate = {
        head:{
            result:'-99',
            message:''
        }
    };
    if(retValue.code!=0){
        retDate.head.message = 'query.payResult!=1';
        retf(retDate);
        return ;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retDate.head.message = 'MERVER CONNECTION EXCEPTION';
            retf(retDate);
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

            request(options, function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retDate.head.message = 'MERVER CONNECTION EXCEPTION';
                        retf(retDate);
                        return;
                    }
                    if (retOut.code == '0') {
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if (query.uid == retOut.id
                            && query.payFee*100 >= retOut.amount * 0.9
                            && query.payFee*100 <= retOut.amount) {
                            if (retOut.status == '2') {
                                retDate.head.message = '订单已取消';
                                retf(retDate);
                                return;
                            } else if (retOut.status == '4' || retOut.status == '3') {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.payFee*100);
                                retDate.head.code = 0;
                                retDate.head.message = 'NORMAL';
                                retf(retDate);
                                return;
                            } else {
                                logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2,0);
                                var data = {};
                                data.code = '0000';
                                data.msg = 'NORMAL';
                                retf(data);
                                return;
                            }
                        } else {
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,0);
                            retDate.head.message = '订单数据有误';
                            retf(retDate);
                            return;
                        }
                    } else {
                        retDate.head.message = 'MERVER CONNECTION EXCEPTION';
                        retf(retDate);
                        return;
                    }
                } else {
                    retDate.head.message = 'MERVER CONNECTION EXCEPTION';
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
    retValue.code =  query.payResult==1?0:1;
    retValue.id = query.uid;
    retValue.order = query.sdkOrderId;
    retValue.cporder = query.cpOrderId;
    retValue.info = query.exInfo;
    var retDate = {
        head:{
            result:'-99',
            message:''
        }
    };
    if(retValue.code!=0){
        retDate.head.message = 'query.payResult!=1';
        retf(retDate);
        return ;
    }

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retDate.head.message = 'MERVER CONNECTION EXCEPTION';
            retf(retDate);
            return;
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.payFee*100 + '';
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
                        retDate.head.message = 'MERVER CONNECTION EXCEPTION';
                        retf(retDate);
                        return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.payFee*100);
                        retDate.head.result = '0';
                        retDate.head.message = 'NORMAL';
                        retf(retDate);
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                        retDate.head.message = 'MERVER CONNECTION EXCEPTION';
                        retf(retDate);
                        return;
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                    retDate.head.message = 'MERVER CONNECTION EXCEPTION';
                    retf(retDate);
                    return;
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var sign = query.sign;
    delete  query.sign;
    var  str =JSON.stringify(query);
    var osign = crypto.createHash('md5').update(query.str+attrs.secret_key).digest('base64');
    console.log(sign + " :: " + osign);
    console.log(query.str+attrs.secret_key);

    if (sign != osign)
    {
        return false;
    }
    return true;
}
function checkOrder()
{
    return false;
}

function parseBody(req, retf){

    var data = {code:1, data: {}};
    data.data= {};
    if(req.body&&req.params.action=='Pay'){
        var paramobj ;
        for(var i in req.body){
            paramobj = i;
        }
        if(paramobj){
            data.data = JSON.parse(paramobj);
            data.data.payFee =data.data.payFee.toFixed(2);
            data.data.str =paramobj;
        }
        if(req.headers){
            data.data.sign = req.headers.sign;
        }else{
            data.data.sign = '';
        }
    }else{
        data.code = 0;
    }
    retf(data);
}
exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.compareOrder= compareOrder;
exports.parseBody =parseBody;