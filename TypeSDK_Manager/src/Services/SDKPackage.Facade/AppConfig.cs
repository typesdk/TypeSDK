using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

//using EV.Entity.Platform;
using SDKPackage.Utils;

namespace SDKPackage.Facade
{
    public class AppConfig
    {
        //private PlatformFacade platformFacade = new PlatformFacade();

        public static readonly string DB_GAMESCORE = "QPGameScoreDB";

        public static readonly string DB_TREASURE = "QPTreasureDB";

        public static readonly string LINK_FORMAT = "Data Source={0}; Initial Catalog={1}; User ID={2}; Password={3}; Pooling=true";

        // 构建数据库连接串
        private string BuildDBLink(string server, int port, string uid, string pwd, string dbname)
        {
            string serverAddress = server;

            if (port > 0)
                serverAddress = string.Concat(server, ",", port);

            return string.Format(LINK_FORMAT, serverAddress, dbname, uid, pwd);
        }

        ///// <summary>
        ///// 根据游戏ID获取数据库连接字符串
        ///// </summary>
        ///// <param name="kindID"></param>
        ///// <returns></returns>
        //public string GetDBlink(int kindID)
        //{
        //    GameGameItem game = platformFacade.GetDBAddString( kindID );
        //    if (game != null)
        //    {
        //        DataBaseInfo dbInfo = platformFacade.GetDatabaseInfo(game.DataBaseAddr);
        //        if( dbInfo != null )
        //        {
        //            return BuildDBLink( dbInfo.DBAddr , dbInfo.DBPort , Utils.CWHEncryptNet.XorCrevasse( dbInfo.DBUser ) , Utils.CWHEncryptNet.XorCrevasse( dbInfo.DBPassword ) , game.DataBaseName );
        //        }
        //    }
        //    return "";
        //}

        /// <summary>
        /// 是否有移动版
        /// </summary>
        public static byte IsHaveMobileVersion
        {
            get
            {
                try
                {
                    string key = ApplicationSettings.Get("IsHaveMobileVersion");
                    if (!string.IsNullOrEmpty(key))
                    {
                        return Convert.ToByte(key);
                    }
                    return 0;
                }
                catch
                {
                    return 0;
                }
            }
        }
    }
}
