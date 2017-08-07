/**
 * Created by TypeSDK on 2017/1/7.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var urlencode = require("urlencode");
var rsa = require("node-rsa");

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

    ret.tokenkey = cloned.token;
    return true;
}



function callChannelLogin( attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    cloned.sign = crypto.createHash('md5').update(attrs.app_key + cloned.tokenkey).digest('hex');

    var options = {
        url: params.out_url,
        method:params.method,
        qs: cloned
    };

    console.log('Login Options: ' + JSON.stringify(options));

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);

    //console.log(options);
    request(options, function (error, response, body) {
        console.log('HTTP ERROR: ' + JSON.stringify(error));
        console.log('HTTP BODY: ' + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = (typeof body == 'string' ? JSON.parse(body) : body);
            if(retOut.errorno == '0'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.guid;
                ret.nick = retOut.data.username;
                ret.token = cloned.tokenkey;
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
    //解析
    decryptPayData(query, attrs);
    if(typeof query.encryp_data.game_orderid == 'undefined' || query.encryp_data.game_orderid != query.game_orderid){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('failed'); return;
    }
    var retValue = {};
    retValue.code =  query.encryp_data.payflag == '1' ? '0':'1';
    retValue.id = query.guid;
    retValue.order = query.xiao7_goid;
    retValue.cporder = query.game_orderid;
    retValue.info = '';

    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('failed');return;
    }
    logicCommon.getNotifyUrl(game, retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf('failed');
            return;
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,query);
            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };

            console.log('Compare Options: ' + JSON.stringify(options));
            request(options, function (error, response, body) {
                console.log('HTTP ERROR: ' + JSON.stringify(error));
                console.log('HTTP BODY: ' + JSON.stringify(body));
                if(!error && response.statusCode == 200){
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        retf('failed');
                        return;
                    }
                    if(retOut.code=='0'){
                        if(parseInt(query.encryp_data.pay) * 100 >= retOut.amount * 0.9 && parseInt(query.encryp_data.pay)  * 100 <= retOut.amount){
                            if(retOut.status=='4'||retOut.status=='3'){
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,0);
                                retf('success');
                                return;
                            }else{
                                logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,2,parseInt(query.encryp_data.pay) * 100);
                                var data  = {};
                                data.code = '0000';
                                data.msg = 'NORMAL';
                                retf(data);
                                return;
                            }
                        }else{
                            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,3,0);
                            retf('failed');
                            return;
                        }
                    }else{
                        retf('failed');
                        return;
                    }
                }else{
                    retf('failed');
                    return;
                }
            });

        }
    });
}
function callGamePay(attrs,gattrs,params,query,ret,retf,game,channel,channelId)
{
    var retValue = {};
    retValue.code =  query.encryp_data.payflag == '1' ? '0':'1';
    retValue.id = query.guid;
    retValue.order = query.xiao7_goid;
    retValue.cporder = query.game_orderid;
    retValue.info = '';

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);

            retf('failed');
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);

            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = query.encryp_data.pay * 100 + '';

            logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,1,query);

            var options = {
                url: params.out_url,
                method: params.method,
                body: retValue,
                json: true
            };
            console.log("Pay Option :" + JSON.stringify(options));

            //打点：支付回调通知
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PayNotice);

            request(options, function (error, response, body) {
                console.log('HTTP ERROR: ' + JSON.stringify(error));
                console.log('HTTP BODY: ' + JSON.stringify(body));
                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('failed');
                        return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,parseInt(query.encryp_data.pay) * 100);
                        retf('success');
                    }
                    else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('failed');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('failed');
                }
            });
        }
    });
}

/**
 * RSA解密小七渠道参数
 * @param {object} query
 * @param {object} attrs
 * @private
 * */
function decryptPayData(query, attrs){
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

    var rsaPK = new rsa(PublicKey);
    var encryptStr = rsaPK.decryptPublic(query.encryp_data, "utf8");
    var encryptArr = encryptStr.split('&');
    var encryptObj = {};
    for(var i = 0; i< encryptArr.length; i ++){
        encryptObj[encryptArr[i].split('=')[0]] =encryptArr[i].split('=')[1];
    }
    console.log('EncryptObj: ' + JSON.stringify(encryptObj));
    query.encryp_data = encryptObj;
    console.log(JSON.stringify(query));
}

/**
 * 核实外部订单号的唯一性
 *
 * @param {string} game
 * @param {string} channel
 * @param {object} attrs
 * @param {Object} query
 * @param {function} retf
 * @public
 * */
function checkChOrder(game, channel,attrs, query, retf){
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(game, channel,query.oid,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, query.doid, query.oid,function(res){
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

/**
 * 支付回调签名验证
 *
 * @param {Object} attrs
 * @param {Object} query
 * @return {boolean}
 * @public
 * */
function checkSignPay(attrs,query){
    var sourceStr = getSignStr(query);
    console.log('CheckSignPay SourceStr: ' + sourceStr);

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

    var sign = crypto.createVerify('RSA-SHA1');
    //sign.update(logicCommon.utf16to8(sourceStr));
    sign.update(sourceStr);
    return sign.verify(PublicKey, query.sign_data, 'base64');
}

function checkOrder()
{
    return false;
}

function CreateChannelOrder(attrs,params,query,ret,retf)
{
    return false;
}

/**
 * 拼接支付回调签名加密参数
 *
 * @param {Object} query
 * @return {String}
 * @private
 * */
function getSignStr(query) {

    var arrKey = Object.keys(query).map(function (k) {
        return k;
    });
    var arr = new Array();
    for (var i in query) {
        arr[i] = query[i];
    }

    arrKey.sort();

    var arr2 = new Array();
    var key = "";

    var k = "";
    for (var j in arrKey) {
        k = arrKey[j];
        arr2[k] = arr[k];
    }

    var str = "";
    for (var key in arr2) {
        if (key != 'sign_data')
            str += key + '=' + urlencode(arr2[key]) + '&';
    }
    var signStr = str.substr(0, str.length - 1);
    return signStr;
}

exports.convertParamLogin = convertParamLogin;
exports.callChannelLogin = callChannelLogin;
exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;

exports.CreateChannelOrder = CreateChannelOrder;
exports.compareOrder= compareOrder;
exports.checkChOrder = checkChOrder;