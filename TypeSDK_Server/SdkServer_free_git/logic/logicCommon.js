/**
 * Created by TypeSDK 2016/11/16
 */
var redisDA = require("./dbRedis.js");
var crypto = require('crypto');
var logicOrderKey = "Order";
var channelClientId = "ClientId";
var Sync = require('./sync.js');
var request = require('request');
var config = require('../config');
var mysqlDA = require('../logic/dataAccess.js');
var logicRedis = require("./logicRedis.js");


function saveOrder(game, channel, cporder, notifyurl, data, ret, retf, verifyurl, channelId) {
  redisDA.select('0', function (error) {
    console.log(error);
    if (error) {
      console.log(error);

      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";

      retf(ret);
    } else {
      // set
      var savedata =
          {
            "game": game
            , "channel": channel
            , "orderdate": Date.now()
            , "orderstatus": 0
            , "orderdata": data
            , "cporder": cporder
            , "notifyurl": notifyurl
            , "verifyurl": verifyurl
          };
      redisDA.set(logicOrderKey + ':' + cporder, JSON.stringify(savedata), function (error, res) {
        console.log(error);
        if (error) {
          console.log(error);

          ret.code = 1;
          ret.msg = "SAVE ERROR";
          ret.value = "";

        } else {
          console.log(res);

          ret.code = 0;
          ret.msg = "NORMAL";
          ret.value = savedata;
        }
        // 关闭链接
        //client.end();

        retf(ret);
      });

      if (data.goodsinfo != undefined && data.uid != undefined)
      {
        var uid = data.uid;
         var price = data.goodsinfo.price;
         var name = data.goodsinfo.name;
      }
      else
      {
        //需要游戏服务器传递
          var uid = '000';
          var price = '000';
          var name = '000';
      }

      redisDA.expire(logicOrderKey + ':' + cporder, 3 * 24 * 60 * 60);
      mysqlDA.createOrder(game, channel, cporder, verifyurl, channelId, notifyurl,uid,price,name);
    }
  });
}

function updataOrder(cporder, amount) {
  var ret = {};
  redisDA.select('0', function (error) {
    if (error) {
      console.log(error);

      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";
      console.log(ret);

    } else {
      redisDA.get(logicOrderKey + ':' + cporder, function (error, res) {
        if (error) {
          console.log(error);
          ret.code = 2;
          ret.msg = "CHECK ERROR";
          ret.value = "";
          console.log(ret);
        } else {
          if (res == null) {
            ret.code = 1;
            ret.msg = "NO RECORD";
            ret.value = "";
            console.log(ret);
          } else {
            var data = JSON.parse(res);
            data.amount = amount;
            redisDA.set(logicOrderKey + ':' + cporder, JSON.stringify(data), function (error, res) {
              console.log(error);
              console.log(JSON.stringify(res));
            });
          }
        }
      });
    }
  });
}

function checkOrder(cporder, ret, retf) {
  redisDA.select('0', function (error) {
    if (error) {
      console.log(error);

      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";

      retf(ret);
    } else {
      redisDA.get(logicOrderKey + ':' + cporder, function (error, res) {
        if (error) {
          console.log(error);

          ret.code = 2;
          ret.msg = "CHECK ERROR";
          ret.value = "";
        } else {
          console.log(res);
          if (res == null) {
            ret.code = 1;
            ret.msg = "NO RECORD";
            ret.value = "";
          } else {
            ret.code = 0;
            ret.msg = "NORMAL";
            ret.value = JSON.parse(res);
          }
        }

        // 关闭链接
        //client.end();

        retf(ret);
      });
    }
  });
}

function getNotifyUrl(cporder, params, callback) {
  redisDA.select('0', function (error) {
    if (error) {
      callback(false);
    } else {
      redisDA.get(logicOrderKey + ':' + cporder, function (error, res) {
        if (error) {
          callback(false);
        } else {
          if (res == null) {
            callback(false);
          } else {
            var data = JSON.parse(res);
            params.out_url = data.notifyurl;
            params.verifyurl = data.verifyurl;
            params.orderdata = data.orderdata.cporder;
            if (data.orderdata.goodsinfo != undefined)
            {
                params.goodsname = data.orderdata.goodsinfo.name;
                params.goodsprice = data.orderdata.goodsinfo.price;
            }
            else
            {
                params.goodsname = '2.2之前版本没有这个参数';
                params.goodsprice = '2.2之前版本没有这个参数';
            }
            params.goodsid = data.orderdata.data;
            callback(true);
          }
        }
      });
    }
  });
}

function createSignPay(query, key) {
  return crypto.createHash('md5').update(
      query.code + '|' +
      query.id + '|' +
      query.order + '|' +
      query.cporder + '|' +
      query.info + '|' +
      key
      , 'utf8').digest('hex');
}

