//--------------------------------------------------------------------
// 
//   打包程序
//   （Nodejs服务--Android,Windows,Mac版）
//
// 功能：每隔一段时间请求打包任务
// Update Date 2017-01-14  
// Create By Typesdk
//--------------------------------------------------------------------
var child_process = require('child_process');
var http = require('http');
var config = require('./config.json');
var log4js = require('log4js');
var fs = require('fs-extra');
var fsSync = require('fs-sync');
var path = require('path');
var AdmZip = require('adm-zip');
var os = require('os');
var iconv = require('iconv-lite');

var TYPESDK_PATH = path.normalize(__dirname + '/../..');
var INTERVAL = config.INTERVAL; // 监控间隔时间
var GIT_URL = config.GIT_URL;
var WEB_REQUEST_URL = 'http://' + config.WEB_SERVER_IP + '/taskmanage';
// api请求地址
var PLATFORM = config.PLATFORM; // 平台（Android/ios）
//var IP_ADDR = config.IP_ADDR;                		  // IP地址
var IP_ADDR = getIPAdress();
var processNO = '01'; //默认值

// #获取打包脚本配置设置，如config文件中未指定，则使用默认设置
var SHARE_PATH = path.join(TYPESDK_PATH, 'share'); // 共享文件夹
var PACKAGE_PATH = path.join(TYPESDK_PATH, 'package'); //Package文件夹
var TMP_PATH = path.join(path.normalize(TYPESDK_PATH + '/..'), 'typesdk.tmp'); //打包临时文件夹
var ANT_HOME = path.join(TYPESDK_PATH, 'software/ant/ant_1.9'); //ANT位置
var ANT_BIN = path.join(ANT_HOME, 'bin/ant'); //ANT BIN位置
if (os.platform() == 'linux') {
	var JAVA_HOME = path.join(TYPESDK_PATH, 'software/jdk1.8.0_65'); //JDK位置
	var ANDROID_HOME = path.join(TYPESDK_PATH, 'software/android-sdk-linux'); //Android SDK位置
} else if (os.platform() == 'darwin') {
	//	var JAVA_HOME = path.normalize('/Library/Internet\ Plug-Ins/JavaAppletPlugin.plugin/Contents/Home');
	var ANDROID_HOME = path.join(TYPESDK_PATH, 'software/android-sdk-mac');
} else {
	var JAVA_HOME = path.join(TYPESDK_PATH, 'software/jdk1.8.0_121');
	var ANDROID_HOME = path.join(TYPESDK_PATH, 'software/android-sdk-windows');
}
var ANDROID_TOOLS_BIN = path.join(ANDROID_HOME, 'tools/android'); //Android 工具应用位置
var LOG_PATH = path.join(SHARE_PATH, 'output/logs'); //输出日志目录
var SDK_SOURCE_PATH = path.join(SHARE_PATH, 'SDK'); //TypeSDK客户端代码目录
var GAME_FILE_NAME = 'Game.zip'; //游戏项目文件名
var TMP_FOLDER = '';
var LOG_FILE = '';
var FLAG_FILE = '';
var NEW_ANDROIDMANIFEST_PATH = '';

if (os.platform() != 'darwin') {
	process.env.JAVA_HOME = JAVA_HOME;
	process.env.CLASSPATH = ':' + path.join(JAVA_HOME, 'lib/dt.jar') + ':' + path.join(JAVA_HOME, 'lib/tools.jar');
}
process.env.ANDROID_HOME = ANDROID_HOME;
process.env.ANT_HOME = ANT_HOME;

function getIPAdress() {
	var interfaces = require('os').networkInterfaces();
	for (var devName in interfaces) {
		var iface = interfaces[devName];
		for (var i = 0; i < iface.length; i++) {
			var alias = iface[i];
			if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal) {
				return alias.address;
			}
		}
	}
}

process.argv.forEach(function(val, index, array) {
	if (index == 2) {
		processNO = val; //命令行参数  
	} else {
		processNO = process.pid;
	}
});


log4js.configure({
	"appenders": [{
		"type": "console",
		"category": "console"
	}, {
		"type": "file",
		"filename": "logs/" + IP_ADDR + "_" + processNO + ".log",
		"maxLogSize": "1024000",
		"backups": "10",
		"category": "normal"
	}],
	"replaceConsole": true
});

var logger = log4js.getLogger('normal');
//trace, debug, info, warn, error, fatal
logger.setLevel('debug');

