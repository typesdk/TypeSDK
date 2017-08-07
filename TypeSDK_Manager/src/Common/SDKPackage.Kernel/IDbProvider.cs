namespace SDKPackage.Kernel
{
    using System;
    using System.Data;
    using System.Data.Common;
    /// <summary>
    /// 数据库访问接口
    /// </summary>
    public interface IDbProvider
    {
        /// <summary>
        /// 转换到数据库类型
        /// </summary>
        /// <param name="t"></param>
        /// <returns></returns>
        object ConvertToLocalDbType( Type t );
        /// <summary>
        /// 转换到Net类型
        /// </summary>
        /// <param name="netType"></param>
        /// <returns></returns>
        string ConvertToLocalDbTypeString( Type netType );
        /// <summary>
        /// 检索SQL参数信息并填充
        /// </summary>
        /// <param name="cmd"></param>
        void DeriveParameters( IDbCommand cmd );
        /// <summary>
        /// 返回刚插入记录的自增ID值, 如不支持则为""
        /// </summary>
        /// <returns></returns>
        string GetLastIdSql( );
        /// <summary>
        /// 返回DbProviderFactory实例
        /// </summary>
        /// <returns></returns>
        DbProviderFactory Instance( );
        /// <summary>
        /// 是否支持备份数据库
        /// </summary>
        /// <returns></returns>
        bool IsBackupDatabase( );
        /// <summary>
        /// 是否支持压缩数据库
        /// </summary>
        /// <returns></returns>
        bool IsCompactDatabase( );
        /// <summary>
        /// 是否支持数据库优化
        /// </summary>
        /// <returns></returns>
        bool IsDbOptimize( );
        /// <summary>
        /// 是否支持全文搜索
        /// </summary>
        /// <returns></returns>
        bool IsFullTextSearchEnabled( );
        /// <summary>
        /// 是否支持数据库收缩
        /// </summary>
        /// <returns></returns>
        bool IsShrinkData( );
        /// <summary>
        /// 是否支持存储过程
        /// </summary>
        /// <returns></returns>
        bool IsStoreProc( );
        /// <summary>
        /// 生成存储过程参数
        /// </summary>
        /// <param name="paraName">参数名称</param>
        /// <param name="paraValue">参数值</param>
        /// <param name="direction">参数方向：: in / out /return</param>
        /// <returns></returns>
        DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction );
        /// <summary>
        /// 生成存储过程参数
        /// </summary>
        /// <param name="paraName">参数名称</param>
        /// <param name="paraValue">参数值</param>
        /// <param name="direction">参数方向：: in / out /return</param>
        /// <param name="paraType">参数类型</param>
        /// <param name="sourceColumn"></param>
        /// <returns></returns>
        DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction , Type paraType , string sourceColumn );
        /// <summary>
        /// 生成存储过程参数
        /// </summary>
        /// <param name="paraName">参数名称</param>
        /// <param name="paraValue">参数值</param>
        /// <param name="direction">参数方向：: in / out /return</param>
        /// <param name="paraType">参数类型</param>
        /// <param name="sourceColumn"></param>
        /// <param name="size"></param>
        /// <returns></returns>
        DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction , Type paraType , string sourceColumn , int size );
        /// <summary>
        /// 参数前缀
        /// </summary>
        string ParameterPrefix { get; }
    }
}

