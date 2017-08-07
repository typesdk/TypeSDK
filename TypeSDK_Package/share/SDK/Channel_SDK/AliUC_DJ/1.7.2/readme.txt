从uc开发平台下载的UCGameInfo.ini文件置于游戏工程的assets根目录下。
在九游游戏平台进行申请时，然后下载平台生成的支付信息文件，即“pay.png”文件, 并将其放置游戏工程的assets/UCPaySDK目录下（注意大小写）。
1. 中国电信：
如果需要接入中国电信SDK短信支付，需要到电信游戏平台进行申报，然后下载系统生成的配置文件，即“feeInfo.dat”, 并将其放置游戏工程的assets目录，才能正常进行中国电信单机sdk短信支付。
2. 中国联通：
如果需要接入中国联通SDK短信支付，需要到联通沃商店平台进行申报，然后得到包含计费文件的jar包“Multimode_UniPay_base.jar”并替换“ugpsdk-integration”工程中对应的jar包即可。
3. 中国移动“和游戏”（南京基地）：
在ugpsdk-integration工程的assets目录下包含移动南京基地支付sdk登录及计费相关数据文件。游戏开发者需要将该目录下所有的文件拷贝到游戏工程的assets（因为打包不会将lib工程的assets打进去），该目录下提供了测试使用的Charge.xml、ConsumeCodeInfo.xml，待游戏提交至移动平台后，请替换该文件内容（替换成游戏实际业务及计费信息）。
注意：该两个文件是计费的核心数据，请勿擅自修改其内容，否则计费失败。
