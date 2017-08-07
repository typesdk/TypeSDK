<%@ Page Title="" Language="C#" AutoEventWireup="true" MasterPageFile="/Windows.Master" CodeBehind="AddMyVersion.aspx.cs" Inherits="SDKPackage.PJConfig.AddMyVersion" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
        <div class="form-group" style="margin:20px 0 0 20px;">
            <label>请输入版本号:</label>
            <asp:TextBox ID="txtMyVersion" runat="server" placeholder="1.0" required="required" CssClass="form-control" style=" width:250px;"></asp:TextBox>
        </div>
        <div class="form-group" style="margin:20px 0 0 20px;">
            <asp:Button ID="btnAddMyVersion" runat="server" Text="添加" OnClick="btnAddMyVersion_Click" CssClass="col-md-offset-2 btn btn-primary " />
            <asp:Label ID="lblLog" runat="server" Text="" Style="margin-left: 20px; color: #f00"></asp:Label>
        </div>
</asp:Content>
