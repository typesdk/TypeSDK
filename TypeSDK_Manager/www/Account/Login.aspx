<%@ Page Title="登录" Language="C#" AutoEventWireup="true" CodeBehind="Login.aspx.cs" Inherits="SDKPackage.Account.Login" Async="true" %>

<%@ Register Src="~/Account/OpenAuthProviders.ascx" TagPrefix="uc" TagName="OpenAuthProviders" %>

<!DOCTYPE html>
<html class="bootstrap-admin-vertical-centered">
<head runat="server">
    <title><%: Page.Title %></title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap -->
    <link href="/vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="/vendors/font-awesome/css/font-awesome.min.css" rel="stylesheet">
    <!-- NProgress -->
    <link href="/vendors/nprogress/nprogress.css" rel="stylesheet">
    <!-- Animate.css -->
    <link href="/vendors/animate.css/animate.min.css" rel="stylesheet">

    <!-- Custom Theme Style -->
    <link href="/build/css/custom.min.css" rel="stylesheet">

</head>
<body class="login">
      <div class="login_wrapper">
        <div class="animate form login_form">
          <section class="login_content">
                <asp:PlaceHolder runat="server" ID="ErrorMessage" Visible="false">
                    <div class="alert alert-info">
                        <a class="close" data-dismiss="alert" href="#">&times;</a>
                        <asp:Literal runat="server" ID="FailureText" />
                    </div>
                </asp:PlaceHolder>
                <form runat="server">
                    <h1>登录</h1>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="Email" CssClass="form-control" TextMode="Email" placeholder="E-mail" />
                        <asp:RequiredFieldValidator ID="RequiredFieldValidator1" runat="server" ControlToValidate="Email"
                            CssClass="text-danger" ErrorMessage="“电子邮件”字段是必填字段。" />
                        <div class="right">
                            <asp:HyperLink runat="server" ID="RegisterHyperLink" ViewStateMode="Disabled" Visible="false">没有账号?</asp:HyperLink>
                        </div>
                    </div>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="Password" TextMode="Password" CssClass="form-control" />
                        <asp:RequiredFieldValidator ID="RequiredFieldValidator2" runat="server" ControlToValidate="Password"
                            CssClass="text-danger" ErrorMessage="“密码”字段是必填字段。" />
                        <div class="right">
                            <asp:HyperLink runat="server" ID="ForgotPasswordHyperLink" ViewStateMode="Disabled">忘记了密码?</asp:HyperLink>
                        </div>
                    </div>
                    <div class="form-group">
                    <asp:Button ID="Button1" runat="server" OnClick="LogIn" Text="登录" CssClass="btn btn-default submit" style="float:none;"/>
                        <label>
                            <asp:CheckBox runat="server" ID="RememberMe" />
                            记住登录信息
                        </label>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!-- jQuery -->
    <script src="/vendors/jquery/dist/jquery.min.js"></script>
    <!-- Bootstrap -->
    <script src="/vendors/bootstrap/dist/js/bootstrap.min.js"></script>
    <script type="text/javascript">
        $(function () {
            // Setting focus
            $('input[name="Email"]').focus();

        });
    </script>
</body>
</html>
