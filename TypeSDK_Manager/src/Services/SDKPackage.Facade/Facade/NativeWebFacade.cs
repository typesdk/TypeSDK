using SDKPackage.Data.Factory;
using SDKPackage.IData;
using SDKPackage.Kernel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Facade
{
    /// <summary>
    /// 后台外观
    /// </summary>
    public class NativeWebFacade //: BaseFacadeProvider
    {
        #region Fields

        private INativeWebDataProvider aideNativeWebData;

        #endregion

        #region 构造函数

        /// <summary>
        /// 构造函数
        /// </summary>
        public NativeWebFacade()
        {
            aideNativeWebData = ClassFactory.GetINativeWebDataProvider();
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
        /// /// <param name="sdkgameid"></param>
        /// <param name="sdkgamekey"></param>
        /// <returns></returns>
        public Message AddGame(string gamename, string gamedisplayname, string androidVersionID, string iosVersionID, string AndroidKeyID, string iosKeyID, string gameicon, string createuser, string gameNameSpell, string UnityVer, string gameProductName, string gameIsEncryption, string sdkgameid, string sdkgamekey)
        {
            return aideNativeWebData.AddGame(gamename, gamedisplayname, androidVersionID, iosVersionID, AndroidKeyID, iosKeyID, gameicon, createuser, gameNameSpell, UnityVer, gameProductName, gameIsEncryption,sdkgameid,sdkgamekey);
        }

        /// <summary>
        /// 获取角色权限下的游戏详细列表
        /// </summary>
        /// <param name="username"></param>
        /// <returns></returns>
        public DataSet GetGameInfoList(string username)
        {
            return aideNativeWebData.GetGameInfoList(username);
        }

        /// <summary>
        /// 删除游戏
        /// </summary>
        /// <param name="gameid"></param>
        /// <returns></returns>
        public Message DeleteGame(string gameid)
        {
            return aideNativeWebData.DeleteGame(gameid);
        }


        /// <summary>
        /// 更新游戏关联渠道
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformlist"></param>
        /// <param name="singkeyidlist"></param>
        /// <returns></returns>
        public Message UpdateGamePlatform(int gameid, string platformlist, string versionList, string singkeyidlist, int systemid, string pluginid, string pluginversion)
        {
            return aideNativeWebData.UpdateGamePlatform(gameid, platformlist, versionList, singkeyidlist, systemid, pluginid, pluginversion);
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
            return aideNativeWebData.UpdateGamePlatform(gameid, platformlist, versionList, systemid);
        }

        #endregion

        #region 打包任务处理

        public Message GetGainTask(string platform)
        {
            return aideNativeWebData.GetGainTask(platform);
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
            return aideNativeWebData.AddNewPackageTask(taskid, createuser, placeidlist, createtaskid, systemname, gameid, gameversion, gamelable, platformversionlist, gameIsEncryption);
        }

        /// <summary>
        /// 删除任务
        /// </summary>
        /// <param name="recid"></param>
        /// <param name="systemname"></param>
        public void DeleteNewPackageTask(int recid, string systemname)
        {
            aideNativeWebData.DeleteNewPackageTask(recid, systemname);
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
        public void CreateAdPackageTask(string recid, string gameid, string adid, string adname, string createtaskid,string username)
        {
            aideNativeWebData.CreateAdPackageTask(recid, gameid, adid, adname, createtaskid, username);

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
            return aideNativeWebData.GetGamePlatformIcon(gameid, platformid);
        }

        /// <summary>
        /// 设置游戏渠道图标
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformid"></param>
        /// <param name="iconid"></param>
        /// <returns></returns>
        public Message SetGamePlatformIcon(int gameid, int platformid, string iconname)
        {
            return aideNativeWebData.SetGamePlatformIcon(gameid, platformid, iconname);
        }

        #endregion

        #region 版本管理

        /// <summary>
        /// 新增版本
        /// </summary>
        /// <param name="version"></param>
        /// <param name="accoutns"></param>
        /// <returns></returns>
        public Message AddMyVersion(string version, string accoutns, int platformid, int myversionid)
        {
            return aideNativeWebData.AddMyVersion(version, accoutns, platformid, myversionid);
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
            return aideNativeWebData.AddMyVersion(version, username, platformid, systemid);
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
            return aideNativeWebData.AddPlatForm(platformName, platformDisplayName, sdkVersion, myversionid, systemID, platformIcon, createUser);
        }

        #endregion

        #region 权限管理

        /// <summary>
        /// 获取角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        public DataSet GetRolePorwe(string userid)
        {
            return aideNativeWebData.GetRolePower(userid);
        }

        /// <summary>
        /// 修改角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <param name="gameidlist"></param>
        /// <returns></returns>
        public Message UpdateRolePower(string userid, string gameidlist)
        {
            return aideNativeWebData.UpdateRolePower(userid, gameidlist);
        }


        /// <summary>
        /// 获取角色的游戏
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        public DataSet GetRoleGame(string userid)
        {
            return aideNativeWebData.GetRoleGame(userid);
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
            aideNativeWebData.AddPlugInVersion(pluginID, pluginVersion);
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
            return aideNativeWebData.GetPlatform_Attrs(id);
        }


        /// <summary>
        /// 初始化游戏server_platform表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        /// <returns></returns>
        public Message InitGameServerPlatform(int gameid, int server_platformid)
        {
            return aideNativeWebData.InitGameServerPlatform(gameid,server_platformid);
        }

        /// <summary>
        /// 初始化游戏server_platform_attrs表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void InitGameServerPlatformAttrs(int gameid, int server_platformid)
        {
            aideNativeWebData.InitGameServerPlatformAttrs(gameid,server_platformid);
        }
        /// <summary>
        /// 更新server_platform删除游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        public void UpdateServerPlatformidDeleteGameAttrs(int server_platformid, string attrs_key)
        {
            aideNativeWebData.UpdateServerPlatformidDeleteGameAttrs(server_platformid,attrs_key);
        }

        /// <summary>
        /// 更新server_platform添加游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        public void UpdateServerPlatformidAddGameAttrs(int server_platformid, string attrs_key)
        {
            aideNativeWebData.UpdateServerPlatformidAddGameAttrs(server_platformid,attrs_key);
        }

        /// <summary>
        /// 删除游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void DeleteGameServerPlatformAttrs(int gameid, int server_platformid)
        {
            aideNativeWebData.DeleteGameServerPlatformAttrs(gameid,server_platformid);
        }

        /// <summary>
        /// 插入游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        public void InsertGameServerPlatformAttrs(int gameid, int server_platformid, string attrs_key)
        {
            aideNativeWebData.InsertGameServerPlatformAttrs(gameid,server_platformid,attrs_key);
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
            return aideNativeWebData.GetObjectBySql(sqlQuery);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        public void ExecuteStoredProcedure(string StoredProcedureName)
        {
            aideNativeWebData.ExecuteStoredProcedure(StoredProcedureName);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        public void ExecuteStoredProcedure(string StoredProcedureName, Dictionary<string, string> dic)
        {
            aideNativeWebData.ExecuteStoredProcedure(StoredProcedureName, dic);
        }

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        public DataSet ExecuteStoredProcedureByDataSet(string StoredProcedureName, Dictionary<string, string> dic)
        {
            return aideNativeWebData.ExecuteStoredProcedureByDataSet(StoredProcedureName, dic);
        }


        /// <summary>
        /// 执行SQL语句返回受影响的行数
        /// </summary>
        /// <param name="sql"></param>
        public int ExecuteSql(string sql)
        {
            return aideNativeWebData.ExecuteSql(sql);
        }

        /// <summary>
        ///  执行sql返回DataSet
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        public DataSet GetDataSetBySql(string sql)
        {
            return aideNativeWebData.GetDataSetBySql(sql);
        }

        /// <summary>
        /// 执行SQL语句返回一个值
        /// </summary>
        /// <param name="sql"></param>
        /// <returns></returns>
        public string GetScalarBySql(string sql)
        {
            return aideNativeWebData.GetScalarBySql(sql);
        }
        #endregion
    }
}
