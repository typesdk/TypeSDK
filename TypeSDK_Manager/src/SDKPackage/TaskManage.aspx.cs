using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using SDKPackage.Kernel;
using SDKPackage.Entity.NativeWeb;
using System.Net.Mail;
using System.IO;
using System.Xml;
using ICSharpCode.SharpZipLib.Zip;

namespace SDKPackage
{
    public partial class TaskManage : System.Web.UI.Page
    {
        string action = string.Empty;

        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                //Test(1,"Lenovo");

                //XmlDocument AndroidManifest1 = new XmlDocument();
                //String AndroidManifestFile1 = "D://test/1.xml";//渠道（主）

                //AndroidManifest1.Load(AndroidManifestFile1);

                //XmlDocument xml = GetPrimaryAndroidManifest(AndroidManifest1);
                //string aa;
                GetAction(GameRequest.GetQueryString("Action"));
            }
        }

        /// <summary>
        /// 根据控制器执行方法
        /// </summary>
        /// <param name="actionName"></param>
        protected void GetAction(string actionName)
        {
            switch (actionName)
            {
                case "gainTask":
                    GetTask();
                    break;
                case "startTask":
                    StartUpTask();
                    break;
                case "finishTask":
                    FinishTask();
                    break;
                case "loseTask":
                    LoseTask();
                    break;
                default:
                    WriteError();
                    break;
            }
        }

        /// <summary>
        /// 获取一条任务
        /// </summary>
        private void GetTask()
        {
            string msg = "";
            string platform = GameRequest.GetQueryString("platform");
            if (string.IsNullOrEmpty(platform))
            {
                msg = "{\"status\":\"error\",\"msg\":\"error03\"}";
            }
            else if (platform != "Android" && platform != "iOS") { msg = "{\"status\":\"error\",\"msg\":\"error03\"}"; }
            else
            {
                if (platform == "Android")
                {
                    Message umsg = aideNativeWebFacade.GetGainTask(platform);
                    if (umsg.Success)
                    {
                        GainTask gt = umsg.EntityList[0] as GainTask;
                        if (gt.RecID == 0)
                        {
                            msg = "{\"status\":\"success\",\"msg\":\"no data now\",\"data\":null}";
                        }
                        else
                        {
                            string recid = gt.RecID.ToString();

                            try
                            {
                                CheckManifest(gt);
                            }
                            catch (Exception ex)
                            {
                                msg = "{\"status\":\"error99\",\"msg\":\"" + Server.UrlEncode(ex.Message) + "\",\"data\":null}"; Response.Write(msg);
                                PrintLog(recid, platform, msg);
                                PrintServerLog(recid, platform, "Manifest:" + msg);
                                string sql = string.Format(@"update {1} set PackageTaskStatus=4,FinishDatetime=getdate() where recid={0} and (PackageTaskStatus=2 or PackageTaskStatus=0)", recid, platform == "Android" ? "[sdk_NewPackageCreateTask]" : "[sdk_NewPackageCreateTask_IOS]");
                                aideNativeWebFacade.ExecuteSql(sql);
                                return;
                            }


                            //string urlHead = "http://" + HttpContext.Current.Request.Url.Host + ":" + HttpContext.Current.Request.Url.Port + "/";

                            string gameid = gt.GameID;
                            string gamename = gt.GameName;
                            string platFormName = gt.PlatFormName;
                            string gameVersion = gt.GameVersion;
                            string strCollectDatetime = gt.StrCollectDatetime;
                            string strIconPath = gt.IconPath;
                            string strCreateTaskID = gt.CreateTaskID;
                            string strmyversion = gt.MyVersion;
                            string strChannelVersion = gt.ChannelVersion;
                            string strIsEncryption = gt.IsEncryption;
                            string strAdID = gt.AdID;
                            int strPlugInID = gt.PlugInID;
                            string strPlugInVersion = gt.PlugInVersion;
                            string strCompileMode = gt.CompileMode;
                            string strKeyname = gt.KeyName;

                            msg = "{\"status\":\"success\",\"msg\":\"gainTask_OK\",\"data\":[{\"TaskID\":\"" + recid + "\",\"Channel\":\"" + platFormName + "\",\"GameID\":\"" + gameid + "\",\"ChannelVersion\":\"" + strChannelVersion + "\",\"GameVersion\":\"" + gameVersion + "_" + strCollectDatetime + "\",\"IconID\":\"" + strIconPath + "\",\"BatchNo\":\"" + strCreateTaskID + "\",\"SdkVer\":\"" + strmyversion + "\",\"IsEncrypt\":\"" + strIsEncryption + "\",\"AdID\":\"" + strAdID + "\",\"PluginID\":\"" + strPlugInID + "\",\"PluginVersion\":\"" + strPlugInVersion + "\",\"CompileMode\":\"" + strCompileMode + "\",\"SignKey\":\"" + strKeyname + "\"}]}";

                            PrintLog(recid, platform, msg);



                        
                        }
                    }
                    else
                    {
                        msg = "{\"status\":\"error\",\"msg\":\"error02\"}";
                    }
                }
                else
                {
                    Message umsg = aideNativeWebFacade.GetGainTask(platform);
                    if (umsg.Success)
                    {
                        GainTask gt = umsg.EntityList[0] as GainTask;
                        if (gt.RecID == 0)
                        {
                            msg = "{\"status\":\"success\",\"msg\":\"no data now\",\"data\":null}";
                        }
                        else
                        {
                            string urlHead = "http://" + HttpContext.Current.Request.Url.Host + ":" + HttpContext.Current.Request.Url.Port + "/";
                            string recid = gt.RecID.ToString();
                            string gameid = gt.GameID;
                            string gamename = gt.GameName;
                            string platFormName = gt.PlatFormName;
                            string gameVersion = gt.GameVersion;
                            string strCollectDatetime = gt.StrCollectDatetime;
                            string strIconPath = gt.IconPath;
                            string strCreateTaskID = gt.CreateTaskID;
                            string strmyversion = gt.MyVersion;
                            string strChannelVersion = gt.ChannelVersion;
                            string strgamenamespell = gt.GameNameSpell;
                            string strunityver = gt.UnityVer;
                            string strproductname = gt.ProductName;

                            //msg = "{\"status\":\"success\",\"msg\":\"gainTask_OK\",\"data\":[{\"TaskID\":\"" + recid + "\",\"Channel\":\"" + platFormName.ToLower() + "\",\"GameID\":\"" + strgamenamespell + "\",\"GameVersion\":\"" + strCollectDatetime + "\",\"IconID\":\"" + strIconPath + "\",\"BatchNo\":\"" + strCreateTaskID + "\",\"SdkVer\":\"" + strmyversion + "\",\"GameFileName\":\"" + gameVersion + "\",\"UnityVer\":\"" + strunityver + "\",\"ProductName\":\"" + strproductname + "\"}]}";
                            msg = "{\"status\":\"success\",\"msg\":\"gainTask_OK\",\"data\":[{\"TaskID\":\"" + recid + "\",\"Channel\":\"" + platFormName + "\",\"GameID\":\"" + gameid + "\",\"ChannelVersion\":\"" + strChannelVersion + "\",\"GameVersion\":\"" + gameVersion + "\",\"CreateTime\":\"" + strCollectDatetime + "\",\"IconID\":\"" + strIconPath + "\",\"BatchNo\":\"" + strCreateTaskID + "\",\"SdkVer\":\"" + strmyversion + "\",\"GameFileName\":\"" + gamename + "\",\"UnityVer\":\"" + strunityver + "\",\"ProductName\":\"" + strproductname + "\"}]}";

                            PrintLog(recid, platform, msg);
                        }
                    }
                    else
                    {
                        msg = "{\"status\":\"error\",\"msg\":\"error02_2\"}";
                    }
                }

            }
            Response.Write(msg);
        }


        private void PrintLog(string recid,string plarform,string msg)
        {
            string ip = GameRequest.GetUserIP();
            string serpath = Server.MapPath("TaskLog");
            string content = string.Format("{2} 任务ID:{0} 目标IP:{1} {4}\r\n msg:{3}\r\n", recid, ip, DateTime.Now.ToString("yyyy-MM-dd hh:mm:ss"), msg, plarform);
            PrintTxt.RecordLog(serpath, content);
        }


        private void PrintServerLog(string recid,string createtaskid,string msg)
        {
            string SDKAndroidPackageLogs = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"];
            string path = SDKAndroidPackageLogs + createtaskid;
            if (!System.IO.Directory.Exists(path))
            {
                System.IO.Directory.CreateDirectory(path);
            }
            path += "/" + recid + ".log";
            using (StreamWriter sw = new StreamWriter(path, true))
            {
                sw.Write("");
                sw.WriteLine(Server.UrlDecode(msg));
            }
        }


        /// <summary>
        /// 任务启动 更改数据库状态
        /// </summary>
        private void StartUpTask()
        {
            string msg = "";
            int recid = GameRequest.GetInt("taskid", 0);
            string platform = GameRequest.GetQueryString("platform");
            string serveraddr = GameRequest.GetString("serveraddr");
            //string fileaddr = GameRequest.GetString("fileaddr");
            //string filename = GameRequest.GetString("filename");
            if (recid == 0)
            {
                msg = "{\"status\":\"error\",\"msg\":\"error04\"}";
            }
            else if (string.IsNullOrEmpty(serveraddr)) { msg = "{\"status\":\"error\",\"msg\":\"'error05\"}"; }
            //else if (string.IsNullOrEmpty(fileaddr)) { msg = "{status:'error',msg:'error06'}"; }
            //else if (string.IsNullOrEmpty(filename)) { msg = "{status:'error',msg:'error07'}"; }
            else
            {
                if (platform != "Android" && platform != "iOS") { msg = "{\"status\":\"error\",\"msg\":\"error03\"}"; }
                else
                {
                    string sql = string.Format(@"  update {2} set PackageTaskStatus=2, StartDatetime=GETDATE(),serverAddr='{0}'
                                               where RecID='{1}' and ((PackageTaskStatus=0 and DATEDIFF(N,StartDatetime,GETDATE())<5) or PackageTaskStatus=1)", serveraddr, recid, platform == "Android" ? "[sdk_NewPackageCreateTask]" : "[sdk_NewPackageCreateTask_IOS]");
                    int row = aideNativeWebFacade.ExecuteSql(sql);
                    if (row > 0)
                    {
                        msg = "{\"status\":\"success\",\"msg\":\"startTask_OK\"}";
                    }
                    else
                    {
                        msg = "{\"status\":\"error\",\"msg\":\"error08\"}";
                    }
                }
            }
            Response.Write(msg);
        }

        /// <summary>
        /// 完成任务
        /// </summary>
        private void FinishTask()
        {
            string msg = "";
            int recid = GameRequest.GetInt("taskid", 0);
            string platform = GameRequest.GetQueryString("platform");
            //string packageaddr = GameRequest.GetString("packageaddr");
            string packagename = GameRequest.GetString("packagename");
            if (recid == 0)
            {
                msg = "{\"status\":\"error\",\"msg\":\"error04\"}";
            }
            //else if (string.IsNullOrEmpty(packageaddr)) { msg = "{\"status\":\"error\",\"msg\":\"error09\"}"; }
            else if (string.IsNullOrEmpty(packagename)) { msg = "{\"status\":\"error\",\"msg\":\"error10\"}"; }
            else
            {
                if (platform != "Android" && platform != "iOS") { msg = "{\"status\":\"error\",\"msg\":\"error03\"}"; }
                else
                {
                    string sql = string.Format(@"  update {2} set PackageTaskStatus=3, FinishDatetime=GETDATE(),
                                                [packagename]='{1}' where RecID='{0}' and PackageTaskStatus=2", recid, packagename, platform == "Android" ? "[sdk_NewPackageCreateTask]" : "[sdk_NewPackageCreateTask_IOS]");
                    int row = aideNativeWebFacade.ExecuteSql(sql);
                    if (row > 0)
                    {
                        msg = "{\"status\":\"success\",\"msg\":\"finishTask_OK\"}";
                    }
                    else
                    {
                        msg = "{\"status\":\"error\",\"msg\":\"error11\"}";
                    }

                }

            }
            Response.Write(msg);
        }

        /// <summary>
        /// 打包任务失败
        /// </summary>
        private void LoseTask()
        {
            string msg = "";
            int recid = GameRequest.GetInt("taskid", 0);
            string platform = GameRequest.GetQueryString("platform");
            if (recid == 0)
            {
                msg = "{\"status\":\"error\",\"msg\":\"error04\"}";
            }
            else
            {
                if (platform != "Android" && platform != "iOS") { msg = "{\"status\":\"error\",\"msg\":\"error03\"}"; }
                else
                {
                    string sql = string.Format(@"update {1} set PackageTaskStatus=4,FinishDatetime=getdate() where recid={0} and (PackageTaskStatus=2 or PackageTaskStatus=0)", recid, platform == "Android" ? "[sdk_NewPackageCreateTask]" : "[sdk_NewPackageCreateTask_IOS]");
                    int row = aideNativeWebFacade.ExecuteSql(sql);
                    if (row > 0)
                    {
                        msg = "{\"status\":\"success\",\"msg\":\"loseTask_OK\"}";
                    }
                    else
                    {
                        msg = "{\"status\":\"error\",\"msg\":\"error12\"}";
                    }
                }
            }
            try
            {
                if (IsSendMail(recid))
                    SendMailUseGmail();
            }
            catch (Exception) { }
            Response.Write(msg);
        }

        private bool IsSendMail(int recid)
        {
            string sql = string.Format(@"  select * from [AspNetUserRoles] r inner join AspNetUsers u on r.UserId=u.Id and u.UserName=(select CreateUser from sdk_NewPackageCreateTask where recid={0}) and RoleId in (2,3)", recid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            if (ds.Tables[0].Rows.Count > 0)
                return false;
            else
                return true;
        }

        /// <summary>
        /// 未捕捉的action\(非法)
        /// </summary>
        protected void WriteError()
        {
            string msg = "{\"status\":\"error\",\"msg\":\"error01\"}";
            Response.Write(msg);
        }


        public string SendMailUseGmail()
        {
            int recid = GameRequest.GetInt("taskid", 0);
            string systemname = GameRequest.GetQueryString("platform");
            string sql = string.Format(@"  select npct.RecID as taskid,gi.GameDisplayName,u.Compellation,npct.CollectDatetime,dpf.PlatformDisplayName from 
  [{1}] npct
  inner join sdk_DefaultPlatform dpf on npct.RecID={0} and npct.PlatFormID=dpf.Id
  inner join sdk_GameInfo gi on npct.GameID=gi.GameID
  inner join AspNetUsers u on npct.CreateUser=u.Email", recid, systemname == "Android" ? "sdk_NewPackageCreateTask" : "sdk_NewPackageCreateTask_IOS");
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            string createuser = "";
            string gamename = "";
            string platformname = "";
            if (ds.Tables[0].Rows.Count > 0)
            {
                createuser = ds.Tables[0].Rows[0]["Compellation"].ToString();
                gamename = ds.Tables[0].Rows[0]["GameDisplayName"].ToString();
                platformname = ds.Tables[0].Rows[0]["PlatformDisplayName"].ToString();
            }

            string toMail = GameRequest.GetQueryString("platform") == "Android" ? System.Configuration.ConfigurationManager.AppSettings["SDKPackageLoseEmail_Android"] : System.Configuration.ConfigurationManager.AppSettings["SDKPackageLoseEmail_IOS"];


            string subject = string.Format("打包任务失败（{0}，{1}，{2}，{3}，{4}）", recid, gamename, systemname, platformname, createuser);//邮件标题 
            string context = GetErrorLog(ds);
            return SendEamil.SendMailUseGmail(toMail, subject, context);

        }

        private string GetErrorLog(DataSet ds)
        {
            int recid = GameRequest.GetInt("taskid", 0);
            string systemname = GameRequest.GetQueryString("platform");
            string createtaskid = GameRequest.GetQueryString("batchno");

            string logHeadText = "";
            if (ds.Tables[0].Rows.Count > 0)
            {
                logHeadText += "  任务ID  :    " + recid + "<br>";
                logHeadText += "  平  台  :    " + systemname + "<br>";
                logHeadText += "  游戏名称  :    " + ds.Tables[0].Rows[0]["GameDisplayName"].ToString() + "<br>";
                logHeadText += "  渠道名称  :    " + ds.Tables[0].Rows[0]["PlatformDisplayName"].ToString() + "<br>";
                logHeadText += "  创建人  :    " + ds.Tables[0].Rows[0]["Compellation"].ToString() + "<br>";
                logHeadText += "  创建时间  :    " + ds.Tables[0].Rows[0]["CollectDatetime"].ToString() + "<br>";
                logHeadText += "<br>  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓  详 细 错 误  ↓  ↓  ↓  ↓  ↓  ↓  ↓  ↓<br>";
            }

            try
            {
                string logPath;
                if (systemname == "Android")
                    logPath = "\\\\192.168.1.6/package_share/output/logs/" + createtaskid + "/" + recid + ".log ";
                else
                    logPath = "\\\\192.168.1.125/package_share_ios/output/logs/" + createtaskid + "/" + recid + ".log ";
                string content = "";
                if (systemname == "Android")
                {
                    using (StreamReader sr = new StreamReader(logPath, System.Text.Encoding.UTF8))
                    {
                        content = sr.ReadToEnd().Replace("\n", "<br>");
                    }
                    return logHeadText + content;
                }
                else
                {
                    content = "<a href=\"http://192.168.1.35:81/PJPackage/sdkPackageLog?taskid=" + recid + "&createtaskid=" + createtaskid + "&systemname=IOS\">点击查看详情</a>";
                    return logHeadText + content;
                }

            }
            catch (Exception) { return logHeadText + "由于未知原因打包失败，log 未生成，请联系打包组开发人员！taskid:" + recid + "  systemname:" + systemname + "  createtaskid:" + createtaskid + "\r\n logPath:\\\\192.168.1.125/package_share_ios/output/logs/" + createtaskid + "/" + recid + ".log "; }

        }

        #region replace.xml

        private void Test(int pluginid,string platformname)
        {
            string xmlfile = "D://test/";
            XmlDocument AndroidManifest1 = new XmlDocument();
            String AndroidManifestFile1 = xmlfile + "1.xml";//渠道（主）
            //XmlDocument AndroidManifest2 = new XmlDocument();
            //String AndroidManifestFile2 = xmlfile + "2.xml";//
            //XmlDocument AndroidManifest3 = new XmlDocument();
            //String AndroidManifestFile3 = xmlfile + "3.xml";//gamefile

            string content = "";
            AndroidManifest1.Load(AndroidManifestFile1);
            //AndroidManifest2.Load(AndroidManifestFile2);
            //AndroidManifest3.Load(AndroidManifestFile3);
            if (pluginid == 1)
            {
                AndroidManifest1 = GetPrimaryAndroidManifest(AndroidManifest1);
                GetManifest(xmlfile + "4.xml", AndroidManifest1.InnerXml, platformname, out content);
                //插件需删除主xml中的一个activety android:name="com.galaxy.sdk.android.amigo.MainActivity"

            }
            GetManifest(xmlfile + "2.xml", pluginid == 0 ? AndroidManifest1.InnerXml : content, out content);
            GetManifest(xmlfile + "3.xml", content, out content);


            XmlDocument manifestxml = new XmlDocument();
            manifestxml.LoadXml(content);
            manifestxml.Save("D://test/AndroidManifest.xml");
        }

        private void CheckManifest(GainTask gt)
        {
            CreateManifest(gt);
        }

        private void CreateManifest(GainTask gt)
        {
            int recid = gt.RecID;
            if (CreateFile(recid)) return;
            string platformname = gt.PlatFormName;
            string platformversion = gt.ChannelVersion;
            string myversion = gt.MyVersion;
            string gamename = gt.GameName;
            string gameid = gt.GameID;
            string gameversion = gt.GameVersion + "_" + gt.StrCollectDatetime;
            int pluginid = gt.PlugInID;
            string pluginversion = gt.PlugInVersion;



            string SDKPackageDir = "";
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKPackageDir"];
            //接入时生成的主项目配置
            string pfManifest = SDKPackageDir + "SDK\\Channel_SDK\\" + platformname + "\\" + platformversion + "\\MainActivity\\AndroidManifest.xml";
            //SDK基础库项目配置
            string typeManifest = SDKPackageDir + "SDK\\Type_SDK\\" + myversion + "\\lib\\TypeSDKBaseLibrary\\AndroidManifest.xml";
            //CP Unity项目生成的配置
            string gamefileManifest = SDKPackageDir + "game_file\\" + gameid + "\\" + gameversion + "\\AndroidManifest.xml";

           

            if (!System.IO.File.Exists(gamefileManifest))
            {
                string gamefile = System.Configuration.ConfigurationManager.AppSettings["SDKPackageDir"] + "game_file\\" + gameid + "\\" + gameversion + "\\";
                UnZip(gamefile + "game.zip", gamefile, "");
            }
            string pluginManifest = "";
            if (pluginid == 1)
            {
                pluginManifest = SDKPackageDir + "SDK\\Channel_SDK\\LeBian\\" + pluginversion + "\\MainActivity_LeBian\\AndroidManifest.xml";
            }

            string filepath = SDKPackageDir + "SDK/Extra_Config/Manifest/" + recid + "/";
            string temporaryFilename = "";
            CopyManifest(filepath, pfManifest, typeManifest, gamefileManifest, pluginManifest, out temporaryFilename);
            string xmlfile = filepath + temporaryFilename + "/";
            try
            {


                string content = "";

                XmlDocument AndroidManifest1 = new XmlDocument();
                String AndroidManifestFile1 = xmlfile + "1.xml";//渠道（主）
                //XmlDocument AndroidManifest2 = new XmlDocument();
                //String AndroidManifestFile2 = xmlfile + "2.xml";//
                //XmlDocument AndroidManifest3 = new XmlDocument();
                //String AndroidManifestFile3 = xmlfile + "3.xml";//gamefile

                AndroidManifest1.Load(AndroidManifestFile1);
                //AndroidManifest2.Load(AndroidManifestFile2);
                //AndroidManifest3.Load(AndroidManifestFile3);
                if (pluginid == 1)
                {
                    AndroidManifest1 = GetPrimaryAndroidManifest(AndroidManifest1);
                    GetManifest(xmlfile + "4.xml", AndroidManifest1.InnerXml,gt.PlatFormName, out content);
                    //插件需删除主xml中的一个activety android:name="com.galaxy.sdk.android.amigo.MainActivity"

                }
                GetManifest(xmlfile + "2.xml", pluginid == 0 ? AndroidManifest1.InnerXml : content, out content);
                GetManifest(xmlfile + "3.xml", content, out content);

                XmlDocument manifestxml = new XmlDocument();
                manifestxml.LoadXml(content);
                manifestxml.Save(filepath + "AndroidManifest.xml");


                //if (System.IO.Directory.Exists(xmlfile))
                //{
                //    System.IO.Directory.Delete(xmlfile, true);
                //}
                
            }
            catch (Exception)
            {

            }
        }



        /// <summary>
        /// 乐变 需去掉manifest-application-activity（action-category）这个节点
        /// </summary>
        /// <param name="AndroidManifest"></param>
        /// <returns></returns>
        private XmlDocument GetPrimaryAndroidManifest(XmlDocument AndroidManifest)
        {
            XmlNodeList nodelist = AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").SelectNodes("activity");
            bool flag = false;
            for (int i = 0; i < nodelist.Count; i++)
            {
                XmlNodeList intentNode = nodelist[i].SelectNodes("intent-filter");

                for (int j = 0; j < intentNode.Count; j++)
                {
                    XmlNode action = intentNode[j].SelectSingleNode("action");
                    XmlNode category = intentNode[j].SelectSingleNode("category");
                    if (action != null && category != null)
                    {
                        string action_value = action.Attributes["android:name"].Value;
                        string category_value = category.Attributes["android:name"].Value;
                        if (action_value == "android.intent.action.MAIN" && category_value == "android.intent.category.LAUNCHER")
                        {
                            AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").SelectNodes("activity")[i].RemoveChild(intentNode[j]);
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) break;
            }
            return AndroidManifest;
        }

        private bool CreateFile(int recid)
        {
            string SDKPackageDir = "";
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKPackageDir"];
            string filepath = SDKPackageDir + "SDK/Extra_Config/Manifest/" + recid + "/";
            CreatePatch(filepath);
            if (System.IO.File.Exists(filepath + "AndroidManifest.xml"))
            {
                return true;
            }
            return false;
        }

        private void CopyManifest(string filePaht, string pfManifest, string typeManifest, string gamefileManifest, string pluginManifest, out string temporaryFilename)
        {
            CreatePatch(filePaht, out temporaryFilename);
            System.IO.File.Copy(pfManifest, filePaht + temporaryFilename + "/1.xml", true);
            System.IO.File.Copy(typeManifest, filePaht + temporaryFilename + "/2.xml", true);
            System.IO.File.Copy(gamefileManifest, filePaht + temporaryFilename + "/3.xml", true);
            if (!string.IsNullOrEmpty(pluginManifest))
            {
                System.IO.File.Copy(pluginManifest, filePaht + temporaryFilename + "/4.xml", true);
            }
        }


        private void CreatePatch(string patch)
        {
            if (!System.IO.Directory.Exists(patch))
            {
                System.IO.Directory.CreateDirectory(patch);
            }
        }

        private void CreatePatch(string patch, out string filename)
        {
            string temporaryFilename = TextUtility.GetDateTimeLongString();
            if (System.IO.Directory.Exists(patch + temporaryFilename))
            {
                CreatePatch(patch, out filename);
            }
            System.IO.Directory.CreateDirectory(patch + temporaryFilename);
            filename = temporaryFilename;
        }

        private void GetManifest(string xmlPath, string maincontent, out string content)
        {
            XmlDocument AndroidManifest = new XmlDocument();
            String AndroidManifestFile = xmlPath;
            AndroidManifest.Load(AndroidManifestFile);

            XmlDocument MainAndroidManifest = new XmlDocument();
            MainAndroidManifest.LoadXml(maincontent);

            XmlNodeList usespermission = AndroidManifest.SelectSingleNode("manifest").SelectNodes("uses-permission");
            XmlNode manifest = MainAndroidManifest.SelectSingleNode("manifest");
            //XmlNode manifestapplication = MainAndroidManifest.SelectSingleNode("manifest").SelectNodes("application"); ;
            foreach (XmlNode node in usespermission)
            {
                //XmlElement element = node.;
                manifest.InnerXml += node.OuterXml;
            }
            //XmlNodeList application = AndroidManifest.SelectSingleNode("manifest").SelectNodes("application");
            if (manifest.SelectSingleNode("application") == null)
            {

            }
            else if (xmlPath.Substring(xmlPath.Length - 5, 5) == "3.xml")
            {
                XmlNodeList applicationNode = AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").ChildNodes;
                foreach (XmlNode node in applicationNode)
                {

                    if (node.Name == "activity")
                    {
                        XmlNamespaceManager xmlnsManager = new XmlNamespaceManager(new XmlDocument().NameTable);
                        xmlnsManager.AddNamespace("android", "http://schemas.android.com/apk/res/android");
                        //判断是否有启动项
                        XmlNode action = node.SelectSingleNode("intent-filter//action[@android:name='android.intent.action.MAIN']", xmlnsManager);
                        XmlNode category = node.SelectSingleNode("intent-filter//category[@android:name='android.intent.category.LAUNCHER']", xmlnsManager);
                        if (action != null && category != null)
                        {
                            continue;
                        }
                    }
                    else if (node.Name == "meta-data")
                    {
                        if (node.Attributes["android:value"].Value == "YOUR_CHANNEL_ID")
                        {
                            node.Attributes["android:value"].Value = "@sdk_name@";
                        }
                    }

                    manifest.SelectSingleNode("application").InnerXml += node.OuterXml;
                }
            }
            else
            {
                manifest.SelectSingleNode("application").InnerXml += AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").InnerXml;
            }

            //乐变manifest 需改变主文件的application key=android:name
            if (xmlPath.Substring(xmlPath.Length - 5, 5) == "4.xml")
            {
                XmlNode node = AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application");
                if (node != null)
                {
                    string strvalue = node.Attributes["android:name"].Value;
                    manifest.SelectSingleNode("application").Attributes["android:name"].Value = strvalue;
                    XmlNodeList nodelist = manifest.SelectSingleNode("application").SelectNodes("activity");//node.SelectNodes("activity");
                    foreach (XmlNode n in nodelist)
                    {
                        string androidname = n.Attributes["android:name"].Value;
                        int index = androidname.LastIndexOf(".") + 1;
                        string strGalaxySDKSplash = androidname.Substring(index, androidname.Length - index);
                        if (strGalaxySDKSplash == "GalaxySDKSplash")//闪屏
                        {
                            //乐变闪屏 需改变主文件的activity  android:name="com.excelliance.open.KXQP" 下的 meta-data ndroid:value="com.galaxy.sdk.android.@channelName@.MainActivity"

                            for (int i = 0; i < manifest.SelectSingleNode("application").SelectNodes("activity").Count; i++)
                            {
                                XmlNode nd = manifest.SelectSingleNode("application").SelectNodes("activity")[i];
                                string androidname2 = nd.Attributes["android:name"].Value;
                                int index2 = androidname2.LastIndexOf(".") + 1;
                                string strKXQP = androidname2.Substring(index2, androidname2.Length - index2);
                                if (strKXQP == "KXQP")//找到替换字段
                                {
                                    string replace_name = manifest.SelectSingleNode("application").SelectNodes("activity")[i].SelectSingleNode("meta-data").Attributes["android:value"].Value;
                                    int index3 = replace_name.LastIndexOf(".") + 1;
                                    manifest.SelectSingleNode("application").SelectNodes("activity")[i].SelectSingleNode("meta-data").Attributes["android:value"].Value = replace_name.Substring(0, index3) + strGalaxySDKSplash;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            content = MainAndroidManifest.InnerXml;
        }


        private void GetManifest(string xmlPath, string maincontent, string platformName, out string content)
        {
            XmlDocument AndroidManifest = new XmlDocument();
            String AndroidManifestFile = xmlPath;
            AndroidManifest.Load(AndroidManifestFile);

            XmlDocument MainAndroidManifest = new XmlDocument();
            MainAndroidManifest.LoadXml(maincontent);

            XmlNodeList usespermission = AndroidManifest.SelectSingleNode("manifest").SelectNodes("uses-permission");
            XmlNode manifest = MainAndroidManifest.SelectSingleNode("manifest");
            //XmlNode manifestapplication = MainAndroidManifest.SelectSingleNode("manifest").SelectNodes("application"); ;
            foreach (XmlNode node in usespermission)
            {
                //XmlElement element = node.;
                manifest.InnerXml += node.OuterXml;
            }
            //XmlNodeList application = AndroidManifest.SelectSingleNode("manifest").SelectNodes("application");
            if (manifest.SelectSingleNode("application") == null)
            {

            }
            else if (xmlPath.Substring(xmlPath.Length - 5, 5) == "3.xml")
            {
                XmlNodeList applicationNode = AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").ChildNodes;
                foreach (XmlNode node in applicationNode)
                {

                    if (node.Name == "activity")
                    {
                        XmlNamespaceManager xmlnsManager = new XmlNamespaceManager(new XmlDocument().NameTable);
                        xmlnsManager.AddNamespace("android", "http://schemas.android.com/apk/res/android");
                        //判断是否有启动项
                        XmlNode action = node.SelectSingleNode("intent-filter//action[@android:name='android.intent.action.MAIN']", xmlnsManager);
                        XmlNode category = node.SelectSingleNode("intent-filter//category[@android:name='android.intent.category.LAUNCHER']", xmlnsManager);
                        if (action != null && category != null)
                        {
                            continue;
                        }
                    }
                    else if (node.Name == "meta-data")
                    {
                        if (node.Attributes["android:value"].Value == "YOUR_CHANNEL_ID")
                        {
                            node.Attributes["android:value"].Value = "@sdk_name@";
                        }
                    }

                    manifest.SelectSingleNode("application").InnerXml += node.OuterXml;
                }

                //处理android:versionCode和android:versionName
                manifest.Attributes["android:versionCode"].Value = AndroidManifest.SelectSingleNode("manifest").Attributes["android:versionCode"].Value;
                manifest.Attributes["android:versionName"].Value = AndroidManifest.SelectSingleNode("manifest").Attributes["android:versionName"].Value;
            }
            else
            {
                manifest.SelectSingleNode("application").InnerXml += AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application").InnerXml;
            }

            //乐变manifest 需改变主文件的application key=android:name
            if (xmlPath.Substring(xmlPath.Length - 5, 5) == "4.xml")
            {
                XmlNode node = AndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application");
                if (node != null)
                {
                    string strvalue = node.Attributes["android:name"].Value;
                    manifest.SelectSingleNode("application").Attributes["android:name"].Value = strvalue;
                    XmlNodeList nodelist = manifest.SelectSingleNode("application").SelectNodes("activity");//node.SelectNodes("activity");
                    foreach (XmlNode n in nodelist)
                    {
                        string androidname = n.Attributes["android:name"].Value;
                        int index = androidname.LastIndexOf(".") + 1;
                        string strGalaxySDKSplash = androidname.Substring(index, androidname.Length - index);
                        if (strGalaxySDKSplash == "GalaxySDKSplash")//闪屏
                        {
                            //乐变闪屏 需改变主文件的activity  android:name="com.excelliance.open.KXQP" 下的 meta-data ndroid:value="com.galaxy.sdk.android.@channelName@.MainActivity"

                            for (int i = 0; i < manifest.SelectSingleNode("application").SelectNodes("activity").Count; i++)
                            {
                                XmlNode nd = manifest.SelectSingleNode("application").SelectNodes("activity")[i];
                                string androidname2 = nd.Attributes["android:name"].Value;
                                int index2 = androidname2.LastIndexOf(".") + 1;
                                string strKXQP = androidname2.Substring(index2, androidname2.Length - index2);
                                if (strKXQP == "KXQP")//找到替换字段
                                {
                                    string replace_name = manifest.SelectSingleNode("application").SelectNodes("activity")[i].SelectSingleNode("meta-data").Attributes["android:value"].Value;
                                    int index3 = replace_name.LastIndexOf(".") + 1;
                                    manifest.SelectSingleNode("application").SelectNodes("activity")[i].SelectSingleNode("meta-data").Attributes["android:value"].Value = replace_name.Substring(0, index3) + strGalaxySDKSplash;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (platformName == "Lenovo")//联想
                    {
                        XmlNamespaceManager xmlnsManager = new XmlNamespaceManager(new XmlDocument().NameTable);
                        xmlnsManager.AddNamespace("android", "http://schemas.android.com/apk/res/android");
                        MainAndroidManifest.SelectSingleNode("manifest").SelectSingleNode("application//activity[@android:name='com.excelliance.open.KXQP']", xmlnsManager).SelectSingleNode("meta-data").Attributes["android:value"].Value = "com.lenovo.lsf.gamesdk.ui.WelcomeActivity";
                    }

                }
            }
            content = MainAndroidManifest.InnerXml;
        }

        public static string UnZip(string fileToUnZip, string zipedFolder, string password)
        {
            //bool result = true;
            string result = "";
            FileStream fs = null;
            ZipInputStream zipStream = null;
            ZipEntry ent = null;
            string fileName;

            if (!File.Exists(fileToUnZip))
                return "包文件内容错误";//return false;

            if (!Directory.Exists(zipedFolder))
                Directory.CreateDirectory(zipedFolder);

            try
            {
                zipStream = new ZipInputStream(File.OpenRead(fileToUnZip));
                if (!string.IsNullOrEmpty(password)) zipStream.Password = password;
                while ((ent = zipStream.GetNextEntry()) != null)
                {
                    if (ent.Name.Contains("AndroidManifest.xml"))
                    {
                        fileName = Path.Combine(zipedFolder, ent.Name.Substring(5, ent.Name.Length - 5));
                        fileName = fileName.Replace('/', '\\');//change by Mr.HopeGi   

                        //if (fileName.EndsWith("\\"))
                        //{
                        //Directory.CreateDirectory(zipedFolder + "\\");
                        //continue;
                        //}

                        fs = File.Create(fileName);
                        int size = 2048;
                        byte[] data = new byte[size];
                        while (true)
                        {
                            size = zipStream.Read(data, 0, data.Length);
                            if (size > 0)
                            {
                                fs.Write(data, 0, size);
                                fs.Flush();
                            }
                            else
                                break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                result = "找不到Game文件夹，请将工程文件放在Game文件夹下压缩";//ex.Message;//result = false;
            }
            finally
            {
                if (fs != null)
                {
                    fs.Close();
                    fs.Dispose();
                }
                if (zipStream != null)
                {
                    zipStream.Close();
                    zipStream.Dispose();
                }
                if (ent != null)
                {
                    ent = null;
                }
                GC.Collect();
                GC.Collect(1);
            }
            return result;
        }


        #endregion
    }
}