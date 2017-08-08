/**
 * Created by Administrator on 2016/2/1.
 */
var log4js = require('log4js');

log4js.configure({
    appenders: [
        {
            type: 'console',
            category: 'console'
        },
        {
            type: 'file',
            absolute: true,
            filename: '../logs/debugLog.log',
            maxLogSize: 1024 * 1024,
            backups: 3,
            category: 'debug_log'
        }
    ],
    replaceConsole: true,
    levels: {
        console: "ALL",
        debug_log: "ALL"
    }
});
//var logger = log4js.getLogger(name);
//logger.setLevel('INFO');

var loggerf = function (name) {
    var logger = log4js.getLogger(name);
    logger.setLevel('INFO');
    //return log4js.connectLogger(logger(name),{level:'auto'});
    return logger;
};
exports.loggerf = loggerf;
exports.logger = function (name) {
    return log4js.connectLogger(loggerf(name), {level: 'auto'})
};
