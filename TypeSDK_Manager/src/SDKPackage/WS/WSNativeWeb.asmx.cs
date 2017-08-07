using SDKPackage.Facade;
using SDKPackage.Kernel;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Services;

using Newtonsoft.Json;


namespace SDKPackage.WS
{
    /// <summary>
    /// WSNativeWeb 的摘要说明
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // 若要允许使用 ASP.NET AJAX 从脚本中调用此 Web 服务，请取消注释以下行。 
    [System.Web.Script.Services.ScriptService]
    public class WSNativeWeb : System.Web.Services.WebService
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();

        #region 图标工具
        /// <summary>
        /// 更新渠道图标
        /// </summary>
        /// <param name="platformID"></param>
        /// <param name="platformIconName"></param>
        /// <returns></returns>
        [WebMethod]
        public string UpdatePlatformIconName(int platformID, string platformIconName)
        {
            //platformIconName = TextFilter.FilterScript(platformIconName);
            string sql = string.Format("update sdk_DefaultPlatform set PlatformIcon='{0}' where id={1}", platformIconName, platformID);
            aideNativeWebFacade.ExecuteSql(sql);
            return "{success:'success'}";
        }
        #endregion

        #region 用户工具

        /// <summary>
        /// 获取角色游戏权限
        /// </summary>
        /// <param name="userid"></param>
        /// <returns></returns>
        [WebMethod]
        public string GetRoleGamePower(string userid)
        {
            DataSet ds = aideNativeWebFacade.GetRolePorwe(userid);
            int count = ds.Tables[0].Rows.Count;

            string strJson = "{data:[";
            if (count > 0)
            {
                for (int i = 0; i < count; i++)
                {
                    strJson += "{gameid:'" + ds.Tables[0].Rows[i]["GameID"].ToString() + "',gamename:'" + ds.Tables[0].Rows[i]["GameDisplayName"].ToString() + "',rolepower:'" + ds.Tables[0].Rows[i]["rolepower"].ToString() + "'},";
                }
                strJson = strJson.Substring(0, strJson.Length - 1);
                strJson += "]}";
            }
            else
            {
                return "{data:null}";
            }
            return strJson;
        }

        /// <summary>
        /// 更新角色权限
        /// </summary>
        /// <param name="userid"></param>
        /// <param name="gameidlist"></param>
        /// <returns></returns>
        [WebMethod]
        public string UpdateRoleGamePower(string userid, string gameidlist)
        {
            Message umsg = aideNativeWebFacade.UpdateRolePower(userid, gameidlist);
            if (umsg.Success)
            {
                return "{success:'success'}";
            }
            return "{success:'error'}";
        }

        #endregion

        #region 打包工具

        /// <summary>
        /// 重新发布打包任务
        /// </summary>
        [WebMethod]
        public void PackageAgain(int id, string systemname)
        {
            string sql = string.Format(@"update {0} set PackageTaskStatus=1 where recid={1} and PackageTaskStatus=4", systemname == "Android" ? "sdk_NewPackageCreateTask" : "sdk_NewPackageCreateTask_IOS", id);
            aideNativeWebFacade.ExecuteSql(sql);
        }

