
<%@ Page Title="密钥管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="KeyConfig.aspx.cs" Inherits="SDKPackage.PJConfig.KeyConfig" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>密钥管理</h2>
					<ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
				<div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <a href="#addKey" class="btn btn-primary" data-toggle="modal">添加新的密钥</a>
                    <asp:Label ID="LabelLog" runat="server" Text=""></asp:Label>
                    <asp:ListView ID="ListView1" runat="server" DataSourceID="SqlDataSourceSignekey" DataKeyNames="Id">
                        <EmptyDataTemplate>
                            <table runat="server">
                                <tr>
                                    <td>未返回数据。</td>
                                </tr>
                            </table>
                        </EmptyDataTemplate>
                        <ItemTemplate>
                            <tr>
                                <td><%# Eval("KeyName") %>
                                </td>
                                <td><%# Eval("KeyStore") %>
                                </td>
                                <td><%# Eval("KeyAlias") %>
                                </td>
                                <td>
                                    <asp:Button ID="DeleteButton" runat="server" CommandName="Delete" class="btn btn-danger btn-sm fa fa-trash" Text="&#xf014; 删除" OnClientClick="return confirm('确定要删除数据吗？')" />
                                </td>
                            </tr>
                        </ItemTemplate>
                        <LayoutTemplate>
                            <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                <thead>
                                <tr>
                                    <th>密钥名称</th>
                                    <th>文件位置</th>
                                    <th>密钥别名</th>
                                    <th>操作</th>
                                </tr>
                                </thead>
                                <tr id="itemPlaceholder" runat="server">
                                </tr>
                            </table>
                        </LayoutTemplate>
                    </asp:ListView>

                    <asp:SqlDataSource ID="SqlDataSourceSignekey" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" DeleteCommand="DELETE FROM [sdk_SignatureKey] WHERE [Id] = @Id" InsertCommand="INSERT INTO [sdk_SignatureKey] ([KeyName], [KeyStore], [KeyStorePassword], [KeyAlias], [KeyAliasPassword]) VALUES (@KeyName, @KeyStore, @KeyStorePassword, @KeyAlias, @KeyAliasPassword)" SelectCommand="SELECT * FROM [sdk_SignatureKey]" UpdateCommand="UPDATE [sdk_SignatureKey] SET [KeyName] = @KeyName, [KeyStore] = @KeyStore, [KeyStorePassword] = @KeyStorePassword, [KeyAlias] = @KeyAlias, [KeyAliasPassword] = @KeyAliasPassword WHERE [Id] = @Id">
                        <DeleteParameters>
                            <asp:Parameter Name="Id" Type="Int32" />
                        </DeleteParameters>
                    </asp:SqlDataSource>
                </div>
            </div>
        </div>
    </div>
    <div id="addKey" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="addKeyLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        ×
                    </button>
                    <h4 class="modal-title" id="addKeyLabel">添加新签名密钥
                    </h4>
                    
                </div>
                <div class="modal-body">
                    <div class="form-horizontal">
                        <div class="form-group">
                            <label class="col-md-2 control-label">选择密钥</label>
                            <div class="col-md-6">
                                <asp:FileUpload ID="KeyFileUpload" runat="server" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">密钥名称</label>
                            <div class="col-md-10">
                                <asp:TextBox ID="KeyNameBox" runat="server" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">密钥密码</label>
                            <div class="col-md-10">
                                <asp:TextBox ID="KeyStorePasswordBox" runat="server" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">密钥别名</label>
                            <div class="col-md-10">
                                <asp:TextBox ID="KeyAliasBox" runat="server" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">别名密码</label>
                            <div class="col-md-10">
                                <asp:TextBox ID="KeyAliasPasswordTextBox" runat="server" CssClass="form-control"></asp:TextBox>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">
                        关闭
                    </button>
                    <asp:Button ID="ButtonAddKey" runat="server" class="btn btn-primary" Text="保存" OnClick="ButtonAddKey_Click" />
                </div>
            </div>
        </div>
    </div>
</asp:Content>