function createSign(query, key) {
  var str = '';
  for (var i in query) {
    str += query[i] + '|'
  }
  return crypto.createHash('md5').update(str + key, 'utf8').digest('hex');
}

function UpdateOrderStatus(game, channel, cporder, chorder, status, amount, retdata) {
  mysqlDA.UpdateOrderStatus(game, channel, cporder, chorder, status, amount, retdata);
}

function asGameSearch(cporder, userId, ret, retf) {
  mysqlDA.asGameSearch(cporder, userId, ret, retf);
}

function checkVersion(version, attrs, ret, retf, channelupdateswitch) {
  if (channelupdateswitch) {
    //约定：0 不强更，1 强更
    if (!attrs.force
        || attrs.force.length == 0) {
      ret.code = 0;
      ret.msg = "NO UPDATE";
    } else if (attrs.force == 1 && cutVersion(version) != cutVersion(attrs.gameVerMax)) {
      ret.code = 1;
      ret.ClientMD5 = attrs.ClientMD5;
      ret.ClientURL = attrs.ClientURL;
      if (!attrs.updateURL || attrs.updateURL.lenght == 0) {
        ret.updateURL = '';
      } else {
        ret.updateURL = attrs.updateURL + 'patch_' + attrs.gameVerMax + '.apk';
      }
      ret.msg = "ok";
      ret.force = "1";
    } else {
      if (!attrs.gameVerMin
          || attrs.gameVerMin.length == 0
          || !attrs.gameVerMax
          || attrs.gameVerMax.length == 0
          || !attrs.updateURL
          || attrs.updateURL.length == 0
          || cutVersion(version) == cutVersion(attrs.gameVerMax)) {
        ret.code = 0;
        ret.msg = "NO UPDATE";
      } else if (cutVersion(version) >= cutVersion(attrs.gameVerMin) && cutVersion(version) < cutVersion(attrs.gameVerMax)) {
        ret.code = 1;
        ret.ClientMD5 = attrs.ClientMD5;
        ret.ClientURL = attrs.ClientURL;
        if (!attrs.updateURL || attrs.updateURL.lenght == 0) {
          ret.updateURL = '';
        } else {
          ret.updateURL = attrs.updateURL + 'patch_' + attrs.gameVerMax + '.apk';
        }
        ret.msg = "ok";
        ret.force = "0";
      }
      else {
        ret.code = 1;
        ret.ClientMD5 = attrs.ClientMD5;
        ret.ClientURL = attrs.ClientURL;
        if (!attrs.updateURL || attrs.updateURL.lenght == 0) {
          ret.updateURL = '';
        } else {
          ret.updateURL = attrs.updateURL + 'patch_' + attrs.gameVerMax + '.apk';
        }
        ret.msg = "ok";
        ret.force = "1";
      }
    }
  } else {
    ret.code = 0;
    ret.msg = "NO UPDATE";
  }

  retf(ret);

  return true;
}

function searchOrder(cporder, ret, reft) {
  mysqlDA.searchOrder(cporder, ret, reft);
}

function selectAllOrder(game, channel, retf) {
  mysqlDA.selectAllOrder(game, channel, retf);
}

function getItemList(Redis, gameId, channelId, ret, cb) {
  logicRedis.getItemList(Redis, gameId, channelId, ret, cb);
}

function setItemList(Redis, gameId, channelId, item, ret, cb) {
  logicRedis.setItemList(Redis, gameId, channelId, item, ret, cb);
}

function SaveOrderRedis(query, ret, retf) {
  logicRedis.createOrUpdateOrder(query, ret, retf);
}

