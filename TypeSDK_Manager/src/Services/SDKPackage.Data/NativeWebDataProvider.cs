using SDKPackage.Entity.NativeWeb;
using SDKPackage.IData;
using SDKPackage.Kernel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Common;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Data
{
    public class NativeWebDataProvider : BaseDataProvider, INativeWebDataProvider
    {
        #region 构造方法

        /// <summary>
        /// 构造函数
        /// </summary>
        public NativeWebDataProvider(string connString)
            : base(connString)
        {


        }
        #endregion

        #region 游戏管理

        /// <summary>
        /// 添加游戏
        /// </summary>
        /// <param name="gamename"></param>
        /// <param name="gamedisplayname"></param>
        /// <param name="androidVersionID"></param>
        /// <param name="iosVersionID"></param>
        /// <param name="AndroidKeyID"></param>
        /// <param name="iosKeyID"></param>
        /// <param name="gameicon"></param>
        /// <param name="createuser"></param>
        /// <returns></returns>
        public Message AddGame(string gamename, string gamedisplayname, string androidVersionID, string iosVersionID, string AndroidKeyID, string iosKeyID, string gameicon, string createuser, string gameNameSpell, string UnityVer, string gameProductName, string gameIsEncryption,string sdkgameid,string sdkgamekey)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameName", gamename));
            prams.Add(Database.MakeInParam("GameDisplayName", gamedisplayname));
            prams.Add(Database.MakeInParam("AndroidVersionID", androidVersionID));
            prams.Add(Database.MakeInParam("IOSVersionID", iosVersionID));
            prams.Add(Database.MakeInParam("AndroidKeyID", AndroidKeyID));
            prams.Add(Database.MakeInParam("IOSKeyID", iosKeyID));
            prams.Add(Database.MakeInParam("GameIcon", gameicon));
            prams.Add(Database.MakeInParam("CreateUser", createuser));
            prams.Add(Database.MakeInParam("GameNameSpell", gameNameSpell));
            prams.Add(Database.MakeInParam("UnityVer", UnityVer));
            prams.Add(Database.MakeInParam("ProductName", gameProductName));
            prams.Add(Database.MakeInParam("IsEncryption", gameIsEncryption));

            prams.Add(Database.MakeInParam("SDKGameID", sdkgameid));
            prams.Add(Database.MakeInParam("SDKGameKey", sdkgamekey));

            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            
            return MessageHelper.GetMessage(Database, "sdk_addGame", prams);
        }


        /// <summary>
        /// 获取角色权限下的游戏详细列表
        /// </summary>
        /// <param name="username"></param>
        /// <returns></returns>
        public DataSet GetGameInfoList(string username)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("UserName", username));
            return Database.ExecuteDataset(CommandType.StoredProcedure, "sdk_getGameInfoList", prams.ToArray());
        }

        /// <summary>
        /// 删除游戏
        /// </summary>
        /// <param name="gameid"></param>
        /// <returns></returns>
        public Message DeleteGame(string gameid)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_deleteGame", prams);
        }



        /// <summary>
        /// 更新游戏关联渠道Andorid
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformlist"></param>
        /// <param name="singkeyidlist"></param>
        /// <returns></returns>
        public Message UpdateGamePlatform(int gameid, string platformlist, string versionList, string singkeyidlist, int systemid, string pluginid, string pluginversion)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("PlatfomrList", platformlist));
            prams.Add(Database.MakeInParam("VersionList", versionList));
            prams.Add(Database.MakeInParam("SignatureKeyIDList", singkeyidlist));
            prams.Add(Database.MakeInParam("SystemID", systemid));
            prams.Add(Database.MakeInParam("PlugInID", pluginid));
            prams.Add(Database.MakeInParam("PlugInVersion", pluginversion));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_UpdateGamePlatform", prams);
        }

        /// <summary>
        /// 更新游戏关联渠道IOS
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformlist"></param>
        /// <param name="systemid"></param>
        /// <returns></returns>
        public Message UpdateGamePlatform(int gameid, string platformlist, string versionList, int systemid)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("PlatfomrList", platformlist));
            prams.Add(Database.MakeInParam("VersionList", versionList));
            prams.Add(Database.MakeInParam("SystemID", systemid));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_UpdateGamePlatform_IOS", prams);
        }

        #endregion

        #region 游戏包管理

        //public Message

        #endregion

        #region 打包任务处理

        /// <summary>
        /// 获取任务（share接口）
        /// </summary>
        /// <param name="platform"></param>
        /// <returns></returns>
        public Message GetGainTask(string platform)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("SystemName", platform));
            return MessageHelper.GetMessageForObject<GainTask>(Database, "sdk_getPackageTask", prams);
        }

        /// <summary>
        /// 新建打包任务
        /// </summary>
        /// <param name="taskid"></param>
        /// <param name="createuser"></param>
        /// <param name="placeidlist"></param>
        /// <param name="createtaskid"></param>
        /// <returns></returns>
        public Message AddNewPackageTask(int taskid, string createuser, string placeidlist, string createtaskid, string systemname, int gameid, string gameversion, string gamelable, string platformversionlist, string gameIsEncryption)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("dwTaskID", taskid));
            prams.Add(Database.MakeInParam("dwCreateUser", createuser));
            prams.Add(Database.MakeInParam("dwPlaceIDList", placeidlist));
            prams.Add(Database.MakeInParam("dwCreateTaskID", createtaskid));
            prams.Add(Database.MakeInParam("SystemName", systemname));
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("GameVersion", gameversion));
            prams.Add(Database.MakeInParam("GameLable", gamelable));
            prams.Add(Database.MakeInParam("PlatformVersionList", platformversionlist));
            prams.Add(Database.MakeInParam("IsEncryption", gameIsEncryption));
            return MessageHelper.GetMessage(Database, "sdk_AddNewPackageTaskTest", prams);
        }

        /// <summary>
        /// 删除任务
        /// </summary>
        /// <param name="recid"></param>
        /// <param name="systemname"></param>
        public void DeleteNewPackageTask(int recid, string systemname)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("RecID", recid));
            prams.Add(Database.MakeInParam("SystemName", systemname));
            Database.ExecuteNonQuery(CommandType.StoredProcedure, "sdk_deletePackageCreateTask", prams.ToArray());
        }

        #endregion

        #region Ad打包任务处理
        /// <summary>
        /// 添加ad打包任务
        /// </summary>
        /// <param name="recid"></param>
        /// <param name="gameid"></param>
        /// <param name="adid"></param>
        /// <param name="adname"></param>
        /// <param name="createtaskid"></param>
        public void CreateAdPackageTask(string recid, string gameid, string adid, string adname, string createtaskid, string username)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("RecID", recid));
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("AdID", adid));
            prams.Add(Database.MakeInParam("AdName", adname));
            prams.Add(Database.MakeInParam("CreateTaskID", createtaskid));
            prams.Add(Database.MakeInParam("CreateUser", username));
            Database.ExecuteNonQuery(CommandType.StoredProcedure, "sdk_AddNewAdPackageTask", prams.ToArray());

        }

        #endregion

        #region 版本管理

        /// <summary>
        /// 新增版本(老)
        /// </summary>
        /// <param name="version"></param>
        /// <param name="accoutns"></param>
        /// <returns></returns>
        public Message AddMyVersion(string version, string accoutns, int platformid, int myversionid)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("dwMyVersionName", version));
            prams.Add(Database.MakeInParam("dwAccoutns", accoutns));
            prams.Add(Database.MakeInParam("dwFlatformID", platformid));
            prams.Add(Database.MakeInParam("dwMyVersionID", myversionid));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_AddMyVersion", prams);
        }

        /// <summary>
        /// 添加新渠道版本
        /// </summary>
        /// <param name="version"></param>
        /// <param name="username"></param>
        /// <param name="platformid"></param>
        /// <param name="systemid"></param>
        /// <returns></returns>
        public Message AddMyVersion(string version, string username, string platformid, string systemid)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("PlatformVersion", version));
            prams.Add(Database.MakeInParam("CreateUser", username));
            prams.Add(Database.MakeInParam("PlatformID", platformid));
            prams.Add(Database.MakeInParam("SystemID", systemid));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_setPlatformVersion", prams);
        }

        /// <summary>
        /// 新增渠道
        /// </summary>
        /// <param name="platformName"></param>
        /// <param name="platformDisplayName"></param>
        /// <param name="sdkVersion"></param>
        /// <param name="myversionid"></param>
        /// <param name="systemID"></param>
        /// <returns></returns>
        public Message AddPlatForm(string platformName, string platformDisplayName, string sdkVersion, int myversionid, int systemID, string platformIcon, string createUser)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("dwPlatformName", platformName));
            prams.Add(Database.MakeInParam("dwPlatformDisplayName", platformDisplayName));
            prams.Add(Database.MakeInParam("dwSdkVersion", sdkVersion));
            prams.Add(Database.MakeInParam("dwMyVersionID", myversionid));
            prams.Add(Database.MakeInParam("dwSystemID", systemID));
            prams.Add(Database.MakeInParam("dwPlatformIcon", platformIcon));
            prams.Add(Database.MakeInParam("CREATEUSER", createUser));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            return MessageHelper.GetMessage(Database, "sdk_AddPlatfrom", prams);
        }

        #endregion

        #region 图标管理
        /// <summary>
        /// 获取游戏渠道当前图标
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformid"></param>
        /// <returns></returns>
        public DataSet GetGamePlatformIcon(int gameid, int platformid)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("PlatformID", platformid));
            return Database.ExecuteDataset(CommandType.StoredProcedure, "sdk_getGamePlatformIcon", prams.ToArray());
        }

        /// <summary>
        /// 设置游戏渠道图标
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformid"></param>
        /// <param name="iconname"></param>
        /// <returns></returns>
        public Message SetGamePlatformIcon(int gameid, int platformid, string iconname)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("GameID", gameid));
            prams.Add(Database.MakeInParam("PlatformID", platformid));
            prams.Add(Database.MakeInParam("IconName", iconname));
            return MessageHelper.GetMessage(Database, "sdk_setPlatformGameIcon", prams);
        }

        #endregion

        #region 权限管理

        /// <summary>
        /// 获取角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        public DataSet GetRolePower(string userid)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("UserID", userid));
            return Database.ExecuteDataset(CommandType.StoredProcedure, "sdk_getUserGamePower", prams.ToArray());
        }

        /// <summary>
        /// 修改角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <param name="gameidlist"></param>
        /// <returns></returns>
        public Message UpdateRolePower(string userid, string gameidlist)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("UserID", userid));
            prams.Add(Database.MakeInParam("GameIDList", gameidlist));
            return MessageHelper.GetMessage(Database, "sdk_setUserGamePower", prams);
        }

        /// <summary>
        /// 获取角色的游戏
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        public DataSet GetRoleGame(string userid)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("UserID", userid));
            return Database.ExecuteDataset(CommandType.StoredProcedure, "sdk_getUserGame", prams.ToArray());
        }


        #endregion

        #region 插件管理
        /// <summary>
        /// 添加插件版本
        /// </summary>
        /// <param name="pluginID"></param>
        /// <param name="pluginVersion"></param>
        public void AddPlugInVersion(string pluginID, string pluginVersion)
        {
            List<DbParameter> prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("PlugInID", pluginID));
            prams.Add(Database.MakeInParam("PlugInVersion", pluginVersion));
            Database.ExecuteNonQuery(CommandType.StoredProcedure, "sdk_setPlugInVersion", prams.ToArray());
        }

        #endregion

        #region server_platform

        /// <summary>
        /// 获取渠道Attrs详情(key,value)
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        public DataSet GetPlatform_Attrs(int id)
        {
            string strSql = string.Format("select attrs_key,attrs_val from server_platform_attrs where platformid={0} and gameid=0", id);
            DataSet ds = Database.ExecuteDataset(strSql);
            return ds;
        }

        /// <summary>
        /// 初始化游戏server_platform表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        /// <returns></returns>
        public Message InitGameServerPlatform(int gameid, int server_platformid)
        {
            var prams = new List<DbParameter>();
            prams.Add(Database.MakeInParam("gameid", gameid));
            prams.Add(Database.MakeInParam("platformid", server_platformid));
            prams.Add(Database.MakeOutParam("strErrorDescribe", typeof(string), 127));
            Message umsg = MessageHelper.GetMessage(Database, "server_init_platform_game", prams);
            if (umsg.Success)
            {
                try
                {
                    InitGameServerPlatformAttrs(gameid, server_platformid);
                }
                catch (Exception ex) {
                    umsg.Success = false;
                    umsg.Content = ex.Message.ToString();
                }
            }
            return umsg;

        }

        /// <summary>
        /// 初始化游戏server_platform_attrs表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void InitGameServerPlatformAttrs(int gameid, int server_platformid)
        {
            string strSql = string.Format("select platformid,gameid={1},attrs_key from server_platform_attrs where platformid={0} and gameid={1}", server_platformid, gameid);
            DataSet ds = Database.ExecuteDataset(strSql);
            if (ds.Tables[0].Rows.Count > 0)
            {
                foreach (DataRow dr in ds.Tables[0].Rows)
                {
                    string attrs_key = dr["attrs_key"].ToString();
                    string sql = string.Format("insert into server_platform_attrs (platformid,gameid,attrs_key) values ({0},{1},'{2}')", server_platformid, gameid, attrs_key);
                    Database.ExecuteNonQuery(sql);
                }
            }
        }
        /// <summary>
        /// 更新server_platform删除游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        public void UpdateServerPlatformidDeleteGameAttrs(int server_platformid,string attrs_key)
        {
            string strSql = string.Format("select gameid from server_platform_attrs where platformid={0} and attrs_key='{1}'",server_platformid,attrs_key);
            DataSet ds = Database.ExecuteDataset(strSql);
            if (ds.Tables[0].Rows.Count > 0) {
                string gameidlist = "";
                foreach (DataRow dr in ds.Tables[0].Rows) {
                    string gameid = dr["gameid"].ToString();
                    gameidlist += gameid + ",";
                }
                if (gameidlist.Length > 0) gameidlist = gameidlist.Substring(0, gameidlist.Length - 1);
                string sql = string.Format("delete from server_platform_attrs where platformid={0} and attrs_key={1} and gameid in ({2})", server_platformid, attrs_key,gameidlist);
            }
        }

        /// <summary>
        /// 更新server_platform添加游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        public void UpdateServerPlatformidAddGameAttrs(int server_platformid, string attrs_key)
        {
            string strSql = string.Format("select gameid from server_platform_attrs where platformid={0} and attrs_key='{1}'", server_platformid, attrs_key);
            DataSet ds = Database.ExecuteDataset(strSql);
            if (ds.Tables[0].Rows.Count > 0)
            {
                foreach (DataRow dr in ds.Tables[0].Rows)
                {
                    int gameid = Convert.ToInt32( dr["gameid"].ToString());
                    InsertGameServerPlatformAttrs(gameid,server_platformid,attrs_key);
                }
            }

        }

        /// <summary>
        /// 删除游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void DeleteGameServerPlatformAttrs(int gameid, int server_platformid)
        {
            string strSql = string.Format("delete from server_platform_attrs where platformid={0} and gameid={1}", server_platformid, gameid);
        }

        /// <summary>
        /// 插入游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void InsertGameServerPlatformAttrs(int gameid, int server_platformid,string attrs_key)
        {
            string strSql = string.Format("insert into server_platform_attrs (platformid,gameid,attrs_key) values ({0},{1},'{2}')", server_platformid, gameid, attrs_key);
            Database.ExecuteNonQuery(strSql);
        }

        #endregion

        #region 公共

        /// <summary>
        /// 根据SQL语句查询一个值
        /// </summary>
        /// <param name="sqlQuery"></param>
        /// <returns></returns>
        public object GetObjectBySql(string sqlQuery)
        {
            return Database.ExecuteScalar(System.Data.CommandType.Text, sqlQuery);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        public void ExecuteStoredProcedure(string StoredProcedureName)
        {
            Database.ExecuteNonQuery(CommandType.StoredProcedure, StoredProcedureName);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        public void ExecuteStoredProcedure(string StoredProcedureName, Dictionary<string, string> dic)
        {
            var prams = new List<DbParameter>();
            foreach (KeyValuePair<string, string> Pair in dic)
            {
                prams.Add(Database.MakeInParam(Pair.Key, Pair.Value));
            }
            Database.ExecuteNonQuery(CommandType.StoredProcedure, StoredProcedureName, prams.ToArray());
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        public DataSet ExecuteStoredProcedureByDataSet(string StoredProcedureName, Dictionary<string, string> dic)
        {
            var prams = new List<DbParameter>();
            foreach (KeyValuePair<string, string> Pair in dic)
            {
                prams.Add(Database.MakeInParam(Pair.Key, Pair.Value));
            }
            return Database.ExecuteDataset(CommandType.StoredProcedure, StoredProcedureName, prams.ToArray());
        }

        public int ExecuteSql(string sql)
        {
            int row = Database.ExecuteNonQuery(sql);
            return row;
        }

        public System.Data.DataSet GetDataSetBySql(string sql)
        {
            DataSet ds = Database.ExecuteDataset(CommandType.Text, sql);
            return ds;
        }

        public string GetScalarBySql(string sql)
        {
            return Database.ExecuteScalarToStr(CommandType.Text, sql);
        }


        #endregion
    }
}
