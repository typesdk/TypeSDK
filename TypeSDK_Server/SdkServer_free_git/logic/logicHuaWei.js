/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var querystring = require("querystring");
var urlencode= require("urlencode");


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
    ret.playerId = cloned.id;
    ret.access_token = cloned.token.split("|")[0];
    ret.ts = cloned.token.split("|")[1];
    return true;
}



function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    var a = attrs.app_id + cloned.ts + cloned.playerId;
    var tempPublicKey = attrs.PublicKey;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');
    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");
    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\r\n";
    }
    var publickey  = beginArray.join("");
    var v = crypto.createVerify('RSA-SHA256');
    v.update(new Buffer(a, 'utf-8'));
    var flag = v.verify(publickey, query.access_token, 'base64');
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    if(flag){
        console.log('Sign Success');
        //打点：验证成功
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
        ret.code = 0;
        ret.msg = "NORMAL";
        ret.id = cloned.playerId;
        ret.token = cloned.access_token;
        ret.value = flag;
    }else{
        console.log('Sign Error');
        //打点：验证失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
        ret.code =  1;
        ret.msg = "LOGIN User ERROR";
        ret.id = cloned.playerId;
        ret.token = cloned.access_token;
        ret.value = flag;
    }
    retf(ret);    }

function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var retValue = {};
    retValue.code = query.result=='0'?'0':'1';
    retValue.id = query.extReserved;
    retValue.order = query.orderId;
    retValue.cporder =  query.requestId;
    retValue.info = "";

    var retData = {};
    retData.result = "99";
    if(retValue.code!='0'){
        retf(retData);
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf(retData);
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
                        retf(retData);
                        return;
                    }
                    if(retOut.Itemid){
                        logicCommon.mapItemLists(attrs,retOut);
                    }
                    if(query.extReserved==retOut.id
                        &&query.requestId==retOut.cporder
                        &&query.amount * 100>=retOut.amount*0.9
                        &&query.amount * 100<=retOut.amount){
                        if(retOut.status=='2'){
                            retf(retData);
                            return;
                        }else if(retOut.status=='4'||retOut.status=='3'){
                            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount * 100);
                            retData.result = "0";
                            retf(retData);
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
                        retf(retData);
                        return;
                    }
                }else{
                    retf(retData);
                    return;
                }
            });
        }
    });

}

function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code = query.result=='0'?'0':'1';
    retValue.id = query.extReserved;
    retValue.order = query.orderId;
    retValue.cporder =  query.requestId;
    retValue.info = "";
    var retData = {};
    retData.result = "99";
    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf(retData);
        return;
    }
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData){
        if(!hasData)
        {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf(retData);
            return;
        }else{
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);

            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.amount * 100 + '';

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

                    //��־��¼CP�˷���
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retData.result = "99";
                        retf(retData);
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.amount * 100);
                        retData.result = "0";
                        retf(retData);
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                        retData.result = "99";
                        retf(retData);
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

                    retData.result = "99";
                    retf(retData);
                }
            });
        }
    });
}



function checkSignPay(attrs,query)
{

    var arrKey = Object.keys(query).map(function(k){return k;});
    console.log(arrKey);

    var arr = new Array();
    for(var i in query){
        arr[i] = query[i];
    }
    console.log(arr);

    arrKey.sort();
    console.log(arrKey);

    var arr2 = new Array();
    var key = "";

    var k ="";
    for(var j in arrKey){
        k = arrKey[j];
        arr2[k] = arr[k];
    }

    console.log(arr2);

    var str = getSignStr(arr2);
    console.log(str);

    var tempPublicKey = attrs.product_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");

    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\r\n";
    }
    var publickey  = beginArray.join("");

    var v = crypto.createVerify('SHA1');
    //v.update(logicCommon.utf16to8(str));
    v.update(str);
    var flag = v.verify(publickey,query.sign, 'base64');
    if(flag){
        console.log('Sign Success');
    }else{
        console.log('Sign Error');
    }
    return flag;
}

function getSignStr(param){
    var str = "";
    for(var key in param){
        if(key != 'sign')
            str += key + '=' + param[key] + '&';
    }
    var signStr = str.substr(0,str.length -1);
    return signStr;
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
        var retStr = {};
        retStr.code = 0;
        retStr.msg = 'NORMAL';
        retStr.playerid = 0;
        retStr.order = 0;
        retStr.cporder = 0;
        retStr.data = attrs.secret_key;
        retf(JSON.stringify(retStr));
    }
    else if(query.cporder == "233"  && query.cporder && query.price)
    {
        var retStr = {};
        retStr.code = 0;
        retStr.msg = 'NORMAL';
        retStr.playerid = 0;
        retStr.order = 0;
        retStr.cporder = 0;
        retStr.data = attrs.app_key;
        retf(JSON.stringify(retStr));
    }
    else
    {
        retErrorData.code = -2;
        retErrorData.msg = 'ERROR';
        retf(retErrorData);
    }
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
            logicCommon.saveCHOrderInRedis(game, channel, query.requestId, query.orderId,function(res){
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
exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;