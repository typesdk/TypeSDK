using System;
using System.Web;
using System.Web.UI;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Owin;
using SDKPackage.Models;
using System.Net.Mail;
using SDKPackage.Utils;

namespace SDKPackage.Account
{
    public partial class ForgotPassword : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
        }

        protected void Forgot(object sender, EventArgs e)
        {
            if (IsValid)
            {
                // 验证用户密码
                var manager = Context.GetOwinContext().GetUserManager<ApplicationUserManager>();
                ApplicationUser user = manager.FindByName(Email.Text);
                if (user == null || !manager.IsEmailConfirmed(user.Id))
                {
                    FailureText.Text = "用户不存在或未确认。";
                    ErrorMessage.Visible = true;
                    return;
                }
                // 有关如何启用帐户确认和密码重置的详细信息，请访问 http://go.microsoft.com/fwlink/?LinkID=320771
                // 发送包含此代码和重定向到“重置密码”页的电子邮件
                string code = manager.GeneratePasswordResetToken(user.Id);
                code = Server.UrlEncode(code);
                string callbackUrl = IdentityHelper.GetResetPasswordRedirectUrl(code);
                string context = "您刚申请了打包平台密码重置,请通过单击 <a href=\"{0}\">此处</a> 来重置你的密码。如非本人申请请勿点击链接。";
                //manager.SendEmail(user.Id, "重置密码", "请通过单击 <a href=\"" + callbackUrl + "\">此处</a> 来重置你的密码。");
                string message = SendEamil.SendMailUseGmail(Email.Text, code, callbackUrl, context);//SendEamil.SendEmail(Email.Text, code, callbackUrl, context);
                this.FailureText.Text = message;
                ErrorMessage.Visible = true;
            }

        }
    }
}