function payBudan(attrs, gattrs, params, query, ret, game, channel, channelId, retf) {

  var gameData = {};

  getNotifyUrlfromMsql(query.cporder, query, function (hasData) {
    if (!hasData) {
      retf('FAILURE');
      return;
    } else {
      var functions = {
        'gameSearch': function (next) {
          var retValue = {};
          retValue.code = '0';
          retValue.id = query.id;
          retValue.order = query.order;
          retValue.cporder = query.cporder;
          retValue.info = '';
          retValue.sign = createSign(retValue, gattrs.gkey);

          var options = {
            url: query.verifyurl,
            method: "POST",
            body: retValue,
            json: true
          };

          request(options, function (error, response, body) {

            if (!error && response.statusCode == 200) {
              var retOut = body;
              if (typeof retOut.code == 'undefined') {
                ret.msg = 'FAILURE';
                retf(ret);
                return;
              }
              if (retOut.code == 0) {
                gameData = retOut;
                next('sdkSearch');
                return;

              } else {
                ret.msg = retOut.msg;
                retf(ret);
              }
            } else {
              ret.msg = error;
              retf(ret);
            }
          });
        },
        'sdkSearch': function (next) {
          mysqlDA.searchOrder(gameData.cporder, ret, function (res) {
            if (res.code == '0') {
              var status = res.status;
              if (status >= 0) {
                if (gameData.cporder == query.cporder && gameData.amount == query.amount) {
                  if (status == '0') {
                    UpdateOrderStatus(game, channel, query.cporder, query.order, 2);
                  }
                  //满足自动补单、跟手动补单的情况
                  if ((gameData.status == '0' && status == '0')
                      || (gameData.status == '1' && (status == '0' || status == '1' || status == '2' || status == '4'))
                      || (gameData.status == '3' && (status == '2' || status == '4'))) {
                    next('callGamePay');
                    return;
                  } else {
                    ret.code = 0;
                    ret.msg = 'NORMAL';
                    retf(ret);
                    return;
                  }
                } else {
                  UpdateOrderStatus(game, channel, gameData.cporder, gameData.order, 3);
                  ret.code = '0';
                  ret.msg = 'NORMAL';
                  retf(ret);
                  return;
                }
              } else if (status == 'unknown') {
                if (gameData.status == '0' || gameData.status == '1') {
                  next('callGamePay');
                  return;
                } else {
                  ret.msg = 'NORMAL';
                  ret.code = 0;
                  retf(ret);
                }
              } else {
                ret.msg = '数据有误请重试！';
                ret.code = -1;
                retf(ret);
              }
            } else {
              retf(res);
            }
          });
        },
        'callGamePay': function (next) {
          var retValue = {};
          retValue.code = 0;
          retValue.id = gameData.id;
          retValue.order = gameData.order;
          retValue.cporder = gameData.cporder;
          retValue.info = '';

          retValue.sign = createSignPay(retValue, gattrs.gkey);

          retValue.gamename = game;
          retValue.sdkname = channel;
          retValue.channel_id = channelId;
          retValue.amount = '' + gameData.amount + '';

          var options = {
            url: query.out_url,
            method: "POST",
            body: retValue,
            json: true
          };
          request(options, function (error, response, body) {
            if (!error && response.statusCode == 200) {
              var retOut = body;
              //日志记录CP端返回
              console.log(retOut);
              if (typeof retOut.code == 'undefined') {
                ret.msg = 'FAILURE';
                retf(ret);
                return;
              }
              if (retOut.code == 0) {
                UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 102, retOut.res);
                ret.code = 0;
                ret.msg = 'NORMAL';
                retf(ret);
                return;
              } else {
                ret.code = -1;
                ret.msg = retOut.msg;
                retf(ret);
              }
            } else {
              ret.code = -1;
              ret.msg = error;
              retf(ret);
            }
          });
        }
      };
      var sync = new Sync(functions);
      sync.runbyMap('gameSearch');
    }
  });
}

function getNotifyUrlfromMsql(cporder, query, callback) {
  var ret = {code: '-99999', msg: 'unknown err'};
  mysqlDA.searchOrder(cporder, ret, function (res) {
    if (res.code == '0') {
      var data = res;
      if (data.notifyurl && data.notifyurl != 'unknown' && data.verifyurl && data.verifyurl != 'unknown') {
        ret.code = '0';
        ret.msg = 'NORMAL';
        query.out_url = data.notifyurl;
        query.verifyurl = data.verifyurl;
      } else {
        ret.code = '0';
        ret.msg = 'notifyurl==null或verifyurl==null';
      }
    } else {
      ret.code = res.code;
      ret.msg = res.msg;
    }
    callback(ret);
  })
}

function payBuchang(game, channel, query, ret, retf) {

  var functions = {
    "searchOrder": function () {
      mysqlDA.searchOrder(cporder, ret, function (obj) {
        if (obj.code == '0') {
          var data = obj.data;
          if (data.length == 0) {
            //创建数据
            next('createOrder');
            return;
          } else {
            // 更新数据
            next('updateOrder');
            return;
          }
        } else {
          retf(ret);
          return;
        }
      });
    },
    "createOrder": function (next) {
      saveOrder(game, channel, query.cporder, query.notifyurl, query.data, ret, function (obj) {
        if (obj.code == '0') {
          UpdateOrderStatus(game, channel, query.cporder, query.order, 4);
          ret.code = '0';
          ret.msg = 'NORMAL';
          ret.data = '';
          retf(ret);
          return;
        } else {
          retf(ret);
          return;
        }
      }, query.verifyurl);
    },
    "updateOrder": function (next) {
      updateOrder(game, channel, query.cporder, query.notifyurl, query.data, ret, query.verifyurl, function (obj) {
        if (obj.code == '0') {
          UpdateOrderStatus(game, channel, query.cporder, query.order, 4);
          ret.code = '0';
          ret.msg = 'NORMAL';
          ret.data = '';
          retf(ret);
          return;
        } else {
          retf(ret);
        }
      });
    }
  };

  var sync = new Sync(functions);
  sync.runbyMap('searchOrder');
}

