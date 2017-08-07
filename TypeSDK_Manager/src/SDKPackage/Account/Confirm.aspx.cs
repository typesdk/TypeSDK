using System;
using System.Web;
using System.Web.UI;
using System.Net.Mail;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using Owin;
using SDKPackage.Models;

namespace SDKPackage.Account
{
    public partial class Confirm : Page
    {
        protected string StatusMessage
        {
            get;
            private set;
        }

        protected void Page_Load(object sender, EventArgs e)
        {
            string code = IdentityHelper.GetCodeFromRequest(Request);
            string userId = IdentityHelper.GetUserIdFromRequest(Request);
            if (code != null && userId != null)
            {
                var manager = Context.GetOwinContext().GetUserManager<ApplicationUserManager>();
                var result = manager.ConfirmEmail(userId, code);
                if (result.Succeeded)
                {
                    StatusMessage = "感谢你确认帐户，请等待管理员进行权限审核。";
                    SendMailUseGmail();
                    return;
                }
            }

            StatusMessage = "出现错误";
        }

        private string SendMailUseGmail()
        {
            string fromMail = "cs@typesdk.com";
            string fromUser = "渠道打包开发组";
            System.Net.Mail.MailMessage msg = new System.Net.Mail.MailMessage();
            msg.To.Add("cs@typesdk.com");
            msg.From = new MailAddress(fromMail, fromUser, System.Text.Encoding.UTF8);
            /* 上面3个参数分别是发件人地址（可以随便写），发件人姓名，编码*/
            msg.Subject = "申请渠道打包平台账号";//邮件标题 
            msg.SubjectEncoding = System.Text.Encoding.UTF8;//邮件标题编码 
            msg.Body = "有人申请了打包渠道账号";//邮件内容 
            msg.BodyEncoding = System.Text.Encoding.UTF8;//邮件内容编码 
            msg.IsBodyHtml = false;//是否是HTML邮件 
            msg.Priority = MailPriority.High;//邮件优先级 
            SmtpClient client = new SmtpClient();
            client.Credentials = new System.Net.NetworkCredential("cs@typesdk.com", "ABC@typesdk.com");
            client.Port = 25;//Gmail使用的端口 
            client.Host = "c1.icoremail.net";
            client.EnableSsl = true;//经过ssl加密 
            //object userState = msg;
            try
            {
                //client.SendAsync(msg, userState);
                //简单一点儿可以
                client.Send(msg); 
                return ("验证邮件已发送");
            }
            catch (System.Net.Mail.SmtpException ex)
            {
                return ("验证邮件发送失败");
            }
        }
    }
}