/**
 * Created by TypeSDK 2016/11/16
 */

var express = require('express');
var multiparty = require('multiparty');
var router = express.Router();
var merge = require('merge');

var channelConfig = require('../logic/channelConfig.js').channelsBuffer;
var logic = require('../logic/logic.js');
var retObj = require('../logic/returnObject.js');

var sylog = require('../logic/sylog.js');
var logger = sylog.loggerf('sdk');
var xmlreader = require('xmlreader');
var logicCommon = require('../logic/logicCommon.js');
var buildRequestBody = require("../logic/buildRequestBody.js");
var parseBody = require("../logic/buildRequestBody.js").commonParseBody;

router.get('/:game/:channel/:action/', function (req, res) {
  var csllogger = sylog.loggerf('console');
  csllogger.requrl = req.protocol + '://' + req.headers.host + req.url;

  console.log('GAME:' + req.params.game + ' CHANNEL:' + req.params.channel + ' ACTION:' + req.params.action);
  var isRoute = false;

  retObj.init();

  //var matchgame = config.games.filter(function(e){return e.name == req.params.game;});
  var matchgame = [];
  if (channelConfig['game' + req.params.game]) matchgame.push(channelConfig['game' + req.params.game]);

  console.log(req.params);

  if (matchgame.length > 0) {
    //var matchchannel = matchgame[0].channels.filter(function(e){return e.name == req.params.channel;});
    var matchchannel = [];
    //  根据channelid获取匹配的channel参数
    if (matchgame[0]['ch' + req.params.channel]) matchchannel.push(matchgame[0]['ch' + req.params.channel]);
    console.log(matchchannel);
    if (matchchannel.length > 0) {
      var requestQuery = req.query;

      //var matchaction = matchchannel[0].actions.filter(function(e){return e.name == req.params.action;});
      //  根据渠道名获取渠道配置信息
      var caConfig = require('../channel/ca' + matchchannel[0].name);

      if (typeof caConfig != "undefined") {
        console.log(caConfig);
        var matchaction = caConfig.actions.filter(function (e) {
          return e.name == req.params.action;
        });

        var g = matchgame[0].attrs;
        var a = matchchannel[0];
        var p = matchaction[0] || {};

        logger.info("***GET PARAM:****");
        logger.info(requestQuery);
        isRoute = logic.doAction(matchgame[0].id, matchchannel[0].name, req.params.action, g, a, requestQuery, p, retObj.v, retf);
      }
    }
  }

  if (!isRoute) {
    retObj.v.code = -1;
    retObj.v.msg = "ROUTE ERROR";
    retf(retObj.v);
  }

  function retf(ret) {
    logger.info("RESPONSE:");
    logger.info(ret);
    if (typeof(ret) == 'object' && ret) {
      res.send(JSON.stringify(ret));
    } else {
      res.end(ret);
    }
  }

});