function updateOrder(game, channel, cporder, notifyurl, data, ret, verifyurl, retf) {
  var sdkorder = crypto.randomBytes(20).toString('hex');
  redisDA.select('0', function (error) {
    if (error) {
      console.log(error);

      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";

      retf(ret);
    } else {
      // set

      var savedata =
          {
            "game": game
            , "channel": channel
            , "orderdate": Date.now()
            , "orderstatus": 0
            , "orderdata": data
            , "cporder": cporder
            , "notifyurl": notifyurl
            , "sdkorder": sdkorder
            , "verifyurl": verifyurl
          };
      //redisDA.set(logicOrderKey +  ':' + game + ':' + channel + ':' + cporder, JSON.stringify(data), function(error, res) {
      redisDA.set(logicOrderKey + ':' + cporder, JSON.stringify(savedata), function (error, res) {
        if (error) {
          console.log(error);

          ret.code = 1;
          ret.msg = "SAVE ERROR";
          ret.value = "";

        } else {
          console.log(res);

          ret.code = 0;
          ret.msg = "NORMAL";
          ret.value = savedata;
        }
        retf(ret);
        client.end();
      });
    }
  });
}

function utf16to8(str) {
  var out, i, len, c;

  out = "";
  len = str.length;
  for (i = 0; i < len; i++) {
    c = str.charCodeAt(i);
    if ((c >= 0x0001) && (c <= 0x007F)) {
      out += str.charAt(i);
    } else if (c > 0x07FF) {
      out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
      out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
      out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
    } else {
      out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
      out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
    }
  }
  return out;
}

function utf8to16(str) {
  var out, i, len, c;
  var char2, char3;

  out = "";
  len = str.length;
  i = 0;
  while (i < len) {
    c = str.charCodeAt(i++);
    switch (c >> 4) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
        // 0xxxxxxx
        out += str.charAt(i - 1);
        break;
      case 12:
      case 13:
        // 110x xxxx   10xx xxxx
        char2 = str.charCodeAt(i++);
        out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
        break;
      case 14:
        // 1110 xxxx  10xx xxxx  10xx xxxx
        char2 = str.charCodeAt(i++);
        char3 = str.charCodeAt(i++);
        out += String.fromCharCode(((c & 0x0F) << 12) |
            ((char2 & 0x3F) << 6) |
            ((char3 & 0x3F) << 0));
        break;
    }
  }

  return out;
}

function sortObject(object) {
  var sortedObj = {},
      keys = Object.keys(object);

  keys.sort(function (key1, key2) {
    key1 = key1.toLowerCase(), key2 = key2.toLowerCase();
    if (key1 < key2) return -1;
    if (key1 > key2) return 1;
    return 0;
  });

  for (var index in keys) {
    var key = keys[index];
    if (typeof object[key] == 'object' && !(object[key] instanceof Array)) {
      sortedObj[key] = sortObject(object[key]);
    } else {
      if (object[key] != '' && object[key] != null) {
        sortedObj[key] = object[key];
      }
    }
  }

  return sortedObj;
}

function cutVersion(version) {
  var ver = version.split(".");
  return ver[ver.length - 1];
}

//返回每64为一个字符串的数组
function returnBase64Array(publicKey) {
  var array = publicKey.match(/[\s\S]{64}/g);
  if (array && array.length) {
    var lastLength = array.length * 64;
    var lastStr = publicKey.substring(lastLength);
    if (lastStr && lastStr.length) {
      array.push(lastStr);
    }
  } else {
    array = [];
    array.push(publicKey);
  }
  return array;
}

function callGamePay(attrs, gattrs, params, query, ret, retf, game, channel, channelId) {
  var retValue = {};
  retValue.code = 0;
  retValue.id = query.userId;
  retValue.order = query.chorder;
  retValue.cporder = query.cporder;
  retValue.info = '';

  retValue.gamename = game;
  retValue.sdkname = channel;
  retValue.channel_id = channelId;
  retValue.amount = '' + query.amount + ''; //单位未定

  var options = {
    url: query.notifyurl,
    method: "POST",
    body: retValue,
    json: true
  };
  logicCommon.UpdateOrderStatus(game, channel, query.cporder, query.order, 1);

  request(options, function (error, response, body) {

    if (!error && response.statusCode == 200) {
      var retOut = body;
      //日志记录CP端返回
      console.log(retOut);
      if (typeof retOut.code == 'undefined') {
        ret.code = -1;
        ret.msg = 'FAILURE';
        retf(ret);
        return;
      }

      if (retOut.code == 0) {
        logicCommon.UpdateOrderStatus(game, channel, retValue.cporder, retValue.order, 2);
        ret.code = 0;
        ret.msg = 'NORMAL';
        retf(ret);
        //retf('FAILURE');
      } else {
        ret.code = -2;
        ret.msg = '游戏服验证失败';
        retf(ret);
      }
    } else {
      ret.code = -1;
      ret.msg = error;
      retf(ret);
    }
  });
}

