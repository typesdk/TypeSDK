using SDKPackage.Kernel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.IData
{
    public interface INativeWebDataProvider
    {

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
        Message AddGame(string gamename, string gamedisplayname, string androidVersionID, string iosVersionID, string AndroidKeyID, string iosKeyID, string gameicon, string createuser, string gameNameSpell, string UnityVer, string gameProductName, string gameIsEncryption, string sdkgameid, string sdkgamekey);

        /// <summary>
        /// 获取角色权限下的游戏详细列表
        /// </summary>
        /// <param name="username"></param>
        /// <returns></returns>
        DataSet GetGameInfoList(string username);

        /// <summary>
        /// 删除游戏
        /// </summary>
        /// <param name="gameid"></param>
        /// <returns></returns>
        Message DeleteGame(string gameid);
        /// <summary>
        /// 更新游戏关联渠道
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformlist"></param>
        /// <param name="singkeyidlist"></param>
        /// <returns></returns>
        Message UpdateGamePlatform(int gameid, string platformlist, string versionList, string singkeyidlist, int systemid, string pluginid, string pluginversion);

        /// <summary>
        /// 更新游戏关联渠道IOS
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformlist"></param>
        /// <param name="systemid"></param>
        /// <returns></returns>
        Message UpdateGamePlatform(int gameid, string platformlist, string versionList, int systemid);
        #endregion

        #region 打包任务处理

        Message GetGainTask(string platform);

        /// <summary>
        /// 新建打包任务
        /// </summary>
        /// <param name="taskid"></param>
        /// <param name="createuser"></param>
        /// <param name="placeidlist"></param>
        /// <param name="createtaskid"></param>
        /// <returns></returns>
        Message AddNewPackageTask(int taskid, string createuser, string placeidlist, string createtaskid, string systemname, int gameid, string gameversion, string gamelable, string platformversionlist, string gameIsEncryption);

        /// <summary>
        /// 删除任务
        /// </summary>
        /// <param name="recid"></param>
        /// <param name="systemname"></param>
        void DeleteNewPackageTask(int recid, string systemname);

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
        void CreateAdPackageTask(string recid, string gameid, string adid, string adname, string createtaskid, string username);


        #endregion

        #region 图标管理

        /// <summary>
        /// 获取游戏渠道当前图标
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformid"></param>
        /// <returns></returns>
        DataSet GetGamePlatformIcon(int gameid, int platformid);

        /// <summary>
        /// 设置游戏渠道图标
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="platformid"></param>
        /// <param name="iconid"></param>
        /// <returns></returns>
        Message SetGamePlatformIcon(int gameid, int platformid, string iconname);

        #endregion

        #region 版本管理

        /// <summary>
        /// 新增版本
        /// </summary>
        /// <param name="version"></param>
        /// <param name="accoutns"></param>
        /// <returns></returns>
        Message AddMyVersion(string version, string accoutns, int platformid, int myversionid);


        /// <summary>
        /// 添加新渠道版本
        /// </summary>
        /// <param name="version"></param>
        /// <param name="username"></param>
        /// <param name="platformid"></param>
        /// <param name="systemid"></param>
        /// <returns></returns>
        Message AddMyVersion(string version, string username, string platformid, string systemid);

        /// <summary>
        /// 新增渠道
        /// </summary>
        /// <param name="platformName"></param>
        /// <param name="platformDisplayName"></param>
        /// <param name="sdkVersion"></param>
        /// <param name="myversionid"></param>
        /// <param name="systemID"></param>
        /// <returns></returns>
        Message AddPlatForm(string platformName, string platformDisplayName, string sdkVersion, int myversionid, int systemID, string platformIcon, string createUser);

        #endregion

        #region 权限管理

        /// <summary>
        /// 获取角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        DataSet GetRolePower(string userid);

        /// <summary>
        /// 修改角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <param name="gameidlist"></param>
        /// <returns></returns>
        Message UpdateRolePower(string userid, string gameidlist);


        /// <summary>
        /// 获取角色的游戏
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        DataSet GetRoleGame(string userid);

        #endregion

        #region 插件管理
        /// <summary>
        /// 添加插件版本
        /// </summary>
        /// <param name="pluginID"></param>
        /// <param name="pluginVersion"></param>
        void AddPlugInVersion(string pluginID, string pluginVersion);

        #endregion

        #region server_platform


        /// <summary>
        /// 获取渠道Attrs详情(key,value)
        /// </summary>
        /// <param name="id"></param>
        /// <returns></returns>
        DataSet GetPlatform_Attrs(int id);

             /// <summary>
        /// 初始化游戏server_platform表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        /// <returns></returns>
        Message InitGameServerPlatform(int gameid, int server_platformid);

        /// <summary>
        /// 初始化游戏server_platform_attrs表单
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        void InitGameServerPlatformAttrs(int gameid, int server_platformid);

        /// <summary>
        /// 更新server_platform删除游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        void UpdateServerPlatformidDeleteGameAttrs(int server_platformid, string attrs_key);

        /// <summary>
        /// 更新server_platform添加游戏attrs
        /// </summary>
        /// <param name="server_platformid"></param>
        void UpdateServerPlatformidAddGameAttrs(int server_platformid, string attrs_key);

        /// <summary>
        /// 删除游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        void DeleteGameServerPlatformAttrs(int gameid, int server_platformid);

        /// <summary>
        /// 插入游戏server_platform_attrs
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="server_platformid"></param>
        void InsertGameServerPlatformAttrs(int gameid, int server_platformid, string attrs_key);

        #endregion

        #region 公共

        /// <summary>
        /// 根据SQL语句查询一个值
        /// </summary>
        /// <param name="sqlQuery"></param>
        /// <returns></returns>
        object GetObjectBySql(string sqlQuery);

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        void ExecuteStoredProcedure(string StoredProcedureName);

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        void ExecuteStoredProcedure(string StoredProcedureName, Dictionary<string, string> dic);

        /// <summary>
        /// 执行存储过程
        /// </summary>
        /// <param name="StoredProcedureName"></param>
        /// <param name="dic">字典参数</param>
        DataSet ExecuteStoredProcedureByDataSet(string StoredProcedureName, Dictionary<string, string> dic);


        /// <summary>
        /// 执行SQL语句返回受影响的行数
        /// </summary>
        /// <param name="sql"></param>
        int ExecuteSql(string sql);


        /// <summary>
        ///  执行sql返回DataSet
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        DataSet GetDataSetBySql(string sql);


        /// <summary>
        /// 执行SQL语句返回一个值
        /// </summary>
        /// <param name="sql"></param>
        /// <returns></returns>
        string GetScalarBySql(string sql);
        #endregion
    }
}
