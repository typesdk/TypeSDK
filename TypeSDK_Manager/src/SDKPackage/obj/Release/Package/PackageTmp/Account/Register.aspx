<%@ Page Title="注册" Language="C#" AutoEventWireup="true" CodeBehind="Register.aspx.cs" Inherits="SDKPackage.Account.Register" %>



<!DOCTYPE html>
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
                    <h1>注册</h1>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="Email" CssClass="form-control" TextMode="Email" placeholder="E-mail" />
                        <asp:RequiredFieldValidator runat="server" ControlToValidate="Email"
                            CssClass="text-danger" ErrorMessage="“电子邮件”字段是必填字段。" />
                    </div>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="Password" TextMode="Password" CssClass="form-control" placeholder="密码" />
                        <asp:RequiredFieldValidator runat="server" ControlToValidate="Password"
                            CssClass="text-danger" ErrorMessage="“密码”字段是必填字段。" />
                    </div>
                    <div class="form-group">
                        <asp:TextBox runat="server" ID="ConfirmPassword" TextMode="Password" CssClass="form-control" placeholder="确认密码" />
                        <asp:RequiredFieldValidator runat="server" ControlToValidate="ConfirmPassword"
                            CssClass="text-danger" Display="Dynamic" ErrorMessage="“确认密码”字段是必填字段。" />
                        <asp:CompareValidator runat="server" ControlToCompare="Password" ControlToValidate="ConfirmPassword"
                            CssClass="text-danger" Display="Dynamic" ErrorMessage="密码和确认密码不匹配。" />
                    </div>
                    <div class="form-group">
                        <asp:Button runat="server" OnClick="CreateUser_Click" Text="注册" CssClass="btn btn-default btn-block submit" style="float:none; margin-left:0px;" />

                    </div>
                    <asp:ValidationSummary runat="server" CssClass="text-danger" />
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