InfoLog("Package service started.");
InfoLog("process Id: " + process.pid);
InfoLog('process No: ' + processNO);

//打包shell参数
var TaskID = '';
var Channel = '';
var ChannelVersion = '';
var GameID = '';
var IconID = '';
var Version = '';
var BatchNo = '';
var SdkVer = '';
var GameFile = '';
var ConfigFileDir = '';
var IconFileDir = '';
var GameFileName = "Game.zip";
var IsEncrypt = '';
var AdID = '';
var CompileMode = 'release';
var PluginID = '';
var PluginVer = '';
var SignKey = '';

var isFileExitst = fs.existsSync(path.join(TMP_PATH, "PACKAGING_FLAG_" + processNO));
if (isFileExitst) {
	//删除文件
	fs.unlinkSync(path.join(TMP_PATH, "PACKAGING_FLAG_" + processNO));
}

//每隔一段时间请求打包任务
setTimeout(function() {
		setInterval(function() {

			//判断打包标识文件
			InfoLog(TMP_PATH);
			InfoLog("PACKAGING_FLAG_" + processNO);
			var isBusy = fs.existsSync(path.join(TMP_PATH, "PACKAGING_FLAG_" + processNO));
			if (isBusy) {
				//系统正在打包
				InfoLog("Performing the package task. (id:" + TaskID + ")");
				return;
			}

			//InfoLog("Check package task.");
			//发送【获取任务/gainTask】命令
			var urlGainTask = WEB_REQUEST_URL + "?action=gainTask";
			urlGainTask = urlGainTask + "&platform=" + PLATFORM;
			urlGainTask = urlGainTask + "&serveraddr=" + IP_ADDR;
			InfoLog(urlGainTask);
			http.get(urlGainTask, function(res) {
				InfoLog("Received normal response. (gainTask)");

				if (res.statusCode != '200') {
					InfoLog('STATUS: ' + res.statusCode);
					ErrLog('Status Code Error.');
					return;
				}

				res.setEncoding('utf8');
				res.on('data', function(chunk) {
					InfoLog('BODY: ' + chunk);

					var body = JSON.parse(chunk);
					if (body.data !== null) {
						//返回打包任务
						InfoLog("Got a package task.");
						//获取任务参数（游戏id，渠道id，版本，文件下载路径，）
						InfoLog(JSON.stringify(body.data[0]));
						TaskID = body.data[0].TaskID;
						Channel = body.data[0].Channel;
						ChannelVersion = body.data[0].ChannelVersion;
						GameID = body.data[0].GameID;
						IconID = body.data[0].IconID;
						Version = body.data[0].GameVersion;
						BatchNo = body.data[0].BatchNo;
						SdkVer = body.data[0].SdkVer;
						IsEncrypt = body.data[0].IsEncrypt;
						AdID = body.data[0].AdID;
						CompileMode = body.data[0].CompileMode;
						PluginID = body.data[0].PluginID;
						PluginVer = body.data[0].PluginVersion;
						SignKey = body.data[0].SignKey;

						if (typeof PluginID == "undefined" || typeof PluginVer == "undefined") {
							PluginID = "0";
						}

						//检查打包材料
						GameFile = path.join(SHARE_PATH, "game_file", GameID, Version, GameFileName);
						ConfigFileDir = path.join(SHARE_PATH, "config", GameID);
						IconFileDir = path.join(SHARE_PATH, "icon", GameID, IconID);

						var exists = fs.existsSync(GameFile);
						if (!exists) {
							ErrLog("Game file no exist. " + GameFile);
							sendLoseTask(TaskID);
							InfoLog("--------------------------------------------------------------------------------------\n\n");
							return;
						} else {
							InfoLog("Game file exist.   " + GameFile);
						}

						exists = fs.existsSync(ConfigFileDir);
						if (!exists) {
							ErrLog("Config file no exist. " + ConfigFileDir);
							sendLoseTask(TaskID);
							InfoLog("--------------------------------------------------------------------------------------\n\n");
							return;
						} else {
							InfoLog("Config file exist. " + ConfigFileDir);
						}

						exists = fs.existsSync(IconFileDir);
						if (!exists) {
							ErrLog("Icon file no exist. " + IconFileDir);
							sendLoseTask(TaskID);
							InfoLog("--------------------------------------------------------------------------------------\n\n");
							return;
						} else {
							InfoLog("Icon file exist.   " + IconFileDir);
						}


						//发送【开始任务/startTask】命令
						var urlStartTask = WEB_REQUEST_URL + "?action=startTask";
						urlStartTask = urlStartTask + "&platform=" + PLATFORM;
						urlStartTask = urlStartTask + "&taskid=" + TaskID;
						urlStartTask = urlStartTask + "&serveraddr=" + IP_ADDR;

						InfoLog("Send start task reply.");
						InfoLog(urlStartTask);

						//发送开始打包的消息
						http.get(urlStartTask, function(res) {
							if (res.statusCode != '200') {
								ErrLog("Status Code Error. (startTask) STATUS:" + res.statusCode);
								return;
							}
							InfoLog("Received normal response. (startTask)");
							res.setEncoding('utf8');
							res.on('data', function(chunk) {
								//消息回答
								InfoLog('BODY: ' + chunk);

								//执行shell打包
								try {
									callShell(TaskID, function callback(taskId, result) {
										if (result == "normal") {
											//shell执行正常
											sendFinishTask(taskId);
										} else {
											//shell执行失败
											sendLoseTask(taskId);
										}
									});
								} catch (e) {
									ErrLog("Package error:" + e.message);
									PkgLog(e);
									sendLoseTask(TaskID);
								}
							});
						}).on('error', function(e) {
							ErrLog("Received error response. (startTask) message:" + e.message);
							InfoLog("--------------------------------------------------------------------------------------\n\n");
						});
					} else {
						//无打包任务
						InfoLog("Check package task. No task.\n");
					}

				});

			}).on('error', function(e) {
				ErrLog("Received error response. (gainTask) message:" + e.message);
				InfoLog("--------------------------------------------------------------------------------------\n\n");
			});

		}, INTERVAL);
	},
	Math.floor(Math.random() * 3) * 1000);

