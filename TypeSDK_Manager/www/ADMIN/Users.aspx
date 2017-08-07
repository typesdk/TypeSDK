<%@ Page Title="平台用户管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="Users.aspx.cs" Inherits="SDKPackage.ADMIN.Users" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script type="text/javascript">
        function GetRoleGame(id) {
            $.ajax({
                contentType: "application/json",
                async: false,
                url: "/WS/WSNativeWeb.asmx/GetRoleGamePower",
                data: "{userid:'" + id + "'}",
                type: "POST",
                dataType: "json", success: function (data) {
                    data = eval("(" + data.d + ")");
                    if (data.data != null) {
                        var strhtml = "";
                        var strdisplay = "";
                        var row = 0;
                        for (var i = 0; i < data.data.length; i++) {
                            if (data.data[i].rolepower == "1") {
                                strdisplay = "checked=\"true\"";
                            } else if (data.data[i].rolepower == "-1") { row++ }
                            strhtml += '<div style="float: left; padding: 20px;">';
                            strhtml += '<input type="checkbox" id="cb' + i + '"  ' + strdisplay + '   value="' + data.data[i].gameid + '"/><label for="cb' + i + '" style="margin-left: 5px;">' + data.data[i].gamename + '</label></div>';
                            strdisplay = "";
                        }
                        if (row == data.data.length) strdisplay = "checked=\"true\"";
                        strhtml += "<div style=\"float: left; padding: 20px;\">";
                        strhtml += "<input type=\"checkbox\" id=\"cbAll\" value=\"0\" " + strdisplay + " /><label for=\"cbAll\" style=\"margin-left: 5px;\">所有游戏</label></div>";
                        strhtml += "<div style=\"clear: both;\"></div>";
                        strhtml += "<input type=\"hidden\" id=\"hiddenID\" value=\"\" />";
                        $("#divRolePower").html(strhtml);
                        $("#hiddenID").val(id);
                    }
                }
            });
        }

        $(function () {
            $("#MainContent_ButtonUpdateRole").click(function () {
                var cbAll = $("#cbAll");
                var strgameidlist = "";
                if (cbAll.prop("checked")) strgameidlist = "0";
                else {
                    var cblist = $("#divRolePower input:checkbox");
                    for (var i = 0; i < cblist.length - 1; i++) {
                        if (cblist[i].checked) {
                            strgameidlist += cblist[i].value + ",";
                        }
                    }
                    strgameidlist = strgameidlist.substring(0, strgameidlist.length - 1);
                }
                //alert(strgameidlist); return;
                var id = $("#hiddenID").val();
                $.ajax({
                    contentType: "application/json",
                    async: false,
                    url: "/WS/WSNativeWeb.asmx/UpdateRoleGamePower",
                    data: "{userid:'" + id + "',gameidlist:'" + strgameidlist + "'}",
                    type: "POST",
                    dataType: "json", success: function (json) {
                        json = eval("(" + json.d + ")");
                        if (json.success) {
                            <%this.ListView1.DataBind(); %>
                        } else {

                        }
                    }
                });
            });
        })

    </script>
    <div class="modal fade" id="divRoleGameManager" tabindex="-1" role="dialog" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h2 class="modal-title">游戏权限管理</h2>
                </div>
                <!--内容-->
                <div style="margin: 20px 0; border: 1px solid #808080;" id="divRolePower">
                   
                </div>
                <!--内容-->
                <div class="modal-footer" id="modalfooter2">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonUpdateRole" runat="server" class="btn btn-primary" Text="保存" />
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>用户管理</h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <asp:ListView ID="ListView1" runat="server" DataKeyNames="Id" DataSourceID="SqlDataSourceUser">
                        <EditItemTemplate>
                            <tr>
                                <td>
                                    <asp:Button ID="UpdateButton" runat="server" CssClass="btn btn-success btn-sm fa fa-save" CommandName="Update" Text="&#xf0c7; 更新" />
                                    <asp:Button ID="CancelButton" runat="server" CssClass="btn btn-default btn-sm fa fa-reply" CommandName="Cancel" Text="&#xf112; 取消" />
                                </td>
                                <td>
                                    <asp:TextBox ID="Compellation" runat="server" Width="120" CssClass="form-control" Text='<%# Bind("Compellation") %>' Style="line-height: 1;"></asp:TextBox>
                                </td>
                                <td>
                                    <asp:Label ID="UserNameLabel" runat="server" Text='<%# Eval("UserName") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="EmailConfirmedCheckBox" runat="server" Checked='<%# Bind("EmailConfirmed") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isQACheckBox" runat="server" Checked='<%# Bind("isQA") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isDevelopCheckBox" runat="server" Checked='<%# Bind("isDevelop") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isAdminCheckBox" runat="server" Checked='<%# Bind("isAdmin") %>' />
                                </td>
                                <td>
                                    <%# ((bool)Eval("isQA")&&(bool)Eval("isDevelop")==false&&(bool)Eval("isAdmin")==false)?"<a class=\"btn btn-info \" data-toggle=\"modal\" data-target=\"#divRoleGameManager\" onclick=\"GetRoleGame('"+Eval("ID")+"');\">管理</a>":"" %>                            
                                </td>
                                <td><%#((bool)Eval("isQA")&&(bool)Eval("isDevelop")==false&&(bool)Eval("isAdmin")==false)?GetRoleGameName(Eval("ID").ToString()):"" %></td>
                            </tr>
                        </EditItemTemplate>
                        <EmptyDataTemplate>
                            <table runat="server" style="">
                                <tr>
                                    <td>未返回数据。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>

                        <ItemTemplate>
                            <tr>
                                <td>
                                    <asp:Button ID="EditButton" runat="server" CssClass="btn btn-primary btn-sm fa fa-edit" CommandName="Edit" Text="&#xf044;  编辑" />
                                    <asp:Button ID="DeleteButton" runat="server" CssClass="btn btn-danger btn-sm fa fa-trash" CommandName="Delete" Text="&#xf014; 删除" OnClientClick="return confirm('确定要删除用户吗？')" />
                                </td>
                                <td>
                                    <asp:Label ID="UserCompellation" runat="server" Text='<%# Eval("Compellation") %>' />
                                </td>
                                <td>
                                    <asp:Label ID="UserNameLabel" runat="server" Text='<%# Eval("UserName") %>' />
                                </td>
                                <td>
                                    <asp:CheckBox ID="EmailConfirmedCheckBox" runat="server" Checked='<%# Eval("EmailConfirmed") %>' Enabled="false" />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isQACheckBox" runat="server" Checked='<%# Eval("isQA") %>' Enabled="false" />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isDevelopCheckBox" runat="server" Checked='<%# Eval("isDevelop") %>' Enabled="false" />
                                </td>
                                <td>
                                    <asp:CheckBox ID="isAdminCheckBox" runat="server" Checked='<%# Eval("isAdmin") %>' Enabled="false" />
                                </td>
                                <td>
                                    <%# ((bool)Eval("isQA")&&(bool)Eval("isDevelop")==false&&(bool)Eval("isAdmin")==false)?"<a class=\"btn btn-info \" data-toggle=\"modal\" data-target=\"#divRoleGameManager\" onclick=\"GetRoleGame('"+Eval("ID")+"');\">管理</a>":"" %>   
                                </td>
                                <td><%#((bool)Eval("isQA")&&(bool)Eval("isDevelop")==false&&(bool)Eval("isAdmin")==false)?GetRoleGameName(Eval("ID").ToString()):"" %></td>
                            </tr>
                        </ItemTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <tr>
                                    <th>管理</th>
                                    <th>姓名</th>
                                    <th>邮件地址</th>
                                    <th>邮件绑定</th>
                                    <th>QA</th>
                                    <th>Develop</th>
                                    <th>Admin</th>
                                    <th>游戏权限</th>
                                    <th>游戏名</th>
                                </tr>
                                <tr id="itemPlaceholder" runat="server">
                                </tr>
                            </table>
                        </LayoutTemplate>
                    </asp:ListView>
                    <asp:SqlDataSource ID="SqlDataSourceUser" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="sdk_getUsers" SelectCommandType="StoredProcedure" UpdateCommand="sdk_setUserRole" UpdateCommandType="StoredProcedure" DeleteCommand="sdk_deleteUser" DeleteCommandType="StoredProcedure">
                        <UpdateParameters>
                            <asp:Parameter Name="Id" Type="String" />
                            <asp:Parameter Name="Compellation" Type="String" />
                            <asp:Parameter Name="EmailConfirmed" Type="Boolean" />
                            <asp:Parameter Name="isAdmin" Type="Boolean" />
                            <asp:Parameter Name="isDevelop" Type="Boolean" />
                            <asp:Parameter Name="isQA" Type="Boolean" />
                        </UpdateParameters>
                        <DeleteParameters>
                            <asp:Parameter Name="Id" Type="String" />
                        </DeleteParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>
    </div>

</asp:Content>
