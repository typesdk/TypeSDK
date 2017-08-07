using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.Facility
{
    public partial class Log : System.Web.UI.Page
    {
        string logPath = GameRequest.GetQueryString("logpath");
        protected void Page_Load(object sender, EventArgs e)
        {
            string content = "";
            if (string.IsNullOrEmpty(logPath))
            {
                 content = "路径尚未提供！";
            }
            else
            {
                logPath = System.Configuration.ConfigurationManager.AppSettings["SDKPackageDir"] + logPath;            
            }
            using (StreamReader sr = new StreamReader(logPath, System.Text.Encoding.UTF8))
            {
                content = sr.ReadToEnd().Replace("\n", "<br>");
                divtxt.InnerHtml = content;
            }
        }
    }
}