//打包完成通知
function sendFinishTask(taskId) {

	var urlFinish = WEB_REQUEST_URL + "?action=finishTask";
	var Encrypted = ""; //文件名加密标记
	if (IsEncrypt == "1") {
		Encrypted = "_encrypted";
	}
	var packagename = GameID + "_" + Version.substring(0, Version.length - 18) + "_" + Channel + ChannelVersion + "_" + TaskID + Encrypted + ".apk";

	urlFinish = urlFinish + "&platform=" + PLATFORM;
	urlFinish = urlFinish + "&taskid=" + taskId;
	urlFinish = urlFinish + "&packagename=" + packagename;
	urlFinish = urlFinish + "&serveraddr=" + IP_ADDR;

	InfoLog("Send finish task message.");
	InfoLog(urlFinish);

	//发送打包完成的消息
	http.get(urlFinish, function(res) {
		if (res.statusCode != '200') {
			ErrLog("Status Code Error. (finishTask) STATUS:" + res.statusCode);
			//重新发送请求
			http.get(urlFinish, function(res) {
				if (res.statusCode != '200') {
					ErrLog("Status Code Error. (finishTask) STATUS:" + res.statusCode);
				} else {
					InfoLog("Status Code Normal. (finishTask) STATUS:" + res.statusCode);
				}
			});

			return;
		}
		InfoLog("Received normal response. (finishTask)");
		res.setEncoding('utf8');
		res.on('data', function(chunk) {
			//消息回答
			InfoLog('BODY: ' + chunk);
			InfoLog("--------------------------------------------------------------------------------------\n\n");
		});

	}).on('error', function(e) {
		ErrLog("Received error response. (finishTask) message:" + e.message);
		InfoLog("--------------------------------------------------------------------------------------\n\n");
	});
}

//打包失败通知
function sendLoseTask(taskId) {

	var urlLose = WEB_REQUEST_URL + "?action=loseTask";
	urlLose = urlLose + "&platform=" + PLATFORM;
	urlLose = urlLose + "&taskid=" + taskId;
	urlLose = urlLose + "&batchno=" + BatchNo;
	urlLose = urlLose + "&serveraddr=" + IP_ADDR;
	InfoLog("Send lose task message.");
	InfoLog(urlLose);

	//发送打包失败的消息
	http.get(urlLose, function(res) {
		if (res.statusCode != '200') {
			ErrLog("Status Code Error. (loseTask) STATUS:" + res.statusCode);
			return;
		}
		InfoLog("Received normal response. (loseTask)");
		res.setEncoding('utf8');
		res.on('data', function(chunk) {
			//消息回答
			InfoLog('BODY: ' + chunk);
			InfoLog("--------------------------------------------------------------------------------------\n\n");
		});

	}).on('error', function(e) {
		ErrLog("Received error response. (loseTask) message:" + e.message);
		InfoLog("--------------------------------------------------------------------------------------\n\n");
	});
}

