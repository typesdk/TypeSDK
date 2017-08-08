/**
 * Created by Wans on 2016/8/16.
 */
var debug = require("debug")("Router_Game"),
    config = require('config-lite'),
    uid = require('uid2'),
    request = require('request');
var querystring = require("querystring"),
    crypto = require('crypto');
var redisClient = require('../src/redisDA');
var express = require('express');
var router = express.Router();
var debug_log = require('../src/logger.js').loggerf('debug_log');

router.get('/', function(req, res, next) {
  debug("GET /");

  res.send('NORMAL');
});

router.get('/login', function(req, res, next) {
  debug("GET /login");
  debug("PARAM : ", req.query);
  debug_log.info("GET /login");
  debug_log.info("PARAM : ", req.query);

  var ret = {
    code: -99,
    msg : 'LOGIN ERROR - UNKNOWN ERROR'
  };
  //req.query = JSON.parse(req.query.data);

  querystring.parse(req.query);


  var param_id = querystring.unescape(req.query.id ? req.query.id : '');
  var param_token = querystring.unescape(req.query.token ? req.query.token : '');
  var param_data = querystring.unescape(req.query.data ? req.query.data : '');
  var param_cid = querystring.unescape(req.query.cid ? req.query.cid : '');

  var reqbody = {
    id : param_id,
    token : param_token,
    data : param_data
  };

  var str = reqbody.id + '|' + reqbody.token + '|'+ reqbody.data + '|' + config.appkey;
  debug("STR : ", str);
  reqbody.sign = crypto.createHash('md5').update(str).digest('hex');

  var options = {
    url: config.sdkaddress + '/' + config.appid + '/' + param_cid + '/Login/' ,
    method: 'POST',
    body: reqbody,
    json: true
  };

  debug('REQUST OPTIONS : ',options);

  request(options, function (error, response, body) {
    debug('Get Request Error: ' + JSON.stringify(error));
    debug('Get Request Response: ' + JSON.stringify(response));
    debug('Get Request Body: ' + JSON.stringify(body));

    if (error == null && response.statusCode == 200){
      if(response.body.code == 0){
        ret.code = 0;
        ret.uid = response.body.id;
        ret.token= response.body.token;
        ret.msg = 'NORMAL';
      }else{
        ret.code = 1;
        ret.msg = 'LOGIN ERROR - CHANNEL';
      }

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    } else {
      ret.code = -1;
      ret.msg = 'LOGIN ERROR - SDK';

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }
  });
});

router.post('/login', function(req, res, next) {
  debug("POST /login");
  debug("PARAM : ", req.body);
  debug_log.info("POST /login");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code: -99,
    msg : 'LOGIN ERROR - UNKNOWN ERROR'
  };
  //req.query = JSON.parse(req.query.data);

  querystring.parse(req.body);

  var param_id = querystring.unescape(req.body.id ? req.body.id : '');
  var param_token = querystring.unescape(req.body.token ? req.body.token : '');
  var param_data = querystring.unescape(req.body.data ? req.body.data : '');
  var param_cid = querystring.unescape(req.body.cid ? req.body.cid : '');

  var reqbody = {
    id : param_id,
    token : param_token,
    data : param_data
  };

  var str = reqbody.id + '|' + reqbody.token + '|'+ reqbody.data + '|' + config.appkey;
  debug("STR : ", str);
  reqbody.sign = crypto.createHash('md5').update(str).digest('hex');

  var options = {
    url: config.sdkaddress + '/' + config.appid + '/' + param_cid + '/Login/' ,
    method: 'POST',
    body: reqbody,
    json: true
  };

  debug('REQUST OPTIONS : ',options);

  request(options, function (error, response, body) {
    debug('Get Request Error: ' + JSON.stringify(error));
    debug('Get Request Response: ' + JSON.stringify(response));
    debug('Get Request Body: ' + JSON.stringify(body));

    if (error == null && response.statusCode == 200){
      if(response.body.code == 0){
        ret.code = 0;
        ret.uid = response.body.id;
        ret.token= response.body.token;
        ret.msg = 'NORMAL';
      }else{
        ret.code = 1;
        ret.msg = 'LOGIN ERROR - CHANNEL';
      }

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    } else {
      ret.code = -1;
      ret.msg = 'LOGIN ERROR - SDK';

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }
  });
});