/**
 * @param
 *      dotType 打点类型
 * */
function sdkMonitorDot(dotType) {
  var dotName = config.dotConf.sdkMonitorName || 'SDK_MONITOR',
      incrValue = config.dotConf.incrementValue || 1;

  try {
    redisDA.select('0', function (error) {
      if (error) {
        console.log('MonitorError: Redis服务连接失败，请联系运维检查相关配置！');
        return;
      } else {
        redisDA.hincrby(dotName, dotType, incrValue, function (err, res) {
          if (err) {
            console.log('MonitorError: \n' + err);
            return;
          }
          redisDA.expire(dotName, 7 * 24 * 60 * 60); //默认一周

          return;
        });
      }
    });
  } catch (e) {
    console.log('MonitorError: 打点错误!\nMonitorErrorDetails: ' + e);
    return;
  }
}

/**
 * @param
 *      retf    返回打点记录
 * */
function querySDKMonitorDot(retf) {
  var dotName = config.dotConf.sdkMonitorName || 'SDK_MONITOR';

  try {
    redisDA.select('0', function (error) {
      if (error) {
        console.log('MonitorError: Redis服务连接失败，请联系运维检查相关配置！');
        retf('');
        return;
      } else {
        //是否存在Hash表
        redisDA.exists(dotName, function (err, res) {
          if (err) {
            console.log('MonitorError: \n' + err);
            retf('');
            return;
          }

          if (res == 0) {
            console.log('MonitorError: 不巧……打点记录刚刚过期自动清理了！');
            retf(false);
          } else {
            redisDA.hgetall(dotName, function (err, res) {
              if (err) {
                console.log('MonitorError: \n' + err);
                retf('');
                return;
              }

              if (res) {
                retf(res);
                return;
              } else {
                console.log('MonitorError: \n' + err);
                retf('');
                return;
              }
            });
          }
        });
      }
    });
  } catch (e) {
    console.log('MonitorError: 打点记录查询错误!\nMonitorErrorDetails: ' + e);
    retf(false);
    return;
  }
}

/**
 * 保存Token到Redis,并返回保存成功与失败
 *      哈希表，格式 SDKTOKEN:{channelId}:{md5(tokenStr)} status number token tokenStr
 *      status      0 未验证过；1 验证过；2 请求超时；Other ……
 * @param
 *      channelId   所要保存Token的渠道ID
 *      tokenStr    所要保存的Token字符串
 *      status      该Token的状态
 *      retf        保存订单时会核查此笔 True保存成功 | False 保存失败
 * */
function saveTokenInRedis(channelId, tokenStr, status, retf) {
  if (!tokenStr) {
    console.log('SDKError: 保存Token失败!');
    retf(false);
    return;
  }

  channelId = channelId ? channelId : '101';
  var tokenStrMd5 = crypto.createHash('md5').update(tokenStr).digest('hex').toUpperCase();

  try {
    redisDA.select('0', function (error) {
      if (error) {
        console.log('SDKError: Redis服务连接失败，请联系运维检查相关配置！');
        reft(false);
        return;
      } else {
        redisDA.hmset('SDKTOKEN:' + channelId + ':' + tokenStrMd5, 'status', status, 'token', tokenStr, function (err, res) {
          if (err) {
            console.log('SDKError: 保存Token失败!\n' + err);
            retf(false);
            return;
          }

          retf(true);
          return;
        });
      }
    });
  } catch (e) {
    console.log('SDKError: 保存Token失败!\n' + e);
    retf(false);
    return;
  }
}

/**
 * 核查本Token是否存在Redis中，并核实该Token的状态
 *      订单状态    0 未验证过；1 验证过；2 请求超时；Other ……
 * @param
 *      channelId   对应以保存Token的渠道ID
 *      tokenStr    所要核查的Token字符串
 *      retf        返回查询结果 false 存在且状态合法|true 不存在或存在但状态为超时
 * */