//准备打包文件
function prepareGamefile(taskId) {
	//	InfoLog("Run shell ==> " + SHELL_FILE_PATH);
	InfoLog("Start packing tasks. (id:" + taskId + ")");

	var strAdFile = "";
	if (typeof(AdID) != "undefined" && AdID != "" && AdID != "0") {

		strAdFile = '{' + '\n' +
			'	"adid": ' + AdID + ',' + '\n' +
			'	"adurl": [' + '\n' +
			'	{' + '\n' +
			'		"url": "' + config.AD_SERVER_URL_1 + '"\n' +
			'	},' + '\n' +
			' 	{' + '\n' +
			'		"url": "' + config.AD_SERVER_URL_2 + '"\n' +
			'	}' + '\n' +
			'	],' + '\n' +
			'	"extra": "info"' + '\n' +
			'}';
		InfoLog("AD文件：" + strAdFile);
	}

	// #定义LOG_FILE

	var Batch_path = path.join(LOG_PATH, BatchNo);
	if (!fs.existsSync(Batch_path)) {
		fs.mkdirpSync(Batch_path);
	}

	LOG_FILE = path.join(LOG_PATH, BatchNo, taskId + '.log');

	fs.writeFileSync(LOG_FILE, "日志开始", 'utf8', function(err) {
		if (err) {
			return ErrLog(err);
		}
		InfoLog("建立日志文件完成");
	});

	fs.chmodSync(LOG_FILE, 0644, function(err) {
		if (err) {
			return ErrLog(err);
		}
		InfoLog("修改日志文件权限完成");
	});

	// #打印传入参数

	var date = new Date();
	var startTime = date.toLocaleDateString() + " " + date.toLocaleTimeString();
	PkgLog("开始: " + startTime + "\n参数：" + ' processNO: ' + processNO + ' GameID: ' + GameID + ' Version: ' + Version + ' Channel: ' + Channel + ' ChannelVersion: ' + ChannelVersion + ' BatchNo: ' + BatchNo + ' taskId: ' + taskId + ' IconID: ' + IconID + ' SdkVer: ' + SdkVer + ' CompileMode: ' + CompileMode + ' SignKey: ' + SignKey + "\n工作目录：" + TMP_PATH);

	// #检测游戏项目工程是否存在

	var GAME_FILE = path.join(SHARE_PATH, 'game_file', GameID, Version, GAME_FILE_NAME);
	PkgLog("检查游戏工程文件：" + GAME_FILE);

	if (!fs.existsSync(GAME_FILE)) {
		PkgLog("错误：游戏包文件没有找到.");
		return 'err';
	} else {

		// #清理并生成工作目录

		TMP_FOLDER = path.join(TMP_PATH, taskId);
		PkgLog("打包临时文件夹：" + TMP_FOLDER);

		// #清空临时目录

		if (fs.existsSync(TMP_FOLDER)) {
			PkgLog("清空临时文件夹。");
			fs.removeSync(TMP_FOLDER, function(err) {});
		}
		fs.mkdirsSync(path.join(TMP_FOLDER, 'icon'));

		// #复制配置文件至工作目录内

		PkgLog("复制Config文件 " + path.join(SHARE_PATH, 'config', GameID, Channel) + TMP_FOLDER);

		fsSync.copy(path.join(SHARE_PATH, 'config', GameID, Channel), TMP_FOLDER, {
			force: true
		});

		// #复制SDK框架文件至工作目录内,通过-s参数设置SDK框架版本。

		var TYPE_SDK_DIR = path.join(SHARE_PATH, 'SDK/Type_SDK', SdkVer, 'lib');
		PkgLog("复制SDK框架文件 " + TYPE_SDK_DIR);

		if (!fs.existsSync(TYPE_SDK_DIR)) {
			PkgLog("错误: 所指定的SDK框架版本文件夹不存在.\n" + TYPE_SDK_DIR);
			return 'err';
		} else {

			fsSync.copy(TYPE_SDK_DIR, TMP_FOLDER, {
				force: true
			});

			// #复制渠道SDK接入逻辑、渠道SDK库至工作目录内,通过-w参数设置渠道SDK版本，接入代码和渠道版本一同保存。


			var CHANNEL_SDK_DIR = path.join(SHARE_PATH, 'SDK/Channel_SDK', Channel, ChannelVersion);
			PkgLog("复制渠道SDK文件 " + CHANNEL_SDK_DIR);

			if (!fs.existsSync(CHANNEL_SDK_DIR)) {
				PkgLog("错误: 所指定的渠道SDK版本文件夹不存在.\n" + CHANNEL_SDK_DIR);
				return 'err';
			} else {

				TravelCopy(CHANNEL_SDK_DIR, TMP_FOLDER);

				// #复制渠道准备脚本文件至工作目录内，如对个别渠道有特殊处理，可修改各个渠道的准备脚本以达到目的。

				var CHANNEL_SHELL_PATH = path.join(SHARE_PATH, 'SDK/Channel_Shell', Channel);

				if (fs.existsSync(CHANNEL_SHELL_PATH)) {
					PkgLog("发现准备脚本，复制该渠道准备脚本至工作目录.\n" + CHANNEL_SHELL_PATH);
					fsSync.copy(CHANNEL_SHELL_PATH, TMP_FOLDER, {
						force: true
					});
				}

				// #复制指定的图标文件至工作目录内，可制作多套图标资源，通过-i参数指定使用图标。

				fsSync.copy(path.join(SHARE_PATH, 'icon', GameID, IconID), path.join(TMP_FOLDER, 'icon'), {
					force: true
				});
				fs.removeSync(path.join(TMP_FOLDER, 'icon/app_icon.png'));
				if (fs.existsSync(path.join(TMP_FOLDER, 'icon/512'))) {
						fs.removeSync(path.join(TMP_FOLDER, 'icon/512'));
					};

				// #如果扩展配置中存在对应任务ID编号的AndroidManifest.xml文件则替换主项目，该文件由打包控制器在发布任务前，更具游戏、基础项目、渠道项目的AndroidManifest.xm整合生成，详见打包控制器源代码。如不需要自动生成，可注销此段替换逻辑，并将手动修改的AndroidManifest.xml文件存放在各个渠道目录下。

				//NEW_ANDROIDMANIFEST_PATH = path.join(SHARE_PATH, 'SDK/Extra_Config/Manifest', taskId, 'AndroidManifest.xml');

				//if (fs.existsSync(NEW_ANDROIDMANIFEST_PATH)) {
				//	PkgLog("发现AndroidManifest文件，复制该文件至工作目录.\n" + NEW_ANDROIDMANIFEST_PATH);
				//	fsSync.copy(NEW_ANDROIDMANIFEST_PATH, path.join(TMP_FOLDER, 'MainActivity/AndroidManifest.xml'), {
				//		force: true
				//	});
				//}

				// #创建正在打包标识文件，防止启动新任务

				FLAG_FILE = path.join(TMP_PATH, 'PACKAGING_FLAG_' + processNO);

				fs.writeFileSync(FLAG_FILE, "", 'utf8', function(err) {});

				fs.chmodSync(FLAG_FILE, 0644, function(err) {});

				AppendText(FLAG_FILE, "StartTime：" + startTime + "\n");
				AppendText(FLAG_FILE, "Game: " + GameID + "\n");
				AppendText(FLAG_FILE, "Channel：" + Channel + "\n");
				AppendText(FLAG_FILE, "TaskID：" + taskId);

				// #解压游戏项目工程至工作目录内

				PkgLog("正在解压游戏包.");

				var GameZip = new AdmZip(path.join(SHARE_PATH, 'game_file', GameID, Version, GAME_FILE_NAME));

				GameZip.extractAllTo(TMP_FOLDER, true);

				// #修正可能出现的目录大小写问题

				if (os.platform() == 'linux') {
					if (fs.existsSync(path.join(TMP_FOLDER, 'game'))) {
						fsSync.copy(path.join(TMP_FOLDER, 'game'), path.join(TMP_FOLDER, 'Game'), {
							force: true
						});
						fs.removeSync(path.join(TMP_FOLDER, 'game'));
					};
				}

				// #检查游戏项目解压复制结果

				if (!fs.existsSync(path.join(TMP_FOLDER, 'Game'))) {
					PkgLog("错误：游戏包里缺少Game文件夹.\n" + NEW_ANDROIDMANIFEST_PATH);
					return 'err';
				} else {
					PkgLog("文件解压完成.");

					// #复制version.properties文件至工作目录，其定义了APK的version_name和version_code，注意androidmainfest.xml定义单version_name和version_code有更高的优先级。

					PkgLog("复制version.properties文件");
					fsSync.copy(path.join(SHARE_PATH, 'game_file', GameID, Version, 'version.properties'), path.join(TMP_FOLDER, 'Game/version.properties'), {
						force: true
					});
					if (!fs.existsSync(path.join(TMP_FOLDER, 'Game/version.properties'))) {
						PkgLog("复制version.properties文件失败.");
						return 'err';
					}

					// #复制游戏项目内资源文件至编译工程项目内，标记项目为lib工程项目，并使用android update命令对编译项目生成build.xml文件。

					PkgLog("复制assets文件");
					var Assets_path = path.join(TMP_FOLDER, 'MainActivity/assets');
					if (!fs.existsSync(Assets_path)) {
						fs.mkdirpSync(Assets_path);
					}
					fsSync.copy(Assets_path, path.join(TMP_FOLDER, 'Game/assets'), {
						force: true
					});
				}
			}
		}
	}
}

