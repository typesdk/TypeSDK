/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');

function convertParamLogin(query,ret)
{
    var org = {
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
    ret.username = cloned.id.split('|')[0];
    ret.logintime = cloned.id.split('|')[1];
    ret.sign = cloned.token;

    return true;
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    var str = 'username='+cloned.username+'&appkey='+attrs.app_key+'&logintime='+cloned.logintime;
    console.log(str);
    var sign = crypto.createHash('md5').update(str).digest('hex');
    console.log('login sign=='+sign);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    if(sign==cloned.sign){
        //打点：验证成功
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
        ret.code = 0;
        ret.msg = "NORMAL";
        ret.id = query.username;
        ret.nick = query.username;
        ret.token ='';
        ret.value = '';
    }else{
         //打点：验证失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
        ret.code = -1;
        ret.msg = "LOGIN SIGN ERROR";
        ret.value = '';
    }
   retf(ret);
}
function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code = 0;
    retValue.id = query.roleid;
    retValue.order = query.orderid;
    retValue.cporder = query.attach;
    retValue.info = '';

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('error');
            console.log('getNotifyUrl error');
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
                console.log('request unsuccess = '+JSON.stringify(error));
                console.log('request response.statusCode='+response.statusCode);
                console.log('request body= '+JSON.stringify(body));
                if(!error && response.statusCode == 200){
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        retf('error');
                        return;
                    }
                    if(retOut.code =='0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(query.amount*100>=retOut.amount*0.9
                            &&query.amount*100<=retOut.amount){
                                if(retOut.status=='2'){
                                    retf('error');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount*100);
                                    retf('success');
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
                            retf('error');
                            return;
                        }
                    }else{
                        retf('error');
                        return;
                    }
                }else{
                    retf('error');
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
    retValue.id = query.roleid;
    retValue.order = query.orderid;
    retValue.cporder = query.attach;
    retValue.info = '';

    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('error');return;
    }

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
            retValue.amount = '' + query.amount*100 + '';
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
                console.log('request unsuccess = '+JSON.stringify(error));
                console.log('request response.statusCode='+response.statusCode);
                console.log('request body= '+JSON.stringify(body));
                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('error');return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);

                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount*100);
                        retf('success');
                    }else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('error');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('error');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var signArray  = ["orderid",'username','gameid','roleid',
        'serverid','paytype','amount','paytime','attach'];
    var  str = '';

    for(var i=0;i<signArray.length;i++){
        for( j in query){
            if(j==signArray[i]){
                str+=signArray[i]+'='+query[j]+'&'
            }
        }
    }
    str +='appkey='+ attrs.app_key;
    console.log(str);
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

/**
 * 核实外部订单号的唯一性
 * @param
 *      query   请求串Obj
 *      retf    返回校验结果 True 合法|False 不合法
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,query.orderid,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.attach, query.orderid,function(res){
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