function selTokenInRedis(channelId, tokenStr, retf) {
  channelId = channelId ? channelId : '101';
  var tokenStrMd5 = crypto.createHash('md5').update(tokenStr).digest('hex').toUpperCase();

  var isExist = false;
  if (!tokenStr) {
    console.log('SDKError: 查询Token失败!');
    retf(isExist);
    return;
  } else {
    try {
      redisDA.select('0', function (err) {
        if (err) {
          console.log('SDKError: ' + err);
          retf(isExist);
          return;
        } else {
          redisDA.hgetall('SDKTOKEN:' + channelId + ':' + tokenStrMd5, function (err, data) {
            if (err) {
              console.log('SDKError: ' + err);
            } else {
              if (data == null) {
                isExist = true;
              } else if (data.status == 1) {
                isExist = false;
              } else {
                isExist = true;
              }
            }
            retf(isExist);
            return;
          });
        }
      });
    } catch (e) {
      console.log('SDKError: 查询Token失败!\n' + e);
      retf(isExist);
      return;
    }
  }
}

/**
 * 保存IOS内购流水号【此方法，到SDKFrame3.0 时希望合并】
 * @param
 *      retf        返回保存结果 True 保存成功|False 保存失败
 * */
function saveIAPOrderInRedis(game, channel, cporder, chorder, retf) {
  var isExist = false;
  try {
    redisDA.select('0', function (err) {
      if (err) {
        console.log('SDKError: ' + err);
        retf(isExist);
        return;
      }

      var savedata =
          {
            "game": game
            , "channel": channel
            , "orderdate": Date.now()
            , "chorder": chorder
            , "cporder": cporder
          };

      redisDA.setnx(logicOrderKey + ':IAPOrder:' + chorder, JSON.stringify(savedata), function (err, res) {
        if (err) {
          console.log('SDKError: ' + err);
          retf(isExist);
          return;
        }

        if (res == 0) {
          console.log('SDKError: 保存IAPOrder 失败！\n' + err);
          retf(isExist);
          return;
        }

        isExist = true;
        retf(isExist);
        return;
      });
    });
  } catch (e) {
    console.log('SDKError: 保存IAPOrder 失败!\n' + e);
    retf(isExist);
    return;
  }
}

/**
 * 查询IOS内购流水号【此方法，到SDKFrame3.0 时希望合并】
 * @param
 *      order     IAP流水号
 *      retf      返回查询结果 True 存在|False 不存在
 * */
function selIAPOrderInRedis(order, retf) {
  var isExist = false;
  if (!order) {
    console.log('SDKError: 查询IAPOrder失败!');
    retf(isExist);
    return;
  } else {
    try {
      redisDA.select('0', function (err) {
        if (err) {
          console.log('SDKError: ' + err);
          retf(isExist);
          return;
        } else {
          redisDA.exists(logicOrderKey + ':IAPOrder:' + order, function (err, data) {
            if (err) {
              console.log('SDKError: ' + err);
            } else {
              try {
                if (data == 1) {
                  isExist = true;
                }
              } catch (e) {
                console.log('SDKError: ' + e);
              }
            }
            retf(isExist);
            return;
          })
        }
      });
    } catch (e) {
      console.log('SDKError: 查询IAPOrder失败!\n' + e);
      retf(isExist);
      return;
    }
  }
}

/**
 * 保存商品的外部订单号
 * @param
 *      retf        返回保存结果 True 保存成功|False 保存失败
 * */
function saveCHOrderInRedis(game, channel, cporder, chorder, retf) {
  var isExist = false;
  try {
    redisDA.select('0', function (err) {
      if (err) {
        console.log('SDKError: ' + err);
        retf(isExist);
        return;
      }

      var savedata =
          {
            "game": game
            , "channel": channel
            , "orderdate": Date.now()
            , "chorder": chorder
            , "cporder": cporder
          };

      redisDA.setnx(logicOrderKey + ':CHOrder:' + channel + ':' + chorder, JSON.stringify(savedata), function (err, res) {
        if (err) {
          console.log('SDKError: ' + err);
          retf(isExist);
          return;
        }

        if (res == 0) {
          console.log('SDKError: 保存CHOrder 失败！\n' + err);
          retf(isExist);
          return;
        }

        isExist = true;
        retf(isExist);
        return;
      });
    });
  } catch (e) {
    console.log('SDKError: 保存CHOrder 失败!\n' + e);
    retf(isExist);
    return;
  }
}

/**
 * 查询商品的外部订单号
 * @param
 *      order     外部订单号
 *      retf      返回查询结果 True 存在|False 不存在
 * */