router.get('/create_order', function(req, res, next) {
  debug("GET /create_order");
  debug("PARAM : ", req.query);

  var ret = {
    code : -99,
    msg : 'CREATE ORDER ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_uid = querystring.unescape(req.query.uid ? req.query.uid : '');
  var param_pid = querystring.unescape(req.query.pid ? req.query.pid : '');
  var param_sid = querystring.unescape(req.query.sid ? req.query.sid : '');
  var param_cid = querystring.unescape(req.query.cid ? req.query.cid : '');

  var data = {
    oid : uid(8),                                                                 //内部订单号
    uid : param_uid,                                                              //渠道用户id
    pid : param_pid,                                                              //商品id
    sid : param_sid,                                                              //区服id
    createtime : new Date().getTime(),                                             //时间戳
    status : 0                                                                    //订单状态
  };

  debug("DATA : ", data);

  redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
    if (err){
      ret.code = 2;
      ret.msg = 'CREATE ORDER ERROR - REDIS ERROR';

      res.json(ret);
    }else{
      var reqbody = {
        cporder : data.oid,
        data : data.pid,
        notifyurl : config.selfaddress + ':' + config.port + '/game/notify',
        verifyurl : config.selfaddress + ':' + config.port + '/game/verify'
      };

      var str = reqbody.cporder + '|' + reqbody.data + '|' + config.appkey;
      console.log("STR : ", str);
      reqbody.sign = crypto.createHash('md5').update(str).digest('hex');

      var options = {
        url: config.sdkaddress + '/' + config.appid + '/' + param_cid + '/SaveOrder/' ,
        method: 'POST',
        body: reqbody,
        json: true
      };

      console.log('REQUST OPTIONS : ',options);

      request(options, function (error, response, body) {
        console.log('Get Request Error: ' + JSON.stringify(error));
        console.log('Get Request Response: ' + JSON.stringify(response));
        console.log('Get Request Body: ' + JSON.stringify(body));

        if (error == null && response.statusCode == 200){
          if(response.body.code == 0){
            ret.code = 0;
            ret.oid = data.oid;
            ret.msg = 'NORMAL';
          }else{
            ret.code = 1;
            ret.msg = 'CREATE ORDER ERROR - SDK INNER';
          }

          res.json(ret);
        } else {
          ret.code = -1;
          ret.msg = 'CREATE ORDER ERROR - SDK';

          res.json(ret);
        }
      });
    }
  });
});

