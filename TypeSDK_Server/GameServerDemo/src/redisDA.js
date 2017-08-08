/**
 * Created by Wans on 2016/7/13.
 */
var  debug = require('debug')('redisDA'),
      redis = require('redis'),
      config = require('config-lite');

redisOption = config.redisConf || {};

debug('redis config all: %j', redisOption);
debug('redis config port: %s', redisOption.port || (redisOption.port = 6379));
debug('redis config host: %s', redisOption.host || (redisOption.host = '127.0.0.1'));
debug('redis config options: %j', redisOption.options || (redisOption.options = {}));
debug('redis config db: %s', redisOption.db || (redisOption.db = 0));
debug('redis config ttl: %s', redisOption.ttl);

var client = redis.createClient(
    redisOption.port,
    redisOption.host,
    redisOption.options
);

client.select(redisOption.db, function () {
  debug('redis changed to db %d', redisOption.db);
});

client.on('connect', function () {
  debug('redis is connecting');
});

client.on('ready', function () {
  debug('redis ready');
  debug('redis host: %s', client.host);
  debug('redis port: %s', client.port);
  debug('redis parser: %s', client.reply_parser.name);
  debug('redis server info: %j', client.server_info);
});

client.on('reconnect', function () {
  debug('redis is reconnecting');
});

client.on('error', function (err) {
  debug('redis encouters error: %j', err.stack || err);
});

client.on('end', function () {
  debug('redis connection ended');
});

module.exports = client;


