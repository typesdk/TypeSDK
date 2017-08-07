<%@ Page Title="忘记密码" Language="C#" AutoEventWireup="true" CodeBehind="Forgot.aspx.cs" Inherits="SDKPackage.Account.ForgotPassword" Async="true" %>

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
                    <h1>找回密码</h1>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="Email" CssClass="form-control" TextMode="Email" placeholder="E-mail" />
                        <asp:RequiredFieldValidator runat="server" ControlToValidate="Email"
                            CssClass="text-danger" ErrorMessage="“电子邮件”字段是必填字段。" />
                    </div>
                    <div class="form-group">
                        <asp:Button runat="server" OnClick="Forgot" Text="提交" CssClass="btn btn-default btn-block submit" ID="ButtonForgot" style="float:none; margin-left:0px;"/>
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
            $("#MainContent_ButtonForgot").click(function () {
                $(this).val("邮件发送中");
            });
            $(".text-danger").change(function () {
                $("#MainContent_ButtonForgot").val("提交");
            });
        })
        $(function () {
            // Setting focus
            $('input[name="email"]').focus();
            // Setting width of the alert box
            var alert = $('.alert');
            var formWidth = $('form').innerWidth();
            var alertPadding = parseInt($('.alert').css('padding'));

            if (isNaN(alertPadding)) {
                alertPadding = parseInt($(alert).css('padding-left'));
            }
            $('.alert').width(formWidth - 2 * alertPadding);
        });
    </script>
</body>
</html>
