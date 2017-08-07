<%@ Page Title="渠道版本管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="PlatformVersion.aspx.cs" Inherits="SDKPackage.PJConfig.PlatformVersion" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script type="text/javascript">
    </script>
    <script type="text/javascript">
        $(function () {
            $("#addVersion").click(function () {
                $("#MainContent_TextBoxVersion").val("");
            });
            $("#MainContent_ButtonAddPlatformVersion").click(function () {
                var strval = $("#MainContent_TextBoxVersion").val();
                if (strval == null || strval == "" || strval == undefined) {
                    alert("版本不呢为空！");
                    return false;
                }
            });
        })
    </script>
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>渠道管理-版本管理</h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="form-inline">
                        <div class="form-group">
                            <label style="float: left; font-size: 24px; margin-right: 10px;">平台:</label>
                            <asp:DropDownList ID="DropDownListSystem" runat="server" CssClass="form-control" Style="width: 150px; float: left; margin-right: 20px;" AutoPostBack="True">
                                <asp:ListItem Value="1">Android</asp:ListItem>
                                <asp:ListItem Value="2">IOS</asp:ListItem>
                            </asp:DropDownList>
                        </div>
                        <div class="form-group">
                            <label style="float: left; font-size: 24px; margin-right: 10px;">渠道：</label>
                            <asp:DropDownList ID="DropDownListPlatform" runat="server" CssClass="form-control" Style="width: 150px; float: left; margin-right: 20px;" AutoPostBack="True" DataSourceID="DataSourceIDPlatform" DataTextField="PlatformDisplayName" DataValueField="ID"></asp:DropDownList>

                            <a id="addVersion" class="btn btn-primary" data-toggle="modal" data-target="#divplatformVersion">添加新版本</a><asp:Label ID="LabelMessage" runat="server" Text=""></asp:Label>
                        </div>
                    </div>
                    <hr />
                    <asp:ListView ID="ListView1" runat="server" DataKeyNames="ID" DataSourceID="SqlDataSourcePlatformVersion">
                        <EmptyDataTemplate>
                            <table runat="server">
                                <tr>
                                    <td>没有配置数据。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <thead>
                                    <tr runat="server">
                                        <th runat="server">渠道SDK版本</th>
                                        <th runat="server">上传人</th>
                                        <th runat="server">上传时间</th>
                                        <%--<th runat="server">编辑</th>--%>
                                        <th runat="server">删除</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id="itemPlaceholder" runat="server">
                                    </tr>
                                </tbody>
                            </table>
                        </LayoutTemplate>
                        <AlternatingItemTemplate>
                            <tr>
                                <td><%#Eval("Version") %></td>
                                <td><%#Eval("Compellation") %></td>
                                <td><%#Eval("CollectDatetime") %></td>
                                <td>
                                    <asp:Button ID="ButtonDelete" runat="server" Text="&#xf014; 删除" Class="btn btn-danger btn-sm fa fa-trash" OnClientClick="return confirm('确定要删除数据吗？')" CommandName="Delete" /></td>
                            </tr>
                        </AlternatingItemTemplate>
                        <ItemTemplate>
                            <tr>
                                <td><%#Eval("Version") %></td>
                                <td><%#Eval("Compellation") %></td>
                                <td><%#Eval("CollectDatetime") %></td>
                                <td>
                                    <asp:Button ID="ButtonDelete" runat="server" Text="&#xf014; 删除" Class="btn btn-danger btn-sm fa fa-trash" OnClientClick="return confirm('确定要删除数据吗？')" CommandName="Delete" /></td>
                            </tr>
                        </ItemTemplate>
                    </asp:ListView>
                </div>
                <asp:SqlDataSource ID="DataSourceIDPlatform" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPlatformList" SelectCommandType="StoredProcedure">
                    <SelectParameters>
                        <asp:ControlParameter ControlID="DropDownListSystem" Name="SystemID" Type="Int32" PropertyName="SelectedValue" />
                    </SelectParameters>
                </asp:SqlDataSource>
                <asp:SqlDataSource ID="SqlDataSourcePlatformVersion" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getPlatformVersion" SelectCommandType="StoredProcedure"
                    InsertCommand="sdk_setPlatformVersion" InsertCommandType="StoredProcedure" DeleteCommand="delete from sdk_PlatformVersion where id=@ID">
                    <SelectParameters>
                        <asp:ControlParameter ControlID="DropDownListPlatform" Name="PlatformID" Type="Int32" PropertyName="SelectedValue" />
                        <asp:ControlParameter ControlID="DropDownListSystem" Name="SystemID" Type="Int32" PropertyName="SelectedValue" />
                    </SelectParameters>
                    <DeleteParameters>
                        <asp:Parameter Name="ID" Type="Int32" />
                    </DeleteParameters>
                </asp:SqlDataSource>
                <!--添加新版本start-->
                <div class="modal fade" id="divplatformVersion" tabindex="-1" role="dialog" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                                    ×
                                </button>
                                <h2 class="modal-title">添加新版本</h2>
                            </div>
                            <!--内容-->
                            <div class="form-group" style="margin: 20px 0 20px 20px;">
                                <label>请输入版本号:</label>
                                <asp:TextBox ID="TextBoxVersion" runat="server" placeholder="请输入版本号 如1.0" CssClass="form-control" Style="width: 250px;"></asp:TextBox>
                            </div>
                            <!--内容-->
                            <div class="modal-footer" id="modalfooter3">
                                <button type="button" class="btn btn-default" data-dismiss="modal">
                                    关闭
                                </button>
                                <asp:Button ID="ButtonAddPlatformVersion" runat="server" class="btn btn-primary" Text="添加" OnClick="ButtonAddPlatformVersion_Click" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--添加新版本end-->
</asp:Content>
