using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace SDKPackage.Utils
{
    public class SendHelper
    {
        /// <summary>
        /// 短信发送
        /// </summary>
        /// <param name="mobile">电话号</param>
        /// <param name="txt">内容</param>
        /// <returns></returns>
        public static int SendNote(string mobile, string txt)
        {
            //string sign = " 【新城地带】";
            //JianzhouScrvice.BusinessService client = new JianzhouScrvice.BusinessServiceClient();
            ////////账号验证
            ////////JianzhouWebservice.validateUserBody vub = new JianzhouWebservice.validateUserBody();
            ////////vub.account = "";
            ////////vub.password = "";
            ////////JianzhouWebservice.validateUser validateUser=new JianzhouWebservice.validateUser(vub);
            ////////JianzhouWebservice.validateUserResponse resp = client.validateUser(validateUser);
            ////////JianzhouWebservice.validateUserResponseBody body = resp.Body;
            ////////Response.Write(body.validateUserReturn.ToString());

            ////短信发送
            ////普通短信发送，如多个号码有;分隔，编码为UTF-8
            //JianzhouScrvice.sendBatchMessageBody sbmb = new JianzhouScrvice.sendBatchMessageBody();
            //sbmb.account = "sdk_xincheng";//账号
            //sbmb.password = "60297859";//密码
            //sbmb.destmobile = mobile;//手机号
            //sbmb.msgText = txt + sign;//内容+签名
            //JianzhouScrvice.sendBatchMessage req = new JianzhouScrvice.sendBatchMessage(sbmb);
            //JianzhouScrvice.sendBatchMessageResponse resp = client.sendBatchMessage(req);
            //JianzhouScrvice.sendBatchMessageResponseBody body = resp.Body;
            //return body.sendBatchMessageReturn;
            return 0;
        }


        public bool SendEmail(string email, string txt)
        {
            return true;
        }

        /// <summary>
        /// 信天下短信接口
        /// </summary>
        /// <param name="mobile"></param>
        /// <param name="strContent"></param>
        /// <param name="sendTime"></param>
        /// <returns></returns>
        public static string XtxSendNote(string mobile, string strContent, string sendTime)
        {
            string xtxUser = XtxSMSConfig.XtxUser;
            string xtxPwd = XtxSMSConfig.XtxPwd;
            string xtxID = XtxSMSConfig.XtxID;
            //发送短信
            string param = string.Format("action=send&userid={0}&account={1}&password={2}&content={3}&mobile={4}&sendtime={5}",
                                         xtxID, xtxUser, xtxPwd, strContent, mobile, string.IsNullOrEmpty(sendTime) ? "" : sendTime);

            byte[] bs = Encoding.UTF8.GetBytes(param);

            HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create("http://xtx.telhk.cn:8888/sms.aspx");
            req.Method = "POST";
            req.ContentType = "application/x-www-form-urlencoded";
            req.ContentLength = bs.Length;

            using (Stream reqStream = req.GetRequestStream())
            {
                reqStream.Write(bs, 0, bs.Length);
            }
            string strReturn = string.Empty;
            using (WebResponse wr = req.GetResponse())
            {
                StreamReader sr = new StreamReader(wr.GetResponseStream(), System.Text.Encoding.UTF8);
                strReturn = sr.ReadToEnd().Trim();
            }
            return strReturn;
        }

    }


    public class XtxSMSConfig
    {
        private static string xtxUser;
        private static string xtxPwd;
        private static string xtxID;
        static XtxSMSConfig()
        {
            xtxUser = ApplicationSettings.Get("xtxUser");
            xtxPwd = ApplicationSettings.Get("xtxPwd");
            xtxID = ApplicationSettings.Get("xtxID");
        }

        public static string XtxUser
        {
            set { xtxUser = value; }
            get { return xtxUser; }
        }

        public static string XtxPwd
        {
            set { xtxPwd = value; }
            get { return xtxPwd; }
        }

        public static string XtxID
        {
            set { xtxID = value; }
            get { return xtxID; }
        }
    }
}

