<%@ Page Title="渠道包下载" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="DownLoadIOSPackage.aspx.cs" Inherits="SDKPackage.Dropload.DownLoadIOSPackage" %>

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
	<style type="text/css">
	.table > tbody > tr > td {
     vertical-align: middle;
	}
	</style>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>IOS渠道包下载
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
                                    <label class="control-label">查看版本</label>
                                    <asp:DropDownList ID="DropDownList2" runat="server" CssClass="form-control" DataSourceID="SqlDataSourceGameVersion" DataTextField="GameVersion" DataValueField="id" AutoPostBack="True"></asp:DropDownList>
                                    <asp:SqlDataSource ID="SqlDataSourceGameVersion" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="  select id=0,GameVersion='--全部--' union all select id,(GameVersion+'_'+PageageTable) as GameVersion from [sdk_UploadPackageInfo] where GameID=@GameID and GamePlatFrom='IOS'">
                                        <SelectParameters>
                                            <asp:ControlParameter ControlID="DropDownList1" Name="GameID" PropertyName="SelectedValue" Type="Int32" />
                                        </SelectParameters>
                                    </asp:SqlDataSource>
                                </div>
                            </div>
							</div>
                    <div class="row">
                        <div class="col-md-12">
                            <br />
                            <asp:ListView ID="ListView1" runat="server" DataSourceID="SqlDataSourcePackage">

                                <EmptyDataTemplate>
                                    <table runat="server">
                                        <tr>
                                            <td>演示平台不开放IOS打包功能（没有MAC系统的云主机）</td>
                                        </tr>
                                    </table>
                                </EmptyDataTemplate>

                                <ItemTemplate>
                                    <tr>
                                        <td>
                                            <%#"<a href=\"/share/ios-output/ipa/"+Eval("GameID")+"/"+Eval("CreateTaskID")+"/"+Eval("PackageName")+"\">下载</a>" %>
                                        </td>
                                        <td>
                                            <asp:Label ID="platformNameLabel" runat="server" Text='<%# Eval("platformName") %>' />
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
                                    </tr>
                                </ItemTemplate>
                                <LayoutTemplate>
                                    <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                        <caption>游戏IPK包列表</caption>
                                        <thead>
                                            <tr>
                                                <th>渠道包</th>
                                                <th>渠道</th>
                                                <th>批号</th>
                                                <th>时间</th>
                                                <th>创建人</th>
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
                                    <asp:Parameter Name="SystemName" Type="String" DefaultValue="IOS" />
                                    <asp:Parameter Name="IsSign" Type="String" DefaultValue="0" />

                                    <asp:Parameter Name="PackageReviewStatus" Type="Int32" DefaultValue="1" />
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
      $(document).ready(function() {
        $('#itemPlaceholderContainer').dataTable({
            "sPaginationType" : "full_numbers",
            "oLanguage" : {
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
    </script>
    <!-- /Datatables -->
</asp:Content>
