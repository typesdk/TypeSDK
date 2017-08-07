namespace SDKPackage.Kernel
{
    using System;
    using System.Data;
    using System.Data.Common;
    using System.Data.SqlClient;
    /// <summary>
    /// SQL Server 数据库访问类
    /// </summary>
    public class SqlServerProvider : IDbProvider
    {
        /// <summary>
        /// 转换到数据库类型
        /// </summary>
        /// <param name="t"></param>
        /// <returns></returns>
        public object ConvertToLocalDbType( Type t )
        {
            switch ( t.ToString() )
            {
                case "System.Boolean":
                    return SqlDbType.Bit;

                case "System.DateTime":
                    return SqlDbType.DateTime;

                case "System.Decimal":
                    return SqlDbType.Decimal;

                case "System.Single":
                    return SqlDbType.Float;

                case "System.Double":
                    return SqlDbType.Float;

                case "System.Byte[]":
                    return SqlDbType.Image;

                case "System.Int64":
                    return SqlDbType.BigInt;

                case "System.Int32":
                    return SqlDbType.Int;

                case "System.String":
                    return SqlDbType.NVarChar;

                case "System.Int16":
                    return SqlDbType.SmallInt;

                case "System.Byte":
                    return SqlDbType.TinyInt;

                case "System.Guid":
                    return SqlDbType.UniqueIdentifier;

                case "System.TimeSpan":
                    return SqlDbType.Time;

                case "System.Object":
                    return SqlDbType.Variant;
            }
            return SqlDbType.Int;
        }
        /// <summary>
        /// 转换到Net类型
        /// </summary>
        /// <param name="netType"></param>
        /// <returns></returns>
        public string ConvertToLocalDbTypeString( Type netType )
        {
            switch ( netType.ToString() )
            {
                case "System.Boolean":
                    return "bit";

                case "System.DateTime":
                    return "datetime";

                case "System.Decimal":
                    return "decimal";

                case "System.Single":
                    return "float";

                case "System.Double":
                    return "float";

                case "System.Int64":
                    return "bigint";

                case "System.Int32":
                    return "int";

                case "System.String":
                    return "nvarchar";

                case "System.Int16":
                    return "smallint";

                case "System.Byte":
                    return "tinyint";

                case "System.Guid":
                    return "uniqueidentifier";

                case "System.TimeSpan":
                    return "time";

                case "System.Byte[]":
                    return "image";

                case "System.Object":
                    return "sql_variant";
            }
            return null;
        }
        /// <summary>
        /// 检索SQL参数信息并填充
        /// </summary>
        /// <param name="cmd"></param>
        public void DeriveParameters( IDbCommand cmd )
        {
            if ( cmd is SqlCommand )
            {
                SqlCommandBuilder.DeriveParameters( cmd as SqlCommand );
            }
        }
        /// <summary>
        /// 返回刚插入记录的自增ID值, 如不支持则为""
        /// </summary>
        /// <returns></returns>
        public string GetLastIdSql( )
        {
            return "SELECT SCOPE_IDENTITY()";
        }
        /// <summary>
        /// 返回DbProviderFactory实例
        /// </summary>
        /// <returns></returns>
        public DbProviderFactory Instance( )
        {
            return SqlClientFactory.Instance;
        }
        /// <summary>
        /// 是否支持备份数据库
        /// </summary>
        /// <returns></returns>
        public bool IsBackupDatabase( )
        {
            return true;
        }
        /// <summary>
        /// 是否支持压缩数据库
        /// </summary>
        /// <returns></returns>
        public bool IsCompactDatabase( )
        {
            return true;
        }
        /// <summary>
        /// 是否支持数据库优化
        /// </summary>
        /// <returns></returns>
        public bool IsDbOptimize( )
        {
            return true;
        }
        /// <summary>
        /// 是否支持全文搜索
        /// </summary>
        /// <returns></returns>
        public bool IsFullTextSearchEnabled( )
        {
            return true;
        }
        /// <summary>
        /// 是否支持数据库收缩
        /// </summary>
        /// <returns></returns>
        public bool IsShrinkData( )
        {
            return true;
        }
        /// <summary>
        /// 是否支持存储过程
        /// </summary>
        /// <returns></returns>
        public bool IsStoreProc( )
        {
            return true;
        }
        /// <summary>
        /// 生成存储过程参数
        /// </summary>
        /// <param name="paraName">参数名称</param>
        /// <param name="paraValue">参数值</param>
        /// <param name="direction">参数方向：: in / out /return</param>
        /// <returns></returns>
        public DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction )
        {
            Type paraType = null;
            if ( paraValue != null )
            {
                paraType = paraValue.GetType();
            }
            return this.MakeParam( paraName , paraValue , direction , paraType , null );
        }
        /// <summary>
        /// 生成存储过程参数
        /// </summary>
        /// <param name="paraName">参数名称</param>
        /// <param name="paraValue">参数值</param>
        /// <param name="direction">参数方向：: in / out /return</param>
        /// <param name="paraType">参数类型</param>
        /// <param name="sourceColumn"></param>
        /// <returns></returns>
        public DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction , Type paraType , string sourceColumn )
        {
            return this.MakeParam( paraName , paraValue , direction , paraType , sourceColumn , 0 );
        }
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
        public DbParameter MakeParam( string paraName , object paraValue , ParameterDirection direction , Type paraType , string sourceColumn , int size )
        {
            SqlParameter parameter = new SqlParameter
            {
                ParameterName = this.ParameterPrefix + paraName
            };
            if ( paraType != null )
            {
                parameter.SqlDbType = ( SqlDbType )this.ConvertToLocalDbType( paraType );
            }
            parameter.Value = paraValue;
            if ( parameter.Value == null )
            {
                parameter.Value = DBNull.Value;
            }
            parameter.Direction = direction;
            if ( ( direction != ParameterDirection.Output ) || ( paraValue != null ) )
            {
                parameter.Value = paraValue;
            }
            if ( direction == ParameterDirection.Output )
            {
                parameter.Size = size;
            }
            if ( sourceColumn != null )
            {
                parameter.SourceColumn = sourceColumn;
            }
            return parameter;
        }
        /// <summary>
        /// 参数前缀
        /// </summary>
        public string ParameterPrefix
        {
            get
            {
                return "@";
            }
        }
    }
}