        /// <summary>
        /// 删除游戏包
        /// </summary>
        /// <param name="id"></param>
        /// <param name="platform"></param>
        /// <param name="gameName"></param>
        /// <param name="filepath"></param>
        [WebMethod]
        public void DeleteGamePackage(int id, string platform, string gameId, string filepath)
        {
            string SDKPackageDir = "";
            if (platform == "Android")
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageGameFile"] + gameId + "\\" + filepath;
            }
            else
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageGameFile"] + gameId + "\\" + filepath;
            }
            if (System.IO.Directory.Exists(SDKPackageDir))
            {
                System.IO.Directory.Delete(SDKPackageDir, true);
            }
            string sql = string.Format(@"delete from sdk_UploadPackageInfo where id={0}", id);
            aideNativeWebFacade.ExecuteSql(sql);
        }

        [WebMethod]
        public void DeletePlatformPackage(int id, string platform, string filepath)
        {
            string SDKPackageDir = "";
            string[] arrfile = filepath.Split('/');
            string createtaskid = arrfile[1];
            if (platform == "Android")
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageApk"] + filepath;
            }
            else
            {
                SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIPA"] + filepath;
            }
            //if (System.IO.File.Exists(SDKPackageDir))
            //{
            //    System.IO.File.Delete(SDKPackageDir);
            //}
            DeleteFile(id, platform == "Android" ? 1 : 2, SDKPackageDir, createtaskid);
            string sql = string.Format(@"delete from {0} where recid={1}", platform == "Android" ? "sdk_NewPackageCreateTask" : "sdk_NewPackageCreateTask_IOS", id);
            aideNativeWebFacade.ExecuteSql(sql);
        }

        private void DeleteFile(int id, int systemid, string filepath, string createtaskid)
        {
            string dir_file = filepath.Substring(0, filepath.LastIndexOf('/'));
            if (systemid == 1)//Android
            {
                if (System.IO.Directory.Exists(dir_file))//文件
                {
                    string[] filelist = System.IO.Directory.GetFiles(dir_file);
                    if (filelist.Length <= 1)
                    {
                        System.IO.Directory.Delete(dir_file, true);
                    }
                    else
                    {
                        if (System.IO.File.Exists(filepath))
                            System.IO.File.Delete(filepath);
                    }
                }
                string logpath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"] + createtaskid + "/" + id + ".logs";
                dir_file = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"] + createtaskid + "/";
                if (System.IO.Directory.Exists(dir_file))//文件
                {
                    string[] filelist = System.IO.Directory.GetFiles(dir_file);
                    if (filelist.Length <= 1)
                    {
                        System.IO.Directory.Delete(dir_file, true);
                    }
                    else
                    {
                        if (System.IO.File.Exists(logpath))
                            System.IO.File.Delete(logpath);
                    }
                }
            }
            else
            {
                if (System.IO.Directory.Exists(dir_file))
                {
                    if (System.IO.File.Exists(dir_file + "/.DS_Store"))
                    {
                        System.IO.File.Delete(dir_file + "/.DS_Store");
                    }
                    string[] filelist = System.IO.Directory.GetFiles(dir_file);
                    if (filelist.Length <= 1)
                    {
                        System.IO.Directory.Delete(dir_file, true);
                    }
                    else
                    {
                        System.IO.File.Delete(filepath);
                    }
                }
                string logpath = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageLogs"] + createtaskid + "/" + id + ".logs";
                dir_file = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageLogs"] + createtaskid + "/";
                if (System.IO.Directory.Exists(dir_file))//文件
                {
                    if (System.IO.File.Exists(dir_file + "/.DS_Store"))
                    {
                        System.IO.File.Delete(dir_file + "/.DS_Store");
                    }
                    string[] filelist = System.IO.Directory.GetFiles(dir_file);
                    if (filelist.Length <= 1)
                    {
                        System.IO.Directory.Delete(dir_file, true);
                    }
                    else
                    {
                        System.IO.File.Delete(filepath);
                    }
                }
            }
        }

