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
    public partial class SelectPlace : System.Web.UI.Page
    {
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gameId = GameRequest.GetQueryString("gameid");
        protected string gameName = GameRequest.GetQueryString("gameName");
        protected string gameDisplayName = GameRequest.GetQueryString("gameDisplayName");
        protected string gamenamespell = GameRequest.GetQueryString("gamenamespell");
        protected string taskid = GameRequest.GetQueryString("taskid");
        protected string gameversion = GameRequest.GetQueryString("gameversion");
        protected string placeidlist = GameRequest.GetQueryString("placeidlist");
        protected string gamelable = GameRequest.GetQueryString("gamelable");
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected bool isBack = false;
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(placeidlist))
            {
                placeidlist = placeidlist.Replace("'", "");
                isBack = true;
            }
            if (Cache["Roleid"] == null || Cache["Roleid"].ToString() == "")
            {
                BindingCache();
            }
            //Verification();
            this.GamePlaceList.DataBind();
        }

        private void BindingCache()
        {
            string sql = string.Format(@"  select * from [AspNetUserRoles] r inner join AspNetUsers u on r.UserId=u.Id and u.UserName='{0}' and RoleId in (2,3)", Context.User.Identity.Name);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            if (ds.Tables[0].Rows.Count > 0)
                Cache["Roleid"] = "0";
            else
                Cache["Roleid"] = "1";
        }

        DataTable dtNew = new DataTable();
        protected DataTable Verification()
        {
            DataView dvGamePlaceList = (DataView)SqlDataSourceGamePlaceList.Select(DataSourceSelectArguments.Empty);
            //string strPlatformIdList = "";
            DataTable dt = dvGamePlaceList.Table;
            dtNew = dt.Copy();//拷贝信息表 用作 listviwe 数据源
            if (dt.Rows.Count > 0)
            {
                for (int i = 0; i < dt.Rows.Count; i++)
                {
                    string platformName = dt.Rows[i]["PlatformName"].ToString();
                    string platformVersion = dt.Rows[i]["Version"].ToString();
                    string iconName = dt.Rows[i]["iconFlag"].ToString();
                    byte nullity = (byte)dt.Rows[i]["Nullity"];
                    string pluginid = dt.Rows[i]["PlugInID"].ToString();
                    string pluginversion = dt.Rows[i]["PlugInVersion"].ToString();
                    ExisitFile(platformName, platformVersion, iconName, nullity, pluginid, pluginversion, i);
                }
            }
            return dtNew;
        }

        /// <summary>
        /// 校验打包所需文件是否完全
        /// </summary>
        /// <returns></returns>
        private void ExisitFile(string platformName, string platformVersion, string iconName, byte nullity, string pluginid, string pluginversion, int row)
        {
            if (nullity == 1)
            {
                if (Cache["Roleid"].ToString() == "1")
                {
                    dtNew.Rows[row]["error"] = "渠道维护中";
                    return;
                }
            }
            if (platform == "Android")
            {
                string iconPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
                iconPath += gameId + "\\" + iconName + "\\";
                if (!System.IO.Directory.Exists(iconPath))//判断图标文件是否存在
                {
                    dtNew.Rows[row]["error"] = "图标文件不存在";
                    return;
                }
                string configPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
                configPath += gameId + "\\" + (pluginid == "0" ? platformName : platformName + "_LeBian") + "\\";
                if (!System.IO.Directory.Exists(configPath)) //判断config文件是否存在
                {
                    dtNew.Rows[row]["error"] = "config文件不存在";
                    return;
                }
                if (!System.IO.File.Exists(configPath + "local.properties")) //判断config文件是否存在
                {
                    dtNew.Rows[row]["error"] = "local.properties文件不存在";
                    return;
                }
                //if (!System.IO.File.Exists(configPath + "replace_key.xml")) //判断config文件是否存在
                //{
                //    dtNew.Rows[row]["error"] = "replace_key文件不存在";
                //    return;
                //}
                if (pluginid == "1")
                {
                    if (!ExistsPlugInFile(pluginid, pluginversion))
                    {
                        dtNew.Rows[row]["error"] = "插件版本文件不存在";
                        return;
                    }
                }

                string platformVersionPath = System.Configuration.ConfigurationManager.AppSettings["SDKPlatformVersion"];
                platformVersionPath += platformName + "\\" + platformVersion + "\\";
                if (!System.IO.Directory.Exists(platformVersionPath) || string.IsNullOrEmpty(platformVersion)) //判断渠道版本(*V1.0*)文件是否存在
                {
                    dtNew.Rows[row]["error"] = "渠道版本文件不存在";
                    return;
                }
            }
            else
            {
                string iconPath = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"];
                iconPath += gameId + "\\" + iconName + "\\";
                if (!System.IO.Directory.Exists(iconPath))//判断图标文件是否存在
                {
                    dtNew.Rows[row]["error"] = "图标文件不存在";
                    return;
                }
            }
        }


        private bool ExistsPlugInFile(string pluginid, string pluginversion)
        {
            string platformVersionPath = System.Configuration.ConfigurationManager.AppSettings["SDKPlatformVersion"];

            platformVersionPath += "LeBian\\" + pluginversion + "\\";
            switch (pluginid)
            {
                case "1":
                    if (!System.IO.Directory.Exists(platformVersionPath)) //判断插件版本(*V1.0*)文件是否存在
                    {
                        return false;
                    }
                    return true;
                case "2":
                    break;
            }
            return false;
        }


    }
}