function preparePkgfile(taskId) {
	if (!fs.existsSync(path.join(TMP_FOLDER, 'MainActivity/assets'))) {
		fs.mkdirpSync(path.join(TMP_FOLDER, 'MainActivity/assets'));
	}
	if (!fs.existsSync(path.join(TMP_FOLDER, 'MainActivity/res'))) {
		fs.mkdirpSync(path.join(TMP_FOLDER, 'MainActivity/res'));
	}
	if (!fs.existsSync(path.join(TMP_FOLDER, 'MainActivity/src'))) {
		fs.mkdirpSync(path.join(TMP_FOLDER, 'MainActivity/src'));
	}
	if (!fs.existsSync(path.join(TMP_FOLDER, 'MainActivity/libs'))) {
		fs.mkdirpSync(path.join(TMP_FOLDER, 'MainActivity/libs'));
	}

	PkgLog("移动version.properties文件");
	fsSync.copy(path.join(TMP_FOLDER, 'Game/version.properties'), path.join(TMP_FOLDER, "MainActivity/version.properties"), {
		force: true
	});
	fs.removeSync(path.join(TMP_FOLDER, 'Game/version.properties'));

	PkgLog("移动CPSettings.txt文件");
	fsSync.copy(path.join(TMP_FOLDER, "CPSettings.txt"), path.join(TMP_FOLDER, "Game/assets/CPSettings.txt"), {
		force: true
	});
	fs.removeSync(path.join(TMP_FOLDER, 'CPSettings.txt'));

	PkgLog("复制local.properties文件");
	fsSync.copy(path.join(TMP_FOLDER, 'local.properties'), path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/local.properties'), {
		force: true
	});
	fsSync.copy(path.join(TMP_FOLDER, 'local.properties'), path.join(TMP_FOLDER, "Game/local.properties"), {
		force: true
	});
		fsSync.copy(path.join(TMP_FOLDER, 'local.properties'), path.join(TMP_FOLDER, "MainActivity/local.properties"), {
		force: true
	});
	fs.removeSync(path.join(TMP_FOLDER, 'local.properties'));

	//if (fs.existsSync(path.join(TMP_FOLDER, 'MainActivity/libs/android-support-v4.jar'))) {
	//	fsSync.copy(path.join(TMP_FOLDER, 'MainActivity/libs/android-support-v4.jar'), path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/libs/android-support-v4.jar'), {
	//		force: true
	//	});
	//}

	//if (fs.existsSync(path.join(TMP_FOLDER, 'ZhuoyiPaySDK_library/libs/android-support-v4.jar'))) {
	//	fsSync.copy(path.join(TMP_FOLDER, 'ZhuoyiPaySDK_library/libs/android-support-v4.jar'), path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/libs/android-support-v4.jar'), {
	//		force: true
	//	});
	//}

	PkgLog('拷贝unity-classes.jar文件');
	//	fs.removeSync(path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/libs/unity-classes.jar'));
	fsSync.copy(path.join(TMP_FOLDER, 'Game/libs/unity-classes.jar'), path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/libs/unity-classes.jar'), {
		force: true
	});
	


	PkgLog('拷贝strings.xml文件');
	fsSync.copy(path.join(TMP_FOLDER, 'MainActivity/res/values/strings.xml'), path.join(TMP_FOLDER, 'MainActivity/res/values/app_strings.xml'), {
		force: true
	});
	fs.removeSync(path.join(TMP_FOLDER, 'MainActivity/res/values/strings.xml'));

	PkgLog('拷贝icon文件');
	fsSync.copy(path.join(TMP_FOLDER, 'icon/'), path.join(TMP_FOLDER, 'TypeSDKBaseLibrary/res'), {
		force: true
	});

	PkgLog('拷贝app_icon文件');
	if (fs.existsSync(path.join(TMP_FOLDER, 'icon/drawable/app_icon.png'))) {
		fsSync.copy(path.join(TMP_FOLDER, 'icon'), path.join(TMP_FOLDER, 'Game/res'), {
			force: true
		});
	}
}