#endregion

        #region server_platform
        [WebMethod]
        public string ValidatePlatformID(int id)
        {
            string strSql = string.Format("select platformdisplayname from server_platform where platformid={0}",id);
            var val= aideNativeWebFacade.GetObjectBySql(strSql)??"";
            if (string.IsNullOrEmpty(val.ToString()))
                return "{success:'success',msg:''}";
            return "{success:'error',msg:'抱歉，当前id已存在(" + val + ")'}";
        }


        /// <summary>
        /// 编辑server_platform删除attrs
        /// </summary>
        /// <param name="id"></param>
        /// <param name="attr_key"></param>
        /// <returns></returns>
        [WebMethod]
        public string delServerPlatformAttrs(int id,string attrs_key)
        {
            string msg = "";
            msg = "{success:'success',msg:''}";
            msg = "{success:'error',msg:'测试删除失败'}";
            return msg;
        }


        /// <summary>
        /// 编辑server_platform添加attrs
        /// </summary>
        /// <param name="id"></param>
        /// <param name="attr_key"></param>
        /// <returns></returns>
        [WebMethod]
        public string addServerPlatformAttrs(int id, string attrs_key)
        {
            string msg = "";
            msg = "{success:'success',msg:''}";
            msg = "{success:'error',msg:'测试删除失败'}";
            return msg;
        }

        #endregion

        #region ad工具

        /// <summary>
        /// 重新发布ad打包任务
        /// </summary>
        [WebMethod]
        public void AdPackageAgain(int id, int adid)
        {
            string sql = string.Format(@"update sdk_AdPackageCreateTask set PackageTaskStatus=1,StartDatetime='' where recid={0} and adid={1} and PackageTaskStatus=4", id, adid);
            aideNativeWebFacade.ExecuteSql(sql);
        }


        [WebMethod]
        public void DeleteAdPlatformPackage(int id, int adid, string filepath)
        {
            string SDKPackageDir = "";
            string[] arrfile = filepath.Split('/');
            string createtaskid = arrfile[1];
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageApk"] + filepath;

            //if (System.IO.File.Exists(SDKPackageDir))
            //{
            //    System.IO.File.Delete(SDKPackageDir);
            //}
            DeleteAdFile(id, SDKPackageDir, createtaskid);
            string sql = string.Format(@"delete from sdk_AdPackageCreateTask where recid={0} and adid={1}", id, adid);
            aideNativeWebFacade.ExecuteSql(sql);
        }

        private void DeleteAdFile(int id,  string filepath, string createtaskid)
        {
            string dir_file = filepath.Substring(0, filepath.LastIndexOf('/'));
            if (System.IO.Directory.Exists(dir_file))//文件
            {
                string[] filelist = System.IO.Directory.GetFiles(dir_file);
                if (filelist.Length <= 1)
                {
                    System.IO.Directory.Delete(dir_file, true);
                }
                else
                {
                    if (System.IO.File.Exists(filepath))
                        System.IO.File.Delete(filepath);
                }
            }

        }
        #endregion

        [WebMethod]
        public string ReviewPackage(int id, int status, string platform)
        {
            string sql = string.Format(@"UPDATE [sdk_NewPackageCreateTask] SET [PackageReviewStatus] = {0} WHERE [RecID] = {1}", status, id);
            if (string.Equals(platform, "IOS"))
            {
                sql = string.Format(@"UPDATE [sdk_NewPackageCreateTask_IOS] SET [PackageReviewStatus] = {0} WHERE [RecID] = {1}", status, id);
            }
            int nRet = aideNativeWebFacade.ExecuteSql(sql);

            var objJson = new { ret = 0 };
            if (nRet <= 0) objJson = new { ret = 1 };

            string strJson = "";
            strJson = JsonConvert.SerializeObject(objJson);

            return strJson;
        }
        
        [WebMethod]
        public string ReviewGameProjectVersion(int id, int status)
        {
            string sql = string.Format(@"UPDATE [sdk_UploadPackageInfo] SET [Status] = {0} WHERE [ID] = {1}",status, id);
            int nRet = aideNativeWebFacade.ExecuteSql(sql);

            var objJson = new { ret = 0 };
            if (nRet <= 0) objJson = new { ret = 1 };

            string strJson = "";
            strJson = JsonConvert.SerializeObject(objJson);

            return strJson;
        }

        [WebMethod]
        public string GetPackgeStatus(string id, string systemname)
        {
            if (String.IsNullOrWhiteSpace(id))
            {
                id = "(0)";
            }
            else
            {
                id = "(" + id+ ")";
            }
            string sql = string.Format(@"SELECT [RecID],[PackageTaskStatus] FROM {0} WHERE [RecID] in {1}", String.Equals(systemname, "Android") ? "sdk_NewPackageCreateTask" : "sdk_NewPackageCreateTask_IOS", id);

            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            var oData = ds.Tables[0].AsEnumerable();

            var objJson = new { ret = 1, data = new List<object>() };

            if (oData.Count() != 0)
            {
                var objtasklist = ds.Tables[0].AsEnumerable();
                List<object> tasklist = new List<object>();
                foreach (var item in objtasklist)
                {
                    tasklist.Add(new
                    {
                        RecID = item["RecID"],
                        PackageTaskStatus = item["PackageTaskStatus"]
                    });
                }

                objJson = new { ret = 0, data = tasklist };
            }

            string strJson = "";
            strJson = JsonConvert.SerializeObject(objJson);

            return strJson;
        }
    }
}
