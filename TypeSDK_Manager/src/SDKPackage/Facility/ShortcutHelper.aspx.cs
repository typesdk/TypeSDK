using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.Facility
{
    public partial class ShortcutHelper : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        /// <summary>
        /// svn更新sdk代码
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonUpdateSVN_Click(object sender, EventArgs e)
        {
            string getUrl = "http://192.168.1.6/updateSDK";
            string jsonData = HttpGet(getUrl);
            JsonData msg = JsonConvert.DeserializeObject<JsonData>(jsonData);
            if (msg.Status == "1000")
            {
                string sl = "<script language='javascript' type='text/javascript'>openWindowSelf();</script>";
                Page.ClientScript.RegisterStartupScript(ClientScript.GetType(), "mya", sl);
            }
            else
            {
                string sl = "<script language='javascript' type='text/javascript'>alert('" + msg.message + "');</script>";
                Page.ClientScript.RegisterStartupScript(ClientScript.GetType(), "mya", sl);
            }
        }

        private string HttpGet(string getUrl)
        {
            string strResult = "";
            try
            {
                HttpWebRequest httpWebRequest = (HttpWebRequest)WebRequest.Create(getUrl);

                httpWebRequest.ContentType = "application/json";
                httpWebRequest.Method = "GET";
                httpWebRequest.Timeout = 40000;

                //byte[] btBodys = Encoding.UTF8.GetBytes(body);
                //httpWebRequest.ContentLength = btBodys.Length;
                //httpWebRequest.GetRequestStream().Write(btBodys, 0, btBodys.Length);

                HttpWebResponse httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();
                StreamReader streamReader = new StreamReader(httpWebResponse.GetResponseStream());
                strResult = streamReader.ReadToEnd();

                httpWebResponse.Close();
                streamReader.Close();
            }
            catch (Exception ex)
            {
                strResult = "{\"Status\":\"" + 99 + "\",\"message\":\"" + ex.Message + "\"}";
            }
            return strResult;
        }

        /// <summary>
        /// apk重命名
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAPKRename_Click(object sender, EventArgs e)
        {
            string apkname = this.txtAPKName.Text;
            string sign = this.txtSign.Text;

            string getUrl = string.Format(@"http://xxx.xxx.xxx.xxx/apkSign?apkname={0}&sign={1}", apkname, sign == "" ? "typesdk" : sign);
            string jsonData= HttpGet(getUrl);
            JsonData msg = JsonConvert.DeserializeObject<JsonData>(jsonData);
            if (msg.Status == "1000")
            {
                string logPath="apksign/log/apksign.log";
                string sl = "<script language='javascript' type='text/javascript'>openWindowSelf2('" + logPath + "');</script>";
                Page.ClientScript.RegisterStartupScript(ClientScript.GetType(), "mya", sl);
            }
            else
            {
                string sl = "<script language='javascript' type='text/javascript'>alert('" + msg.message + "');</script>";
                Page.ClientScript.RegisterStartupScript(ClientScript.GetType(), "mya", sl);
            }
        }
    }

    public class JsonData
    {
        public string Status { get; set; }
        public string message { get; set; }
    }
}