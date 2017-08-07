using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.Facility
{
    public partial class UpdateSVNLog : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string logPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"] + "updateSDK.log";
            string content = "";
            using (StreamReader sr = new StreamReader(logPath, System.Text.Encoding.UTF8))
            {
                content = sr.ReadToEnd().Replace("\n", "<br>");
                divtxt.InnerHtml = content;
            }
        }
    }
}