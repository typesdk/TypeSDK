#TypeSDK 手游渠道一键接入 剑锋所指纵横四海
***
##TypeSDK手游渠道SDK聚合/融合工具说明
  手游行业渠道SDK是指发行渠道提供的工具包，用来连接渠道和游戏开发商，提供基本的用户登录和支付的功能。当游戏开发商完成游戏开发后 ，就应准备接入渠道SDK在发行渠道发行上架架游戏。通常一款游戏会选择在多家渠道发布，以保障市场覆盖率。这也就意味这开发商需要花费数个月的时间来完成大量渠道SDK接入工作。TypeSDK作为聚合渠道SDK接入工具，可以简化渠道SDK接入工作，将数个月的接入工作缩短至1天。节省开发商人力和时间成本，并凭借TypeSDK成熟产品质量降低游戏上线技术风险。本产品完全免费开源，如您需要更好的VIP专人技术支持及游戏上线的咨询，可联系TypeSDK商务索取价格及服务内容。


##TypeSDK手游国内/海外自发现定制SDK说明
  除了免费开源的国内渠道SDK聚合/融合工具外。TypeSDK同时提供手机游戏自发行渠道SDK产品，游戏开发商和发行商可以利用TypeSDK渠道聚合接入工具进行高效渠道SDK接入，也可以使用手游国内/海外自发现定制SDK，进行国内和海外推广发行。自建渠道包括国内版本和海外版本。国内版本支持自有用户系统、手机绑定、游客、支付宝、微信、银联支付。海外版本支持自有用户系统、Facebook登录、google pay支付。并可更具您的需要进行定制二次开发。详细价格服务请联系TypeSDK商务。

##TypeSDK官网
http://www.typesdk.com

##TypeSDK文档
http://www.typesdk.com/document
***
##项目目录结构说明
###TypeSDK_Clinet 
客户端接入资源（不包含源码，源码在TypeSDK_Package中)

    └ Android 原生Android接入资源。
    └ Unity Unity3D项目接入资源。
接入资源包含：Android层聚合接入框架，Unity层接入框架和TypeSDK定制测试渠道。为了各类游戏开发引擎能够直接运行，已将Android代码编译为jar包。

###TypeSDK_Manager 
打包工具Web端

    └ src 打包工具源代码C#
    └ www 打包工具编译后资源
    └ TypeSDK.sql 打包工具SQLServer数据建库脚本
源代码供二次开发使用，如需直接安装，可至官网下载直接运行安装的版本。

###TypeSDK_Package 
打包工具Android编译服务（客户端所有源码）

    └ packge 打包调度服务Nodejs
    └ share 代码资源及输出APK
         └ config 游戏渠道配置文件保存目录
         └ game_file 上传游戏项目工程保存目录
         └ icon 游戏渠道图标保存目录
         └ output 打包结果APK及日志保存目录
         └ SDK TypeSDK基础代码及渠道接入代码保存目录（包含所有聚合接入框架、渠道接入源代码、渠道SDK）
         └ signkey APK签名密钥保存目录
    └ software 环境工具包
         └ android-sdk-windows AndroidSDK目录，需要使用SDK Manager进行下载安装API 25及以上版本。
         └ ant Apache ant目录
         └ jdk1.8.0_121 JDK目录
         └ nodejs  NodeJS目录

###TypeSDK_Server 
SDK服务端（服务端所有源码）

    └ SdkServer_free_git 服务端项目文件
    └ Tools 支持环境工具包