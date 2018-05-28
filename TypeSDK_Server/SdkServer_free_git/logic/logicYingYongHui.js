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
        //�жϲ������Ƿ���е��ֶ���ȫ
        if(org[i] == cloned[i] && i != "data" && i != "id")
        {
            return false;
        }

        //�жϲ������Ƿ���Ϊ�յ��ֶ�
        if(0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length && i != "data" && i != "id")
        {
            return false;
        }
    }

    ret.ticket = cloned.token ;

    return true;
}



function callChannelLogin(attrs,params,query,ret,retf)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);
    var options;

    if(attrs.product_id&&attrs.product_id!='11'){
        options = params.out_url_2+'?login_id='+attrs.product_id+'&login_key='+attrs.app_key+'&ticket='+cloned.ticket;
    }else{
        cloned.app_id = attrs.app_id;
        cloned.app_key =  attrs.app_key;
        options = {
            url: params.out_url_1,
            method:params.method,
            qs: cloned
        };
    }

    console.log("options.url:"+options.url);
    console.log("options:"+options);
    console.log(options);

    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    request(options, function (error, response, body) {
        if (!error && response.statusCode == 200) {
            console.log("body:"+body);
            var retOut = JSON.parse(body);
            if( retOut.status == '0'){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.data.user_id;
                ret.nick = "";
                ret.token = query.ticket;
                ret.value = retOut;

                //ret.ret = '1';
                //ret.msg = 'success';
                //ret.uid = '' + retOut.data.user_id;
                //ret.channel = '39';
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

               // ret.ret = '0';
               // ret.msg = 'error';
                //ret.uid = '0';
                //ret.channel = '39';
            }
        }
        else
        {
            //打点：验证失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifyErr);
            //ret.code = 2;
            //ret.msg = "OUT URL ERROR";
            //ret.value = "";

            ret.ret = '0';
            ret.msg = 'error';
            ret.uid = '0';
            ret.channel = '39';
        }
        retf(ret);
    });
}

function compareOrder(attrs,gattrs,params,query,ret,game,channel,retf){
    var transdata = JSON.parse(query.transdata);

    var retValue = {};
    retValue.code =transdata.result=='0'?'0':'1';
    retValue.id = transdata.appuserid;
    retValue.order = transdata.transid;
    retValue.cporder =  transdata.cporderid;
    retValue.info = '';

    if(retValue.code!='0'){
        retf('FAILED');
        return;
    }
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
                    if(retOut.code == '0'){
                        if(retOut.Itemid){
                            logicCommon.mapItemLists(attrs,retOut);
                        }
                        if(transdata.cporderid==retOut.cporder
                            &&transdata.money*100>=retOut.amount*0.9
                            &&transdata.money*100<=retOut.amount){
                                if(retOut.status=='2'){
                                    retf('FAILURE');
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,transdata.money*100);
                                    retf('SUCCESS');
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
    //console.log('++++++++++++++++');
    var transdata = JSON.parse(query.transdata);

    var retValue = {};
    retValue.code =transdata.result=='0'?'0':'1';
    retValue.id = transdata.appuserid;
    retValue.order = transdata.transid;
    retValue.cporder =  transdata.cporderid;
    retValue.info = '';

    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf('FAILED');
        return;
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
            retValue.amount = '' + transdata.money*100 + '';

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
                    console.log(retOut);
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILED');
                    }

                    if (retOut.code == '0')
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);
                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,transdata.money*100);
                        retf("SUCCESS");
                    }
                    else {
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf('FAILED');
                    }
                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf('FAILED');
                }
            });
        }
    });
}

function checkSignPay(attrs,query)
{
    var tempPublicKey = attrs.public_key;
    var beginArray1 = new Array();
    beginArray1.push('-----BEGIN PUBLIC KEY-----');

    var strArray1 = logicCommon.returnBase64Array(tempPublicKey);
    strArray1.push("-----END PUBLIC KEY-----");

    beginArray1 = beginArray1.concat(strArray1);
    for(var i = 0; i<beginArray1.length;i++){
        if(i != beginArray1.length-1)
            beginArray1[i] = beginArray1[i]+ "\n";
    }
    var publickey  = beginArray1.join("");

    var tempPrivateKey = attrs.private_key;
    var beginArray = new Array();
    beginArray.push('-----BEGIN PRIVATE KEY-----');

    var strArray = logicCommon.returnBase64Array(tempPrivateKey);
    strArray.push("-----END PRIVATE KEY-----");

    beginArray = beginArray.concat(strArray);
    for(var i = 0; i<beginArray.length;i++){
        if(i != beginArray.length-1)
            beginArray[i] = beginArray[i]+ "\n";
    }
    var PrivateKey  = beginArray.join("");

    var data = decodeURIComponent(query.transdata).toString('ascii');
    var v = crypto.createVerify('RSA-MD5');
    var key = publickey.toString('ascii');
    var sign = query.sign.replace(/\s+/g,"+").toString('ascii');

    console.log(data);
    //console.log('...............................');
    // console.log(key);
    // console.log('..............................');
    console.log(sign);
    v.update(data,'utf8');
    return v.verify(key,sign, 'base64');
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
    var transdata = JSON.parse(query.transdata);
    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,transdata.transid,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, transdata.cporderid, transdata.transid,function(res){
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
