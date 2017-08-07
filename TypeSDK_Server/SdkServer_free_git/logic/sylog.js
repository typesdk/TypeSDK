/**
 * Created by TypeSDK 2016/10/10.
 */

var log4js = require('log4js');
var path = require("path");
log4js.configure({
	appenders: [
		{type: 'console'},
		{
			type: 'file',
			absolute: true,
			filename: path.join(__dirname, '..', 'logs', 'console.log'),
			maxLogSize: 1024 * 1024,
			backups: 10,
			category: 'console',
			layout: {
				type: 'pattern',
				pattern: "[%d][%p][%h][%z]- %x{lurl} %n %m",
				tokens: {
					lurl: function () {
						var logger = log4js.getLogger('console');
						return logger.requrl ? logger.requrl : 'NOURL';
					},
					ls: function () {
						var logger = log4js.getLogger('console');
						return logger.reqparam ? JSON.stringify(logger.reqparam) : '{}';
					}
				}
			}

		},
		{
			type: 'dateFile',
			absolute: true,
			filename: path.join(__dirname, '..', 'logs', 'sdk'),
			maxLogSize: 1024 * 1024,
			backups: 10,
			pattern: "_yyyy-MM-dd.log",
			alwaysIncludePattern: true,
			category: 'sdk'
		}
	],
	replaceConsole: true,
	levels: {
		log_file: "ALL",
		console: "ALL",
		log_date: "ALL"
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

