namespace SDKPackage.Kernel
{
    using SDKPackage.Utils;
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Data.Common;
    /// <summary>
    /// 消息辅助类
    /// </summary>
    public class MessageHelper
    {
        private MessageHelper()
        {
        }
        /// <summary>
        /// 获取未携带实体消息的对象
        /// </summary>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static Message GetMessage(List<DbParameter> prams)
        {
            if (prams.Count == 1)
                return new Message(TypeParse.StrToInt(prams[prams.Count - 1].Value, -1),"");
            
            return new Message(TypeParse.StrToInt(prams[prams.Count - 1].Value, -1), prams[prams.Count - 2].Value.ToString());
        }
        /// <summary>
        /// 获取未携带实体消息的对象
        /// </summary>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static Message GetMessage(DbHelper database, string procName, List<DbParameter> prams)
        {
            database.RunProc(procName, prams);
            return GetMessage(prams);
        }
        /// <summary>
        /// 获取消息并携带 DataSet 对象
        /// </summary>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static Message GetMessageForDataSet(DbHelper database, string procName, List<DbParameter> prams)
        {
            DataSet ds = null;
            database.RunProc(procName, prams, out ds);
            Message message = GetMessage(prams);
            if (message.MessageID == 0)
            {
                message.AddEntity(ds);
            }
            return message;
        }
        /// <summary>
        /// 获取消息并携带实体单个对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static Message GetMessageForObject<T>(DbHelper database, string procName, List<DbParameter> prams)
        {
            DataSet ds = null;
            database.RunProc(procName, prams, out ds);
            Message message = GetMessage(prams);
            if (message.MessageID == 0)
            {
                message.AddEntity(DataHelper.ConvertRowToObject<T>(ds.Tables[0].Rows[0]));
            }
            return message;
        }
        /// <summary>
        /// 获取消息并携带实体单个列表对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static Message GetMessageForObjectList<T>(DbHelper database, string procName, List<DbParameter> prams)
        {
            DataSet ds = null;
            database.RunProc(procName, prams, out ds);
            Message message = GetMessage(prams);
            if (message.MessageID == 0)
            {
                message.AddEntity(DataHelper.ConvertDataTableToObjects<T>(ds.Tables[0]));
            }
            return message;
        }
        /// <summary>
        /// 获取对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static T GetObject<T>(DbHelper database, string procName, List<DbParameter> prams)
        {
            return database.RunProcObject<T>(procName, prams);
        }
        /// <summary>
        /// 获取对象列表
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="database"></param>
        /// <param name="procName"></param>
        /// <param name="prams"></param>
        /// <returns></returns>
        public static IList<T> GetObjectList<T>(DbHelper database, string procName, List<DbParameter> prams)
        {
            return database.RunProcObjectList<T>(procName, prams);
        }
    }
}

