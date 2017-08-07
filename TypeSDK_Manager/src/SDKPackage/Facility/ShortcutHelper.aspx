<%@ Page Title="工具管理" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true" CodeBehind="ShortcutHelper.aspx.cs" Inherits="SDKPackage.Facility.ShortcutHelper" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <style type="text/css">
        #AndroidHepler input {
            margin-right: 20px;
        }
        .mb {
        margin-top:20px;}
        .clear {
        clear:both;}
        
    </style>
    <script type="text/javascript">
        $(function () {
            $("#MainContent_ButtonAPKRename").click(function () {
                var txt = $("#MainContent_txtAPKName").val();
                if (txt == null || txt == undefined || txt == "")
                {
                    alert("apk name 不能为空！");
                    return false;
                }
            });
        })
    </script>
    <div class="row">
        <h3>打包平台工具箱</h3>
        <ul id="myTab" class="nav nav-tabs">
            <li class="active">
                <a href="#AndroidHepler" data-toggle="tab">Android工具</a>
            </li>
            <li><a href="#IOSHepler" data-toggle="tab">iOS工具</a></li>
        </ul>
        <div id="myTabContent" class="tab-content">
            <br />
            <div class="tab-pane fade in active" id="AndroidHepler">
                <asp:Button ID="ButtonUpdateSVN" runat="server" Text="更新SDK代码" CssClass="btn btn-info" OnClick="ButtonUpdateSVN_Click" OnClientClick="return confirm('确定要更新svn吗？')" />
                <a class="btn btn-info " data-toggle="modal" data-target="#diva_apkRename" id="a_apkRename" >apk重新签名</a>
            </div>
            <div class="tab-pane fade" id="IOSHepler">
                <asp:Button ID="ButtonUpdatePlist" runat="server" Text="Plist处理" CssClass="btn btn-info" />
            </div>
        </div>
    </div>
    <!--LeBianandroid渠道列表start-->
    <div class="modal fade" id="diva_apkRename" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title">apk重新签名</h2>
                </div>
                <!--内容-->
                <div class="col-md-12">
                    <div class="mb">
                        <label for="txtPlatformName" class="col-sm-3 control-label">apk文件名</label>
                        <div class="col-sm-9">
                            <asp:TextBox ID="txtAPKName" runat="server" CssClass="form-control" placeholder="请输入 apk name"></asp:TextBox>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="mb">
                        <label for="txtPlatformName" class="col-sm-3 control-label">sign</label>
                        <div class="col-sm-9">
                            <asp:TextBox ID="txtSign" runat="server" CssClass="form-control" placeholder="签名（可空 默认typesdk）"></asp:TextBox>
                        </div>
                        <div class="clear"></div>
                    </div>
                </div>
                <!--内容-->
                <div class="modal-footer" id="modalfooter2">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonAPKRename" runat="server" Text="确定" CssClass="btn btn-info" OnClick="ButtonAPKRename_Click" />
                </div>
            </div>
        </div>
    </div>
    <!--LeBianandroid渠道列表end-->
    <script type="text/javascript">
        function openWindowSelf() {
            var url = "UpdateSVNLog.aspx";
            var width = 800;
            var height = 600;
            var left = (window.screen.availWidth - width) / 2;
            var top = (window.screen.availHeight - height) / 2;
            var openthiswin = window.open(url, "SVN更新日志", 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,left=' + left + ',top=' + top + ',width=' + width + ',height=' + height);
            if (openthiswin != null) openthiswin.focus();
        }
        function openWindowSelf2(logpath) {
            var url = "log.aspx?logpath=" + logpath;
            var width = 800;
            var height = 600;
            var left = (window.screen.availWidth - width) / 2;
            var top = (window.screen.availHeight - height) / 2;
            var openthiswin = window.open(url, "日志详情", 'toolbar=no,location=no,directories=no,status=yes,menubar=no,scrollbars=yes,resizable=yes,left=' + left + ',top=' + top + ',width=' + width + ',height=' + height);
            if (openthiswin != null) openthiswin.focus();
        }
        
    </script>
</asp:Content>