router.post('/create_order', function(req, res, next) {
  debug("POST /create_order");
  debug("PARAM : ", req.body);
  debug_log.info("POST /create_order");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'CREATE ORDER ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.body)

  var param_uid = querystring.unescape(req.body.uid ? req.body.uid : '');
  var param_pid = querystring.unescape(req.body.pid ? req.body.pid : '');
  var param_sid = querystring.unescape(req.body.sid ? req.body.sid : '');
  var param_cid = querystring.unescape(req.body.cid ? req.body.cid : '');

  var data = {
    oid : uid(8),                                                                 //内部订单号
    uid : param_uid,                                                              //渠道用户id
    pid : param_pid,                                                              //商品id
    sid : param_sid,                                                              //区服id
    createtime : new Date().getTime(),                                             //时间戳
    status : 0                                                                    //订单状态
  };

  debug("DATA : ", data);

  redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
    if (err){
      ret.code = 2;
      ret.msg = 'CREATE ORDER ERROR - REDIS ERROR';

      res.json(ret);
    }else{
      var reqbody = {
        cporder : data.oid,
        data : data.pid,
        notifyurl : config.selfaddress + ':' + config.port + '/game/notify',
        verifyurl : config.selfaddress + ':' + config.port + '/game/verify'
      };

      var str = reqbody.cporder + '|' + reqbody.data + '|' + config.appkey;
      console.log("STR : ", str);
      reqbody.sign = crypto.createHash('md5').update(str).digest('hex');

      var options = {
        url: config.sdkaddress + '/' + config.appid + '/' + param_cid + '/SaveOrder/' ,
        method: 'POST',
        body: reqbody,
        json: true
      };

      console.log('REQUST OPTIONS : ',options);

      request(options, function (error, response, body) {
        console.log('Get Request Error: ' + JSON.stringify(error));
        console.log('Get Request Response: ' + JSON.stringify(response));
        console.log('Get Request Body: ' + JSON.stringify(body));

        if (error == null && response.statusCode == 200){
          if(response.body.code == 0){
            ret.code = 0;
            ret.oid = data.oid;
            ret.msg = 'NORMAL';
          }else{
            ret.code = 1;
            ret.msg = 'CREATE ORDER ERROR - SDK INNER';
          }

          res.json(ret);
        } else {
          ret.code = -1;
          ret.msg = 'CREATE ORDER ERROR - SDK';

          res.json(ret);
        }
      });
    }
  });
});

router.get('/client_pay_cancel', function(req, res, next) {
  debug("GET /client_pay_cancel");
  debug("PARAM : ", req.query);

  var ret = {
    code : -99,
    msg : 'CLIENT PAY CANCEL ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_oid = querystring.unescape(req.query.uid ? req.query.uid : '');

  redisClient.get('TESTGAME:' + data.oid + ':ORDER',function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CLIENT PAY CANCEL ERROR - REDIS GET ERROR';

      res.json(ret);
    }else{
      var order = {};
      if (reply && reply.length > 2)
      {
        order = JSON.parse(reply);

        if(order.status !== 0 ){
          ret.code = 3;
          ret.msg = 'CLIENT PAY CANCEL ERROR - ORDER STATUS ERR';

          res.json(ret);
        }else{
          order.status = 2;
          redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
            if (err) {
              ret.code = 2;
              ret.msg = 'CLIENT PAY CANCEL ERROR - REDIS SET ERROR';
            } else {
              ret.code = 0;
              ret.msg = 'NORMAL';
            }

            res.json(ret);
          });
        }
      }else{
        ret.code = 1;
        ret.msg = 'CLIENT PAY CANCEL ERROR - EMPTY';

        res.json(ret);
      }
    }
  });
});

router.post('/client_pay_cancel', function(req, res, next) {
  debug("POST /client_pay_cancel");
  debug("PARAM : ", req.body);
  debug_log.info("POST /client_pay_cancel");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'CLIENT PAY CANCEL ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.body)

  var param_oid = querystring.unescape(req.query.body ? req.query.body : '');

  redisClient.get('TESTGAME:' + data.oid + ':ORDER',function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CLIENT PAY CANCEL ERROR - REDIS GET ERROR';

      res.json(ret);
    }else{
      var order = {};
      if (reply && reply.length > 2)
      {
        order = JSON.parse(reply);

        if(order.status !== 0 ){
          ret.code = 3;
          ret.msg = 'CLIENT PAY CANCEL ERROR - ORDER STATUS ERR';

          res.json(ret);
        }else{
          order.status = 2;
          redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
            if (err) {
              ret.code = 2;
              ret.msg = 'CLIENT PAY CANCEL ERROR - REDIS SET ERROR';
            } else {
              ret.code = 0;
              ret.msg = 'NORMAL';
            }

            res.json(ret);
          });
        }
      }else{
        ret.code = 1;
        ret.msg = 'CLIENT PAY CANCEL ERROR - EMPTY';

        res.json(ret);
      }
    }
  });
});