function pkgFinsh(taskId) {
	// #打包成功，将签名成功的APK复制到统一输出目录中。

	PkgLog("拷贝apk文件.");

	var APK_NAME = GameID + '_' + Version.substring(0, Version.length - 18) + '_' + Channel + ChannelVersion + '_' + taskId;
	var OUTPUT_PATH = path.join(SHARE_PATH, 'output/apk', GameID, BatchNo);
	var APK_SAVEPATH = path.join(OUTPUT_PATH, APK_NAME + '.apk');
	PkgLog("apk保存路径：" + APK_SAVEPATH);
	PkgLog("log信息：" + path.join(SHARE_PATH, 'output/logs', BatchNo, taskId + '.log'));
	if (!fs.existsSync(OUTPUT_PATH)) {
		fs.mkdirpSync(OUTPUT_PATH);
	}

	Travel(path.join(TMP_FOLDER, 'Game/bin'), function(pathname) {
		if (new RegExp(CompileMode + '.apk').test(pathname)) {
			fsSync.copy(pathname, APK_SAVEPATH, {
				force: true
			});
			fs.removeSync(pathname);
			if (fs.existsSync(APK_SAVEPATH)) {
				PkgLog("apk文件拷贝失败.");
			} else {
				PkgLog("apk文件拷贝成功.");
			}
		}
	});

	// #保存release.log至统一输出目录中

	// #如果是release打包模式，则清理工作目录。

	// #如果是其他模式，则保留工作目录，并输出至日志打印。

	fsSync.copy(path.join(TMP_FOLDER, 'Game/release.log'), path.join(SHARE_PATH, 'output/logs', BatchNo, taskId + '_release.log'), {
		force: true
	});
	fs.removeSync(path.join(TMP_FOLDER, 'Game/release.log'));
	if (CompileMode == "release") {
		PkgLog("删除打包临时文件夹");
		fs.removeSync(TMP_FOLDER);
		fs.removeSync(path.join(SHARE_PATH, 'SDK/Extra_Config/Manifest', taskId));
	} else {
		PkgLog("Debug模式时保留打包临时文件夹。文件路径：【" + TMP_FOLDER + "】");
	}

	PkgLog("打包成功");

	var date = new Date();
	var endTimeStamp = date.toLocaleDateString() + " " + date.toLocaleTimeString();
	PkgLog("完成: " + endTimeStamp);


	// #输出release日志至打印日志中。
	// #删除release日志

	PkgLog(" ----------------------- Release log ----------------------- : ");
	var rlog = fs.readFileSync(path.join(SHARE_PATH, 'output/logs', BatchNo, taskId + '_release.log'))
	if (os.platform() != 'win32') {
		PkgLog(rlog);
	} else {
		var buffer = new Buffer(rlog);
		var gbk_rlog = iconv.decode(buffer, 'gbk');
		PkgLog(gbk_rlog);
	}
	fs.removeSync(path.join(SHARE_PATH, 'output/logs', BatchNo, taskId + '_release.log'));

}
//打包处理 New
function callShell(taskId, callback) {
	try {

		var preGamefile = prepareGamefile(taskId);
	} catch (e) {
		var preGamefile = 'err';
		ErrLog("Package error:" + e);
		PkgLog(e);
		fs.removeSync(FLAG_FILE);
		callback(taskId, "error");
	}


	AppendText(path.join(TMP_FOLDER, 'MainActivity/project.properties'), "android.library=true");

	if (preGamefile != 'err') {
		fsSync.copy(path.join(TMP_FOLDER, 'MainActivity/build.xml'), path.join(TMP_FOLDER, 'Game/build.xml'), {
		    force: true
	  });
	  fsSync.copy(path.join(TMP_FOLDER, 'MainActivity/custom_rules.xml'), path.join(TMP_FOLDER, 'Game/custom_rules.xml'), {
		    force: true
	  });
	  fs.removeSync(path.join(TMP_FOLDER, 'MainActivity/custom_rules.xml'));
	  fsSync.copy(path.join(TMP_FOLDER, 'MainActivity/replace_key.xml'), path.join(TMP_FOLDER, 'Game/replace_key.xml'), {
		    force: true
	  });
    fs.removeSync(path.join(TMP_FOLDER, 'MainActivity/replace_key.xml'));
		PkgLog('开始执行通用打包');

		try {

			preparePkgfile(taskId);
		} catch (e) {
			ErrLog("Package error:" + e);
			PkgLog(e);
			fs.removeSync(FLAG_FILE);
			callback(taskId, "error");
		}

		var buildPath = path.join(TMP_FOLDER, 'Game');

		PkgLog("开始进行编译，请稍等...");
		PkgLog(ANT_BIN + ' ' + CompileMode + ' -l ' + path.join(buildPath, 'release.log'));

		setTimeout(function() {
			child_process.exec(
				ANT_BIN + ' ' + CompileMode + ' -l ' + path.join(buildPath, 'release.log'), {
					encoding: 'utf8',
					cwd: buildPath
				},
				function(err, stdout, stderr) {
					if (err) {
						ErrLog("打包失败：ant命令执行失败.\n" + err);
						ErrLog("\nOutput package log information.\n" + stdout);
						PkgLog("打包失败：ant命令执行失败.\n" + err);
						PkgLog("\nOutput package log information.\n" + stdout);
						if (fs.existsSync(path.join(TMP_FOLDER, 'Game/release.log'))) {
							PkgLog(" ----------------------- Release log ----------------------- : ");
							var rlog = fs.readFileSync(path.join(TMP_FOLDER, 'Game/release.log'))
							if (os.platform() != 'win32') {
								PkgLog(rlog);
							} else {
								var buffer = new Buffer(rlog);
								var gbk_rlog = iconv.decode(buffer, 'gbk');
								PkgLog(gbk_rlog);
							}
						}
						fs.removeSync(FLAG_FILE);
						callback(taskId, "error");
					} else {
						if (fs.existsSync(path.join(TMP_FOLDER, 'Game/bin'))) {
							try {
								pkgFinsh(taskId);
							} catch (e) {
								ErrLog("Package error:" + e);
								PkgLog(e);
								fs.removeSync(FLAG_FILE);
								callback(taskId, "error");
							}
							PkgLog("编译成功");
							InfoLog("\nOutput package log information.\n" + stdout);
							fs.removeSync(FLAG_FILE);
							callback(taskId, "normal");
						} else {
							//shell执行失败
							ErrLog("打包失败：未生成APK文件。");
							ErrLog("\nOutput package log information.\n" + stdout);
							PkgLog("打包失败：未生成APK文件。");
							PkgLog("\nOutput package log information.\n" + stdout);
							fs.removeSync(FLAG_FILE);
							callback(taskId, "error");
						}
					}
				});
		}, 5000);
		// }
	} else {
		PkgLog("准备打包文件失败");
		if (fs.existsSync(FLAG_FILE)) {
			fs.removeSync(FLAG_FILE);
		}
		callback(taskId, "error");
	}

}

