<%@ Control Language="C#" AutoEventWireup="true" CodeBehind="OpenAuthProviders.ascx.cs" Inherits="SDKPackage.Account.OpenAuthProviders" %>

<div id="socialLoginList">
    <h4>使用其他服务登录。</h4>
    <hr />
    <asp:ListView runat="server" ID="providerDetails" ItemType="System.String"
        SelectMethod="GetProviderNames" ViewStateMode="Disabled">
        <ItemTemplate>
            <p>
                <button type="submit" class="btn btn-default" name="provider" value="<%#: Item %>"
                    title="使用你的 <%#: Item %> 帐户登录。">
                    <%#: Item %>
                </button>
            </p>
        </ItemTemplate>
        <EmptyDataTemplate>
            <div>
                <p>未配置外部身份验证服务。请参见<a href="http://go.microsoft.com/fwlink/?LinkId=252803">此文章</a>，以详细了解如何将此 ASP.NET 应用程序设置为支持通过外部服务登录。</p>
            </div>
        </EmptyDataTemplate>
    </asp:ListView>
</div>