router.get('/client_pay_complete', function(req, res, next) {
  debug("GET /client_pay_complete");
  debug("PARAM : ", req.query);

  var ret = {
    code : -99,
    msg : 'CLIENT PAY COMPLETE ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_oid = querystring.unescape(req.query.uid ? req.query.uid : '');

  redisClient.get('TESTGAME:' + data.oid + ':ORDER',function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CLIENT PAY COMPLETE ERROR - REDIS GET ERROR';

      res.json(ret);
    }else{
      var order = {};
      if (reply && reply.length > 2)
      {
        order = JSON.parse(reply);

        if(order.status !== 0 ){
          ret.code = 3;
          ret.msg = 'CLIENT PAY COMPLETE ERROR - ORDER STATUS ERR';

          res.json(ret);
        }else{
          order.status = 1;
          redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
            if (err) {
              ret.code = 2;
              ret.msg = 'CLIENT PAY COMPLETE ERROR - REDIS SET ERROR';
            } else {
              ret.code = 0;
              ret.msg = 'NORMAL';
            }

            res.json(ret);
          });
        }
      }else{
        ret.code = 1;
        ret.msg = 'CLIENT PAY COMPLETE ERROR - EMPTY';

        res.json(ret);
      }
    }
  });
});

router.post('/client_pay_complete', function(req, res, next) {
  debug("POST /client_pay_complete");
  debug("PARAM : ", req.body);
  debug_log.info("POST /client_pay_complete");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'CLIENT PAY COMPLETE ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_oid = querystring.unescape(req.query.uid ? req.query.uid : '');

  redisClient.get('TESTGAME:' + data.oid + ':ORDER',function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CLIENT PAY COMPLETE ERROR - REDIS GET ERROR';

      res.json(ret);
    }else{
      var order = {};
      if (reply && reply.length > 2)
      {
        order = JSON.parse(reply);

        if(order.status !== 0 ){
          ret.code = 3;
          ret.msg = 'CLIENT PAY COMPLETE ERROR - ORDER STATUS ERR';

          res.json(ret);
        }else{
          order.status = 1;
          redisClient.set('TESTGAME:' + data.oid + ':ORDER',JSON.stringify(data),function(err, reply) {
            if (err) {
              ret.code = 2;
              ret.msg = 'CLIENT PAY COMPLETE ERROR - REDIS SET ERROR';
            } else {
              ret.code = 0;
              ret.msg = 'NORMAL';
            }

            res.json(ret);
          });
        }
      }else{
        ret.code = 1;
        ret.msg = 'CLIENT PAY COMPLETE ERROR - EMPTY';

        res.json(ret);
      }
    }
  });
});

