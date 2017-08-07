using System;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Net.Mail;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Owin;
using SDKPackage.Models;
using SDKPackage.Utils;

namespace SDKPackage.Account
{
    public partial class Register : Page
    {
        protected void CreateUser_Click(object sender, EventArgs e)
        {
            var manager = Context.GetOwinContext().GetUserManager<ApplicationUserManager>();
            var user = new ApplicationUser() { UserName = Email.Text, Email = Email.Text };
            IdentityResult result = manager.Create(user, Password.Text);
            if (result.Succeeded)
            {
                IdentityHelper.SignIn(manager, user, isPersistent: false);

                // 有关如何启用帐户确认和密码重置的详细信息，请访问 http://go.microsoft.com/fwlink/?LinkID=320771
                string code = manager.GenerateEmailConfirmationToken(user.Id);
                code = Server.UrlEncode(code);
                string callbackUrl = IdentityHelper.GetUserConfirmationRedirectUrl(code, user.Id);
                //manager.SendEmail(user.Id, "渠道打包平台注册邮件地址确认", "您刚申请了渠道打包平台账号，请通过单击 <a href=\"" + callbackUrl + "\">此处 </a> 来确认你的帐户。如非本人申请请勿点击链接。");
                string context = "您刚申请了打包平台账号，请通过单击 <a href=\"{0}\">此处 </a> 来确认你的帐户。如非本人申请请勿点击链接。";
                string SendMail = SendEamil.SendMailUseGmail(user.Email, code, callbackUrl, context);
                if (SendMail == "验证邮件发送失败")
                {

                    FailureText.Text = SendMail;
                    ErrorMessage.Visible = true;
                }
                else
                {
                    IdentityHelper.RedirectToReturnUrl(Request.QueryString["ReturnUrl"], Response);
                }
            }
            else
            {
                FailureText.Text = result.Errors.FirstOrDefault();
                ErrorMessage.Visible = true;
            }

        }

    }
}