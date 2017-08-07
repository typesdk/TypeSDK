<%@ Page Title="任务进度" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="SelectPackageInfo.aspx.cs" Inherits="SDKPackage.PJPackage.SelectPackageInfo" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">
    <asp:ScriptManager runat="server">

    </asp:ScriptManager>
    <script type="text/javascript">
        function openfile(taskid, createtaskid, systemname) {
            var str_href = "sdkPackageLog.aspx?taskid=" + taskid + "&createtaskid=" + createtaskid + "&systemname=" + systemname;
            var w = $(window).width();
            var h = $(window).height();
            window.open(str_href, '打包日志', 'height=' + h + ',width=' + w + ',toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no');
        }
        function packageAgain(id, systemname) {
            $.ajax({
                contentType: "application/json",
                async: false,
                url: "/WS/WSNativeWeb.asmx/PackageAgain",
                data: "{id:'" + id + "',systemname:'" + systemname + "'}",
                type: "POST",
                dataType: "json", success: function () {
                    window.location.reload();
                }
            });
        }
    </script>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>开始打包</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div id="wizard" class="form_wizard wizard_horizontal">
                    <ul class="wizard_steps anchor">
                        <li>
                          <a href="#step-1" class="done" isdone="1" rel="1">
                            <span class="step_no">1</span>
                            <span class="step_descr">
                                              <small>选择游戏</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-2" class="done" isdone="1" rel="2">
                            <span class="step_no">2</span>
                            <span class="step_descr">
                                              <small>选择平台</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-3" class="done" isdone="1" rel="3">
                            <span class="step_no">3</span>
                            <span class="step_descr">
                                              <small>选择项目</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-4" class="done" isdone="0" rel="4">
                            <span class="step_no">4</span>
                            <span class="step_descr">
                                              <small>选择渠道</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-5" class="done" isdone="0" rel="5">
                            <span class="step_no">5</span>
                            <span class="step_descr">
                                              <small>确认任务</small>
                                          </span>
                          </a>
                        </li>
                        <li>
                          <a href="#step-6" class="selected" isdone="0" rel="6">
                            <span class="step_no">6</span>
                            <span class="step_descr">
                                              <small>开始打包</small>
                                          </span>
                          </a>
                        </li>
                      </ul>
                    </div>
                    <hr />
                    <div class="form-inline navbar-left">
                        <div class="form-group">
                            <span class="name">游戏:</span>
                            <span class="value text-primary"><%= gameDisplayName %></span>
                        </div>
                        <div class="form-group">
                            <span class="name">平台:</span>
                            <span class="value text-primary"><%= platform %></span>
                        </div>
                        <div class="form-group">
                            <span class="name">版本:</span>
                            <span class="value text-primary"><%= gameversion %></span>
                        </div>
                    </div>
                    <asp:ListView ID="GamePlaceList" runat="server">

                        <EmptyDataTemplate>
                            <table runat="server" style="">
                                <tr>
                                    <td>当前没有任务。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>

                        <ItemTemplate>
                            <tr>
                                <td><%#Eval("RecID") %></td>
                                <td>
                                    <%#Eval("PlugInID").ToString()=="1"?"乐变"+Eval("PlatformDisplayName")+"渠道": Eval("PlatformDisplayName") %></td>

                                <td><%#Eval("PlugInID").ToString()=="1"?Eval("PlatformName").ToString()+"_LeBian":Eval("PlatformName").ToString()%> <%#!string.IsNullOrEmpty(Eval("adname").ToString())?"("+Eval("adname").ToString()+")":"" %>

                                <td><span style='<%#platform=="Android"?"": "display:none"%>'><%#isencryption=="0"?"N":"Y" %></span></td>

                                <td class="clStatus" id='<%#Eval("RecID") %>' data-status="<%# Eval("PackageTaskStatus")%>"><i id="spinner" class="hidden fa fa=fw fa-spinner fa-spin"></i> <%# Eval("PackageTaskStatus").ToString()=="0"?"等待调度":Eval("PackageTaskStatus").ToString()=="1"?"等待调度":Eval("PackageTaskStatus").ToString()=="2"?"进行中":Eval("PackageTaskStatus").ToString()=="3"?"<span style=\"color:#338610\">完成</span>":"<span style=\"color:#f00\">失败</span>" %></td>

                                <td><%# Eval("PackageTaskStatus").ToString()=="0"?" ":Eval("PackageTaskStatus").ToString()=="1"?" ":"<a onclick=\"openfile('"+Eval("RecID")+"','"+ createtaskid+"','"+platform+"');\" class='btn btn-default btn-sm'><i class='fa fa-fw fa-info-circle'></i> 详情</a>" %></td>
                                <%--<a onclick=\"openfile("+Eval("RecID")+","+createtaskid+");\">详情</a>--%>

                                <td><%# Eval("PackageTaskStatus").ToString()=="3"?(platform=="Android"?"<a href=\"/share/output/apk/"+gameid+"/"+createtaskid+"/"+Eval("PackageName")+"\" class='btn btn-primary btn-sm'><i class='fa fa-fw fa-download'></i> 下载</a>":
                    "<a href=\"/share/ios-output/ipa/"+gameid+"/"+createtaskid+"/"+Eval("PackageName")+"\" class='btn btn-default btn-sm'><i class='fa fa-fw fa-download'></i> 下载</a>"):Eval("PackageTaskStatus").ToString()=="4"?"<a class=\"btn btn-primary btn-sm\" onclick=\"packageAgain("+Eval("RecID")+",'"+platform+"')\" class='btn btn-primary btn-sm'><i class=\'fa fa-fw fa-refresh\'></i>重新打包</a>":"<a onclick='stopTask(\"/taskmanage?action=loseTask&platform=Android&taskid=" + Eval("RecID") + "\");' class='btn btn-warning btn-sm' target='_blank'><i class='fa fa-fw fa-warning'></i> 强制终止</a>" %></td>

                            </tr>
                        </ItemTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr>
                                        <th>任务编号</th>
                                        <th>渠道名称</th>
                                        <th>渠道编号</th>
                                        <th id="thjm">加密</th>
                                        <th>打包状况</th>
                                        <th>日志</th>
                                        <th>渠道包</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>

                    </asp:ListView>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <asp:SqlDataSource ID="SqlDataSource1" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="">
        <SelectParameters>
        </SelectParameters>
    </asp:SqlDataSource>

    <script type="text/javascript">
        $(function () {
            <% if (platform == "IOS")
               {%>
            $("#MainContent_GamePlaceList_thjm").hide();
            <%}%>
        })

		function stopTask(u){
		      $.ajax({
					url: 'http://'+window.location.host+u,
					success: function(data) {
						if (data.indexOf('loseTask_OK')>=0){
						  alert('终止任务成功');
						  location.reload();
						  }
					},
					error: function(err) {
						alert('终止任务失败');
					}
				});
		}

        $(document).ready(function () {
            var statuslist = $(".clStatus");

            var id = [];
            for (var k = 0; k < statuslist.length; k++) {
                var c = statuslist.eq(k).attr('data-status');
                if (c < 3) {
                    id.push(statuslist.eq(k).attr('id'));
                    if (c == 1) {
                        statuslist.eq(k).children('i').removeClass('hidden').css('color', '#ff0000');
                    } else if (c == 2) {
                        statuslist.eq(k).children('i').removeClass('hidden').css('color', '#00ff00');
                    }
                }
            }
            var systemname = '<%= platform %>';

        $(function () {
            function getWorkStatus() {
                if (id.length > 0) {
                    $.ajax({
                        contentType: "application/json",
                        async: false,
                        url: "/WS/WSNativeWeb.asmx/GetPackgeStatus",
                        data: "{id:'" + id.join(',') + "',systemname:'" + systemname + "'}",
                        type: "POST",
                        dataType: "json",
                        success: function (json) {
                            json = eval("(" + json.d + ")");
                            if (json.ret === 0) {
                                $.each(json.data, function (idx, item) {
                                    if (item.PackageTaskStatus === 4) {
                                        $('#' + item.RecID).html('<span style="color:#f00">失败</span>');
                                        id.splice($.inArray('item.RecID', id), 1);
                                        window.location.reload();
                                    } else if (item.PackageTaskStatus === 3) {
                                        $('#' + item.RecID).html('<span style="color:#338610">完成</span>');
                                        id.splice($.inArray('item.RecID', id), 1);
                                        window.location.reload();
                                    } else if (item.PackageTaskStatus === 2) {
                                        $('#' + item.RecID).html('<i id="spinner" class="fa fa=fw fa-spinner fa-spin" style="color: #00ff00;"></i> 进行中');
                                    }
                                });
                            } else {
                            }
                        }
                    });
                }
            }
            setInterval(getWorkStatus, 3000);
        });
      });
    </script>
</asp:Content>
