<%@ Page Title="首页" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="Default.aspx.cs" Inherits="SDKPackage._Default" %>
<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><i class="fa fa-flag"> 欢迎</i></h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <p>您可以在TypeSDK官网发现更多精彩内容。我们为您准备了：TypeSDK相关专业文档和视频教程、渠道SDK接入经验分享、游戏上线指导、各类实用工具、技术支持服务、二次开发指导、版号公关服务。
                    </p>
                    <a href="http://typesdk.com" target="_block" class="btn btn-primary btn-lg">进入TypeSDK官网</a>
                </div>
            </div>
        </div>
    </div>

        <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><i class="fa fa-book"> 使用帮助</i></h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <p>请点击下面按钮移步浏览我们的平台使用说明：</p>
                    <div class="row"><div class="col-md-3 col-sm-6 col-xs-12" style=" float: none; margin: 0 auto;"><a class="btn btn-success btn-block" target="_block" href="http://www.typesdk.com/docs/typesdk_install/manage_guilde/">TypeSDK打包平台使用说明</a></div></div>
                    
                    <p>或直接下载我们为您准备的Unity Demo源代码:</p>
                    <div class="row"><div class="col-md-3 col-sm-6 col-xs-12" style=" float: none; margin: 0 auto;"><a class="btn btn-success btn-block" target="_block" href="https://code.csdn.net/typesdk_code/typesdk_client/tree/master">Unity Demo 源代码</a></div></div>
                    
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-4 col-sm-6 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><i class="fa fa-download"> 下载包</i></h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <p>下载并管理您的APK、ipa包</p>
                    <a class="btn btn-default btn-block" href="/DownLoad/DownLoadAndroidPackage.aspx">Android包</a>
                    <a class="btn btn-default btn-block" href="/DownLoad/DownLoadIOSPackage.aspx">IOS包</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-sm-6 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><i class="fa fa-archive"> 打包操作</i></h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <p>进行渠道打包操作</p>
                    <a class="btn btn-default btn-block" href="/PJPackage/SelectGame.aspx">发起打包任务</a>
                    <a class="btn btn-default btn-block" href="/PJPackage/PackageInfoList.aspx">查询打包任务</a>
<%--                    <a class="btn btn-default btn-block" href="/AdPackage/SelectPackage.aspx">发起广告分包任务</a>
                    <a href="#">什么是广告分包？</a>--%>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-sm-6 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2><i class="fa fa-gears"> 配置管理</i></h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <p>管理游戏、渠道配置、签名、平台用户。</p>
                    <a class="btn btn-default btn-block" href="/GameConfig/GameList.aspx">管理游戏</a>
                    <a class="btn btn-default btn-block" href="/PJConfig/Platform.aspx">管理渠道参数、SDK版本</a>
                    <a class="btn btn-default btn-block" href="/GameIcon/GameIconList.aspx">管理图标</a>
                    <a class="btn btn-default btn-block" href="/PJConfig/KeyConfig.aspx">管理签名密钥</a>
                </div>
            </div>
        </div>
    </div>
</asp:Content>
