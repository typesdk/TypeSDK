/**
 * Created by TypeSDK 2016/10/10.
 */
var Redis = require("./dbRedis.js");
var channelsBuffer = {};
var version = 0;

/**
 * 检查游戏渠道是否有更新
 *
 * @param gameid
 * @param cb
 */
function checkRefreshFlag(gameid, cb) {
    var ret = {code: 1, msg: 'NEED REFRESH'};
    Redis.hget('GAME:' + gameid + ':VERSION', 'version', function(err, versionData){
        if (err != null || versionData == null){
            ret.code = 2;
            ret.msg = 'REDIS_NOT_EXIST';
            cb(ret);
        } else {
            if (version != versionData){
                ret.code = 1;
                ret.msg = 'NEED REFRESH';
                version = versionData;
            } else {
                ret.code = 0;
                ret.msg = 'NO REFRESH';
            }
            cb(ret);
        }
    });
}

function getChannelsByGameID(gameid, cb) {
    var ret = {code: 1, msg: 'GAME NOT EXISTS'};

    checkRefreshFlag(gameid, function (obj) {
        console.log("****** checkRefreshFlag ******");
        console.log(obj);
        if (obj.code == 1) {
            getGameNewChannels(gameid, cb);
        } else {
            if (channelsBuffer['game' + gameid] != null){
                ret.code = 0;
                ret.msg = 'NORMAL';
                ret.data = channelsBuffer['game' + gameid];
                cb(ret);
            } else {
                getGameNewChannels(gameid, cb);
            }
        }
    });
}

function getGameByID(gameid, cb) {
    var ret = {code: 1, msg: 'GAME NOT EXISTS'};

    Redis.hgetall('GAME:' + gameid + ':ID', function (err, obj) {
        if (err) {
            ret.code = -1;
            ret.msg = 'REDIS ERROR';
        } else {
            if (obj) {
                ret.code = 0;
                ret.msg = 'NORMAL';
                ret.data = obj;
            }
        }

        cb(ret);
    });

}

function getGameByName(gameName, cb) {
    var ret = {code: 1, msg: 'GAME NOT EXISTS'};

    Redis.hget('GAME:' + gameName + ':NAME', function (err, obj) {
        if (err) {
            ret.code = -1;
            ret.msg = 'REDIS ERROR';

            cb(ret);
        } else {
            if (obj) {
                getGameByID(obj.id, cb);
            } else {
                cb(ret);
            }
        }
    });
}

/**
 * 获取游戏的新的渠道信息
 *
 * @param gameid
 * @param callBack
 */
function getGameNewChannels(gameid, callBack) {
    var ret = {code: -99, msg: 'UNKNOWN ERROR'};
    Redis.hgetall('GAME:' + gameid + ':CHANNEL', function (err, obj) {
        if (err) {
            ret.code = -1;
            ret.msg = 'REDIS ERROR';

            callBack(ret);
        } else {
            for (var i in obj) {
                obj[i] = JSON.parse(obj[i]);
            }

            console.log('obj:',obj);

            channelsBuffer['game' + gameid] = obj ? obj : {};
            getGameByID(gameid, function (objG) {
                if (objG.code == 1) {
                    callBack(objG);
                } else {
                    channelsBuffer['game' + gameid].id = objG.data.id;
                    channelsBuffer['game' + gameid].name = objG.data.gameName;
                    channelsBuffer['game' + gameid].attrs = {};
                    channelsBuffer['game' + gameid].attrs.gkey = objG.data.apikey;
                    channelsBuffer['game' + gameid].attrs.ckey = 'clientKey';
                    channelsBuffer['game' + gameid].attrs.id = objG.data.id;


                    ret.code = 0;
                    ret.msg = 'NORMAL';
                    ret.data = channelsBuffer['game' + gameid];
                    callBack(ret);
                }
            });
        }
    });
}

exports.getGameByID = getGameByID;
exports.getGameByName = getGameByName;
exports.getChannelsByGameID = getChannelsByGameID;
exports.channelsBuffer = channelsBuffer;