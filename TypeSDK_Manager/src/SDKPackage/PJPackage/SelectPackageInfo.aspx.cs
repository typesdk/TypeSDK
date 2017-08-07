using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class SelectPackageInfo : System.Web.UI.Page
    {
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gameName = GameRequest.GetQueryString("gameName");
        protected string gameDisplayName = GameRequest.GetQueryString("gameDisplayName");
        protected string taskid = GameRequest.GetQueryString("taskid");
        protected string gameversion = GameRequest.GetQueryString("gameversion");
        protected string placeidlist = GameRequest.GetQueryString("placeidlist");
        protected string createtaskid = GameRequest.GetQueryString("createtaskid");
        protected string isencryption = GameRequest.GetQueryString("isencryption");
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            BindingList();
        }


        public void BindingList()
        {
            string sqlQuery = "";
            if (platform == "Android")
                sqlQuery = string.Format(@"select npct.PackageTaskStatus,dpf.PlatformName,dpf.PlatformDisplayName,npct.PackageName,npct.RecID,npct.adid,npct.adname,npct.PlugInID from 
                                              sdk_NewPackageCreateTask npct
                                              inner join sdk_DefaultPlatform dpf on npct.PlatFormID=dpf.Id and npct.CreateTaskID='{0}'", createtaskid);
            else
                sqlQuery = string.Format(@"select npct.PackageTaskStatus,dpf.PlatformName,dpf.PlatformDisplayName,npct.PackageName,npct.RecID,adname='',PlugInID=0 from 
                                              sdk_NewPackageCreateTask_IOS npct
                                              inner join sdk_DefaultPlatform dpf on npct.PlatFormID=dpf.Id and npct.CreateTaskID='{0}'", createtaskid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sqlQuery);
            this.GamePlaceList.DataSource = ds;
            this.GamePlaceList.DataBind();
            var p = ds.Tables[0].AsEnumerable().Where(r => r["PackageTaskStatus"].ToString() == "0" || r["PackageTaskStatus"].ToString() == "1" || r["PackageTaskStatus"].ToString() == "2").Select(d => d);

            //if (p != null && p.Count() > 0)
            //{
            //    this.Timer1.Enabled = true;
            //}
            //else
            //{
            //    this.Timer1.Enabled = false;
            //}
            //this.GamePlaceList.DataSource = ClientIDSeparator;
        }

        protected void Timer1_Tick(object sender, EventArgs e)
        {
            BindingList();
        }

    }
}