/**
 * Created by TypeSDK 2017/1/7.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var rsa = require('node-rsa');

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
    var tokens = cloned.token.split('|');
    var n = tokens[0];
    var v = tokens[1];
    var h = tokens[2];
    var t = tokens[3];

    ret.AmigoToken = {};
    ret.AmigoToken.n = n;
    ret.AmigoToken.v = v;
    ret.AmigoToken.h = h;
    ret.AmigoToken.t = t;
    //ret.sign = cloned.sign;
    return true;
}

function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.apikey = attrs.app_key;
    cloned.ts = Math.floor(Date.now()/1000);
    cloned.nonce = require('crypto').randomBytes(4).toString('hex');

    var signatureString = {};

    signatureString.ts = cloned.ts;
    signatureString.nonce = cloned.nonce;
    signatureString.method = "POST";
    signatureString.uri = "/account/verify.do";
    signatureString.host = "id.gionee.com";
    signatureString.port = "443";
    
    var osignatureString = signatureString.ts + '\n' + signatureString.nonce + '\n' + signatureString.method + '\n' + signatureString.uri + '\n' + signatureString.host + '\n' + signatureString.port + '\n' + '\n';
    var osign = crypto.createHmac('sha1',attrs.secret_key).update(osignatureString).digest('Base64');
    var auth = "MAC id=\"" + attrs.app_key + "\",ts=\"" + cloned.ts + "\",nonce=\"" + cloned.nonce + "\",mac=\"" + osign + "\"";
    var options = {
        url: params.out_url,
        method:params.method,
        body:cloned.AmigoToken,
        json: true,
        headers: {
            'Authorization':auth,
             'Content-Type': 'application/x-www-form-urlencoded'
             // JSON.stringify(cloned.AmigoToken).length
        }
    };

    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        console.log(error);
        console.log(body);
        if (!error && response.statusCode == 200) {
            var retOut = body;
            if(!retOut.r||retOut.r==0){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.ply[0].pid;
                ret.nick = "";
                ret.token = cloned.AmigoToken;
                ret.value = JSON.stringify(retOut);
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
                ret.value = JSON.stringify(retOut);
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
    retValue.id =query.out_order_no.split("|",2)[1] || '';
    retValue.order = "";
    retValue.cporder =query.out_order_no.split("|",2)[0];
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('FAILURE');
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
                        if(retValue.cporder ==retOut.cporder
                            &&query.deal_price * 100>=retOut.amount*0.9
                            &&query.deal_price * 100<=retOut.amount){
                            if(retOut.status=='2'){
                                retf('FAILURE');
                                return;
                            }else if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.deal_price * 100);
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
    retValue.id = query.out_order_no.split("|",2)[1] || '';
    retValue.order = "";
    retValue.cporder = query.out_order_no.split("|",2)[0];
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
            retValue.amount = '' + query.deal_price * 100 + '';

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
                console.log(" error   "+ error );
                console.log(body);
                 if (!error && response.statusCode == 200) {
                    var retOut = body;
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILURE');return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.deal_price * 100);
                        retf('success');
                        //retf('FAILURE');
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


function checkSignPay(attrs,query)
{

    var tempPublicKey = attrs.public_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PUBLIC KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PUBLIC KEY-----");

    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\r\n";
    }
    var PublicKey  = beginArray.join("");

    var StrSignatureString = "api_key=" + query.api_key + "&" +
    "close_time=" + query.close_time + "&" +
    "create_time=" + query.create_time + "&" +
    "deal_price=" + query.deal_price + "&" +
    "out_order_no=" + query.out_order_no + "&" +
    "pay_channel=" + query.pay_channel + "&" +
    "submit_time=" + query.submit_time + "&" +
    "user_id=" + "null";


    var sign = crypto.createVerify('RSA-SHA1');
    //sign.update(logicCommon.utf16to8(StrSignatureString));
    sign.update(StrSignatureString);
    return sign.verify(PublicKey,query.sign,'base64');
}

function checkOrder()
{
    return false;
}

function CreateChannelOrderSign(query,key)
{
    console.log("********* channelOrderSign  **************");
    var strdata = '' + query.api_key +
        query.deal_price +
        query.deliver_type +
       // query.notify_url +
        query.out_order_no +
        query.subject +
        query.submit_time +
        query.total_fee;
    console.log(strdata);

    var sign = crypto.createSign('RSA-SHA1');
    //sign.update(logicCommon.utf16to8(strdata));
    sign.update(strdata);
    var t = sign.sign(key,'base64');
    console.log("CRYPTO SIGN: " +  t);
    return t;
}

function GetNowStr()
{
    var util = require('util');
    var now = new Date();

    pad = function(tbl) {
        return function(num, n) {
            return (0 >= (n = n-num.toString().length)) ? num : (tbl[n] || (tbl[n] = Array(n+1).join(0))) + num;
        }
    }([]);
    var result = ''+ now.getFullYear() +
        pad(now.getMonth() + 1,2) +
        pad(now.getDate(),2) +
        pad(now.getHours(),2) +
        pad(now.getMinutes(),2) +
        pad(now.getSeconds(),2);

    return result;
}

function CreateChannelOrder(attrs,params,query,ret,retf)
{
    var tempPublicKey = attrs.private_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PRIVATE KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPublicKey);
    strArray.push("-----END PRIVATE KEY-----");

    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\r\n";
    }
    var PrivateKey  = beginArray.join("");

     var data = {
        "player_id" : query.playerid
        ,"api_key" : attrs.app_key
        ,"deal_price" : query.price
        ,"deliver_type" : "1"
        ,"out_order_no" : query.cporder
        ,"subject" : query.subject
        ,"submit_time" : GetNowStr()
        ,"total_fee" : query.price
    };

    data.sign = CreateChannelOrderSign(data,PrivateKey);

    var options = {
        url: 'https://pay.gionee.com/order/create',
        method: 'POST',
        body: data,
        json: true
    };
    console.log(options);

    var retdata = {
        "code" : -99
        ,"playerid":""
        ,"order": ""
        ,"cporder":""
        ,"submit_time":data.submit_time
        ,"msg":"UNKNOWN ERROR"
        ,"data":{}
    };

    request(options, function (error, response, body) {
         console.log(" **** error **** :" +error);
         console.log(body);
         if (!error && response.statusCode == 200) {
            var retOut = body;

            //日志记录CP端返回
            console.log(retOut);
            if (typeof retOut.status == 'undefined'){
                retdata.code = -2;
                retdata.msg = 'FORMAT ERROR';
                retf(retdata);
            }

            if (retOut.status == "200010000")
            {
                retdata.code = 0;
                retdata.msg = 'NORMAL';
                retdata.playerid = query.playerid;
                retdata.order = retOut.order_no;
                retdata.cporder = retOut.out_order_no;
                retdata.data = retOut;
                retf(retdata);
            }
            else {
                retdata.code = 1;
                retdata.msg = 'RETURN ERROR';
                retdata.data = retOut;
                retf(retdata);
            }
        }else
        {
            retdata.code = -1;
            retdata.msg = 'NET ERROR';
            retf(retdata);
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
    retf(true);
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;

exports.CreateChannelOrder = CreateChannelOrder;

exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;