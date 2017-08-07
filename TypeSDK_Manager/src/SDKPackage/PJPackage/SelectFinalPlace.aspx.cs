using Newtonsoft.Json;
using SDKPackage.Facade;
using SDKPackage.Kernel;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class SelectFinalPlace : System.Web.UI.Page
    {
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gameId = GameRequest.GetQueryString("gameid");
        protected string gameName = GameRequest.GetQueryString("gameName");
        protected string gameDisplayName = GameRequest.GetQueryString("gameDisplayName");
        protected string gamenamespell = GameRequest.GetQueryString("gamenamespell");
        protected int taskid = GameRequest.GetQueryInt("taskid", 0);
        protected string gameversion = GameRequest.GetQueryString("gameversion");
        protected string gamelable = GameRequest.GetQueryString("gamelable");
        protected string placeidlist = GameRequest.GetQueryString("placeidlist");
        protected string platformversionlist = GameRequest.GetQueryString("platformversionlist");
        protected string pluginidlist = GameRequest.GetQueryString("pluginidlist");
        //string iconPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
        //string configPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
        //string gamefilePath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageGameFile"];
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                if (platform == "Android")
                {
                    BindingCB();
                    if (Cache["Roleid"] == null || Cache["Roleid"].ToString() == "")
                    {
                        BindingCache();
                    }

                    ShowCompileMode(Cache["Roleid"].ToString() == "0" ? true : false);
                    BindingCkBox();
                }
                else
                {
                    this.CheckBoxIsEncryption.Visible = false;
                }
            }

        }

        private void BindingCache()
        {
            string sql = string.Format(@"  select * from [AspNetUserRoles] r inner join AspNetUsers u on r.UserId=u.Id and u.UserName='{0}' and RoleId in (2,3)", Context.User.Identity.Name);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            if (ds.Tables[0].Rows.Count > 0)
            {
                Cache["Roleid"] = "0";
            }
            else
                Cache["Roleid"] = "1";
        }


        private void ShowCompileMode(bool ispermission)
        {
            this.CheckBoxCompileMode.Visible = ispermission;
            this.CheckBoxCompileMode.Text = "Debug模式";
        }


        private void BindingCB()
        {
            string sqlQuery = string.Format("select IsEncryption from sdk_gameInfo where gameid={0}", gameId);
            bool isEncryption = aideNativeWebFacade.GetObjectBySql(sqlQuery).ToString() == "1" ? true : false;
            this.CheckBoxIsEncryption.Text = " Unity加密";
            if (isEncryption)
            {
                this.CheckBoxIsEncryption.Visible = true;
                this.CheckBoxIsEncryption.Checked = true;
            }
            else
            {
                this.CheckBoxIsEncryption.Visible = false;
                this.CheckBoxIsEncryption.Checked = false;
            }
        }


        /// <summary>
        /// 开始打包
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void btnStart_Click(object sender, EventArgs e)
        {
            //if (!Verification()) { LabelLog.Text = "打包渠道SDK所需文件不完整"; return; }
            string createtaskid = TextUtility.GetDateTimeLongString();
            //string[] arr = placeidlist.Split(',');
            string hx_ad_list = string.Empty;
            string lbhx_ad_list = string.Empty;
            string adplatformlist = string.Empty;
            string adidlist = string.Empty;

            foreach (Control item in GamePlaceList.Items)
            {
                HiddenField hf = (HiddenField)item.FindControl("HiddenFieldPlatformID");
                HiddenField hf_val = (HiddenField)item.FindControl("HiddenFieldAdID");
                string val = hf.Value;
                if (val == "1071_0")
                {
                    hx_ad_list = hf_val.Value;
                    adplatformlist += val + ",";
                }
                else if (val == "1071_1")
                {
                    lbhx_ad_list = hf_val.Value;
                    adplatformlist += val + ",";
                }
            }

            string ad_platform = string.Empty;
            if (!string.IsNullOrEmpty(hx_ad_list))
            {
                ad_platform += hx_ad_list + "_";
            }
            if (!string.IsNullOrEmpty(lbhx_ad_list))
            {
                ad_platform += lbhx_ad_list + "_";
            }
            if (ad_platform.Length > 0)
            {
                ad_platform = ad_platform.Substring(0, ad_platform.Length - 1);
            }
            if (adplatformlist.Length > 0)
            {
                adplatformlist = adplatformlist.Substring(0, adplatformlist.Length - 1);
            }
            SetPackageTask(taskid.ToString(), Context.User.Identity.Name, createtaskid, platform, gameId, gameversion, gamelable, platformversionlist, this.CheckBoxIsEncryption.Checked ? "1" : "0", adplatformlist, ad_platform);

            //string isencryption=this.CheckBoxIsEncryption.Checked ? "1" : "0";

            //Message umsg = aideNativeWebFacade.AddNewPackageTask(taskid, Context.User.Identity.Name, placeidlist, createtaskid, platform, Convert.ToInt32(gameId), gameversion, gamelable, platformversionlist, isencryption);
            //if (umsg.Success)
            //{
            //    Response.Redirect("SelectPackageInfo.aspx?gameid=" + gameId + "&gameName=" + gameName + "&gameDisplayName=" + gameDisplayName + "&platform=" + platform + "&createtaskid=" + createtaskid + "&gameversion=" + gameversion + "&isencryption=" + isencryption);
            //}
            //else
            //{
            //    this.LabelLog.Text = "打包任务发布失败！";
            //}
        }

        protected bool Verification()
        {
            string sql = string.Format(@"select dpf.PlatformName,pv.[Version],gpi.iconName from 
                                        [sdk_GamePlatFromInfo] gpfi inner join sdk_PlatformVersion pv on gpfi.[VersionID]=pv.ID and gpfi.GameID={0}
                                        inner join sdk_DefaultPlatform dpf on pv.PlatformID=dpf.Id and dpf.Id in ({1})
                                        inner join sdk_GamePlatformIcon gpi on gpfi.GameID=gpi.GameName and dpf.id=gpi.PlatformName ", gameId, placeidlist);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);//获取要打包任务 渠道简称 版本
            var len = placeidlist.Split(',').Length;
            if (ds.Tables[0].Rows.Count == len && len > 0)
            {
                for (int i = 0; i < len; i++)
                {
                    string platformName = ds.Tables[0].Rows[i]["PlatformName"].ToString();
                    string platformVersion = ds.Tables[0].Rows[i]["Version"].ToString();
                    string iconName = ds.Tables[0].Rows[i]["iconName"].ToString();
                    if (!ExisitFile(platformName, platformVersion, iconName)) return false;
                }
                return true;
            }

            return false;
        }

        /// <summary>
        /// 校验打包所需文件是否完全
        /// </summary>
        /// <returns></returns>
        private bool ExisitFile(string platformName, string platformVersion, string iconName)
        {
            string iconPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
            iconPath += gameId + "\\" + iconName + "\\";
            if (!System.IO.Directory.Exists(iconPath)) return false;//判断图标文件是否存在
            string configPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
            configPath += gameId + "\\" + platformName + "\\";
            if (!System.IO.Directory.Exists(configPath)) return false;//判断config文件是否存在
            string platformVersionPath = System.Configuration.ConfigurationManager.AppSettings["SDKPlatformVersion"];
            platformVersionPath += platformName + "\\" + platformVersion + "\\";
            if (!System.IO.Directory.Exists(platformVersionPath)) return false;//判断渠道版本(*V1.0*)文件是否存在
            return true;
        }

        private void BindingCkBox()
        {
            //string[] platformlist = placeidlist.Replace("','", ",").Split(',');
            //foreach (var id in platformlist)
            //{
            //    if (id == "1071_0" || id == "1071_1")
            //    {
            //        this.CheckBoxListChannel.DataSource = GetCkBoxData();
            //        this.CheckBoxListChannel.DataTextField = "name";
            //        this.CheckBoxListChannel.DataValueField = "id";
            //        this.CheckBoxListChannel.DataBind();
            //        break;
            //    }
            //}
        }



        static DataTable dtAdList = null;
        private DataTable GetCkBoxData()
        {
            //dtAdList.Clear();
            if (dtAdList != null) dtAdList.Clear();//return dtAdList;
            string sqlQuery = string.Format("select SDKGameID from sdk_gameInfo where gameid={0}", gameId);
            string sdkGameID = aideNativeWebFacade.GetObjectBySql(sqlQuery).ToString();

            string getAdUrl = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidAdListUrl"] + sdkGameID;
            string jsonData = HttpGet(getAdUrl);

            JsonAd adlist = JsonConvert.DeserializeObject<JsonAd>(jsonData);
            DataTable dt = new DataTable();
            dt.Columns.Add("id", typeof(string));
            dt.Columns.Add("name", typeof(string));
            if (adlist.Code == "0")
            {
                for (int i = 0; i < adlist.Data.Count; i++)
                {
                    DataRow dr = dt.NewRow();
                    dr["id"] = adlist.Data[i].AdID;
                    dr["name"] = adlist.Data[i].AdName;
                    dt.Rows.Add(dr);
                }
            }
            dtAdList = dt;
            return dt;
        }

        private string GetAdName(string adid)
        {
            var p = dtAdList.AsEnumerable().Where(r => r["id"].ToString() == adid).Select(d => d);//dtAdList.Select("id="+adid);
            if (p.Count() > 0)
            {
                foreach (var row in p)
                {
                    return row["name"].ToString();
                }
            }
            return "";
        }


        private string GetSDKGameID()
        {
            string sqlQuery = string.Format("select sdkgameid from sdk_gameInfo where gameid={0}", gameId);
            string sdkgameid = aideNativeWebFacade.GetScalarBySql(sqlQuery);
            return sdkgameid;
        }

        /// <summary>
        /// 插入任务
        /// </summary>
        /// <param name="taskid"></param>
        /// <param name="UserName"></param>
        /// <param name="placeidlist"></param>
        /// <param name="createtaskid"></param>
        /// <param name="platform"></param>
        /// <param name="gameId"></param>
        /// <param name="gameversion"></param>
        /// <param name="gamelable"></param>
        /// <param name="platformversionlist"></param>
        /// <param name="isencryption"></param>
        /// <param name="adplatformlist"></param>
        /// <param name="adidlist"></param>
        private void SetPackageTask(string taskid, string UserName, string createtaskid, string platform, string gameId, string gameversion, string gamelable, string platformversionlist, string isencryption, string adplatformlist, string adidlist)
        {
            string[] strplaceidlist = placeidlist.Split(',');//渠道ID组
            string[] strplatformversionlist = platformversionlist.Split(',');//渠道版本组
            string[] stradplatformlist = adplatformlist.Split(',');//广告商组
            string[] stradidlist = adidlist.Split('_');//广告商ID组
            string[] strpluginidlist = pluginidlist.Split(',');//插件ID组
            string sql = string.Format(@"select top 1 gpfi.pluginversion from sdk_DefaultPlatform dpf inner join sdk_GamePlatFromInfo gpfi
                                                 on gpfi.VersionPlatFromID=dpf.id and gpfi.GameID={0} and gpfi.SystemID=1 and gpfi.PlugInID=1", gameId);
            string pluginversion = aideNativeWebFacade.GetScalarBySql(sql);
            string compileMode = (this.CheckBoxCompileMode.Checked && Cache["Roleid"].ToString()=="0") ? "debug" : "release";
            bool flag = false;
            for (int i = 0; i < strplaceidlist.Length; i++)
            {
                if (platform == "Android")
                {
                    string placeid = strplaceidlist[i];
                    if (strplaceidlist.Length == 1)
                    {
                        placeid = strplaceidlist[i].Substring(0, strplaceidlist[i].Length - 2);
                    }
                    else if (i == 0)
                    {
                        placeid = strplaceidlist[i].Substring(0, strplaceidlist[i].Length - 3);
                    }
                    else if (i < strplaceidlist.Length - 1)
                    {
                        placeid = strplaceidlist[i].Substring(1, strplaceidlist[i].Length - 4);
                    }
                    else
                    {
                        placeid = strplaceidlist[i].Substring(1, strplaceidlist[i].Length - 3);
                    }

                    if (!string.IsNullOrEmpty(adplatformlist) && !string.IsNullOrEmpty(adidlist))
                    {
                        for (int j = 0; j < stradplatformlist.Length; j++)//判断是否是需要加广告商的任务
                        {
                            try
                            {
                                if (stradplatformlist[j] == strplaceidlist[i].Replace("'", ""))
                                {
                                    flag = true;
                                    string[] arradid = stradidlist[j].Split(',');
                                    for (int k = 0; k < arradid.Length; k++)//循环添加不同广告商id任务
                                    {
                                        //逐条添加带有adid的任务
                                        string sqlQueryAd = string.Format(@"insert into sdk_NewPackageCreateTask (PackageTaskID,CreateUser,PlatFormID,CreateTaskID,GameID,GameFileVersion,GameVersionLable,PlatformVersion,IsEncryption,AdID,AdName,PlugInID,PlugInVersion,CompileMode) values ('{0}','{1}','{2}','{3}','{4}','{5}','{6}','{7}','{8}','{9}','{10}',{11},'{12}','{13}')",
                                                              taskid, UserName, placeid, createtaskid, gameId, gameversion, gamelable, strplatformversionlist[i], isencryption, arradid[k], GetAdName(arradid[k]), string.IsNullOrEmpty(pluginversion) ? "0" : "1", pluginversion, compileMode);
                                        aideNativeWebFacade.ExecuteSql(sqlQueryAd);
                                        //PrintTxt.RecordLog("D://sqlData/", sqlQueryAd);
                                    }
                                    break;
                                }
                            }
                            catch (Exception)
                            {
                                break;
                            }

                        }
                    }
                    if (!flag)//非广告商任务
                    {
                        string sqlQuery = string.Format(@"insert into sdk_NewPackageCreateTask (PackageTaskID,CreateUser,PlatFormID,CreateTaskID,GameID,GameFileVersion,GameVersionLable,PlatformVersion,IsEncryption,PlugInID,PlugInVersion,CompileMode) values ('{0}','{1}','{2}','{3}','{4}','{5}','{6}','{7}','{8}',{9},'{10}','{11}')",
                            taskid, UserName, placeid, createtaskid, gameId, gameversion, gamelable, strplatformversionlist[i], isencryption, strpluginidlist[i] == "0" ? "0" : "1", strpluginidlist[i] == "0" ? "" : pluginversion, compileMode);
                        aideNativeWebFacade.ExecuteSql(sqlQuery);
                        //PrintTxt.RecordLog("D://sqlData/", sqlQuery);
                    }
                }
                else
                {
                    string sqlQuery = string.Format(@"insert into sdk_NewPackageCreateTask_IOS (PackageTaskID,CreateUser,PlatFormID,CreateTaskID,GameID,GameFileVersion,GameVersionLable,PlatformVersion) values ('{0}','{1}','{2}','{3}','{4}','{5}','{6}','{7}')",
                                                      taskid, UserName, strplaceidlist[i], createtaskid, gameId, gameversion, gamelable, strplatformversionlist[i]);
                    aideNativeWebFacade.ExecuteSql(sqlQuery);
                }
            }

            //跳转
            Response.Redirect("SelectPackageInfo.aspx?gameid=" + gameId + "&gameName=" + gameName + "&gameDisplayName=" + gameDisplayName + "&platform=" + platform + "&createtaskid=" + createtaskid + "&gameversion=" + gameversion + "&isencryption=" + isencryption);
        }

        private string HttpGet(string getUrl)
        {
            string strResult = "";
            try
            {
                HttpWebRequest httpWebRequest = (HttpWebRequest)WebRequest.Create(getUrl);

                httpWebRequest.ContentType = "application/json";
                httpWebRequest.Method = "GET";
                httpWebRequest.Timeout = 20000;

                //byte[] btBodys = Encoding.UTF8.GetBytes(body);
                //httpWebRequest.ContentLength = btBodys.Length;
                //httpWebRequest.GetRequestStream().Write(btBodys, 0, btBodys.Length);

                HttpWebResponse httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();
                StreamReader streamReader = new StreamReader(httpWebResponse.GetResponseStream());
                strResult = streamReader.ReadToEnd();

                httpWebResponse.Close();
                streamReader.Close();
            }
            catch (Exception)
            {
                strResult = "{\"code\":\"" + 1 + "\",\"data\":\"\"}";
            }
            return strResult;
        }
    }

    public class JsonAd
    {
        public JsonAd() { }

        private string m_code;
        private List<AdInfo> m_data;

        public string Code { get; set; }
        public List<AdInfo> Data { get; set; }
    }


    public class AdInfo
    {
        public AdInfo()
        {

        }

        private string m_adid;
        private string m_adname;
        private string m_gameid;
        private string m_gamename;

        public string AdID { get; set; }
        public string AdName { get; set; }
        public string GameID { get; set; }
        public string GameName { get; set; }
    }
}