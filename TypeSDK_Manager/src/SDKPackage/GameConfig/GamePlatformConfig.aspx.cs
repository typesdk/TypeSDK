using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.GameConfig
{
    public partial class GamePlatformConfig : System.Web.UI.Page
    {
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string androidversionid = GameRequest.GetQueryString("androidversionid");
        protected string iosversionid = GameRequest.GetQueryString("iosversionid");
        protected string gamedisplayname = GameRequest.GetQueryString("gamedisplayname");
        protected string platformid = GameRequest.GetQueryString("platformid");
        protected string platformname = GameRequest.GetQueryString("platformname");
        protected string pluginid = GameRequest.GetQueryString("pluginid");
        protected string systemname = GameRequest.GetQueryString("systemid");
        protected void Page_Load(object sender, EventArgs e)
        {
            if (IsPostBack)
            {
            }
        }

        /// <summary>
        /// 配置商品列表
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonConfigProduct_Click(object sender, EventArgs e)
        {
            try
            {
                //CreateCPSettings();
                //CreateLocalConfig();
                //ConfigZip();
                Label2.Text = "配置文件生成完毕";
            }
            catch (Exception ex)
            {
                Label2.Text = ex.Message.ToString();
            }
        }

        /// <summary>
        /// 同步服务端
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonSyncServer_Click(object sender, EventArgs e)
        {
            try
            {
                //CreateCPSettings();
                //CreateLocalConfig();
                //ConfigZip();
                Label2.Text = "配置文件生成完毕";
            }
            catch (Exception ex)
            {
                Label2.Text = ex.Message.ToString();
            }
        }

        /// <summary>
        /// 保存配置文件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonCreateConfigFile_Click(object sender, EventArgs e)
        {
            try
            {
                CreateCPSettings();
                CreateLocalConfig();
                //ConfigZip();
                Label2.Text = "配置文件生成完毕";
            }
            catch (Exception ex)
            {
                Label2.Text = ex.Message.ToString();
            }
        }


        private void CreateCPSettings()
        {
            DataView dvCpSetting = (DataView)SqlDataSourceCpSetting.Select(DataSourceSelectArguments.Empty);
            String jsonCpSetting = ToJson(dvCpSetting);
            //TextBox1.Text = jsonCpSetting;
            string system = GameRequest.GetQueryString("systemid");
            String SDKPackageDir = string.Empty;
            string filePatch = string.Empty;
            if (system == "1")
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
                filePatch = SDKPackageDir + gameid + "\\" + GameRequest.GetQueryString("platformname");
            }
            else
            {
                //IOS不确定
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageConfig"]; //System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
                filePatch = SDKPackageDir + gameid + "\\sdk_res_" + GameRequest.GetQueryString("platformname") + "\\cpsettings";
            }
            //string system = GameRequest.GetQueryString("systemid") == "1" ? "Android\\" : "IOS\\";

            if (pluginid == "1")
            {
                filePatch += "_LeBian";
            }
            if (!System.IO.Directory.Exists(filePatch))
            {
                System.IO.Directory.CreateDirectory(filePatch);
            }
            String fileCpSetting = filePatch + "\\CPSettings.txt";
            StreamWriter sw = new StreamWriter(fileCpSetting, false, Encoding.UTF8);
            sw.Write(jsonCpSetting);
            sw.Close();
        }

        private void CreateLocalConfig()
        {
            DataView dvLocalConfig = (DataView)SqlDataSourceLocal.Select(DataSourceSelectArguments.Empty);
            String localConfig = ToConfig(dvLocalConfig);
            String SDKPackageDir = string.Empty;
            string filePatch = string.Empty;
            string system = GameRequest.GetQueryString("systemid");
            if (system == "1")
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
                filePatch = SDKPackageDir + gameid + "\\" + GameRequest.GetQueryString("platformname");
            }
            else
            {
                //IOS不确定
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageConfig"];//System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
                filePatch = SDKPackageDir + gameid + "\\sdk_res_" + GameRequest.GetQueryString("platformname");
            }
            //string system = GameRequest.GetQueryString("systemid") == "1" ? "Android\\" : "IOS\\";

            if (pluginid == "1")
            {
                filePatch += "_LeBian";
            }
            if (!System.IO.Directory.Exists(filePatch))
            {
                System.IO.Directory.CreateDirectory(filePatch);
            }
            String fileCpSetting = filePatch + "\\local.properties";
            UTF8Encoding encoding = new UTF8Encoding(false);
            StreamWriter sw = new StreamWriter(fileCpSetting, false, encoding);
            sw.Write(localConfig);
            sw.Close();
        }

        public static string ToJson(DataView dv)
        {
            DataTable dt = dv.Table;
            DataRowCollection drc = dt.Rows;
            StringBuilder jsonString = new StringBuilder();
            jsonString.Append("{\r\n");

            for (int i = 0; i < drc.Count; i++)
            {
                string strKey = drc[i][0].ToString().Replace(" ", "");
                string strValue = drc[i][1].ToString().Replace(" ", "");
                jsonString.Append("\"" + strKey + "\":\"" + strValue + "\",\r\n");
            }
            jsonString.Remove(jsonString.Length - 3, 1);
            jsonString.Append("}");
            return jsonString.ToString();
        }

        public static string ToConfig(DataView dv)
        {
            DataTable dt = dv.Table;
            DataRowCollection drc = dt.Rows;
            StringBuilder configString = new StringBuilder();

            for (int i = 0; i < drc.Count; i++)
            {
                string strKey = drc[i][0].ToString().Replace(" ", "");
                string strValue = drc[i][1].ToString().Replace(" ", "");
                configString.Append(strKey + "=" + strValue + "\r\n");
            }
            return configString.ToString();
        }

        private void ConfigZip()
        {
            String SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKPackageDir"];
            string system = GameRequest.GetQueryString("systemid") == "1" ? "Android\\" : "IOS\\";
            string filePatch = SDKPackageDir + system + "Config\\" + gameid + "\\" + GameRequest.GetQueryString("platformname") + "\\";
            string saveFilePath = SDKPackageDir + system + "Config\\" + gameid + "\\Config.zip";
            if (File.Exists(filePatch + "Config.zip"))
            {
                File.Delete(filePatch + "Config.zip");
            }
            if (ZipHelper.ToFile(filePatch, saveFilePath, ZipHelper.PackingScope.All))
            {
                File.Move(saveFilePath, filePatch + "Config.zip");
            }
        }
    }
}