using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Configuration;

namespace SDKPackage.Utils
{
    public class StringFilter
    {
        public StringFilter() { }
        /// <summary>
        /// 验证Email地址格式
        /// </summary>
        /// <param name="email">Email地址</param>
        /// <returns>true合法email,false非法email</returns>
        public static bool IsValidEmail(string email)
        {
            if (string.IsNullOrEmpty(email)) return false;
            string RegexStr = @"^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$";
            Regex regex = new Regex(RegexStr);
            if (regex.IsMatch(email))
                return true;
            else
                return false;
        }

        public static bool SendMailWithCC(string subject, string body, string mailto, string filepath)
        {
            System.Net.Mail.SmtpClient client = new System.Net.Mail.SmtpClient();

            string strHost = ConfigurationSettings.AppSettings["MailHost"];  //STMP服务器地址
            string strAccount = ConfigurationSettings.AppSettings["MailAccount"];       //SMTP服务帐号
            string strPwd = ConfigurationSettings.AppSettings["MailPwd"];   //SMTP服务密码
            string strFrom = ConfigurationSettings.AppSettings["MailFrom"];   //发送方邮件地址

            client.Host = strHost;
            client.UseDefaultCredentials = false;
            client.Credentials = new System.Net.NetworkCredential(strAccount, strPwd);
            //星号改成自己邮箱的密码
            client.DeliveryMethod = System.Net.Mail.SmtpDeliveryMethod.Network;
            System.Net.Mail.MailMessage message = new System.Net.Mail.MailMessage(strAccount, mailto);
            message.Subject = subject;
            message.Body = body;
            message.BodyEncoding = System.Text.Encoding.UTF8;
            message.IsBodyHtml = true;
            message.CC.Add("jifenghua@fengpu.com");
            //添加附件
            if (!string.IsNullOrEmpty(filepath))
            {
                System.Net.Mail.Attachment data = new System.Net.Mail.Attachment(filepath, System.Net.Mime.MediaTypeNames.Application.Octet);
                message.Attachments.Add(data);
            }

            try
            {
                client.Send(message);
                return true;
                //MessageBox.Show("Email successfully send.");
            }
            catch (Exception ex)
            {
                return false;
                //MessageBox.Show("Send Email Failed." + ex.ToString());
            }
        }

        /// <summary>
        /// 发送邮件
        /// </summary>
        /// <param name="sendTo"></param>
        /// <param name="subject"></param>
        /// <param name="body"></param>
        /// <returns></returns>
        public static bool SendEmail(string subject, string body, string mailto, string filepath)
        {
            try
            {
                System.Net.Mail.SmtpClient client = new System.Net.Mail.SmtpClient();

                string strHost = ConfigurationSettings.AppSettings["MailHost"];  //STMP服务器地址
                string strAccount = ConfigurationSettings.AppSettings["MailAccount"];       //SMTP服务帐号
                string strPwd = ConfigurationSettings.AppSettings["MailPwd"];   //SMTP服务密码

                client.Host = strHost;
                client.UseDefaultCredentials = false;
                client.Credentials = new System.Net.NetworkCredential(strAccount, strPwd);
                //星号改成自己邮箱的密码
                client.DeliveryMethod = System.Net.Mail.SmtpDeliveryMethod.Network;
                System.Net.Mail.MailMessage message = new System.Net.Mail.MailMessage(strAccount, mailto);
                message.Subject = subject;
                message.Body = body;
                message.BodyEncoding = System.Text.Encoding.UTF8;
                message.IsBodyHtml = true;
                //添加附件
                if (!string.IsNullOrEmpty(filepath))
                {
                    System.Net.Mail.Attachment data = new System.Net.Mail.Attachment(filepath, System.Net.Mime.MediaTypeNames.Application.Octet);
                    message.Attachments.Add(data);
                }


                client.Send(message);
                return true;
                //MessageBox.Show("Email successfully send.");
            }
            catch (Exception ex)
            {
                return false;
                //MessageBox.Show("Send Email Failed." + ex.ToString());
            }
        }
    }
}