function selCHOrderInRedis(channel, order, retf) {
  var isExist = false;
  if (!order) {
    console.log('SDKError: 查询CHOrder失败!');
    retf(isExist);
    return;
  } else {
    try {
      redisDA.select('0', function (err) {
        if (err) {
          console.log('SDKError: ' + err);
          retf(isExist);
          return;
        } else {
          redisDA.exists(logicOrderKey + ':CHOrder:' + channel + ':' + order, function (err, data) {
            if (err) {
              console.log('SDKError: ' + err);
            } else {
              try {
                if (data == 1) {
                  isExist = true;
                }
              } catch (e) {
                console.log('SDKError: ' + e);
              }
            }
            retf(isExist);
            return;
          })
        }
      });
    } catch (e) {
      console.log('SDKError: 查询CHOrder失败!\n' + e);
      retf(isExist);
      return;
    }
  }
}

function mapItemLists(attrs, retOut) {
  var itemLists = attrs.itemLists;
  if (itemLists) {
    for (var i = 0; i < itemLists.length; i++) {
      if (itemLists[i].itemcpid == retOut.Itemid) {
        retOut.amount = itemLists[i].price * 100;
        break;
      }
    }
  }
}

/**
 * 获取serverid与对应的zoneid
 *
 * @param attrs
 * @param selectedId
 * @returns {*}
 */
function selectedServerMap(attrs, selectedId) {
  var serverList = attrs.serverList;
  var serverData = null;
  for (var index = 0, leng = serverList.length; index < leng; index++) {
    if (serverList[index].serverid == selectedId) {
      serverData = serverList[index];
      break;
    }
  }

  return serverData;
}

/**
 * 保存用户的相关信息
 * */
function saveUserInfo(data, clientId, retf) {
  var ret = {};
  redisDA.select('0', function (error) {
    if (error) {
      console.log(error);

      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";

      console.log(ret);
    } else {
      var dataInfo = data;
      redisDA.set(channelClientId + ':' + clientId, JSON.stringify(dataInfo), function (error, res) {
        if (error) {
          console.log(error);

          ret.code = 1;
          ret.msg = "SAVE ERROR";
          ret.value = "";
        } else {
          console.log(res);
          ret.code = 0;
          ret.msg = "NORMAL";
          ret.value = dataInfo;
        }
        retf(ret);
      });
    }
  });
}

/**
 * 获取用户的相关信息
 * */
function getUserInfo(clientId, retf) {
  var ret = {};
  redisDA.select('0', function (error) {
    if (error) {
      console.log("SDKError: " + json.stringify(error));
      ret.code = -1;
      ret.msg = "CONNECTION ERROR";
      ret.value = "";
      retf(ret);
      return;
    } else {
      redisDA.get(channelClientId + ':' + clientId, function (error, res) {
        if (error) {
          console.log("SDKError: " + json.stringify(error));
          ret.code = 1;
          ret.msg = "SAVE ERROR";
          ret.value = "";
        } else {
          ret.code = 0;
          ret.msg = "NORMAL";
          ret.value = res;
        }
        retf(ret);
      });
    }
  });
}

/**
 * 获取现在的时间并格式化
 * @param
 *      a  标准时间格式 2015-10-10 10:10:10
 *      b  14位时间格式 20151010101010
 *      c  其他
 * */
function getNowFormatDate(param) {
  var date = new Date();
  var seperator1 = "-";
  var seperator2 = " ";
  var seperator3 = ":";
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  var day = date.getDate();
  var hour = date.getHours();
  var minutes = date.getMinutes();
  var seconds = date.getSeconds();
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }
  if (day >= 0 && day <= 9) {
    day = "0" + day;
  }
  if (hour >= 0 && hour <= 9) {
    hour = "0" + hour;
  }
  if (minutes >= 0 && minutes <= 9) {
    minutes = "0" + minutes;
  }
  if (seconds >= 0 && seconds <= 9) {
    seconds = "0" + seconds;
  }

  var formatDate = "";
  switch (param) {
    case 'a':
      formatDate = year + seperator1 + month + seperator1 + day + seperator2 + hour + seperator3 + minutes + seperator3 + seconds;
      break;
    case 'b':
      formatDate = year + month + day + hour + minutes + seconds;
      break;
  }
  return formatDate;
}

/**
 * 配置时间间隔自动回调
 * @param
 *      channel 渠道号
 *      options 请求串
 *      cb 成功SUCCESS | 失败FAILURE
 * */
