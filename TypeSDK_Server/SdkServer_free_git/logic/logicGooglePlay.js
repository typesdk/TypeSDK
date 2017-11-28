/**
 * Created by TypeSDK 2016/10/10.
 */
var crypto = require('crypto');
var request = require('request');
var logicCommon = require('./logicCommon.js');

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
    var cquery = typeof query.purchaseData == 'string' ? JSON.parse(query.purchaseData) : query.purchaseData;
    var retValue = {};
    retValue.code = cquery.purchaseState == '0' ? 0 : 1;
    retValue.id = cquery.productId;
    retValue.order = cquery.developerPayload;
    retValue.cporder = cquery.developerPayload;
    retValue.info = "";

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf('FAILURE');
        } else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);

            retValue.gamename = game;
            retValue.sdkname = channel;
            retValue.channel_id = channelId;
            retValue.amount = '';

            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1,0, query);

            var options = {
                url: params.out_url,
                method: params.method,
                body: retValue,
                json: true
            };

            console.log('Options: ' + JSON.stringify(options));
            console.log(options);

            request(options, function (error, response, body) {
                console.log('CB-Error: ' + JSON.stringify(error));
                console.log('CB-Body: ' + JSON.stringify(body));

                var ret = {};
                if (!error && response.statusCode == 200) {
                    ret.code = 0;
                    ret.msg = 'Success';
                    ret.value = '';
                    retf(ret);
                }
                else {
                    ret.code = 1;
                    ret.msg = 'Fail';
                    ret.value = '';
                    retf(ret);
                }
                return;
            });
        }
    });

}

function checkSignPay(attrs, query) {
    var publickey = attrs.secret_key;
    var public_key = getPublicKey(publickey);
    var signture = query.dataSignature;

    var v = crypto.createVerify('sha1WithRSAEncryption');

    var purchData = typeof query.purchaseData == 'string' ? JSON.parse(query.purchaseData) : query.purchaseData;
    v.update(JSON.stringify(purchData));
    return v.verify(public_key, signture, 'base64');
}

function checkOrder(attrs, params, query, ret, retf) {
    return false;
}


function getPublicKey(publicKey) {
    if (!publicKey) {
        return null;
    }
    var key = chunkSplit(publicKey, 64, '\n');
    var pkey = '-----BEGIN PUBLIC KEY-----\n' + key + '-----END PUBLIC KEY-----\n';
    return pkey;
}

function chunkSplit(str, len, end) {
    len = parseInt(len, 10) || 76;

    if (len < 1) {
        return false;
    }
    end = end || '\r\n';
    return str.match(new RegExp('.{0,' + len + '}', 'g')).join(end);
}

function compareOrder(attrs, gattrs, params, query, ret, game, channel, retf) {
    var cquery = typeof query.purchaseData == 'string' ? JSON.parse(query.purchaseData) : query.purchaseData;
    //var cquery = JSON.parse(query.purchaseData);
    var retValue = {};
    retValue.code = cquery.purchaseState == '0' ? 0 : 1;
    retValue.id = cquery.productId;
    retValue.order = cquery.developerPayload;
    retValue.cporder = cquery.developerPayload;
    retValue.info = '';

    var retDate = {};
    retDate.code = 1;
    retDate.msg = 'Fail';
    retDate.value = '';

    if (retValue.code != '0') {
        retf(retDate);
        return;
    }

    logicCommon.getNotifyUrl(retValue.cporder, params, function (hasData) {
        if (!hasData) {
            retf(retDate);
            return;
        }  else  if (query.app_order_id == params.orderdata && query.product_id == params.goodsid && query.amount >= params.goodsprice*0.9&&query.amount <= params.goodsprice)
        {
            var data  = {};
            data.code = '0000';
            data.msg = 'NORMAL';
            retf(data);
            return;
        }
        else {
            retValue.sign = logicCommon.createSignPay(retValue, gattrs.gkey);
            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 1, 0,query);
            var options = {
                url: params.verifyurl,
                method: "POST",
                body: retValue,
                json: true
            };

            console.log('Options: ' + JSON.stringify(options));
            console.log(options);

            request(options, function (error, response, body) {
                console.log('CB-Error: ' + JSON.stringify(error));
                console.log('CB-Body: ' + JSON.stringify(body));

                if (!error && response.statusCode == 200) {
                    var retOut = body;
                    if (typeof retOut.code == 'undefined') {
                        retf(retDate);
                        return;
                    }
                    if (retOut.code == '0') {
                        //暂时提审，补单后续添加
                        if (cquery.developerPayload == retOut.cporder) {
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 4,query.money*100);
                            retDate.code = '0000';
                            retDate.msg = 'NORMAL';
                            retf(retDate);
                            return;

                        } else {
                            logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 3,0);
                            retf(retDate);
                            return;
                        }
                    } else {
                        retf(retDate);
                        return;
                    }
                } else {
                    retf(retDate);
                    return;
                }
            });
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
    var cquery = typeof query.purchaseData == 'string' ? JSON.parse(query.purchaseData) : query.purchaseData;

    var isIllegal = false;
    logicCommon.selCHOrderInRedis(channel,cquery.orderId,function(res){
        if(!res || typeof res == "undefined"){
            logicCommon.saveCHOrderInRedis(game, channel, cquery.developerPayload, cquery.orderId,function(res){
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

exports.checkSignPay = checkSignPay;
exports.callGamePay = callGamePay;
exports.checkOrder = checkOrder;
exports.compareOrder = compareOrder;
exports.checkChOrder = checkChOrder;