router.get('/get_account', function(req, res, next) {
  debug("GET /get_account");
  debug("PARAM : ", req.query);
  debug_log.info("GET /get_account");
  debug_log.info("PARAM : ", req.query);

  var ret = {
    code : -99,
    msg : 'GET ACCOUNT ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_uid = querystring.unescape(req.query.uid ? req.query.uid : '');
  var param_sid = querystring.unescape(req.query.sid ? req.query.sid : '');

  redisClient.get('TESTGAME:' + param_uid + ':' + param_sid + ':ACCOUNT',function(err, reply) {
    if (err){
      ret.code = 2;
      ret.msg = 'GET ACCOUNT ERROR - REDIS ERROR';

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }else {
      var account = {};
      if (reply && reply.length > 2)
      {
        account = JSON.parse(reply);
	account.balance = '' + account.balance;
        ret.code = 0;
        ret.msg = 'NORMAL';
      }else{
        ret.code = 1;
        ret.msg = 'NO ACCOUNT';
      }

      ret.uid = param_uid;
      ret.data = account;

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }
  });
});

router.post('/get_account', function(req, res, next) {
  debug("POST /get_account");
  debug("PARAM : ", req.body);
  debug_log.info("POST /get_account");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'GET ACCOUNT ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.body)

  var param_uid = querystring.unescape(req.body.uid ? req.body.uid : '');
  var param_sid = querystring.unescape(req.body.sid ? req.body.sid : '');

  redisClient.get('TESTGAME:' + param_uid + ':' + param_sid + ':ACCOUNT',function(err, reply) {
    if (err){
      ret.code = 2;
      ret.msg = 'GET ACCOUNT ERROR - REDIS ERROR';

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }else {
      var account = {};
      if (reply && reply.length > 2)
      {
        account = JSON.parse(reply);
        account.balance = '' + account.balance;
        ret.code = 0;
        ret.msg = 'NORMAL';
      }else{
        ret.code = 1;
        ret.msg = 'NO ACCOUNT';
      }

      ret.uid = param_uid;
      ret.data = account;

      debug_log.info("RETURN : ", ret);
      res.json(ret);
    }
  });
});

router.get('/create_account', function(req, res, next) {
  debug("GET /create_account");
  debug("PARAM : ", req.query);

  var ret = {
    code : -99,
    msg : 'CREATE ACCOUNT ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.query)

  var param_uid = querystring.unescape(req.query.uid ? req.query.uid : '');
  var param_sid = querystring.unescape(req.query.sid ? req.query.sid : '');

  var data = {
    uid : param_uid,                                //用户ID
    sid : param_sid,                                //区服ID
    balance : 0                                     //余额 分
  };

  redisClient.set('TESTGAME:' + param_uid + ':' + param_sid + ':ACCOUNT',JSON.stringify(data),function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CREATE ACCOUNT ERROR - REDIS ERROR';
    } else {
      ret.code = 0;
      ret.msg = 'NORMAL';
    }

    res.json(ret);
  });
});

router.post('/create_account', function(req, res, next) {
  debug("POST /create_account");
  debug("PARAM : ", req.body);
  debug_log.info("POST /create_account");
  debug_log.info("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'CREATE ACCOUNT ERROR - UNKNOWN ERROR'
  };

  //req.query = JSON.parse(req.query.data);
  querystring.parse(req.body)

  var param_uid = querystring.unescape(req.body.uid ? req.body.uid : '');
  var param_sid = querystring.unescape(req.body.sid ? req.body.sid : '');

  var data = {
    uid : param_uid,                                //用户ID
    sid : param_sid,                                //区服ID
    balance : 0                                     //余额 分
  };

  redisClient.set('TESTGAME:' + param_uid + ':' + param_sid + ':ACCOUNT',JSON.stringify(data),function(err, reply) {
    if (err) {
      ret.code = 2;
      ret.msg = 'CREATE ACCOUNT ERROR - REDIS ERROR';
    } else {
      ret.code = 0;
      ret.msg = 'NORMAL';
    }

    res.json(ret);
  });
});

router.post('/verify', function(req, res, next) {
  debug("POST /verify");
  debug("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'VERIFY ERROR - UNKNOWN ERROR'
  };
console.log("1111111111111111111111111111111111111");
console.log(req.body);
  var param_code = req.body.code;
  var param_id = req.body.id;
  var param_order = req.body.order;
  var param_cporder = req.body.cporder;
  var param_info = req.body.info;
  var param_sign = req.body.sign;

  var str = param_code + '|' +
      param_id + '|'+
      param_order + '|'+
      param_cporder + '|'+
      param_info + '|' +
      config.appkey;
  debug("STR : ", str);
  var calsign = crypto.createHash('md5').update(str, 'utf8').digest('hex');

  if (param_sign !== calsign){
    ret.code = 3;
    ret.msg = 'VERIFY ERROR - SIGN ERROR';

    res.json(ret);
  }else{
    redisClient.get('TESTGAME:' + param_cporder + ':ORDER',function(err, reply) {
      if (err){
        ret.code = 2;
        ret.msg = 'VERIFY ERROR - REDIS ERROR';

        res.json(ret);
      }else {
        var order = {};
        if (reply && reply.length > 2)
        {
          order = JSON.parse(reply);
          ret.code = 0;
          ret.msg = 'NORMAL';
          ret.id = order.uid;
          ret.order = '';
          ret.cporder = order.oid;
          ret.amount = config.productlist['p' + order.pid] ? config.productlist['p' + order.pid].price : 0;
          ret.createtime = order.createtime;
          ret.Itemid = order.pid;
          ret.Itemquantity = 0;
          ret.status = order.status;
          ret.info = '';
        }else{
          ret.code = 1;
          ret.msg = 'NO ORDER';
        }
        res.json(ret);
	console.log("1111111111111111111111111111111111111");
	console.log(ret);
      }
    });
  }
});

