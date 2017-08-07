<%@ Page Title="游戏渠道包管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="GameChannelPackageManage.aspx.cs" Inherits="SDKPackage.GameConfig.GameChannelPackageManage" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">

    <script type="text/javascript">
        $(function () {
            $("#MainContent_DropDownList1").change(function () {
                <% this.ListView1.DataBind();%>
            });
        })
    </script>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>游戏渠道包管理
                    </h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="row">
                            <div class="form-inline text-center">
                                <div class="form-group">
                                    <label class="control-label">查看游戏</label>
                                    <asp:DropDownList ID="DropDownList1" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGame" DataTextField="GameDisplayName" DataValueField="GameID" AutoPostBack="True"></asp:DropDownList>
                                    <asp:SqlDataSource ID="SqlDataSourceGame" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="select GameID,GameDisplayName from sdk_gameInfo"></asp:SqlDataSource>
                                </div>
                                <div class="form-group">
                                    <label class="control-label">查看系统</label>
                                    <asp:DropDownList ID="ddlPlatforms" runat="server" CssClass="form-control" AutoPostBack="True">
                                        <asp:ListItem Value="Android">Android</asp:ListItem>
                                        <asp:ListItem Value="IOS">IOS</asp:ListItem>
                                    </asp:DropDownList>
                                </div>
                                <div class="form-group">
                                    <label class="control-label">查看版本</label>
                                    <asp:DropDownList ID="DropDownList2" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGameVersion" DataTextField="GameVersion" DataValueField="id" AutoPostBack="True"></asp:DropDownList>
                                    <asp:SqlDataSource ID="SqlDataSourceGameVersion" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="  select id=0,GameVersion='--全部--' union all select id,(GameVersion+'_'+PageageTable) as GameVersion from [sdk_UploadPackageInfo] where GameID=@GameID and GamePlatFrom=@PlatformName">
                                        <SelectParameters>
                                            <asp:ControlParameter ControlID="DropDownList1" Name="GameID" PropertyName="SelectedValue" Type="Int32" />
                                            <asp:ControlParameter ControlID="ddlPlatforms" Name="PlatformName" PropertyName="SelectedValue" Type="String" />
                                        </SelectParameters>
                                    </asp:SqlDataSource>
                                </div>
                                <div class="form-group">
                                    <label class="control-label">查看签名</label>
                                    <asp:DropDownList ID="DropDownList4" runat="server" CssClass="form-control" AutoPostBack="True">
                                        <asp:ListItem Selected="True" Value="1">签名</asp:ListItem>
                                    </asp:DropDownList>
                                </div>
                            </div>
                            </div>
                    <div class="row">
                        <div class="col-md-12">
                            <br />
                            <asp:ListView ID="ListView1" runat="server" DataSourceID="SqlDataSourcePackage">
                                <AlternatingItemTemplate>
                                    <tr>
                                        <td>
                                            <%#Eval("Recid") %>
                                        </td>
                                        <td>
                                            <%#string.IsNullOrEmpty(Eval("PlugInID").ToString())||Eval("PlugInID").ToString()=="0"? Eval("platformName"):Eval("platformName")+"_LeBian" %><%#Eval("AdName").ToString()!=""?"("+Eval("AdName")+")":"" %>
                                        </td>
                                        <td>
                                            <asp:Label ID="Label6" runat="server" Text='<%# Eval("GameVersion")+"_"+Eval("PageageTable") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="createTaskLabel" runat="server" Text='<%# Eval("CreateTaskID") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="createDatetimeLabel" runat="server" Text='<%# Eval("CollectDatetime") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="Label4" runat="server" Text='<%# Eval("Compellation") %>' />
                                        </td>
                                        <td>
                                            <div class="tdReview" data-id='<%#Eval("Recid") %>'>
                                            <%# Eval("PackageReviewStatus").Equals(0) ? "<a class=\"btn btn-xs btn-danger fa\" onclick='reviewPackage(\"" + Eval("Recid").ToString() + "\",1)'><i class=\"fa fa-trash\"></i>通过</a><a class=\"btn btn-xs btn-danger fa\" onclick='reviewPackage(\"" + Eval("Recid").ToString() + "\",2)'><i class=\"fa fa-trash\"></i>打回</a>"
                                                                                      : Eval("PackageReviewStatus").Equals(1) ? "已通过"
                                                                                                                              : "已打回"
                                            %>
                                            </div>
                                        </td>
                                        <td>
                                            <%#Eval("IsSign").ToString()=="1"?"<a class=\"btn btn-primary btn-sm fa\" href=\"/share/output/apk/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/"+Eval("PackageName")+"\"><i class=\"fa fa-fw fa-download\"></i> 下载</a>":"<a class=\"btn btn-primary btn-xs fa\" href=\"/share/output/apk/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/us_"+Eval("PackageName")+"\"><i class=\"fa fa-fw fa-download\"></i> 下载</a>" %>
                                        </td>
                                    </tr>
                                </AlternatingItemTemplate>
                                <EmptyDataTemplate>
                                    <table runat="server">
                                        <tr>
                                            <td>没有找到数据</td>
                                        </tr>
                                    </table>
                                </EmptyDataTemplate>

                                <ItemTemplate>
                                    <tr>
                                        <td>
                                            <%#Eval("Recid") %>
                                        </td>
                                        <td>
                                            <%#string.IsNullOrEmpty(Eval("PlugInID").ToString())||Eval("PlugInID").ToString()=="0"? Eval("platformName"):Eval("platformName")+"_LeBian" %><%#Eval("AdName").ToString()!=""?"("+Eval("AdName")+")":"" %>
                                        </td>
                                        <td>
                                            <asp:Label ID="Label6" runat="server" Text='<%# Eval("GameVersion")+"_"+Eval("PageageTable") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="createTaskLabel" runat="server" Text='<%# Eval("CreateTaskID") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="createDatetimeLabel" runat="server" Text='<%# Eval("CollectDatetime") %>' />
                                        </td>
                                        <td>
                                            <asp:Label ID="Label4" runat="server" Text='<%# Eval("Compellation") %>' />
                                        </td>
                                        <td>
                                            <div class="tdReview" data-id='<%#Eval("Recid") %>'>
                                            <%# Eval("PackageReviewStatus").Equals(0) ? "<a class=\"btn btn-xs btn-danger fa\" onclick='reviewPackage(\"" + Eval("Recid").ToString() + "\",1)'><i class=\"fa fa-trash\"></i>通过</a><a class=\"btn btn-xs btn-danger fa\" onclick='reviewPackage(\"" + Eval("Recid").ToString() + "\",2)'><i class=\"fa fa-trash\"></i>打回</a>"
                                                                                      : Eval("PackageReviewStatus").Equals(1) ? "已通过"
                                                                                                                              : "已打回"
                                            %>
                                            </div>
                                        </td>
                                        <td>
                                            <%#Eval("IsSign").ToString()=="1"?"<a class=\"btn btn-primary btn-sm fa\" href=\"/share/output/apk/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/"+Eval("PackageName")+"\"><i class=\"fa fa-fw fa-download fa\"></i> 下载</a>":"<a class=\"btn btn-primary btn-xs fa\" href=\"/share/output/apk/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/us_"+Eval("PackageName")+"\"><i class=\"fa fa-fw fa-download\"></i> 下载</a>" %>
                                        </td>
                                    </tr>
                                </ItemTemplate>
                                <LayoutTemplate>
                                    <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                        <thead>
                                            <tr runat="server">
                                                <th runat="server">任务</th>
                                                <th runat="server">渠道</th>
                                                <th runat="server">版本</th>
                                                <th runat="server">批号</th>
                                                <th runat="server">时间</th>
                                                <th runat="server">创建人</th>
                                                <th runat="server">状态</th>
                                                <th runat="server">渠道包操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr id="itemPlaceholder" runat="server">
                                            </tr>
                                        </tbody>
                                    </table>
                                </LayoutTemplate>
                            </asp:ListView>
                            <asp:SqlDataSource ID="SqlDataSourcePackage" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPackageList" SelectCommandType="StoredProcedure">
                                <SelectParameters>
                                    <asp:ControlParameter ControlID="DropDownList1" Name="GameID" Type="Int32" DefaultValue="0" />
                                    <asp:ControlParameter ControlID="DropDownList2" Name="PackTaskID" Type="Int32" DefaultValue="0" />
                                    <asp:ControlParameter ControlID="ddlPlatforms" Name="SystemName" Type="String" DefaultValue="Android" />
                                    <asp:ControlParameter ControlID="DropDownList4" Name="IsSign" Type="String" />
                                    
                                    <asp:Parameter Name="PackageReviewStatus" Type="Int32" DefaultValue="0" />

                                </SelectParameters>
                            </asp:SqlDataSource>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

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
        $(document).ready(function () {
            $('#itemPlaceholderContainer').dataTable({
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
        });

        function reviewPackage(id, status) {
            var platform = "<%=ddlPlatforms.SelectedValue%>";
            if (confirm("确定要审核数据吗？")) {
                $.ajax({
                    contentType: "application/json;charset=utf-8",
                    async: false,
                    url: "/WS/WSNativeWeb.asmx/ReviewPackage",
                    //data: "{id:" + id + ",status:" + status + "}",
                    data: "{id:" + id + ",status:" + status + ",platform:'" + platform + "'}",
                    type: "POST",
                    dataType: "json", success: function (json) {
                        json = eval("(" + json.d + ")");

                        if (json.ret === 0) {
                            $(".tdReview[data-id='" + id + "']").text((status == 1) ? '已通过' : '已打回');
                        } else {
                            alert('ERR');
                        }
                    }
                });
            }
        }
    </script>
    <!-- /Datatables -->
</asp:Content>
