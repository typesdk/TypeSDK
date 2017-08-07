<%@ Page Title="游戏信息设置" Language="C#" MasterPageFile="~/Site.Master" AutoEventWireup="true" CodeBehind="GameConfig.aspx.cs" Inherits="SDKPackage.GameConfig.GameVersion" %>
<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <hr />
    <div class="row">
    <asp:ListView ID="ListView1" runat="server" DataKeyNames="Id" DataSourceID="SqlDataSourceGame">
        <AlternatingItemTemplate>
            <table runat="server">
                <tr>
                    <td style="min-width:100px">
                        游戏序号:
                    </td>
                    <td>
                        <asp:Label ID="IdLabel" runat="server" Text='<%# Eval("Id") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏代号:
                    </td>
                    <td>
                        <asp:Label ID="GameNameLabel" runat="server" Text='<%# Eval("GameName") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏名称:
                    </td>
                    <td>
                        <asp:Label ID="Label1" runat="server" Text='<%# Eval("GameDisplayName") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏官网:
                    </td>
                    <td>
                        <asp:HyperLink ID="GameWebSiteHyperLink" runat="server" NavigateUrl='<%# Eval("GameWebSite") %>'><%# Eval("GameWebSite") %></asp:HyperLink>
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏介绍:
                    </td>
                    <td>
                        <span>
                            <%# Eval("GameIntroduce") %>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td>
                        介绍图片:
                    </td>
                    <td>
                        <asp:Image ID="GamePicImage" runat="server" ImageUrl='<%# Eval("GamePic") %>' />
                    </td>
                </tr>
            </table>
            <br />
            <asp:Button ID="EditButton" runat="server" CommandName="Edit" Text="编辑" />
        </AlternatingItemTemplate>
        <EditItemTemplate>
            <div class="form-horizontal col-md-8">
                <div class="form-group">
                    <asp:Label ID="Label1" runat="server" CssClass="control-label col-md-2">游戏序号</asp:Label>
                    <div class="col-md-10">
                        <asp:Label ID="IdLabel1" runat="server" CssClass="control-label"><%# Eval("Id") %></asp:Label>
                    </div>
                </div>
                <div class="form-group">
                    <asp:Label ID="Label2" runat="server" CssClass="control-label col-md-2">游戏代号</asp:Label>
                    <div class="col-md-10">
                        <asp:TextBox ID="GameNameTextBox" runat="server" CssClass="form-control" Text='<%# Bind("GameName") %>' />
                    </div>
                </div>
                <div class="form-group">
                    <asp:Label ID="Label3" runat="server" CssClass="control-label col-md-2">游戏名称</asp:Label>
                    <div class="col-md-10">
                        <asp:TextBox ID="GameDisplayNameTextBox" runat="server" CssClass="form-control" Text='<%# Bind("GameDisplayName") %>' />
                    </div>
                </div>
                <div class="form-group">
                    <asp:Label ID="Label4" runat="server" CssClass="control-label col-md-2">介绍图片</asp:Label>
                    <div class="col-md-10">
                        <asp:TextBox ID="GamePicTextBox" runat="server" CssClass="form-control" Text='<%# Bind("GamePic") %>' />
                    </div>
                </div>
                <div class="form-group">
                    <asp:Label ID="Label5" runat="server" CssClass="control-label col-md-2">介绍图片</asp:Label>
                    <div class="col-md-10">
                        <asp:TextBox ID="GameIntroduceTextBox" runat="server" CssClass="form-control" Text='<%# Bind("GameIntroduce") %>' />
                    </div>
                </div>
                <div class="form-group">
                    <asp:Label ID="Label6" runat="server" CssClass="control-label col-md-2">游戏官网</asp:Label>
                    <div class="col-md-10">
                        <asp:TextBox ID="GameWebSiteTextBox" runat="server" CssClass="form-control" Text='<%# Bind("GameWebSite") %>' />
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-offset-2 col-md-10">
                        <asp:Button ID="Button1" runat="server" CssClass="btn btn-default" CommandName="Update" Text="保存" />
                        <asp:Button ID="Button2" runat="server" CssClass="btn btn-default" CommandName="Cancel" Text="取消" />
                    </div>
                </div>
            </div>
        </EditItemTemplate>
        <EmptyDataTemplate>
            <span>未返回数据。</span>
        </EmptyDataTemplate>
        <ItemTemplate>
                        <table runat="server">
                <tr>
                    <td style="min-width:100px">
                        游戏序号:
                    </td>
                    <td>
                        <asp:Label ID="IdLabel" runat="server" Text='<%# Eval("Id") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏代号:
                    </td>
                    <td>
                        <asp:Label ID="GameNameLabel" runat="server" Text='<%# Eval("GameName") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏名称:
                    </td>
                    <td>
                        <asp:Label ID="Label1" runat="server" Text='<%# Eval("GameDisplayName") %>' />
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏官网:
                    </td>
                    <td>
                        <asp:HyperLink ID="GameWebSiteHyperLink" runat="server" NavigateUrl='<%# Eval("GameWebSite") %>'><%# Eval("GameWebSite") %></asp:HyperLink>
                    </td>
                </tr>
                <tr>
                    <td>
                        游戏介绍:
                    </td>
                    <td>
                        <span>
                            <%# Eval("GameIntroduce") %>
                        </span>
                    </td>
                </tr>
                <tr>
                    <td>
                        介绍图片:
                    </td>
                    <td>
                        <asp:Image ID="GamePicImage" runat="server" ImageUrl='<%# Eval("GamePic") %>' />
                    </td>
                </tr>
            </table>
            <br />
            <asp:Button ID="EditButton" runat="server" CommandName="Edit" CssClass="btn btn-default" Text="编辑" />
        </ItemTemplate>
        <LayoutTemplate>
            <div id="itemPlaceholderContainer" runat="server" style="">
                <span runat="server" id="itemPlaceholder" />
            </div>
            <div style="">
            </div>
        </LayoutTemplate>
    </asp:ListView>
    <asp:SqlDataSource ID="SqlDataSourceGame" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>" SelectCommand="SELECT * FROM [sdk_Games] WHERE ([Id] = @Id)" UpdateCommand="UPDATE [sdk_Games] SET [GameName] = @GameName, [GameDisplayName] = @GameDisplayName, [GamePic] = @GamePic, [GameIntroduce] = @GameIntroduce, [GameWebSite] = @GameWebSite WHERE [Id] = @Id">
        <SelectParameters>
            <asp:QueryStringParameter Name="Id" QueryStringField="id" Type="Int32" />
        </SelectParameters>
        <UpdateParameters>
            <asp:Parameter Name="GameName" Type="String" />
            <asp:Parameter Name="GameDisplayName" Type="String" />
            <asp:Parameter Name="GamePic" Type="String" />
            <asp:Parameter Name="GameIntroduce" Type="String" />
            <asp:Parameter Name="GameWebSite" Type="String" />
            <asp:Parameter Name="Id" Type="Int32" />
        </UpdateParameters>
    </asp:SqlDataSource>
        </div>
</asp:Content>
