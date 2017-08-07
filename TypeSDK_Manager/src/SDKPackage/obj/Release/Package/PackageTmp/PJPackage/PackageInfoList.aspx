<%@ Page Title="查询打包任务" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="PackageInfoList.aspx.cs" Inherits="SDKPackage.PJPackage.PackageInfoList" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <asp:ScriptManager runat="server">
    </asp:ScriptManager>
    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">

    <script type="text/javascript">
        function openfile(taskid, createtaskid) {
            var systemname = $("#MainContent_DropDownList3").val();
            var str_href = "/PJPackage/sdkPackageLog.aspx?taskid=" + taskid + "&createtaskid=" + createtaskid + "&systemname=" + systemname;
            var w = $(window).width();
            var h = $(window).height();
            window.open(str_href, '打包日志', 'height=' + h + ',width=' + w + ',toolbar=no,menubar=no,scrollbars=yes, resizable=no,location=no, status=no');
        }
        function packageAgain(id, systemname) {
            var gameid = $("#MainContent_DropDownList1").val();
            var taskType = $("#MainContent_DropDownList2").val();
            systemname = $("#MainContent_DropDownList3").val();
            $.ajax({
                contentType: "application/json",
                async: false,
                url: "/WS/WSNativeWeb.asmx/PackageAgain",
                data: "{id:'" + id + "',systemname:'" + systemname + "'}",
                type: "POST",
                dataType: "json", success: function () {
                }
            });
            var href = location.href;// + "?gameid=" + id + "&taskType=" + taskType + "&systemid=" + systemname;
            if (href.indexOf("?") > 0) {
                href = href.substring(0, href.indexOf("?"));
            }
            var isMy = $("#MainContent_ckMy").prop('checked');
            href = href + "?gameid=" + gameid + "&taskType=" + taskType + "&systemid=" + systemname + "&ck=" + (isMy ? 1 : 0);
            location.href = href;
            //alert(href);
            //window.location.reload();
        }
        $(function () {
            $("#MainContent_DropDownList1,#MainContent_DropDownList2,#MainContent_DropDownList3,#MainContent_ckMy").change(function () {
                locationuUrl();
            });
            $("#Button1").click(function () {
                locationuUrl();
            });
        })

        function locationuUrl() {
            var systemname = $("#MainContent_DropDownList3").val();
            var taskType = $("#MainContent_DropDownList2").val();
            var gameid = $("#MainContent_DropDownList1").val();
            var href = location.href;// + "?gameid=" + id + "&taskType=" + taskType + "&systemid=" + systemname;
            if (href.indexOf("?") > 0) {
                href = href.substring(0, href.indexOf("?"));
            }
            var isMy = $("#MainContent_ckMy").prop('checked') ? 1 : 0;
            href = href + "?gameid=" + gameid + "&taskType=" + taskType + "&systemid=" + systemname + "&ck=" + isMy + "&c=" + gameid + taskType + systemname + isMy;
            //alert(href);
            location.href = href;
        }
    </script>

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>查询打包任务</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                    <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="form-inline text-center">
                        <div class="form-group">
                            <label class="control-label">平台</label>
                            <asp:DropDownList ID="DropDownList3" runat="server" CssClass="form-control">
                                <asp:ListItem Selected="True">Android</asp:ListItem>
                                <asp:ListItem>IOS</asp:ListItem>
                            </asp:DropDownList>
                        </div>
                        <div class="form-group">
                            <label class="control-label">游戏名称</label>
                            <asp:DropDownList ID="DropDownList1" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGame" DataTextField="GameDisplayName" DataValueField="gameid"></asp:DropDownList>
                            <%--<asp:SqlDataSource ID="SqlDataSourceGame" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="select gameid=0,GameDisplayName='--全部--' union all select gameid,GameDisplayName from sdk_GameInfo"></asp:SqlDataSource>--%>
                            <asp:SqlDataSource ID="SqlDataSourceGame" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getGameList" SelectCommandType="StoredProcedure">
                                <SelectParameters>
                                    <asp:ControlParameter ControlID="saveusername" Type="String" Name="UserName" />
                                </SelectParameters>
                            </asp:SqlDataSource>
                            <asp:HiddenField ID="HiddenFieldUserName" runat="server" />
                        </div>
                        <div class="form-group">
                            <label class="control-label">任务状态</label>
                            <asp:DropDownList ID="DropDownList2" runat="server" CssClass="form-control">
                                <asp:ListItem Selected="True" Value="0">所有任务</asp:ListItem>
                                <asp:ListItem Value="1">进行中</asp:ListItem>
                                <asp:ListItem Value="3">成功</asp:ListItem>
                                <asp:ListItem Value="4">失败</asp:ListItem>
                            </asp:DropDownList>
                        </div>
                        <div class="form-group">
                            <asp:CheckBox ID="ckMy" runat="server" CssClass="control-label" Text=" 仅显示我的任务" />
                            <asp:Label ID="LabelMessage" runat="server" Text=""></asp:Label>
                        </div>
                    </div>
                    <hr />
                    <asp:ListView ID="GamePlaceList" runat="server" DataKeyNames="RecID" OnItemCommand="GamePlaceList_ItemCommand">
                        <EmptyDataTemplate>
                            <table>
                                <tr>
                                    <td>没有任务。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>

                        <ItemTemplate>
                            <tr>
                                <td class="clRec" title='<%#Eval("CreateTaskID") %>'>
                                    <%#Eval("RecID") %>
                                </td>
                                <td>
                                    <%#Eval("GameDisplayName") %>
                                </td>
                                <td>
                                    <%#Eval("PlatformDisplayName") %>
                                </td>
                                <td>
                                    <%#Eval("PlatformVersion") %>
                                </td>
                                <td>
                                    <%#Eval("GameFileVersion") %>
                                </td>
                                <td><%#Eval("IsEncryption").ToString()=="0"?"N":"<span style=\"color:#f00\">Y</span>" %></td>
                                <td>
                                    <%#Eval("StartDatetime") %>
                                </td>
                                <td>
                                    <%#Eval("Compellation") %>
                                </td>
                                <td class="clStatus" id='<%#Eval("RecID") %>' data-status="<%# Eval("PackageTaskStatus")%>"><i id="spinner" class="hidden fa fa=fw fa-spinner fa-spin"></i> <%# Eval("PackageTaskStatus").ToString()=="0"?"等待调度":Eval("PackageTaskStatus").ToString()=="1"?"等待调度":Eval("PackageTaskStatus").ToString()=="2"?"进行中":Eval("PackageTaskStatus").ToString()=="3"?"<span style=\"color:#338610\">完成</span>":"<span style=\"color:#f00\">失败</span>" %></td>

                                <td><%# (Eval("PackageTaskStatus").ToString()=="0" || Eval("PackageTaskStatus").ToString()=="1" || Eval("PackageTaskStatus").ToString()=="2" )?" ":"<a onclick=\"openfile('"+Eval("RecID")+"','"+Eval("CreateTaskID")+"');\" class=\"btn btn-default btn-sm\"><i class=\"fa fa-fw fa-info-circle\"></i>详情</a>" %></td>

                                <td><%# Eval("PackageTaskStatus").ToString()=="3"?(systemname=="Android"? "<a href=\"/share/output/apk/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/"+Eval("PackageName")+"\" class=\"btn btn-primary btn-sm\"><i class=\"fa fa-fw fa-download\"></i> 下载</a>"
                        :"<a class=\"btn btn-primary btn-sm\" href=\"/share/ios-output/ipa/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/"+Eval("PackageName")+"\"><i class=\"fa fa-fw fa-download\"></i> 下载</a>"):Eval("PackageTaskStatus").ToString()=="4"?"<a style=\"cursor:pointer;\" onclick=\"packageAgain("+Eval("RecID")+")\" class=\"btn btn-warning btn-sm\"><i class=\"fa fa-fw fa-refresh\"></i>重新打包</a>":"<a onclick='stopTask(\"/taskmanage?action=loseTask&platform=Android&taskid=" + Eval("RecID") + "\");' class='btn btn-warning btn-sm' target='_blank'><i class='fa fa-fw fa-warning'></i> 强制终止</a>" %>

                                    <a class="btn btn-danger btn-sm" style='<%#((Eval("qx").ToString()=="0"&&Eval("qx2").ToString()=="0")||(Eval("PackageTaskStatus").ToString()!="1"&&Eval("PackageTaskStatus").ToString()!="3"&&Eval("PackageTaskStatus").ToString()!="4"))?"display:none;": ""%>'
                                        onclick='deleteFile(this,<%#Eval("RecID").ToString() %>,"<%=systemname %>","<%#systemname=="Android"?Eval("GameName").ToString()+"/"+Eval("CreateTaskID").ToString()+"/"+Eval("PackageName").ToString():
                        Eval("GameNameSpell").ToString()+"/"+Eval("CreateTaskID").ToString()+"/"+Eval("PackageName").ToString() %>")'><i class="fa fa-fw fa-trash-o"></i> 删除</a>
                                </td>
                            </tr>
                        </ItemTemplate>

                        <LayoutTemplate>
                            <table id="example" class="table table-striped jambo_table">
                                <thead>
                                    <tr>
                                        <th>任务ID</th>
                                        <th>游戏名称</th>
                                        <th>渠道名称</th>
                                        <th>渠道版本</th>
                                        <th>游戏版本</th>
                                        <th runat="server" style='<%#systemname=="IOS"?"display:none": ""%>'>加密</th>
                                        <th>启动时间</th>
                                        <th>创建人</th>
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
                    <asp:SqlDataSource ID="SqlDataSource1" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPackageTaskList" SelectCommandType="StoredProcedure">
                        <SelectParameters>
                            <%--<asp:ControlParameter ControlID="DropDownList1" Name="GameID" Type="Int32" PropertyName="SelectedValue" DefaultValue="0" />--%>
                            <asp:QueryStringParameter QueryStringField="gameid" Type="Int32" Name="GameID" DefaultValue="0" />
                            <asp:ControlParameter ControlID="DropDownList2" Name="PackageTaskStatus" Type="Int32" PropertyName="SelectedValue" />
                            <asp:ControlParameter ControlID="DropDownList3" Name="SystemName" Type="String" PropertyName="SelectedValue" />
                            <asp:ControlParameter ControlID="HiddenFieldUserName" Name="UserName" Type="String" PropertyName="Value" />
                            <asp:ControlParameter ControlID="ckMy" Name="IsMy" Type="Boolean" PropertyName="Checked" />
                        </SelectParameters>
                        <DeleteParameters>
                            <asp:Parameter Name="RecID" Type="Int32" />
                        </DeleteParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>

        <asp:HiddenField ID="saveusername" runat="server" />
    </div>

    <script type="text/javascript">
         function deleteFile(obj, id, platform, filepath) {
             if (confirm("确定要删除数据吗？")) {
                 var tr = $(obj).parent("td").parent("tr");
                 $.ajax({
                     contentType: "application/json",
                     async: false,
                     url: "/WS/WSNativeWeb.asmx/DeletePlatformPackage",
                     data: "{id:'" + id + "',platform:'" + platform + "',filepath:'" + filepath + "'}",
                     type: "POST",
                     dataType: "json", success: function () {
                         tr.hide();
                     }
                 });
             }
         }
    </script>

    <!-- Datatables -->
    <script src="/vendors/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="/vendors/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="/vendors/datatables.net-buttons-bs/js/buttons.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.flash.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="/vendors/datatables.net-fixedheader/js/dataTables.fixedHeader.min.js"></script>
    <script src="/vendors/datatables.net-keytable/js/dataTables.keyTable.min.js"></script>
    <script src="/vendors/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script src="/vendors/datatables.net-responsive-bs/js/responsive.bootstrap.js"></script>
    <script src="/vendors/datatables.net-scroller/js/datatables.scroller.min.js"></script>
    <script src="/vendors/jszip/dist/jszip.min.js"></script>
    <script src="/vendors/pdfmake/build/pdfmake.min.js"></script>
    <script src="/vendors/pdfmake/build/vfs_fonts.js"></script>

    <!-- Datatables -->
    <script>
    
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
            $('#example').dataTable({
                "order": [[0, "desc"]],
                "sPaginationType": "full_numbers",
                "oLanguage": {
                    "sLengthMenu": "每页显示 _MENU_ 条记录",
                    "sZeroRecords": "抱歉， 没有找到",
                    "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
                    "sInfoEmpty": "没有数据",
                    "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
                    "sZeroRecords": "没有检索到数据",
                    "sSearch": "名称:",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sPrevious": "前一页",
                        "sNext": "后一页",
                        "sLast": "尾页"
                    }
                }
            });

            var statuslist = $(".clStatus");

            var id = [];
            for (var k = 0; k < statuslist.length; k++) {
                var c = statuslist.eq(k).attr('data-status');
                if (c < 3) {
                    id.push(statuslist.eq(k).attr('id'));
                    if (c <= 1) {
                        statuslist.eq(k).children('i').removeClass('hidden').css('color', '#ff0000');
                    } else if (c == 2) {
                        statuslist.eq(k).children('i').removeClass('hidden').css('color', '#00ff00');
                    }
                }
            }
            var systemname = $("#MainContent_DropDownList3").val();

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
    <!-- /Datatables -->

</asp:Content>