router.post('/notify', function(req, res, next) {
  debug("POST /notify");
  debug("PARAM : ", req.body);

  var ret = {
    code : -99,
    msg : 'VERIFY ERROR - UNKNOWN ERROR'
  };

  var param_code = req.body.code;
  var param_id = req.body.id;
  var param_order = req.body.order;
  var param_cporder = req.body.cporder;
  var param_info = req.body.info;
  var param_sign = req.body.sign;
  var param_amount = req.body.amount;

  var str = param_code + '|' + param_id + '|'+ param_order + '|'+ param_cporder + '|'+ param_info + '|' + config.appkey;
  debug("STR : ", str);
  var calsign = crypto.createHash('md5').update(str).digest('hex');

  if (param_sign !== calsign){
    ret.code = 3;
    ret.msg = 'VERIFY ERROR - SIGN ERROR';

    res.json(ret);
  }

  ret.code = 0;
  ret.msg = 'NORMAL';
  res.json(ret);

  redisClient.get('TESTGAME:' + param_cporder + ':ORDER',function(err, reply) {
    if (err) {
      debug('NOTIFY STEPS - ','GET ORDER ERR');
    }else{
      var order = {};
      if (reply && reply.length > 2)
      {
        order = JSON.parse(reply);

        if(order.status !== 1 && order.status !== 0 ){
          debug('NOTIFY STEPS - ','ORDER STATUS ERR');
        }else{
          order.coid = param_order;
          order.status = 3;
          order.notifytime = new Date().getTime();

          redisClient.set('TESTGAME:' + param_cporder + ':ORDER',JSON.stringify(order),function(err, reply) {
            if (err) {
              debug('NOTIFY STEPS - ','SET ORDER 3 ERR');
            }else{
              redisClient.get('TESTGAME:' + order.uid + ':' + order.sid + ':ACCOUNT',function(err, reply) {
                if (err) {
                  debug('NOTIFY STEPS - ','GET ACCOUNT ERR');
                }else{
                  var account = {};
                  if (reply && reply.length > 2)
                  {
                    account = JSON.parse(reply);
                    account.balance += config.productlist['p' + order.pid] ? parseInt(config.productlist['p' + order.pid].price) : 0;

                    redisClient.set('TESTGAME:' + order.uid + ':' + order.sid + ':ACCOUNT',JSON.stringify(account),function(err, reply) {
                      if (err) {
                        debug('NOTIFY STEPS - ','SET ACCOUNT ERR');
                      }else{
                        order.status = 4;
                        order.completetime = new Date().getTime();
                        redisClient.set('TESTGAME:' + param_cporder + ':ORDER',JSON.stringify(order),function(err, reply) {
                          if (err) {
                            debug('NOTIFY STEPS - ','SET ORDER 4 ERR');
                          } else {
                            debug('NOTIFY STEPS - ','PAY COMPLETE');
                          }
                        });
                      }
                    });
                  }else{
                    debug('NOTIFY STEPS - ','GET ACCOUNT EMPTY');
                  }
                }
              });
            }
          });
        }
      }else{
        console.log('NOTIFY STEPS - ','GET ORDER EMPTY');
      }
    }
  });
});

//未完成
router.get('/test', function(req, res, next) {
  debug("GET /notify");
  debug("PARAM : ", req.query);


});

module.exports = router;