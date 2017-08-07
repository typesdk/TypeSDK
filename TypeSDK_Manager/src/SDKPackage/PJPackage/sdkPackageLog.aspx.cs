using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class sdkPackageLog : System.Web.UI.Page
    {
        protected int taskid2 = GameRequest.GetQueryInt("taskid", 0);
        protected string createtaskid2 = GameRequest.GetQueryString("createtaskid");
        protected string systemname = GameRequest.GetQueryString("systemname");
        protected void Page_Load(object sender, EventArgs e)
        {
            
            string logPath = "";
            if (systemname == "Android")
                logPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"] + createtaskid2 + "/" + taskid2 + ".log ";
            else
                logPath = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageLogs"] + createtaskid2 + "/" + taskid2 + ".log ";
            string content = "";
            using (StreamReader sr = new StreamReader(logPath, System.Text.Encoding.UTF8))
            {
                content = sr.ReadToEnd().Replace("\n", "<br>");
                divtxt.InnerHtml = content;
            }
        }
    }
}