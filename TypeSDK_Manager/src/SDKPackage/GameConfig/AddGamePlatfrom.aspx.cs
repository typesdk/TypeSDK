using SDKPackage.Facade;
using SDKPackage.Kernel;
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

using ServiceStack.Redis;
using Newtonsoft.Json;
using System.Net;

namespace SDKPackage.GameConfig
{
    public partial class AddGamePlatfrom : System.Web.UI.Page
    {
        protected int gameid = GameRequest.GetQueryInt("gameid", 0);
        protected string androidversionid = GameRequest.GetQueryString("androidversionid");
        protected string iosversionid = GameRequest.GetQueryString("iosversionid");
        protected string gamedisplayname = HttpUtility.UrlDecode(GameRequest.GetQueryString("gamedisplayname"));
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string platformname = GameRequest.GetQueryString("platformname");
        protected string platformid = GameRequest.GetQueryString("platformid");
        static DataSet dsAndroidPlatformVersion = new DataSet();
        static DataSet dsIosPlatformVersion = new DataSet();
        static DataSet dsGP = new DataSet();

        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();


        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack) { BindingDsAndroidPlatformVersion();  }
        }

        private void BindingDsAndroidPlatformVersion()
        {
            string sqlAndroid = string.Format(@"select id,PlatformID,[Version] from [sdk_PlatformVersion] where SystemID=1");
            string sqlIos = string.Format(@"select id,PlatformID,[Version] from [sdk_PlatformVersion] where SystemID=2");
            dsAndroidPlatformVersion = aideNativeWebFacade.GetDataSetBySql(sqlAndroid);
            dsIosPlatformVersion = aideNativeWebFacade.GetDataSetBySql(sqlIos);

            string sqlGPAndroid = string.Format(@"select 
                                            gi.GameID as GameID,
                                            gpfi.VersionID as VersionID,
                                            dpf.id as Platformid,
                                            pv.[Version] as [Version]
                                            from sdk_GamePlatFromInfo gpfi 
	                                            inner join [sdk_GameInfo] gi on gpfi.GameID=gi.GameID and gi.GameID='{0}' and gpfi.SystemID='1' 
	                                            inner join sdk_defaultPlatform dpf on gpfi.[VersionPlatFromID]=dpf.id 
	                                            left join sdk_PlatformVersion pv on gpfi.VersionID=pv.ID", gameid);
            dsGP = aideNativeWebFacade.GetDataSetBySql(sqlGPAndroid);
        }

        private static object OrderGP(DataRow obj)
        {
            int ret = 1;
            var dr = dsGP.Tables[0].AsEnumerable().Where(r => String.Equals(r["Version"].ToString(), obj["Version"].ToString())).Select(d => d);
            if (dr.Count() > 0)
            {
                ret = 0;
            }
            return ret;
        } 

        protected DataSet GetAndroidPlatformVersion(string pid)
        {
            DataSet dsPlatform = SetDsHead();
            var dr = dsAndroidPlatformVersion.Tables[0].AsEnumerable().Where(r => r["PlatformID"].ToString() == pid).Select(d => d).OrderBy(p => OrderGP(p));

            if (dr.Count() > 0)
            {
                foreach (var row in dr)
                {
                    dsPlatform.Tables[0].ImportRow(row);
                }
                //return dsPlatform;
            }
            return dsPlatform;
        }

        /// <summary>
        /// 获取插件版本
        /// </summary>
        /// <param name="pluginid"></param>
        /// <returns></returns>
        protected DataSet GetPlugInVersionList(string pluginid)
        {
            string sql = string.Format(@"select pv.id,pv.pluginversion,gpfi.pluginversion as db from [sdk_PlugInVersion] pv
                                         left join (select top 1 pluginid,pluginversion from [sdk_GamePlatFromInfo] where GameID={0} and pluginid={1} )
                                         gpfi on pv.PlugInVersion=gpfi.PlugInVersion where pv.pluginid={1} order by db desc", gameid, pluginid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            return ds;
        }



        private DataSet SetDsHead()
        {
            DataSet ds = new DataSet();
            DataTable dt = new DataTable();
            //DataRow dr = dt.NewRow();
            dt.Columns.Add("id", typeof(string));
            dt.Columns.Add("PlatformID", typeof(string));
            dt.Columns.Add("Version", typeof(string));
            ds.Tables.Add(dt);
            return ds;
        }

        protected DataSet GetIosPlatformVersion(string pid)
        {
            DataSet dsPlatform = SetDsHead();
            var dr = dsIosPlatformVersion.Tables[0].AsEnumerable().Where(r => r["PlatformID"].ToString() == pid).Select(d => d);

            if (dr.Count() > 0)
            {
                foreach (var row in dr)
                {
                    dsPlatform.Tables[0].ImportRow(row);
                }
                
            }
            return dsPlatform;
        }

         /// <summary>
        /// 添加渠道关联
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddIOSPlatform_Click(object sender, EventArgs e)
        {
            string platfromList = this.hfIOSPlatformList.Value;
            string versionList = this.hfIOSVersionList.Value;

            Message umsg = aideNativeWebFacade.UpdateGamePlatform(gameid, platfromList, versionList, 2);
            if (umsg.Success)
            {
                //UpdateFile(0);
                ListViewIOS.DataBind();
            }
            else
            {
            }
        }

        /// <summary>
        /// 添加渠道关联
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddAndroidPlatform_Click(object sender, EventArgs e)
        {
            string platfromList = this.hfAndroidPlatformList.Value;
            string signList = this.hfAndroidSignList.Value;
            //string initSignList = this.hfInitSignList.Value;
            string versionList = hfAndroidVersionList.Value;
            //return;
            Message umsg = aideNativeWebFacade.UpdateGamePlatform(gameid, platfromList, versionList, signList, 1, "0", "");
            if (umsg.Success)
            {
                UpdateFile(0);
                ListViewAndroid.DataBind();
            }
            else
            {
            }
        }

        /// <summary>
        /// 同步Redis
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonSyncRedis_Click(object sender, EventArgs e)
        {
            string sdkhost = CtrlHelper.GetText(txtSDKHost);
            int sdkport = 40000;
            int.TryParse(CtrlHelper.GetText(txtSDKPort), out sdkport);

            if (String.IsNullOrWhiteSpace(sdkhost))
            {
                lblSyncRedis.Text = "HOST 不能为空";
                return;
            }

            try
            {
                //var redisClient = new RedisClient(redishost, redisport, redispass, redisdb);
                //var str = redisClient.Get<String>("GAME:ID:IDENTITY");
                //System.Console.WriteLine(str);

                string apikey = "";

                string sqlGameInfo = string.Format(@"select [GameID],[GameName],[SDKGameID],[SDKGameKey] from [sdk_GameInfo] where [GameID]={0}", gameid);
                //string sqlIos = string.Format(@"select id,PlatformID,[Version] from [sdk_PlatformVersion] where SystemID=2");
                var gameobj = aideNativeWebFacade.GetDataSetBySql(sqlGameInfo).Tables[0].Rows[0];

                var objgameid = new { id = (string)gameobj["SDKGameID"], apikey = (string)gameobj["SDKGameKey"], gameName = (string)gameobj["GameName"] };
                if (gameobj == null
                    || string.IsNullOrWhiteSpace(objgameid.id)
                    || string.IsNullOrWhiteSpace(objgameid.apikey)
                    || string.IsNullOrWhiteSpace(objgameid.gameName))
                {
                    lblSyncRedis.Text = "数据异常";
                    return;
                }

                string sqlGameChannelProductList = string.Format(@"SELECT [GameName],[PlatformName],[itemid],[itemcpid],[itemid],[price],[name],[type],[info] FROM [sdk_PlatformConfigProductList] WHERE [GameName]='{0}'", gameid);
                var gamechannelattrsproductlistobj = aideNativeWebFacade.GetDataSetBySql(sqlGameChannelProductList).Tables[0].AsEnumerable();
                Dictionary<string, List<object>> productlist = new Dictionary<string, List<object>>();
                foreach (var item in gamechannelattrsproductlistobj)
                {
                    if (!productlist.ContainsKey(item["PlatformName"].ToString()))
                    {
                        productlist.Add(item["PlatformName"].ToString(), new List<object>());
                    }

                    productlist[item["PlatformName"].ToString()].Add(new
                    {
                        itemid = item["itemid"],
                        itemcpid = item["itemcpid"],
                        price = item["price"],
                        name = item["name"],
                        type = item["type"],
                        info = item["info"]
                    });
                }

                string sqlGameChannelAttrs = string.Format(@"SELECT [GameName],[PlatformName],[SDKKey],[StringValue] 
                                                              FROM [sdk_PlatformConfig] conf 
                                                              WHERE [GameName]='{0}' 
                                                              AND SDKKey!='SignatureKey'  
                                                              AND [isServer] = 1 
                                                              AND [PlatformName] IN ( 
                                                                SELECT convert(varchar(20),[VersionPlatFromID]) 
                                                                FROM [sdk_GamePlatFromInfo] info 
                                                                WHERE  info.[GameID] =conf.[GameName])
                                                              ORDER BY SDKKey", gameid);
                var gamechannelattrsobj = aideNativeWebFacade.GetDataSetBySql(sqlGameChannelAttrs).Tables[0].AsEnumerable();
                //string json = JsonConvert.SerializeObject(gamechannelattrsobj);
                Dictionary<string, Dictionary<string, object>> dicattrslist = new Dictionary<string, Dictionary<string, object>>();
                foreach(var item in gamechannelattrsobj){
                    if (!dicattrslist.ContainsKey(item["PlatformName"].ToString()))
                    {
                        dicattrslist.Add(item["PlatformName"].ToString(), new Dictionary<string, object>());
                    }
                    dicattrslist[item["PlatformName"].ToString()].Add(item["SDKKey"].ToString(), item["StringValue"].ToString());
                }

                Dictionary<string,object> jsonoutlist = new Dictionary<string,object>();
                foreach (var item in dicattrslist)
                {
                    item.Value.Add("itemLists", productlist.ContainsKey(item.Key) ? productlist[item.Key] : new List<object>());
                    var jsonout = new { id = item.Value["channel_id"], name = item.Value["sdk_name"], attrs =item.Value };
                    if (!string.IsNullOrWhiteSpace((string)item.Value["channel_id"]))
                    {
                        if (jsonoutlist.ContainsKey("ch" + item.Value["channel_id"]))
                        {
                            throw new ChannelException("渠道编号 " + item.Key + " ChannelID配置重复，请检查");
                        }
                        else
                        {
                            jsonoutlist.Add("ch" + item.Value["channel_id"], jsonout);
                        }
                    }
                }

                Encoding encoding = Encoding.GetEncoding("UTF-8");
                Stream outstream = null;
                Stream instream = null;
                StreamReader sr = null;
                string url = "http://" + sdkhost + ":" + sdkport + "/" + objgameid.id + "/1/SetChannelConfig";
                HttpWebRequest request = null;
                HttpWebResponse response = null;

                // 准备请求,设置参数  
                request = WebRequest.Create(url) as HttpWebRequest;
                request.Method = "POST";
                request.ContentType = "application/x-www-form-urlencoded";

                string strdata = JsonConvert.SerializeObject(new { objgameid = objgameid, jsonoutlist = jsonoutlist });

                byte[] data = encoding.GetBytes(url + "&data=" + strdata);
                request.ContentLength = data.Length;
                outstream = request.GetRequestStream();
                outstream.Write(data, 0, data.Length);
                outstream.Flush();
                outstream.Close();
                //发送请求并获取相应回应数据  


                response = request.GetResponse() as HttpWebResponse;
                //直到request.GetResponse()程序才开始向目标网页发送Post请求  
                instream = response.GetResponseStream();
                sr = new StreamReader(instream, encoding);
                //返回结果网页(html)代码  

                string content = sr.ReadToEnd();
                SdkResult jsonContent = JsonConvert.DeserializeObject<SdkResult>(content);
                if (jsonContent.code == 0)
                {
                    lblSyncRedis.Text = "SDK 同步成功";
                }
                else
                {
                    lblSyncRedis.Text = "SDK 同步出错: " + content;
                }
                
                //using (IRedisTransaction IRT = redisClient.CreateTransaction())
                //{
                //    //1.更新 'GAME:' + gameid + ':ID' 写入游戏信息，必须有游戏id name gkey
                //    string keygameid = "GAME:" + objgameid.id + ":ID";
                //    IRT.QueueCommand(r => r.SetEntryInHash(keygameid, "id", objgameid.id));
                //    IRT.QueueCommand(r => r.SetEntryInHash(keygameid, "gameName", objgameid.gameName));
                //    IRT.QueueCommand(r => r.SetEntryInHash(keygameid, "apikey", objgameid.apikey));

                //    //2.更新 'GAME:' + gamename + ':NAME' 写入游戏信息，{id:game.id,name:game.gameName}
                //    string keygamename = "GAME:" + objgameid.gameName + ":NAME";
                //    IRT.QueueCommand(r => r.SetEntryInHash(keygamename, "id", objgameid.id));
                //    IRT.QueueCommand(r => r.SetEntryInHash(keygamename, "name", objgameid.gameName));

                //    //3.操作 'GAME:' + gameid + ':CHANNEL' 写入渠道attr信息
                //    string keygamechannel = "GAME:" + objgameid.id + ":CHANNEL";
                //    foreach (var jsonout in jsonoutlist)
                //    {
                //        IRT.QueueCommand(r => r.SetEntryInHash(keygamechannel, jsonout.Key, JsonConvert.SerializeObject(jsonout.Value)));
                //    }

                //    //4.更新 'GAME:' + gameId + ':VERSION' ++
                //    string keygameversion = "GAME:" + objgameid.id + ":VERSION";
                //    IRT.QueueCommand(r => r.IncrementValueInHash(keygameversion,"version", 1));

                //    IRT.Commit(); // 提交事务
                //}
                

                //var objgamename = new { id = gameid, name = gamename };

                //lblSyncRedis.Text = "同步成功";
            }
            catch (ChannelException ex)
            {
                lblSyncRedis.Text = "SDK 同步出错: " + ex.Message;
                return;
            }
            catch(Exception ex)
            {
                //lblSyncRedis.Text = "SDK 同步出错";
                lblSyncRedis.Text = "SDK 同步出错: " + ex.Message + "sdkhost:" + sdkhost + ", sdkport:" + sdkport;
                return;
            } 
        }

        private void UpdateFile(int pluginid)
        {
            string sql = string.Format(@"  select gi.GameID,gi.GameName,dpf.PlatformName,dpf.ID from 
                                           sdk_GameInfo gi
                                           inner join sdk_GamePlatFromInfo gpi on gi.GameID=gpi.GameID and gi.GameID={0} and gpi.SystemID=1
                                           inner join sdk_DefaultPlatform dpf on gpi.VersionPlatFromID=dpf.id", gameid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            for (int i = 0; i < ds.Tables[0].Rows.Count; i++)
            {
                string filePatch = ds.Tables[0].Rows[i]["GameName"] + "\\" + ds.Tables[0].Rows[i]["PlatformName"] + pluginid.ToString() == "0" ? "" : "_LeBian" + "\\";
                CreateCPSettings(filePatch, ds.Tables[0].Rows[i]["GameID"].ToString(), ds.Tables[0].Rows[i]["ID"].ToString(), pluginid);
                CreateLocalConfig(filePatch, ds.Tables[0].Rows[i]["GameID"].ToString(), ds.Tables[0].Rows[i]["ID"].ToString(), pluginid);
            }
        }

        //private void UpdatePlatformMainifest()
        //{
        //    string pluginversion = this.DropDownListLeBianVersion.SelectedItem.Text;
        //    string platfromList = this.hfLeBianAndroidPlatformList.Value;
        //    string versionList = hfLeBianAndroidVersionList.Value;
        //    string[] arrPlatform = platfromList.Split(',');
        //    string[] arrVersion = versionList.Split(',');
        //    string sql = string.Format(@"select PlatformName from sdk_DefaultPlatform where id in ({0}) order by id", platfromList);
        //    DataSet dsPlatformname = aideNativeWebFacade.GetDataSetBySql(sql);//获取渠道名称

        //    string SDKPackageDir = "";
        //    SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageFinalMainifest"];
        //    string filename = SDKPackageDir + "AndroidManifest_LeBian";
        //    for (int i = 0; i < arrPlatform.Length; i++)
        //    {
        //        string pfname = dsAndroidPlatformVersion.Tables[0].Rows[i]["PlatformName"].ToString();
        //        filename += pluginversion + pfname + arrVersion[i] + ".xml";
        //        if (System.IO.File.Exists(filename))
        //        {
        //            //删除创建
        //            System.IO.File.Delete(filename);
        //        }
        //        //重新创建Mianifest

        //    }
        //}

        //private void CreateLeBianMainifest(string pfname,string pfversion,string lbversion)
        //{
        //    string SDKPackageDir = "";
        //    SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageInitialMainifest"];
        //    string platform_mainifest = "";
        //    string lebian_mainifest = "";
        //    string pfxml = SDKPackageDir + pfname + "\\" + pfversion + "\\MainActivity\\AndroidManifest.xml";
        //    string lbxml = SDKPackageDir + "LeBian\\" + lbversion + "\\MainActivity_LeBian\\AndroidManifest.xml";
            
        //}




        private void CreateCPSettings(string filePatch, string gid, string pid, int pluginid)
        {
            string SDKPackageDir = "";
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
            filePatch = SDKPackageDir + filePatch;
            if (!Directory.Exists(filePatch))
            {
                return;
            }
            Dictionary<string, string> dic = new Dictionary<string, string>();
            dic.Add("GameName", gid);
            dic.Add("PlatformName", pid);
            dic.Add("PlugInID", pluginid.ToString());
            DataView dvCpSetting = aideNativeWebFacade.ExecuteStoredProcedureByDataSet("sdk_getPlatformConfigCPSetting", dic).Tables[0].DefaultView;

            String fileCpSetting = filePatch + "\\CPSettings.txt";
            String jsonCpSetting = ToJson(dvCpSetting);
            StreamWriter sw = new StreamWriter(fileCpSetting, false, Encoding.UTF8);
            sw.Write(jsonCpSetting);
            sw.Close();
        }


        private void CreateLocalConfig(string filePatch, string gid, string pid, int pluginid)
        {
            string SDKPackageDir = "";
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
            filePatch = SDKPackageDir + filePatch;
            if (!Directory.Exists(filePatch))
            {
                return;
            }
            Dictionary<string, string> dic = new Dictionary<string, string>();
            dic.Add("GameName", gid);
            dic.Add("PlatformName", pid);
            dic.Add("PlugInID", pluginid.ToString());
            DataView dvLocalConfig = aideNativeWebFacade.ExecuteStoredProcedureByDataSet("sdk_getPlatformConfigLocal", dic).Tables[0].DefaultView;

            //DataView dvLocalConfig = new DataSet().Tables[0].DefaultView;
            String localConfig = ToConfig(dvLocalConfig);
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
                string strKey = drc[i][0].ToString();
                string strValue = drc[i][1].ToString();
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

    }

    class SdkResult
    {
        public int code;
        public string msg;
    }

    class ChannelException : ApplicationException
    {
        public ChannelException()      
        {      
        }
        public ChannelException(string message): base(message)
        {

        }
        public ChannelException(string message, Exception inner):base(message,inner)     
        {    
 
        }
    }
}