/**
 * Created by TypeSDK 2016/11/16
 */
var config = require('../config');
var mysql = require('mysql');
var pool = mysql.createPool(config.dbconfig);

var poolModule = require('generic-pool');
var poolgeneric = poolModule.Pool({
    name: 'mysql',
    //将建 一个 连接的 handler
    create: function (callback) {
        var c = require('mysql').createConnection(config.dbconfig);
        c.connect();
        callback(null, c);
    },
    // 释放一个连接的 handler
    destroy: function (client) {
        client.end();
    },
    // 连接池中最大连接数量
    max: 20,
    // 连接池中最少连接数量
    min: 20,
    // 如果一个线程3秒钟内没有被使用过的话。那么就释放
    idleTimeoutMillis: 30000,
    // 如果 设置为 true 的话，就是使用 console.log 打印入职，当然你可以传递一个 function 最为作为日志记录handler
    log: false
});

function saveRequsetLog(game, channel, action, detail) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }
        var sqlparam = '';
        sqlparam += " SET @game = '" + game + "'";
        sqlparam += " , @channel = '" + channel + "'";
        sqlparam += " , @action = '" + action + "'";
        sqlparam += " , @detail = '" + JSON.stringify(detail) + "'";
        client.query(sqlparam + '; CALL  p_sdk_request_log_insert(@game,@channel,@action,@detail);', function (err, rows) {
            //console.log(rows);
            if (err) console.log(err);

            poolgeneric.release(client);
        });
    });
}

//function createOrder(game,channel,cporder,verifyurl,channelId)
function createOrder(game, channel, cporder, verifyurl, channelId, notifyurl) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }
        var sqlparam = '';
        sqlparam += "SET @game = '" + game + "'";
        sqlparam += " , @channel = '" + channel + "'";
        sqlparam += " , @cporder = '" + cporder + "'";
        sqlparam += ", @verifyurl='" + verifyurl + "'";
        sqlparam += ", @channelId='" + channelId + "'";
        sqlparam += ", @notifyurl='" + notifyurl + "'";

        client.query(sqlparam + '; CALL  p_sdk_order_create(@game,@channel,@cporder,@verifyurl,@channelId,@notifyurl); ', function (err, rows) {
            //console.log(rows);
            if (err) console.log(err);

            poolgeneric.release(client);
        });
    });
}

function UpdateOrderStatus(game, channel, cporder, chorder, status, amount,retdata) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }

        var sqlparam = '';
        sqlparam += "SET @game = '" + game + "'";
        sqlparam += " , @channel = '" + channel + "'";
        sqlparam += " , @cporder = '" + cporder + "'";
        sqlparam += " , @chorder = '" + chorder + "'";
        sqlparam += " , @status = '" + status + "'";
        sqlparam += " , @amount = '" + amount + "'";

        if (typeof retdata != 'undefined') {
            var dataInfo = JSON.stringify(retdata);
            sqlparam += " , @data = '" + dataInfo.replace(/\\/g, '\\\\') + "'";
            console.log(sqlparam + '; CALL  p_sdk_order_update_statusAndData(@game,@channel,@cporder,@chorder,@status,@amount,@data); ');
            client.query(sqlparam + '; CALL  p_sdk_order_update_statusAndData(@game,@channel,@cporder,@chorder,@status,@amount,@data); ', function (err, rows) {
                //console.log(rows);
                if (err) console.log(err);
                poolgeneric.release(client);
            });
        } else {
            client.query(sqlparam + '; CALL  p_sdk_order_update_status(@game,@channel,@cporder,@chorder,@status,@amount ); ', function (err, rows) {
                //console.log(rows);
                if (err) console.log(err);

                poolgeneric.release(client);
            });
        }
    });
}

function searchOrder(cporder, ret, retf) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            retf(ret);
            return;
        }
        var sqlparam = '';
        sqlparam += " SET @cporder = '" + cporder + "'";
        client.query(sqlparam + '; CALL p_sdk_cporder_select(@cporder); ', function (err, rows) {
            //console.log(rows);
            if (err) {
                console.log(err);
                poolgeneric.release(client);
                retf(ret);
                return;
            }

            ret.code = 0;
            ret.msg = "NORMAL";
            if (rows[1].length > 0) {
                ret.status = rows[1][0].ord_status;
                ret.channelId = rows[1][0].ord_channelId;
                ret.notifyurl = rows[1][0].ord_notifyurl;
                ret.verifyurl = rows[1][0].ord_verifyurl;
            } else {
                ret.status = 'unknown';
                ret.channelId = 'unknown';
                ret.notifyurl = 'unknown';
                ret.verifyurl = 'unknown';
            }
            poolgeneric.release(client);
            retf(ret);
            return;
        });
    });
}

function selectAllOrder(game, channel, retf) {
    var ret = {'code': -99, 'msg': "MYSQL ERROR"};
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }
        var date = new Date();
        var endTime = date.getTime() / 1000;

        var startTime = endTime - 30 * 24 * 60 * 60;
        var sqlparam = '';
        sqlparam += "SET @game = '" + game + "'";
        sqlparam += " , @startTime = '" + startTime + "'";
        sqlparam += " , @endTime = '" + endTime + "'";
        client.query(sqlparam + '; CALL  p_sdk_order_stutas_select(@game,@startTime,@endTime); ', function (err, rows) {
            if (err) console.log(err);
            if (!err) {
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.data = rows;
            } else {
                ret.data = [];
            }
            poolgeneric.release(client);
            retf(ret);
            //console.log(rows);
        });
    });
}
function searchByCporderAndOrder(cporder, ret, retf) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }
        var date = new Date();
        var endTime = date.getTime() / 1000;

        var startTime = endTime - 30 * 24 * 60 * 60;
        var sqlparam = '';
        sqlparam += "SET @cporder = '" + cporder + "'";

        client.query(sqlparam + '; CALL  p_sdk_order_searchByorder(@cporder); ', function (err, rows) {
            if (err) console.log(err);
            if (!err) {
                ret.code = 0;
                ret.msg = "NORMAL";
                ret.data = rows;
            } else {
                ret.data = [];
            }
            poolgeneric.release(client);
            retf(ret);
            //console.log(rows);
        });

    });
}
function asGameSearch(cporder, userId, ret, retf) {
    poolgeneric.acquire(function (err, client) {
        if (err) {
            console.log(err);
            poolgeneric.release(client);
            return;
        }

        var sqlparam = '';
        sqlparam += "SET @cporder = '" + cporder + "'";

        client.query(sqlparam + '; CALL  p_game_order_search(@cporder); ', function (err, rows) {
            if (err) {
                console.log(err);
                poolgeneric.release(client);
                return;
            }
            if (!err) {
                if (rows[1].length > 0) {
                    ret = rows[1][0];
                }
                ret.code = 0;
                ret.msg = "NORMAL";

            } else {
                ret.code = 0;
                ret.msg = "NORMAL";
            }
            poolgeneric.release(client);
            //console.log(rows);
            retf(ret);
            return;
        });
    });
}

exports.saveRequsetLog = saveRequsetLog;
exports.createOrder = createOrder;
exports.UpdateOrderStatus = UpdateOrderStatus;
exports.searchOrder = searchOrder;
exports.selectAllOrder = selectAllOrder;
exports.searchByCporderAndOrder = searchByCporderAndOrder;
exports.asGameSearch = asGameSearch;
