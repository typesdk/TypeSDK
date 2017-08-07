using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Mail;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using System.Configuration;

namespace SDKPackage.Utils
{
    public static class SendEamil
    {
        public static void SetCertificatePolicy()
        {
            ServicePointManager.ServerCertificateValidationCallback
                       += RemoteCertificateValidate;
        }

        private static bool RemoteCertificateValidate(
         object sender, X509Certificate cert,
          X509Chain chain, SslPolicyErrors error)
        {
            // trust any certificate!!!
            //System.Console.WriteLine("Warning, trust any certificate");
            return true;
        }

        public static string SendMailUseGmail(string toMail, string code, string callbackUrl, string context)
        {
            SetCertificatePolicy();
            string fromMail = ConfigurationManager.AppSettings["MailSendEmail"];
            string fromUser = ConfigurationManager.AppSettings["MailSendDisplay"];
            System.Net.Mail.MailMessage msg = new System.Net.Mail.MailMessage();
            msg.To.Add(toMail);
            msg.CC.Add(fromMail);

            msg.From = new MailAddress(fromMail, fromUser, System.Text.Encoding.UTF8);
            /* 上面3个参数分别是发件人地址（可以随便写），发件人姓名，编码*/
            msg.Subject = "打包平台邮件账号确认";//邮件标题 
            msg.SubjectEncoding = System.Text.Encoding.UTF8;//邮件标题编码
            string port = HttpContext.Current.Request.Url.Port.ToString();
            if (port == "80")
            {
                callbackUrl = "http://" + HttpContext.Current.Request.Url.Host + callbackUrl;
            }
            else
            {
                callbackUrl = "http://" + HttpContext.Current.Request.Url.Host + ":" + port + callbackUrl;
            }
            msg.Body = context.Replace("{0}", callbackUrl);//"您刚申请了渠道打包平台账号，请通过单击 <a href=\"" + callbackUrl + "\">此处 </a> 来确认你的帐户。如非本人申请请勿点击链接。";//邮件内容 
            msg.BodyEncoding = System.Text.Encoding.UTF8;//邮件内容编码 
            msg.IsBodyHtml = true;//是否是HTML邮件 
            msg.Priority = MailPriority.High;//邮件优先级 
            SmtpClient client = new SmtpClient();
            string mailAccount = ConfigurationManager.AppSettings["MailAccount"];
            string mailPasswd = ConfigurationManager.AppSettings["MailPasswd"];
            string mailHost = ConfigurationManager.AppSettings["MailHost"];
            string mailPort = ConfigurationManager.AppSettings["MailPort"];
            string mailSSL = ConfigurationManager.AppSettings["true"];
            client.Credentials = new System.Net.NetworkCredential(mailAccount, mailPasswd);
            client.Port = Convert.ToInt32(mailPort);//Gmail使用的端口 
            client.Host = mailHost;
            client.EnableSsl = Convert.ToBoolean(mailSSL);//经过ssl加密 
            try
            {
                try
                {
                    client.Send(msg);
                }
                catch
                {
                    return ("邮箱错误或不存在，邮件发送失败");
                }
                return ("验证邮件已发送");
            }
            catch (System.Net.Mail.SmtpException ex)
            {
                return ("验证邮件发送失败");
            }
        }


        public static string SendMailUseGmail(string toMail, string subject, string context)
        {
            SetCertificatePolicy();
            string fromMail = ConfigurationManager.AppSettings["MailSendEmail"];
            string fromUser = ConfigurationManager.AppSettings["MailSendDisplay"];
            System.Net.Mail.MailMessage msg = new System.Net.Mail.MailMessage();
            msg.To.Add(toMail);
            msg.CC.Add(fromMail);

            msg.From = new MailAddress(fromMail, fromUser, System.Text.Encoding.UTF8);
            /* 上面3个参数分别是发件人地址（可以随便写），发件人姓名，编码*/
            msg.Subject = subject;//邮件标题 
            msg.SubjectEncoding = System.Text.Encoding.UTF8;//邮件标题编码
            string port = HttpContext.Current.Request.Url.Port.ToString();
            msg.Body = context;
            msg.BodyEncoding = System.Text.Encoding.UTF8;//邮件内容编码 
            msg.IsBodyHtml = true;//是否是HTML邮件 
            msg.Priority = MailPriority.High;//邮件优先级 
            SmtpClient client = new SmtpClient();
            string mailAccount = ConfigurationManager.AppSettings["MailAccount"];
            string mailPasswd = ConfigurationManager.AppSettings["MailPasswd"];
            string mailHost = ConfigurationManager.AppSettings["MailHost"];
            string mailPort = ConfigurationManager.AppSettings["MailPort"];
            string mailSSL = ConfigurationManager.AppSettings["true"];
            client.Credentials = new System.Net.NetworkCredential(mailAccount, mailPasswd);
            client.Port = Convert.ToInt32(mailPort);//Gmail使用的端口 
            client.Host = mailHost;
            client.EnableSsl = Convert.ToBoolean(mailSSL);//经过ssl加密 
            try
            {

                try
                {
                    client.Send(msg);
                }
                catch
                {
                    return ("邮箱错误或不存在，邮件发送失败");
                }
                return ("验证邮件已发送");
            }
            catch (System.Net.Mail.SmtpException ex)
            {
                return ("验证邮件发送失败");
            }
        }
    }
}
