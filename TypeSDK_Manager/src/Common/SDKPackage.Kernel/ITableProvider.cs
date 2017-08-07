namespace SDKPackage.Kernel
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    /// <summary>
    /// 单张数据表访问器接口
    /// </summary>
    public interface ITableProvider
    {
        /// <summary>
        /// 批量插入
        /// </summary>
        /// <param name="dataSet"></param>
        /// <param name="columnMapArray"></param>
        void BatchCommitData(DataSet dataSet, string[][] columnMapArray);
        /// <summary>
        /// 批量插入
        /// </summary>
        /// <param name="table"></param>
        /// <param name="columnMapArray"></param>
        void BatchCommitData(DataTable table, string[][] columnMapArray);
        /// <summary>
        /// 批量更新,提交DataTable中的changes到数据库
        /// </summary>
        /// <param name="dt"></param>
        void CommitData(DataTable dt);
        /// <summary>
        ///  删除目标表中所有满足where条件的记录
        /// </summary>
        /// <param name="where"></param>
        void Delete(string where);
        /// <summary>
        /// 获取满足条件的DataSet
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        DataSet Get(string where);
        /// <summary>
        ///  获取一个空的DataTable，该DataTable反映了目标表的结构
        /// </summary>
        /// <returns></returns>
        DataTable GetEmptyTable();
        /// <summary>
        /// 获取满足条件的对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="where"></param>
        /// <returns></returns>
        T GetObject<T>(string where);
        /// <summary>
        /// 获取满足条件的列表对象,仅一张表
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="where"></param>
        /// <returns></returns>
        IList<T> GetObjectList<T>(string where);
        /// <summary>
        /// 获取满足where条件的第一条记录
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        DataRow GetOne(string where);
        /// <summary>
        /// 获取目标表中满足where条件的记录总数
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        int GetRecordsCount(string where);
        /// <summary>
        /// 将row存放到数据库中
        /// </summary>
        /// <param name="row"></param>
        void Insert(DataRow row);
        /// <summary>
        /// 返回一个与目标表大纲完全一致的DataRow
        /// </summary>
        /// <returns></returns>
        DataRow NewRow();
        /// <summary>
        /// 目标表名称
        /// </summary>
        string TableName { get; }
    }
}

