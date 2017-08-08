/**
 * Created by Wans on 2016/3/14.
 */

module.exports = {
  selfaddress: 'http://127.0.0.1',
  port: process.env.PORT || 40001,
  redisConf: {
    "host": "127.0.0.1",
    "port": 6379,
    "options": {"auth_pass" : "typesdk.com"}
  },
  appid: 1001,
  appkey: 'typesdk_app_key',
  sdkaddress: 'http://127.0.0.1:40000',
  productlist: {
    "p1" : {"name" : "测试商品1","price" : 100},
    "p2" : {"name" : "测试商品2","price" : 200},
    "p3" : {"name" : "测试商品3","price" : 300}
  }
};
