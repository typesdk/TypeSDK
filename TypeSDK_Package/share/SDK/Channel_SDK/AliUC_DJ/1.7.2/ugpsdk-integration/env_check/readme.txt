使用说明：

脚本帮助命令  python env_check.py -h
---------------------------------------------
Usage: env_check.py [options]

Options:
  -h, --help         show this help message and exit
  -l LIB, --lib=LIB  指定要检查的lib的相对目录，默认为[../ugpsdk-integration/libs]
  -a, --alipay       忽略对支付宝支付配置进行校验!
  -w, --wechat       忽略对微信支付配置进行校验!
  -m, --cmmm         忽略对移动MM支付配置进行校验!
  -b, --cmbase       忽略对移动基地支付配置进行校验!
  -u, --cu           忽略对联通运营商支付配置进行校验!
  -t, --ct           忽略对电信运营商支付配置进行校验!
  -o, --operator     忽略对所有运营商支付配置进行校验!
  -r, --release      用于发布包解压后检查配置, 即默认检查本工程的lib目录

一、打包前检查：使用上述命令：
   比如:默认检查so和jar是在"../ugpsdk-integration/libs"目录，如果integration工程与目录工程不在同级目录，请指定即可
           完整接入，在目标工程根目录直接运行 env_check.py即可()
           忽略移动MM检查：env_check.py -m

 二、检查apk包
 1. 解apk包命令如下 
   apktool.jar d -f 你的应用.apk
 2. 将脚本放入apk解包的根目录，并在该目录下运行脚本：python env_check.py -r


 