function ErrLog(message) {
	msg = '[process No:' + processNO + '] ' + message;
	console.error(msg);
	logger.error(msg);
}

function WarnLog(message) {
	msg = '[process No:' + processNO + '] ' + message;
	console.warn(msg);
	logger.warn(msg);
}

function InfoLog(message) {
	msg = '[process No:' + processNO + '] ' + message;
	console.log(msg);
	logger.info(msg);
}

function PkgLog(message) {
	AppendText(LOG_FILE, message);
}

function AppendText(file, message) {
	fs.appendFileSync(file, message + "\n", 'utf8', function(err) {
		if (err) throw err;
	});
}

function TravelCopy(src, dst) {
	fs.readdirSync(src).forEach(function(file) {
		var srcpath = path.join(src, file);
		var dstpath = path.join(dst, file);

		if (fs.statSync(srcpath).isDirectory()) {
			if (!fs.existsSync(dstpath)) {
				fs.mkdirsSync(dstpath);
			}
			TravelCopy(srcpath, dstpath);
		} else {
			fsSync.copy(srcpath, dstpath, {
				force: true
			});
		}
	});
}

function Travel(dir, callback) {
	fs.readdirSync(dir).forEach(function(file) {
		var pathname = path.join(dir, file);

		if (fs.statSync(pathname).isDirectory()) {
			Travel(pathname, callback);
		} else {
			callback(pathname);
		}
	});
}