<%@ Page Title="帐户确认" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="Confirm.aspx.cs" Inherits="SDKPackage.Account.Confirm" Async="true" %>

<asp:Content runat="server" ID="BodyContent" ContentPlaceHolderID="MainContent">
    <h2><%: Title %>。</h2>

    <div>
        <asp:PlaceHolder runat="server" ID="status" ViewStateMode="Disabled">
            <p><%: StatusMessage %></p>
        </asp:PlaceHolder>
    </div>
</asp:Content>