router.post('/:game/:channel/:action/', function (req, res) {
  //console.log(req);
  var csllogger = sylog.loggerf('console');
  csllogger.requrl = req.protocol + '://' + req.headers.host + req.url;

  console.log('GAME:' + req.params.game + ' CHANNEL:' + req.params.channel + ' ACTION:' + req.params.action);


  logger.info('POST - GAME:' + req.params.game + ' CHANNEL:' + req.params.channel + ' ACTION:' + req.params.action);
  var isRoute = false;

  retObj.init();


  var matchgame = [];

  //同步新增游戏逻辑
  if (/^(S|s)etChannelConfig$/.test(req.params.action)) {
    if (!channelConfig['game' + req.params.game]) channelConfig['game' + req.params.game] = {
      id: req.params.game,
      ch1: {id: 1, name: 'UC',attrs:{}}
    };
  }

  console.log('channelConfig:', channelConfig);

  if (channelConfig['game' + req.params.game]) matchgame.push(channelConfig['game' + req.params.game]);

  if (matchgame.length > 0) {
    var matchchannel = [];

    console.log('before - matchgame[0]:', matchgame[0]);

    //同步新增游戏逻辑
    if (/^(S|s)etChannelConfig$/.test(req.params.action)) {
      if (!matchgame[0]['ch' + req.params.channel]) matchgame[0]['ch' + req.params.channel] = {id: 1, name: 'UC',attrs:{}};
    }

    console.log('after - matchgame[0]:', matchgame[0]);

    if (matchgame[0]['ch' + req.params.channel]) matchchannel.push(matchgame[0]['ch' + req.params.channel]);

    if (matchchannel.length <= 0) {
    } else {
      //var matchaction = matchchannel[0].actions.filter(function(e){return e.name == req.params.action;});
      //  根据渠道名获取渠道配置信息
      var caConfig = require('../channel/ca' + matchchannel[0].name);

      if (typeof caConfig != "undefined") {
        var matchaction = caConfig.actions.filter(function (e) {
          return e.name == req.params.action;
        });
        var g = matchgame[0].attrs;
        var a = matchchannel[0];
        var p = matchaction[0] || {};

        var urlSuffix = req._parsedUrl.query;

        logic.doAction(matchgame[0].id, matchchannel[0].name, "ParseBody", g, a, req, p, retObj.v, function (res) {
          if (res.code == 1) {
            //特殊渠道解析完成
            var requestBody = res.data;
            logger.info("POST PARAM");
            logger.info(res.data);
            switch (urlSuffix) {
              case "serviceid=validateorderid":   //沃商店 发起的订单校验接口
                isRoute = logic.doAction(matchgame[0].id, matchchannel[0].name, 'CHCheckOrder', g, a, requestBody, p, retObj.v, retf);

                break;
              default :
                isRoute = logic.doAction(matchgame[0].id, matchchannel[0].name, req.params.action, g, a, requestBody, p, retObj.v, retf);
                break;
            }

            if (!isRoute) {
              retObj.v.code = -1;
              retObj.v.msg = "ROUTE ERROR";
              retf(retObj.v);
            }
          } else {
            //一般渠道解析
            parseBody(req, function (code, msg, data) {
              logger.info("POST PARAM");
              logger.info(data);

              if (code == 0) {
                isRoute = logic.doAction(matchgame[0].id, matchchannel[0].name, req.params.action, g, a, data, p, retObj.v, retf);
              } else {
                console.log("Parse Error: " + msg);
                isRoute = false;
              }

              if (!isRoute) {
                retObj.v.code = -1;
                retObj.v.msg = "ROUTE ERROR";
                retf(retObj.v);
              }
            });
            isRoute = true;
          }
        });
        isRoute = true;
      }
    }
  }

  if (!isRoute) {
    retObj.v.code = -1;
    retObj.v.msg = "ROUTE ERROR";
    retf(retObj.v);
  }

  function retf(ret) {
    logger.info("RESPONSE:");
    logger.info(ret);
    if (typeof(ret) == 'object' && ret)
      res.json(ret);
    else
      res.end(ret);
  }
});

router.get('/server_monitor', function (req, res) {
  //console.log('Action:　server_monitor');

  logicCommon.querySDKMonitorDot(function (dotData) {
    var resDotData = verifyParam(dotData);
    retf(resDotData);
  });

  function verifyParam(dotData) {
    //该部分约定见协议文档说明
    var org = {
      "Login_1": 0,
      "Login_2": 0,
      "Login_3": 0,
      "Login_3_1": 0,
      "Login_e": 0,
      "Pay_1": 0,
      "Pay_2": 0,
      "Pay_3": 0,
      "Pay_3_1": 0,
      "Pay_4": 0,
      "Pay_5": 0,
      "Pay_e": 0,
      "Other_1": 0,
      "Other_2": 0,
      "Other_3": 0,
      "Other_4": 0,
      "Other_5": 0,
      "Other_6": 0,
      "Other_7": 0,
      "Other_e": 0
    };

    var cloned = merge(true, org);
    merge(cloned, dotData);

    var resDotData = '';
    for (var i in cloned) {
      if (0 == (cloned[i] + "").replace(/(^s*)|(s*$)/g, "").length) {
        return false;
      }
      resDotData += i + ':' + cloned[i] + ' ';
    }
    resDotData = resDotData.substr(0, resDotData.length - 1);
    return resDotData;
  }

  function retf(ret) {
    //console.log('Response: ' + JSON.stringify(ret) + '\n');

    if (typeof ret.code == 'undefined' && typeof ret.ret == 'undefined')
      res.end(ret);
    else
      res.json(ret);
  }
});

router.post('/server_monitor', function (req, res) {
  res.end('WARNING：This interface applies only to GET request mode！');
});

module.exports = router;