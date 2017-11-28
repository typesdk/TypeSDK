/**
 * Created by TypeSDK on 2016/10/10.
 */
var merge = require('merge');
var crypto = require('crypto');
var logicCommon = require('./logicCommon.js');
var redisconfig = require('../config.json');
var redisDA = require("./dbRedis.js");

function doAction(game, channel, action, gattrs, channeloptions, oquery, params, ret, retf) {

  console.log(channel + '::' + action);

  var logic = require('./logic' + channel + '.js');

  var attrs = channeloptions.attrs;

  //更新开关
  var channelupdateswitch = false;


  if (typeof logic == 'undefined') {
    return false;
  }

  //console.log(oquery);

  switch (true) {
    case /^(L|l)ogin$/.test(action):
      var query = {};
      var tarr = ["id", "token", "data"];
      // 检验原始参数签名
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }

      //原始参数转换为各渠道特定参数
      if (!logic.convertParamLogin(oquery, query)) {
        console.log('PARAM ERROR');
        ret.code = -1;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      console.log(query);
      // console.log(params.in_params);
      //检验参数合法性
      if (!verifyParam(params.in_params, query)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      //同步向渠道登录接口发送请求（处理回调中记录请求返回结果日志）
      logic.callChannelLogin(attrs, params, query, ret, retf,gattrs);
      break;
    case /^(P|p)ay$/.test(action):
      //渠道参数转换 -- 实际不需要
      var query = oquery;
      //console.log(query);
      // 检验请求参数合法性
      if (!verifyParam(params.in_params, query)) {

        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }


      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验请求参数中的签名
      if (!logic.checkSignPay(attrs, query)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }

      // 游戏服查询订单 对比订单，id = '101'或 '202' 是AppStore
      // 海外苹果非越狱渠道比较特殊，得先进入逻辑验证，收到苹果返回才能订单对比

      if (channeloptions.id == '101' || channeloptions.id == '202') {
        logic.callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channeloptions.id);
      } else {
        logic.compareOrder(attrs, gattrs, params, query, ret, game, channeloptions.id, function (obj) {
          console.log(obj);
          if (obj && typeof obj == 'object') {

            if (obj.code == '0000') {
              //C.同步向游戏支付接口发送请求（处理回调中记录请求返回结果日志）
              logic.callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channeloptions.id);
            } else {
              retf(obj);
              return;
            }
          } else {
            retf(obj);
            return;
          }
        });
      }
      break;
    case /^ClientPay$/.test(action):
      //接收SDKClient转发的支付回调
      //渠道参数转换 -- 实际不需要
      var query = oquery;

      // 检验请求参数合法性
      if (!verifyParam(params.in_params, query)) {

        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      attrs = merge(attrs, gattrs);
      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验请求参数中的签名
      if (!logic.checkSignPay(attrs, query)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";
        retf(ret);
        return true;
      }
      // 游戏服查询订单 对比订单，id = '101'或 '202' 或 '1300' 是AppStore
      // 海外苹果非越狱渠道比较特殊，得先进入逻辑验证，收到苹果返回才能订单对比
      if (channeloptions.id == '101' || channeloptions.id == '202' || channeloptions.id == '1300' || channeloptions.id == '1301') {
        logic.callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channeloptions.id);
      } else {
        logic.compareOrder(attrs, gattrs, params, query, ret, game, channeloptions.id, function (obj) {
          if (obj && typeof obj == 'object') {

            if (obj.code == '0000') {
              //C.同步向游戏支付接口发送请求（处理回调中记录请求返回结果日志）
              logic.callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channeloptions.id);
            } else {
              retf(obj);
              return;
            }
          } else {
            retf(obj);
            return;
          }
        });
      }
      break;
    case /^(S|s)aveOrder$/.test(action):
      //console.log("• Matched 'SaveOrder' test");

      var soParam =
          {
            "cporder": ""
            , "data": ""
            , "notifyurl": ""
            , "verifyurl": ""
          };

      // if (!oquery.searchurl) {
      //     oquery.searchurl = oquery.verifyurl;
      // }
      var tarr = ["cporder", "data"];

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验参数签名
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      logicCommon.saveOrder(game, channel, oquery.cporder, oquery.notifyurl, oquery, ret, retf, oquery.verifyurl, channeloptions.id);

      break;
    case /^(C|c)heckOrder$/.test(action):   //CP发起的订单号校验
      //console.log("• Matched 'SaveOrder' test");
      var soParam =
          {
            "cporder": ""
          };
      var tarr = ["cporder"];

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验参数签名
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }

      if (!logic.checkOrder(attrs, params, oquery, ret, retf)) {
        logicCommon.checkOrder(oquery.cporder, ret, retf);
      }

      break;
    case /^CHCheckOrder$/.test(action):   //渠道发起的订单号校验
      console.log("CHCheckOrder");

      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验参数签名
      if (!logic.checkSignCHCheckOrder(attrs, oquery)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }

      logic.chCheckOrder(attrs, params, oquery, channeloptions.id, ret, retf);
      break;
    case /^(C|c)heckVersion$/.test(action):     //  更新逻辑
      //console.log("• Matched 'SaveOrder' test");
      var soParam =
          {
            "version": "0"
          };

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }

      logicCommon.checkVersion(oquery.version, channeloptions, ret, retf, channelupdateswitch);

      break;
    case /^(G|g)etServerMap$/.test(action):
      if (typeof logic.getServerMapById != "undefined") {
        logic.getServerMapById(attrs, oquery, retf);
      }
      break;
    case /^(C|c)reateChannelOrder$/.test(action):
      var soParam = {};
      var tarr = [];

      if (channeloptions.id != '2') {
        soParam =
            {
              "playerid": "0",
              "price": "0",
              "cporder": "0",
              "subject": ""
            };
        tarr = ["playerid", "price", "cporder", "subject"];

        //A.同步分渠道调用相应解析逻辑，包括以下逻辑
        //  1.检验参数签名
        if (!checkSign(gattrs.ckey, oquery, tarr)) {
          ret.code = -3;
          ret.msg = "SIGN ERROR";

          retf(ret);
          return true;
        }
        //检验参数合法性
        if (!verifyParam(soParam, oquery)) {
          console.log('PARAM ERROR');
          ret.code = -2;
          ret.msg = "PARAM ERROR";

          retf(ret);
          return true;
        }

        if (typeof logic.CreateChannelOrder != "undefined") {
          logic.CreateChannelOrder(attrs, params, oquery, ret, retf);
        }
        else {
          ret.code = -4;
          ret.msg = "NO FUNCTION";
          retf(ret);
        }
      } else {
        //  这两个渠道是应用宝支付渠道
        soParam =
            {
              "openid": "",
              "openkey": "",
              "zoneid": "",
              "billno": ""
            };

        tarr = ["playerid", "price", "cporder", "subject"];

        //  渠道统一验签，验签数据对应用宝没什么卵用
        if (!checkSign(gattrs.ckey, oquery, tarr)) {
          ret.code = -3;
          ret.msg = "SIGN ERROR";

          retf(ret);
          return true;
        }

        //检验参数合法性
        if (!verifyParam(soParam, oquery)) {
          console.log('PARAM ERROR');
          ret.code = -2;
          ret.msg = "PARAM ERROR";

          retf(ret);
          return true;
        }

        if (typeof logic.CreateChannelOrder != "undefined") {
          logic.CreateChannelOrder(game, channel, channeloptions.id, gattrs, attrs, params, oquery, retf);
        }
        else {
          ret.code = -4;
          ret.msg = "NO FUNCTION";

          retf(ret);
        }
      }

      break;
    case /^SearhchOrder$/.test(action):
      var soParam = {
        "cporder": ""
      };
      var tarr = ["cporder"];
      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验参数签名
      if (!checkSign(gattrs.ckey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      logicCommon.searchOrder(oquery.cporder, ret, retf);
      break;
    case /^Budan$/.test(action):
      var soParam = {
        "cporder": ""
        , "id": ""
        , "amount": ""
        , "order": ""
      };
      var tarr = ["id", "cporder", "amount", "order"];
      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      //A.同步分渠道调用相应解析逻辑，包括以下逻辑
      //  1.检验参数签名
      if (!checkSign(gattrs.ckey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      logicCommon.payBudan(attrs, gattrs, params, oquery, ret, game, channel, channeloptions.id, retf);
      break;
    case /^updateSDKstatus$/.test(action):
      var soParam =
          {
            "cporder": ""
          };

      var tarr = ["data", "cporder", "order", "type"];

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.ckey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      var status;
      if (oquery.type == 'chongfahuo') {
        status = 101;
      } else if (oquery.type == 'buchang') {
        status = 100;
      } else {
        ret.code = -2;
        ret.msg = 'PARAM ERROR';
        consol.log('param error');
        retf(ret);
        return;
      }
      logicCommon.UpdateOrderStatus(game, channel, oquery.cporder, oquery.order, status, 0);
      ret.code = 0;
      ret.msg = "NORMAL";
      retf(ret);
      return true;
      break;
    case /^asGameSerch$/.test(action):
      var soParam =
          {
            "cporder": "",
            "id": ""
          };

      var tarr = ["id", "order", "cporder", "info"];

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.ckey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        console.log(ret);
        return true;
      }
      logicCommon.asGameSearch(oquery.cporder, oquery.userId, ret, retf);
      break;
    case /^TCGetBalance$/.test(action):
      var soParam =
          {
            "openid": "",
            "openkey": "",
            "zoneid": ""
          };
      var tarr = ["openid", "openkey", "pay_token", "pf", "pfkey", "zoneid"];

      ret = {};

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      if (typeof logic.TCGetBalanceForAct != "undefined") {
        logic.TCGetBalanceForAct(attrs, params, oquery, ret, retf);
      }
      else {
        ret.code = -4;
        ret.msg = "NO FUNCTION";

        retf(ret);
      }

      break;
    case /^TCPresent$/.test(action):
      var soParam =
          {
            "openid": "",
            "openkey": "",
            "zoneid": ""
          };
      var tarr = ["openid", "openkey", "pay_token", "pf", "pfkey", "zoneid", "presenttimes", "billno"];

      ret = {};

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      if (typeof logic.TCPresent != "undefined") {
        logic.TCPresent(attrs, params, oquery, ret, retf);
      }
      else {
        ret.code = -4;
        ret.msg = "NO FUNCTION";

        retf(ret);
      }

      break;

    case /^TCGradePackage$/.test(action):
      var soParam =
          {
            "cmd": "",
            "openid": "",
            "contractid": "",
            "step": "",
            "payitem": "",
            "billno": "",
            "providetype": "",
            "sig": ""
          };

      ret = {};

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      if (typeof logic.TCGradePackage != "undefined") {
        logic.TCGradePackage(game, channel, action, attrs, params, oquery, ret, retf);
      }
      else {
        ret.ret = -4;
        ret.msg = "NO FUNCTION";

        retf(ret);
      }

      break;
    case /^TCChargeCallBack$/.test(action):
      var soParam =
          {
            "openid": "",
            "payitem": "",
            "token": "",
            "billno": "",
            "zoneid": "",
            "providetype": "",
            "sig": ""
          };

      ret = {};

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      if (typeof logic.TCChargeCallBack != "undefined") {
        logic.TCChargeCallBack(game, channel, action, attrs, params, oquery, ret, retf);
      }
      else {
        ret.ret = -4;
        ret.msg = "NO FUNCTION";

        retf(ret);
      }

      break;
    case /^TCSubscribeQuery$/.test(action):
      var soParam =
          {
            "openid": "",
            "openkey": ""
          };
      var tarr = ["openid", "openkey"];

      ret = {};

      //检验参数合法性
      if (!verifyParam(soParam, oquery)) {
        console.log('PARAM ERROR');
        ret.code = -2;
        ret.msg = "PARAM ERROR";

        retf(ret);
        return true;
      }
      if (!checkSign(gattrs.gkey, oquery, tarr)) {
        ret.code = -3;
        ret.msg = "SIGN ERROR";

        retf(ret);
        return true;
      }
      if (typeof logic.TCLogin != "undefined") {
        logic.TCLogin(attrs, params, oquery, ret, retf);
      }
      else {
        ret.code = -4;
        ret.msg = "NO FUNCTION";

        retf(ret);
      }
      break;
    case /^ParseBody$/.test(action):
      if (typeof logic.parseBody === 'function') {
        var req = oquery;
        logic.parseBody(req, retf);
      } else {
        retf({code: 0, data: {}});
      }
      break;
    case /^(G|g)etChannelConfig$/.test(action):

      var sdkRedisList = redisconfig.redisconfig;
      var gameId = gattrs ? gattrs.id : game;
      var channelId = channeloptions.id;
      var redisConfig = sdkRedisList;
      if (redisConfig != undefined) {
        var redis = require("redis");
        var Redis = redis.createClient(redisConfig.port, redisConfig.host);
        Redis.auth(redisConfig.pass, function () {
          console.log('connected to target redis-server');
        });
        logicCommon.getItemList(Redis, gameId, channelId, ret, function (obj) {
          retf(obj);
        });
      } else {
        ret.msg = 'gameId 不存在';
        retf(ret);
      }
      break;
    case /^(S|s)etChannelConfig$/.test(action):

      var sdkRedisList = redisconfig.redisconfig;
      var gameId = gattrs ? gattrs.id : game;
      var channelId = channeloptions.id;
      var redisConfig = sdkRedisList;
      var item = oquery.data;
      if (redisConfig != undefined) {
        var redis = require("redis");
        var Redis = redis.createClient(redisConfig.port, redisConfig.host);
        Redis.auth(redisConfig.pass, function () {
          console.log('connected to target redis-server');

        });

        //todo 后台配置同步接口
        logicCommon.setItemList(Redis, gameId, channelId, item, ret, function (obj) {
          retf(obj);
        });
      } else {
        ret.msg = 'gameId 不存在';
        retf(ret);
      }
      break;
    default:
      console.log("• Didn't match any test");
      break;
  }

  return true;
}

function verifyParam(params, query) {
  var org = params;
  cloned = merge(true, org);
  merge(cloned, query);

  for (var i in org) {
    //判断参数中是否该有的字段齐全
    if (org[i] == cloned[i]) {
      return false;
    }

    //判断参数中是否有为空的字段
    if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length) {
      return false;
    }
  }

  return true;
}

function checkSign(gkey, query, tarr) {
  var str = "";
  for (i in tarr) {
    str += query[tarr[i]] + '|';
  }
  console.log(str);
  //var osign = crypto.createHash('md5').update(logicCommon.utf16to8(str) + gkey).digest('hex');
  var osign = crypto.createHash('md5').update(str + gkey).digest('hex');
  console.log(query.sign + " :: " + osign);

  if (query.sign != osign) {
    return false;
  }
  return true;
}

exports.doAction = doAction;