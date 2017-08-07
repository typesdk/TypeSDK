using System;
using System.Web;
using System.Web.UI;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Owin;
using SDKPackage.Models;

namespace SDKPackage.Account
{
    public partial class Login : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            RegisterHyperLink.NavigateUrl = "Register";
            // 在为密码重置功能启用帐户确认后，启用此项
            ForgotPasswordHyperLink.NavigateUrl = "Forgot";
            //OpenAuthLogin.ReturnUrl = Request.QueryString["ReturnUrl"];
            var returnUrl = HttpUtility.UrlEncode(Request.QueryString["ReturnUrl"]);
            if (!String.IsNullOrEmpty(returnUrl))
            {
                RegisterHyperLink.NavigateUrl += "?ReturnUrl=" + returnUrl;
            }
        }

        protected void LogIn(object sender, EventArgs e)
        {
            if (IsValid)
            {
                // 验证用户密码
                var manager = Context.GetOwinContext().GetUserManager<ApplicationUserManager>();
                ApplicationUser user = manager.Find(Email.Text, Password.Text);
                if (user != null)
                {
                    if (user.EmailConfirmed)
                    {
                        IdentityHelper.SignIn(manager, user, RememberMe.Checked);
                        Cache["Roleid"] = "";
                        IdentityHelper.RedirectToReturnUrl(Request.QueryString["ReturnUrl"], Response);
                    }
                    else
                    {
                        FailureText.Text = "邮箱尚未进行确认。";
                        ErrorMessage.Visible = true;
                    }
                }
                else
                {
                    FailureText.Text = "无效的用户名或密码。";
                    ErrorMessage.Visible = true;
                }
            }
        }
    }
}