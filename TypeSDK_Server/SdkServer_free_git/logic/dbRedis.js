/**
 * Created by TypeSDK 2016/10/10.
 */
var config = require('../config');
var redis = require("redis"),
    client = redis.createClient(config.redisconfig.port, config.redisconfig.host);

//Redis验证
client.auth(config.redisconfig.pass);

// redis 链接错误
var index = 0;
client.on("error", function(error) {
    console.log('RedisError: 请联系运维检查配置！' + error);
    client = redis.createClient({
        retry_strategy: function (options) {
            if (options.error.code === 'ECONNREFUSED') {
                // End reconnecting on a specific error and flush all commands with a individual error
                return new Error('The server refused the connection');
            }
            if (options.total_retry_time > 1000 * 60 * 60) {
                // End reconnecting after a specific timeout and flush all commands with a individual error
                return new Error('Retry time exhausted');
            }
            if (options.times_connected > 10) {
                // End reconnecting with built in error
                return undefined;
            }
            // reconnect after
            return Math.max(options.attempt * 100, 3000);
        }
    });
    client.end(true);
});
client.on('reconnecting',function(obj){
    console.log('reconnecting: Redis重连第' + obj.attempt+'次');
    console.log('reconnecting: Redis连接延迟' + obj.delay+'毫秒');
    client.end(false);
});
module.exports = client;