function autoCallBack(channel, options, cb) {
  //回调时间间隔（单位：ms）
  var timeTip = 1000 * 20;//60 * 60 * 10;

  //最大回调循环次数
  var countNum = 5;

  //当前循环回调次数
  var count = 1;

  var timer = setInterval(function () {
    console.log('Auto-CallBack-Ing [ Channel: ' + channel + ',Count: ' + count + ']');

    request(options, {timeout: 1000 * 10}, function (err, response, body) {
      console.log('CB-Error: ' + JSON.stringify(err));
      console.log('CB-Body: ' + JSON.stringify(body));

      if (!err && response.statusCode == 200) {
        var retOut = body;
        if (typeof retOut.code == 'undefined') {
          if (count >= countNum) {
            clearInterval(timer);
            count = 0;
          }

          count++;
          cb('FAILURE');
        }

        if (retOut.code == '0') {
          clearInterval(timer);
          count = 0;

          cb('SUCCESS');
        }
        else {
          if (count >= countNum) {
            clearInterval(timer);
            count = 0;
          }

          count++;
          cb('FAILURE');
        }
      } else {
        if (count >= countNum) {
          clearInterval(timer);
          count = 0;
        }

        count++;
        cb('FAILURE');
      }
    });
  }, timeTip);
}


function createLoginLog(game, channelid, channelname, channeluserid) {
    mysqlDA.createLoginLog(game, channelid, channelname, channeluserid);
}


// function veriorder(cporder,params,callback) {
//     redisDA.select('0', function (error) {
//         if (error) {
//             callback(false);
//         } else {
//             redisDA.get(logicOrderKey + ':' + cporder, function (error, res) {
//                 if (error) {
//                     callback(false);
//                 } else {
//                     if (res == null) {
//                         callback(false);
//                     } else {
//                         var order = JSON.parse(res);
//                         params.orderdata = order.orderdata.cporder;
//                         params.goodsname = order.orderdata.goodsinfo.name;
//                         params.goodsprice = order.orderdata.goodsinfo.price;
//                         callback(params);
//                     }
//                 }
//             });
//         }
//     });
//
// }

exports.dotType = {
  LoginDot: {
    GetReqFromGClient: 'Login_1',   //接收到客户端登录请求
    RelaySDKVerify: 'Login_2',      //- 转发SDK服务器验证
    ChVerifySuc: 'Login_3',         //- 验证成功
    ChVerifyErr: 'Login_3_1',       //- 验证失败
    Error: 'Login_e'                //其他登录失败
  },
  PayDot: {
    GetCBPay: 'Pay_1',             //接收到渠道支付回调
    GetCPOrderFromCP: 'Pay_2',     //服务器返回订单号
    CompareOrderSuc: 'Pay_3',      //支付订单对比成功
    CompareOrderErr: 'Pay_3_1',    //支付订单对比失败
    PayNotice: 'Pay_4',            //支付回调通知
    PaySuc: 'Pay_5',               //- 服务器正确处理支付成功回调
    Error: 'Pay_e'                 //- 其他支付失败
  },
  OtherDot: {
    HttpGetReq: 'Other_1',         //接收到的HttpGet请求总数
    HttpPostReq: 'Other_2',        //接收到的HttpPost请求总数
    HttpGetRes: 'Other_3',         //返回HttpGet响应总数
    HttpPostRes: 'Other_4',        //返回HttpPost响应总数
    DoAction: 'Other_5',           //合法的Http请求总数
    ParamErr: 'Other_6',           //请求参数错误
    SignErr: 'Other_7',            //通用请求算签
    Error: 'Other_e'               //其他错误
  }
};
exports.getNotifyUrl = getNotifyUrl;
exports.checkOrder = checkOrder;
exports.saveOrder = saveOrder;
exports.checkVersion = checkVersion;
exports.createSignPay = createSignPay;
exports.UpdateOrderStatus = UpdateOrderStatus;
exports.asGameSearch = asGameSearch;
exports.sortObject = sortObject;

exports.utf8to16 = utf8to16;
exports.utf16to8 = utf16to8;

exports.returnBase64Array = returnBase64Array;
exports.callGamePay = callGamePay;
exports.searchOrder = searchOrder;
exports.selectAllOrder = selectAllOrder;

exports.payBudan = payBudan;
exports.payBuchang = payBuchang;

exports.sdkMonitorDot = sdkMonitorDot;
exports.querySDKMonitorDot = querySDKMonitorDot;

exports.saveTokenInRedis = saveTokenInRedis;
exports.selTokenInRedis = selTokenInRedis;
exports.saveIAPOrderInRedis = saveIAPOrderInRedis;
exports.selIAPOrderInRedis = selIAPOrderInRedis;
exports.saveCHOrderInRedis = saveCHOrderInRedis;
exports.selCHOrderInRedis = selCHOrderInRedis;

exports.mapItemLists = mapItemLists;
exports.selectedServerMap = selectedServerMap;
exports.saveUserInfo = saveUserInfo;
exports.getUserInfo = getUserInfo;
exports.getNowFormatDate = getNowFormatDate;
exports.autoCallBack = autoCallBack;
exports.updataOrder = updataOrder;

exports.SaveOrderRedis = SaveOrderRedis;
exports.getItemList = getItemList;
exports.setItemList = setItemList;

exports.createLoginLog = createLoginLog;