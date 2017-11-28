/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var merge = require('merge');
var logicCommon = require('./logicCommon.js');
var buildBuffer = require('./buildRequestBody.js').buildBuffer;

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
    var tokenArr= cloned.token.split('|');
    console.log(tokenArr);
    var macaddress = tokenArr[2];
    if(!macaddress){
        return false;
    }
    var currentIpaddress = tokenArr[3];
    if(!currentIpaddress){
        return false;
    }
    macaddress=macaddress.split(':').join("");
    var currentArr  = currentIpaddress.split('.');
    var ipaddress = '';
    currentArr.forEach(function(str){
        if(str.length==3){
            ipaddress+= str;
        }else if(str.length==2){
            ipaddress+='0'+  str;
        }else if(str.length==1){
            ipaddress+='00'+  str;
        }
    });
    ret.access_token = tokenArr[0];

    var data = {
        "userId":cloned.id,
        "imei":tokenArr[1],
        "macaddress":macaddress,
        "ipaddress":ipaddress
    };
    logicCommon.saveUserInfo(data,cloned.id,function(obj){
        if(obj.code==0){
            console.log('创建成功');
        }else{
            console.log('创建失败');
        }
    });
    return true;

}

function callChannelLogin(attrs,params,query,ret,retf,gattrs)
{
    var cloned = merge(true, params.out_params);
    merge(cloned,query);

    cloned.client_secret = attrs.app_key;
    cloned.client_id = attrs.app_id;
    var options = {
        url: params.out_url,
        method:params.method,
        headers: {
            'client_id': cloned.client_id,
            'access_token' : cloned.access_token,
            'client_secret' : cloned.client_secret
        }
    };
    console.log(options);
    //打点：登录验证
    logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.RelaySDKVerify);
    process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";     //设置Shell环境变量，允许不使用证书到SSL站点
    request(options, function (error, response, body) {
        console.log("CB-Error: " + JSON.stringify(error));
        console.log("CB-Body: " + JSON.stringify(body));

        if (!error && response.statusCode == 200) {
            var retOut = body;
            if(typeof retOut == "string"){
                try{
                    retOut = JSON.parse(retOut);
                }catch(e){
                    console.log(e);
                }
            }

            if(retOut.user_id){
                //打点：验证成功
                logicCommon.sdkMonitorDot(logicCommon.dotType.LoginDot.ChVerifySuc);

                ret.code = 0;
                ret.msg = "NORMAL";
                ret.id = retOut.user_id;
                ret.nick = '';
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
    retValue.code = (query.status  == '00000'　&& query.hRet == '0') ? 0 : 1;
    retValue.id = query.orderid.split('|')[1];
    retValue.order = '';
    retValue.cporder = query.orderid.split('|')[0];
    retValue.info = '';
    var retData = '<?xml version="1.0" encoding="UTF-8"?><callbackRsp>1</callbackRsp>';
    if(retValue.code!='0'){
        retf(retData);return;
    }

    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            retf(retData);
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

            console.log(options);

            request(options, function (error, response, body) {
                console.log("CB-Error: " + JSON.stringify(error));
                console.log("CB-Body: " + JSON.stringify(body));

                if(!error && response.statusCode == 200){
                    var retOut = body;
                    if (typeof retOut.code == 'undefined'){
                        retf(retData);
                        return;
                    }

                    if(retOut.code =='0'){
                        if(query.payfee>=retOut.amount*0.9
                            &&query.payfee<=retOut.amount){
                                if(retOut.status=='2'){
                                    retf(retData);
                                    return;
                                }else if(retOut.status=='4'||retOut.status=='3'){
                                    logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.payfee);
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
                            retf(retData);
                            return;
                        }
                    }else{
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
    retValue.code = (query.status  == '00000'　&& query.hRet == '0') ? 0 : 1;
    retValue.id = query.orderid.split('|')[1];
    retValue.order = '';
    retValue.cporder = query.orderid.split('|')[0];
    retValue.info = '';
    var retData = '<?xml version="1.0" encoding="UTF-8"?><callbackRsp>1</callbackRsp>';
    if(retValue.code!='0'){
        //打点：其他支付失败
        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
        retf(retData);return;
    }
    console.log(retValue);
    logicCommon.getNotifyUrl(retValue.cporder,params,function(hasData) {
        if (!hasData) {
            //打点：其他支付失败
            logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
            retf(retData);
        } else {
            retValue.sign = logicCommon.createSignPay(retValue,gattrs.gkey);
            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '' + query.payfee;

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
                console.log("CB-Error: " + JSON.stringify(error));
                console.log("CB-Body: " + JSON.stringify(body));

                if (!error && response.statusCode == 200) {
                    var retOut = body;

                    //日志记录CP端返回
                    if (typeof retOut.code == 'undefined'){
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retData);return;
                    }

                    if (retOut.code == 0)
                    {
                        //打点：服务器正确处理支付成功回调
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.PaySuc);

                        logicCommon.UpdateOrderStatus(game,channel,retValue.cporder,retValue.order,4,query.payfee);
                        retf(retData);
                    }else{
                        //打点：其他支付失败
                        logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                        retf(retData);
                    }

                }else
                {
                    //打点：其他支付失败
                    logicCommon.sdkMonitorDot(logicCommon.dotType.PayDot.Error);
                    retf(retData);
                }
            });
        }
    });
}

function checkSignPay(attrs, query) {
    var signArr = ["orderid", "ordertime", "cpid", "appid", "fid", "consumeCode", "payfee", "payType", "hRet", "status"];
    var str = '';
    signArr.forEach(function (obj) {
        str += obj + "=" + query[obj] + '&';
    });
    str += "Key=" +　attrs.secret_key;
    var osign = crypto.createHash('md5').update(str).digest('hex');
    console.log(query.signMsg + " :: " + osign);
    if (query.signMsg != osign) {
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
 * 渠道发起的订单校验
 * */
function chCheckOrder(attrs, params, oquery, channel, ret, retf){
    var userId = parseInt(oquery.orderid.split("|")[1], 32);
    var orderServiceId = oquery.orderid.split("|")[2];
    orderServiceId = parseInt(orderServiceId, 32);
    if(orderServiceId == ""){
        retf("<?xml version='1.0' encoding='UTF-8'?><paymessages><checkOrderIdRsp>1</checkOrderIdRsp></paymessages>");
        return;
    }
    //获取用户相关数据
    logicCommon.getUserInfo(userId, function(data){
        if(typeof data != 'undefined' && data.code == 0){
            var userInfo;
            try{
                userInfo = JSON.parse(data.value);
            }catch(e){
                console.log("存储的用户信息数据结构非JSON，源数据： " + data.value);
                retf("<?xml version='1.0' encoding='UTF-8'?><paymessages><checkOrderIdRsp>1</checkOrderIdRsp></paymessages>");
                return;
            }
            var retStr =
                "<?xml version='1.0' encoding='UTF-8'?><callbackRsp>1</callbackRsp><paymessages><checkOrderIdRsp>0</checkOrderIdRsp><gameaccount>"    //0-验证成功 1-验证失败，必填
                + userInfo.userId + "</gameaccount><imei>"  //游戏账号，长度<=64，必填
                + userInfo.imei + "</imei><macaddress>" //设备标识，必填
                + userInfo.macaddress + "</macaddress><ipaddress>"  //MAC地址去掉冒号，必填
                + userInfo.ipaddress + "</ipaddress><serviceid>"   //IP地址，去掉点号，补零到每地址段3位，如：192168000001，必填
                + orderServiceId + "</serviceid><channelid>"//12位计费点（业务代码），必填
                + channel + "</channelid><cpid>"   //渠道ID，必填，如00012243
                + attrs.sdk_cp_id + "</cpid><ordertime>"//CPID，必填，账户管理，账户信息中查看
                + logicCommon.getNowFormatDate('b') + "</ordertime><appversion>" //订单时间戳，14位时间格式
                + '1.0' + "</appversion></paymessages>";    //应用版本号，必填，长度<=32

            retf(retStr);
            return;
        }
    });
}

/**
 * 订单校验接口签名验证
 * */
function checkSignCHCheckOrder(attrs,oquery){
    var signStr = "orderid=" + oquery.orderid
        + "&Key=" + attrs.secret_key;
    var osign = crypto.createHash('md5').update(signStr).digest('hex');

    console.log(oquery.signMsg + " :: " + osign);

    if (oquery.signMsg != osign)
    {
        return false;
    }
    return true;
}

function CreateChannelOrder(attrs, params, oquery, ret, retf){
    retf(true);
}

/**
 * 解析并格式化渠道请求
 * */
function parseBody(req, retf){
    var data = {code:0, data: {}};

    buildBuffer(req, function(bufferStr){
        if(typeof bufferStr == "string" && bufferStr.length > 0){
            var xmlreader = require('xmlreader');

            xmlreader.read(bufferStr, function(err, resp){
                if(err !== null){
                    //打点： 其他错误
                    logicCommon.sdkMonitorDot(logicCommon.dotType.OtherDot.Error);
                    retf(data);
                    return;
                }

                if(resp.callbackReq){
                    channelParam = formatMessage(resp.callbackReq);
                    data.code = 1;
                    data.data = channelParam;
                    retf(data);
                }else if(resp.checkOrderIdReq){
                    channelParam = formatMessage(resp.checkOrderIdReq);
                    data.code = 1;
                    data.data = channelParam;
                    retf(data);
                }else{
                    retf(data);
                    return;
                }
            });
        }else{
            retf(data);
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
exports.compareOrder = compareOrder;
exports.chCheckOrder =chCheckOrder;
exports.checkChOrder = checkChOrder;
exports.checkSignCHCheckOrder = checkSignCHCheckOrder;
exports.CreateChannelOrder = CreateChannelOrder;
exports.parseBody = parseBody;